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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import static org.corehunter.Constants.INVALID_SIZE;
import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.neighbourhood.impl.AdditionMove;
import org.corehunter.neighbourhood.impl.DeletionMove;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.SubsetSolution;

public class LRSearch<IndexType, SolutionType extends SubsetSolution<IndexType>>
                                        extends AbstractSubsetSearch<IndexType, SolutionType> {

    private int l = INVALID_SIZE;
    private int r = INVALID_SIZE;
    private boolean continueSearch = true;
    
    public LRSearch() {
        super();
    }
    
    protected LRSearch(LRSearch<IndexType, SolutionType> search) throws CoreHunterException {
        super(search);
        setL(search.getL());
        setR(search.getR());
    }
    
    @Override
    public Search<SolutionType> copy() throws CoreHunterException {
        return new LRSearch<IndexType, SolutionType>(this);
    }
    
    public final int getL() {
        return l;
    }
    
    public final void setL(int l) throws CoreHunterException {
        if (this.l != l) {
            this.l = l;
            handleLSet();
        }
    }
    
    public final int getR() {
        return r;
    }
    
    public final void setR(int r) throws CoreHunterException {
        if (this.r != r) {
            this.r = r;
            handleRSet();
        }
    }
    
    protected void handleLSet() throws CoreHunterException {        
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("L can not be set while search in process");
        }
        if (l < 0) {
            throw new CoreHunterException("L can not be less than zero!");
        }
    }
    
    protected void handleRSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("R can not be set while search in process");
        }
        if (r < 0) {
            throw new CoreHunterException("R can not be less than zero!");
        }
    }
    
    @Override
    protected void validate() throws CoreHunterException {
        super.validate();
        
        // check L and R
        if(l < 0){
            throw new CoreHunterException("L can not be less than zero");
        }
        if(r < 0){
            throw new CoreHunterException("R can not be less than zero");
        }
        if(l == r){
            throw new CoreHunterException("L and R can not be equal");
        }
        
        // if subset size is increasing, initial size can not be too large
        if(increasingSubsetSize() && getCurrentSolution().getSubsetSize() > getSubsetMaximumSize()){
            throw new CoreHunterException("L > R (increasing subset size): initial subset size can not be larger than maximum subset size");
        }
        
        // if subset size is decreasing, initial size can not be too small
        if(decreasingSubsetSize() && getCurrentSolution().getSubsetSize() < getSubsetMinimumSize()){
            throw new CoreHunterException("L < R (decreasing subset size): initial subset size can not be smaller than minimum subset size");
        }
        
    }
    
    /**
     * Check whether subset size is increasing (L > R).
     */
    private boolean increasingSubsetSize(){
        return l > r;
    }
    
    /**
     * Check whether subset size is decreasing (R > L).
     */
    private boolean decreasingSubsetSize(){
        return r > l;
    }
    
    @Override
    protected void runSearch() throws CoreHunterException {
//        continueSearch = true;
//        
//        double evaluation, newEvaluation, bestNewEvaluation, deltaEvaluation;
//        
//        IndexType bestAddIndex = null;
//        @SuppressWarnings("unused")
//        IndexType bestRemIndex = null; // TO DO why not used ??
//        Stack<IndexedMove<IndexType, SolutionType>> history = new Stack<IndexedMove<IndexType, SolutionType>>();
//        
//        boolean skipadd = false;
//        
//        if (l > r) {
//            // Run 'seed engine' to create initial solution
//            //executeSeedEngine();
//        } else {
//            // Start with full set, stepwise decrease size
//            getCurrentSolution().addAllIndices();
//            skipadd = true;
//        }
//        
//        // check if at least 2 indices contained in subset
//        if(getCurrentSolution().getSize() < 2) {
//            throw new CoreHunterException("Seed engine should select at least 2 indices");
//        }
//        
//        evaluation = getObjectiveFunction().calculate(getCurrentSolution());
//        bestNewEvaluation = evaluation;
//        handleNewBestSolution(getCurrentSolution(), bestNewEvaluation);
//
//        // Determine whether to continue search
//        if (l > r) {
//            // Increasing core size
//            if (getCurrentSolution().getSubsetSize() >= getSubsetMinimumSize()) {
//                continueSearch = false; // Equal or worse evaluation and size increased
//            } else {
//                if (getCurrentSolution().getSubsetSize() + l - r > getSubsetMaximumSize()) {
//                    continueSearch = false; // Maximum size reached
//                }
//            }
//        } else {
//            // Decreasing core size
//            if (getCurrentSolution().getSubsetSize() <= getSubsetMaximumSize()) {
//                continueSearch = false; // Worse evaluation
//            } else {
//                if (getCurrentSolution().getSubsetSize() + l - r < getSubsetMinimumSize()) {
//                    continueSearch = false; // Minimum size reached
//                }
//            }
//        }
//        
//        while (continueSearch) {
//            // Add l new accessions to core
//            if (!skipadd) {
//                for (int i = 0; i < l; i++) {
//                    List<IndexType> unselected = new ArrayList<IndexType>(getCurrentSolution().getRemainingIndices());
//
//                    // Search for best new accession
//                    bestNewEvaluation = getWorstEvaluation();                    
//                    
//                    Iterator<IndexType> iterator = unselected.iterator();
//                    IndexType index;
//                    
//                    while (iterator.hasNext()) {
//                        index = iterator.next();
//                        getCurrentSolution().addIndex(index);
//                        newEvaluation = getObjectiveFunction().calculate(getCurrentSolution());
//                        
//                        if (isBetterSolution(newEvaluation, bestNewEvaluation)) {
//                            bestNewEvaluation = newEvaluation;
//                            bestAddIndex = index;
//                        }
//                        
//                        getCurrentSolution().removeIndex(index);
//                    }
//                    // Add best new accession
//                    getCurrentSolution().addIndex(bestAddIndex);
//                    history.add(new AdditionMove<IndexType, SolutionType>(bestAddIndex));
//                }
//                skipadd = false;
//            }
//            // Remove r accessions from core
//            for (int i = 0; i < r; i++) {
//                // Search for worst accession
//                bestNewEvaluation = getWorstEvaluation();                
//                
//                List<IndexType> selected = new ArrayList<IndexType>(getCurrentSolution().getSubsetIndices());
//                
//                Iterator<IndexType> iterator = selected.iterator();
//                IndexType index;
//                
//                while (iterator.hasNext()) {
//                    index = iterator.next();
//                    getCurrentSolution().removeIndex(index);
//                    newEvaluation = getObjectiveFunction().calculate(getCurrentSolution());
//                    
//                    if (isBetterSolution(newEvaluation, bestNewEvaluation)) {
//                        bestNewEvaluation = newEvaluation;
//                        bestRemIndex = index;
//                    }
//                    
//                    getCurrentSolution().addIndex(index);
//                }
//
//                // Remove worst accession
//                getCurrentSolution().removeIndex(bestAddIndex);
//                history.add(new DeletionMove<IndexType, SolutionType>(bestAddIndex));
//            }
//            
//            deltaEvaluation = getDeltaScore(bestNewEvaluation, evaluation);
//            evaluation = bestNewEvaluation;
//
//            // Determine whether to continue search
//            if (l > r) {
//                // Increasing core size
//                if (getCurrentSolution().getSubsetSize() >= getSubsetMinimumSize() && deltaEvaluation <= 0) {
//                    continueSearch = false; // Equal or worse evaluation and size increased
//                    // Restore previous core
//                    for (int i = 0; i < l + r; i++) {
//                        history.pop().undo(getCurrentSolution());
//                    }
//                } else {
//                    if (getCurrentSolution().getSubsetSize() + l - r > getSubsetMaximumSize()) {
//                        continueSearch = false; // Maximum size reached
//                    }
//                }
//            } else {
//                // Decreasing core size
//                if (getCurrentSolution().getSubsetSize() <= getSubsetMaximumSize() && deltaEvaluation < 0) {
//                    continueSearch = false; // Worse evaluation
//                    // Restore previous core
//                    for (int i = 0; i < l + r; i++) {
//                        history.pop().undo(getCurrentSolution());
//                    }
//                } else {
//                    if (getCurrentSolution().getSubsetSize() + l - r < getSubsetMinimumSize()) {
//                        continueSearch = false; // Minimum size reached
//                    }
//                }
//            }
//            
//            handleNewBestSolution(getCurrentSolution(), evaluation);
//        }
    }
    
}
