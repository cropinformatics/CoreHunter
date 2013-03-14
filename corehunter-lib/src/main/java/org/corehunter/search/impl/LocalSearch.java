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

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.search.Search;
import org.corehunter.search.solution.SubsetSolution;

public class LocalSearch<
	IndexType,
        SolutionType extends SubsetSolution<IndexType>,
        DatasetType extends IndexedData<IndexType>,
        NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
            extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> {

    public LocalSearch() {
        super();
    }

    protected LocalSearch(LocalSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search) throws CoreHunterException {
        super(search);
    }

    @Override
    public Search<SolutionType> copy() throws CoreHunterException {
        return new LocalSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>(this);
    }

    @Override
    protected void runSearch() throws CoreHunterException {
        
        double newEvaluation;
        int newSize;

        setCurrentSolutionEvaluation(getObjectiveFunction().calculate(getCurrentSolution()));
        handleNewBestSolution(getCurrentSolution(), getCurrentSolutionEvaluation());

        IndexedMove<IndexType, SolutionType> move;
        long step = 1;

        while (canContinue(step)) {
            // run Local Search step
            move = getNeighbourhood().performRandomMove(getCurrentSolution());
            if(move != null){
                newEvaluation = getObjectiveFunction().calculate(getCurrentSolution());
                newSize = getCurrentSolution().getSubsetSize();
                // check if improvement
                if (isBetterSolution(newEvaluation, getBestSolutionEvaluation())
                        || (newEvaluation == getBestSolutionEvaluation() && newSize < getBestSolution().getSubsetSize())) // TODO assumes smaller cores are better
                {
                    // handle new best solution
                    handleNewBestSolution(getCurrentSolution(), newEvaluation);
                    // set current solution evaluation
                    setCurrentSolutionEvaluation(newEvaluation);
                } else {
                    // reject new solution (undo move)
                    move.undo(getCurrentSolution());
                }
            } else {
                stop();
            }
            step++;
        }
        
    }

}
