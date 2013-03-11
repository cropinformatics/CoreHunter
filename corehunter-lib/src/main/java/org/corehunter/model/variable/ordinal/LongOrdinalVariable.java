package org.corehunter.model.variable.ordinal;

import java.util.List;

import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.RangedCategoricalVariableImpl;

public class LongOrdinalVariable extends RangedCategoricalVariableImpl<Long>
{
	public LongOrdinalVariable(String name)
  {
	  super(name);
  }

	public LongOrdinalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }
	
	public LongOrdinalVariable(String name, Long minimumValue,
			Long maximumValue)
  {
	  super(name, minimumValue, maximumValue);
  }

	public LongOrdinalVariable(String uniqueIdentifier, String name,
			Long minimumValue, Long maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
  }
	
	public LongOrdinalVariable(String name, List<Long> values)
  {
	  super(name, values);
  }
	
	public LongOrdinalVariable(String uniqueIdentifier, String name,
      List<Long> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public LongOrdinalVariable(String name, Long minimumValue,
			Long maximumValue, List<Long> values)
  {
	  super(name, minimumValue, maximumValue, values);
  }
	
	public LongOrdinalVariable(String uniqueIdentifier, String name,
			Long minimumValue, Long maximumValue, List<Long> values)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue, values);
  }

	public LongOrdinalVariable(LongOrdinalVariable variable)
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
	  return VariableDataType.LONG ;
  }
}
