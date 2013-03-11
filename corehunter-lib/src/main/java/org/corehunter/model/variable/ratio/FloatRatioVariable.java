package org.corehunter.model.variable.ratio;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedVariableImpl;

public class FloatRatioVariable extends RangedVariableImpl<Float>
{

	public FloatRatioVariable(String name)
  {
	  super(name);
  }
	
	public FloatRatioVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public FloatRatioVariable(String name, Float minimumValue,
			Float maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public FloatRatioVariable(String uniqueIdentifier, String name,
			Float minimumValue, Float maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }
	
	public FloatRatioVariable(FloatRatioVariable variable)
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
	  return VariableDataType.FLOAT ;
  }
}
