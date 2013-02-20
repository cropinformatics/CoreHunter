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

import static org.corehunter.Constants.INVALID_SIZE;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.neighbourhood.impl.AdditionMove;
import org.corehunter.neighbourhood.impl.DeletionMove;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.SubsetSolution;

public class LRSearch<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>, 
	DatasetType extends IndexedData<IndexType>> 
	extends AbstractSubsetSearch<IndexType, SolutionType, DatasetType>
{
	private int l = INVALID_SIZE ;
	private int	r = INVALID_SIZE ;
	
	private boolean continueSearch ;
	private ExhaustiveSubsetSearch<IndexType, SolutionType, DatasetType> exhaustiveSearch;

	public LRSearch()
	{
		super();
	}

	protected LRSearch(LRSearch<IndexType, SolutionType, DatasetType> search) throws CoreHunterException
  {
		super(search) ;
		
		setL(search.getL()) ;
		setR(search.getR()) ;
		setExhaustiveSearch((ExhaustiveSubsetSearch<IndexType, SolutionType, DatasetType>)search.getExhaustiveSearch().copy()) ;
  }

	@Override
  public Search<SolutionType> copy() throws CoreHunterException
  {
	  return new LRSearch<IndexType, SolutionType, DatasetType>(this);
  }
	
	public final int getL()
  {
  	return l;
  }

	public final void setL(int l) throws CoreHunterException
  {
		if (this.l != l)
		{
			this.l = l;
			
			handleLSet() ;
		}
  }

	public final int getR()
  {
  	return r;
  }

	public final void setR(int r) throws CoreHunterException
  {
		if (this.r != r)
		{
			this.r = r;
			
			handleRSet() ;
		}
  }

	public final ExhaustiveSubsetSearch<IndexType, SolutionType, DatasetType> getExhaustiveSearch()
  {
  	return exhaustiveSearch;
  }

	public final void setExhaustiveSearch(
      ExhaustiveSubsetSearch<IndexType, SolutionType, DatasetType> exhaustiveSearch) throws CoreHunterException
  {
		if (this.exhaustiveSearch != exhaustiveSearch)
		{
			this.exhaustiveSearch = exhaustiveSearch;
			
			handleExhaustiveSearchSet() ;
		}
  }
	
	protected void handleLSet() throws CoreHunterException
  { 
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("L can not be set while search in process") ;
		
	  if (l < 0)
	  	throw new CoreHunterException("L can not be less than zero!") ;
  }
	
	protected void handleRSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("R can not be set while search in process") ;
		
	  if (r < 0)
	  	throw new CoreHunterException("R can not be less than zero!") ;
  }
	
	protected void handleExhaustiveSearchSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Exhaustive Search can not be set while search in process") ;
  }

	@Override
	protected void runSearch() throws CoreHunterException
	{
		continueSearch = true;

		double score, newScore, bestNewScore, dscore;

		IndexType bestAddIndex = null, bestRemIndex = null;
		Stack<IndexedMove<IndexType, SolutionType>> history = new Stack<IndexedMove<IndexType, SolutionType>>();

		boolean skipadd = false;
		if (l > r)
		{
			// Start with minimal set, stepwise increase size
			if (exhaustiveSearch != null)
			{
				// Because distance measures require at least two accessions to
				// be
				// computable, exhaustively select the best core set of size 2
				executeExhaustiveSearch(getSolution(), getObjectiveFunction());
			}
			else
			{
				// Random first pair, to save computational cost: this
				// transforms the
				// deterministic lr search into a semi-random method
				executeRandomSearch(getSolution(), getObjectiveFunction());
			}
		}
		else
		{
			// Start with full set, stepwise decrease size
			getSolution().addAllIndices() ;
			skipadd = true;
		}
		
		score = getObjectiveFunction().calculate(getSolution());
		bestNewScore = score;
		handleNewBestSolution(getSolution(), bestNewScore);
	
		while (continueSearch)
		{
			// Add l new accessions to core
			if (!skipadd)
			{
				for (int i = 0; i < l; i++)
				{
					List<IndexType> unselected = new ArrayList<IndexType>(getSolution().getRemainingIndices()) ;

					// Search for best new accession
					bestNewScore = getWorstEvaluation() ; 
					
					Iterator<IndexType> iterator = unselected.iterator() ;
					IndexType index ;
					
					while (iterator.hasNext())
					{
						index = iterator.next() ;
						getSolution().addIndex(index) ;
						newScore = getObjectiveFunction().calculate(getSolution());
						
						if (isBetterSolution(newScore, bestNewScore))
						{
							bestNewScore = newScore;
							bestAddIndex = index ;
						}
						
						getSolution().removeIndex(index);
					}
					// Add best new accession
					getSolution().addIndex(bestAddIndex) ;
					history.add(new AdditionMove<IndexType, SolutionType>(bestAddIndex));
				}
				skipadd = false;
			}
			// Remove r accessions from core
			for (int i = 0; i < r; i++)
			{
				// Search for worst accession
				bestNewScore = getWorstEvaluation() ; 
				
				List<IndexType> selected = new ArrayList<IndexType>(getSolution().getSubsetIndices()) ;
				
				Iterator<IndexType> iterator = selected.iterator() ;
				IndexType index ;
				
				while (iterator.hasNext())
				{
					index = iterator.next() ;
					getSolution().removeIndex(index) ;
					newScore = getObjectiveFunction().calculate(getSolution());
					
					if (isBetterSolution(newScore, bestNewScore))
					{
						bestNewScore = newScore;
						bestRemIndex = index ;
					}
					
					getSolution().addIndex(index);
				}

				// Remove worst accession
				getSolution().removeIndex(bestAddIndex) ;
				history.add(new DeletionMove<IndexType, SolutionType>(bestAddIndex));
			}

			dscore = bestNewScore - score;
			score = bestNewScore;

			// Determine whether to continue search
			if (l > r)
			{
				// Increasing core size
				if (getSolution().getSubsetSize() > getSubsetMinimumSize() && dscore <= 0)
				{
					continueSearch = false; // Equal or worse score and size increased
					// Restore previous core
					for (int i = 0; i < l + r; i++)
					{
						history.pop().undo(getSolution());
					}
				}
				else
				{
					if (getSolution().getSubsetSize() + l - r > getSubsetMaximumSize())
					{
						continueSearch = false; // Max size reached
					}
				}
			}
			else
			{
				// Decreasing core size
				if (getSolution().getSubsetSize() > getSubsetMaximumSize() && dscore < 0)
				{
					continueSearch = false; // Worse score
					// Restore previous core
					for (int i = 0; i < l + r; i++)
					{
						history.pop().undo(getSolution());
					}
				}
				else
				{
					if (getSolution().getSubsetSize() + l - r < getSubsetMinimumSize())
					{
						continueSearch = false; // Min size reached
					}
				}
			}

			handleNewBestSolution(getSolution(), score) ;
		}
	}
	
	@Override
  protected void stopSearch() throws CoreHunterException
  {
		continueSearch = false;
  }

	private void executeExhaustiveSearch(SolutionType solution,
      ObjectiveFunction<SolutionType> objectiveFunction) throws CoreHunterException
  {
		exhaustiveSearch.setObjectiveFunction(objectiveFunction) ;
		exhaustiveSearch.setData(getData()) ;
		exhaustiveSearch.setSubsetMinimumSize(2) ;
		exhaustiveSearch.setSubsetMaximumSize(2) ;
		
		exhaustiveSearch.start()  ;
		
		setCurrentSolution(exhaustiveSearch.getBestSolution()) ;
  }

	private void executeRandomSearch(SolutionType solution,
      ObjectiveFunction<SolutionType> objectiveFunction) throws CoreHunterException
  {
		RandomSearch<IndexType, SolutionType, DatasetType> search = new RandomSearch<IndexType, SolutionType, DatasetType>() ;
		
		search.setObjectiveFunction(objectiveFunction) ;
		search.setSolution(getSolution()) ;
		search.setData(getData()) ;
		search.setSubsetMinimumSize(2) ;
		search.setSubsetMaximumSize(2) ;
		
		search.start() ;
		
		setCurrentSolution(search.getBestSolution()) ;
  }
}
