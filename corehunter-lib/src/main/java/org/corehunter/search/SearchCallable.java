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

import java.util.concurrent.Callable;

public class SearchCallable<SolutionType extends Solution, SearchType extends Search<SolutionType>> implements Callable<SearchType>
{
	private SearchType search ;
	
	public SearchCallable(SearchType search)
  {
	  super();
	  this.search = search;
  }

	public final SearchType getSearch()
  {
  	return search;
  }

	@Override
  public SearchType call() throws Exception
  {
    search.start() ;
    
	  return search;
  }

}
