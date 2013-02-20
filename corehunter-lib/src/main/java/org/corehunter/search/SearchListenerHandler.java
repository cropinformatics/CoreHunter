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
package org.corehunter.search;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.search.solution.Solution;

public class SearchListenerHandler<SolutionType extends Solution>
{
	private List<SearchListener<SolutionType>> searchListeners;
	private Search<SolutionType> search ;

	public SearchListenerHandler(Search<SolutionType> search)
  {
	  super();
	  this.search = search ;
		searchListeners = new LinkedList<SearchListener<SolutionType>>();
  }

	public void dispose()
  {
		searchListeners.clear() ; 
  }
	
	public final synchronized void addSearchListener(SearchListener<SolutionType> searchListener)
	{
		searchListeners.add(searchListener);
	}

	public final synchronized void removeSearchListener(SearchListener<SolutionType> searchListener)
	{
		searchListeners.remove(searchListener);
	}
	
	public void fireSearchStarted()
	{
		//new Thread(new Runnable()
		//{
			//@Override
      //public void run()
     // {
				//synchronized (searchListeners)
				//{
					Iterator<SearchListener<SolutionType>> iterator = searchListeners
					    .iterator();

					while (iterator.hasNext())
					{
						iterator.next().searchStarted(search);
					}
				//}
     // }
			
		//}).start() ;
	}

	public void fireSearchCompleted()
	{
		//new Thread(new Runnable()
		//{
			//@Override
     // public void run()
     // {
			//	synchronized (searchListeners)
				//{
					Iterator<SearchListener<SolutionType>> iterator = searchListeners
					    .iterator();

					while (iterator.hasNext())
					{
						iterator.next().searchCompleted(search);
					}
				//}
     // }
			
		//}).start() ;
	}
	
	public void fireSearchStopped()
	{
		//new Thread(new Runnable()
		//{
			//@Override
     // public void run()
      //{
				//synchronized (searchListeners)
				//{
					Iterator<SearchListener<SolutionType>> iterator = searchListeners
					    .iterator();

					while (iterator.hasNext())
					{
						iterator.next().searchStopped(search);
					}
				//}
    //  }
			
		//}).start() ;
	}

	public void fireSearchFailed(final CoreHunterException exception)
	{
		//synchronized (searchListeners)
	//	{
			Iterator<SearchListener<SolutionType>> iterator = searchListeners
			    .iterator();

			while (iterator.hasNext())
			{
				iterator.next().searchFailed(search, exception);
			}
		//}
		/*new Thread(new Runnable()
		{
			@Override
      public void run()
      {
				synchronized (searchListeners)
				{
					Iterator<SearchListener<SolutionType>> iterator = searchListeners
					    .iterator();

					while (iterator.hasNext())
					{
						iterator.next().searchFailed(search, exception);
					}
				}
      }
			
		}).start() ;*/
	}

	public void fireNewBestSolution(final SolutionType bestSolution, final double bestScore)
	{
		//new Thread(new Runnable()
		//{
		//	@Override
    //  public void run()
    //  {
			//	synchronized (searchListeners)
			//	{
					Iterator<SearchListener<SolutionType>> iterator = searchListeners
					    .iterator();

					while (iterator.hasNext())
					{
						iterator.next().newBestSolution(search, bestSolution, bestScore);
					}
			//	}
    //  }
			
		//}).start() ;
	}

	public void fireSearchProgress(final double searchProgress)
	{
		//new Thread(new Runnable()
		//{
		//	@Override
    //  public void run()
    //  {
			//	synchronized (searchListeners)
				//{
					Iterator<SearchListener<SolutionType>> iterator = searchListeners
					    .iterator();

					while (iterator.hasNext())
					{
						iterator.next().searchProgress(search, searchProgress);
					}
			//	}
   //   }
			
		//}).start() ;
	}

	public void fireSearchMessage(final String message)
	{
		//new Thread(new Runnable()
		//{
		//	@Override
    //  public void run()
    //  {
				//synchronized (searchListeners)
			//	{
					Iterator<SearchListener<SolutionType>> iterator = searchListeners
					    .iterator();

					while (iterator.hasNext())
					{
						iterator.next().searchMessage(search, message);
					}
				//}
    //  }
		//	
	//	}).start() ;
	}
}
