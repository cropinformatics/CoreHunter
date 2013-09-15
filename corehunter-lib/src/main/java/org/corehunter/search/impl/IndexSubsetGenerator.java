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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.math.util.MathUtils;
import org.corehunter.CoreHunterException;
import org.corehunter.search.SubsetGenerator;
import static org.corehunter.Constants.INVALID_SIZE ;

/**
 * Generates all possible subsets of a given set of indices, of fixed size.
 * Implementation is NOT synchronized.
 * 
 * @author hermandebeukelaer
 */
public class IndexSubsetGenerator<IndexType> implements SubsetGenerator<IndexType>
{
	private List<IndexType> completeSet;
	private int subsetSize = INVALID_SIZE;
        private int[] curSelectedIndices; // indices (int) of currently selected elements
                                          // of list representation of complete set,
                                          // not to be confused with the selected
                                          // elements (of type IndexType) themselves
        private long cachedNumberOfSubsets = -1;

	public IndexSubsetGenerator()
	{
                
	}
	
	@Override
  public List<IndexType> getCompleteSet()
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
  public void setCompleteSet(Collection<IndexType> completeSet) throws CoreHunterException
  {
		if (completeSet != null){
                    this.completeSet = new ArrayList<IndexType>(completeSet) ;
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
            // not completed if no subsets generated yet
            if(curSelectedIndices == null){
                return true;
            }
            // else, check if not all subsets have been generated:
            // selected indices of last subset are
            //  [0, 1, 2, ..., n-1]
            // with n the size of the complete set
            boolean finalIndices = true;
            int i=0;
            while(finalIndices && i<subsetSize-1){
                finalIndices = (curSelectedIndices[i] == i);
                i++;
            }
            finalIndices = (finalIndices && curSelectedIndices[subsetSize-1] == completeSet.size()-1);
            // has next if not final indices
            return !finalIndices;
        }
        
	/**
         * Get first subset.
         * 
         * @return
         * @throws CoreHunterException 
         */
        private List<IndexType> first()
	{
		// Generate first subset
		List<IndexType> first = new ArrayList<IndexType>(subsetSize);
                curSelectedIndices = new int[subsetSize];
		
		for (int i = 0; i < subsetSize; i++)
		{
			first.add(completeSet.get(i)) ;
                        curSelectedIndices[i] = i;
		}
		
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
        public List<IndexType> next() throws CoreHunterException
	{
            
                // validate generator
                validate();
                
                // check if next subset exists
                if(!hasNext()){
                    throw new NoSuchElementException("No next subset; all subsets have been generated");
                }
                
                // check if first call
                if(curSelectedIndices == null){
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

		List<IndexType> next = new ArrayList<IndexType>(subsetSize);
                
                for (int i = 1; i < subsetSize + 1; i++)
		{
                        curSelectedIndices[i-1] = tmp[i]-1;
                        next.add(completeSet.get(curSelectedIndices[i-1]));
		}
		
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
    public SubsetGenerator<IndexType> copy() throws CoreHunterException {
        validate();
        // create new generator with same complete set and subset size
        SubsetGenerator<IndexType> copy = new IndexSubsetGenerator<IndexType>();
        copy.setCompleteSet(completeSet);
        copy.setSubsetSize(subsetSize);
        return copy;
    }

    

    
}
