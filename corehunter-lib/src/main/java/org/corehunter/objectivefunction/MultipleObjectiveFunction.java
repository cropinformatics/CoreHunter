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

package org.corehunter.objectivefunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Data;
import org.corehunter.model.impl.EntityWithDescriptionImpl;
import org.corehunter.search.Solution;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public class MultipleObjectiveFunction<SolutionType extends Solution, DatasetType extends Data> 
	extends EntityWithDescriptionImpl implements ObjectiveFunction<SolutionType>
{
	private List<ObjectiveFunction<SolutionType>> objectiveFunctions;
	private Map<String, Integer>	objectiveFunctionIndices;
	private List<Double>	       objectiveFunctionWeights;

	public MultipleObjectiveFunction()
	{
		this("MO", "A multiple objective function, combining two or more of other objective functions)");
	}

	public MultipleObjectiveFunction(String name, String description)
	{
		super(name) ;
		objectiveFunctions = new ArrayList<ObjectiveFunction<SolutionType>>();
		objectiveFunctionIndices = new HashMap<String, Integer>();
		objectiveFunctionWeights = new ArrayList<Double>();
	}

	@Override
  public boolean isMinimizing()
  {
	  return false;
  }

	@Override
	public final double calculate(SolutionType solution) throws CoreHunterException
	{
		return calculate(solution, null);
	}

	@Override
	public final double calculate(SolutionType solution, String cacheId) throws CoreHunterException
	{
		double score = 0.0;

		for (int i = 0; i < objectiveFunctions.size(); i++)
		{
			ObjectiveFunction<SolutionType> m = objectiveFunctions.get(i);
			double s;
			if (cacheId != null)
			{
				s = m.calculate(solution, cacheId);
			}
			else
			{
				s = m.calculate(null);
			}

			if (m.isMinimizing())
			{
				s = -s;
			}

			double weight = objectiveFunctionWeights.get(i).doubleValue();
			score += s * weight;
		}

		return score;
	}

	public final Map<String, Double> componentScores(SolutionType solution) throws CoreHunterException
	{
		return componentScores(solution, null);
	}

	public final Map<String, Double> componentScores(SolutionType solution,
	    String cacheId) throws CoreHunterException
	{
		Map<String, Double> scores = new HashMap<String, Double>();

		for (int i = 0; i < objectiveFunctions.size(); i++)
		{
			ObjectiveFunction<SolutionType> m = objectiveFunctions.get(i);
			double s;
			if (cacheId != null)
			{
				s = m.calculate(solution, cacheId);
			}
			else
			{
				s = m.calculate(null);
			}
			scores.put(m.getName(), new Double(s));
		}
		return scores;
	}

	public final void addObjectiveFunction(ObjectiveFunction<SolutionType> objectiveFunction, double weight)
	    throws CoreHunterException
	{
		if (objectiveFunctionIndices.get(objectiveFunction.getUniqueIdentifier()) != null)
		{
			throw new DuplicateMeasureException(
			    "Objective Function : " + objectiveFunction.getName() + " having the identifier " + objectiveFunction.getUniqueIdentifier()
			        + " already exists.");
		}
		
		if (weight < 0 || weight > 1)
			throw new CoreHunterException("Weight for objective Function " + objectiveFunction.getName() + " must be between zero and one : " + weight ) ;

		objectiveFunctions.add(objectiveFunction);
		objectiveFunctionIndices.put(objectiveFunction.getUniqueIdentifier(), objectiveFunctions.size());
		objectiveFunctionWeights.add(new Double(weight));
	}

	@Override
  public void validate() throws CoreHunterException
  {
	  Iterator<ObjectiveFunction<SolutionType>> iterator = objectiveFunctions.iterator() ;
	  
	  while (iterator.hasNext())
	  	iterator.next().validate() ;
  }
	
	
}
