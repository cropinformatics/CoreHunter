package org.corehunter.model.variable.impl;

import org.corehunter.model.variable.RangedVariable;

public abstract class RangedVariableImpl<DataType extends Number> extends VariableImpl implements
    RangedVariable<DataType>
{

	private DataType maximumValue = null ;
	private DataType minimumValue = null ;

	public RangedVariableImpl(String name)
  {
	  super(name);
  }
	
	public RangedVariableImpl(String uniqueIdentifier, String name)
  {
	  super(uniqueIdentifier, name);
  }
	
	public RangedVariableImpl(String name, DataType minimumValue, DataType maximumValue)
  {
	  super(name);
	  
	  setMinimumValue(minimumValue) ;
	  setMaximumValue(maximumValue) ;
  }
	
	public RangedVariableImpl(String uniqueIdentifier, String name, DataType minimumValue, DataType maximumValue)
  {
	  super(uniqueIdentifier, name);
	  
	  setMinimumValue(minimumValue) ;
	  setMaximumValue(maximumValue) ;
  }

	public RangedVariableImpl(RangedVariable<DataType> variable)
  {
	  super(variable);
	  
	  setMinimumValue(variable.getMinimumValue()) ;
	  setMaximumValue(variable.getMaximumValue()) ;
  }

	@Override
	public final DataType getMinimumValue()
	{
		return minimumValue ;
	}

	@Override
	public final void setMinimumValue(DataType minimumValue)
	{
		this.minimumValue = minimumValue;
	}
	
	@Override
	public final DataType getMaximumValue()
	{
		return maximumValue ;
	}

	@Override
	public final void setMaximumValue(DataType maximumValue)
	{
		this.maximumValue = maximumValue;
	}

	@Override
  public int hashCode()
  {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result
	      + ((maximumValue == null) ? 0 : maximumValue.hashCode());
	  result = prime * result
	      + ((minimumValue == null) ? 0 : minimumValue.hashCode());
	  return result;
  }

	@Override
  public boolean equals(Object obj)
  {
	  if (this == obj)
		  return true;
	  if (!super.equals(obj))
		  return false;
	  if (!RangedVariable.class.isAssignableFrom(obj.getClass()))
		  return false;
	  @SuppressWarnings("rawtypes")
    RangedVariable other = (RangedVariable) obj;
	  if (maximumValue == null)
	  {
		  if (other.getMaximumValue() != null)
			  return false;
	  }
	  else
		  if (!maximumValue.equals(other.getMaximumValue()))
			  return false;
	  if (minimumValue == null)
	  {
		  if (other.getMinimumValue() != null)
			  return false;
	  }
	  else
		  if (!minimumValue.equals(other.getMinimumValue()))
			  return false;
	  return true;
  }
}
