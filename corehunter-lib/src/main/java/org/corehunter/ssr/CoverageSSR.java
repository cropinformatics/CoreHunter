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


import org.corehunter.CoreHunterException;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.solution.SubsetSolution;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public final class CoverageSSR<IndexType> extends AbstractAccessionSSRObjectiveFunction<IndexType>
{
	private ProportionNonInformativeAllelesSSR<IndexType>	pn;

	public CoverageSSR()
	{
		this("CV", "Trait coverage relative to collection");
	}

	public CoverageSSR(String name, String description)
	{
		super(name, description);
		pn = new ProportionNonInformativeAllelesSSR<IndexType>();
	}

	protected CoverageSSR(CoverageSSR<IndexType> objectiveFunction) 
	{
		super(objectiveFunction) ;
	}
	
	@Override
  public ObjectiveFunction<SubsetSolution<IndexType>> copy()
  {
	  return new CoverageSSR<IndexType>(this);
  }

	@Override
	public final double calculate(SubsetSolution<IndexType> solution) throws CoreHunterException
	{
		return 1.0 - pn.calculate(solution);
	}
}
