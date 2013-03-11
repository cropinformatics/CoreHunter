package org.corehunter.model.variable.interval;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedVariableImpl;

public class LongIntervalVariable extends RangedVariableImpl<Long>
{
	
	public LongIntervalVariable(String name)
  {
	  super(name);
  }
	
	public LongIntervalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public LongIntervalVariable(String name, Long minimumValue,
			Long maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public LongIntervalVariable(String uniqueIdentifier, String name,
			Long minimumValue, Long maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }

	public LongIntervalVariable(LongIntervalVariable variable)
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
		return VariableDataType.LONG ;
	}
}
