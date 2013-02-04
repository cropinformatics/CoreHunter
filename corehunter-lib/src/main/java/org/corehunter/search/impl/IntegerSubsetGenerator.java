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
import java.util.List;

import org.apache.commons.math.util.MathUtils;
import org.corehunter.CoreHunterException;
import org.corehunter.search.SubsetGenerator;

/**
 * @author hermandebeukelaer
 */
public class IntegerSubsetGenerator implements SubsetGenerator<Integer>
{
	private List<Integer> indices ;
	private int	subsetSize = INVALID_SIZE;

	public IntegerSubsetGenerator()
	{

	}
	
	@Override
  public List<Integer> getIndices()
  {
	  return indices;
  }

	@Override
  public void setIndices(List<Integer> indices) throws CoreHunterException
  {
		if (indices != null)
			this.indices = indices ;
		else
			throw new CoreHunterException("Indices must be defined!") ;
  }

	@Override
  public int getSubsetSize()
  {
	  return subsetSize ;
  }

	@Override
  public void setSubsetSize(int subsetSize) throws CoreHunterException
  {
		if (subsetSize > 0)
			this.subsetSize = subsetSize ;
		else
			throw new CoreHunterException("Subset size must be greater than zero!") ;
  }

	/* (non-Javadoc)
   * @see org.corehunter.search.SubsetGenerator#getNumberOfKSubsets()
   */
	@Override
  public long getNumberOfSubsets()
	{
		return MathUtils.binomialCoefficient(indices.size(), subsetSize);
	}

	/* (non-Javadoc)
   * @see org.corehunter.search.SubsetGenerator#first()
   */
	@Override
  public List<Integer> first()
	{
		// Generate first k-subset
		List<Integer> first = new ArrayList<Integer>(subsetSize);
		
		for (int i = 0; i < subsetSize; i++)
		{
			first.add(i) ;
		}
		
		return first;
	}

	/* (non-Javadoc)
   * @see org.corehunter.search.SubsetGenerator#next(java.util.List)
   */
	@Override
  public List<Integer> next(List<Integer> subset)
	{
		Integer[] S = new Integer[subsetSize + 2];
		S[0] = 0; // t_0
		for (int i = 1; i < subsetSize + 1; i++)
		{
			S[i] = subset.get(i - 1) + 1;
		}
		
		S[subsetSize + 1] = subsetSize + 1; // t_{k+1}

		int j = 1;
		while (j <= subsetSize && S[j] == j)
		{
			j++;
		}
		if (subsetSize % 2 != j % 2)
		{
			if (j == 1)
			{
				S[1]--;
			}
			else
			{
				S[j - 1] = j;
				S[j - 2] = j - 1;
			}
		}
		else
		{
			if (S[j + 1] != S[j] + 1)
			{
				S[j - 1] = S[j];
				S[j] = S[j] + 1;
			}
			else
			{
				S[j + 1] = S[j];
				S[j] = j;
			}
		}

		for (int i = 1; i < subsetSize + 1; i++)
		{
			subset.set(i - 1, S[i] - 1 ) ;
		}
		
		return subset ;
	}
	
	@Override
	public void validate() throws CoreHunterException 
	{
		if (subsetSize > 0)
		{
			if (indices  != null)
			{
				if (subsetSize >= indices.size())
					throw new CoreHunterException("Subset size must be less than set size!") ;
			}
			else
			{
				throw new CoreHunterException("Indices must be defined, please set them first!") ;
			}
		}
		else
		{
			throw new CoreHunterException("Subset size must be greater than zero!") ;
		}
	}
}
