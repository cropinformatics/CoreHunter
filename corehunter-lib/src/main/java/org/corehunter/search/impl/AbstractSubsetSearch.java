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
import org.corehunter.search.SearchStatus;
import org.corehunter.search.SubsetSearch;
import org.corehunter.search.solution.SubsetSolution;

public abstract class AbstractSubsetSearch<IndexType, SolutionType extends SubsetSolution<IndexType>, DatasetType extends IndexedData<IndexType>>
        extends AbstractObjectiveSearch<SolutionType, DatasetType>
        implements SubsetSearch<IndexType, SolutionType> {

    private int subsetMinimumSize = INVALID_SIZE;
    private int subsetMaximumSize = INVALID_SIZE;

    public AbstractSubsetSearch() {
        super();
    }

    protected AbstractSubsetSearch(AbstractSubsetSearch<IndexType, SolutionType, DatasetType> search) throws CoreHunterException {
        super(search);
        setSubsetMinimumSize(search.getSubsetMinimumSize());
        setSubsetMaximumSize(search.getSubsetMaximumSize());
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
    protected void validate() throws CoreHunterException {
        super.validate();

        if (subsetMinimumSize <= 0) {
            throw new CoreHunterException("Subset minimum size must be greater than zero!");
        }

        if (subsetMaximumSize <= 0) {
            throw new CoreHunterException("Subset maximum size must be greater than zero!");
        }

        if (subsetMinimumSize > getData().getSize()) {
            throw new CoreHunterException("Subset minimum size must be less than the dataset size!");
        }

        if (subsetMaximumSize > getData().getSize()) {
            throw new CoreHunterException("Subset maximum size must be less than the dataset size!");
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

}
