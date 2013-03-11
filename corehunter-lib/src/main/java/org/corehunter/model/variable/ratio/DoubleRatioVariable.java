package org.corehunter.model.variable.ratio;

import org.corehunter.model.variable.RangedVariable;
import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedVariableImpl;

public class DoubleRatioVariable extends RangedVariableImpl<Double>
{

	public DoubleRatioVariable(String name)
  {
	  super(name);
  }
	
	public DoubleRatioVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public DoubleRatioVariable(String name, Double minimumValue,
      Double maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public DoubleRatioVariable(String uniqueIdentifier, String name,
      Double minimumValue, Double maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }
	
	public DoubleRatioVariable(DoubleRatioVariable variable)
  {
	  super(variable);
  }

	@Override
  public VariableType getType()
  {
	  return VariableType.RATIO ;
  }

	@Override
  public VariableDataType getDataType()
  {
	  return VariableDataType.DOUBLE ;
  }
}
