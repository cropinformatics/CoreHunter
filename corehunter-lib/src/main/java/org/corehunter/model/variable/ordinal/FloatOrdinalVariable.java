package org.corehunter.model.variable.ordinal;

import java.util.List;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedCategoricalVariableImpl;

public class FloatOrdinalVariable extends RangedCategoricalVariableImpl<Float>
{
	public FloatOrdinalVariable(String name)
  {
	  super(name);
  }

	public FloatOrdinalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }
	
	public FloatOrdinalVariable(String name, Float minimumValue,
			Float maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public FloatOrdinalVariable(String uniqueIdentifier, String name,
			Float minimumValue, Float maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }
	
	public FloatOrdinalVariable(String name, List<Float> values)
  {
	  super(name, values);
  }
	
	public FloatOrdinalVariable(String uniqueIdentifier, String name,
      List<Float> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public FloatOrdinalVariable(String name, Float minimumValue,
			Float maximumValue, List<Float> values)
  {
	  super(name, minimumValue, maximumValue, values);
  }
	
	public FloatOrdinalVariable(String uniqueIdentifier, String name,
			Float minimumValue, Float maximumValue, List<Float> values)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue, values);
  }

	public FloatOrdinalVariable(FloatOrdinalVariable variable)
  {
	  super(variable);
  }

	@Override
  public VariableType getType()
  {
	  return VariableType.ORDINAL ;
  }

	@Override
  public VariableDataType getDataType()
  {
	  return VariableDataType.FLOAT;
  }
}
