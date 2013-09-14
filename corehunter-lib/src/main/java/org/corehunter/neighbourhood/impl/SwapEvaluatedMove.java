package org.corehunter.neighbourhood.impl;

import org.corehunter.neighbourhood.EvaluatedMove;
import org.corehunter.search.solution.SubsetSolution;

public class SwapEvaluatedMove<IndexType, SolutionType extends SubsetSolution<IndexType>> extends
    SwapMove<IndexType, SolutionType> implements
    EvaluatedMove<SolutionType>
{
	private double evaluation ;
	
	public SwapEvaluatedMove(IndexType addedIndex, IndexType removedIndex, double evaluation)
  {
	  super(addedIndex, removedIndex);
	 
	  setEvaluation(evaluation) ;
  }

	@Override
	public final double getEvaluation()
	{
		return evaluation;
	}

	public final void setEvaluation(double evaluation)
	{
		this.evaluation = evaluation;
	}

}
