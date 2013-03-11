package org.corehunter.model.variable.nominal;

import java.util.List;

import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.CategoricalVariableImpl;

public class DoubleNominalVariable extends CategoricalVariableImpl<Double> implements Variable
{
	public DoubleNominalVariable(String name)
  {
	  super(name);
  }
	
	public DoubleNominalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public DoubleNominalVariable(String name, List<Double> values)
  {
	  super(name, values);
  }

	public DoubleNominalVariable(String uniqueIdentifier, String name,
      List<Double> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public DoubleNominalVariable(DoubleNominalVariable variable)
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
		return VariableDataType.DOUBLE ;
	}
}
