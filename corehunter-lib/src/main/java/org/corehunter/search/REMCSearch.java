//  Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer, Guy Davenport
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
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.corehunter.Accession;
import org.corehunter.AccessionCollection;
import org.corehunter.CoreHunterException;
import org.corehunter.measures.PseudoMeasure;

import static org.corehunter.Constants.K_b2;

public class REMCSearch extends AbstractSubsetSearch
{
	private AccessionCollection ac;
	private Neighborhood nh;
	private PseudoMeasure pm;
	private int sampleMin;
	private int sampleMax;
	private double runtime;
	private double minProg;
	private double stuckTime;
	private int numReplicas;
	private double minT;
	private double maxT;
	private int mcSteps;

	public REMCSearch(AccessionCollection ac, Neighborhood nh,
			PseudoMeasure pm, int sampleMin, int sampleMax, double runtime,
			double minProg, double stuckTime, int numReplicas, double minT,
			double maxT, int mcSteps)
	{
		super();
		this.ac = ac;
		this.nh = nh;
		this.pm = pm;
		this.sampleMin = sampleMin;
		this.sampleMax = sampleMax;
		this.runtime = runtime;
		this.minProg = minProg;
		this.stuckTime = stuckTime;
		this.numReplicas = numReplicas;
		this.minT = minT;
		this.maxT = maxT;
		this.mcSteps = mcSteps;
	}

	public final AccessionCollection getAc()
	{
		return ac;
	}

	public final void setAc(AccessionCollection ac)
	{
		this.ac = ac;
	}

	public final Neighborhood getNh()
	{
		return nh;
	}

	public final void setNh(Neighborhood nh)
	{
		this.nh = nh;
	}

	public final PseudoMeasure getPm()
	{
		return pm;
	}

	public final void setPm(PseudoMeasure pm)
	{
		this.pm = pm;
	}

	public final int getSampleMin()
	{
		return sampleMin;
	}

	public final void setSampleMin(int sampleMin)
	{
		this.sampleMin = sampleMin;
	}

	public final int getSampleMax()
	{
		return sampleMax;
	}

	public final void setSampleMax(int sampleMax)
	{
		this.sampleMax = sampleMax;
	}

	public final double getRuntime()
	{
		return runtime;
	}

	public final void setRuntime(double runtime)
	{
		this.runtime = runtime;
	}

	public final double getMinProg()
	{
		return minProg;
	}

	public final void setMinProg(double minProg)
	{
		this.minProg = minProg;
	}

	public final double getStuckTime()
	{
		return stuckTime;
	}

	public final void setStuckTime(double stuckTime)
	{
		this.stuckTime = stuckTime;
	}

	public final int getNumReplicas()
	{
		return numReplicas;
	}

	public final void setNumReplicas(int numReplicas)
	{
		this.numReplicas = numReplicas;
	}

	public final double getMinT()
	{
		return minT;
	}

	public final void setMinT(double minT)
	{
		this.minT = minT;
	}

	public final double getMaxT()
	{
		return maxT;
	}

	public final void setMaxT(double maxT)
	{
		this.maxT = maxT;
	}

	public final int getMcSteps()
	{
		return mcSteps;
	}

	public final void setMcSteps(int mcSteps)
	{
		this.mcSteps = mcSteps;
	}

	protected void runSearch() throws CoreHunterException
	{
		SimpleMonteCarloReplica replicas[] = new SimpleMonteCarloReplica[numReplicas];
		Random r = new Random();

		for (int i = 0; i < numReplicas; i++)
		{
			double T = minT + i * (maxT - minT) / (numReplicas - 1);
			replicas[i] = new SimpleMonteCarloReplica(ac, pm, nh.clone(),
					mcSteps, -1, sampleMin, sampleMax, T);
			replicas[i].init();
		}

		double bestScore = -Double.MAX_VALUE;
		List<Accession> bestCore = new ArrayList<Accession>();

		List<Future> futures = new ArrayList<Future>(numReplicas);
		ExecutorService pool = Executors.newCachedThreadPool();

		long eTime = getStartTime() + (long) (runtime * 1000);

		int swapBase = 0;
		boolean cont = true, impr;
		double prevBestScore = bestScore, prog;
		int prevBestSize = ac.size();
		long lastImprTime = 0;

		while (cont && System.currentTimeMillis() < eTime)
		{

			// run MC search for each replica (parallel in pool!)
			for (int i = 0; i < numReplicas; i++)
			{
				Future fut = pool.submit(replicas[i]);
				futures.add(fut);
			}

			// Wait until all tasks have been completed
			for (int i = 0; i < futures.size(); i++)
			{
				try
				{
					futures.get(i).get(); // doesn't return a result, but blocks
											// until done
				}
				catch (InterruptedException ex)
				{
					throw new CoreHunterException("Error in thread pool: " + ex);
				}
				catch (ExecutionException ex)
				{
					throw new CoreHunterException("Error in thread pool: " + ex);
				}
			}

			// All tasks are done, inspect results
			impr = false;
			for (int i = 0; i < numReplicas; i++)
			{
				double bestRepScore = replicas[i].getBestScore();

				if (bestRepScore > bestScore
						|| (bestRepScore == bestScore && replicas[i]
								.getBestCore().size() < bestCore.size()))
				{

					bestScore = bestRepScore;
					bestCore.clear();
					bestCore.addAll(replicas[i].getBestCore());

					impr = true;
					lastImprTime = System.currentTimeMillis() - getStartTime() ;
					System.out.println("best score: " + bestRepScore
							+ "\tsize: " + bestCore.size() + "\ttime: "
							+ lastImprTime / 1000.0);

					handleNewBestSolution(bestCore, bestScore) ;
				}
			}

			// check min progression
			prog = bestScore - prevBestScore;
			if (impr && bestCore.size() >= prevBestSize && prog < minProg)
			{
				cont = false;
			}
			// check stuckTime
			if ((System.currentTimeMillis() - getStartTime() - lastImprTime) / 1000.0 > stuckTime)
			{
				cont = false;
			}

			prevBestScore = bestScore;
			prevBestSize = bestCore.size();

			// consider swapping temperatures of adjacent replicas
			for (int i = swapBase; i < numReplicas - 1; i += 2)
			{
				SimpleMonteCarloReplica m = replicas[i];
				SimpleMonteCarloReplica n = replicas[i + 1];

				double B_m = 1.0 / (K_b2 * m.getTemperature());
				double B_n = 1.0 / (K_b2 * n.getTemperature());
				double B_diff = B_n - B_m;
				double E_delta = m.getScore() - n.getScore();

				boolean swap = false;

				if (E_delta <= 0)
				{
					swap = true;
				}
				else
				{
					double p = r.nextDouble();

					if (Math.exp(B_diff * E_delta) > p)
					{
						swap = true;
					}
				}

				if (swap)
				{
					m.swapTemperature(n);
					SimpleMonteCarloReplica temp = replicas[i];
					replicas[i] = replicas[i + 1];
					replicas[i + 1] = temp;
				}
			}
			
			swapBase = 1 - swapBase;
		}
		
		fireSearchCompleted();
	}
}
