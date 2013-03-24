package org.corehunter.model.variable.impl;

import org.corehunter.model.impl.EntityWithDescriptionImpl;
import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;

public abstract class VariableImpl extends EntityWithDescriptionImpl implements Variable
{
	public VariableImpl(String name)
  {
	  super(name);
  }

	public VariableImpl(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }

	protected VariableImpl(Variable variable)
  {
	  super(variable);
  }
	
	@Override
	public abstract VariableType getType() ;


	@Override
	public abstract VariableDataType getDataType() ;
	
	
}
