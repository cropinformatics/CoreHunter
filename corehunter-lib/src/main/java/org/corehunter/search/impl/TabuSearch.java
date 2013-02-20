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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.LinkedList;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.neighbourhood.AddedIndexMove;
import org.corehunter.neighbourhood.Move;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.SubsetSolution;

/**
 * TABU Search. Tabu list is a list of indices at which the current core set
 * cannot be perturbed (delete, swap) to form a new core set as long as the
 * index is contained in the tabu list. After each perturbation step, the index
 * of the newly added accession (if it exists) is added to the tabu list, to
 * ensure this accesion is not again removed from the core set (or replaced)
 * during the next few rounds. If no new accession was added (pure deletion), a
 * value "-1" is added to the tabu list. As long as such values are contained in
 * the tabu list, adding a new accesion without removing one (pure addition) is
 * considered tabu, to prevent immediately re-adding the accession which was
 * removed in the previous step.
 */
public class TabuSearch<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>, 
	DatasetType extends IndexedData<IndexType>,
	NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
	extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>
{
	private long	              runtime;
	private double	            minimumProgression;
	private long	              stuckTime;
	private int	                tabuListSize;
	private boolean 						continueSearch ;
	
	public TabuSearch()
	{
		super();
	}
	
	protected TabuSearch(TabuSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search) throws CoreHunterException
	{
		super(search);
		
		setRuntime(search.getRuntime()) ;
		setMinimumProgression(search.getMinimumProgression()) ;
		setStuckTime(search.getStuckTime()) ;
	}

	@Override
  public Search<SolutionType> copy() throws CoreHunterException
  {
	  return new TabuSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>(this);
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
	
	protected void handleRuntimeSet() throws CoreHunterException
  {
	  if (runtime < 0)
	  	throw new CoreHunterException("Runtime can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Runtime can not be set while search in process") ;
  }
	
	protected void handleMinimumProgressionTimeSet() throws CoreHunterException
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

	protected void handleTabuListSizeSet() throws CoreHunterException
  {
	  if (tabuListSize < 0)
	  	throw new CoreHunterException("Tabu List Size can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Tabu List Size can not be set while search in process") ;
  }
	
	
	
	@SuppressWarnings("unchecked")
  @Override
	protected void runSearch() throws CoreHunterException
	{
		double evalution ;
		int size, newSize;

		LinkedList<IndexType> tabuList;
		
		size = getSolution().getSubsetSize() ;
		
		// initialize tabu list
		tabuList = new LinkedList<IndexType>();

		Move<SolutionType> move;
		continueSearch = true;

		setEvaluation(getObjectiveFunction().calculate(getSolution()));
		handleNewBestSolution(getSolution(), getEvaluation());

		while (continueSearch && getSearchTime() < runtime)
		{
			// run TABU search step

			// ALWAYS accept new core, even it is not an improvement
			move = getNeighbourhood().performBestMove(getSolution(), getObjectiveFunction(), tabuList, getBestSolutionEvaluation());
			evalution = getObjectiveFunction().calculate(getSolution());

			// check if new best core was found
			if (evalution > getBestSolutionEvaluation()
			    || (evalution == getBestSolutionEvaluation() && getSolution().getSubsetSize() < getBestSolution().getSubsetSize()))
			{
				// check min progression
				if (getSolution().getSubsetSize() >= getBestSolution().getSubsetSize() && evalution - getBestSolutionEvaluation() < minimumProgression)
				{
					continueSearch = false;
				}
				
				// store new best core
				handleNewBestSolution(getSolution(), evalution);
			}
			else
			{
				// check stuckTime
				continueSearch = continueSearch && stuckTime > getBestSolutionTime() ;
			}

			// finally, update tabu list
			if (tabuList.size() == tabuListSize)
			{
				// capacity reached, remove oldest tabu index
				tabuList.poll();
			}
			
			// add new tabu index
			if (move instanceof AddedIndexMove)
				tabuList.offer(((AddedIndexMove<IndexType, SolutionType>)move).getAddedIndex());
			else
				tabuList.offer(null) ;
		}
	}
	
	@Override
  protected void stopSearch() throws CoreHunterException
  {
		continueSearch = false;
  }
}
