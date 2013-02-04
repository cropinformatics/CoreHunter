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

package org.corehunter.objectivefunction.impl;

import org.corehunter.model.IndexedData;
import org.corehunter.search.SubsetSolution;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public abstract class AbstractSubsetObjectiveFunction<
	IndexType, 
	DatasetType extends IndexedData<IndexType>> 
	extends AbstractObjectiveFunction<SubsetSolution<IndexType>, DatasetType>
{
	public AbstractSubsetObjectiveFunction(String name, String description)
	{
		super (name, description) ;
	}
	
	@Override
  public boolean isMinimizing()
  {
	  return false;
  }

}