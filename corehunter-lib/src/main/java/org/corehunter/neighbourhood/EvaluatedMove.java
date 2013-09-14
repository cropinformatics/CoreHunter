package org.corehunter.neighbourhood;

import org.corehunter.search.solution.Solution;

public interface EvaluatedMove<SolutionType extends Solution> extends Move<SolutionType>
{
	/** 
	 * Gets the evaluation solution after the move was performed
	 * @return
	 */
	double getEvaluation() ;
}
