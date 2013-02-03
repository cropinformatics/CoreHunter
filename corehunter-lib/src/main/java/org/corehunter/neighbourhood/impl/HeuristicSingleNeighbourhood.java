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
import java.util.ListIterator;

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.Move;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.SubsetSolution;

/**
 * @author hermandebeukelaer
 */
public class HeuristicSingleNeighbourhood<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>> 
	extends SingleNeighbourhood<IndexType, SolutionType>
{

	public HeuristicSingleNeighbourhood() throws CoreHunterException
  {

  }
	
	public HeuristicSingleNeighbourhood(
      HeuristicSingleNeighbourhood<IndexType, SolutionType> heuristicSingleneighbourhood) throws CoreHunterException
  {
	  super(heuristicSingleneighbourhood) ;
  }

	@Override
	public HeuristicSingleNeighbourhood<IndexType, SolutionType> copy()
	{
		try
    {
	    return new HeuristicSingleNeighbourhood<IndexType, SolutionType>(this);
    }
    catch (Exception e)
    {
	    return null ;
    }
	}

	@Override
  public Move<SolutionType> performBestMove(SolutionType solution,
      ObjectiveFunction<SolutionType> objectiveFunction, List<IndexType> tabuList, 
      double currentBestEvaluation, String cacheID)
  {
		// search for a good neighbour by perturbing core using the following
		// heuristic:
		// - investigate all possible additions of one accession to the core and
		// choose this one with the highest score for the new set of accessions
		// - remove one old accession from the core, which gives rise to the
		// highest score of the new core
		// remark: only adding or deleting an item is also possible if core size
		// limits not reached

		IndexType bestRemIndex = null;
    IndexType bestAddIndex = null;
    
    try
    {
	    double bestScore = objectiveFunction.isMinimizing() ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY ;
	    double score;

	    // first add best, then delete worst

	    if (solution.getSubsetSize() > getSubsetMinimumSize())
	    {
	    	// pure deletion is possible - include option without addition
	    	bestScore = objectiveFunction.calculate(solution, cacheID);
	    }

	    ArrayList<IndexType> unselected = new ArrayList<IndexType>(solution.getRemainingIndices()) ;
	    Iterator<IndexType> iterator = unselected.iterator() ;
	    
	    IndexType index ;
	    
	    // try adding each accession from unselected
	    while (iterator.hasNext())
	    {
	    	index = iterator.next() ;
	    	
	    	solution.addIndex(index) ;
	    	score = objectiveFunction.calculate(solution, cacheID);
	    	
	    	if (score > bestScore)
	    	{
	    		bestScore = score;
	    		bestAddIndex = index ;
	    	}
	    	
	    	solution.removeIndex(index) ;
	    } 

	    // best addition has been determined, reset best score
	    bestScore = objectiveFunction.isMinimizing() ? Double.POSITIVE_INFINITY: Double.NEGATIVE_INFINITY ;

	    // now determine best removal
	    if (bestAddIndex == null)
	    {
	    	ArrayList<IndexType> selected = new ArrayList<IndexType>(solution.getSubsetIndices()) ;
	    	
	    	ListIterator<IndexType> listIterator = selected.listIterator(selected.size());
	    	
	    	while (listIterator.hasPrevious())
	    	{
	    		index = listIterator.previous() ;
	    		
	    		solution.removeIndex(index) ;
	    		score = objectiveFunction.calculate(solution, cacheID);
	    		
	    		if (score > bestScore
	    		    && (tabuList == null || !tabuList.contains(index) || score - currentBestEvaluation > MIN_TABU_ASPIRATION_PROG))
	    		{
	    			bestScore = score;
	    			bestRemIndex = index ;
	    		}
	    		
	    		solution.addIndex(index) ;
	    	}
	    }
	    else
	    {

	    	// new accession will be added, now two options remain:
	    	// - only add this new accession (pure addition)
	    	// - swap (remove one old accession)
	    	// --> choose best

	    	// try all possible non-tabu swaps

	    	ArrayList<IndexType> selected = new ArrayList<IndexType>(solution.getSubsetIndices()) ;
	    	
	    	iterator = unselected.iterator();
	    	
	    	while (iterator.hasNext())
	    	{
	    		index = iterator.next() ;
	    		
	    		solution.swapIndices(bestAddIndex, index) ;
	    		score = objectiveFunction.calculate(solution, cacheID);
	    		
	    		if (score > bestScore
	    		    && (tabuList == null || !tabuList.contains(index) || score - currentBestEvaluation > MIN_TABU_ASPIRATION_PROG))
	    		{
	    			bestScore = score;
	    			bestRemIndex = index ;
	    		}
	    		
	    		solution.swapIndices(index, bestAddIndex) ;
	    	}

	    	// try pure addition if possible and not tabu
	    	if (solution.getSubsetSize() < getSubsetMaximumSize())
	    	{
	    		solution.addIndex(bestAddIndex);
	    		score = objectiveFunction.calculate(solution, cacheID);
	    		if (score > bestScore
	    		    && (tabuList == null || !tabuList.contains(-1) || score - currentBestEvaluation > MIN_TABU_ASPIRATION_PROG))
	    		{
	    			bestScore = score;
	    			bestRemIndex = null;
	    		}
	    		solution.removeIndex(bestAddIndex);
	    	}

	    }
    }
    catch (CoreHunterException e)
    {
	    // should not happen
	    e.printStackTrace();
    }

		// peturb core into 'best' neighbour found by heuristic
		return performBestMove(solution, bestRemIndex,
		    bestAddIndex, tabuList);
	}

	@Override
	public Move<SolutionType> performRandomMove(SolutionType solution)
	{
		throw new UnsupportedOperationException(
		    "The HeuristicSingleneighbourhood can not be use to generate random neighbours. "
		        + "It is especially designed as a heuristic to generate one of the 'best' "
		        + "neighbours, without investigating them all. To generate random neighbours "
		        + "obtained by a single perturbation, please use the RandomSingleneighbourhood.");
	}
}
