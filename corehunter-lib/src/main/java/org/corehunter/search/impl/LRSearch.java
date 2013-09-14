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
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import static org.corehunter.Constants.INVALID_SIZE;

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.EvaluatedMove;
import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.neighbourhood.impl.AdditionMove;
import org.corehunter.neighbourhood.impl.DeletionMove;
import org.corehunter.search.Search;
import org.corehunter.search.SearchException;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.SubsetSolution;

public class LRSearch<IndexType, SolutionType extends SubsetSolution<IndexType>>
    extends AbstractSubsetSearch<IndexType, SolutionType>
{

	private int	l	= INVALID_SIZE;
	private int	r	= INVALID_SIZE;

	public LRSearch()
	{
		super();
	}

	protected LRSearch(LRSearch<IndexType, SolutionType> search)
	    throws CoreHunterException
	{
		super(search);
		setL(search.getL());
		setR(search.getR());
	}

	@Override
	public Search<SolutionType> copy() throws CoreHunterException
	{
		return new LRSearch<IndexType, SolutionType>(this);
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
			handleLSet();
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
			handleRSet();
		}
	}

	protected void handleLSet() throws CoreHunterException
	{
		if (SearchStatus.STARTED.equals(getStatus()))
		{
			throw new CoreHunterException("L can not be set while search in process");
		}
		if (l < 0)
		{
			throw new CoreHunterException("L can not be less than zero!");
		}
	}

	protected void handleRSet() throws CoreHunterException
	{
		if (SearchStatus.STARTED.equals(getStatus()))
		{
			throw new CoreHunterException("R can not be set while search in process");
		}
		if (r < 0)
		{
			throw new CoreHunterException("R can not be less than zero!");
		}
	}

	/**
	 * Check whether subset size is increasing (L > R).
	 */
	private boolean increasingSubsetSize()
	{
		return l > r;
	}

	/**
	 * Check whether subset size is decreasing (R > L).
	 */
	private boolean decreasingSubsetSize()
	{
		return r > l;
	}

	private boolean insideValidSizeRegion(SolutionType solution)
	{
		return solution.getSubsetSize() >= getSubsetMinimumSize()
		    && solution.getSubsetSize() <= getSubsetMaximumSize();
	}

	@Override
	protected void runSearch() throws CoreHunterException
	{
		int difference  = l - r ;
		IndexedMove<IndexType, SolutionType> move;
		
		if (difference > 0) 
		{
			// Increasing core size, stop if can not continue or next step will go over maximum
			while (canContinue() && getCurrentSolution().getSubsetSize() + difference <= getSubsetMinimumSize())
			{
				for (int i = 0 ; i < l ; ++i)
				{
					move = findBestAddMove() ;
					
					if (move != null)
					{
						performMove(move) ;
					}
				}
				
				for (int i = 0 ; i < r ; ++i)
				{
					move = findBestRemoveMove() ;
					
					if (move != null)
					{
						performMove(move) ;
					}
				}
			}
		}
		else
		{
			if (difference < 0) 
			{
				// Decreasing core size, stop if can not continue or next step will go under minimum
				while (getCurrentSolution().getSubsetSize() + difference >= getSubsetMinimumSize())
				{
					for (int i = 0 ; i < r ; ++i)
					{
						move = findBestRemoveMove() ;
						
						if (move != null)
						{
							performMove(move) ;
						}
					}
					
					for (int i = 0 ; i < l ; ++i)
					{
						move = findBestAddMove() ;
						
						if (move != null)
						{
							performMove(move) ;
						}
					}
				}
			}
			else
			{
				// should not happen
				throw new SearchException("L == R!!!!");
			}
		}
		
		// check if valid solution
		if (getBestSolution() == null || !insideValidSizeRegion(getBestSolution()))
		{
			throw new SearchException("Search stopped before it could find a valid solution");
		}
		
			// take next step

			// ...

			// if valid solution obtained: check if new best solution

			// ...

			// if next step will jump over valid size region: adjust L and R
			// so that |L-R| = 1, to obtain valid solution(s)
		  // NOTE this is not needed a valid solution will always be obtained if possible

			// ...

	}
	
	private IndexedMove<IndexType, SolutionType> findBestRemoveMove()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	private IndexedMove<IndexType, SolutionType> findBestAddMove()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@SuppressWarnings("rawtypes")
  private void performMove(IndexedMove<IndexType, SolutionType> move) throws CoreHunterException
  {
		move.apply(getCurrentSolution());
		
		if (move instanceof EvaluatedMove)
		{
			setCurrentSolutionEvaluation(((EvaluatedMove)move).getEvaluation());
		}
		else
		{
			getObjectiveFunction().calculate(getCurrentSolution()) ;
		}
		
		if (insideValidSizeRegion(getCurrentSolution()) && isNewBestSolution(getCurrentSolutionEvaluation(), getCurrentSolution().getSize()))
		{
			handleNewBestSolution(getCurrentSolution(), getCurrentSolutionEvaluation());
		}
  }

	@Override
	protected void validate() throws CoreHunterException
	{
		super.validate();

		// check L and R
		if (l < 0)
		{
			throw new CoreHunterException("L can not be less than zero");
		}
		if (r < 0)
		{
			throw new CoreHunterException("R can not be less than zero");
		}
		if (l == r)
		{
			throw new CoreHunterException("L and R can not be equal");
		}

		// initial subset should contain at least two indices, for the distance
		// measures to be computable
		if (getCurrentSolution().getSubsetSize() < 2)
		{
			throw new CoreHunterException(
			    "Initial subset should contain at least 2 indices");
		}

		// if subset size is increasing, initial size can not be too large
		if (increasingSubsetSize()
		    && getCurrentSolution().getSubsetSize() > getSubsetMaximumSize())
		{
			throw new CoreHunterException(
			    "L > R (increasing subset size): initial subset size can not be larger than maximum subset size");
		}

		// if subset size is decreasing, initial size can not be too small
		if (decreasingSubsetSize()
		    && getCurrentSolution().getSubsetSize() < getSubsetMinimumSize())
		{
			throw new CoreHunterException(
			    "L < R (decreasing subset size): initial subset size can not be smaller than minimum subset size");
		}

	}

}
