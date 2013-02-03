// Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer, Guy Davenport
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

import static org.corehunter.Constants.K_b2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.SubsetSolution;

public class REMCSearch<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>, 
	DatasetType extends IndexedData<IndexType>,
	NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
	extends AbstractParallelSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType, MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>>
{
	private long	              minimumProgressionTime;
	private long	              stuckTime;
	private int	                numberOfReplicas;
	private double	            minimumTemperature;
	private double	            maximumTemperature;
	private int	                steps;
	private long   						  runtime;
	
	private boolean 						continueSearch ;
	
	public REMCSearch()
	{
		super();
	}
	
	protected REMCSearch(REMCSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search) throws CoreHunterException
	{
		super(search);
		
		setMinimumProgressionTime(search.getMinimumProgressionTime()) ;
		setStuckTime(search.getStuckTime()) ;
		setNumberOfReplicas(search.getNumberOfReplicas()) ;
		setMinimumTemperature(search.getMinimumTemperature()) ;
		setMaximumTemperature(search.getMaximumTemperature()) ;
		setSteps(search.getSteps()) ;
		setRuntime(search.getRuntime()) ;
	}

	@Override
  public Search<SolutionType> copy() throws CoreHunterException
  {
	  return new REMCSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>(this);
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

	public final long getMinimumProgressionTime()
  {
  	return minimumProgressionTime;
  }

	public final void setMinimumProgressionTime(long minimumProgressionTime) throws CoreHunterException
  {
		if (this.minimumProgressionTime != minimumProgressionTime)
		{
			this.minimumProgressionTime = minimumProgressionTime;
			
			handleMinimumProgressionTimeSet() ;
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

	public final int getNumberOfReplicas()
  {
  	return numberOfReplicas;
  }

	public final void setNumberOfReplicas(int numberOfReplicas) throws CoreHunterException
  {
		if (this.numberOfReplicas != numberOfReplicas)
		{
			this.numberOfReplicas = numberOfReplicas;
			
			handleNumberOfReplicasSet() ;
		}
  }

	public final double getMinimumTemperature()
  {
  	return minimumTemperature;
  }

	public final void setMinimumTemperature(double minimumTemperature) throws CoreHunterException
  {
		if (this.minimumTemperature != minimumTemperature)
		{
			this.minimumTemperature = minimumTemperature;
			
			handleMinimumTemperatureSet() ;
		}
  }

	public final double getMaximumTemperature()
  {
  	return maximumTemperature;
  }

	public final void setMaximumTemperature(double maximumTemperature) throws CoreHunterException
  {
		if (this.maximumTemperature != maximumTemperature)
		{
			this.maximumTemperature = maximumTemperature;
			
			handleMaximumTemperatureSet() ;
		}
  }

	public final int getSteps()
	{
		return steps;
	}

	public final void setSteps(int steps)
	{
		this.steps = steps;
	}
	
	protected void handleRuntimeSet() throws CoreHunterException
  {
	  if (runtime < 0)
	  	throw new CoreHunterException("Runtime can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Runtime can not be set while search in process") ;
  }
	
	protected void handleMinimumProgressionTimeSet() throws CoreHunterException
  {
	  if (minimumProgressionTime < 0)
	  	throw new CoreHunterException("Minimum Progression Time can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Minimum Progression Time can not be set while search in process") ;
  }
	
	protected void handleStuckTimeSet() throws CoreHunterException
  {
	  if (stuckTime < 0)
	  	throw new CoreHunterException("Stuck Time can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Stuck Time can not be set can not be set while search in process") ;
  }

	protected void handleNumberOfReplicasSet() throws CoreHunterException
  {
	  if (numberOfReplicas < 0)
	  	throw new CoreHunterException("Number Of Replicas can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Number Of Replicas can not be set while search in process") ;
  }
	
	protected void handleMinimumTemperatureSet() throws CoreHunterException
  {
	  if (minimumTemperature < 0)
	  	throw new CoreHunterException("Minimum Temperature can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Minimum Temperature can not be set while search in process") ;
  }
	
	protected void handleMaximumTemperatureSet() throws CoreHunterException
  {
	  if (maximumTemperature < 0)
	  	throw new CoreHunterException("Maximum Temperature can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Maximum Temperature can not be set while search in process") ;
  }
	
	@Override
  protected void validate() throws CoreHunterException
  {
	  super.validate();
	  
	  if (maximumTemperature < minimumTemperature)
	  	throw new CoreHunterException("Maximum Temperature can not be less than Miimum Temperature!") ;
  }

	@SuppressWarnings("unchecked")
  protected void runSearch() throws CoreHunterException
	{
		List<MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>> replicas = new ArrayList<MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>>(numberOfReplicas);

		for (int i = 0; i < numberOfReplicas; i++)
		{
			double temperature = minimumTemperature + i * (maximumTemperature - minimumTemperature) / (numberOfReplicas - 1);
			replicas.add(createMetropolisSearch((SolutionType)getSolution().copy(), getObjectiveFunction(), (NeighbourhoodType)getNeighbourhood().copy(), steps,
			    -1, temperature));
		
		}

		int swapBase = 0;
		continueSearch = true ;
		double previousBestEvaluation = getBestSolutionEvaluation() ;
		int previousBestSubsetSize = getBestSolution().getSubsetSize();
		
		double progression;
		long lastImprovementTime = 0;
		
		// All tasks are done, inspect results
		Iterator<MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>> iterator ;
		
		MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> subSearch ;
		double bestReplicaScore ;
		boolean improvement ;
		MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> bestSubSearch ;

		while (continueSearch &&  getSearchTime() < runtime)
		{
			progression = 0 ;
			bestSubSearch = null ;
			
			startSubSearches(replicas) ;

			// All tasks are done, find best result
			iterator = replicas.iterator() ;
			
			improvement = false ;
			
			while (iterator.hasNext())
			{
				subSearch = iterator.next() ;
				
				if (SearchStatus.COMPLETED.equals(subSearch.getStatus()))
				{
					bestReplicaScore = subSearch.getBestSolutionEvaluation() ;
	
					if (bestReplicaScore > previousBestEvaluation
					    || (previousBestEvaluation == previousBestEvaluation && subSearch.getBestSolution().getSubsetSize() < previousBestSubsetSize))
					{
						lastImprovementTime = getBestSolutionTime() ;
						
						bestSubSearch = subSearch ;
					}
				}
			}

			if (bestSubSearch != null)
			{
				progression = bestSubSearch.getBestSolutionEvaluation() - previousBestEvaluation;

				// check min progression
				if (improvement && bestSubSearch.getBestSolution().getSubsetSize() >= previousBestSubsetSize && progression < minimumProgressionTime)
				{
					continueSearch = false;
				}
				
				previousBestEvaluation = bestSubSearch.getBestSolutionEvaluation() ;
				previousBestSubsetSize = bestSubSearch.getBestSolution().getSubsetSize();
			}
			
			// check stuckTime
			if ((getBestSolutionTime() - lastImprovementTime) > stuckTime)
			{
				continueSearch = false;
			}

			// consider swapping temperatures of adjacent replicas
			for (int i = swapBase; i < numberOfReplicas - 1; i += 2)
			{
				MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> m = replicas.get(i);
				MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> n = replicas.get(i + 1);

				double B_m = 1.0 / (K_b2 * m.getTemperature());
				double B_n = 1.0 / (K_b2 * n.getTemperature());
				double B_diff = B_n - B_m;
				double E_delta = getDeltaScore(m.getBestSolutionEvaluation(), n.getBestSolutionEvaluation()) ;

				boolean swap = false;

				if (E_delta <= 0)
				{
					swap = true;
				}
				else
				{
					double p = getRandom().nextDouble();

					if (Math.exp(B_diff * E_delta) > p)
					{
						swap = true;
					}
				}

				if (swap)
				{
					m.swapTemperature(n);
					MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> temp = replicas.get(i);
					replicas.set(i, replicas.get(i + 1));
					replicas.set(i + 1, temp);
				}
			}

			swapBase = 1 - swapBase;
		}

		fireSearchCompleted();
	}
	
	@Override
  protected void stopSearch() throws CoreHunterException
  {
		continueSearch = false;
  }

	private MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> createMetropolisSearch(
      SolutionType solution, ObjectiveFunction<SolutionType> objectiveFunction,
      NeighbourhoodType neighbourhood, int numberOfSteps, int runtime, double temperature) throws CoreHunterException
  {
		MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> subsearch = 
				new MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>() ;
		
		subsearch.setSolution(solution) ;
		subsearch.setObjectiveFunction(objectiveFunction) ;
		subsearch.setNeighbourhood(neighbourhood) ;
		subsearch.setRuntime(runtime) ;
		subsearch.setNumberOfSteps(numberOfSteps) ;
		
	  return subsearch;
  }
}
