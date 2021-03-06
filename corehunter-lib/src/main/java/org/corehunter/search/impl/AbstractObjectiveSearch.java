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
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.ObjectiveSearch;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.Solution;

public abstract class AbstractObjectiveSearch<SolutionType extends Solution>
        extends AbstractSearch<SolutionType>
        implements ObjectiveSearch<SolutionType> {

    private ObjectiveFunction<SolutionType> objectiveFunction;    

    public AbstractObjectiveSearch() {
        super();
    }

    protected AbstractObjectiveSearch(AbstractObjectiveSearch<SolutionType> search) throws CoreHunterException {
        super(search);
        setObjectiveFunction(search.getObjectiveFunction().copy());        
    }
    
    @Override
    public final ObjectiveFunction<SolutionType> getObjectiveFunction() {
        return objectiveFunction;
    }

    public final void setObjectiveFunction(ObjectiveFunction<SolutionType> objectiveFunction) throws CoreHunterException {
        if (this.objectiveFunction != objectiveFunction) {
            this.objectiveFunction = objectiveFunction;
            handleObjectiveFunctionSet();
            // reset solution evaluations to worst possible values
            // when the objective was set or changed
            setCurrentSolutionEvaluation(getWorstEvaluation());
            setBestSolutionEvaluation(getWorstEvaluation());
        }
    }
    
    /**
     * Positive delta for a better solution, negative for a worse solution.
     */
    @Override
    protected double getDeltaScore(double newEvalution, double oldEvalution) {
        return getObjectiveFunction().isMinimizing() ? oldEvalution - newEvalution : newEvalution - oldEvalution;
    }

    @Override
    protected double getWorstEvaluation() {
        return getObjectiveFunction().isMinimizing() ? Double.MAX_VALUE : -Double.MAX_VALUE;
    }

    protected void handleObjectiveFunctionSet() throws CoreHunterException {
        if (objectiveFunction == null) {
            throw new CoreHunterException("No objective function defined!");
        }
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Objective function can not be set while search in process");
        }
    }

    @Override
    protected void validate() throws CoreHunterException {
        super.validate();

        if (objectiveFunction == null) {
            throw new CoreHunterException("No objective function defined!");
        }

        objectiveFunction.validate();
        
    }
}
