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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.neighbourhood.Move;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.solution.SubsetSolution;

/**
 * Implements a standard neighbourhood which contains all sets that differ in at
 * most one accession from the current core set, meaning one accession will be
 * deleted, added or swapped at random.
 * 
 * @author hermandebeukelaer
 */
public class RandomSingleNeighbourhood<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>> 
	extends SingleNeighbourhood<IndexType, SolutionType>
{
	public RandomSingleNeighbourhood() throws CoreHunterException
  {
	  super() ;
  }
	
	protected RandomSingleNeighbourhood(
      RandomSingleNeighbourhood<IndexType, SolutionType> randomSingleneighbourhood) throws CoreHunterException
  {
	  super(randomSingleneighbourhood) ;
  }

	@Override
	public RandomSingleNeighbourhood<IndexType, SolutionType> copy()
	{
		try
    {
	    return new RandomSingleNeighbourhood<IndexType, SolutionType>(this);
    }
    catch (CoreHunterException e)
    {
	    return null ;
    }
	}

	@Override
  public Move<SolutionType> performBestMove(SolutionType solution,
      ObjectiveFunction<SolutionType> objectiveFunction, List<IndexType> tabuList,
      double currentBestEvaluation)
	{

		// search for (one of the) best neighbour(s) by perturbing core
		// in all possible ways (remove 1, add 1, swap 1)
		IndexType bestAddIndex = null;
		IndexType bestRemoveIndex = null;
		double bestScore = getWorstScore(objectiveFunction.isMinimizing()) ; 
		double score = bestScore ;

		// try removing one, if min size not reached
		if (solution.getSubsetSize() > getSubsetMinimumSize())
		{	
			Iterator<IndexType> selected = solution.getSubsetIndices().iterator() ;
			IndexType index ;
			
			try
      {
	      while (selected.hasNext())
	      {
	      	index = selected.next() ;
	      	solution.removeIndex(index) ;
	      	score = objectiveFunction.calculate(solution);
	      	
	      	// ensure index is not tabu
	      	if (isBetterScore(objectiveFunction.isMinimizing(), score, bestScore) 
	      				&& (tabuList == null || !tabuList.contains(index) || score - currentBestEvaluation > MIN_TABU_ASPIRATION_PROG))
	      	{
	      		bestScore = score;
	      		bestAddIndex = null ;  // do not add anything
	      		bestRemoveIndex = index ; // remove element 
	      	}
	      	
	      	solution.addIndex(index);
	      }
      }
      catch (CoreHunterException e)
      {
	      // TODO should not happen!
	      e.printStackTrace();
      }
		}
		
		List<IndexType> selected = new ArrayList<IndexType>(solution.getSubsetIndices()) ;
		List<IndexType> unselected = new ArrayList<IndexType>(solution.getRemainingIndices()) ;
		IndexType indexToAdd ;
		IndexType indexToRemove ;
		Iterator<IndexType> selectedIterator ;
		Iterator<IndexType> unselectedIterator = unselected.iterator() ;
		
		// try all possible swaps: remove 1 AND add 1 to replace it
		while (unselectedIterator.hasNext())
		{
			// accession to add
			indexToAdd = unselectedIterator.next() ;
			// loop over all possible elements and try replacing them with
			// new element
			
			selectedIterator = selected.iterator() ;
			
			// try all possible swaps: remove 1 AND add 1 to replace it
			while (selectedIterator.hasNext())
			{
				indexToRemove = selectedIterator.next() ;
				
				// replace index with new index
				solution.swapIndices(indexToAdd, indexToRemove) ;
				
				// ensure index is not tabu
				if (isBetterScore(objectiveFunction.isMinimizing(), score, bestScore) 
				    && (tabuList == null || !tabuList.contains(indexToAdd) || score - currentBestEvaluation > MIN_TABU_ASPIRATION_PROG))
				{
					bestScore = score;
					bestAddIndex = indexToAdd; // add element

					bestRemoveIndex = indexToRemove; // remove element
				}
				// undo swap
				solution.swapIndices(indexToRemove, indexToAdd) ;
			}
		}
		// try adding one, if max size not reached and not restricted by tabu
		// list
		// (if a pure deletion occurred in the scope of the tabu list, an pure a
		// addition
		// is not allowed to prevent going back to the previous solution!)
		if (solution.getSubsetSize() < getSubsetMaximumSize())
		{
			unselectedIterator = unselected.iterator() ;
			IndexType index ;
			
			try
      {
	      while (unselectedIterator.hasNext())
	      {
	      	index = unselectedIterator.next() ;
	      	solution.addIndex(index) ;
	      	score = objectiveFunction.calculate(solution);
	      	
	      	// ensure index is not tabu
	      	if (isBetterScore(objectiveFunction.isMinimizing(), score, bestScore) 
	      				&& (tabuList == null || !tabuList.contains(index) || score - currentBestEvaluation > MIN_TABU_ASPIRATION_PROG))
	      	{
	      		bestScore = score;
	      		bestAddIndex = index ;  // add element
	      		bestRemoveIndex = null ; // do not remove anything
	      	}
	      	
	      	solution.removeIndex(index);
	      }
      }
      catch (CoreHunterException e)
      {
	      // TODO should not happen
	      e.printStackTrace();
      }
		}

		// perform best perturbation on core and return this as the new core
		// also update unselected list and/or tabu list
		return performBestMove(solution, bestRemoveIndex,
		    bestAddIndex, tabuList);

	}

	@Override
	public Move<SolutionType> performRandomMove(SolutionType solution)
	{
		// randomly perturb core elements
		if (solution.getRemainingSize() == 0)
		{
			// core currently contains ALL accessions, only remove possible
			return removeRandom(solution);
		}
		else
		{
			double p = getRandom().nextDouble();
			if (p >= 0.66 && solution.getSubsetSize() < getSubsetMaximumSize())
			{
				return addRandom(solution);
			}
			else
				if (p >= 0.33 && solution.getSubsetSize() > getSubsetMinimumSize())
				{
					return removeRandom(solution);
				}
				else
				{
					return swapRandom(solution);
				}
		}
	}

	private IndexedMove<IndexType, SolutionType> swapRandom(SolutionType solution)
	{
		// randomly swap one item
		IndexType[] indices = solution.swapRandomIndices(getRandom()) ;
		
		return addHistoryItem(new SwapMove<IndexType, SolutionType>(indices[0], indices[1]));
	}

	private IndexedMove<IndexType, SolutionType>  addRandom(SolutionType solution)
	{
		// randomly add one item
		IndexType addIndex = solution.addRandomIndex(getRandom()) ;
		
		// update history
		return addHistoryItem(new AdditionMove<IndexType, SolutionType>(addIndex));
	}

	private IndexedMove<IndexType, SolutionType>  removeRandom(SolutionType solution)
	{
		// randomly remove one item
		IndexType removeIndex = solution.removeRandomIndex(getRandom()) ;
		
		return addHistoryItem(new DeletionMove<IndexType, SolutionType>(removeIndex));
	}
}
