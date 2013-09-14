package org.corehunter.objectivefunction;

import org.corehunter.CoreHunterException;
import org.corehunter.model.EntityWithDescription;
import org.corehunter.model.Validatable;
import org.corehunter.search.solution.Solution;

public interface ObjectiveFunction<SolutionType extends Solution> extends EntityWithDescription, Validatable
{
	public boolean isMinimizing();

	// TODO discussion point should this be called 'evaluate'
	public double calculate(SolutionType solution) throws CoreHunterException ;
	
	/**
	 * Creates a copy of this objective function. Exactly what is copied (the depth of the
	 * copy) depends on the implementation
	 * 
	 * @return a copy of this objective function
	 * @throws CoreHunterException 
	 */
	public ObjectiveFunction<SolutionType> copy() throws CoreHunterException;
}