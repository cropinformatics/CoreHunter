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
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.search.PreferredSize;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.SubsetSearch;
import org.corehunter.search.solution.SubsetSolution;

public abstract class AbstractSubsetNeighbourhoodSearch<
	IndexType,
        SolutionType extends SubsetSolution<IndexType>,
        DatasetType extends IndexedData<IndexType>,
        NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
            extends AbstractNeighbourhoodSearch<SolutionType, DatasetType, IndexedMove<IndexType, SolutionType>, NeighbourhoodType>
            implements SubsetSearch<IndexType, SolutionType>
{

    public AbstractSubsetNeighbourhoodSearch() {
        super();
    }
    
    protected AbstractSubsetNeighbourhoodSearch(AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search) throws CoreHunterException {
        super(search);
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
        if(getSubsetPreferredSize() == PreferredSize.LARGEST && sizeDelta > 0
                || getSubsetPreferredSize() == PreferredSize.SMALLEST && sizeDelta < 0){
            return true;
        } else {
            // size not better (or don't care about size)
            return false;
        }
    }

    @Override
    public final int getSubsetMinimumSize() {
        return getNeighbourhood() != null ? getNeighbourhood().getSubsetMinimumSize() : INVALID_SIZE;
    }

    @Override
    public final void setSubsetMinimumSize(int subsetMinimumSize) throws CoreHunterException {
        if (getNeighbourhood() != null) {
            getNeighbourhood().setSubsetMinimumSize(subsetMinimumSize);
            handleSubsetMinimumSizeSet();
        } else {
            throw new CoreHunterException("Neighbourhood undefined!");
        }
    }

    @Override
    public final int getSubsetMaximumSize() {
        return getNeighbourhood() != null ? getNeighbourhood().getSubsetMaximumSize() : INVALID_SIZE;
    }

    @Override
    public final void setSubsetMaximumSize(int subsetMaximumSize) throws CoreHunterException {
        if (getNeighbourhood() != null) {
            getNeighbourhood().setSubsetMaximumSize(subsetMaximumSize);
            handleSubsetMaximumSizeSet();
        } else {
            throw new CoreHunterException("Neighbourhood undefined!");
        }
    }
    
    @Override
    public final PreferredSize getSubsetPreferredSize(){
        return getNeighbourhood() != null ? getNeighbourhood().getSubsetPreferredSize() : null;
    }
    
    @Override
    public final void setSubsetPreferredSize(PreferredSize size) throws CoreHunterException {
        if(getNeighbourhood() != null){
            getNeighbourhood().setSubsetPreferredSize(size);
            handleSubsetPreferredSizeSet();
        } else {
            throw new CoreHunterException("Neighbourhood undefined!");
        }
    }
    
    protected void handleSubsetMinimumSizeSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Subset minimum size can not be set while search in process");
        }
    }

    protected void handleSubsetMaximumSizeSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Subset maximum size can not be set while search in process");
        }
    }
    
    protected void handleSubsetPreferredSizeSet() throws CoreHunterException {
        if (SearchStatus.STARTED.equals(getStatus())) {
            throw new CoreHunterException("Subset preferred size can not be set while search in process");
        }
    }

    @Override
    protected void validate() throws CoreHunterException {
        
        super.validate();
        
        if(getNeighbourhood() == null){
            throw new CoreHunterException("Neighbourhood undefine!");
        }
        
        // check min/max subset size compared to full dataset size
        if (getNeighbourhood().getSubsetMinimumSize() >= getData().getSize()) {
            throw new CoreHunterException("Subset minimum size must be less than the dataset size!");
        }
        if (getNeighbourhood().getSubsetMaximumSize() > getData().getSize()) {
            throw new CoreHunterException("Subset maximum size must be less than or equal to the dataset size!");
        }
        
        int size = getCurrentSolution().getSubsetSize();

        // ensure initial solution is within maximum and minimum
        if (size < getNeighbourhood().getSubsetMinimumSize()) {
            // randomly increase subset until it reaches minimum
            for (int i = 0; i < getNeighbourhood().getSubsetMinimumSize()-size; i++) {
                getCurrentSolution().addRandomIndex(getRandom());
            }
        } else if (size > getNeighbourhood().getSubsetMaximumSize()) {
            // randomly decrease subset until it reaches maximum
            for (int i = 0; i < size - getNeighbourhood().getSubsetMaximumSize(); i++) {
                getCurrentSolution().removeRandomIndex(getRandom());
            }
        }
    }
}
