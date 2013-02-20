// Copyright 2012 Guy Davenport, Herman De Beukelaer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.corehunter.search.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.neighbourhood.Neighbourhood;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.neighbourhood.impl.HeuristicSingleNeighbourhood;
import org.corehunter.neighbourhood.impl.RandomSingleNeighbourhood;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.SubsetSearch;
import org.corehunter.search.solution.SubsetSolution;

public class MixedReplicaSearch<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>, 
	DatasetType extends IndexedData<IndexType>,
	NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
	extends AbstractParallelSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType, SubsetSearch<IndexType, SolutionType>>
{
	private static final int DEFAULT_LRSEARCH_L = 2;
	private static final int DEFAULT_LRSEARCH_R = 1;
	private static final int DEFAULT_PROG_BOOST_FACTOR = 2;

	private long	              runtime;
	private double	              minimumProgression;
	private long	              stuckTime;
	private int	                numberOfTabuReplicas;
	private int	                numberOfNonTabuReplicas;
	private int	                roundsWithoutTabu;
	private int	                numberOfTabuSteps;
	private int	                tournamentSize;
	private int	                tabuListSize;
	private int	                boostNumber;
	private long	              boostMinimumProgressionTime;
	private int	                boostTimeFactor;
	private long	              minimumBoostTime;
	private double	            minimumMetropolisTemp;
	private double	            maximumMetropolisTemp;
	
	private LRSearch<IndexType, SolutionType, DatasetType> lrSearchTemplate;
	private LocalSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> localSearchTemplate;
	private MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> metropolisSearchTemplate;
	private TabuSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> tabuSearchTemplate;
	private boolean continueSearch;

	public MixedReplicaSearch()
	{
		super();
	}
	
	protected MixedReplicaSearch(MixedReplicaSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search) throws CoreHunterException
  {
  	super(search) ;
  	
		setRuntime(search.getRuntime()) ;
		setMinimumProgression(search.getMinimumProgression()) ;
		setStuckTime(search.getStuckTime()) ;
		//TODO other members
  }
	
	@Override
  public Search<SolutionType> copy() throws CoreHunterException
  {
	  return new MixedReplicaSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>(this);
  }
	
	public final long getRuntime()
  {
  	return runtime;
  }

	public final void setRuntime(long runtime) throws CoreHunterException
  {
		if (this.runtime != runtime)
		{
			this.runtime = runtime;
			
			handleRuntimeSet() ;
		}
  }

	public final double getMinimumProgression()
  {
  	return minimumProgression;
  }

	public final void setMinimumProgression(double minimumProgression) throws CoreHunterException
  {
		if (this.minimumProgression != minimumProgression)
		{
			this.minimumProgression = minimumProgression;
			
			handleMinimumProgressionSet() ;
		}
  }

	public final long getStuckTime()
  {
  	return stuckTime;
  }

	public final void setStuckTime(long stuckTime) throws CoreHunterException
  {
		if (this.stuckTime != stuckTime)
		{
			this.stuckTime = stuckTime;
			
			handleStuckTimeSet() ;
		}
  }

	public final int getNumberOfTabuReplicas()
  {
  	return numberOfTabuReplicas;
  }

	public final void setNumberOfTabuReplicas(int numberOfTabuReplicas) throws CoreHunterException
  {
		if (this.numberOfTabuReplicas != numberOfTabuReplicas)
		{
			this.numberOfTabuReplicas = numberOfTabuReplicas;
			
			handleNumberOfTabuReplicasSet() ;
		}
  }

	public final int getNumberOfNonTabuReplicas()
  {
  	return numberOfNonTabuReplicas;
  }

	public final void setNumberOfNonTabuReplicas(int numberOfNonTabuReplicas) throws CoreHunterException
  {
		if (this.numberOfNonTabuReplicas != numberOfNonTabuReplicas)
		{
			this.numberOfNonTabuReplicas = numberOfNonTabuReplicas;
			
			handleNumberOfNonTabuReplicasSet() ;
		}
  }

	public final int getRoundsWithoutTabu()
  {
  	return roundsWithoutTabu;
  }

	public final void setRoundsWithoutTabu(int roundsWithoutTabu) throws CoreHunterException
  {
		if (this.roundsWithoutTabu != roundsWithoutTabu)
		{
			this.roundsWithoutTabu = roundsWithoutTabu;
			
			handleRoundsWithoutTabuSet() ;
		}
  }

	public final int getNumberOfTabuSteps()
  {
  	return numberOfTabuSteps;
  }
	
	public final void setNumberOfTabuSteps(int numberOfTabuSteps) throws CoreHunterException
  {
		if (this.numberOfTabuSteps != numberOfTabuSteps)
		{
			this.numberOfTabuSteps = numberOfTabuSteps;
			
			handleNumberOfTabuStepsSet() ;
		}
  }
	
	public final int getTournamentSize()
  {
  	return tournamentSize;
  }

	public final void setTournamentSize(int tournamentSize) throws CoreHunterException
  {
		if (this.tournamentSize != tournamentSize)
		{
			this.tournamentSize = tournamentSize;
			
			handleTournamentSizeSet() ;
		}
  }
	
	public final int getTabuListSize()
  {
  	return tabuListSize;
  }

	public final void setTabuListSize(int tabuListSize) throws CoreHunterException
  {
		if (this.tabuListSize != tabuListSize)
		{
			this.tabuListSize = tabuListSize;
			
			handleTabuListSizeSet() ;
		}
  }
	
	public final int getBoostNumber()
  {
  	return boostNumber;
  }

	public final void setBoostNumber(int boostNumber) throws CoreHunterException
  {
		if (this.boostNumber != boostNumber)
		{
			this.boostNumber = boostNumber;
			
			handleBoostNumberSet() ;
		}
  }
	
	public final long getBoostMinimumProgressionTime()
  {
  	return boostMinimumProgressionTime;
  }

	public final void setBoostMinimumProgressionTime(
			long boostMinimumProgressionTime) throws CoreHunterException
  {
		if (this.boostMinimumProgressionTime != boostMinimumProgressionTime)
		{
			this.boostMinimumProgressionTime = boostMinimumProgressionTime;
			
			handleBoostMinimumProgressionTimeSet() ;
		}
  }
	
	public final int getBoostTimeFactor()
  {
  	return boostTimeFactor;
  }

	public final void setBoostTimeFactor(int boostTimeFactor) throws CoreHunterException
  {
		if (this.boostTimeFactor != boostTimeFactor)
		{
			this.boostTimeFactor = boostTimeFactor;
			
			handleBoostTimeFactorSet() ;
		}
  }

	public final long getMinimumBoostTime()
  {
  	return minimumBoostTime;
  }

	public final void setMinimumBoostTime(long minimumBoostTime) throws CoreHunterException
  {
		if (this.minimumBoostTime != minimumBoostTime)
		{
			this.minimumBoostTime = minimumBoostTime;
			
			handleMinimumBoostTimeSet() ;
		}
  }	
	
	public final LRSearch<IndexType, SolutionType, DatasetType> getLrSearchTemplate()
  {
  	return lrSearchTemplate;
  }

	public final void setLrSearchTemplate(
      LRSearch<IndexType, SolutionType, DatasetType> lrSearchTemplate) throws CoreHunterException
  {
		if (this.lrSearchTemplate != lrSearchTemplate)
		{
			this.lrSearchTemplate = lrSearchTemplate;
			
			handlelrSearchTemplateSet() ;
		}
  }

	public final LocalSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> getLocalSearchTemplate()
  {
  	return localSearchTemplate;
  }

	public final void setLocalSearchTemplate(
      LocalSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> localSearchTemplate) throws CoreHunterException
  {
		if (this.localSearchTemplate != localSearchTemplate)
		{
			this.localSearchTemplate = localSearchTemplate;
			
			handleLocalSearchTemplateSet() ;
		}
  }

	public final MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> getMetropolisSearchTemplate()
  {
  	return metropolisSearchTemplate;
  }

	public final void setMetropolisSearchTemplate(
      MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> metropolisSearchTemplate) throws CoreHunterException
  {
		if (this.metropolisSearchTemplate != metropolisSearchTemplate)
		{
			this.metropolisSearchTemplate = metropolisSearchTemplate;
			
			handleMetropolisSearchTemplateSet() ;
		}
  }

	public final TabuSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> getTabuSearchTemplate()
  {
  	return tabuSearchTemplate;
  }

	public final void setTabuSearchTemplate(
      TabuSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> tabuSearchTemplate) throws CoreHunterException
  {
		if (this.tabuSearchTemplate != tabuSearchTemplate)
		{
			this.tabuSearchTemplate = tabuSearchTemplate;
			
			handleTabuSearchTemplateSet() ;
		}
  }

	protected void handleRuntimeSet() throws CoreHunterException
  {
	  if (runtime < 0)
	  	throw new CoreHunterException("Runtime can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Runtime can not be set while search in process") ;
  }
	
	protected void handleMinimumProgressionSet() throws CoreHunterException
  {
	  if (minimumProgression < 0)
	  	throw new CoreHunterException("Minimum Progression can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Minimum Progression can not be set while search in process") ;
  }
	
	protected void handleStuckTimeSet() throws CoreHunterException
  {
	  if (stuckTime < 0)
	  	throw new CoreHunterException("Stuck Time can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Stuck Time can not be set can not be set while search in process") ;
  }

	protected void handleNumberOfTabuReplicasSet() throws CoreHunterException
  {
	  if (numberOfTabuReplicas < 0)
	  	throw new CoreHunterException("Number Of Tabu Replicas can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Number Of Tabu Replicas can not be set while search in process") ;
  }
	
	protected void handleNumberOfNonTabuReplicasSet() throws CoreHunterException
  {
	  if (numberOfNonTabuReplicas < 0)
	  	throw new CoreHunterException("Number Of Non Tabu Replicas can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Number Of Non Tabu Replicas can not be set while search in process") ;
  }

	protected void handleRoundsWithoutTabuSet() throws CoreHunterException
  {
	  if (roundsWithoutTabu < 0)
	  	throw new CoreHunterException("Rounds Without Tabu can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Rounds Without Tabu can not be set while search in process") ;
  }
	
	protected void handleNumberOfTabuStepsSet() throws CoreHunterException
  {
	  if (numberOfTabuSteps < 0)
	  	throw new CoreHunterException("Number Of Tabu Steps can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Number Of TabuS teps can not be set while search in process") ;
  }

	protected void handleTournamentSizeSet() throws CoreHunterException
  {
	  if (tournamentSize < 0)
	  	throw new CoreHunterException("Tournament Size can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Tournament Size  can not be set while search in process") ;
  }

	protected void handleTabuListSizeSet() throws CoreHunterException
  {
	  if (tabuListSize < 0)
	  	throw new CoreHunterException("Tabu List Size can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Tabu List Size can not be set while search in process") ;
  }

	protected void handleBoostNumberSet() throws CoreHunterException
  {
	  if (boostNumber < 0)
	  	throw new CoreHunterException("Boost Number can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Boost Number can not be set while search in process") ;
  }

	protected void handleBoostMinimumProgressionTimeSet() throws CoreHunterException
  {
	  if (boostMinimumProgressionTime < 0)
	  	throw new CoreHunterException("Boost Minimum Progression Time can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Boost Minimum Progression Time can not be set while search in process") ;
  }
	
	protected void handleBoostTimeFactorSet() throws CoreHunterException
  {
	  if (boostTimeFactor < 0)
	  	throw new CoreHunterException("Boost Time Factor can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Boost Time Factor can not be set while search in process") ;
  }
	
	protected void handleMinimumBoostTimeSet() throws CoreHunterException
  {
	  if (minimumBoostTime < 0)
	  	throw new CoreHunterException("Minimum Boost Time can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Minimum Boost Time can not be set while search in process") ;
  }
	
	protected void handlelrSearchTemplateSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("LRSearch template can not be set while search in process") ;
  }
	
	protected void handleLocalSearchTemplateSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Local Search template can not be set while search in process") ;
  }

	protected void handleMetropolisSearchTemplateSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Metropolis Search template can not be set while search in process") ;
  }
	
	protected void handleTabuSearchTemplateSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Tabu Search template can not be set while search in process") ;
  }

	@Override
	protected void runSearch() throws CoreHunterException
	{
		continueSearch = true ;
		
		long boostTime = 0;
		boolean boostTimeLocked = false;

		// LS can perform more steps than tabu because each step is very fast,
		// only sampling one neighbour instead of Tabu which samples about
		// ac.size()
		// neighbours in each step to select the (heuristic) best neighbour
		int numberOfSteps = getSolution().getSubsetSize();

		Neighbourhood<SolutionType> randNeighbourhood = createRandomSingleNeighbourhood(getNeighbourhood().getSubsetMinimumSize(), getNeighbourhood().getSubsetMaximumSize());
		Neighbourhood<SolutionType> heurNeighbourhood = createHeuristicSingleNeighbourhood(getNeighbourhood().getSubsetMinimumSize(), getNeighbourhood().getSubsetMaximumSize());

		// create, init and store initial replicas (local search)
		List<SubsetSearch<IndexType, SolutionType>> nonTabuReplicas = new ArrayList<SubsetSearch<IndexType, SolutionType>>(numberOfNonTabuReplicas);
		List<SubsetSearch<IndexType, SolutionType>> tabuReplicas = new ArrayList<SubsetSearch<IndexType, SolutionType>>(numberOfTabuReplicas);
		
		List<SolutionType> parents ;
		List<SolutionType> children ;

		int nrOfTabus = 0;
		int nrOfNonTabus = numberOfNonTabuReplicas;
		int nrStuck;

		double previousBestEvaluation = getBestSolutionEvaluation() ;
		int previousBestSubsetSize = getBestSolution().getSubsetSize();
		
		double progression = 0 ;
		//long lastImprovementTime = 0;
		
		// All tasks are done, inspect results
		Iterator<SubsetSearch<IndexType, SolutionType>> iterator ;
		
		SubsetSearch<IndexType, SolutionType> subSearch ;
		double bestReplicaEvaluation = getWorstEvaluation() ;
		boolean improvement ;
		SubsetSearch<IndexType, SolutionType> bestSubSearch = null;
		
		long prevRoundTime = 0;
		long numround = 1;
		long lastBoostTime = 0;

		boolean lrChecked = false;
		
		// create and init one LR Semi replica
		SubsetSearch<IndexType, SolutionType> lrReplica = createLRSearch();

		List<Future<SubsetSearch<IndexType, SolutionType>>> localAndMCFutures = new ArrayList<Future<SubsetSearch<IndexType, SolutionType>>>(numberOfNonTabuReplicas);
		List<Future<SubsetSearch<IndexType, SolutionType>>> tabuFutures = new ArrayList<Future<SubsetSearch<IndexType, SolutionType>>>(numberOfTabuReplicas);
		
		// add Local Search Replicas
		for (int i = 0; i < numberOfNonTabuReplicas; i++)
		{
			SubsetSearch<IndexType, SolutionType> replica;

			// initially, create some extra LS Replica
			replica = createLocalSearch(randNeighbourhood, numberOfSteps);

			nonTabuReplicas.add(replica);
		}
		
		// start LR replica, continuously runs in background until finished
		submitSubSearch((SubsetSearch<IndexType, SolutionType>) lrReplica, Thread.MAX_PRIORITY) ;

		long firstRounds = 0;

		while (continueSearch && getSearchTime() < runtime)
		{
			// submit all tabu replicas
			submitSubSearches(tabuReplicas) ;

			// loop submission of Local and REMC replicas (short runs)
			while ((firstRounds < roundsWithoutTabu || tabuReplicasBusy(tabuFutures))
			    && continueSearch && getSearchTime() < runtime)
			{
				firstRounds++;

				localAndMCFutures.clear();
				
				startSubSearches(nonTabuReplicas) ;

				// Non-tabu replicas are done, inspect results
				improvement = false;
				nrStuck = 0;
				
				// All tasks are done, find best result
				iterator = nonTabuReplicas.iterator() ;
				
				improvement = false ;
				
				while (iterator.hasNext())
				{
					subSearch = iterator.next() ;
					
					if (SearchStatus.COMPLETED.equals(subSearch.getStatus()))
					{
						bestReplicaEvaluation = subSearch.getBestSolutionEvaluation() ;
		
						if (isBetterSolution(subSearch.getBestSolutionEvaluation() , bestReplicaEvaluation)
						    || (previousBestEvaluation == previousBestEvaluation && subSearch.getBestSolution().getSubsetSize() < previousBestSubsetSize))
						{	
							bestSubSearch = subSearch ;
						}
					}
					
					if (subSearch.isStuck())
					{
						nrStuck++;
					}
				}

				// Check LR result, if done and not checked before
				if (SearchStatus.COMPLETED.equals(lrReplica.getStatus()) && !lrChecked)
				{
					if (isBetterSolution(lrReplica.getBestSolutionEvaluation() , bestReplicaEvaluation)
					    || (lrReplica.getBestSolutionEvaluation() == bestReplicaEvaluation && lrReplica.getBestSolution().getSubsetSize() < previousBestSubsetSize))
					{
						bestSubSearch = lrReplica ;
					}
					
					lrChecked = true;
					// Since LR is done, we add it to the list of replicas so
					// that its result can be used for merging
					nonTabuReplicas.add(lrReplica);
					nrOfNonTabus++;
				}

				// update boost time
				if (!boostTimeLocked)
				{
					boostTime = boostTime / boostTimeFactor;
					boostTime = (boostTime * (numround - 1) + (getSearchTime() - prevRoundTime))
					    / numround;
					boostTime = boostTime * boostTimeFactor;
					prevRoundTime = getSearchTime() ;
				}
				
				if (bestSubSearch != null)
				{
					progression = bestSubSearch.getBestSolutionEvaluation() - previousBestEvaluation;

					// check min progression
					if (improvement && bestSubSearch.getBestSolution().getSubsetSize() >= previousBestSubsetSize && progression < minimumProgression)
					{
						continueSearch = false;
					}
					
					previousBestEvaluation = bestSubSearch.getBestSolutionEvaluation() ;
					previousBestSubsetSize = bestSubSearch.getBestSolution().getSubsetSize();
				}

				// check stuckTime
				continueSearch = continueSearch && stuckTime > getBestSolutionTime() ;

				// check boost prog
				if (improvement && progression < boostMinimumProgressionTime)
				{
					lastBoostTime = getBestSolutionTime() ; // TODO check if this correct, this is the nanoseconds since last best solution
					// only boost with some fraction of the normal nr of boost
					// replicas in case of min prog boost
					int progBoostNr = boostNumber / DEFAULT_PROG_BOOST_FACTOR;
					boostReplicas(nonTabuReplicas, progBoostNr, randNeighbourhood, numberOfSteps);
					nrOfNonTabus += progBoostNr;
					// System.out.println("[progBoost] - #rep: " +
					// replicas.size());
				}

				// check boost time -- do not boost if previous boost effect
				// still visible!
				// TOOD getBestSolutionTime() may not be appropriate here
				if (getBestSolutionTime()  - Math.max(getBestSolutionTime()  ,
				    lastBoostTime)  > Math.max(boostTime, minimumBoostTime)
				    && nonTabuReplicas.size() == numberOfNonTabuReplicas + numberOfTabuReplicas)
				{

					lastBoostTime = getBestSolutionTime() ;
					boostReplicas(nonTabuReplicas, boostNumber, randNeighbourhood, numberOfSteps);
					nrOfNonTabus += boostNumber;
					boostTimeLocked = true;
					// System.out.println("[timeBoost] - #rep: " +
					// replicas.size());

				}

				// Merge replicas to create new MC replicas (non-tabu)
				int nonTabuChildren = numberOfNonTabuReplicas - (nrOfNonTabus - nrStuck);
				
				if (nonTabuChildren > 0)
				{
					// Select parents from non-tabu replicas only! (tabus are
					// still being manipulated, so skip these)
					parents = selectParents(nonTabuReplicas, 2 * nonTabuChildren, tournamentSize,
					    getRandom(), TabuSearch.class);
					
					// Create new children by merging parents
					children = createNewChildren(parents, getRandom());
					// Create new MC recplicas which use merged children as
					// initial solutions
					for (SolutionType child : children)
					{
						// New REMC replicas
						SubsetSearch<IndexType, SolutionType> replica = createMetropolisSearch(randNeighbourhood.copy(), minimumMetropolisTemp
						        + getRandom().nextDouble() * (maximumMetropolisTemp - minimumMetropolisTemp));
						nrOfNonTabus++;

						nonTabuReplicas.add(replica);
					}
				}

				// Now permanently delete stuck non-tabu replicas
				iterator = nonTabuReplicas.iterator();
				while (iterator.hasNext())
				{
					Search<SolutionType> replica = iterator.next();
					if (replica.isStuck())
					{
						iterator.remove();
						nrOfNonTabus--;
					}
				}

				previousBestEvaluation = getBestSolutionEvaluation() ;
				previousBestSubsetSize = getBestSolution().getSubsetSize();

				numround++;
			}

			if (!tabuReplicasBusy(tabuFutures))
			{
				// Tabu replicas have finished --> check for improvements &
				// count stuck tabus
				nrStuck = 0;
				Iterator<SubsetSearch<IndexType, SolutionType>> itr = tabuReplicas.iterator();
				while (itr.hasNext())
				{
					SubsetSearch<IndexType, SolutionType> replica = itr.next();
						// check for better solution
						if (replica.getBestSolutionEvaluation() > previousBestEvaluation
						    || (replica.getBestSolutionEvaluation() == getBestSolutionEvaluation() && replica.getBestSolution().getSubsetSize() < previousBestSubsetSize))
						{
							improvement = true;
						}
						// count nr of stuck non-tabu reps
						if (replica.isStuck())
						{
							nrStuck++;
						}
				}

				// Create new tabus by merging current results (from all
				// replicas!!!)
				int tabuChildren = numberOfTabuReplicas - (nrOfTabus - nrStuck);
				if (tabuChildren > 0)
				{
					// Select parents from all replicas!
					parents = selectParents(nonTabuReplicas, 2 * tabuChildren, tournamentSize, getRandom());
					// Merge parents to create children
					children = createNewChildren(parents, getRandom());
					// Create new tabu replicas with merged children as initial
					// solutions
					for (SolutionType child : children)
					{
						// new Tabu replicas
						int listsize = getRandom().nextInt(tabuListSize) + 1;
						SubsetSearch<IndexType, SolutionType> rep = createTabuSearch(numberOfTabuSteps, listsize);
						nrOfTabus++;

						nonTabuReplicas.add(rep);
					}
				}

				// Now permanently remove stuck tabus
				itr = nonTabuReplicas.iterator();
				while (itr.hasNext())
				{
					Search<SolutionType> rep = itr.next();
					if (rep.isStuck())
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
		
		// TODO need to remove future etc.

		lrReplica.stop();
	}

	@Override
  protected void stopSearch() throws CoreHunterException
  {
		continueSearch = false;
  }
	
  protected final void boostReplicas(List<SubsetSearch<IndexType, SolutionType>> replicas,
      int progBoostNr, Neighbourhood<SolutionType> neighbourhood, int numberOfSteps) throws CoreHunterException
	{
		SubsetSearch<IndexType, SolutionType> replica ;

		// Boost with new LS replicas
		for (int i = 0; i < progBoostNr; i++)
		{
			// create LS Replica
			replica = createLocalSearch(neighbourhood.copy(), numberOfSteps) ;

			replicas.add(replica);
		}
	}

	@SuppressWarnings("unchecked")
  protected SubsetSearch<IndexType, SolutionType> createLRSearch() throws CoreHunterException
  {
		LRSearch<IndexType, SolutionType, DatasetType> search ;
		
		if (lrSearchTemplate != null)
		{
			search = (LRSearch<IndexType, SolutionType, DatasetType>) lrSearchTemplate.copy() ;
		}
		else
		{
			search = new LRSearch<IndexType, SolutionType, DatasetType>() ;
			
			search.setL(DEFAULT_LRSEARCH_L) ;
			search.setR(DEFAULT_LRSEARCH_R) ;
		}

		search.setCurrentSolution((SolutionType) getSolution().copy()) ;
		search.setObjectiveFunction(getObjectiveFunction()) ; // TODO perhaps a copy of the Objective function is required for optimisation
		search.setData(getData()) ;
		search.setSubsetMinimumSize(getNeighbourhood().getSubsetMinimumSize()) ;
		search.setSubsetMaximumSize(getNeighbourhood().getSubsetMinimumSize()) ;
		
	  return (SubsetSearch<IndexType, SolutionType>) search;
  }

	@SuppressWarnings("unchecked")
  protected SubsetSearch<IndexType, SolutionType> createLocalSearch(Neighbourhood<SolutionType> neighbourhood, int numberOfSteps) throws CoreHunterException
  {
		LocalSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search ;
		
		if (localSearchTemplate != null)
		{
			search = (LocalSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>)localSearchTemplate.copy() ;
		}
		else
		{
			search = new LocalSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>() ;
		}
		
		search.setCurrentSolution((SolutionType) getSolution().copy()) ;
		search.setObjectiveFunction(getObjectiveFunction()) ; // TODO perhaps a copy of the Objective function is required for optimisation
		search.setNeighbourhood((NeighbourhoodType)neighbourhood) ;
		search.setData(getData()) ;
		//search.setNumberOfSteps(numberOfSteps) ; TODO

	  return (SubsetSearch<IndexType, SolutionType>) search;
  }
	
	@SuppressWarnings("unchecked")
  protected SubsetSearch<IndexType, SolutionType> createMetropolisSearch(Neighbourhood<SolutionType> neighbourhood, double temperature) throws CoreHunterException
  {
		MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search ;
		
		if (metropolisSearchTemplate != null)
		{
			search = (MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>)metropolisSearchTemplate.copy() ;
		}
		else
		{
			search = new MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>() ;

		}
		
	// TODO set defaults for MetropolisSearch
		search.setCurrentSolution((SolutionType) getSolution().copy()) ;
		search.setObjectiveFunction(getObjectiveFunction()) ; // TODO perhaps a copy of the Objective function is required for optimisation
		search.setNeighbourhood((NeighbourhoodType)neighbourhood) ;
		search.setData(getData()) ;
		
	  return (SubsetSearch<IndexType, SolutionType>) search;
		
  }
	
	@SuppressWarnings("unchecked")
  private SubsetSearch<IndexType, SolutionType> createTabuSearch(int numberOfTabuSteps, int tabuListSize) throws CoreHunterException
  {
		TabuSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search ;
		
		if (tabuSearchTemplate != null)
		{
			search = (TabuSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>) tabuSearchTemplate.copy() ;
			
			search.setTabuListSize(tabuListSize) ;
		}
		else
		{
			search = new TabuSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>() ;
		}
		
		search.setCurrentSolution((SolutionType) getSolution().copy()) ;
		search.setObjectiveFunction(getObjectiveFunction()) ; // TODO perhaps a copy of the Objective function is required for optimisation
		search.setData(getData()) ;
		search.setNeighbourhood((NeighbourhoodType)getNeighbourhood().copy()) ;
		search.setTabuListSize(tabuListSize) ;
		
	  return (SubsetSearch<IndexType, SolutionType>) search;
  }

	protected Neighbourhood<SolutionType> createHeuristicSingleNeighbourhood(
      int subsetMinimumSize, int subsetMaximumSize) throws CoreHunterException
  {
		HeuristicSingleNeighbourhood<IndexType, SolutionType> neighbourhood = new HeuristicSingleNeighbourhood<IndexType, SolutionType>() ;
		
		neighbourhood.setSubsetMinimumSize(subsetMinimumSize) ;
		neighbourhood.setSubsetMaximumSize(subsetMaximumSize) ;
		
 	  return neighbourhood;
  }

	protected Neighbourhood<SolutionType> createRandomSingleNeighbourhood(
      int subsetMinimumSize, int subsetMaximumSize) throws CoreHunterException
  {
		RandomSingleNeighbourhood<IndexType, SolutionType> neighbourhood = new RandomSingleNeighbourhood<IndexType, SolutionType>() ;
		
		neighbourhood.setSubsetMinimumSize(subsetMinimumSize) ;
		neighbourhood.setSubsetMaximumSize(subsetMaximumSize) ;
		
 	  return neighbourhood;
  }

	protected List<SolutionType> selectParents(List<SubsetSearch<IndexType, SolutionType>> replicas,
	    int nrOfParents, int T, Random random)
	{
		return selectParents(replicas, nrOfParents, T, random, null);
	}

	protected List<SolutionType> selectParents(List<SubsetSearch<IndexType, SolutionType>> replicas, int nrOfParents, int T, Random random,
	    @SuppressWarnings("rawtypes") Class<? extends Search> skipType)
	{
		List<SolutionType> parents = new ArrayList<SolutionType>(nrOfParents);
		
		double bestParentEvaluation ;
		double parentEvaluation ;
		
		SolutionType bestParent = null ;
		SolutionType nextParent ;
		Class<? extends Search> bestParentType = null ;
		SubsetSearch<IndexType, SolutionType> replica ;
		
		for (int i = 0; i < nrOfParents; i++)
		{
			// Tournament selection: choose T random, select best.
			// Repeat for each parent.
			bestParentEvaluation = getWorstEvaluation() ;
			for (int j = 0; j < T; j++)
			{
				// Choose random individual
				int k = random.nextInt(replicas.size());
				replica = replicas.get(k);
				
				if (skipType == null || !skipType.isAssignableFrom(replica.getClass()))
				{
					nextParent = replica.getBestSolution() ;
					parentEvaluation = replica.getBestSolutionEvaluation() ;
					// Check if new best parent found
					if (parentEvaluation > bestParentEvaluation)
					{
						bestParentEvaluation = parentEvaluation;
						bestParent = nextParent;
						bestParentType = replica.getClass();
					}
				}
				else
				{
					j--; // ignore cases when a skipped replica was drawn
				}
			}
			parents.add(bestParent);
			// System.out.println("Parent: " + bestParType + ", score: " +
			// bestParScore);
		}
		
		return parents ;
	}

	protected List<SolutionType> createNewChildren(List<SolutionType> parents, Random random)
	{
		List<SolutionType> children = new ArrayList<SolutionType>() ;
		
		SolutionType parent1 ;
		SolutionType parent2 ;
		SolutionType child ;
		int p1size, p2size, childSize;

		for (int i = 0; i < parents.size() - 1; i += 2)
		{
			// Cross-over

			// Get parents (make sure parent1 is the SMALLEST one)
			if (parents.get(i).getSubsetSize() <= parents.get(i + 1).getSubsetSize())
			{
				parent1 = parents.get(i);
				p1size = parent1.getSubsetSize();
				parent2 = parents.get(i + 1);
				p2size = parent2.getSubsetSize();

			}
			else
			{
				parent1 = parents.get(i + 1);
				p1size = parent1.getSubsetSize();
				parent2 = parents.get(i);
				p2size = parent2.getSubsetSize();
			}
			
			// Create child (cross-over)
			childSize = p1size + random.nextInt(p2size - p1size + 1);
			child = createChildSolution(parent1);

			// Get some parts of parent1
			for (int j = 0; j < p1size; j++)
			{
				// Randomly decide whether to add the indices at
				// index j in parent1 to the child (probability of 50%)
				if (random.nextBoolean())
				{
					child.addIndex(parent1.getIndexInSubsetAt(j));
				}
			}
			// Get remaining parts from parent2
			int j = random.nextInt(p2size); // Start looping over parent2 at random
			// index
			// While child not full: add new indices from parent2

			IndexType index ;
			
			while (child.getSubsetSize() < childSize)
			{
				// Add new accession from parent2 if not already present in
				// child
				index = parent2.getIndexInSubsetAt(j);
				if (!child.containsIndexInSubset(index))
				{
					child.addIndex(index);
				}
				j = (j + 1) % p2size;
			}

			// Add new child to list
			children.add(child);
		}
		
		return children ;
	}

	@SuppressWarnings("unchecked")
  private SolutionType createChildSolution(SolutionType templateSolution)
  {
		SolutionType childSolution = (SolutionType) templateSolution.copy() ;
		
		childSolution.removeAllIndices() ;
		
	  return childSolution;
  }
}
