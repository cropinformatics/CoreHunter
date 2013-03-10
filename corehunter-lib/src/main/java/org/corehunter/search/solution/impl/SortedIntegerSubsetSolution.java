// Copyright 2013 Guy Davenport, Herman De Beukelaer
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
package org.corehunter.search.solution.impl;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.corehunter.CoreHunterException;
import org.corehunter.search.solution.Solution;
import org.corehunter.search.solution.SubsetSolution;

/**
 * Subset solution in which order of the indices in the subset are
 * maintained in their natural order
 * 
 * @author daveneti
 *
 */
public class SortedIntegerSubsetSolution implements SubsetSolution<Integer>
{
	
	private Collection<Integer> indices;
	private Collection<Integer> subsetIndices;
	private Collection<Integer> remainingIndices;

	public SortedIntegerSubsetSolution(Collection<Integer> indices, Collection<Integer> subsetIndices)
	{
		
	}
	
	public SortedIntegerSubsetSolution(SortedIntegerSubsetSolution subsetSolution)
	{
		
	}
	
	@Override
  public Solution copy()
  {
	  return new SortedIntegerSubsetSolution(this);
  }

	@Override
  public void validate() throws CoreHunterException
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public Collection<Integer> getIndices()
  {
	  return indices;
  }

	@Override
  public int getSize()
  {
	  return indices.size();
  }

	@Override
  public Collection<Integer> getSubsetIndices()
  {
	  return subsetIndices ;
  }

	@Override
  public void setSubsetIndices(List<Integer> subsetIndices)
  {
		this.subsetIndices.clear() ;
		this.subsetIndices.addAll(subsetIndices) ;
		remainingIndices.clear() ;
		remainingIndices.addAll(indices) ;
		remainingIndices.removeAll(subsetIndices) ;
  }

	@Override
  public int getSubsetSize()
  {
	  return indices.size();
  }

	@Override
  public Collection<Integer> getRemainingIndices()
  {
	  return remainingIndices ;
  }

	@Override
  public int getRemainingSize()
  {
	  return remainingIndices.size(); 
  }

	@Override
  public void addIndex(Integer index)
  {
	  if (remainingIndices.remove(index))
	  {
	  	subsetIndices.add(index) ;
	  }
  }

	@Override
  public Integer addRandomIndex(Random random)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void addAllIndices()
  {
		subsetIndices.clear() ;
		remainingIndices.clear() ;
		subsetIndices.addAll(indices) ;
  }

	@Override
  public void removeIndex(Integer index)
  {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public Integer removeRandomIndex(Random random)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void removeAllIndices()
  {
		subsetIndices.clear() ;
		remainingIndices.clear() ;
		remainingIndices.addAll(indices) ;
  }

	@Override
  public void swapIndices(Integer indexToAdd, Integer indexToRemove)
  {
	  if (subsetIndices.remove(indexToRemove))
	  {
		  if (remainingIndices.remove(indexToAdd))
		  {
		  	subsetIndices.add(indexToAdd) ;
		  	remainingIndices.add(indexToRemove) ;
		  }
		  else
		  {
		  	subsetIndices.add(indexToRemove) ; // revert
		  }
	  }
  }

	@Override
  public Integer[] swapRandomIndices(Random random)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  @Deprecated
  public int getPositionInSubset(Integer position)
  {
	  // TODO Auto-generated method stub
	  return 0;
  }

	@Override
  @Deprecated
  public Integer getIndexInSubsetAt(int position)
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public boolean containsIndexInSubset(Integer index)
  {
	  // TODO Auto-generated method stub
	  return false;
  }

	
}
