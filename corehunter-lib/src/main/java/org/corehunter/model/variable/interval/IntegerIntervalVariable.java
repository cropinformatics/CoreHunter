package org.corehunter.model.variable.interval;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedVariableImpl;

public class IntegerIntervalVariable extends RangedVariableImpl<Integer>
{
	
	public IntegerIntervalVariable(String name)
  {
	  super(name);
  }
	
	public IntegerIntervalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public IntegerIntervalVariable(String name, Integer minimumValue,
			Integer maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public IntegerIntervalVariable(String uniqueIdentifier, String name,
			Integer minimumValue, Integer maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }

	public IntegerIntervalVariable(IntegerIntervalVariable variable)
  {
	  super(variable);
  }

	@Override
	public VariableType getType()
	{
		return VariableType.INTERVAL ;
	}

	@Override
	public VariableDataType getDataType()
	{
		return VariableDataType.INTEGER ;
	}
}
