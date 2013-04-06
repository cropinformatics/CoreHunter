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
        NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
            extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, NeighbourhoodType> {

    public LocalSearch() {
        super();
    }

    protected LocalSearch(LocalSearch<IndexType, SolutionType, NeighbourhoodType> search) throws CoreHunterException {
        super(search);
    }

    @Override
    public Search<SolutionType> copy() throws CoreHunterException {
        return new LocalSearch<IndexType, SolutionType, NeighbourhoodType>(this);
    }

    @Override
    protected void runSearch() throws CoreHunterException {
        
        double newEvaluation, evaluation;
        int newSize, size;

        // accept current solution
        setCurrentSolutionEvaluation(getObjectiveFunction().calculate(getCurrentSolution()));
        // check if current solution is new best solution (may not be the case if this
        // is not the first run of this search engine)
        if(isNewBestSolution(getCurrentSolutionEvaluation(), getCurrentSolution().getSubsetSize())){
            handleNewBestSolution(getCurrentSolution(), getCurrentSolutionEvaluation());
        }

        IndexedMove<IndexType, SolutionType> move;
        long step = 1;

        while (canContinue(step)) {
            
            // run Local Search step
            
            size = getCurrentSolution().getSubsetSize();
            evaluation = getCurrentSolutionEvaluation();
            
            move = getNeighbourhood().performRandomMove(getCurrentSolution());
            if(move != null){
                newEvaluation = getObjectiveFunction().calculate(getCurrentSolution());
                newSize = getCurrentSolution().getSubsetSize();
                // check if improvement
                if (isBetterSolution(newEvaluation, evaluation, newSize, size)) {
                    // accept new solution
                    setCurrentSolutionEvaluation(newEvaluation);
                    // check if new best solution
                    if(isNewBestSolution(newEvaluation, newSize)){
                        // handle new best solution
                        handleNewBestSolution(getCurrentSolution(), newEvaluation);
                    }
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
