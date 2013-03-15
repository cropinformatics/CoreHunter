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
import org.corehunter.neighbourhood.impl.IndexedTabuManager;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.SubsetSolution;

/**
 * TABU Search.
 */
public class TabuSearch<
        IndexType,
        SolutionType extends SubsetSolution<IndexType>,
        DatasetType extends IndexedData<IndexType>,
        NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
            extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> {

    // tabu manager
    private IndexedTabuManager<IndexType> tabuManager = null;

    public TabuSearch() {
        super();
    }

    protected TabuSearch(TabuSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search) throws CoreHunterException {
        super(search);
        setTabuManager(search.getTabuManager());
    }

    @Override
    public Search<SolutionType> copy() throws CoreHunterException {
        return new TabuSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>(this);
    }

    public final IndexedTabuManager<IndexType> getTabuManager() {
        return tabuManager;
    }

    public final void setTabuManager(IndexedTabuManager<IndexType> tabuManager) throws CoreHunterException {
        if (this.tabuManager != tabuManager) {
            this.tabuManager = tabuManager;
            handleTabuManagerSet();
        }
    }

    protected void handleTabuManagerSet() throws CoreHunterException {
        if (tabuManager != null && tabuManager.getTabuHistorySize() < 0) {
            throw new CoreHunterException("Tabu Manager's history size can not be less than zero!");
        }
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Tabu Manager can not be set while search in process");
        }
    }
    
    @Override
    protected void validate() throws CoreHunterException {
        super.validate();
        if (tabuManager != null && tabuManager.getTabuHistorySize() < 0) {
            throw new CoreHunterException("Tabu Manager's history size can not be less than zero!");
        }
    }

    @Override
    protected void runSearch() throws CoreHunterException {
        
        IndexedMove<IndexType, SolutionType> move;
        double newEvaluation;
        int newSize;
        
        // accept current solution
        setCurrentSolutionEvaluation(getObjectiveFunction().calculate(getCurrentSolution()));
        // check if current solution is new best solution (may not be the case if this
        // is not the first run of this search engine)
        if(isNewBestSolution(getCurrentSolutionEvaluation())){
            handleNewBestSolution(getCurrentSolution(), getCurrentSolutionEvaluation());
        }
        
        long step = 1;
        while (canContinue(step)) {
            
            // run TABU search step

            move = getNeighbourhood().performBestMove(getCurrentSolution(), getObjectiveFunction(), tabuManager, getBestSolutionEvaluation());
            if(move != null){
                // evaluate new solution
                newEvaluation = getObjectiveFunction().calculate(getCurrentSolution());
                newSize = getCurrentSolution().getSubsetSize();
                // ALWAYS accept new solution, even it is not an improvement
                setCurrentSolutionEvaluation(newEvaluation);
                // check if new best solution was found
                if (isNewBestSolution(newEvaluation, newSize)) {
                    // handle new best solution
                    handleNewBestSolution(getCurrentSolution(), newEvaluation);
                }
                // register last move in tabu manager
                tabuManager.registerMoveTaken(move);
            } else {
                // no non-tabu neighbour found
                stop();
            }
            step++;
        }
         
    }
    
}
