package org.corehunter.neighbourhood.impl;

import org.corehunter.neighbourhood.EvaluatedMove;
import org.corehunter.search.solution.SubsetSolution;

public class DeletionEvaluatedMove<IndexType, SolutionType extends SubsetSolution<IndexType>> extends
	DeletionMove<IndexType, SolutionType> implements
    EvaluatedMove<SolutionType>
{
	private double evaluation ;
	
	public DeletionEvaluatedMove(IndexType addedIndex, double evaluation)
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
