package org.corehunter.model.variable.interval;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedVariableImpl;

public class FloatIntervalVariable extends RangedVariableImpl<Float>
{
	
	public FloatIntervalVariable(String name)
  {
	  super(name);
  }
	
	public FloatIntervalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public FloatIntervalVariable(String name, Float minimumValue,
			Float maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public FloatIntervalVariable(String uniqueIdentifier, String name,
			Float minimumValue, Float maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }

	public FloatIntervalVariable(FloatIntervalVariable variable)
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
		return VariableDataType.FLOAT ;
	}
}
