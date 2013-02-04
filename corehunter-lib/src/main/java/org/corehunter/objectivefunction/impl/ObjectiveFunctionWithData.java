package org.corehunter.objectivefunction.impl;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Data;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.Solution;

public interface ObjectiveFunctionWithData<SolutionType extends Solution, DataType extends Data> extends
    ObjectiveFunction<SolutionType> 
{
	public DataType getData();

	public void setData(DataType data) throws CoreHunterException;
}
