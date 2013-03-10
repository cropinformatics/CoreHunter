package org.corehunter.model.variable;


public enum VariableType
{
	BINARY("B", "Binary", VariableDataType.BOOLEAN), 
	NOMINAL("N", "Nominal", VariableDataType.INTEGER),
	INTERVAL("I", "Interval", VariableDataType.INTEGER),
	ORDINAL("O", "Ordinal", VariableDataType.INTEGER),
	RATIO("R", "Ratio", VariableDataType.INTEGER) ;
	
	private String code ;
	private String name ;
	private VariableDataType defaultDataType ;
	
	VariableType(String code, String name, VariableDataType defaultDataType)
	{
		this.code = code ;
		this.name = name ;
		this.defaultDataType = defaultDataType ;
	}

	public final String getCode()
	{
		return code;
	}
	
	public final String getName()
	{
		return name;
	}

	public final VariableDataType getDefaultDataType()
	{
		return defaultDataType;
	}

	@Override
  public String toString()
  {
	  return name ;
  }

	
	public static final VariableType find(String code)
	{
		VariableType[] variableTypes = VariableType.values() ;
		
		VariableType variableType = null ;
		
		int i = 0 ;
		
		while (variableType == null && i < variableTypes.length)  
		{
			if (variableTypes[i].getCode().equals(code))
				variableType = variableTypes[i] ;
		  ++i ;
		}
		
		return variableType ;
	}
}
