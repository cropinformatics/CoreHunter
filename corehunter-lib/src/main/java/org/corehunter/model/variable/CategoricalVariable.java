package org.corehunter.model.variable;

import java.util.List;

public interface CategoricalVariable<DataType extends Object> extends Variable
{
	public List<DataType> getValues() ;

	public boolean addValue(DataType value);
}
