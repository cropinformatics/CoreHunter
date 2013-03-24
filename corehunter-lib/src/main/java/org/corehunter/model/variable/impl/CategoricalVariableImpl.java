package org.corehunter.model.variable.impl;

import java.util.ArrayList;
import java.util.List;

import org.corehunter.model.variable.CategoricalVariable;

public abstract class CategoricalVariableImpl<DataType extends Object> extends VariableImpl implements
    CategoricalVariable<DataType>
{
	private List<DataType> values;

	public CategoricalVariableImpl(String name)
  {
	  super(name);
	  
	  this.values = createDefaultValues() ;
  }
	
	public CategoricalVariableImpl(String name, List<DataType> values)
  {
	  super(name);
	  
	  this.values = createDefaultValues() ;
	  
	  this.values.addAll(values) ;
  }
	
	public CategoricalVariableImpl(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
	  
	  this.values = createDefaultValues() ;
  }
	
	public CategoricalVariableImpl(String uniqueIdentifier, String name, List<DataType> values)
  {
	  super(uniqueIdentifier, name);
	  
	  this.values = createDefaultValues() ;
	  
	  this.values.addAll(values) ;
  }

	protected CategoricalVariableImpl(CategoricalVariable<DataType> variable)
  {
	  super(variable);
	  
	  this.values = createDefaultValues() ;
	  
	  this.values.addAll(variable.getValues()) ;
  }

	@Override
  public List<DataType> getValues()
  {
	  return values ;
  }

  public boolean addValue(DataType value)
  {
		if (!values.contains(value))
			return values.add(value) ;
		else
			return false ;
  }
  
	@Override
  public int hashCode()
  {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result + ((values == null) ? 0 : values.hashCode());
	  return result;
  }

	@Override
  public boolean equals(Object obj)
  {
	  if (this == obj)
		  return true;
	  if (!super.equals(obj))
		  return false;
	  if (!CategoricalVariable.class.isAssignableFrom(obj.getClass()))
		  return false;
	  @SuppressWarnings("rawtypes")
    CategoricalVariable other = (CategoricalVariable) obj;
	  if (values == null)
	  {
		  if (other.getValues() != null)
			  return false;
	  }
	  else
		  if (!values.equals(other.getValues()))
			  return false;
	  return true;
  }

	protected List<DataType> createDefaultValues()
  {
	  return new ArrayList<DataType>() ;
  }
}
