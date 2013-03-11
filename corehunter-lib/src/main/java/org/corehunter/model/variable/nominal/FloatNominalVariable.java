package org.corehunter.model.variable.nominal;

import java.util.List;

import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.CategoricalVariableImpl;

public class FloatNominalVariable extends CategoricalVariableImpl<Float> implements Variable
{
	public FloatNominalVariable(String name)
  {
	  super(name);
  }
	
	public FloatNominalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public FloatNominalVariable(String name, List<Float> values)
  {
	  super(name, values);
  }

	public FloatNominalVariable(String uniqueIdentifier, String name,
      List<Float> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public FloatNominalVariable(FloatNominalVariable variable)
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
		return VariableDataType.FLOAT;
	}
}
