package org.corehunter.model.variable.nominal;

import java.util.List;

import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.CategoricalVariableImpl;

public class LongNominalVariable extends CategoricalVariableImpl<Long> implements Variable
{
	public LongNominalVariable(String name)
  {
	  super(name);
  }
	
	public LongNominalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public LongNominalVariable(String name, List<Long> values)
  {
	  super(name, values);
  }

	public LongNominalVariable(String uniqueIdentifier, String name,
      List<Long> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public LongNominalVariable(LongNominalVariable variable)
  {
	  super(variable);
  }
	
	@Override
	public VariableType getType()
	{
		return VariableType.NOMINAL ;
	}

	@Override
	public VariableDataType getDataType()
	{
		return VariableDataType.LONG ;
	}
}
