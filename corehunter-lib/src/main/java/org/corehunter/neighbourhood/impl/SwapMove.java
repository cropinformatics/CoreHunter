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

package org.corehunter.neighbourhood.impl;

import org.corehunter.neighbourhood.AddedIndexMove;
import org.corehunter.neighbourhood.RemovedIndexMove;
import org.corehunter.search.solution.SubsetSolution;

/**
 * @author hermandebeukelaer
 */
public class SwapMove<
	IndexType, 
	SolutionType extends SubsetSolution<IndexType>> 
	implements AddedIndexMove<IndexType, SolutionType>, RemovedIndexMove<IndexType, SolutionType>
{
	private IndexType	addedIndex;
	private IndexType	removedIndex;
	
	public SwapMove(IndexType addedIndex, IndexType removedIndex)
	{
		this.addedIndex = addedIndex;
		this.removedIndex = removedIndex;
	}

	public final IndexType getAddedIndex()
  {
  	return addedIndex;
  }
	
	@Override
	public final IndexType getRemovedIndex()
  {
  	return removedIndex;
  }
	
	@Override
	public void undo(SolutionType solution)
	{
		// To undo a swap: swap again!
		solution.swapIndices(removedIndex, addedIndex) ;
		// TODO this should update the tabu list internally!
	}
	
	@SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object object)
  {
		if (object instanceof AddedIndexMove && object instanceof RemovedIndexMove)
			return getAddedIndex().equals(((AddedIndexMove) object).getAddedIndex()) &&
					getRemovedIndex().equals(((RemovedIndexMove) object).getRemovedIndex()) ;
		else
			return super.equals(object);
  }

	@Override
  public String toString()
  {
	  return "Swap index=" + removedIndex + " with index=" + addedIndex ;
  }

}
