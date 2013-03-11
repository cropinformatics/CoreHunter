package org.corehunter.model.variable.interval;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedVariableImpl;

public class DoubleIntervalVariable extends RangedVariableImpl<Double>
{
	
	public DoubleIntervalVariable(String name)
  {
	  super(name);
  }
	
	public DoubleIntervalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public DoubleIntervalVariable(String name, Double minimumValue,
      Double maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public DoubleIntervalVariable(String uniqueIdentifier, String name,
      Double minimumValue, Double maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }

	public DoubleIntervalVariable(DoubleIntervalVariable variable)
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
		return VariableDataType.DOUBLE ;
	}
}
