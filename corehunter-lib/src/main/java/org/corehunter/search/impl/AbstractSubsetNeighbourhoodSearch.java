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

    @Override
    public final int getSubsetMinimumSize() {
        return getNeighbourhood() != null ? getNeighbourhood().getSubsetMinimumSize() : INVALID_SIZE;
    }

    @Override
    public final void setSubsetMinimumSize(int subsetMinimumSize) throws CoreHunterException {
        if (getNeighbourhood() != null) {
            getNeighbourhood().setSubsetMinimumSize(subsetMinimumSize);
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
        } else {
            throw new CoreHunterException("Neighbourhood undefined!");
        }
    }

    @Override
    protected void validate() throws CoreHunterException {
        
        super.validate();
        
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
