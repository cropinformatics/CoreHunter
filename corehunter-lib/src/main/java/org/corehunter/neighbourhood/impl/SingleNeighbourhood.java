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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.AbstractSubsetNeighbourhood;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.neighbourhood.Move;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.SubsetSolution;

/**
 * @author hermandebeukelaer
 */
public abstract class SingleNeighbourhood< 
	IndexType,
	SolutionType extends SubsetSolution<IndexType>> 
	extends AbstractSubsetNeighbourhood<IndexType, SolutionType>
{
	protected LinkedList<IndexedMove<IndexType, SolutionType>>	history;

	public SingleNeighbourhood()
	{
		history = new LinkedList<IndexedMove<IndexType, SolutionType>>();
	}

	protected SingleNeighbourhood(
			SingleNeighbourhood<IndexType, SolutionType> singleneighbourhood) throws CoreHunterException
  {
		super(singleneighbourhood) ;
		history = new LinkedList<IndexedMove<IndexType, SolutionType>>();
  }
	
	@Override
	public final Move<SolutionType> performBestMove(SolutionType solution,
	    ObjectiveFunction<SolutionType> objectiveFunction, 
	    double currentBestEvaluation, String cacheID)
	{
		return performBestMove(solution, objectiveFunction, null, currentBestEvaluation, cacheID) ;
	}
	
	@Override
  public synchronized boolean undoMove(Move<SolutionType> move, SolutionType solution)
  {
		int i = history.indexOf(move) ;
		
		if (i >= 0)
		{
			ListIterator<IndexedMove<IndexType, SolutionType>> iterator = history.listIterator(i) ;
			
			while (iterator.hasNext())
			{
				iterator.next().undo(solution) ;
				iterator.remove() ;
			}
			
			return true ;
		}
		else
		{
			return false ;
		}
  }

	protected synchronized Move<SolutionType> performBestMove(SolutionType solution, IndexType bestRemoveIndex,
	    IndexType bestAddIndex, List<IndexType> tabuList)
	{
		IndexedMove<IndexType, SolutionType> move;
		
		if (bestAddIndex != null)
		{
			if (bestRemoveIndex == null)
			{
				// only add new element
				solution.addIndex(bestAddIndex) ;
				// create history item
				move = new AdditionMove<IndexType, SolutionType>(bestAddIndex);
			}
			else
			{
				// swap element
				solution.swapIndices(bestAddIndex, bestRemoveIndex) ;
				// create history item
				move = new SwapMove<IndexType, SolutionType>(bestAddIndex, bestRemoveIndex);
			}
		}
		else
		{
			// only remove element
			solution.removeIndex(bestRemoveIndex) ;
			
			// update tabu indices!
			if (tabuList != null)
			{
				tabuList.remove(bestRemoveIndex) ;
			}
			
			// create history item
			move = new DeletionMove<IndexType, SolutionType>(bestRemoveIndex);
		}
		// update history
		addHistoryItem(move);

		return move ;    
	}

	protected synchronized IndexedMove<IndexType, SolutionType> addHistoryItem(IndexedMove<IndexType, SolutionType> move)
	{
		if (history.size() == historySize)
		{
			// history is full, delete oldest item
			history.poll();
		}
		// add new item
		history.offer(move);
		
		return move ;
	}
}
