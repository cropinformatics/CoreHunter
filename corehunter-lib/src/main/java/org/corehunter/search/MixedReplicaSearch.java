//  Copyright 2012 Guy Davenport, Herman De Beukelaer
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.corehunter.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.corehunter.Accession;
import org.corehunter.AccessionCollection;
import org.corehunter.CoreHunterException;
import org.corehunter.SearchException;
import org.corehunter.measures.PseudoMeasure;

public class MixedReplicaSearch extends AbstractSubsetSearch
{
	private AccessionCollection ac ; 
	private PseudoMeasure pm ; 
	private int sampleMin ; 
	private int sampleMax ; 
	private double runtime ; 
	private double minProg ; 
	private double stuckTime ; 
	private int nrOfTabuReplicas ; 
	private int nrOfNonTabuReplicas ; 
	private int roundsWithoutTabu ; 
	private int nrOfTabuSteps ; 
	private int tournamentSize ; 
	private int tabuListSize ; 
	private int boostNr ; 
	private double boostMinProg ; 
	private int boostTimeFactor ; 
	private double minBoostTime ; 
	private double minMCTemp ; 
	private double maxMCTemp ;

	public MixedReplicaSearch(AccessionCollection ac, PseudoMeasure pm,
			int sampleMin, int sampleMax, double runtime, double minProg,
			double stuckTime, int nrOfTabuReplicas, int nrOfNonTabuReplicas,
			int roundsWithoutTabu, int nrOfTabuSteps, int tournamentSize,
			int tabuListSize, int boostNr, double boostMinProg,
			int boostTimeFactor, double minBoostTime, double minMCTemp,
			double maxMCTemp)
	{
		super();
		this.ac = ac;
		this.pm = pm;
		this.sampleMin = sampleMin;
		this.sampleMax = sampleMax;
		this.runtime = runtime;
		this.minProg = minProg;
		this.stuckTime = stuckTime;
		this.nrOfTabuReplicas = nrOfTabuReplicas;
		this.nrOfNonTabuReplicas = nrOfNonTabuReplicas;
		this.roundsWithoutTabu = roundsWithoutTabu;
		this.nrOfTabuSteps = nrOfTabuSteps;
		this.tournamentSize = tournamentSize;
		this.tabuListSize = tabuListSize;
		this.boostNr = boostNr;
		this.boostMinProg = boostMinProg;
		this.boostTimeFactor = boostTimeFactor;
		this.minBoostTime = minBoostTime;
		this.minMCTemp = minMCTemp;
		this.maxMCTemp = maxMCTemp;
	}

	@Override
	protected void runSearch() throws CoreHunterException
	{
		double boostTime = 0;
		boolean boostTimeLocked = false;

		final int PROG_BOOST_FACTOR = 2;

		final int LR_L = 2;
		final int LR_R = 1;
		final boolean LR_EXH_START = false;
		// no limit on nr of steps for LR, just keeps running in background
		// until done
		final int NR_OF_LR_STEPS = -1;

		// LS can perform more steps than tabu because each step is very fast,
		// only sampling one neighbor instead of Tabu which samples about
		// ac.size()
		// neighbors in each step to select the (heursistic) best neighbor
		final int NR_OF_LS_STEPS = ac.size();

		double bestScore = -Double.MAX_VALUE;
		List<Accession> bestCore = new ArrayList<Accession>();

		Random rg = new Random();

		Neighborhood randNh = new RandomSingleNeighborhood(sampleMin, sampleMax);
		Neighborhood heurNh = new HeuristicSingleNeighborhood(sampleMin,
				sampleMax);

		// create, init and store initial replicas (local search)
		List<Replica> replicas = new ArrayList<Replica>(nrOfNonTabuReplicas);
		// add Local Search Replicas
		for (int i = 0; i < nrOfNonTabuReplicas; i++)
		{
			Replica rep;

			// initially, create some extra LS Replica
			rep = new LocalSearchReplica(ac, pm, randNh.clone(),
					NR_OF_LS_STEPS, -1, sampleMin, sampleMax);

			// Init replica
			rep.init();
			replicas.add(rep);
		}

		int nrOfTabus = 0;
		int nrOfNonTabus = nrOfNonTabuReplicas;
		int nrStuck;

		// create and init one LR Semi replica
		LRReplica lrrep = new LRReplica(ac, pm, NR_OF_LR_STEPS, -1, sampleMin,
				sampleMax, LR_L, LR_R, LR_EXH_START);
		lrrep.init();

		List<Future> localAndMCReplicas = new ArrayList<Future>(
				nrOfNonTabuReplicas);
		List<Future> tabuFutures = new ArrayList<Future>(nrOfTabuReplicas);
		List<List<Accession>> parents = new ArrayList<List<Accession>>();
		List<List<Accession>> children = new ArrayList<List<Accession>>();

		// create thread pool
		final ThreadGroup threadGroup = new ThreadGroup("replicaThreadGroup");

		ThreadFactory factory = new ThreadFactory()
		{
			public Thread newThread(Runnable r)
			{
				Thread thr = new Thread(threadGroup, r);
				thr.setPriority(Thread.MIN_PRIORITY);
				return thr;
			}
		};

		ExecutorService pool = Executors.newCachedThreadPool(factory);

		boolean cont = true, impr;
		double prevBestScore = bestScore, prog;
		int prevBestSize = ac.size();
		long lastImprTime = 0;
		long prevRoundTime = 0;
		int numround = 1;
		double lastBoostTime = 0;

		boolean lrChecked = false;

		long sTime = System.currentTimeMillis();
		long eTime = sTime + (long) (runtime * 1000);

		// start LR replica, continuously runs in background until finished
		Thread lrThread = factory.newThread(lrrep);
		lrThread.setPriority(Thread.MAX_PRIORITY);
		lrThread.start();

		long firstRounds = 0;

		while (cont && System.currentTimeMillis() < eTime)
		{
			// submit all tabu replicas
			for (Replica rep : replicas)
			{
				if (rep.shortType().equals("Tabu"))
				{
					tabuFutures.add(pool.submit(rep));
				}
			}

			// loop submission of Local and REMC replicas (short runs)
			while ((firstRounds < roundsWithoutTabu || tabuReplicasBusy(tabuFutures))
					&& cont && System.currentTimeMillis() < eTime)
			{

				firstRounds++;

				localAndMCReplicas.clear();
				// Submit non-tabu replicas
				for (int i = 0; i < replicas.size(); i++)
				{
					Replica rep = replicas.get(i);
					if (!rep.shortType().equals("Tabu"))
					{
						localAndMCReplicas.add(pool.submit(rep));
					}
				}

				// Wait until all non-tabu replicas have completed their current
				// run
				for (int i = 0; i < localAndMCReplicas.size(); i++)
				{
					try
					{
						// System.out.println("Waiting for non-tabu rep #" + (i+1));
						localAndMCReplicas.get(i).get(); // doesn't return a
                                                                                 // result, but
                                                                                 // blocks until done
					}
					catch (InterruptedException ex)
					{
						throw new SearchException("Error in thread pool: " + ex);
					}
					catch (ExecutionException ex)
					{
						throw new SearchException("Error in thread pool: " + ex);
					}
				}

				// Non-tabu replicas are done, inspect results
				impr = false;
				nrStuck = 0;
				Iterator<Replica> itr = replicas.iterator();
				while (itr.hasNext())
				{
					Replica rep = itr.next();
					if (!rep.shortType().equals("Tabu"))
					{
						// check for better solution
						if (rep.getBestScore() > bestScore
								|| (rep.getBestScore() == bestScore && rep
										.getBestCore().size() < bestCore.size()))
						{

							// store better core
							bestScore = rep.getBestScore();
							bestCore.clear();
							bestCore.addAll(rep.getBestCore());

							impr = true;
							lastImprTime = System.currentTimeMillis() - sTime;
							System.out.println("best score: " + bestScore
									+ "\tsize: " + bestCore.size() + "\ttime: "
									+ lastImprTime / 1000.0 + "\t#rep: "
									+ replicas.size() + "\tfound by: "
									+ rep.type());
							// update progress writer
							handleNewBestSolution(bestCore, bestScore) ;
						}
						// count nr of stuck non-tabu reps
						if (rep.stuck())
						{
							nrStuck++;
						}
					}
				}

				// Check LR result, if done and not checked before
				if (lrrep.isDone() && !lrChecked)
				{
					if (lrrep.getBestScore() > bestScore
							|| (lrrep.getBestScore() == bestScore && lrrep
									.getBestCore().size() < bestCore.size()))
					{

						// store better core
						bestScore = lrrep.getBestScore();
						bestCore.clear();
						bestCore.addAll(lrrep.getBestCore());

						impr = true;
						lastImprTime = System.currentTimeMillis() - sTime;
						System.out.println("best score: " + bestScore
								+ "\tsize: " + bestCore.size() + "\ttime: "
								+ lastImprTime / 1000.0 + "\t#rep: "
								+ replicas.size() + "\tfound by: "
								+ lrrep.type());
						
						handleNewBestSolution(bestCore, bestScore) ;
					}
					lrChecked = true;
					// Since LR is done, we add it to the list of replicas so
					// that its result can be used for merging
					replicas.add(lrrep);
					nrOfNonTabus++;
				}

				// update boost time
				if (!boostTimeLocked)
				{
					boostTime = boostTime / boostTimeFactor;
					boostTime = (boostTime * (numround - 1) + (System
							.currentTimeMillis() - sTime - prevRoundTime) / 1000.0)
							/ numround;
					boostTime = boostTime * boostTimeFactor;
					prevRoundTime = System.currentTimeMillis() - sTime;
				}

				prog = bestScore - prevBestScore;

				// check min progression
				if (impr && bestCore.size() >= prevBestSize && prog < minProg)
				{
					cont = false;
				}
				// check stuckTime
				if ((System.currentTimeMillis() - sTime - lastImprTime) / 1000.0 > stuckTime)
				{
					cont = false;
				}

				// check boost prog
				if (impr && prog < boostMinProg)
				{

					lastBoostTime = System.currentTimeMillis() - sTime;
					// only boost with some fraction of the normal nr of boost
					// replicas in case of min prog boost
					int progBoostNr = boostNr / PROG_BOOST_FACTOR;
					boostReplicas(replicas, progBoostNr, ac, pm, randNh,
							NR_OF_LS_STEPS, sampleMin, sampleMax);
					nrOfNonTabus += progBoostNr;
					// System.out.println("[progBoost] - #rep: " +
					// replicas.size());

				}

				// check boost time -- do not boost if previous boost effect
				// still visible!
				if ((System.currentTimeMillis() - sTime - Math.max(
						lastImprTime, lastBoostTime)) / 1000.0 > Math.max(
						boostTime, minBoostTime)
						&& replicas.size() == nrOfNonTabuReplicas
								+ nrOfTabuReplicas)
				{

					lastBoostTime = System.currentTimeMillis() - sTime;
					boostReplicas(replicas, boostNr, ac, pm, randNh,
							NR_OF_LS_STEPS, sampleMin, sampleMax);
					nrOfNonTabus += boostNr;
					boostTimeLocked = true;
					// System.out.println("[timeBoost] - #rep: " +
					// replicas.size());

				}

				// Merge replicas to create new MC replicas (non-tabu)
				int nonTabuChildren = nrOfNonTabuReplicas
						- (nrOfNonTabus - nrStuck);
				if (nonTabuChildren > 0)
				{
					// Select parents from non-tabu replicas only! (tabus are
					// still being manipulated, so skip these)
					selectParents(replicas, parents, 2 * nonTabuChildren,
							tournamentSize, rg, "Tabu");
					// Create new children by merging parents
					createNewChildren(parents, children, rg);
					// Create new MC recplicas which use merged children as
					// initial solutions
					for (List<Accession> child : children)
					{
						// New REMC replicas
						Replica rep = new SimpleMonteCarloReplica(ac, pm,
								randNh.clone(), NR_OF_LS_STEPS, -1, sampleMin,
								sampleMax, minMCTemp + rg.nextDouble()
										* (maxMCTemp - minMCTemp));
						nrOfNonTabus++;

						rep.init(child);
						replicas.add(rep);
					}
				}

				// Now permanently delete stuck non-tabu replicas
				itr = replicas.iterator();
				while (itr.hasNext())
				{
					Replica rep = itr.next();
					if (rep.stuck() && !rep.shortType().equals("Tabu"))
					{
						itr.remove();
						nrOfNonTabus--;
					}
				}

				prevBestScore = bestScore;
				prevBestSize = bestCore.size();

				numround++;
			}

			if (!tabuReplicasBusy(tabuFutures))
			{
				// Tabu replicas have finished --> check for improvements &
				// count stuck tabus
				nrStuck = 0;
				Iterator<Replica> itr = replicas.iterator();
				while (itr.hasNext())
				{
					Replica rep = itr.next();
					if (rep.shortType().equals("Tabu"))
					{
						// check for better solution
						if (rep.getBestScore() > bestScore
								|| (rep.getBestScore() == bestScore && rep
										.getBestCore().size() < bestCore.size()))
						{

							// store better core
							bestScore = rep.getBestScore();
							bestCore.clear();
							bestCore.addAll(rep.getBestCore());

							impr = true;
							lastImprTime = System.currentTimeMillis() - sTime;
							System.out.println("best score: " + bestScore
									+ "\tsize: " + bestCore.size() + "\ttime: "
									+ lastImprTime / 1000.0 + "\t#rep: "
									+ replicas.size() + "\tfound by: "
									+ rep.type());
							
							handleNewBestSolution(bestCore, bestScore) ;
						}
						// count nr of stuck non-tabu reps
						if (rep.stuck())
						{
							nrStuck++;
						}
					}
				}

				// Create new tabus by merging current results (from all
				// replicas!!!)
				int tabuChildren = nrOfTabuReplicas - (nrOfTabus - nrStuck);
				if (tabuChildren > 0)
				{
					// Select parents from all replicas!
					selectParents(replicas, parents, 2 * tabuChildren,
							tournamentSize, rg);
					// Merge parents to create children
					createNewChildren(parents, children, rg);
					// Create new tabu replicas with merged children as initial
					// solutions
					for (List<Accession> child : children)
					{
						// new Tabu replicas
						int listsize = rg.nextInt(tabuListSize) + 1;
						Replica rep = new TabuReplica(ac, pm, heurNh.clone(),
								nrOfTabuSteps, -1, sampleMin, sampleMax,
								listsize);
						nrOfTabus++;

						rep.init(child);
						replicas.add(rep);
					}
				}

				// Now permanently remove stuck tabus
				itr = replicas.iterator();
				while (itr.hasNext())
				{
					Replica rep = itr.next();
					if (rep.stuck() && rep.shortType().equals("Tabu"))
					{
						itr.remove();
						nrOfTabus--;
					}
				}

			}
			else
			{
				// Tabu replicas have not finished, which means search was
				// stopped during inner loop
				// of non-tabu replicas. Search will stop, so don't do anything
				// anymore at this point.
			}

		}

		lrrep.stop();
	}


	private static boolean tabuReplicasBusy(List<Future> tabuFutures)
	{
		// remove all tabu replica futures which are already done
		Iterator<Future> itr = tabuFutures.iterator();
		while (itr.hasNext())
		{
			if (itr.next().isDone())
			{
				itr.remove();
			}
		}
		// if busy futures remain, return true
		return tabuFutures.size() > 0;
	}

	/**
	 * Boost replicas with new randomly initialized LS replicas
	 */
	private static void boostReplicas(List<Replica> replicas, int boost,
			AccessionCollection ac, PseudoMeasure pm, Neighborhood randNh,
			int nrOfLsSteps, int sampleMin, int sampleMax)
	{

		// Boost with new LS replicas
		for (int i = 0; i < boost; i++)
		{
			Replica rep;
			// create LS Replica
			rep = new LocalSearchReplica(ac, pm, randNh.clone(), nrOfLsSteps,
					-1, sampleMin, sampleMax);
			rep.init();
			replicas.add(rep);
		}

	}

	private static void selectParents(List<Replica> replicas,
			List<List<Accession>> parents, int nrOfParents, int T, Random rg)
	{

		selectParents(replicas, parents, nrOfParents, T, rg, null);

	}

	private static void selectParents(List<Replica> replicas,
			List<List<Accession>> parents, int nrOfParents, int T, Random rg,
			String skipType)
	{
		double bestParScore, parScore;
		List<Accession> bestPar = null, nextPar;
		String bestParType = null;
		parents.clear();
		for (int i = 0; i < nrOfParents; i++)
		{
			// Tournament selection: choose T random, select best.
			// Repeat for each parent.
			bestParScore = -Double.MAX_VALUE;
			for (int j = 0; j < T; j++)
			{
				// Choose random individual
				int k = rg.nextInt(replicas.size());
				Replica rep = replicas.get(k);
				if (skipType == null || !rep.shortType().equals(skipType))
				{
					nextPar = rep.getBestCore();
					parScore = rep.getBestScore();
					// Check if new best parent found
					if (parScore > bestParScore)
					{
						bestParScore = parScore;
						bestPar = nextPar;
						bestParType = rep.type();
					}
				}
				else
				{
					j--; // ignore cases when a skipped replica was drawn
				}
			}
			parents.add(bestPar);
			// System.out.println("Parent: " + bestParType + ", score: " +
			// bestParScore);
		}
	}

	private static void createNewChildren(List<List<Accession>> parents,
			List<List<Accession>> children, Random rg)
	{

		List<Accession> parent1, parent2, child;
		int p1size, p2size, childSize;

		children.clear();
		for (int i = 0; i < parents.size() - 1; i += 2)
		{

			// Cross-over

			// Get parents (make sure parent1 is the SMALLEST one)
			if (parents.get(i).size() <= parents.get(i + 1).size())
			{
				parent1 = parents.get(i);
				p1size = parent1.size();
				parent2 = parents.get(i + 1);
				p2size = parent2.size();

			}
			else
			{
				parent1 = parents.get(i + 1);
				p1size = parent1.size();
				parent2 = parents.get(i);
				p2size = parent2.size();
			}
			// Create child (cross-over)
			childSize = p1size + rg.nextInt(p2size - p1size + 1);
			child = new ArrayList<Accession>(childSize);

			// Get some parts of parent1
			for (int j = 0; j < p1size; j++)
			{
				// Randomly decide wether to add the accession at
				// index j in parent1 to the child (probability of 50%)
				if (rg.nextBoolean())
				{
					child.add(parent1.get(j));
				}
			}
			// Get remaining parts from parent2
			int j = rg.nextInt(p2size); // Start looping over parent2 at random
										// index
			// While child not full: add new accessions from parent2
			Accession a;
			while (child.size() < childSize)
			{
				// Add new accession from parent2 if not already present in
				// child
				a = parent2.get(j);
				if (!child.contains(a))
				{
					child.add(a);
				}
				j = (j + 1) % p2size;
			}

			// Add new child to list
			children.add(child);
		}
	}
}
