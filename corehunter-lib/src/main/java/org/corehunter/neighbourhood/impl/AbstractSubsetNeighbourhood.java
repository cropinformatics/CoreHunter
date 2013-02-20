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

package org.corehunter.neighbourhood.impl;

import java.util.Random;

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.Neighbourhood;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.search.solution.SubsetSolution;

/**
 * Implements an abstract neighbourhood which defines the neighbours of a given
 * core subset. Depending on the chosen algorithm that uses the neighbourhood,
 * one can generate a random neighbour or the one with the highest value.
 * 
 * @author hermandebeukelaer
 */
public abstract class AbstractSubsetNeighbourhood<IndexType, SolutionType extends SubsetSolution<IndexType>> implements SubsetNeighbourhood<IndexType, SolutionType> 
{

	private Random	random = new Random();
	protected static final double	MIN_TABU_ASPIRATION_PROG	= 10e-9;

	private int	subsetMinimumSize;
	private int	subsetMaximumSize;
  // nr of previous states that can be recovered using undo
  protected int historySize = 1;

	public AbstractSubsetNeighbourhood()
	{

	}
	
	protected AbstractSubsetNeighbourhood(
			AbstractSubsetNeighbourhood<IndexType, SolutionType> singleneighbourhood) throws CoreHunterException
  {
		setSubsetMinimumSize(singleneighbourhood.getSubsetMinimumSize()) ;
		setSubsetMaximumSize(singleneighbourhood.getSubsetMaximumSize()) ;
		setHistorySize(singleneighbourhood.getHistorySize()) ;
  }

	@Override
	public final int getSubsetMinimumSize()
  {
  	return subsetMinimumSize;
  }

	@Override
	public final void setSubsetMinimumSize(int subsetMinimumSize) throws CoreHunterException
  {
		if (this.subsetMinimumSize != subsetMinimumSize)
  	{
			this.subsetMinimumSize = subsetMinimumSize;
		
			handleSubsetMinimumSizeSet() ;
  	}
  }

	@Override
	public final int getSubsetMaximumSize()
  {
  	return subsetMaximumSize;
  }

	@Override
	public synchronized final void setSubsetMaximumSize(int subsetMaximumSize) throws CoreHunterException
  {
		if (this.subsetMaximumSize != subsetMaximumSize)
  	{
			this.subsetMaximumSize = subsetMaximumSize;
		
			handleSubsetMaximumSizeSet() ;
  	}
  }
	
	public final int getHistorySize()
  {
  	return subsetMaximumSize;
  }

	public synchronized final void setHistorySize(int historySize) throws CoreHunterException
  {
		if (this.historySize != historySize)
  	{
			this.historySize = historySize;
		
			handleHistorySizeSet() ;
  	}
  }

	@Override
  public void validate() throws CoreHunterException
  {
		if (subsetMinimumSize <= 0)
	  	throw new CoreHunterException("Subset minimum size must be greater than zero!") ;
	  
		if (subsetMaximumSize <= 0)
	  	throw new CoreHunterException("Subset maximum size must be greater than zero!") ;
	  
		// TODO
		//if (subsetMinimumSize <= getDataset().getSize())
	  //	throw new CoreHunterException("Subset minimum size must be less than or equal to dataset size!") ;
	  
		//if (subsetMaximumSize <= getDataset().getSize())
	  //	throw new CoreHunterException("Subset maximum size must be less than or equal to dataset size!") ;
		
		if (subsetMaximumSize < subsetMinimumSize)
	  	throw new CoreHunterException("Subset maximum size must be greater then or equal to minimum size!") ; 
		
		if (historySize < 0)
	  	throw new CoreHunterException("History size must be greater than or equal to zero!") ;
  }
	
	public final void setRandom(Random random)
  {
  	this.random = random;
  }

	public final Random getRandom()
  {
  	return random;
  }

	protected void handleSubsetMinimumSizeSet() throws CoreHunterException
  {
		if (subsetMinimumSize <= 0)
	  	throw new CoreHunterException("Subset minimum size must be greater than zero!") ;
  }

	protected void handleSubsetMaximumSizeSet() throws CoreHunterException
  {
		if (subsetMaximumSize <= 0)
	  	throw new CoreHunterException("Subset maximum size must be greater than zero!") ;
  }
	
	protected void handleHistorySizeSet() throws CoreHunterException
  {
		if (historySize < 0)
	  	throw new CoreHunterException("History size must be greater than or equal to zero!") ;
  }
	
	protected boolean isBetterScore(boolean isMinimizing, double newScore, double bestNewScore)
  {
	  return isMinimizing ? newScore < bestNewScore : newScore > bestNewScore ;
  }

	protected double getWorstScore(boolean isMinimizing)
  {
	  return isMinimizing ? Double.MAX_VALUE: Double.MIN_VALUE ;
  }

	/* (non-Javadoc)
   * @see org.corehunter.search.Neighbourhood#copy()
   */
	@Override        
	public abstract Neighbourhood<SolutionType> copy();
}
