package org.corehunter.model.variable.impl;

import java.util.ArrayList;
import java.util.List;

import org.corehunter.model.variable.CategoricalVariable;
import org.corehunter.model.variable.RangedCategoricalVariable;

public abstract class RangedCategoricalVariableImpl<DataType extends Number> extends
    RangedVariableImpl<DataType> implements RangedCategoricalVariable<DataType>
{
	private List<DataType> values;
	
	public RangedCategoricalVariableImpl(String name)
  {
	  super(name);
	  
	  this.values = createDefaultValues() ;
  }
	
	public RangedCategoricalVariableImpl(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
	  
	  this.values = createDefaultValues() ;
  }

	public RangedCategoricalVariableImpl(String name, DataType minimumValue,
      DataType maximumValue)
  {
	  super(name, minimumValue, maximumValue);
	  
	  this.values = createDefaultValues() ;
  }

	public RangedCategoricalVariableImpl(String uniqueIdentifier, String name,
      DataType minimumValue, DataType maximumValue)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
	  
	  this.values = createDefaultValues() ;
  }
	
	public RangedCategoricalVariableImpl(String name, List<DataType> values)
  {
	  super(name);
	  
	  this.values = createDefaultValues() ;
	  
	  values.addAll(values) ;
  }
	
	public RangedCategoricalVariableImpl(String uniqueIdentifier, String name, List<DataType> values)
  {
	  super(uniqueIdentifier, name);
	  
	  this.values = createDefaultValues() ;
	  
	  this.values.addAll(values) ;
  }
	
	public RangedCategoricalVariableImpl(String name, DataType minimumValue,
      DataType maximumValue, List<DataType> values)
  {
	  super(name, minimumValue, maximumValue);
	  
	  this.values = createDefaultValues() ;
	  
	  this.values.addAll(values) ;
  }

	public RangedCategoricalVariableImpl(String uniqueIdentifier, String name,
      DataType minimumValue, DataType maximumValue, List<DataType> values)
  {
	  super(uniqueIdentifier, name, minimumValue, maximumValue);
	  
	  this.values = createDefaultValues() ;
	  
	  this.values.addAll(values) ;
  }

	protected RangedCategoricalVariableImpl(RangedCategoricalVariable<DataType> variable)
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

	@Override
  public boolean addValue(DataType value)
  {
		if (!values.contains(value))
			return values.add(value) ;
		else
			return false ;
  }
	
	@Override
  public boolean equals(Object obj)
  {
	  if (this == obj)
		  return true;
	  if (!super.equals(obj))
		  return false;
	  if (CategoricalVariable.class.isAssignableFrom(obj.getClass()))
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
