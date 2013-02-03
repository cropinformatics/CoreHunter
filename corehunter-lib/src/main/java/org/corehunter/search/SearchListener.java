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

public interface SearchListener<SolutionType extends Solution>
{

	/**
	 * Called when a search started
	 * 
	 * @param search
	 *          the search started
	 */
	public void searchStarted(Search<SolutionType> search);

	/**
	 * Called when a search completes
	 * 
	 * @param search
	 *          the search completes
	 */
	public void searchCompleted(Search<SolutionType> search);

	/**
	 * Called when a search is stopped with out completing
	 * 
	 * @param search
	 *          the search stopped
	 */
	public void searchStopped(Search<SolutionType> search);
	
	/**
	 * Called when a search fails
	 * 
	 * @param search
	 *          the search fails
	 * @param exception the exception associated with the search fail if any
	 */
	public void searchFailed(Search<SolutionType> search, CoreHunterException exception);

	/**
	 * Called when a new best solution is found
	 * 
	 * @param search
	 *          the search
	 * @param bestSolution
	 *          the new best solution
	 * @param bestSolutionEvaluation
	 *          the new best solution evaluation
	 */
	public void newBestSolution(Search<SolutionType> search,
	    SolutionType bestSolution, double bestSolutionEvaluation);

	/**
	 * Reports the progress of the search
	 * 
	 * @param search
	 *          the search reporting the progress
	 * @param searchProgress
	 *          the progress as a fraction
	 */
	public void searchProgress(Search<SolutionType> search, double progress);

	/**
	 * Called when the search provides a message about the on going search
	 * 
	 * @param search
	 *          the on going search
	 * @param message
	 *          the message
	 */
	public void searchMessage(Search<SolutionType> search, String message);
}
