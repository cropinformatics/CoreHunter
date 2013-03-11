package org.corehunter.model.variable.nominal;

import java.util.List;

import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.CategoricalVariableImpl;

public class IntegerNominalVariable extends CategoricalVariableImpl<Integer> implements Variable
{
	public IntegerNominalVariable(String name)
  {
	  super(name);
  }
	
	public IntegerNominalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public IntegerNominalVariable(String name, List<Integer> values)
  {
	  super(name, values);
  }

	public IntegerNominalVariable(String uniqueIdentifier, String name,
      List<Integer> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public IntegerNominalVariable(IntegerNominalVariable variable)
  {
	  super(variable);
  }
	
	@Override
	public VariableType getType()
	{
		return VariableType.NOMINAL ;
	}

	@Override
	public VariableDataType getDataType()
	{
		return VariableDataType.INTEGER ;
	}
}
