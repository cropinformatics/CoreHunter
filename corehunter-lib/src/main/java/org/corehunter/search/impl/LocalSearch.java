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

import static org.corehunter.Constants.INVALID_TIME;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.neighbourhood.Move;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.SubsetSolution;

public class LocalSearch<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>, 
	DatasetType extends IndexedData<IndexType>,
	NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>> 
	extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>
{
	private long runtime = INVALID_TIME ;
	private long minimumProgressionTime = INVALID_TIME ;
	private long stuckTime = INVALID_TIME ;
	
	private boolean continueSearch ;

	public LocalSearch()
	{
		super();
	}
	
	protected LocalSearch(LocalSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search) throws CoreHunterException
	{
		super(search);
		
		setRuntime(search.getRuntime()) ;
		setMinimumProgressionTime(search.getMinimumProgressionTime()) ;
		setStuckTime(search.getStuckTime()) ;
	}

	@Override
  public Search<SolutionType> copy() throws CoreHunterException
  {
	  return new LocalSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>(this);
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

	@Override
  protected void validate() throws CoreHunterException
  {
	  super.validate();
	  
	  if (runtime <= 0)
	  	throw new CoreHunterException("Runtime can not be less than or equal to zero!") ;
	  
	  if (minimumProgressionTime <= 0)
	  	throw new CoreHunterException("Minimum Progression Time can not be less or equal to  than zero!") ;
	  
	  if (stuckTime <= 0)
	  	throw new CoreHunterException("Stuck Time can not be less than or equal to zero!") ;
  }

	protected void handleRuntimeSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Runtime can not be set while search in process") ;
		
	  if (runtime <= 0)
	  	throw new CoreHunterException("Runtime can not be less than or equal to zero!") ;
  }
	
	protected void handleMinimumProgressionTimeSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Minimum Progression Time can not be set while search in process") ;
		
	  if (minimumProgressionTime <= 0)
	  	throw new CoreHunterException("Minimum Progression Time can not be less or equal to  than zero!") ;
  }
	
	protected void handleStuckTimeSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Stuck Time can not be set can not be set while search in process") ;
		
	  if (stuckTime <= 0)
	  	throw new CoreHunterException("Stuck Time can not be less than or equal to zero!") ;
  }
	
	@Override
	protected void runSearch() throws CoreHunterException
	{
		double score, newScore;
		int size, newSize;

		size = getSolution().getSubsetSize() ;
		
		score = getObjectiveFunction().calculate(getSolution(), getCacheIdentifier());
		size = getSolution().getSubsetSize() ;

		continueSearch = true;
		long lastImprTime = 0;

		handleNewBestSolution(getSolution(), score);
		
		Move<SolutionType> move ;

		while (continueSearch && getSearchTime() < runtime)
		{
			// run Local Search step
			move = getNeighbourhood().performRandomMove(getSolution());
			newScore = getObjectiveFunction().calculate(getSolution(), getCacheIdentifier());
			newSize = getSolution().getSubsetSize();

			if (newScore > score || (newScore == score && newSize < size))
			{
				// check min progression
				if (newSize >= size && newScore - score < minimumProgressionTime)
				{
					continueSearch = false;
				}
				
				// accept new core!
				score = newScore;
				size = newSize;

				handleNewBestSolution(getSolution(), newScore);
				
				lastImprTime = getBestSolutionTime() ;
			}
			else
			{
				// Reject new core
				getNeighbourhood().undoMove(move, getSolution());
				// check stuckTime
				if (getSearchTime() - lastImprTime > stuckTime)
				{
					continueSearch = false;
				}
			}
		}
	}

	@Override
  protected void stopSearch() throws CoreHunterException
  {
		continueSearch = false;
  }
}
