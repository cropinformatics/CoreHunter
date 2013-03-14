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

import java.text.DecimalFormat;
import static org.corehunter.Constants.INVALID_TEMPERATURE;
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
        DatasetType extends IndexedData<IndexType>,
        NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
            extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> {

    private final static double K_b = 7.213475e-7;
    private double temperature = INVALID_TEMPERATURE;
    private int accepts;
    private int rejects;
    private int improvements;
    private int totalSteps;

    public MetropolisSearch() {
        accepts = 0;
        rejects = 0;
        improvements = 0;
        totalSteps = 0;
    }

    protected MetropolisSearch(MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search) throws CoreHunterException {
        super(search);
        accepts = 0;
        rejects = 0;
        improvements = 0;
        totalSteps = 0;
        setTemperature(search.getTemperature());
    }

    @Override
    public Search<SolutionType> copy() throws CoreHunterException {
        return new MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>(this);
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
        return " (T = " + df.format(temperature) + ")";
    }

    @Override
    protected void runSearch() throws CoreHunterException {
        
        double evaluation, newEvaluation;
        boolean acceptMove;
        int size, newSize;
        IndexedMove<IndexType, SolutionType> move;

        setCurrentSolutionEvaluation(getObjectiveFunction().calculate(getCurrentSolution()));
        handleNewBestSolution(getCurrentSolution(), getCurrentSolutionEvaluation());
        
        long curStep = 1;
        while (canContinue(curStep)) {
            
            size = getCurrentSolution().getSubsetSize();
            evaluation = getCurrentSolutionEvaluation();
            
            move = getNeighbourhood().performRandomMove(getCurrentSolution());
            if(move != null){
                newSize = getCurrentSolution().getSubsetSize();
                newEvaluation = getObjectiveFunction().calculate(getCurrentSolution());

                double deltaScore = getDeltaScore(newEvaluation, evaluation);

                // check for improvement w.r.t current solution
                if (deltaScore > 0 || (deltaScore == 0 && newSize < size)) // TODO should we always accept smaller solutions for equal evaluations?
                {
                    // accept new solution!
                    acceptMove = true;
                } else {

                    // no improvement -- annealing criterion used to decide whether
                    // to accept the new solution

                    int deltaSize = newSize - size;

                    if (deltaSize > 0) // TODO should we always reject larger solutions for worst evaluations?
                    {
                        // new solution is bigger than old solution and has no better
                        // evaluation --> reject new solution, stick with old solution

                        acceptMove = false;
                    } else {
                        // new solution is smaller, but has worse or equal evaluation
                        // accept or reject new solution based on temperature
                        double P = Math.exp(deltaScore / (temperature * K_b));
                        double Q = getRandom().nextDouble();

                        if (Q > P) {
                            acceptMove = false;
                        } else {
                            // accept new solution!
                            acceptMove = true;
                        }
                    }
                }

                if (acceptMove) {
                    // accept solution
                    setCurrentSolutionEvaluation(newEvaluation);
                    // check for improvement w.r.t *best* solution
                    double deltaScoreBest = getDeltaScore(newEvaluation, getBestSolutionEvaluation());
                    if (deltaScoreBest > 0 || (deltaScoreBest == 0 && newSize < getBestSolution().getSubsetSize())) {
                        // new best solution!
                        handleNewBestSolution(getCurrentSolution(), getCurrentSolutionEvaluation());
                        improvements++;
                    }
                    accepts++;
                } else {
                    // reject solution: undo move
                    move.undo(getCurrentSolution());
                    rejects++;
                }
            } else {
                // no neighbour found
                stop();
            }
            totalSteps++;
            curStep++;
        }
    }

    public void swapTemperature(MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> other) throws CoreHunterException {
        double myTemp = getTemperature();
        setTemperature(other.getTemperature());
        other.setTemperature(myTemp);
    }
    
}
