package org.corehunter.model.variable.ordinal;

import java.util.List;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedCategoricalVariableImpl;

public class DoubleOrdinalVariable extends RangedCategoricalVariableImpl<Double>
{
	public DoubleOrdinalVariable(String name)
  {
	  super(name);
  }

	public DoubleOrdinalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }
	
	public DoubleOrdinalVariable(String name, Double minimumValue,
      Double maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public DoubleOrdinalVariable(String uniqueIdentifier, String name,
      Double minimumValue, Double maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }
	
	public DoubleOrdinalVariable(String name, List<Double> values)
  {
	  super(name, values);
  }
	
	public DoubleOrdinalVariable(String uniqueIdentifier, String name,
      List<Double> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public DoubleOrdinalVariable(String name, Double minimumValue,
      Double maximumValue, List<Double> values)
  {
	  super(name, minimumValue, maximumValue, values);
  }
	
	public DoubleOrdinalVariable(String uniqueIdentifier, String name,
      Double minimumValue, Double maximumValue, List<Double> values)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue, values);
  }

	public DoubleOrdinalVariable(DoubleOrdinalVariable variable)
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
	  return VariableDataType.DOUBLE;
  }
}
