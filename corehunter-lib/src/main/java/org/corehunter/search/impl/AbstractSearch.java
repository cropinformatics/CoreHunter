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

import java.util.Random;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Data;
import org.corehunter.model.impl.EntityImpl;
import org.corehunter.search.Search;
import org.corehunter.search.SearchListener;
import org.corehunter.search.SearchListenerHandler;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.Solution;

public abstract class AbstractSearch<
	SolutionType extends Solution, DataType extends Data> extends EntityImpl implements
    Search<SolutionType>
{
	private static final String	baseIdentifier	= "id:";
	private static int nextIdentifier	= 0;
	private Random random = new Random();
	
	private SolutionType solution ;
	private DataType	data ;
	private SolutionType bestSolution;
	private double bestSolutionEvaluation;
	
	private SearchListenerHandler<SolutionType> searchListenerHandler ;

	private SearchStatus status;

	private long startTime = -2 ;
  private long endTime = -1;
	private long bestSolutionTime = -1;

	private boolean stuck;

	public AbstractSearch()
	{
		super (getNextUniqueIdentifier(), getCurrentUniqueIdentifier()) ;
		
		status = SearchStatus.NOT_STARTED;
		searchListenerHandler = new SearchListenerHandler<SolutionType>(this) ;
	}

	@SuppressWarnings("unchecked")
	protected AbstractSearch(AbstractSearch<SolutionType, DataType> search) throws CoreHunterException
  {
		super (getNextUniqueIdentifier(), search.getName()) ;
		
		status = SearchStatus.NOT_STARTED;
		searchListenerHandler = new SearchListenerHandler<SolutionType>(this) ;
		
		setSolution((SolutionType)search.getSolution().copy()) ;
		setData(search.getData()) ;
		setBestSolution((SolutionType)search.getBestSolution().copy()) ;
		setBestSolutionEvalution(search.getBestSolutionEvaluation()) ;
  }

	/**
	 * Gets the current solution under evaluation, which may not be the
	 * best solution found so far. To get the 'best' solution use
	 * {@link #getBestSolution()}
	 * @return the current solution under evaluation
	 */
	public final SolutionType getSolution()
  {
  	return solution;
  }

	/**
	 * Gets the initial solution under evaluation
	 * @return the initial solution under evaluation
	 * @throws CoreHunterException if the search is in progress
	 */
	public final void setSolution(SolutionType solution) throws CoreHunterException
  {
		if (this.solution != solution)
		{
			setCurrentSolution(solution) ;
			setBestSolution(solution) ;
			
			handleSolutionSet() ;
		}
  }
	
	public final DataType getData()
  {
  	return data;
  }

	public final void setData(DataType data) throws CoreHunterException
  {
		if (this.data != data)
		{
			this.data = data;
			
			handleDataSet() ;
		}
  }

	@Override
	public void start() throws CoreHunterException
	{
		if (!SearchStatus.STARTED.equals(status))
		{
			startTime = System.nanoTime() ;
			bestSolutionTime = startTime ;
			
			try
      {
				validate() ;
			
				fireSearchStarted() ;
				
	      runSearch();
	      
				endTime = System.nanoTime();
				
				fireSearchCompleted() ;
      }
      catch (CoreHunterException exception)
      {
  			endTime = System.nanoTime();
  			
  			fireSearchFailed(exception) ;
  			
	      throw exception ;
      }
		}
	}
	
	@Override
	public void stop() throws CoreHunterException
	{
		if (SearchStatus.STARTED.equals(status))
		{
			stopSearch();
			
			endTime = System.nanoTime();
			fireSearchStopped() ;
			status = SearchStatus.STOPPED ;
		}
	}
	
	@Override
	public void dispose()
	{
		if (!SearchStatus.STARTED.equals(status))
		{
			status = SearchStatus.DISPOSED ;
			solution = null ;
			data = null ;
			bestSolution = null ;
			searchListenerHandler.dispose() ;
		}
	}

	@Override
	public final SolutionType getBestSolution()
	{
		return bestSolution;
	}

	@Override
	public final double getBestSolutionEvaluation()
	{
		return bestSolutionEvaluation;
	}

	@Override
	public final SearchStatus getStatus()
	{
		return status;
	}

  @Override
	public final void addSearchListener(SearchListener<SolutionType> searchListener)
	{
		searchListenerHandler.addSearchListener(searchListener) ;
	}

  @Override
	public final void removeSearchListener(SearchListener<SolutionType> searchListener)
	{
		searchListenerHandler.removeSearchListener(searchListener) ;
	}

	@Override
	public final long getSearchTime()
	{
		return endTime < 0 ? System.nanoTime() - startTime : endTime - startTime ;
	}
	
	@Override
	public final long getBestSolutionTime()
  {
  	return System.nanoTime() - bestSolutionTime;
  }

	public final void setRandom(Random random)
  {
  	this.random = random;
  }

	public final Random getRandom()
  {
  	return random;
  }
	
	@Override
	public final boolean isStuck()
  {
  	return stuck;
  }

	protected final void setStuck(boolean stuck)
  {
	  this.stuck = stuck ;
  }
	
	protected void validate() throws CoreHunterException
  {
		if (SearchStatus.DISPOSED.equals(status))
	  	throw new CoreHunterException("Solution can not be started if aleady disposed!") ;
		
		if (SearchStatus.FAILED.equals(status))
	  	throw new CoreHunterException("Solution can not be started previously failed!") ;
		
	  if (solution == null)
	  	throw new CoreHunterException("No start solution defined!") ;
	  
	  if (data == null)
	  	throw new CoreHunterException("No dataset defined!") ;
	  
	  solution.validate() ;
		data.validate() ;
  }
	
	protected abstract void runSearch() throws CoreHunterException;
	
	protected abstract void stopSearch() throws CoreHunterException;
	
	protected void handleSolutionSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(status))
	  	throw new CoreHunterException("Solution can not be set while search in process") ;
		
	  if (solution == null)
	  	throw new CoreHunterException("No solution defined!") ;
  }
	
	protected void handleDataSet() throws CoreHunterException
  {
	  
		if (SearchStatus.STARTED.equals(status))
	  	throw new CoreHunterException("Dataset can not be set while search in process") ;
		
	  if (data == null)
	  	throw new CoreHunterException("No dataset defined!") ;
  }
	
	@SuppressWarnings("unchecked")
  protected void handleNewBestSolution(SolutionType bestSolution,
	    double bestSolutionEvaluation)
	{
		setBestSolutionEvalution(bestSolutionEvaluation);
		setBestSolution((SolutionType) bestSolution.copy());
	
		fireNewBestSolution(getBestSolution(), bestSolutionEvaluation);
	}

	protected void setCurrentSolution(SolutionType solution)
	{
		this.solution = solution ;
	}

	private void setBestSolution(SolutionType bestSolution)
	{
		this.bestSolutionTime = System.nanoTime() ;
		this.bestSolution = bestSolution;
	}

	protected void setBestSolutionEvalution(
	    double bestSolutionEvalution)
	{
		this.bestSolutionEvaluation = bestSolutionEvalution;
	}

	protected final void setStatus(SearchStatus status)
	{
		this.status = status;
	}
	
	protected boolean isBetterSolution(double newEvaluation, double oldEvaluation)
  {
	  return newEvaluation < oldEvaluation ;
  }

	protected double getWorstEvaluation()
  {
	  return Double.POSITIVE_INFINITY ;
  }

	protected double getDeltaScore(double newEvalution, double oldEvalution)
  {
	  return oldEvalution - newEvalution ;
  }

	private void fireSearchStarted()
	{
		status = SearchStatus.STARTED;

		searchListenerHandler.fireSearchStarted() ;
	}

	private void fireSearchCompleted()
	{
		status = SearchStatus.COMPLETED;

		searchListenerHandler.fireSearchCompleted() ;
	}
	
	private void fireSearchStopped()
	{
		status = SearchStatus.STOPPED ;

		searchListenerHandler.fireSearchStopped() ;
	}

	private void fireSearchFailed(CoreHunterException exception)
	{
		status = SearchStatus.FAILED;

		searchListenerHandler.fireSearchFailed(exception) ;
	}

  private void fireNewBestSolution(SolutionType bestSolution, double bestScore)
	{
		searchListenerHandler.fireNewBestSolution(bestSolution, bestScore) ;
	}

	protected void fireSearchProgress(double searchProgress)
	{
		searchListenerHandler.fireSearchProgress(searchProgress) ;
	}

	protected void fireSearchMessage(String message)
	{
		searchListenerHandler.fireSearchMessage(message) ;
	}
	
	private static String getCurrentUniqueIdentifier()
	{
		String identifier = baseIdentifier + nextIdentifier ;
		return identifier ;
	}
	
	private static String getNextUniqueIdentifier()
	{
		String identifier = baseIdentifier + nextIdentifier ;
		nextIdentifier++;
		return identifier ;
	}
}
