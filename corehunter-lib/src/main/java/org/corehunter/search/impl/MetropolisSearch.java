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

import static org.corehunter.Constants.INVALID_NUMBER_OF_STEPS;
import static org.corehunter.Constants.INVALID_TIME;
import static org.corehunter.Constants.INVALID_TEMPERATURE;

import java.text.DecimalFormat;

import org.corehunter.CoreHunterException;
import org.corehunter.model.IndexedData;
import org.corehunter.neighbourhood.Move;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.SubsetSolution;

/**
 * @author hermandebeukelaer
 */
public class MetropolisSearch<
	IndexType,
	SolutionType extends SubsetSolution<IndexType>, 
	DatasetType extends IndexedData<IndexType>,
	NeighbourhoodType extends SubsetNeighbourhood<IndexType, SolutionType>> 
	extends AbstractSubsetNeighbourhoodSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>
{
	private final static double	K_b	= 7.213475e-7;

	private double temperature = INVALID_TEMPERATURE ;

	private int numberOfSteps = INVALID_NUMBER_OF_STEPS ;

	private long runtime = INVALID_TIME ;

	private int	accepts;
	private int	rejects;
	private int	improvements;
	private int	totalSteps;
	
	private boolean continueSearch ;

	public MetropolisSearch()
	{
		accepts = 0 ;
		rejects = 0 ;
		improvements = 0 ;
		totalSteps = 0;
	}

	protected MetropolisSearch(MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> search) throws CoreHunterException
  {
  	super(search) ;
  	
		accepts = 0 ;
		rejects = 0 ;
		improvements = 0 ;
		totalSteps = 0;
  	
		setTemperature(search.getTemperature()) ;
		setNumberOfSteps(search.getNumberOfSteps()) ;
		setRuntime(search.getRuntime()) ;
  }
	
	@Override
  public Search<SolutionType> copy() throws CoreHunterException
  {
	  return new MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType>(this);
  }

	public final double getTemperature()
	{
		return temperature;
	}

	public final void setTemperature(double temperature) throws CoreHunterException
	{
		if (this.temperature != temperature)
		{
			this.temperature = temperature;
			
			handleTemperatureSet() ;
		}
	}

	public final int getNumberOfSteps()
  {
  	return numberOfSteps;
  }

	public final void setNumberOfSteps(int numberOfSteps) throws CoreHunterException
  {
		if (this.numberOfSteps != numberOfSteps)
		{
			this.numberOfSteps = numberOfSteps;
			
			handleNumberOfStepsSet() ;
		}
  }

	public final long getRuntime()
  {
  	return runtime;
  }

	public final void setRuntime(long runtime) throws CoreHunterException
  {
		if (this.runtime != runtime)
		{
			this.runtime = runtime;
			
			handleRuntimeSet() ;
		}
  }
	
	@Override
  protected void validate() throws CoreHunterException
  {
	  super.validate();
	  
	  if (temperature < 0)
	  	throw new CoreHunterException("Temperature can not be less than zero!") ;
  }

	@Override
	public String toString()
	{
		DecimalFormat df = new DecimalFormat("#.##");
		return " (T = " + df.format(temperature) + ")";
	}
	
	protected void handleTemperatureSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Temperature can not be set while search in process") ;
		
	  if (temperature < 0)
	  	throw new CoreHunterException("Temperature can not be less than zero!") ;
  }

	protected void handleNumberOfStepsSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Number Of Steps can not be set while search in process") ;
  }

	protected void handleRuntimeSet() throws CoreHunterException
  {
		if (SearchStatus.STARTED.equals(getStatus()))
	  	throw new CoreHunterException("Runtime can not be set while search in process") ;
  }
	
	@Override
	protected void runSearch() throws CoreHunterException
	{
		continueSearch = true ;
		setStuck(true) ;
		
		int i = 0;
		double newEvalution ;
		boolean acceptMove = true ;
		double size ;
		Move<SolutionType> move ;

		size = getSolution().getSubsetSize() ;
		
		setEvaluation(getObjectiveFunction().calculate(getSolution()));

		while (continueSearch)
		{
			move = getNeighbourhood().performRandomMove(getSolution());
			newEvalution = getObjectiveFunction().calculate(getSolution());
			
			double deltaScore = getDeltaScore(newEvalution, getEvaluation()) ;

			if (deltaScore > 0)
			{
				// accept new solution!
				improvements++;
				acceptMove = true ;
			}
			else
			{
				double deltaSize = getSolution().getSubsetSize() - size;

				if (deltaSize > 0) // TODO should we always reject large solutions or worst evaluations!
				{
					// new solution is bigger than old solution and has no better
					// evaluation --> reject new solution, stick with old solution
					
					acceptMove = false ;
				}
				else
				{
					// new solution is not bigger, but has worse evaluation
					// accept or reject new solution based on temperature
					double P = Math.exp(deltaScore / (temperature * K_b));
					double Q = getRandom().nextDouble();
					
					if (Q > P)
					{
						acceptMove = false ;
					}
					else
					{
						// accept new solution!
						acceptMove = true ;
					}
				}
			}
			
			if (acceptMove)
			{
				handleNewBestSolution(getSolution(), getEvaluation()) ;
				setStuck(false) ;
				setEvaluation(newEvalution) ;
				size = getSolution().getSubsetSize() ;
	
				accepts++;
			}
			else
			{
				getNeighbourhood().undoMove(move, getSolution()) ;
				rejects++;
			}

			totalSteps++;
			i++;
			
			if (continueSearch)
			{
				if (numberOfSteps > 0)
					continueSearch = continueSearch && i < numberOfSteps ;
				
				if (runtime > 0)
					continueSearch = continueSearch && getSearchTime() < runtime ;
			}
		}
	}

	public void swapTemperature(MetropolisSearch<IndexType, SolutionType, DatasetType, NeighbourhoodType> other) throws CoreHunterException
	{
		double temperature = getTemperature();
		setTemperature(other.getTemperature()) ;
		other.setTemperature(temperature) ;
	}
	
	@Override
  protected void stopSearch() throws CoreHunterException
  {
		continueSearch = false ;
  }
}
