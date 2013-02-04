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

import org.corehunter.CoreHunterException;

public class SearchListenerAdapter<SolutionType extends Solution> implements SearchListener<SolutionType>
{

	@Override
  public void searchStarted(Search<SolutionType> search)
  {

  }

	@Override
  public void searchCompleted(Search<SolutionType> search)
  {

  }
	
	@Override
  public void searchStopped(Search<SolutionType> search)
  {

  }

	@Override
  public void searchFailed(Search<SolutionType> search, CoreHunterException exception)
  {

  }

	@Override
  public void newBestSolution(Search<SolutionType> search,
      SolutionType bestSolution, double bestSolutionEvaluation)
  {

  }

	@Override
  public void searchProgress(Search<SolutionType> search, double progress)
  {

  }

	@Override
  public void searchMessage(Search<SolutionType> search, String message)
  {

  }



}
