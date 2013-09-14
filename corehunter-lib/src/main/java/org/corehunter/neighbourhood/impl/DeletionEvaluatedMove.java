package org.corehunter.neighbourhood.impl;

import org.corehunter.neighbourhood.EvaluatedIndexedMove;
import org.corehunter.neighbourhood.EvaluatedMove;
import org.corehunter.search.solution.SubsetSolution;

public class DeletionEvaluatedMove<IndexType, SolutionType extends SubsetSolution<IndexType>> extends
	DeletionMove<IndexType, SolutionType> implements
		EvaluatedIndexedMove<IndexType,SolutionType>
{
	private double evaluation ;
	private boolean evaluationSet;
	
	public DeletionEvaluatedMove(IndexType removedIndex)
  {
	  super(removedIndex);
  }
	
	public DeletionEvaluatedMove(IndexType removedIndex, double evaluation)
  {
	  super(removedIndex);
	 
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
		
		evaluationSet = true ;
	}

	@Override
	public final boolean isEvaluationSet()
	{
		return evaluationSet;
	}
}
