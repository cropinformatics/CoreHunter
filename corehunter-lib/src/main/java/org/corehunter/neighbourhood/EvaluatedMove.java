package org.corehunter.neighbourhood;

import org.corehunter.search.solution.Solution;

public interface EvaluatedMove<SolutionType extends Solution> extends Move<SolutionType>
{
	/** 
	 * Gets the evaluation solution after the move was performed
	 * @return the evaluation solution after the move was performed
	 */
	double getEvaluation() ;

	/**
	 * @return <code>true</code> if the evaluation has been set, <code>false</code> otherwise
	 */
	boolean isEvaluationSet();
}
