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

package org.corehunter.neighbourhood;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Validatable;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.solution.Solution;

public interface Neighbourhood<SolutionType extends Solution, MoveType extends Move<SolutionType>> extends Validatable {

    public abstract Neighbourhood<SolutionType, MoveType> copy();

    /**
     * Change the given solution into its best neighbour. If neighbourhood
     * contains multiple solution with exactly the same score, one of these is
     * arbitrarily selected.
     *
     * @param solution The current solution
     * @param objectiveFunction The objectiveFunction used to evaluate the
     * solutions
     * @param currentBestEvaluation The current best evaluation
     * @return The move that was made, or null if no neighbours were found
     */
    public MoveType performBestMove(SolutionType solution, ObjectiveFunction<SolutionType> objectiveFunction,
                    double currentBestEvaluation) throws CoreHunterException;
    
    /**
     * Randomly perturb the given core set into one of its neighbours.
     *
     * @param solution the solution to perturb
     *
     * @return The move that was made, null if no neighbours were found
     */
    public MoveType performRandomMove(SolutionType solution) throws CoreHunterException;
}