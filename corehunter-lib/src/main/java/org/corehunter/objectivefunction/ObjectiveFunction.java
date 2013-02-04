package org.corehunter.objectivefunction;

import org.corehunter.CoreHunterException;
import org.corehunter.Validatable;
import org.corehunter.model.EntityWithDescription;
import org.corehunter.search.Solution;

public interface ObjectiveFunction<SolutionType extends Solution> extends EntityWithDescription, Validatable
{
	public boolean isMinimizing();

	public double calculate(SolutionType solution, String cacheId) throws CoreHunterException ;

	public double calculate(SolutionType solution) throws CoreHunterException ;
}