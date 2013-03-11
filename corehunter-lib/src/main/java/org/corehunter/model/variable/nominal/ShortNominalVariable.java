package org.corehunter.model.variable.nominal;

import java.util.List;

import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.CategoricalVariableImpl;

public class ShortNominalVariable extends CategoricalVariableImpl<Short> implements Variable
{
	public ShortNominalVariable(String name)
  {
	  super(name);
  }
	
	public ShortNominalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public ShortNominalVariable(String name, List<Short> values)
  {
	  super(name, values);
  }

	public ShortNominalVariable(String uniqueIdentifier, String name,
      List<Short> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public ShortNominalVariable(ShortNominalVariable variable)
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
		return VariableDataType.SHORT ;
	}
}
