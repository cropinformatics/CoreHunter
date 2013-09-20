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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.EvaluatedIndexedMove;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.solution.SubsetSolution;

/**
 * Implements a standard neighbourhood which contains all sets that differ in at
 * most one index from the current subset, meaning one index is deleted, added
 * or swapped.
 */
public class ExactSingleNeighbourhood<IndexType, SolutionType extends SubsetSolution<IndexType>>
        extends SingleNeighbourhood<IndexType, SolutionType> {

    public ExactSingleNeighbourhood() {
        super();
    }

    protected ExactSingleNeighbourhood(ExactSingleNeighbourhood<IndexType, SolutionType> randomSingleneighbourhood) throws CoreHunterException {
        super(randomSingleneighbourhood);
    }

    @Override
    public ExactSingleNeighbourhood<IndexType, SolutionType> copy() {
        try {
            return new ExactSingleNeighbourhood<IndexType, SolutionType>(this);
        } catch (CoreHunterException e) {
            return null;
        }
    }

    /**
     * Check all possible moves obtained by adding, removing or swapping one index
     * and apply the best one.
     * 
     * @param solution
     * @param objectiveFunction
     * @param tabuManager
     * @param currentBestEvaluation
     * @return The move that was applied, or null if no non-tabu move was found
     * @throws CoreHunterException 
     */
    
    // TODO need to simplify this once all is working tabu manager should only be in sub-class and used by tubu search, time to use Aspects
    @Override
    public IndexedMove<IndexType, SolutionType> performBestMove(SolutionType solution, ObjectiveFunction<SolutionType> objectiveFunction,
                    IndexedTabuManager<IndexType, SolutionType> tabuManager, double currentBestEvaluation) throws CoreHunterException {

        // search for best neighbour by perturbing solution
        // in all possible ways (deletion, addition or swap)
        
        double bestNeighbourEvaluation = getWorstEvaluation(objectiveFunction.isMinimizing());
        int bestNeighbourSize = getWorstSize();
        EvaluatedIndexedMove<IndexType, SolutionType> bestMove = null;
        
        // get selected and unselected indices (cached copies, see interface SubsetSolution)
        Collection<IndexType> selected = new HashSet<IndexType>(solution.getSubsetIndices());
        Collection<IndexType> unselected = new HashSet<IndexType>(solution.getRemainingIndices());

        /*******************************************************/
        /* Try all deletion moves, if minimum size not reached */
        /*******************************************************/
        
        if (solution.getSubsetSize() > getSubsetMinimumSize())
        {
        	EvaluatedIndexedMove<IndexType, SolutionType> bestDeletionMove = findBestDeletionMove(solution, objectiveFunction, tabuManager, currentBestEvaluation, selected) ;
        	
        	if (isBetterNeighbour(objectiveFunction.isMinimizing(), bestDeletionMove.getEvaluation(), bestNeighbourEvaluation, bestNeighbourSize -1, bestNeighbourSize)) 
        		bestMove = bestDeletionMove ;
        }
        
        /*******************************************************/
        /* Try all addition moves, if maximum size not reached */
        /*******************************************************/
        if (solution.getSubsetSize() > getSubsetMinimumSize())
        {
        	EvaluatedIndexedMove<IndexType, SolutionType> bestAdditionMove = findBestAdditionMove(solution, objectiveFunction, tabuManager, currentBestEvaluation, unselected) ;
        	
        	if (isBetterNeighbour(objectiveFunction.isMinimizing(), bestAdditionMove.getEvaluation(), bestNeighbourEvaluation, bestNeighbourSize +1, bestNeighbourSize)) 
        		bestMove = bestAdditionMove ;
        }
        
        /**********************/
        /* Try all swap moves */
        /**********************/
        EvaluatedIndexedMove<IndexType, SolutionType> bestSwapMove = findBestSwapMove(solution, objectiveFunction, tabuManager, currentBestEvaluation, selected, unselected) ;
        
      	if (isBetterNeighbour(objectiveFunction.isMinimizing(), bestSwapMove.getEvaluation(), bestNeighbourEvaluation, bestNeighbourSize, bestNeighbourSize)) 
      		bestMove = bestSwapMove ;
        
        /***********************/
        /* Apply the best move */
        /***********************/

        // apply best move (if at least one non-tabu neighbour found)
        if(bestMove != null){
            bestMove.apply(solution);
        }
        
        // return best move
        return bestMove;
    }

    /**
     * Check all possible moves obtained by adding, removing or swapping one index
     * and apply the best one.
     * 
     * @param solution
     * @param objectiveFunction
     * @param tabuManager
     * @param currentBestEvaluation
     * @return The move that was applied, or null if no non-tabu move was found
     * @throws CoreHunterException 
     */
    
    // TODO need to simplify this once all is working tabu manager should only be in sub-class and used by tubu search, time to use Aspects
    public EvaluatedIndexedMove<IndexType, SolutionType> findBestDeletionMove(SolutionType solution, ObjectiveFunction<SolutionType> objectiveFunction,
                    IndexedTabuManager<IndexType, SolutionType> tabuManager, double currentBestEvaluation, Collection<IndexType> selected) throws CoreHunterException {

        // search for best neighbour by perturbing solution
        // in all possible ways (deletion, addition or swap)
        
        double bestNeighbourEvaluation = getWorstEvaluation(objectiveFunction.isMinimizing()), neighbourEvaluation;
        int bestNeighbourSize = getWorstSize(), neighbourSize;
        DeletionEvaluatedMove<IndexType, SolutionType> bestMove = null, move;
  
        /*******************************************************/
        /* Try all deletion moves, if minimum size not reached */
        /*******************************************************/
        
        Iterator<IndexType> selectedIterator = selected.iterator();
        IndexType index;
        while (selectedIterator.hasNext()) {
        	index = selectedIterator.next();
        	// create deletion move
        	move = new DeletionEvaluatedMove<IndexType, SolutionType>(index);
        	// apply move
        	move.apply(solution);
        	// compute new score and size
        	neighbourEvaluation = objectiveFunction.calculate(solution);
        	neighbourSize = solution.getSubsetSize();
        	// check score improvement and tabu
        	if (isBetterNeighbour(objectiveFunction.isMinimizing(), neighbourEvaluation, bestNeighbourEvaluation, neighbourSize, bestNeighbourSize)
        			&& (tabuManager == null || tabuManager.moveAllowed(move, neighbourEvaluation, currentBestEvaluation, objectiveFunction.isMinimizing()))) {
        		bestNeighbourEvaluation = neighbourEvaluation;
        		bestNeighbourSize = neighbourSize;
        		bestMove = move;
            bestMove.setEvaluation(bestNeighbourEvaluation);
        	}
        	// undo move
        	move.undo(solution);
        }
       
        // return best move
        return bestMove;
    }
    
    /**
     * Check all possible moves obtained by adding, removing or swapping one index
     * and apply the best one.
     * 
     * @param solution
     * @param objectiveFunction
     * @param tabuManager
     * @param currentBestEvaluation
     * @return The move that was applied, or null if no non-tabu move was found
     * @throws CoreHunterException 
     */
    
    // TODO need to simplify this once all is working tabu manager should only be in sub-class and used by tubu search, time to use Aspects
    public EvaluatedIndexedMove<IndexType, SolutionType> findBestAdditionMove(SolutionType solution, ObjectiveFunction<SolutionType> objectiveFunction,
                    IndexedTabuManager<IndexType, SolutionType> tabuManager, double currentBestEvaluation, Collection<IndexType> unselected) throws CoreHunterException {

        // search for best neighbour by perturbing solution
        // in all possible ways (deletion, addition or swap)
        
        double bestNeighbourEvaluation = getWorstEvaluation(objectiveFunction.isMinimizing()), neighbourEvaluation;
        int bestNeighbourSize = getWorstSize(), neighbourSize;
        AdditionEvaluatedMove<IndexType, SolutionType> bestMove = null, move;
        
        /*******************************************************/
        /* Try all addition moves, if maximum size not reached */
        /*******************************************************/
        
        Iterator<IndexType> unselectedIterator = unselected.iterator();
        IndexType index;
        while (unselectedIterator.hasNext()) {
        	index = unselectedIterator.next();
        	// create addition move
        	move = new AdditionEvaluatedMove<IndexType, SolutionType>(index);
        	// apply move
        	move.apply(solution);
        	// compute new score and size
        	neighbourEvaluation = objectiveFunction.calculate(solution);
        	neighbourSize = solution.getSubsetSize();
        	// check score improvement and tabu
        	if (isBetterNeighbour(objectiveFunction.isMinimizing(), neighbourEvaluation, bestNeighbourEvaluation, neighbourSize, bestNeighbourSize)
        			&& (tabuManager == null || tabuManager.moveAllowed(move, neighbourEvaluation, currentBestEvaluation, objectiveFunction.isMinimizing()))) {
        		bestNeighbourEvaluation = neighbourEvaluation;
        		bestNeighbourSize = neighbourSize;
        		bestMove = move;
            bestMove.setEvaluation(bestNeighbourEvaluation);
        	}
        	// undo move
        	move.undo(solution);
        }
       
        // return best move
        return bestMove;
    }
    
    /**
     * Check all possible moves obtained by swapping one index
     * and apply the best one.
     * 
     * @param solution
     * @param objectiveFunction
     * @param tabuManager
     * @param currentBestEvaluation
     * @return The move that was applied, or null if no non-tabu move was found
     * @throws CoreHunterException 
     */
    // TODO need to simplify this once all is working tabu manager should only be in sub-class and used by tubu search, time to use Aspects
    public EvaluatedIndexedMove<IndexType, SolutionType> findBestSwapMove(SolutionType solution, ObjectiveFunction<SolutionType> objectiveFunction,
                    IndexedTabuManager<IndexType, SolutionType> tabuManager, double currentBestEvaluation, Collection<IndexType> selected, Collection<IndexType> unselected) throws CoreHunterException {

        // search for best neighbour by perturbing solution
        // in all possible ways (deletion, addition or swap)
        
        double bestNeighbourEvaluation = getWorstEvaluation(objectiveFunction.isMinimizing()), neighbourEvaluation;
        int bestNeighbourSize = getWorstSize(), neighbourSize;
        SwapEvaluatedMove<IndexType, SolutionType> bestMove = null, move;
        
        
        /**********************/
        /* Try all swap moves */
        /**********************/

        IndexType indexToAdd, indexToRemove;
        Iterator<IndexType> unselectedIterator = unselected.iterator();
        Iterator<IndexType> selectedIterator; // used in inner loop

        // go through possible indices to add
        while (unselectedIterator.hasNext()) {
            // index to add
            indexToAdd = unselectedIterator.next();
            // now go through all possible indices to remove
            selectedIterator = selected.iterator();
            while (selectedIterator.hasNext()) {
                // index to remove
                indexToRemove = selectedIterator.next();
                // create swap move
                move = new SwapEvaluatedMove<IndexType, SolutionType>(indexToAdd, indexToRemove);
                // apply move
                move.apply(solution);
                // compute new score and size
                neighbourEvaluation = objectiveFunction.calculate(solution);
                neighbourSize = solution.getSubsetSize();
                // check score improvement and tabu
                if (isBetterNeighbour(objectiveFunction.isMinimizing(), neighbourEvaluation, bestNeighbourEvaluation, neighbourSize, bestNeighbourSize)
                        && (tabuManager == null || tabuManager.moveAllowed(move, neighbourEvaluation, currentBestEvaluation, objectiveFunction.isMinimizing()))) {
                    bestNeighbourEvaluation = neighbourEvaluation;
                    bestNeighbourSize = neighbourSize;
                    bestMove = move;
                    bestMove.setEvaluation(bestNeighbourEvaluation);
                }
                // undo swap
                move.undo(solution);
            }
        }
        
        // return best move
        return bestMove;
    }
    
}
