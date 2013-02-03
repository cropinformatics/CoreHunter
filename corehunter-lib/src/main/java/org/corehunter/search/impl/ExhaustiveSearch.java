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

import java.util.List;

import org.apache.commons.math.util.MathUtils;
import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.SubsetGenerator;
import org.corehunter.search.SubsetSolution;

/**
 * Evaluate all possible core sets and return best one
 */
public class ExhaustiveSearch<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>, 
	DatasetType extends IndexedData<IndexType>> 
	extends AbstractSubsetSearch<IndexType, SolutionType, DatasetType>
{
	private SubsetGenerator<IndexType> subsetGenerator ;
	
	public ExhaustiveSearch() 
  {
	  super() ;
  }
	
	protected ExhaustiveSearch(
			ExhaustiveSearch<IndexType, SolutionType, DatasetType> exhaustiveSearch) throws CoreHunterException
  {
	  super(exhaustiveSearch) ;
  }

	@Override
	public ExhaustiveSearch<IndexType, SolutionType, DatasetType> copy()
	{
		try
    {
	    return new ExhaustiveSearch<IndexType, SolutionType, DatasetType>(this);
    }
    catch (Exception e)
    {
	    return null ;
    }
	}

	public final SubsetGenerator<IndexType> getSubsetGenerator()
  {
  	return subsetGenerator;
  }

	public final void setSubsetGenerator(SubsetGenerator<IndexType> subsetGenerator) throws CoreHunterException
  {
		if (this.subsetGenerator != subsetGenerator)
  	{
			this.subsetGenerator = subsetGenerator;
		
			handleSubsetGeneratorSet() ;
  	}
  }

	@Override
  protected void validate() throws CoreHunterException
  {
	  super.validate();
	  
	  if (subsetGenerator == null)
		  throw new CoreHunterException("Subset generator must be defined!") ;
	  
	  subsetGenerator.setIndices(getData().getIndices()) ;
  	subsetGenerator.setSubsetSize(getSubsetMinimumSize()) ;
  	
  	subsetGenerator.validate() ;
  }

	protected void handleSubsetGeneratorSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Subset generator can not be set while search in process") ;
		
	  if (subsetGenerator == null)
		  throw new CoreHunterException("Subset generator must be defined!") ;
  }

	@Override
	protected void runSearch() throws CoreHunterException
	{
		double score, bestScore = getWorstEvaluation() ;
		
		double progress = 0;
		double newProgress;

		// Calculate pseudomeasure for all possible core sets and return best
		// core
		
		long nr = getNumberOfSubsets() ;

		fireSearchMessage("Nr of possible core sets: " + nr + "!");

		for (int i = getSubsetMinimumSize() ; i <= getSubsetMaximumSize(); ++i)
		{
			subsetGenerator.setSubsetSize(i) ;
 			nr = subsetGenerator.getNumberOfSubsets() ;
			
			List<IndexType> subsetIndices = subsetGenerator.first();
	
			for (long j = 1; j <= nr; j++)
			{
				newProgress = (double) j / (double) nr;
	
				if (newProgress > progress)
				{
					fireSearchProgress(newProgress);
					progress = newProgress;
				}
	
				getSolution().setSubsetIndices(subsetIndices);
	
				score = getObjectiveFunction().calculate(getSolution(), getCacheIdentifier());
				
				if (score > bestScore)
				{
					bestScore = score;
					handleNewBestSolution(getSolution(), bestScore);
				}
				subsetGenerator.next(subsetIndices);
			}
		}
	}

	private long getNumberOfSubsets()
  {
		long numberOfSubsets = 0 ;
		
		for (int i = getSubsetMinimumSize() ; i <= getSubsetMaximumSize(); ++i)
			numberOfSubsets = numberOfSubsets + MathUtils.binomialCoefficient(getData().getIndices().size(), i);
		
	  return numberOfSubsets ;
  }

	@Override
  protected void stopSearch() throws CoreHunterException
  {

  }
}
