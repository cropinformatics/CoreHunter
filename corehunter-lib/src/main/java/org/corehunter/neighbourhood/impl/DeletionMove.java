// Copyright 2012 Herman De Beukelaer, Guy Davenport
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.corehunter.neighbourhood.RemovedIndexMove;
import org.corehunter.search.solution.SubsetSolution;

/**
 * 
 */
public class DeletionMove<IndexType, SolutionType extends SubsetSolution<IndexType>>
        implements RemovedIndexMove<IndexType, SolutionType> {

    private IndexType removedIndex;

    public DeletionMove(IndexType removedIndex) {
        this.removedIndex = removedIndex;
    }

    @Override
    public final IndexType getRemovedIndex() {
        return removedIndex;
    }
    
    @Override
    public final Collection<IndexType> getInvolvedIndices(){
        Set<IndexType> indices = new HashSet<IndexType>();
        indices.add(removedIndex);
        return indices;
    }

    @Override
    public void apply(SolutionType solution){
        solution.removeIndex(removedIndex);
    }
    
    @Override
    public void undo(SolutionType solution) {
        solution.addIndex(removedIndex);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object object) {
        if (object instanceof RemovedIndexMove) {
            return getRemovedIndex().equals(((RemovedIndexMove) object).getRemovedIndex());
        } else {
            return super.equals(object);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (getRemovedIndex() != null ? getRemovedIndex().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Add index=" + removedIndex;
    }
}
