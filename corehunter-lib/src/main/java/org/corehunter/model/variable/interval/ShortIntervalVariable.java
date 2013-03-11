package org.corehunter.model.variable.interval;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedVariableImpl;

public class ShortIntervalVariable extends RangedVariableImpl<Short>
{
	
	public ShortIntervalVariable(String name)
  {
	  super(name);
  }
	
	public ShortIntervalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public ShortIntervalVariable(String name, Short minimumValue,
			Short maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public ShortIntervalVariable(String uniqueIdentifier, String name,
			Short minimumValue, Short maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }

	public ShortIntervalVariable(ShortIntervalVariable variable)
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
		return VariableDataType.SHORT ;
	}
}
