package org.corehunter.model.variable;

import org.corehunter.model.impl.EntityImpl;

public class VariableImpl extends EntityImpl implements Variable
{
	private VariableType type;
	private VariableDataType dataType;

	public VariableImpl(String name)
  {
	  super(name);
  }

	public VariableImpl(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }
	
	public VariableImpl(String name, VariableType type)
  {
	  super(name);
	  
	  setType(type) ;
	  setDataType(type.getDefaultDataType()) ;
  }

	public VariableImpl(String uniqueIdentifier, String name, VariableType type)
  {
	  super(uniqueIdentifier, name);
	  
	  setType(type) ;
	  setDataType(type.getDefaultDataType()) ;
  }
	
	public VariableImpl(String name, VariableType type, VariableDataType dataType)
  {
	  super(name);
	  
	  setType(type) ;
	  setDataType(dataType) ;
  }

	public VariableImpl(String uniqueIdentifier, String name, VariableType type, VariableDataType dataType)
  {
	  super(uniqueIdentifier, name);
	  
	  setType(type) ;
	  setDataType(dataType) ;
  }

	public VariableImpl(Variable variable)
  {
	  super(variable);
	  
	  setType(variable.getType()) ;
	  setDataType(variable.getDataType()) ;
  }
	
	@Override
	public VariableType getType()
	{
		return type;
	}
	
	@Override
	public final void setType(VariableType type)
	{
		this.type = type;
	}

	@Override
	public VariableDataType getDataType()
	{
		return dataType;
	}

	@Override
	public final void setDataType(VariableDataType dataType)
	{
		this.dataType = dataType;
	}
}
