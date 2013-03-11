package org.corehunter.model.variable;
import org.corehunter.model.Entity;


public interface Variable extends Entity
{
	public VariableType getType() ;
	
	public VariableDataType getDataType() ;
}
