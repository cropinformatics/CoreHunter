package org.corehunter.model.variable.ordinal;

import java.util.List;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedCategoricalVariableImpl;

public class IntegerOrdinalVariable extends RangedCategoricalVariableImpl<Integer>
{
	public IntegerOrdinalVariable(String name)
  {
	  super(name);
  }

	public IntegerOrdinalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }
	
	public IntegerOrdinalVariable(String name, Integer minimumValue,
			Integer maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public IntegerOrdinalVariable(String uniqueIdentifier, String name,
			Integer minimumValue, Integer maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }
	
	public IntegerOrdinalVariable(String name, List<Integer> values)
  {
	  super(name, values);
  }
	
	public IntegerOrdinalVariable(String uniqueIdentifier, String name,
      List<Integer> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public IntegerOrdinalVariable(String name, Integer minimumValue,
			Integer maximumValue, List<Integer> values)
  {
	  super(name, minimumValue, maximumValue, values);
  }
	
	public IntegerOrdinalVariable(String uniqueIdentifier, String name,
			Integer minimumValue, Integer maximumValue, List<Integer> values)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue, values);
  }

	public IntegerOrdinalVariable(IntegerOrdinalVariable variable)
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
	  return VariableDataType.INTEGER ;
  }
}
