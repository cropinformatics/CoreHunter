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

import java.util.Random;

import org.corehunter.CoreHunterException;
import org.corehunter.model.impl.EntityImpl;
import org.corehunter.search.Search;
import org.corehunter.search.SearchListener;
import org.corehunter.search.SearchListenerHandler;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.Solution;

public abstract class AbstractSearch<SolutionType extends Solution>
    extends EntityImpl implements Search<SolutionType>
{

	private static final String	                baseIdentifier	                = "id:";
	private static int	                        nextIdentifier	                = 0;
	private Random	                            random	                        = new Random();
	private SolutionType	                      solution;
	private double	                            evaluation;
	private SolutionType	                      bestSolution;
	private double	                            bestSolutionEvaluation;
	private double	                            lastBestSolutionEvaluationDelta;
	private SearchListenerHandler<SolutionType>	searchListenerHandler;
	private SearchStatus	                      status;
	private long	                              startTime	                      = -2;
	private long	                              endTime	                        = -1;
	private long	                              bestSolutionTime	              = -1;

	// min delta for new best solution
	private static final double	                MIN_DELTA_FOR_NEW_BEST_SOLUTION	= 1e-10;

	public AbstractSearch()
	{
		super(getNextUniqueIdentifier(), getCurrentUniqueIdentifier());

		status = SearchStatus.NOT_STARTED;
		searchListenerHandler = new SearchListenerHandler<SolutionType>(this);

	}

	@SuppressWarnings("unchecked")
	protected AbstractSearch(AbstractSearch<SolutionType> search)
	    throws CoreHunterException
	{
		super(getNextUniqueIdentifier(), search.getName());

		status = SearchStatus.NOT_STARTED;
		searchListenerHandler = new SearchListenerHandler<SolutionType>(this);

		// set current and best solution + evaluations
		setCurrentSolution((SolutionType) search.getCurrentSolution().copy());
		setCurrentSolutionEvaluation(search.getCurrentSolutionEvaluation());
		setBestSolution((SolutionType) search.getBestSolution().copy());
		setBestSolutionEvaluation(search.getBestSolutionEvaluation());
	}

	@Override
	public void start() throws CoreHunterException
	{
		if (!SearchStatus.STARTED.equals(status))
		{
			startTime = System.nanoTime();
			bestSolutionTime = startTime;
			// reset best solution delta to best value
			lastBestSolutionEvaluationDelta = Double.MAX_VALUE;

			try
			{

				validate();
				fireSearchStarted();

				runSearch();

				endTime = System.nanoTime();
				fireSearchCompleted();

			}
			catch (CoreHunterException exception)
			{
				endTime = System.nanoTime();

				fireSearchFailed(exception);

				throw exception;
			}
		}
	}

	@Override
	public void stop() throws CoreHunterException
	{
		if (SearchStatus.STARTED.equals(status))
		{
			endTime = System.nanoTime();
			
			fireSearchMessage("Stopping... Search engine terminated.");
			
			fireSearchStopped();
		}
	}

	/**
	 * Check whether the search has been stopped manually.
	 */
	protected boolean canContinue()
	{
		// check if has not been stopped, completed or failed for any reason
		// Note status should never be SearchStatus.NOT_STARTED at this point
		return status.equals(SearchStatus.STARTED) ;
	}

	@Override
	public void dispose()
	{
		if (!SearchStatus.STARTED.equals(status))
		{
			status = SearchStatus.DISPOSED;
			solution = null;
			bestSolution = null;
			searchListenerHandler.dispose();
		}
	}
        
	/**
	 * Sets the initial solution under evaluation. For neighbourhood searches, this solution is required to be of
	 * valid size (between specified min. and max. size). For non neighbourhood searches, this is not required; for
	 * example the initial solution might be empty here.
	 *
	 * @throws CoreHunterException if the search is in progress
	 */
	public final void setInitialSolution(SolutionType solution) throws CoreHunterException {
		if (getCurrentSolution() != solution) {
			setCurrentSolution(solution);
			handleInitialSolutionSet();
		}
	}

	protected void handleInitialSolutionSet() throws CoreHunterException {
		if (SearchStatus.STARTED.equals(getStatus())) {
			throw new CoreHunterException("Initial solution can not be set while search in process");
		}
		if (getCurrentSolution() == null) {
			throw new CoreHunterException("No initial solution defined!");
		}
	}

	@Override
	public final SolutionType getBestSolution()
	{
		return bestSolution;
	}

	@Override
	public final double getBestSolutionEvaluation()
	{
		return bestSolutionEvaluation;
	}

	/**
	 * Gets the current solution under evaluation, which may not be the best
	 * solution found so far. In some searches it may not even by a valid solution!
	 * To get the 'best' solution use {@link #getBestSolution()}, this must not be a valid solution. 
	 * 
	 * @return the current solution under evaluation
	 */
	public final SolutionType getCurrentSolution()
	{
		return solution;
	}

	public final double getCurrentSolutionEvaluation()
	{
		return evaluation;
	}

	@Override
	public final SearchStatus getStatus()
	{
		return status;
	}

	@Override
	public final void addSearchListener(
	    SearchListener<SolutionType> searchListener)
	{
		searchListenerHandler.addSearchListener(searchListener);
	}

	@Override
	public final void removeSearchListener(
	    SearchListener<SolutionType> searchListener)
	{
		searchListenerHandler.removeSearchListener(searchListener);
	}

	@Override
	public final long getSearchTime()
	{
		return endTime < 0 ? System.nanoTime() - startTime : endTime - startTime;
	}

	/**
	 * Returns the time in nanoseconds since a new best solution was found.
	 */
	@Override
	public final long getBestSolutionTime()
	{
		return System.nanoTime() - bestSolutionTime;
	}

	public final void setRandom(Random random)
	{
		this.random = random;
	}

	public final Random getRandom()
	{
		return random;
	}

	protected void validate() throws CoreHunterException
	{

		if (SearchStatus.DISPOSED.equals(status))
		{
			throw new CoreHunterException(
					"Solution can not be started if aleady disposed!");
		}

		if (SearchStatus.FAILED.equals(status))
		{
			throw new CoreHunterException(
					"Solution can not be started if previously failed!");
		}

		// validate initial solution

		if (getCurrentSolution() == null) {
			throw new CoreHunterException("No start solution defined!");
		}

		getCurrentSolution().validate();
	}

	protected abstract void runSearch() throws CoreHunterException;

	@SuppressWarnings("unchecked")
	protected void handleNewBestSolution(SolutionType bestSolution,
	    double bestSolutionEvaluation)
	{
		setBestSolutionEvaluation(bestSolutionEvaluation);
		setBestSolution((SolutionType) bestSolution.copy());
		fireNewBestSolution(getBestSolution(), bestSolutionEvaluation);
	}

	private void setBestSolution(SolutionType bestSolution)
	{
		this.bestSolutionTime = System.nanoTime();
		this.bestSolution = bestSolution;
	}

	protected final void setBestSolutionEvaluation(double bestSolutionEvaluation)
	{
		lastBestSolutionEvaluationDelta = getDeltaScore(bestSolutionEvaluation,
		    this.bestSolutionEvaluation);
		// register new evaluation
		this.bestSolutionEvaluation = bestSolutionEvaluation;
	}

	/**
	 * Implementation should take care of maximization vs minimization of the
	 * evaluation. Positive delta for better evaluation, negative for worse.
	 */
	protected abstract double getDeltaScore(double newEvalution,
	    double oldEvalution);

	/**
	 * Implementation should take care of maximization vs minimization of the
	 * evaluation.
	 */
	protected abstract double getWorstEvaluation();

	/**
	 * Compare two different solutions.
	 */
	protected boolean isBetterSolution(double newEvaluation, double oldEvaluation)
	{
		return getDeltaScore(newEvaluation, oldEvaluation) > 0;
	}

	/**
	 * Check whether a solution is better than the currently best solution. Note:
	 * we require a fixed, small minimum improvement to avoid the same solution
	 * being reported multiple times as a new best solution because of rounding
	 * errors during computation of the evaluation.
	 */
	protected boolean isNewBestSolution(double evaluation)
	{
		return getDeltaScore(evaluation, getBestSolutionEvaluation()) > MIN_DELTA_FOR_NEW_BEST_SOLUTION;
	}

	protected double getLastBestSolutionScoreDelta()
	{
		return lastBestSolutionEvaluationDelta;
	}

	protected final void setCurrentSolution(SolutionType solution)
	{
		this.solution = solution;
	}

	protected final void setCurrentSolutionEvaluation(double evaluation)
	{
		this.evaluation = evaluation;
	}

	protected final void setStatus(SearchStatus status)
	{
		this.status = status;
	}

	private void fireSearchStarted()
	{
		status = SearchStatus.STARTED;

		searchListenerHandler.fireSearchStarted();
	}

	private void fireSearchCompleted()
	{
		status = SearchStatus.COMPLETED;

		searchListenerHandler.fireSearchCompleted();
	}

	private void fireSearchStopped()
	{
		status = SearchStatus.STOPPED;

		searchListenerHandler.fireSearchStopped();
	}

	private void fireSearchFailed(CoreHunterException exception)
	{
		status = SearchStatus.FAILED;

		searchListenerHandler.fireSearchFailed(exception);
	}

	private void fireNewBestSolution(SolutionType bestSolution, double bestScore)
	{
		searchListenerHandler.fireNewBestSolution(bestSolution, bestScore);
	}

	protected void fireSearchProgress(double searchProgress)
	{
		searchListenerHandler.fireSearchProgress(searchProgress);
	}

	protected void fireSearchMessage(String message)
	{
		searchListenerHandler.fireSearchMessage(message);
	}

	private static String getCurrentUniqueIdentifier()
	{
		String identifier = baseIdentifier + nextIdentifier;
		return identifier;
	}

	private static String getNextUniqueIdentifier()
	{
		nextIdentifier++;
		String identifier = baseIdentifier + nextIdentifier;
		return identifier;
	}
}
