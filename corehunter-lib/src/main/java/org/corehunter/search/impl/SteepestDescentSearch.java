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
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.search.Search;
import org.corehunter.search.solution.SubsetSolution;

/**
 * Steepest Descent search. Always continue with the best of all neighbours, if
 * it is better than the current solution, stop search if no improvement can
 * be made. This is also called an "iterative improvement" strategy.
 */
public class SteepestDescentSearch<
        IndexType,
        SolutionType extends SubsetSolution<IndexType>,
        NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
            extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, NeighbourhoodType> {

    public SteepestDescentSearch() {
        super();
    }

    protected SteepestDescentSearch(SteepestDescentSearch<IndexType, SolutionType, NeighbourhoodType> search) throws CoreHunterException {
        super(search);
    }

    @Override
    public Search<SolutionType> copy() throws CoreHunterException {
        return new SteepestDescentSearch<IndexType, SolutionType, NeighbourhoodType>(this);
    }

    @Override
    protected void runSearch() throws CoreHunterException {
        
        /*
        
        double evalution, newScore;
        int size, newSize;

        size = getCurrentSolution().getSubsetSize();

        evalution = getObjectiveFunction().calculate(getCurrentSolution());
        size = getCurrentSolution().getSubsetSize();

        handleNewBestSolution(getCurrentSolution(), evalution);

        Move<SolutionType> move;

        continueSearch = true;

        while (continueSearch) {
            // run Steepest Descent search step
            move = getNeighbourhood().performBestMove(getCurrentSolution(), getObjectiveFunction(), evalution);
            newScore = getObjectiveFunction().calculate(getCurrentSolution());
            newSize = getCurrentSolution().getSubsetSize();

            if (newScore > evalution || (newScore == evalution && newSize < size)) {
                // check minimum progression
                if (newSize >= size && newScore - evalution < minimumProgression) {
                    continueSearch = false;
                }

                // accept new core!
                evalution = newScore;
                size = newSize;
                // continue if time left
                continueSearch = continueSearch && getSearchTime() < runtime;

                handleNewBestSolution(getCurrentSolution(), newScore);
            } else {
                // Don't accept new core
                getNeighbourhood().undoMove(move, getCurrentSolution());
                // All neighbours are worse than current core, so stop search
                continueSearch = false;
            }
        }
         
        */
        
    }
}
