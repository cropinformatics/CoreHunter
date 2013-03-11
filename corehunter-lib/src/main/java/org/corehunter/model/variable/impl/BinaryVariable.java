package org.corehunter.model.variable.impl;

import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;

public class BinaryVariable extends VariableImpl implements Variable
{
	public BinaryVariable(String name)
  {
	  super(name);
  }

	public BinaryVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public BinaryVariable(BinaryVariable variable)
  {
	  super(variable);
  }

	@Override
	public VariableType getType()
	{
		return VariableType.BINARY;
	}

	@Override
	public VariableDataType getDataType()
	{
		return VariableDataType.BOOLEAN;
	}
}
