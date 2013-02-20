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

package org.corehunter.ssr;

import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.solution.SubsetSolution;

/**
 * @author hermandebeukelaer
 */
public class ExternalDistanceMeasureSSR<IndexType> extends AbstractAccessionSSRObjectiveFunction<IndexType>
{

	public ExternalDistanceMeasureSSR()
	{
		this("EX", "External Distance Measure");
	}

	public ExternalDistanceMeasureSSR(String name, String description)
	{
		super(name, description);
	}

	protected ExternalDistanceMeasureSSR(ExternalDistanceMeasureSSR<IndexType> objectiveFunction) 
	{
		super(objectiveFunction) ;
	}
	
	@Override
  public final ObjectiveFunction<SubsetSolution<IndexType>> copy()
  {
	  return new ExternalDistanceMeasureSSR<IndexType>(this);
  }
	
	@Override
	public final double calculate(SubsetSolution<IndexType> solution)
	{
		try
		{
			double sum = 0.0;
			for (IndexType index : solution.getIndices())
			{
				sum += getData().getExternalDistance(index);
			}
			sum = sum / solution.getSize();
			return sum;
		}
		catch (NullPointerException ne)
		{
			System.err
			    .println("No external distances present in dataset! Cannot use EX measure.");
			System.exit(1);
		}
		return -1;
	}
}
