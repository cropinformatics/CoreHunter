package org.corehunter.search;

import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.solution.Solution;

public interface ObjectiveSearch<SolutionType extends Solution>
{
	public ObjectiveFunction<SolutionType> getObjectiveFunction();
}
