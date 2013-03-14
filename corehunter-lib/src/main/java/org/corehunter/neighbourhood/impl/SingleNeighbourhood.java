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

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.search.solution.SubsetSolution;

/**
 * A subset neighbourhood that adds, removes or swaps one single index.
 */
public abstract class SingleNeighbourhood<IndexType, SolutionType extends SubsetSolution<IndexType>>
        extends AbstractSubsetNeighbourhood<IndexType, SolutionType> {

    public SingleNeighbourhood() {
    }

    protected SingleNeighbourhood(SingleNeighbourhood<IndexType, SolutionType> singleneighbourhood) throws CoreHunterException {
        super(singleneighbourhood);
    }
    
    @Override
    public IndexedMove<IndexType, SolutionType> performRandomMove(SolutionType solution) throws CoreHunterException {
        // randomly perturb selected indices
        if (solution.getRemainingSize() == 0) {
            // solution currently contains ALL indices, only option is remove
            if(solution.getSubsetSize() > getSubsetMinimumSize()){
                // remove possible
                return removeRandom(solution);
            } else {
                // remove not possible: no neighbours!
                return null;
            }
        } else {
            double p = getRandom().nextDouble();
            if (p >= 0.66 && solution.getSubsetSize() < getSubsetMaximumSize()) {
                return addRandom(solution);
            } else if (p >= 0.33 && solution.getSubsetSize() > getSubsetMinimumSize()) {
                return removeRandom(solution);
            } else {
                return swapRandom(solution);
            }
        }
    }

    private IndexedMove<IndexType, SolutionType> swapRandom(SolutionType solution) {
        // randomly swap one item
        IndexType[] indices = solution.swapRandomIndices(getRandom());
        return new SwapMove<IndexType, SolutionType>(indices[0], indices[1]);
    }

    private IndexedMove<IndexType, SolutionType> addRandom(SolutionType solution) {
        // randomly add one item
        IndexType addIndex = solution.addRandomIndex(getRandom());
        return new AdditionMove<IndexType, SolutionType>(addIndex);
    }

    private IndexedMove<IndexType, SolutionType> removeRandom(SolutionType solution) {
        // randomly remove one item
        IndexType removeIndex = solution.removeRandomIndex(getRandom());
        return new DeletionMove<IndexType, SolutionType>(removeIndex);
    }

}
