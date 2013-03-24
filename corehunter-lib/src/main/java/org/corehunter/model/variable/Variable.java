package org.corehunter.model.variable;
import org.corehunter.model.EntityWithDescription;


public interface Variable extends EntityWithDescription
{
	public VariableType getType() ;
	
	public VariableDataType getDataType() ;
}
