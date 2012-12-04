//  Copyright 2012 Guy Davenport, Herman De Beukelaer
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.corehunter.search;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.corehunter.CoreHunterException;
import org.corehunter.Search;
import org.corehunter.SearchListener;
import org.corehunter.Solution;

public abstract class AbstractSearch<SolutionType extends Solution> implements Search<SolutionType>
{
	private SolutionType bestSolution ;
	private double bestSolutionEvaluation ;
	private final List<SearchListener<SolutionType>> searchListeners ;
	private SearchStatus status;

	long startTime ;
	long endTime ;
        
        public AbstractSearch(){
            searchListeners = new LinkedList<SearchListener<SolutionType>>();
            status = SearchStatus.NOT_STARTED;
        }
	
	@Override
	public void start() throws CoreHunterException
	{
		startTime = System.currentTimeMillis() ;
		
		runSearch() ;
		
		endTime = System.currentTimeMillis() ;
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
	
	public void addSearchListener(SearchListener<SolutionType> searchListener)
	{
                synchronized(searchListeners){
                    searchListeners.add(searchListener);
                }
	}
	
	public void removeSearchListener(SearchListener<SolutionType> searchListener)
	{
                synchronized(searchListeners){
                    searchListeners.remove(searchListener);
                }
	}
	
	public final long getStartTime()
	{
		return startTime;
	}

	public final long getEndTime()
	{
		return endTime;
	}

	protected abstract void runSearch() throws CoreHunterException ;
	
	@SuppressWarnings("unchecked")
	protected void handleNewBestSolution(SolutionType bestSolution,
			double bestSolutionEvalution, boolean copySolution)
	{
		if (copySolution){
                    setBestSolution((SolutionType)bestSolution.copy());
                } else {
                    setBestSolution(bestSolution) ;
                }
		
		setBestSolutionEvalution(bestSolutionEvalution) ;
		fireNewBestSolution(bestSolution, bestSolutionEvalution);
	}

	protected synchronized void setBestSolution(SolutionType bestSolution)
	{
		this.bestSolution = bestSolution ;
	}

	protected synchronized void setBestSolutionEvalution(double bestSolutionEvalution)
	{
		this.bestSolutionEvaluation = bestSolutionEvalution ;
	}

	protected final void setStatus(SearchStatus status)
	{
		this.status = status;
	}

	protected void fireSearchStarted()
	{
		status = SearchStatus.STARTED ;
		
		synchronized (searchListeners)
		{
			Iterator<SearchListener<SolutionType>> iterator = searchListeners.iterator() ;
			
			while (iterator.hasNext()){
				iterator.next().searchStarted(this) ;
                        }
		}
	}

	protected void fireSearchCompleted()
	{
		status = SearchStatus.COMPLETED ;
		
		synchronized (searchListeners)
		{
			Iterator<SearchListener<SolutionType>> iterator = searchListeners.iterator() ;
			
			while (iterator.hasNext()){
				iterator.next().searchCompleted(this) ;
                        }
		}
	}
	
	protected void fireSearchFailed()
	{
		status = SearchStatus.FAILED;
		
		synchronized (searchListeners)
		{
			Iterator<SearchListener<SolutionType>> iterator = searchListeners.iterator() ;
			
			while (iterator.hasNext()){
				iterator.next().searchFailed(this) ;
                        }
		}
	}


	protected void fireNewBestSolution(SolutionType bestSolution, double bestScore)
	{
		synchronized (searchListeners)
		{
			Iterator<SearchListener<SolutionType>> iterator = searchListeners.iterator() ;
			
			while (iterator.hasNext()){
				iterator.next().newBestSolution(this, bestSolution, bestScore) ;
                        }
		}
	}
	

	protected void fireSearchProgress(double searchProgress)
	{
		synchronized (searchListeners)
		{
			Iterator<SearchListener<SolutionType>> iterator = searchListeners.iterator() ;
			
			while (iterator.hasNext()){
				iterator.next().searchProgress(this, searchProgress) ;
                        }
		}
	}

	protected void fireSearchMessage(String message)
	{
		synchronized (searchListeners)
		{
			Iterator<SearchListener<SolutionType>> iterator = searchListeners.iterator() ;
			
			while (iterator.hasNext()){
				iterator.next().searchMessage(this, message) ;
                        }
		}
	}
}
