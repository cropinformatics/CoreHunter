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

import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.solution.SubsetSolution;

public interface SubsetNeighbourhood<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>> 
	extends Neighbourhood<SolutionType>
{
	/**
	 * Change the given solution into its best neighbour. If neighbourhood contains
	 * multiple solution with exactly the same solution, one of these is randomly
	 * selected. This method also accepts a tabu list of indices in the core set
	 * which are currently tabu.
	 * 
	 * @param solution
	 *          The current solution
	 * @param objectiveFunction
	 *          The objectiveFunction used to evaluate the solutions
	 * @param tabuList
	 *          List of indices which are tabu, meaning that the elements in the
	 *          core set at these indices cannot be removed for constructing the
	 *          'best' neighbour, these neighbours themselves are tabu and must be
	 *          avoided! If tabu list contains null value(s), only adding an
	 *          element is tabu. Implementation should ensure that tabu list is
	 *          kept consistent in case of reordering of elements in the core,
	 *          e.g. in case of the deletion of an element.
	 * @param currentBestEvaluation
	 * 					The current best evaluation
	 * @return The move the that was made
	 */
	public Move<SolutionType> performBestMove(SolutionType solution,
	    ObjectiveFunction<SolutionType> objectiveFunction, List<IndexType> tabu, 
	    double currentBestEvaluation);

	public int getSubsetMinimumSize();

	public void setSubsetMinimumSize(int subsetMinimumSize) throws CoreHunterException;

	public int getSubsetMaximumSize();

	public void setSubsetMaximumSize(int subsetMaximumSize) throws CoreHunterException;	
	
}
