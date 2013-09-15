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

import org.corehunter.CoreHunterException;
import org.corehunter.model.Data;
import org.corehunter.model.impl.EntityWithDescriptionImpl;
import org.corehunter.search.solution.Solution;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public abstract class AbstractObjectiveFunction<
	SolutionType extends Solution, 
	DataType extends Data> 
	extends EntityWithDescriptionImpl implements ObjectiveFunctionWithData<SolutionType, DataType>
{
	private DataType data ;

	public AbstractObjectiveFunction(String name, String description)
	{
		super (name, name, description) ;
                // TODO flushCachedResults();
	}
	
	protected AbstractObjectiveFunction(AbstractObjectiveFunction<SolutionType, DataType> objectiveFuncton) throws CoreHunterException
	{
		super(objectiveFuncton) ;
		
		// Shared data
		setData(objectiveFuncton.getData());
                // TODO flushCachedResults();
	}

	/* (non-Javadoc)
   * @see org.corehunter.measures.ObjectiveFunction#isMinimizing()
   */
	@Override
  public abstract boolean isMinimizing() ;

	/* (non-Javadoc)
   * @see org.corehunter.measures.ObjectiveFunction#calculate(SolutionType)
   */
	@Override
  public abstract double calculate(SolutionType solution) throws CoreHunterException;

	@Override
	public final DataType getData()
  {
  	return data;
  }

	@Override
	public final void setData(DataType data) throws CoreHunterException
  {
		if (this.data != data)
		{
			this.data = data;
			
			handleDataSet() ;
		}
  }
	
	protected void handleDataSet() throws CoreHunterException
  {
	  if (data == null)
	  	throw new CoreHunterException("No data defined!") ;
          
          // flush cached results upon switching datasets
          flushCachedResults();
  }

	@Override
	public void validate() throws CoreHunterException
  {
	  if (data == null)
	  	throw new CoreHunterException("No dataset defined!") ;
	  
	  data.validate() ;
  }
}
