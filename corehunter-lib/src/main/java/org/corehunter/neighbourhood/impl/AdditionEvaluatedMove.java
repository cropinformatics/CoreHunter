package org.corehunter.neighbourhood.impl;

import org.corehunter.neighbourhood.EvaluatedMove;
import org.corehunter.search.solution.SubsetSolution;

public class AdditionEvaluatedMove<IndexType, SolutionType extends SubsetSolution<IndexType>> extends
    AdditionMove<IndexType, SolutionType> implements
    EvaluatedMove<SolutionType>
{
	private double evaluation ;
	
	public AdditionEvaluatedMove(IndexType addedIndex, double evaluation)
  {
	  super(addedIndex);
	 
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
