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

package org.corehunter.search.impl;

import static org.corehunter.Constants.INVALID_SIZE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.math.util.MathUtils;
import org.corehunter.CoreHunterException;
import org.corehunter.search.SubsetGenerator;

/**
 * Generates all possible subsets of a given set of integers, of fixed size.
 * Implementation is NOT synchronized.
 * 
 * @author hermandebeukelaer
 */
public class IntegerSubsetGenerator implements SubsetGenerator<Integer>
{
	private List<Integer> completeSet;
	private int subsetSize = INVALID_SIZE;
        private int[] curSelectedIndices; // indices of currently selected elements
                                          // of list representation of complete set,
                                          // not to be confused with the selected elements
                                          // themselves
        private int generated = 0;
        private long cachedNumberOfSubsets = -1;

	public IntegerSubsetGenerator()
	{
                
	}
	
	@Override
  public List<Integer> getCompleteSet()
  {
	  return completeSet;
  }

        /**
         * Specify complete set from which subsets are constructed.
         * Restarts the generation.
         *
         * @param completeSet
         * @throws CoreHunterException 
         */
	@Override
  public void setCompleteSet(Collection<Integer> completeSet) throws CoreHunterException
  {
		if (completeSet != null){
                    this.completeSet = new ArrayList<Integer>(completeSet) ;
                    restart();
                } else {
                    throw new CoreHunterException("Complete set must be defined!") ;
                }
  }

	@Override
  public int getSubsetSize()
  {
	  return subsetSize ;
  }

        /**
         * Specify the desired subset size. Restarts the generation.
         * 
         * @param subsetSize
         * @throws CoreHunterException 
         */
	@Override
  public void setSubsetSize(int subsetSize) throws CoreHunterException
  {
		if (subsetSize > 0){
                    this.subsetSize = subsetSize ;
                    restart();
                } else {
                    throw new CoreHunterException("Subset size must be greater than zero!") ;
                }
  }
        
        @Override
        public void restart() {
            generated = 0;
            curSelectedIndices = null;
            cachedNumberOfSubsets = -1; // flush cache
        }
        
	@Override
        public long getNumberOfSubsets() throws CoreHunterException
	{
                validate();
                // check if cached value expired
                if(cachedNumberOfSubsets == -1){
                    cachedNumberOfSubsets = MathUtils.binomialCoefficient(completeSet.size(), subsetSize);
                }
                return cachedNumberOfSubsets;
	}

        @Override
        public boolean hasNext() throws CoreHunterException {
            // validate generator
            validate();
            // check if not all subsets have been generated
            return generated < getNumberOfSubsets();
        }
        
	/**
         * Get first subset.
         * 
         * @return
         * @throws CoreHunterException 
         */
        private List<Integer> first()
	{
		// Generate first subset
		List<Integer> first = new ArrayList<Integer>(subsetSize);
                curSelectedIndices = new int[subsetSize];
		
		for (int i = 0; i < subsetSize; i++)
		{
			first.add(completeSet.get(i)) ;
                        curSelectedIndices[i] = i;
		}
		
                generated++;
		return first;
	}

	/**
         * Get the next subset.
         * 
         * @param subset
         * @return
         * @throws CoreHunterException 
         */
	@Override
        public List<Integer> next() throws CoreHunterException
	{
            
                // validate generator
                validate();
                
                // check if next subset exists
                if(!hasNext()){
                    throw new NoSuchElementException("No next subset; all subsets have been generated");
                }
                
                // check if first call
                if(generated == 0){
                    return first();
                }
                
                // not first call; generate next subset
                
		Integer[] tmp = new Integer[subsetSize + 2];
		tmp[0] = 0;
		for (int i = 1; i < subsetSize + 1; i++)
		{
			tmp[i] = curSelectedIndices[i - 1] + 1;
		}
		
		tmp[subsetSize + 1] = subsetSize + 1;

		int j = 1;
		while (j <= subsetSize && tmp[j] == j)
		{
			j++;
		}
		if (subsetSize % 2 != j % 2)
		{
			if (j == 1)
			{
				tmp[1]--;
			}
			else
			{
				tmp[j - 1] = j;
				tmp[j - 2] = j - 1;
			}
		}
		else
		{
			if (tmp[j + 1] != tmp[j] + 1)
			{
				tmp[j - 1] = tmp[j];
				tmp[j] = tmp[j] + 1;
			}
			else
			{
				tmp[j + 1] = tmp[j];
				tmp[j] = j;
			}
		}

		List<Integer> next = new ArrayList<Integer>(subsetSize);
                
                for (int i = 1; i < subsetSize + 1; i++)
		{
                        curSelectedIndices[i-1] = tmp[i]-1;
                        next.add(completeSet.get(curSelectedIndices[i-1]));
		}
		
                generated++;
		return next;
	}
        
        
	
	@Override
	public void validate() throws CoreHunterException 
	{
		if (subsetSize > 0)
		{
			if (completeSet  != null)
			{
				if (subsetSize > completeSet.size())
					throw new CoreHunterException("Subset size must be less than or equal to set size!") ;
			}
			else
			{
				throw new CoreHunterException("Complete set must be defined, please set it first!") ;
			}
		}
		else
		{
			throw new CoreHunterException("Subset size must be greater than zero!") ;
		}
	}

    @Override
    public SubsetGenerator<Integer> copy() throws CoreHunterException {
        validate();
        // create new generator with same complete set and subset size
        SubsetGenerator<Integer> copy = new IntegerSubsetGenerator();
        copy.setCompleteSet(completeSet);
        copy.setSubsetSize(subsetSize);
        return copy;
    }

    

    
}
