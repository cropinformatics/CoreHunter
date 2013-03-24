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
 * Applied the MSTRAT heuristic to perform a heuristically "best" move. This
 * heuristic first looks for the best addition and then looks for the best
 * deletion, instead of considering all possible swaps. This reduces the
 * number of considered neighbours from O(n^2) to O(n), where n is the total
 * number of possible indices to select.
 */
public class MSTRATHeuristicSingleNeighbourhood<IndexType, SolutionType extends SubsetSolution<IndexType>>
        extends SingleNeighbourhood<IndexType, SolutionType> {

    public MSTRATHeuristicSingleNeighbourhood() throws CoreHunterException {
    }

    public MSTRATHeuristicSingleNeighbourhood(MSTRATHeuristicSingleNeighbourhood<IndexType, SolutionType> heuristicSingleneighbourhood) throws CoreHunterException {
        super(heuristicSingleneighbourhood);
    }

    @Override
    public MSTRATHeuristicSingleNeighbourhood<IndexType, SolutionType> copy() {
        try {
            return new MSTRATHeuristicSingleNeighbourhood<IndexType, SolutionType>(this);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public IndexedMove<IndexType, SolutionType> performBestMove(
            SolutionType solution,
            ObjectiveFunction<SolutionType> objectiveFunction,
            IndexedTabuManager<IndexType> tabuManager,
            double currentBestEvaluation) throws CoreHunterException {

        // search for "best" neighbour by applying the MSTRAT heuristic
        
        double neighbourEvaluation;
        int neighbourSize;
        Iterator<IndexType> iterator;
        IndexType index;

        Collection<IndexType> unselected = new HashSet<IndexType>(solution.getRemainingIndices());
        Collection<IndexType> selected = new HashSet<IndexType>(solution.getSubsetIndices());
        
        /**************************/
        /* Look for best addition */
        /**************************/

        double bestNeighbourEvaluation = getWorstEvaluation(objectiveFunction.isMinimizing());
        int bestNeighbourSize = getWorstSize();
        AdditionMove<IndexType, SolutionType> bestAdditionMove = null;
        
        // if a pure deletion move is possible, we do not necessarily have to add
        // an index first so in this case the option without an addition is also considered
        if (solution.getSubsetSize() > getSubsetMinimumSize()) {
            bestNeighbourEvaluation = objectiveFunction.calculate(solution);
            bestNeighbourSize = solution.getSubsetSize();
        }

        // evaluate each addition move
        iterator = unselected.iterator();
        AdditionMove<IndexType, SolutionType> additionMove;
        while (iterator.hasNext()) {
            // index to add
            index = iterator.next();
            // create addition move
            additionMove = new AdditionMove<IndexType, SolutionType>(index);
            // apply move
            additionMove.apply(solution);
            // compute new score and size
            neighbourEvaluation = objectiveFunction.calculate(solution);
            neighbourSize = solution.getSubsetSize();
            // check score improvement and tabu
            if (isBetterNeighbour(objectiveFunction.isMinimizing(), neighbourEvaluation, bestNeighbourEvaluation, neighbourSize, bestNeighbourSize)
                    && (tabuManager == null || tabuManager.moveAllowed(additionMove, neighbourEvaluation, currentBestEvaluation, objectiveFunction.isMinimizing()))) {
                bestNeighbourEvaluation = neighbourEvaluation;
                bestNeighbourSize = neighbourSize;
                bestAdditionMove = additionMove;
            }
            // undo move
            additionMove.undo(solution);
        }
        
        // apply best addition move (if not null)
        if(bestAdditionMove != null){
            bestAdditionMove.apply(solution);
        }

        /****************************************/
        /* Now look for best deletion to follow */
        /****************************************/
        
        // best addition has been determined, reset best evaluation and size
        bestNeighbourEvaluation = getWorstEvaluation(objectiveFunction.isMinimizing());
        bestNeighbourSize = getWorstSize();
        DeletionMove<IndexType, SolutionType> bestDeletionMove = null;

        if(bestAdditionMove != null && solution.getSubsetSize() <= getSubsetMaximumSize()){
            // an index was added and we did not exceed the maximum size, so it is not
            // necessarily required to delete something now as well (consider pure addition)
            bestNeighbourEvaluation = objectiveFunction.calculate(solution);
            bestNeighbourSize = solution.getSubsetSize();
        }
        
        // evaluate each deletion move
        if(solution.getSubsetSize() > getSubsetMinimumSize()){
            // iterate over indices that were selected in the ORIGINAL solution,
            // before possibly having added a new index in the first stage, as it
            // is not allowed to again remove this added index because this would
            // result in the original solution (NOT included in the neighbourhood)
            iterator = selected.iterator();
            DeletionMove<IndexType, SolutionType> deletionMove;
            while (iterator.hasNext()) {
                // index to delete
                index = iterator.next();
                // create deletion move
                deletionMove = new DeletionMove<IndexType, SolutionType>(index);
                // apply move
                deletionMove.apply(solution);
                // compute new score and size
                neighbourEvaluation = objectiveFunction.calculate(solution);
                neighbourSize = solution.getSubsetSize();
                // check score improvement and tabu
                if (isBetterNeighbour(objectiveFunction.isMinimizing(), neighbourEvaluation, bestNeighbourEvaluation, neighbourSize, bestNeighbourSize)
                        && (tabuManager == null || tabuManager.moveAllowed(deletionMove, neighbourEvaluation, currentBestEvaluation, objectiveFunction.isMinimizing()))) {
                    bestNeighbourEvaluation = neighbourEvaluation;
                    bestNeighbourSize = neighbourSize;
                    bestDeletionMove = deletionMove;
                }
                // undo move
                deletionMove.undo(solution);
            }
        }
        
        // undo the previously performed addition (if any)
        // to restore the original solution
        if(bestAdditionMove != null){
            bestAdditionMove.undo(solution);
        }
        
        // finally apply the best move, where in case of both an addition and a deletion
        // these are combined in a single swap move
        if(bestAdditionMove == null){
            if(bestDeletionMove == null){
                // no non-tabu moves found
                return null;
            } else {
                // apply deletion only
                bestDeletionMove.apply(solution);
                return bestDeletionMove;
            }
        } else {
            if(bestDeletionMove == null){
                // aply addition only
                bestAdditionMove.apply(solution);
                return bestAdditionMove;
            } else {
                // both addition and deletion: combine in swap
                SwapMove<IndexType, SolutionType> swap = new SwapMove<IndexType, SolutionType>(bestAdditionMove.getAddedIndex(), bestDeletionMove.getRemovedIndex());
                swap.apply(solution);
                return swap;
            }
        }
        
    }

}
