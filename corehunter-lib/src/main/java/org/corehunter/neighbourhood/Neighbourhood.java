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

import org.corehunter.model.Validatable;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.solution.Solution;

public interface Neighbourhood<SolutionType extends Solution> extends Validatable
{

	public abstract Neighbourhood<SolutionType> copy();

	/**
	 * Undo the move to restore the solution to the state before the move. 
	 * Implementations of the Neighbourhood class should keep track of last changes 
	 * to make such undo possible. Remark that this method only guarantees to give the correct
	 * result solution has not been changed externally after the last move! 
	 * The parameter 'historySize', which is set when creating the Neighbourhood, 
	 * defines how much previous states are remembered by the Neighbourhood. 
	 * For example if the history size is set to 1 only the very last move can be undone.
	 * Returns <code>false</false> if the move is not the history. 
	 * 
	 * @param move the move to undone 
	 * @return True if undo was successful, false if not successful because
	 *         history was depleted
	 */
	public abstract boolean undoMove(Move<SolutionType> move, SolutionType solution) ;
		
	/**
	 * Change the given solution into its best neighbour. If neighbourhood contains
	 * multiple solution with exactly the same solution, one of these is randomly
	 * selected.
	 * 
	 * @param solution
	 *          The current solution
	 * @param objectiveFunction
	 *          The objectiveFunction used to evaluate the solutions
	 * @param currentBestEvaluation
	 * 					The current best evaluation
	 * @return The move the that was made
	 */
	public Move<SolutionType> performBestMove(SolutionType solution,
	    ObjectiveFunction<SolutionType> objectiveFunction, double currentBestEvaluation);

	/**
	 * Randomly perturb the given core set into one of its neighbours.
	 * @param solution the solution to perturb
	 * 
	 * @return The move the that was made
	 */
	public Move<SolutionType> performRandomMove(SolutionType solution);
}