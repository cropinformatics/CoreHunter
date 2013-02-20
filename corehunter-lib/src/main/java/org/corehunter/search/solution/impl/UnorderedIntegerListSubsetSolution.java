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
package org.corehunter.search.solution.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.set.ListOrderedSet;
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
public class UnorderedIntegerListSubsetSolution implements
    SubsetSolution<Integer>
{
	private ArrayList<Integer> indices;
	private ListOrderedSet subsetIndices;
	private ListOrderedSet remainingIndices;
	
	public UnorderedIntegerListSubsetSolution(Collection<Integer> indices, Collection<Integer> subsetIndices)
  {
		this.indices = new ArrayList<Integer>(indices.size()) ;
		this.subsetIndices = new ListOrderedSet() ;
		this.remainingIndices = new ListOrderedSet() ;
		
		this.indices.addAll(indices) ;
		this.remainingIndices.addAll(indices) ;
		
		this.subsetIndices.addAll(subsetIndices) ;
		this.remainingIndices.removeAll(subsetIndices) ;
  }

	private UnorderedIntegerListSubsetSolution(
			SubsetSolution<Integer> subsetSolution)
  {
		this.indices = new ArrayList<Integer>(subsetSolution.getSize()) ;
		this.subsetIndices = new ListOrderedSet() ;
		this.remainingIndices = new ListOrderedSet() ;
		
		this.indices.addAll(subsetSolution.getIndices()) ;
		this.subsetIndices.addAll(subsetSolution.getSubsetIndices()) ;
		this.remainingIndices.addAll(subsetSolution.getRemainingIndices()) ;
  }

	@Override
  public final synchronized Solution copy()
  {
	  return new UnorderedIntegerListSubsetSolution(this) ;
  }

	@Override
  public final void validate() throws CoreHunterException
  {
		if (indices.isEmpty())
			throw new CoreHunterException("Set must contain at least one index!") ;
		
		if (subsetIndices.isEmpty())
			throw new CoreHunterException("Subset must contain at least one index!") ;
		
		if (!indices.containsAll(subsetIndices))
			throw new CoreHunterException("Subset indices must all be present in set!") ;
	  
		if (!indices.containsAll(remainingIndices))
			throw new CoreHunterException("Remaining indices must all be present in set!") ;
  }

  @Override
  public final List<Integer> getIndices()
  {
	  return indices;
  }

	@Override
  public final int getSize()
  {
	  return indices.size() ;
  }

	@SuppressWarnings("unchecked")
  @Override
  public final List<Integer> getSubsetIndices()
  {
	  return subsetIndices.asList();
  }

	@Override
  public final synchronized void setSubsetIndices(List<Integer> subsetIndices)
  {
		this.subsetIndices.clear() ;
		this.subsetIndices.addAll(subsetIndices) ;
		remainingIndices.clear() ;
		remainingIndices.addAll(indices) ;
		remainingIndices.removeAll(subsetIndices) ;
  }

	@Override
  public final int getSubsetSize()
  {
	  return subsetIndices.size() ;
  }

	@SuppressWarnings("unchecked")
  @Override
  public final List<Integer> getRemainingIndices()
  {
	  return remainingIndices.asList();
  }

	@Override
  public final int getRemainingSize()
  {
	  return remainingIndices.size() ;
  }

	@Override
  public final synchronized void addIndex(Integer index)
  {
	  if (remainingIndices.remove(index))
	  {
	  	subsetIndices.add(index) ;
	  }
  }

	@Override
  public final synchronized Integer addRandomIndex(Random random)
  {
		Integer index = (Integer)remainingIndices.get(random.nextInt(getRemainingSize())) ;
		addIndex(index) ;
		
		return index ;
  }

	@Override
  public final synchronized void addAllIndices()
  {
		subsetIndices.clear() ;
		remainingIndices.clear() ;
		subsetIndices.addAll(subsetIndices) ;
  }

	@Override
  public final synchronized void removeIndex(Integer index)
  {
	  if (remainingIndices.remove(index))
	  {
	  	subsetIndices.add(index) ;
	  }
  }

	@Override
  public final synchronized Integer removeRandomIndex(Random random)
  {
		Integer index = (Integer)subsetIndices.get(random.nextInt(getSubsetSize())) ;
		removeIndex(index) ;
		
		return index ;
  }

	@Override
  public final synchronized void removeAllIndices()
  {
		subsetIndices.clear() ;
		remainingIndices.clear() ;
		remainingIndices.addAll(indices) ;
  }

	@Override
  public final synchronized void swapIndices(Integer indexToAdd, Integer indexToRemove)
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
  public final Integer[] swapRandomIndices(Random random)
  {
		Integer[] swappedIndices = new 	Integer[2] ;
		
		swappedIndices[0] = (Integer)remainingIndices.get(random.nextInt(getRemainingSize())) ;
		swappedIndices[1] = (Integer)subsetIndices.get(random.nextInt(getSubsetSize())) ;
		
		swapIndices(swappedIndices[0], swappedIndices[1]) ;
		
	  return swappedIndices;
  }

	@Override
  public final int getPositionInSubset(Integer index)
  {
	  return subsetIndices.indexOf(index);
  }

	@Override
  public Integer getIndexInSubsetAt(int position)
  {
	  return (Integer)subsetIndices.get(position);
  }

	@Override
  public final boolean containsIndexInSubset(Integer index)
  {
	  return subsetIndices.contains(index);
  }

	@Override
	public String toString()
	{
		return "indices =" + subsetIndices ;
	}
	
	@SuppressWarnings("rawtypes")
  @Override
	public boolean equals(Object object)
	{
		if (object instanceof SubsetSolution)
		{
			// TODO does order matter?
			return this.getSubsetIndices().equals(((SubsetSolution) object).getSubsetIndices()) ;
		}
		else
		{
			return super.equals(object) ;
		}
	}
}
