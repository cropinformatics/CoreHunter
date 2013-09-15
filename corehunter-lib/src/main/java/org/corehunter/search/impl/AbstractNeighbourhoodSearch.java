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
import org.corehunter.neighbourhood.Move;
import org.corehunter.neighbourhood.Neighbourhood;
import org.corehunter.search.NeighbourhoodSearch;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.Solution;


public abstract class AbstractNeighbourhoodSearch<
        SolutionType extends Solution,
        MoveType extends Move<SolutionType>,
        NeighbourhoodType extends Neighbourhood<SolutionType, MoveType>>
            extends AbstractObjectiveSearch<SolutionType>
            implements NeighbourhoodSearch<SolutionType, MoveType, NeighbourhoodType> {

    // neighbourhood
    private NeighbourhoodType neighbourhood;
    
    // stop criteria
    private Long runtimeLimit = null;
    private Long maximumTimeWithoutImprovement = null;
    private Long maximumNumberOfSteps = null;
    private double minimumProgression = 0;
    
    // number of steps already performed
    private long stepsTaken;

    public AbstractNeighbourhoodSearch() {
        super();
    }

    @SuppressWarnings("unchecked")
    protected AbstractNeighbourhoodSearch(AbstractNeighbourhoodSearch<SolutionType, MoveType, NeighbourhoodType> search) throws CoreHunterException {
        super(search);
        // set neighbourhood
        setNeighbourhood((NeighbourhoodType)search.getNeighbourhood().copy());
        // set stop criteria
        setRuntimeLimit(search.getRuntimeLimit());
        setMaxTimeWithoutImprovement(search.getMaxTimeWithoutImprovement());
        setMaximumNumberOfSteps(search.getMaximumumberOfSteps());
        setMinimumProgression(search.getMinimumProgression());
    }
    
    @Override
    public void start() throws CoreHunterException {
        
        if (!SearchStatus.STARTED.equals(getStatus())) {
            resetStepsTaken();
            super.start();
        }
        
    }

    @Override
    public final NeighbourhoodType getNeighbourhood() {
        return neighbourhood;
    }

    public final void setNeighbourhood(NeighbourhoodType neighbourhood) throws CoreHunterException {
        if (this.neighbourhood != neighbourhood) {
            this.neighbourhood = neighbourhood;
            handleNeighbourhoodSet();
        }
    }

    protected void handleNeighbourhoodSet() throws CoreHunterException {
        if (neighbourhood == null) {
            throw new CoreHunterException("No neighbourhood defined!");
        }
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Neighbourhood can not be set while search in process");
        }
    }
    
    public final Long getRuntimeLimit() {
        return runtimeLimit;
    }

    public final void setRuntimeLimit(Long runtimeLimit) throws CoreHunterException {
        if (this.runtimeLimit != runtimeLimit) {
            this.runtimeLimit = runtimeLimit;
            handleRuntimeLimitSet();
        }
    }

    public final Long getMaximumumberOfSteps() {
        return maximumNumberOfSteps;
    }

    public final void setMaximumNumberOfSteps(Long maximumNumberOfSteps) throws CoreHunterException {
        if (this.maximumNumberOfSteps != maximumNumberOfSteps) {
            this.maximumNumberOfSteps = maximumNumberOfSteps;
            handleMaximumNumberOfStepsSet();
        }
    }

    public final Long getMaxTimeWithoutImprovement() {
        return maximumTimeWithoutImprovement;
    }

    public final void setMaxTimeWithoutImprovement(Long maxTimeWithoutImprovement) throws CoreHunterException {
        if (this.maximumTimeWithoutImprovement != maxTimeWithoutImprovement) {
            this.maximumTimeWithoutImprovement = maxTimeWithoutImprovement;
            handleMaximumTimeWithoutImprovementSet();
        }
    }

    public final double getMinimumProgression() {
        return minimumProgression;
    }

    public final void setMinimumProgression(double minimumProgression) throws CoreHunterException {
        if (this.minimumProgression != minimumProgression) {
            this.minimumProgression = minimumProgression;
            handleMinimumProgressionSet();
        }
    }

    protected void handleRuntimeLimitSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Runtime can not be set while search in process");
        }
        if (runtimeLimit != null && runtimeLimit <= 0) {
            throw new CoreHunterException("Runtime can not be less than or equal to zero!");
        }
    }

    protected void handleMaximumNumberOfStepsSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Number of steps can not be set while search in process");
        }
        if (maximumNumberOfSteps != null && maximumNumberOfSteps <= 0) {
            throw new CoreHunterException("Number of steps can not be less than or equal to zero!");
        }
    }

    protected void handleMaximumTimeWithoutImprovementSet()
            throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Maximum time without improvement can not be set can not be set while search in process");
        }
        if (maximumTimeWithoutImprovement != null
                && maximumTimeWithoutImprovement <= 0) {
            throw new CoreHunterException( "Maximum time without improvement can not be less than or equal to zero!");
        }
    }

    protected void handleMinimumProgressionSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Minimum Progression can not be set while search in process");
        }
        if (minimumProgression < 0) {
            throw new CoreHunterException("Minimum Progression can not be less than zero!");
        }
    }
    
    protected void resetStepsTaken(){
        stepsTaken = 0;
    }
    
    protected void incStepsTaken(){
        stepsTaken++;
    }
    
    protected long getStepsTaken(){
        return stepsTaken;
    }
    
    /**
    * Check whether the search has been stopped manually, or one of the
    * stop criteria has been exceeded.
    */
    @Override
   protected boolean canContinue()
   {
        // check if manually halted (see AbstractSearch)
        if (!super.canContinue())
        {
                return false;
        }
        // check runtime limit
        if (runtimeLimit != null && getSearchTime() > runtimeLimit)
        {
                fireSearchMessage("Stopping... Runtime limit exceeded.");
                return false;
        }
        // check time without improvement
        if (maximumTimeWithoutImprovement != null
            && getBestSolutionTime() > maximumTimeWithoutImprovement)
        {
                fireSearchMessage("Stopping... Maximum time without improvement exceeded.");
                return false;
        }
        // check number of steps
        if (maximumNumberOfSteps != null && stepsTaken >= maximumNumberOfSteps)
        {
                fireSearchMessage("Stopping... Maximum number of steps exceeded.");
                return false;
        }
        // check min progression (of last new best solution)
        if (getLastBestSolutionScoreDelta() < minimumProgression)
        {
                fireSearchMessage("Stopping... Required minimum progression not obtained.");
                return false;
        }
        // search not stopped
        return true;
   }

    @Override
    protected void validate() throws CoreHunterException {
        super.validate();

        // validate stop criteria
        
        if (runtimeLimit != null && runtimeLimit <= 0) {
            throw new CoreHunterException( "Runtime can not be less than or equal to zero!");
        }
        if (maximumNumberOfSteps != null && maximumNumberOfSteps <= 0) {
            throw new CoreHunterException("Number of Steps can not be less than or equal to zero!");
        }
        if (maximumTimeWithoutImprovement != null && maximumTimeWithoutImprovement <= 0) {
            throw new CoreHunterException("Max time without improvement can not be less than or equal to zero!");
        }
        if (minimumProgression < 0) {
            throw new CoreHunterException("Minimum Progression can not be less than zero!");
        }

        // validate neighbourhood
        
        if (neighbourhood == null) {
            throw new CoreHunterException("No neighbourhood defined!");
        }

        neighbourhood.validate();
        
    }
}
