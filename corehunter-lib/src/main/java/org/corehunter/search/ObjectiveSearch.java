package org.corehunter.search;

import org.corehunter.objectivefunction.ObjectiveFunction;

public interface ObjectiveSearch<SolutionType extends Solution>
{
	public ObjectiveFunction<SolutionType> getObjectiveFunction();
}
