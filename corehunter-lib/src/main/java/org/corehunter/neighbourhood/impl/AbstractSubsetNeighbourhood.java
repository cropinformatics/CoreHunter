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

import static org.corehunter.Constants.INVALID_SIZE;

import java.util.Random;

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.PreferredSize;
import org.corehunter.search.solution.SubsetSolution;

/**
 * Implements an abstract neighbourhood which defines the neighbours of a given
 * solution. Depending on the chosen algorithm that uses the neighbourhood,
 * one can generate a random neighbour or the best one.
 */
public abstract class AbstractSubsetNeighbourhood<IndexType, SolutionType extends SubsetSolution<IndexType>> implements SubsetNeighbourhood<IndexType, SolutionType> {

    private Random random = new Random();
    private int subsetMinimumSize = INVALID_SIZE;
    private int subsetMaximumSize = INVALID_SIZE;
    private PreferredSize subsetPreferredSize = PreferredSize.DONT_CARE;

    public AbstractSubsetNeighbourhood() {
    }

    protected AbstractSubsetNeighbourhood(AbstractSubsetNeighbourhood<IndexType, SolutionType> singleneighbourhood) throws CoreHunterException {
        setSubsetMinimumSize(singleneighbourhood.getSubsetMinimumSize());
        setSubsetMaximumSize(singleneighbourhood.getSubsetMaximumSize());
        setSubsetPreferredSize(singleneighbourhood.getSubsetPreferredSize());
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
    public final void setSubsetMaximumSize(int subsetMaximumSize) throws CoreHunterException {
        if (this.subsetMaximumSize != subsetMaximumSize) {
            this.subsetMaximumSize = subsetMaximumSize;
            handleSubsetMaximumSizeSet();
        }
    }
    
    @Override 
    public final PreferredSize getSubsetPreferredSize(){
        return subsetPreferredSize;
    }
    
    @Override
    public final void setSubsetPreferredSize(PreferredSize size){
        if (this.subsetPreferredSize != size){
            this.subsetPreferredSize = size;
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

    protected double getDeltaScore(boolean isMinimizing, double neighbourEvaluation, double curBestNeighbourEvaluation){
        return isMinimizing ? curBestNeighbourEvaluation - neighbourEvaluation : neighbourEvaluation - curBestNeighbourEvaluation;
    }
    
    protected boolean isBetterNeighbour(boolean isMinimizing, double neighbourEvaluation,
                                            double curBestNeighbourEvaluation, int neighbourSize, int curBestNeighbourSize) {
        double delta = getDeltaScore(isMinimizing, neighbourEvaluation, curBestNeighbourEvaluation);
        if(delta > 0){
            // better score
            return true;
        } else if (delta == 0) {
            // equal score: check size to break tie
            return isBetterSize(neighbourSize, curBestNeighbourSize);
        } else {
            // worse score
            return false;
        }
    }
    
    private boolean isBetterSize(int newSize, int oldSize){
        int sizeDelta = newSize - oldSize;
        if(getSubsetPreferredSize() == PreferredSize.LARGEST && sizeDelta > 0
                || getSubsetPreferredSize() == PreferredSize.SMALLEST && sizeDelta < 0){
            return true;
        } else {
            // size not better (or don't care about size)
            return false;
        }
    }
    
    protected int getWorstSize(){
        if (getSubsetPreferredSize() == PreferredSize.LARGEST){
            return Integer.MIN_VALUE;
        } else if (getSubsetPreferredSize() == PreferredSize.SMALLEST){
            return Integer.MAX_VALUE;
        } else {
            return INVALID_SIZE;
        }
    }

    protected double getWorstEvaluation(boolean isMinimizing) {
        return isMinimizing ? Double.MAX_VALUE : Double.MIN_VALUE;
    }

}
