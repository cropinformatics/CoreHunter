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

package org.corehunter.search.impl;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.neighbourhood.Move;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.SubsetSolution;

/**
 * * Steepest Descent search. Always continue with the best of all neighbours, if
 * it is better than the current core set, and stop search if no improvement can
 * be made. This is also called an "iterative improvement" strategy.
 */
public class SteepestDescentSearch<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>, 
	DatasetType extends IndexedData<IndexType>,
	NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>> 
	extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>
{
	private long	              runtime;
	private double	            minimumProgression;
	private boolean 						continueSearch ;

	public SteepestDescentSearch()
	{
		super();
	}
	
	protected SteepestDescentSearch(SteepestDescentSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search) throws CoreHunterException
	{
		super(search);
		
		setRuntime(search.getRuntime()) ;
		setMinimumProgression(search.getMinimumProgression()) ;
	}

	@Override
  public Search<SolutionType> copy() throws CoreHunterException
  {
	  return new SteepestDescentSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>(this);
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
	  	throw new CoreHunterException("Minimum Progression Time can not be less than zero!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Minimum Progression Time can not be set while search in process") ;
  }
	
	@Override
	protected void runSearch() throws CoreHunterException
	{
		double evalution, newScore;
		int size, newSize;

		size = getSolution().getSubsetSize() ;

		evalution = getObjectiveFunction().calculate(getSolution());
		size = getSolution().getSubsetSize() ;

		handleNewBestSolution(getSolution(), evalution);

		Move<SolutionType> move ;
		
		continueSearch = true;
		
		while (continueSearch)
		{
			// run Steepest Descent search step
			move = getNeighbourhood().performBestMove(getSolution(), getObjectiveFunction(), evalution);
			newScore = getObjectiveFunction().calculate(getSolution());
			newSize = getSolution().getSubsetSize() ;

			if (newScore > evalution || (newScore == evalution && newSize < size))
			{
				// check minimum progression
				if (newSize >= size && newScore - evalution < minimumProgression)
				{
					continueSearch = false;
				}
				
				// accept new core!
				evalution = newScore;
				size = newSize;
				// continue if time left
				continueSearch = continueSearch && getSearchTime() < runtime;

				handleNewBestSolution(getSolution(), newScore);
			}
			else
			{
				// Don't accept new core
				getNeighbourhood().undoMove(move, getSolution()) ;
				// All neighbours are worse than current core, so stop search
				continueSearch = false;
			}
		}
	}

	@Override
  protected void stopSearch() throws CoreHunterException
  {
		continueSearch = false;
  }
}
