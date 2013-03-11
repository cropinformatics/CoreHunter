package org.corehunter.model.variable.nominal;

import java.util.List;

import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.impl.CategoricalVariableImpl;

public class StringNominalVariable extends CategoricalVariableImpl<String> implements Variable
{
	public StringNominalVariable(String name)
  {
	  super(name);
  }
	
	public StringNominalVariable(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	public StringNominalVariable(String name, List<String> values)
  {
	  super(name, values);
  }

	public StringNominalVariable(String uniqueIdentifier, String name,
      List<String> values)
  {
	  super(uniqueIdentifier, name, values);
  }

	public StringNominalVariable(StringNominalVariable variable)
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
		return VariableDataType.STRING ;
	}
}
