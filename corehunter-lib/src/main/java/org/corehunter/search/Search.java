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

package org.corehunter.search;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Entity;
import org.corehunter.search.solution.Solution;

public interface Search<SolutionType extends Solution> extends Entity
{
	/**
	 * Starts the search and does not return until the
	 * search is finished, failed or stopped for another reason
	 * 
	 * @throws CoreHunterException if the there is an error within the search run
	 */
	public void start() throws CoreHunterException;
	
	/**
	 * Stops the search as quickly as possible, if possible. There are no guarantees that the search can be
	 * stopped. 
	 * 
	 * @throws CoreHunterException if the there is an error when trying to stop the search
	 */
	public void stop() throws CoreHunterException;
	
	/**
	 * Disposes of the solution once the search has finished, failed or stopped for another reason
	 * Once this method is called the search not be started again. 
	 */
	public void dispose() ;
	
	/**
	 * Creates a copy of this search. 
	 * 
	 * @throws CoreHunterException if the copy can not be created
	 */
	public Search<SolutionType> copy() throws CoreHunterException;

	/**
	 * Gets the best solution, or <code>null</code> if no best solution has been found.
	 * The solution must be valid for the given problem.
	 * @return gets the best solution, or <code>null</code> if no best solution has been found
	 */
	public SolutionType getBestSolution();

	/**
	 * Gets the evaluation of the current best solution. 
	 * If no best solution has been found returns INTEGER.MAX_VALUE if minimising 
	 * or INTEGER.MIN_VALUE if maximising
	 * @return the evaluation of the current best solution.
	 */
	public double getBestSolutionEvaluation(); // TODO move to ObjectiveSearch?
	
	/**
	 * Gets the total time since the last best solution, or negative value if
	 * the search has not started yet
	 * 
	 * @return the total time since the last best solution
	 */
	public long getBestSolutionTime(); // TODO move to ObjectiveSearch?

	/**
	 * Gets the current status of the search
	 * 
	 * @return the current status of the search
	 */
	public SearchStatus getStatus();

	public void addSearchListener(SearchListener<SolutionType> searchListener);

	public void removeSearchListener(SearchListener<SolutionType> searchListener);

	/**
	 * Gets the total time the current search has run in nanoseconds, or negative value if
	 * the search has not started yet
	 * 
	 * @return the total time the current search has run in nanoseconds
	 */
	public long getSearchTime();

}
