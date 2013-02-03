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

import java.util.ArrayList;
import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.search.Search;
import org.corehunter.search.SubsetSolution;

public class RandomSearch<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>, 
	DatasetType extends IndexedData<IndexType>> 
	extends AbstractSubsetSearch<IndexType, SolutionType, DatasetType>
{
	private boolean 						continueSearch ;

	public RandomSearch()
	{
		super();
	}
	
	protected RandomSearch(
			RandomSearch<IndexType, SolutionType, DatasetType> search) throws CoreHunterException
  {
		super(search);
		
		setSubsetMinimumSize(search.getSubsetMinimumSize()) ;
		setSubsetMaximumSize(search.getSubsetMaximumSize()) ;
  }
	
	@Override
  public Search<SolutionType> copy() throws CoreHunterException
  {
	  return new RandomSearch<IndexType, SolutionType, DatasetType>(this);
  }

	@Override
	protected void runSearch() throws CoreHunterException
	{
		List<IndexType> indices = new ArrayList<IndexType>(getData().getIndices());
		List<IndexType> subsetIndices = new ArrayList<IndexType>();

		continueSearch = true;

		while (continueSearch)
		{
			int position = getRandom().nextInt(indices.size());
			IndexType index = indices.remove(position);
			subsetIndices.add(index);
			continueSearch = continueSearch && subsetIndices.size() < getSubsetMaximumSize()
			    && (subsetIndices.size() < getSubsetMinimumSize() || getRandom().nextDouble() > 1.0 / (getSubsetMaximumSize() - getSubsetMinimumSize()));
		}
		
		getSolution().setSubsetIndices(subsetIndices) ;
		// register solution
		handleNewBestSolution(getSolution(), getObjectiveFunction().calculate(getSolution()));
	}
	
	@Override
  protected void stopSearch() throws CoreHunterException
  {
		continueSearch = false;
  }
}
