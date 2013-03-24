//  Copyright 2012 Herman De Beukelaer, Guy Davenport
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.corehunter.search.solution.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.corehunter.CoreHunterException;
import org.corehunter.search.solution.Solution;
import org.corehunter.search.solution.SubsetSolution;

/**
 * The default, unordered integer subset solution.
 */
public class IntegerSubsetSolution implements SubsetSolution<Integer> {

    private Set<Integer> indices;
    private Set<Integer> subsetIndices;
    private Set<Integer> remainingIndices;
    
    public IntegerSubsetSolution(Collection<Integer> indices, Collection<Integer> subsetIndices) {
        this.indices = new HashSet<Integer>(indices.size());
        this.subsetIndices = new HashSet<Integer>(subsetIndices.size());
        this.remainingIndices = new HashSet<Integer>(indices.size()-subsetIndices.size());

        this.indices.addAll(indices);
        this.subsetIndices.addAll(subsetIndices);
        this.remainingIndices.addAll(indices);
        this.remainingIndices.removeAll(subsetIndices);
    }

    private IntegerSubsetSolution(SubsetSolution<Integer> subsetSolution) {
        this.indices = new HashSet<Integer>(subsetSolution.getIndices());
        this.subsetIndices = new HashSet<Integer>(subsetSolution.getSubsetIndices());
        this.remainingIndices = new HashSet<Integer>(subsetSolution.getRemainingIndices());
    }

    public Collection<Integer> getIndices() {
        return indices;
    }

    public int getSize() {
        return indices.size();
    }

    public Collection<Integer> getSubsetIndices() {
        return subsetIndices;
    }

    public void setSubsetIndices(Collection<Integer> subsetIndices) {
        this.subsetIndices.clear();
        this.subsetIndices.addAll(subsetIndices);
        remainingIndices.clear();
        remainingIndices.addAll(indices);
        remainingIndices.removeAll(subsetIndices);
    }

    public int getSubsetSize() {
        return subsetIndices.size();
    }

    public Collection<Integer> getRemainingIndices() {
        return remainingIndices;
    }

    public int getRemainingSize() {
        return remainingIndices.size();
    }

    public void addIndex(Integer index) {
        if(remainingIndices.remove(index)) {
            subsetIndices.add(index);
        }
    }

    public Integer addRandomIndex(Random random) {
        Iterator<Integer> it = remainingIndices.iterator();
        Integer index = it.next();
        int k = random.nextInt(getRemainingSize());
        for(int i=0; i<k; i++){
            index = it.next();
        }
        addIndex(index);
        return index;
    }

    public void addAllIndices() {
        subsetIndices.clear();
        remainingIndices.clear();
        subsetIndices.addAll(indices);
    }

    public void removeIndex(Integer index) {
        if(subsetIndices.remove(index)){
            remainingIndices.add(index);
        }
    }

    public Integer removeRandomIndex(Random random) {
        Iterator<Integer> it = subsetIndices.iterator();
        Integer index = it.next();
        int k = random.nextInt(getSubsetSize());
        for(int i=0; i<k; i++){
            index = it.next();
        }
        removeIndex(index);
        return index;
    }

    public void removeAllIndices() {
        subsetIndices.clear();
        remainingIndices.clear();
        remainingIndices.addAll(indices);
    }

    public void swapIndices(Integer indexToAdd, Integer indexToRemove) {
        if (subsetIndices.remove(indexToRemove)) {
            if (remainingIndices.remove(indexToAdd)) {
                subsetIndices.add(indexToAdd);
                remainingIndices.add(indexToRemove);
            } else {
                subsetIndices.add(indexToRemove); // revert
            }
        }
    }

    public Integer[] swapRandomIndices(Random random) {
        // randomly select index to add
        Iterator<Integer> itUnselected = remainingIndices.iterator();
        Integer indexToAdd = itUnselected.next();
        int k = random.nextInt(getRemainingSize());
        for(int i=0; i<k; i++){
            indexToAdd = itUnselected.next();
        }
        // randomly select index to remove
        Iterator<Integer> itSelected = subsetIndices.iterator();
        Integer indexToRemove = itSelected.next();
        k = random.nextInt(getSubsetSize());
        for(int i=0; i<k; i++){
            indexToRemove = itSelected.next();
        }
        // swap
        swapIndices(indexToAdd, indexToRemove);
        Integer[] swapIndices = new Integer[2];
        swapIndices[0] = indexToAdd;
        swapIndices[1] = indexToRemove;
        return swapIndices;
    }

    public boolean containsIndexInSubset(Integer index) {
        return subsetIndices.contains(index);
    }

    public Solution copy() {
        return new IntegerSubsetSolution(this);
    }

    public void validate() throws CoreHunterException {
        if (indices.isEmpty()) {
            throw new CoreHunterException("Set must contain at least one index!");
        }
        if (subsetIndices.isEmpty()) {
            throw new CoreHunterException("Subset must contain at least one index!");
        }
        if (!indices.containsAll(subsetIndices)) {
            throw new CoreHunterException("Subset indices must all be present in set!");
        }
        if (!indices.containsAll(remainingIndices)) {
            throw new CoreHunterException("Remaining indices must all be present in set!");
        }
    }
    
    @Override
    public String toString() {
        return "indices =" + subsetIndices;
    }

    @Override
    // Only checks subset indices!
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IntegerSubsetSolution other = (IntegerSubsetSolution) obj;
        if (subsetIndices == null) {
            if (other.subsetIndices != null) {
                return false;
            }
        } else if (!subsetIndices.equals(other.subsetIndices)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (subsetIndices != null ? subsetIndices.hashCode() : 0);
        return hash;
    }
    
}
