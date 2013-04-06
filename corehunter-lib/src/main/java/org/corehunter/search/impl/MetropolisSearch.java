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

package org.corehunter.search.impl;

import static org.corehunter.Constants.INVALID_TEMPERATURE;

import java.text.DecimalFormat;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.SubsetSolution;

/**
 * 
 */
public class MetropolisSearch<
	IndexType,
        SolutionType extends SubsetSolution<IndexType>,
        NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
            extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, NeighbourhoodType> {

    private final static double K_b = 7.213475e-7;
    private double temperature = INVALID_TEMPERATURE;

    public MetropolisSearch() {
    }

    protected MetropolisSearch(MetropolisSearch<IndexType, SolutionType, NeighbourhoodType> search) throws CoreHunterException {
        super(search);
        setTemperature(search.getTemperature());
    }

    @Override
    public Search<SolutionType> copy() throws CoreHunterException {
        return new MetropolisSearch<IndexType, SolutionType, NeighbourhoodType>(this);
    }

    public final double getTemperature() {
        return temperature;
    }

    public final void setTemperature(double temperature) throws CoreHunterException {
        if (this.temperature != temperature) {
            this.temperature = temperature;
            handleTemperatureSet();
        }
    }
    
    protected void handleTemperatureSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Temperature can not be set while search in process");
        }
        if (temperature < 0) {
            throw new CoreHunterException("Temperature can not be less than zero!");
        }
    }

    @Override
    protected void validate() throws CoreHunterException {
        super.validate();
        if (temperature < 0) {
            throw new CoreHunterException("Temperature can not be less than zero!");
        }
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        return "Metropolis (T = " + df.format(temperature) + ")";
    }

    @Override
    protected void runSearch() throws CoreHunterException {
        
        double evaluation, newEvaluation;
        int size, newSize;
        IndexedMove<IndexType, SolutionType> move;
        boolean acceptMove;
        
        // accept current solution
        setCurrentSolutionEvaluation(getObjectiveFunction().calculate(getCurrentSolution()));
        // check if current solution is new best solution (may not be the case if this
        // is not the first run of this search engine)
        if(isNewBestSolution(getCurrentSolutionEvaluation(), getCurrentSolution().getSubsetSize())){
            handleNewBestSolution(getCurrentSolution(), getCurrentSolutionEvaluation());
        }
        
        long curStep = 1;
        while (canContinue(curStep)) {
            
            size = getCurrentSolution().getSubsetSize();
            evaluation = getCurrentSolutionEvaluation();
            
            move = getNeighbourhood().performRandomMove(getCurrentSolution());
            if(move != null){
                // compute new evaluation and size
                newEvaluation = getObjectiveFunction().calculate(getCurrentSolution());
                newSize = getCurrentSolution().getSubsetSize();

                // check for improvement w.r.t current solution
                if (isBetterSolution(newEvaluation, evaluation, newSize, size)) {
                    // better solution: always accept it!
                    acceptMove = true;
                } else {
                    // solution is not better: apply annealing criterion
                    double P = Math.exp(getDeltaScore(newEvaluation, evaluation) / (temperature * K_b));
                    double Q = getRandom().nextDouble();
                    acceptMove = (Q < P);
                }

                // check if move was accepted
                if (acceptMove) {
                    // accept solution
                    setCurrentSolutionEvaluation(newEvaluation);
                    // check for improvement w.r.t *best* solution
                    if (isNewBestSolution(newEvaluation, newSize)) {
                        // new best solution!
                        handleNewBestSolution(getCurrentSolution(), newEvaluation);
                    }
                } else {
                    // reject solution: undo move
                    move.undo(getCurrentSolution());
                }
            } else {
                // no neighbour found
                stop();
            }
            curStep++;
        }
    }

    public void swapTemperature(MetropolisSearch<IndexType, SolutionType, NeighbourhoodType> other) throws CoreHunterException {
        double myTemp = getTemperature();
        setTemperature(other.getTemperature());
        other.setTemperature(myTemp);
    }
    
}
