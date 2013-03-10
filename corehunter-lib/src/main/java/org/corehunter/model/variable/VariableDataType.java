package org.corehunter.model.variable;

public enum VariableDataType
{
	BOOLEAN("B", "Boolean"),
	SHORT("S", "Short"),
	INTEGER("I", "Integer"), 
	LONG("L", "Long"),
	FLOAT("F", "Float"),
	DOUBLE("D", "Double"),
	STRING("T", "String") ;
	
	private String code ;
	private String name ;
	
	VariableDataType(String code, String name)
	{
		this.code = code ;
		this.name = name ;
	}

	public final String getCode()
	{
		return code;
	}
	
	public final String getName()
	{
		return name;
	}

	@Override
  public String toString()
  {
	  return name ;
  }
	
	public static final VariableDataType find(String code)
	{
		VariableDataType[] dataTypes = VariableDataType.values() ;
		
		VariableDataType dataType = null ;
		
		int i = 0 ;
		
		while (dataType == null && i < dataTypes.length)  
		{
			if (dataTypes[i].getCode().equals(code))
				dataType = dataTypes[i] ;
		  ++i ;
		}
		
		return dataType ;
	}
}
