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

package org.corehunter.neighbourhood.impl;

import java.util.Random;
import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.solution.SubsetSolution;

/**
 * Implements an abstract neighbourhood which defines the neighbours of a given
 * solution. Depending on the chosen algorithm that uses the neighbourhood,
 * one can generate a random neighbour or the best one.
 */
public abstract class AbstractSubsetNeighbourhood<IndexType, SolutionType extends SubsetSolution<IndexType>> implements SubsetNeighbourhood<IndexType, SolutionType> {

    private Random random = new Random();
    private int subsetMinimumSize;
    private int subsetMaximumSize;

    public AbstractSubsetNeighbourhood() {
    }

    protected AbstractSubsetNeighbourhood(AbstractSubsetNeighbourhood<IndexType, SolutionType> singleneighbourhood) throws CoreHunterException {
        setSubsetMinimumSize(singleneighbourhood.getSubsetMinimumSize());
        setSubsetMaximumSize(singleneighbourhood.getSubsetMaximumSize());
    }
    
    /**
     * Default: no tabu manager specified.
     */
    @Override
    public final IndexedMove<IndexType, SolutionType> performBestMove(SolutionType solution,
            ObjectiveFunction<SolutionType> objectiveFunction, double currentBestEvaluation) throws CoreHunterException {
        
        return performBestMove(solution, objectiveFunction, null, currentBestEvaluation);
        
    }

    @Override
    public final int getSubsetMinimumSize() {
        return subsetMinimumSize;
    }

    @Override
    public final void setSubsetMinimumSize(int subsetMinimumSize) throws CoreHunterException {
        if (this.subsetMinimumSize != subsetMinimumSize) {
            this.subsetMinimumSize = subsetMinimumSize;
            handleSubsetMinimumSizeSet();
        }
    }

    @Override
    public final int getSubsetMaximumSize() {
        return subsetMaximumSize;
    }

    @Override
    public synchronized final void setSubsetMaximumSize(int subsetMaximumSize) throws CoreHunterException {
        if (this.subsetMaximumSize != subsetMaximumSize) {
            this.subsetMaximumSize = subsetMaximumSize;
            handleSubsetMaximumSizeSet();
        }
    }

    @Override
    public void validate() throws CoreHunterException {
        if (subsetMinimumSize <= 0) {
            throw new CoreHunterException("Subset minimum size must be greater than zero!");
        }

        if (subsetMaximumSize <= 0) {
            throw new CoreHunterException("Subset maximum size must be greater than zero!");
        }
        
        if (subsetMaximumSize < subsetMinimumSize) {
            throw new CoreHunterException("Subset maximum size must be greater then or equal to minimum size!");
        }

        // TODO
//        if (subsetMinimumSize <= getDataset().getSize()) {
//            throw new CoreHunterException("Subset minimum size must be less than or equal to dataset size!") ;
//        }
//
//        if (subsetMaximumSize <= getDataset().getSize()) {
//            throw new CoreHunterException("Subset maximum size must be less than or equal to dataset size!") ;
//        }
        
    }

    public final void setRandom(Random random) {
        this.random = random;
    }

    public final Random getRandom() {
        return random;
    }

    protected void handleSubsetMinimumSizeSet() throws CoreHunterException {
        if (subsetMinimumSize <= 0) {
            throw new CoreHunterException("Subset minimum size must be greater than zero!");
        }
    }

    protected void handleSubsetMaximumSizeSet() throws CoreHunterException {
        if (subsetMaximumSize <= 0) {
            throw new CoreHunterException("Subset maximum size must be greater than zero!");
        }
    }

    protected boolean isBetterNeighbour(boolean isMinimizing, double neighbourEvaluation, double curBestNeighbourEvaluation) {
        return isMinimizing ? neighbourEvaluation < curBestNeighbourEvaluation : neighbourEvaluation > curBestNeighbourEvaluation;
    }

    protected double getWorstEvaluation(boolean isMinimizing) {
        return isMinimizing ? Double.MAX_VALUE : Double.MIN_VALUE;
    }

}
