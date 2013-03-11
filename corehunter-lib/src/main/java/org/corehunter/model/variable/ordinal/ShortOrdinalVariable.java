package org.corehunter.model.variable.ordinal;

import java.util.List;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedCategoricalVariableImpl;

public class ShortOrdinalVariable extends RangedCategoricalVariableImpl<Short>
{
	public ShortOrdinalVariable(String name)
  {
	  super(name);
  }

	public ShortOrdinalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }
	
	public ShortOrdinalVariable(String name, Short minimumValue,
			Short maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public ShortOrdinalVariable(String uniqueIdentifier, String name,
			Short minimumValue, Short maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }
	
	public ShortOrdinalVariable(String name, List<Short> values)
  {
	  super(name, values);
  }
	
	public ShortOrdinalVariable(String uniqueIdentifier, String name,
      List<Short> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public ShortOrdinalVariable(String name, Short minimumValue,
			Short maximumValue, List<Short> values)
  {
	  super(name, minimumValue, maximumValue, values);
  }
	
	public ShortOrdinalVariable(String uniqueIdentifier, String name,
			Short minimumValue, Short maximumValue, List<Short> values)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue, values);
  }

	public ShortOrdinalVariable(ShortOrdinalVariable variable)
  {
	  super(variable);
  }

	@Override
  public VariableType getType()
  {
	  return VariableType.ORDINAL;
  }

	@Override
  public VariableDataType getDataType()
  {
	  return VariableDataType.SHORT;
  }
}
