package org.corehunter.model.variable;

public interface RangedVariable<DataType extends Number> extends Variable
{
	public DataType getMinimumValue() ;
	
	public void setMaximumValue(DataType value);
	
	public DataType getMaximumValue() ;
	
	public void setMinimumValue(DataType value);

}
