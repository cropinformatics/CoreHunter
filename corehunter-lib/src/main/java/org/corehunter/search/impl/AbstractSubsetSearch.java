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

import static org.corehunter.Constants.INVALID_SIZE;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.search.PreferredSize;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.SubsetSearch;
import org.corehunter.search.solution.SubsetSolution;

public abstract class AbstractSubsetSearch<IndexType, SolutionType extends SubsetSolution<IndexType>, DatasetType extends IndexedData<IndexType>>
        extends AbstractObjectiveSearch<SolutionType, DatasetType>
        implements SubsetSearch<IndexType, SolutionType> {

    private int subsetMinimumSize = INVALID_SIZE;
    private int subsetMaximumSize = INVALID_SIZE;
    private PreferredSize subsetPreferredSize = PreferredSize.DONT_CARE;

    public AbstractSubsetSearch() {
        super();
    }

    protected AbstractSubsetSearch(AbstractSubsetSearch<IndexType, SolutionType, DatasetType> search) throws CoreHunterException {
        super(search);
        setSubsetMinimumSize(search.getSubsetMinimumSize());
        setSubsetMaximumSize(search.getSubsetMaximumSize());
        setSubsetPreferredSize(search.getSubsetPreferredSize());
    }
    
    /**
     * Check for new best solution based on both its evaluation and its size, where
     * the size is used to break ties according to the preferred subset size setting.
     * 
     * @param evaluation
     * @param size
     * @return 
     */
    protected boolean isNewBestSolution(double evaluation, int size){
        if(isNewBestSolution(evaluation)){
            // better solution based on evaluation
            return true;
        } else if (getDeltaScore(evaluation, getBestSolutionEvaluation()) >= 0) {
            // evaluation not worse: break tie based on size
            return isBetterSize(size, getBestSolution().getSubsetSize());
        } else {
            // worse evaluation
            return false;
        }
    }
    
    /**
     * Compare two different solutions based on both evaluation and size, where the
     * size is used to break ties according to the preferred subset size setting.
     * 
     * @param newEvaluation
     * @param oldEvaluation
     * @param newSize
     * @param oldSize
     * @return 
     */
    protected boolean isBetterSolution(double newEvaluation, double oldEvaluation, int newSize, int oldSize){
        if(isBetterSolution(newEvaluation, oldEvaluation)){
            // better based on evaluation 
            return true;
        } else if (getDeltaScore(newEvaluation, oldEvaluation) >= 0) {
            // evaluation not worse: check sizes to break tie
            return isBetterSize(newSize, oldSize);
        } else {
            // worse evaluation
            return false;
        }
    }
    
    private boolean isBetterSize(int newSize, int oldSize){
        int sizeDelta = newSize - oldSize;
        if(subsetPreferredSize == PreferredSize.LARGEST && sizeDelta > 0
                || subsetPreferredSize == PreferredSize.SMALLEST && sizeDelta < 0){
            return true;
        } else {
            // size not better (or don't care about size)
            return false;
        }
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
    public final void setSubsetPreferredSize(PreferredSize size) throws CoreHunterException{
        if (this.subsetPreferredSize != size){
            this.subsetPreferredSize = size;
            handleSubsetPreferredSizeSet();
        }
    }

    @Override
    protected void validate() throws CoreHunterException {
        super.validate();
        if (subsetMinimumSize <= 0) {
            throw new CoreHunterException("Subset minimum size must be greater than zero!");
        }
        if (subsetMaximumSize <= 0) {
            throw new CoreHunterException("Subset maximum size must be greater than zero!");
        }
        if (subsetMinimumSize >= getData().getSize()) {
            throw new CoreHunterException("Subset minimum size must be less than the dataset size!");
        }
        if (subsetMaximumSize > getData().getSize()) {
            throw new CoreHunterException("Subset maximum size must be less than or equal to the dataset size!");
        }
        if (subsetMaximumSize < subsetMinimumSize) {
            throw new CoreHunterException("Subset maximum size must be greater then or equal to minimum size!");
        }
    }

    protected void handleSubsetMinimumSizeSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Subset minimum size can not be set while search in process");
        }
        if (subsetMinimumSize <= 0) {
            throw new CoreHunterException("Subset minimum size must be greater than zero!");
        }
    }

    protected void handleSubsetMaximumSizeSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Subset maximum size can not be set while search in process");
        }
        if (subsetMaximumSize <= 0) {
            throw new CoreHunterException("Subset maximum size must be greater than zero!");
        }
    }
    
    protected void handleSubsetPreferredSizeSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Subset preferred size can not be set while search in process");
        }
    }

}
