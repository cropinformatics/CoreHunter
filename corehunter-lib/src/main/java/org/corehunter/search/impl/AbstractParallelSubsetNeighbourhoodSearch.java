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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.search.Search;
import org.corehunter.search.SearchCallable;
import org.corehunter.search.SearchListener;
import org.corehunter.search.SearchListenerAdapter;
import org.corehunter.search.SearchRunnable;
import org.corehunter.search.SubsetSearch;
import org.corehunter.search.SubsetSolution;

public abstract class AbstractParallelSubsetNeighbourhoodSearch<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>, 
	DatasetType extends IndexedData<IndexType>,
	NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>,
	SubSearchType extends SubsetSearch<IndexType, SolutionType>>
	extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>
{
	private SearchListener<SolutionType> subSearchListener;
	private ExecutorService executorService ;
	private Map<SubSearchType, Future<SubSearchType>> futures;
	private ThreadFactory threadFactory;

	public AbstractParallelSubsetNeighbourhoodSearch()
	{
		super();

		initialise() ;
	}

	protected AbstractParallelSubsetNeighbourhoodSearch(
      AbstractParallelSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType, SubSearchType> search) throws CoreHunterException
  {
		super(search);

		initialise() ;
  }

	private void initialise()
  {
		subSearchListener = new SearchListenerAdapter<SolutionType>()
		{

			@Override
			public void searchFailed(Search<SolutionType> search, CoreHunterException exception)
			{
				executorService.shutdownNow() ;
				fireSearchFailed(exception) ;
			}

			@Override
			public void newBestSolution(Search<SolutionType> search,
					SolutionType bestSolution, double bestSolutionEvaluation)
			{
				checkForBestSolution(search, bestSolution, bestSolutionEvaluation) ;
			}

			@Override
			public void searchMessage(Search<SolutionType> search, String message)
			{
				fireSearchMessage(message) ;
			}

		} ;
		
		// create thread pool
		final ThreadGroup threadGroup = new ThreadGroup("replicaThreadGroup");

		threadFactory = new ThreadFactory()
		{
			public Thread newThread(Runnable r)
			{
				Thread thr = new Thread(threadGroup, r);
				thr.setPriority(Thread.MIN_PRIORITY);
				return thr;
			}
		};
		
		executorService = Executors.newCachedThreadPool(threadFactory);
		futures = new HashMap<SubSearchType, Future<SubSearchType>>() ;
  }

	protected final synchronized void checkForBestSolution(Search<SolutionType> search,
      SolutionType bestSolution, double bestSolutionEvaluation)
  {
	  if (isBetterSolution(bestSolutionEvaluation, getBestSolutionEvaluation()) || 
	  		(bestSolutionEvaluation == getBestSolutionEvaluation() && bestSolution.getSubsetSize() < getBestSolution().getSubsetSize()))
	  {
	  	fireNewBestSolution(bestSolution, bestSolutionEvaluation) ;
	  }
  }
	
	protected final synchronized void startSubSearches(List<SubSearchType> subSearches) throws CoreHunterException
  {
		List<SearchCallable<SolutionType, SubSearchType>> callables = new ArrayList<SearchCallable<SolutionType, SubSearchType>>() ;
		
		Iterator<SubSearchType> iterator = subSearches.iterator() ;
		
		SubSearchType subSearch ;
		
		while (iterator.hasNext())
		{
			subSearch = iterator.next() ;
			
			subSearch.addSearchListener(subSearchListener) ;
			
			callables.add(new SearchCallable<SolutionType, SubSearchType>(subSearch)) ;
		}
		
		try
		{
			executorService.invokeAll(callables) ;
		}
		catch (InterruptedException ex)
		{
			throw new CoreHunterException("Error in thread pool: " + ex);
		}
		
		iterator = subSearches.iterator() ;
		
		while (iterator.hasNext())
		{
			subSearch = iterator.next() ;
			
			subSearch.removeSearchListener(subSearchListener) ;
		}
  }
	
	protected final synchronized List<Future<SubSearchType>> submitSubSearches(List<SubSearchType> subSearches)
  {
		List<Future<SubSearchType>> futures = new ArrayList<Future<SubSearchType>>() ;
		
		Iterator<SubSearchType> iterator = subSearches.iterator() ;
		
		while (iterator.hasNext())
		{
			futures.add(submitSubSearch(iterator.next())) ;
		}
		
		return futures ;
  }
	
	protected final synchronized Future<SubSearchType> submitSubSearch(SubSearchType subSearch)
  {
		subSearch.addSearchListener(subSearchListener) ;
		
		Future<SubSearchType> future = executorService.submit(new SearchCallable<SolutionType, SubSearchType>(subSearch));
		
		futures.put(subSearch, future);
		
		return future ;
  }
	
	protected final synchronized void submitSubSearch(SubSearchType lrrep, int priority)
  {
		Thread lrThread = threadFactory.newThread(new SearchRunnable<SolutionType, SubSearchType>(lrrep));
		lrThread.setPriority(Thread.MAX_PRIORITY);
		lrThread.start();
  }
	
	protected final synchronized void unregisterSubSearches(List<SubSearchType> subSearches)
  {
		Iterator<SubSearchType> iterator = subSearches.iterator() ;
		
		while (iterator.hasNext())
		{
			unregisterSubSearch(iterator.next()) ;
		}
  }
	
	protected final synchronized void unregisterSubSearch(SubSearchType subSearch)
  {
		subSearch.removeSearchListener(subSearchListener) ;
		futures.remove(subSearch) ;
  }
	
	protected final boolean tabuReplicasBusy(List<Future<SubSearchType>> tabuFutures)
	{
		// remove all tabu replica futures which are already done
		Iterator<Future<SubSearchType>> itr = tabuFutures.iterator();
		while (itr.hasNext())
		{
			if (itr.next().isDone())
			{
				itr.remove();
			}
		}
		// if busy futures remain, return true
		return tabuFutures.size() > 0;
	}

}
