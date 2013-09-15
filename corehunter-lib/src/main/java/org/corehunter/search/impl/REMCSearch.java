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

import static org.corehunter.Constants.K_b2;

import java.util.ArrayList;
import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.SubsetSolution;

public class REMCSearch<IndexType, SolutionType extends SubsetSolution<IndexType>, NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>>
    extends
    AbstractParallelSubsetNeighbourhoodSearch<IndexType, SolutionType, NeighbourhoodType, MetropolisSearch<IndexType, SolutionType, NeighbourhoodType>>
{

	private int	    numberOfReplicas;
	private double	minimumTemperature;
	private double	maximumTemperature;
	private Long	  numberOfMetropolisStepsPerRound;

	public REMCSearch()
	{
		super();
	}

	protected REMCSearch(
	    REMCSearch<IndexType, SolutionType, NeighbourhoodType> search)
	    throws CoreHunterException
	{
		super(search);
		setNumberOfReplicas(search.getNumberOfReplicas());
		setMinimumTemperature(search.getMinimumTemperature());
		setMaximumTemperature(search.getMaximumTemperature());
		setNumberOfMetropolisStepsPerRound(search
		    .getNumberOfMetropolisStepsPerRound());
	}

	@Override
	public Search<SolutionType> copy() throws CoreHunterException
	{
		return new REMCSearch<IndexType, SolutionType, NeighbourhoodType>(this);
	}

	public final int getNumberOfReplicas()
	{
		return numberOfReplicas;
	}

	public final void setNumberOfReplicas(int numberOfReplicas)
	    throws CoreHunterException
	{
		if (this.numberOfReplicas != numberOfReplicas)
		{
			this.numberOfReplicas = numberOfReplicas;
			handleNumberOfReplicasSet();
		}
	}

	public final double getMinimumTemperature()
	{
		return minimumTemperature;
	}

	public final void setMinimumTemperature(double minimumTemperature)
	    throws CoreHunterException
	{
		if (this.minimumTemperature != minimumTemperature)
		{
			this.minimumTemperature = minimumTemperature;
			handleMinimumTemperatureSet();
		}
	}

	public final double getMaximumTemperature()
	{
		return maximumTemperature;
	}

	public final void setMaximumTemperature(double maximumTemperature)
	    throws CoreHunterException
	{
		if (this.maximumTemperature != maximumTemperature)
		{
			this.maximumTemperature = maximumTemperature;
			handleMaximumTemperatureSet();
		}
	}

	public final Long getNumberOfMetropolisStepsPerRound()
	{
		return numberOfMetropolisStepsPerRound;
	}

	public final void setNumberOfMetropolisStepsPerRound(Long numberOfSteps)
	    throws CoreHunterException
	{
		if (this.numberOfMetropolisStepsPerRound != numberOfSteps)
		{
			this.numberOfMetropolisStepsPerRound = numberOfSteps;
			handleNumberOfMetropolisStepsPerRoundSet();
		}
	}

	protected void handleNumberOfReplicasSet() throws CoreHunterException
	{
		if (numberOfReplicas <= 0)
		{
			throw new CoreHunterException(
			    "Number Of Replicas can not be less than or equal to zero!");
		}
		if (SearchStatus.STARTED.equals(getStatus()))
		{
			throw new CoreHunterException(
			    "Number Of Replicas can not be set while search in process");
		}
	}

	protected void handleMinimumTemperatureSet() throws CoreHunterException
	{
		if (minimumTemperature < 0)
		{
			throw new CoreHunterException(
			    "Minimum Temperature can not be less than zero!");
		}
		if (SearchStatus.STARTED.equals(getStatus()))
		{
			throw new CoreHunterException(
			    "Minimum Temperature can not be set while search in process");
		}
	}

	protected void handleMaximumTemperatureSet() throws CoreHunterException
	{
		if (maximumTemperature < 0)
		{
			throw new CoreHunterException(
			    "Maximum Temperature can not be less than zero!");
		}
		if (SearchStatus.STARTED.equals(getStatus()))
		{
			throw new CoreHunterException(
			    "Maximum Temperature can not be set while search in process");
		}
	}

	protected void handleNumberOfMetropolisStepsPerRoundSet()
	    throws CoreHunterException
	{
		if (numberOfMetropolisStepsPerRound == null || numberOfMetropolisStepsPerRound <= 0)
		{
			throw new CoreHunterException(
			    "Number of Metropolis Steps per Round can not be less than or equal to zero!");
		}
		if (SearchStatus.STARTED.equals(getStatus()))
		{
			throw new CoreHunterException(
			    "Number of Metropolis Steps per Round can not be set while search in process");
		}
	}

	@Override
	protected void validate() throws CoreHunterException
	{
		super.validate();

		if (numberOfReplicas <= 0)
		{
			throw new CoreHunterException(
			    "Number Of Replicas can not be less than or equal to zero!");
		}

		if (minimumTemperature < 0)
		{
			throw new CoreHunterException(
			    "Minimum Temperature can not be less than zero!");
		}

		if (maximumTemperature < 0)
		{
			throw new CoreHunterException(
			    "Maximum Temperature can not be less than zero!");
		}

		if (maximumTemperature < minimumTemperature)
		{
			throw new CoreHunterException(
			    "Maximum Temperature can not be less than Miimum Temperature!");
		}

		if (numberOfMetropolisStepsPerRound <= 0)
		{
			throw new CoreHunterException(
			    "Number of Metropolis Steps per Round can not be less than or equal to zero!");
		}
	}

	@SuppressWarnings("unchecked")
	protected void runSearch() throws CoreHunterException
	{
		List<MetropolisSearch<IndexType, SolutionType, NeighbourhoodType>> replicas = new ArrayList<MetropolisSearch<IndexType, SolutionType, NeighbourhoodType>>(
		    numberOfReplicas);

		// initialise searches
		for (int i = 0; i < numberOfReplicas; i++)
		{
			double temperature = minimumTemperature + i
			    * (maximumTemperature - minimumTemperature) / (numberOfReplicas - 1);
			
			replicas.add(createMetropolisSearch(
					(SolutionType) getCurrentSolution().copy(), 
					getObjectiveFunction().copy(),
			    (NeighbourhoodType) getNeighbourhood().copy(),
			    numberOfMetropolisStepsPerRound, 
			    null, 
			    temperature));
		}

		int swapBase = 0;

		registerSubSearches(replicas);

		while (canContinue())
		{
			startSubSearches(replicas); // returns when all are complete

			// consider swapping temperatures of adjacent replicas
			for (int i = swapBase; i < numberOfReplicas - 1; i += 2)
			{
				MetropolisSearch<IndexType, SolutionType, NeighbourhoodType> m = replicas
				    .get(i);
				MetropolisSearch<IndexType, SolutionType, NeighbourhoodType> n = replicas
				    .get(i + 1);

				double B_m = 1.0 / (K_b2 * m.getTemperature());
				double B_n = 1.0 / (K_b2 * n.getTemperature());
				double B_diff = B_n - B_m;
				// TODO check if this the right around +ve delta here means m is better
				// than n
				double E_delta = getDeltaScore(m.getBestSolutionEvaluation(),
				    n.getBestSolutionEvaluation());

				boolean swap = false;

				if (E_delta <= 0)
				{
					swap = true;
				}
				else
				{
					double p = getRandom().nextDouble();

					if (Math.exp(B_diff * E_delta) > p)
					{
						swap = true;
					}
				}

				if (swap)
				{
					m.swapTemperature(n);
					MetropolisSearch<IndexType, SolutionType, NeighbourhoodType> temp = replicas
					    .get(i);
					replicas.set(i, replicas.get(i + 1));
					replicas.set(i + 1, temp);
				}
			}

			swapBase = 1 - swapBase;

			incStepsTaken();
		}

		unregisterSubSearches(replicas);
	}

	protected MetropolisSearch<IndexType, SolutionType, NeighbourhoodType> createMetropolisSearch(
	    SolutionType solution, ObjectiveFunction<SolutionType> objectiveFunction,
	    NeighbourhoodType neighbourhood, Long numberOfSteps, Long runtime,
	    double temperature) throws CoreHunterException
	{
		MetropolisSearch<IndexType, SolutionType, NeighbourhoodType> subsearch = new MetropolisSearch<IndexType, SolutionType, NeighbourhoodType>();

		subsearch.setIndices(getIndices());
		subsearch.setInitialSolution(solution);
		subsearch.setObjectiveFunction(objectiveFunction);
		subsearch.setNeighbourhood(neighbourhood);
		subsearch.setMaximumNumberOfSteps(numberOfSteps);
		subsearch.setRuntimeLimit(runtime);
		subsearch.setTemperature(temperature);

		return subsearch;
	}
}
