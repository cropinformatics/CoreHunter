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

import org.corehunter.CoreHunterException;
import org.corehunter.model.Data;
import org.corehunter.neighbourhood.Neighbourhood;
import org.corehunter.search.NeighbourhoodSearch;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.Solution;

public abstract class AbstractNeighbourhoodSearch<
	SolutionType extends Solution, 
	DataType extends Data,
	NeighbourhoodType extends Neighbourhood<SolutionType>>
	extends AbstractObjectiveSearch<SolutionType, DataType> implements NeighbourhoodSearch<SolutionType, 	NeighbourhoodType>
{
	private NeighbourhoodType neighbourhood ;
	
	public AbstractNeighbourhoodSearch()
  {

  }
	
	protected AbstractNeighbourhoodSearch(
      AbstractNeighbourhoodSearch<SolutionType, DataType, NeighbourhoodType> search) throws CoreHunterException
  {
		super(search) ;
		
		setNeighbourhood(search.getNeighbourhood()) ;
  }

	@Override
	public final NeighbourhoodType getNeighbourhood()
  {
  	return neighbourhood;
  }

	public final void setNeighbourhood(NeighbourhoodType neighbourhood) throws CoreHunterException
  {
		if (this.neighbourhood != neighbourhood)
		{
			this.neighbourhood = neighbourhood;
			
			handleNeighbourhoodSet() ;
		}
  }

	protected void handleNeighbourhoodSet() throws CoreHunterException
  {
	  if (neighbourhood == null)
	  	throw new CoreHunterException("No neighbourhood defined!") ;
	  
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Neighbourhood can not be set while search in process") ;
  }

	@Override
	protected void validate() throws CoreHunterException
  {
		super.validate() ;
		
	  if (neighbourhood == null)
	  	throw new CoreHunterException("No neighbourhood defined!") ;
	  
	  neighbourhood.validate() ;
  }
}
