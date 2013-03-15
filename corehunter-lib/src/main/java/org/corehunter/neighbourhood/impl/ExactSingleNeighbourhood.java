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

    public ExactSingleNeighbourhood() throws CoreHunterException {
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
    @Override
    public IndexedMove<IndexType, SolutionType> performBestMove(SolutionType solution, ObjectiveFunction<SolutionType> objectiveFunction,
                    IndexedTabuManager<IndexType> tabuManager, double currentBestEvaluation) throws CoreHunterException {

        // search for best neighbour by perturbing solution
        // in all possible ways (deletion, addition or swap)
        
        double bestNeighbourEvaluation = getWorstEvaluation(objectiveFunction.isMinimizing()), neighbourEvaluation;
        int bestNeighbourSize = getWorstSize(), neighbourSize;
        IndexedMove<IndexType, SolutionType> bestMove = null, move;
        
        // get selected and unselected indices (cached copies, see interface SubsetSolution)
        Collection<IndexType> selected = new HashSet<IndexType>(solution.getSubsetIndices());
        Collection<IndexType> unselected = new HashSet<IndexType>(solution.getRemainingIndices());

        /*******************************************************/
        /* Try all deletion moves, if minimum size not reached */
        /*******************************************************/
        
        if (solution.getSubsetSize() > getSubsetMinimumSize()) {
            Iterator<IndexType> selectedIterator = selected.iterator();
            IndexType index;
            while (selectedIterator.hasNext()) {
                index = selectedIterator.next();
                // create deletion move
                move = new DeletionMove<IndexType, SolutionType>(index);
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
                }
                // undo move
                move.undo(solution);
            }
        }
        
        /*******************************************************/
        /* Try all addition moves, if maximum size not reached */
        /*******************************************************/
        
        if (solution.getSubsetSize() < getSubsetMaximumSize()) {
            Iterator<IndexType> unselectedIterator = unselected.iterator();
            IndexType index;
            while (unselectedIterator.hasNext()) {
                index = unselectedIterator.next();
                // create addition move
                move = new AdditionMove<IndexType, SolutionType>(index);
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
                }
                // undo move
                move.undo(solution);
            }
        }
        
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
                move = new SwapMove<IndexType, SolutionType>(indexToAdd, indexToRemove);
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
                }
                // undo swap
                move.undo(solution);
            }
        }
        
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

}
