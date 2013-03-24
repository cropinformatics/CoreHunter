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

import static org.corehunter.Constants.INVALID_NUMBER_OF_STEPS;
import static org.corehunter.Constants.INVALID_TIME;

import java.util.Random;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Data;
import org.corehunter.model.impl.EntityImpl;
import org.corehunter.search.Search;
import org.corehunter.search.SearchListener;
import org.corehunter.search.SearchListenerHandler;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.Solution;

public abstract class AbstractSearch<SolutionType extends Solution, DataType extends Data>
        extends EntityImpl
        implements Search<SolutionType>
{

    private static final String baseIdentifier = "id:";
    private static int nextIdentifier = 0;
    private Random random = new Random();
    private DataType data;
    private SolutionType solution;
    private double evaluation;
    private SolutionType bestSolution;
    private double bestSolutionEvaluation;
    private double lastBestSolutionEvaluationDelta;
    private SearchListenerHandler<SolutionType> searchListenerHandler;
    private SearchStatus status;
    private long startTime = -2;
    private long endTime = -1;
    private long bestSolutionTime = -1;
    
    // stop criteria
    private long runtimeLimit = INVALID_TIME;
    private long maxTimeWithoutImprovement = INVALID_TIME;
    private long maxNrOfSteps = INVALID_NUMBER_OF_STEPS;
    private double minimumProgression = 0;
    
    // min delta for new best solution
    private static final double MIN_DELTA_FOR_NEW_BEST_SOLUTION = 1e-10;

    public AbstractSearch() {
        super(getNextUniqueIdentifier(), getCurrentUniqueIdentifier());

        status = SearchStatus.NOT_STARTED;
        searchListenerHandler = new SearchListenerHandler<SolutionType>(this);
        
    }

    @SuppressWarnings("unchecked")
    protected AbstractSearch(AbstractSearch<SolutionType, DataType> search) throws CoreHunterException {
        super(getNextUniqueIdentifier(), search.getName());

        status = SearchStatus.NOT_STARTED;
        searchListenerHandler = new SearchListenerHandler<SolutionType>(this);

        // set data
        setData(search.getData());
        
        // set initial and best solution + evaluations
        setInitialSolution((SolutionType) search.getCurrentSolution().copy());
        setCurrentSolutionEvaluation(search.getCurrentSolutionEvaluation());
        setBestSolution((SolutionType) search.getBestSolution().copy());
        setBestSolutionEvaluation(search.getBestSolutionEvaluation());
        
        // set stop criteria
        setRuntimeLimit(search.getRuntimeLimit());
        setMaxTimeWithoutImprovement(search.getMaxTimeWithoutImprovement());
        setMaxNumberOfSteps(search.getMaxNumberOfSteps());
        setMinimumProgression(search.getMinimumProgression());
    }

    /**
     * Sets the initial solution under evaluation
     *
     * @throws CoreHunterException if the search is in progress
     */
    public final void setInitialSolution(SolutionType solution) throws CoreHunterException {
        if (this.solution != solution) {
            setCurrentSolution(solution);
            handleInitialSolutionSet();
        }
    }

    public final DataType getData() {
        return data;
    }

    public final void setData(DataType data) throws CoreHunterException {
        if (this.data != data) {
            this.data = data;
            handleDataSet();
        }
    }

    @Override
    public void start() throws CoreHunterException {
        if (!SearchStatus.STARTED.equals(status)) {
            startTime = System.nanoTime();
            bestSolutionTime = startTime;
            // reset best solution delta to best value
            lastBestSolutionEvaluationDelta = Double.MAX_VALUE;

            try {
                
                validate();
                fireSearchStarted();
                
                runSearch();

                endTime = System.nanoTime();
                fireSearchCompleted();
                
            } catch (CoreHunterException exception) {
                endTime = System.nanoTime();

                fireSearchFailed(exception);

                throw exception;
            }
        }
    }

    @Override
    public void stop() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(status)) {
            endTime = System.nanoTime();
            fireSearchStopped();
            status = SearchStatus.STOPPED;
        }
    }
    
    /**
     * Check whether the search is allowed to continue, w.r.t to its stop
     * criteria.
     * 
     * @param curStep Current step number (counted from 1)
     * @return 
     */
    public boolean canContinue(long curStep){
        // check if search was stopped externally
        if(status.equals(SearchStatus.STOPPED)){
            fireSearchMessage("Stopping... Search engine terminated.");
            return false;
        }
        // check runtime limit
        if(runtimeLimit != INVALID_TIME && getSearchTime() > runtimeLimit){
            fireSearchMessage("Stopping... Runtime limit exceeded.");
            return false;
        }
        // check time without improvement
        if(maxTimeWithoutImprovement != INVALID_TIME && getBestSolutionTime() > maxTimeWithoutImprovement){
            fireSearchMessage("Stopping... Maximum time without improvement exceeded.");
            return false;
        }
        // check number of steps
        if(maxNrOfSteps != INVALID_NUMBER_OF_STEPS && curStep > maxNrOfSteps){
            fireSearchMessage("Stopping... Maximum number of steps exceeded.");
            return false;
        }
        // check min progression (of last new best solution)
        if(getLastBestSolutionScoreDelta() < minimumProgression){
            fireSearchMessage("Stopping... Required minimum progression not obtained.");
            return false;
        }
        // all stop criteria ok
        return true;
    }
    
    public final long getRuntimeLimit() {
        return runtimeLimit;
    }

    public final void setRuntimeLimit(long runtimeLimit) throws CoreHunterException {
        if (this.runtimeLimit != runtimeLimit) {
            this.runtimeLimit = runtimeLimit;
            handleRuntimeLimitSet();
        }
    }
    
    public final long getMaxNumberOfSteps() {
        return maxNrOfSteps;
    }

    public final void setMaxNumberOfSteps(long maxNrOfSteps) throws CoreHunterException {
        if (this.maxNrOfSteps != maxNrOfSteps) {
            this.maxNrOfSteps = maxNrOfSteps;
            handleMaxNumberOfStepsSet();
        }
    }
    
    public final long getMaxTimeWithoutImprovement() {
        return maxTimeWithoutImprovement;
    }

    public final void setMaxTimeWithoutImprovement(long maxTimeWithoutImprovement) throws CoreHunterException {
        if (this.maxTimeWithoutImprovement != maxTimeWithoutImprovement) {
            this.maxTimeWithoutImprovement = maxTimeWithoutImprovement;
            handleMaxTimeWithoutImprovementSet();
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
        if (runtimeLimit != INVALID_TIME && runtimeLimit <= 0) {
            throw new CoreHunterException("Runtime can not be less than or equal to zero!");
        }
    }
    
    protected void handleMaxNumberOfStepsSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Number Of Steps can not be set while search in process");
        }
        if (maxNrOfSteps != INVALID_NUMBER_OF_STEPS && maxNrOfSteps <= 0) {
            throw new CoreHunterException("Number of Steps can not be less than or equal to zero!");
        }
    }
    
    protected void handleMaxTimeWithoutImprovementSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Max time without improvement can not be set can not be set while search in process");
        }
        if (maxTimeWithoutImprovement != INVALID_TIME && maxTimeWithoutImprovement <= 0) {
            throw new CoreHunterException("Max time without improvement can not be less than or equal to zero!");
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

    @Override
    public void dispose() {
        if (!SearchStatus.STARTED.equals(status)) {
            status = SearchStatus.DISPOSED;
            solution = null;
            data = null;
            bestSolution = null;
            searchListenerHandler.dispose();
        }
    }

    @Override
    public final SolutionType getBestSolution() {
        return bestSolution;
    }

    @Override
    public final double getBestSolutionEvaluation() {
        return bestSolutionEvaluation;
    }
    
    /**
     * Gets the current solution under evaluation, which may not be the best
     * solution found so far. To get the 'best' solution use
     * {@link #getBestSolution()}
     *
     * @return the current solution under evaluation
     */
    public final SolutionType getCurrentSolution() {
        return solution;
    }
    
    public final double getCurrentSolutionEvaluation() {
        return evaluation;
    }

    @Override
    public final SearchStatus getStatus() {
        return status;
    }

    @Override
    public final void addSearchListener(SearchListener<SolutionType> searchListener) {
        searchListenerHandler.addSearchListener(searchListener);
    }

    @Override
    public final void removeSearchListener(SearchListener<SolutionType> searchListener) {
        searchListenerHandler.removeSearchListener(searchListener);
    }

    @Override
    public final long getSearchTime() {
        return endTime < 0 ? System.nanoTime() - startTime : endTime - startTime;
    }

    @Override
    public final long getBestSolutionTime() {
        return System.nanoTime() - bestSolutionTime;
    }

    public final void setRandom(Random random) {
        this.random = random;
    }

    public final Random getRandom() {
        return random;
    }

    protected void validate() throws CoreHunterException {
        
        if (runtimeLimit != INVALID_TIME && runtimeLimit <= 0) {
            throw new CoreHunterException("Runtime can not be less than or equal to zero!");
        }
        if (maxNrOfSteps != INVALID_NUMBER_OF_STEPS && maxNrOfSteps <= 0) {
            throw new CoreHunterException("Number of Steps can not be less than or equal to zero!");
        }
        if (maxTimeWithoutImprovement != INVALID_TIME && maxTimeWithoutImprovement <= 0) {
            throw new CoreHunterException("Max time without improvement can not be less than or equal to zero!");
        }
        if (minimumProgression < 0) {
            throw new CoreHunterException("Minimum Progression can not be less than zero!");
        }
        
        if (SearchStatus.DISPOSED.equals(status)) {
            throw new CoreHunterException("Solution can not be started if aleady disposed!");
        }

        if (SearchStatus.FAILED.equals(status)) {
            throw new CoreHunterException("Solution can not be started previously failed!");
        }

        if (solution == null) {
            throw new CoreHunterException("No start solution defined!");
        }

        if (data == null) {
            throw new CoreHunterException("No dataset defined!");
        }

        solution.validate();
        data.validate();
        
    }

    protected abstract void runSearch() throws CoreHunterException;

    protected void handleInitialSolutionSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(status)) {
            throw new CoreHunterException("Initial solution can not be set while search in process");
        }
        if (solution == null) {
            throw new CoreHunterException("No initial solution defined!");
        }
    }

    protected void handleDataSet() throws CoreHunterException {

        if (SearchStatus.STARTED.equals(status)) {
            throw new CoreHunterException("Dataset can not be set while search in process");
        }

        if (data == null) {
            throw new CoreHunterException("No dataset defined!");
        }
    }

    @SuppressWarnings("unchecked")
    protected void handleNewBestSolution(SolutionType bestSolution, double bestSolutionEvaluation) {
        setBestSolutionEvaluation(bestSolutionEvaluation);
        setBestSolution((SolutionType) bestSolution.copy());
        fireNewBestSolution(getBestSolution(), bestSolutionEvaluation);
    }

    private void setBestSolution(SolutionType bestSolution) {
        this.bestSolutionTime = System.nanoTime();
        this.bestSolution = bestSolution;
    }

    protected final void setBestSolutionEvaluation(double bestSolutionEvaluation) {
        lastBestSolutionEvaluationDelta = getDeltaScore(bestSolutionEvaluation, this.bestSolutionEvaluation);
        // register new evaluation
        this.bestSolutionEvaluation = bestSolutionEvaluation;
    }
    
    /**
     * Implementation should take care of maximization vs minimization of the evaluation.
     * Positive delta for better evaluation, negative for worse.
     */
    protected abstract double getDeltaScore(double newEvalution, double oldEvalution);
    
    /**
     * Implementation should take care of maximization vs minimization of the evaluation.
     */
    protected abstract double getWorstEvaluation();
    
    /**
     * Compare two different solutions.
     */
    protected boolean isBetterSolution(double newEvaluation, double oldEvaluation){
        return getDeltaScore(newEvaluation, oldEvaluation) > 0;
    }
    
    /**
     * Check whether a solution is better than the currently best solution. Note: we
     * require a fixed, small minimum improvement to avoid the same solution being reported
     * multiple times as a new best solution because of rounding errors during computation
     * of the evaluation.
     */
    protected boolean isNewBestSolution(double evaluation){
        return getDeltaScore(evaluation, getBestSolutionEvaluation()) > MIN_DELTA_FOR_NEW_BEST_SOLUTION;
    }
        
    protected double getLastBestSolutionScoreDelta(){
        return lastBestSolutionEvaluationDelta;
    }
    
    protected void setCurrentSolution(SolutionType solution) {
        this.solution = solution;
    }
    
    protected final void setCurrentSolutionEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    protected final void setStatus(SearchStatus status) {
        this.status = status;
    }
        
    private void fireSearchStarted() {
        status = SearchStatus.STARTED;

        searchListenerHandler.fireSearchStarted();
    }

    private void fireSearchCompleted() {
        status = SearchStatus.COMPLETED;

        searchListenerHandler.fireSearchCompleted();
    }

    private void fireSearchStopped() {
        status = SearchStatus.STOPPED;

        searchListenerHandler.fireSearchStopped();
    }

    private void fireSearchFailed(CoreHunterException exception) {
        status = SearchStatus.FAILED;

        searchListenerHandler.fireSearchFailed(exception);
    }

    private void fireNewBestSolution(SolutionType bestSolution, double bestScore) {
        searchListenerHandler.fireNewBestSolution(bestSolution, bestScore);
    }

    protected void fireSearchProgress(double searchProgress) {
        searchListenerHandler.fireSearchProgress(searchProgress);
    }

    protected void fireSearchMessage(String message) {
        searchListenerHandler.fireSearchMessage(message);
    }

    private static String getCurrentUniqueIdentifier() {
        String identifier = baseIdentifier + nextIdentifier;
        return identifier;
    }

    private static String getNextUniqueIdentifier() {
        nextIdentifier++;
        String identifier = baseIdentifier + nextIdentifier;
        return identifier;
    }
}
