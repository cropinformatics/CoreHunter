// Copyright 2013 Guy Davenport, Herman De Beukelaer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.corehunter.model.variable.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.model.DataReader;
import org.corehunter.model.EntityIndexedDataset;
import org.corehunter.model.Matrix;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.accession.impl.AccessionImpl;
import org.corehunter.model.impl.AbstractDataFileReader;
import org.corehunter.model.impl.EntityMatrixListImpl;
import org.corehunter.model.impl.OrderedEntityDatasetListImpl;
import org.corehunter.model.variable.CategoricalVariable;
import org.corehunter.model.variable.RangedVariable;
import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.VariableDataType;
import org.corehunter.model.variable.VariableType;
import org.corehunter.model.variable.interval.DoubleIntervalVariable;
import org.corehunter.model.variable.interval.FloatIntervalVariable;
import org.corehunter.model.variable.interval.IntegerIntervalVariable;
import org.corehunter.model.variable.interval.LongIntervalVariable;
import org.corehunter.model.variable.interval.ShortIntervalVariable;
import org.corehunter.model.variable.nominal.DoubleNominalVariable;
import org.corehunter.model.variable.nominal.FloatNominalVariable;
import org.corehunter.model.variable.nominal.IntegerNominalVariable;
import org.corehunter.model.variable.nominal.LongNominalVariable;
import org.corehunter.model.variable.nominal.ShortNominalVariable;
import org.corehunter.model.variable.nominal.StringNominalVariable;
import org.corehunter.model.variable.ordinal.DoubleOrdinalVariable;
import org.corehunter.model.variable.ordinal.FloatOrdinalVariable;
import org.corehunter.model.variable.ordinal.IntegerOrdinalVariable;
import org.corehunter.model.variable.ordinal.LongOrdinalVariable;
import org.corehunter.model.variable.ordinal.ShortOrdinalVariable;
import org.corehunter.model.variable.ratio.DoubleRatioVariable;
import org.corehunter.model.variable.ratio.FloatRatioVariable;

import au.com.bytecode.opencsv.CSVReader;

/**
 * SSRAccessionMatrixDataset reader that reads the complete matrix into memory before
 * creating the Dataset. Accessions and Marker are indexed from 0 to n-1.
 * Not suitable for big datasets.
 *  
 * @author daveneti
 *
 */
public class AccessionVariableMatrixListImplDataFileReader 
	extends AbstractDataFileReader<Matrix<Integer, Object, Accession, Variable>> 
	implements DataReader<Matrix<Integer, Object, Accession, Variable>>
{

	private static final String ACCESSION_DATASET_NAME_PREFIX = "Accessions for " ;
	private static final String VARIABLE_DATASET_NAME_PREFIX = "Variables for " ;
	private static final String NAME = "name";
	private static final String UNIQUE_IDENTIFIER = "uid";
	private static final String DESCRIPTION = "description";
	private static final String TYPE = "type";
	private static final String DATA_TYPE = "dataType";
	private static final List<String> TRUE = new ArrayList<String>(4);
	private static final List<String> FALSE = new ArrayList<String>(4);
	private static final String EMPTY_STRING = "";
	private static final String MINIMUM_VALUE = "min";
	private static final String MAXIMUM_VALUE = "max";
	
	static
	{
		TRUE.add("true") ;
		TRUE.add("t") ;
		TRUE.add("1") ;
	}
	
	static
	{
		FALSE.add("false") ;
		FALSE.add("f") ;
		FALSE.add("0") ;
	}
	
	private char delimiter;

	public AccessionVariableMatrixListImplDataFileReader(File file)
  {
		super(file);
		
	  setDelimiter(TAB_DELIMITER) ;
  }
	
	public AccessionVariableMatrixListImplDataFileReader(String dataName, File file)
  {
	  super(dataName, file);
	  
	  setDelimiter(TAB_DELIMITER) ;
  }
	
	public AccessionVariableMatrixListImplDataFileReader(File file, char delimiter)
  {
		super(file);
		
	  setDelimiter(delimiter) ;
  }
	
	public AccessionVariableMatrixListImplDataFileReader(String dataName, File file, char delimiter)
  {
	  super(dataName, file);
	  
	  setDelimiter(delimiter) ;
  }

	public final char getDelimiter()
  {
  	return delimiter;
  }

	public final void setDelimiter(char delimiter)
  {
  	this.delimiter = delimiter;
  }

	@SuppressWarnings("rawtypes")
  @Override
  public Matrix<Integer, Object, Accession, Variable> readData()
      throws CoreHunterException
  {
			Matrix<Integer, Object, Accession, Variable> dataset = null;
			List<Accession> accessions = new ArrayList<Accession>();
			List<Variable> variables = new ArrayList<Variable>();

			String line[] = null ;

			try
			{
				// TODO allow for other delimiters
				CSVReader reader = new CSVReader(new FileReader(getFile()), delimiter);

				// TODO general this for excel, excelx and text
				List lines = reader.readAll();
				reader.close();

				Iterator iterator = lines.iterator();
				
				boolean names = false ;
				boolean uniqueIdentifiers = false ;
				boolean ranges = false ;
				
				String[] variableNames = null ;
				String[] variableUniqueIdentifiers = null ;
				String[] variableDescription = null ;
				String[] variableTypes = null ;
				String[] variableDataTypes = null ;
				String[] variableMinimums = null ;
				String[] variableMaximums = null ;
				
				int firstColumn = 0 ;
				int lineNumber = 0;
				
				List<List<Object>> elements = new ArrayList<List<Object>>() ; // TODO find better List structure
				
				if (iterator.hasNext())
				{
					line = (String[]) iterator.next();
					lineNumber = 1;
					
					if (line[0].equalsIgnoreCase(NAME))
					{
						names = true ;
						firstColumn = 1 ;
					}
					else
					{
						if (line[0].equalsIgnoreCase(UNIQUE_IDENTIFIER) && line[1].equalsIgnoreCase(NAME))
						{
							uniqueIdentifiers = true ;
							names = true ;
							firstColumn = 2 ;
						}
						else
						{
							throw new CoreHunterException("Dataset is not properly formatted on line. Please refer to the CoreHunter manual.  "
					        + "There should be a row labelled " + NAME + " at line " + lineNumber);
						}
					}
					
					variableNames = line ;
					variableUniqueIdentifiers = line ;
				}
				
				if (firstColumn > 0 && iterator.hasNext())
				{
					line = (String[]) iterator.next();
					++lineNumber ;
					
					if (line[0].equalsIgnoreCase(UNIQUE_IDENTIFIER))
					{
						variableUniqueIdentifiers =  line ;
						
						line = (String[]) iterator.next();
						++lineNumber ;
					}
					
					if (line[0].equalsIgnoreCase(DESCRIPTION))
					{
						variableDescription =  line ;
						
						line = (String[]) iterator.next();
						++lineNumber ;
					}

					if (line[0].equalsIgnoreCase(TYPE))
					{
						variableTypes =  line ;
						
						line = (String[]) iterator.next();
						++lineNumber ;
						
						if (line[0].equalsIgnoreCase(DATA_TYPE))
						{
							variableDataTypes =  line ;
							
							line = (String[]) iterator.next();
							++lineNumber ;
						}
						
						if (line[0].equalsIgnoreCase(MINIMUM_VALUE))
						{
							variableMinimums =  line ;
							
							line = (String[]) iterator.next();
							++lineNumber ;

							if (line[0].equalsIgnoreCase(MAXIMUM_VALUE))
							{
								variableMaximums =  line ;
								
								line = (String[]) iterator.next();
								++lineNumber ;
								
								ranges = true ;
							}
							else
							{
								throw new CoreHunterException("Dataset is not properly formatted on line. Please refer to the CoreHunter manual.  "
						        + "There should be a row labelled " + MAXIMUM_VALUE + " at line " + lineNumber);
							}
						}
					}

					for (int i = firstColumn; i < line.length; i++)
					{
						variables.add(createVariable(
									variableNames[i], 
									variableUniqueIdentifiers[i], 
									variableDescription != null ? variableDescription[i] : null,
									variableTypes != null ? VariableType.find(variableTypes[i])  : null,
									variableDataTypes != null ? VariableDataType.find(variableDataTypes[i])  : null,
									variableMinimums != null ? variableMinimums[i] : null,
									variableMaximums != null ? variableMaximums[i] : null, i -firstColumn));
					}
				}
				
				if (variables.isEmpty())
				{
					throw new CoreHunterException("Dataset must contain at least 1 variable");
				}
				
				parseAccession(accessions, uniqueIdentifiers ? line[0] : null, names ? uniqueIdentifiers ? line[1] : line[0] : null, lineNumber) ;
				
				parseLine(elements, accessions, variables, line, ranges, false, lineNumber, firstColumn) ;

				while (iterator.hasNext())
				{
					
					line = (String[]) iterator.next();
					lineNumber++;
					
					parseAccession(accessions, uniqueIdentifiers ? line[0] : null, names ? uniqueIdentifiers ? line[1] : line[0] : null, lineNumber) ;
					
					parseLine(elements, accessions, variables, line, ranges, false, lineNumber, firstColumn) ;
				}

				if (accessions.size() < 1)
				{
					throw new CoreHunterException("Dataset must contain at least 1 accession");
				}				
				
				// create the SSRDataset object
				dataset = createAccessionVariableMatrixDataset(createDataName(), createAccessionDataset(createAccessionDatasetName(), accessions), createVariableDataset(createVariableDatasetName(), variables), elements);

			}
			catch (IOException e)
			{
				throw new CoreHunterException(e) ;
			}

			return dataset;
  }
	
	protected void parseAccession(List<Accession> accessions, String uniqueIdentifier, String name, int lineNumber)
  {
		if (name != null)
			if (uniqueIdentifier != null)
				accessions.add(createAccession(uniqueIdentifier, name, lineNumber)) ;
			else
				accessions.add(createAccession(name, lineNumber)) ;
		else
			accessions.add(createAccession(lineNumber)) ;
  }

	protected void parseLine(List<List<Object>> elements, List<Accession> accessions, List<Variable> variables, String[] line, boolean validateRanges, boolean validateValues, int lineNumber, int firstColumn) throws CoreHunterException
  {
		List<Object> row = new ArrayList<Object>(variables.size()) ;
		elements.add(row) ;
		
		for (int i = 0 ; i < variables.size() ; ++i)
		{
			row.add(updateVariable(variables.get(i), parseElement(line[firstColumn + i], variables.get(i), lineNumber, firstColumn + i), validateRanges, validateValues, lineNumber, firstColumn + i)) ;
		}
  }

	protected String createDataName()
  {
	  return getDataName() != null ? getDataName() : getFile().getName() ;
  }
	
	protected String createAccessionDatasetName()
  {
	  return ACCESSION_DATASET_NAME_PREFIX + createDataName();
  }
	
	protected String createVariableDatasetName()
  {
	  return VARIABLE_DATASET_NAME_PREFIX + createDataName();
  }

	protected Accession createAccession(String accessionName, int lineNumber)
  {
	  return new AccessionImpl(accessionName) ; 
  }
	
	protected Accession createAccession(String accessionUniqueIdentifier, String accessionName, int lineNumber)
  {
	  return new AccessionImpl(accessionUniqueIdentifier, accessionName) ; 
  }
	
	protected Accession createAccession(int lineNumber)
  {
	  return new AccessionImpl(String.valueOf(lineNumber - 3)) ; 
  }
	
	private Variable createVariable(String name, String uniqueIdentifier, String description,
      VariableType type, VariableDataType dataType,
      String minimumStringValue, String maximumStringValue, int columnNumber) throws CoreHunterException
  {
		Variable variable = null ;
		
		if (type != null)
		{
			try
      {
	      switch (type)
	      {
	      	case BINARY:
	      		if (dataType == null || VariableDataType.BOOLEAN.equals(dataType))
	      			variable = new BinaryVariable(uniqueIdentifier, name) ;
	      		else
	      			throw new CoreHunterException("Invalid data type : " + dataType + " for type : " + type) ;
	      		break ;
	      	case INTERVAL:
	      		if (dataType != null)
	      		{
	      			switch (dataType)
	      			{
	      				case DOUBLE:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new DoubleIntervalVariable(uniqueIdentifier, name, Double.valueOf(minimumStringValue), Double.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new DoubleIntervalVariable(uniqueIdentifier, name) ;			
	      					break ;
	      				case FLOAT:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new FloatIntervalVariable(uniqueIdentifier, name, Float.valueOf(minimumStringValue), Float.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new FloatIntervalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case INTEGER:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new IntegerIntervalVariable(uniqueIdentifier, name, Integer.valueOf(minimumStringValue), Integer.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new IntegerIntervalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case LONG:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new LongIntervalVariable(uniqueIdentifier, name, Long.valueOf(minimumStringValue), Long.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new LongIntervalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case SHORT:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new ShortIntervalVariable(uniqueIdentifier, name, Short.valueOf(minimumStringValue), Short.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new ShortIntervalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case STRING:
	      				case BOOLEAN:
	      				default:
	      					throw new CoreHunterException("Invalid data type : " + dataType + " for type : " + type) ;
	      			}
	      		}
	      		else
	      		{
	      			variable = new DoubleIntervalVariable(uniqueIdentifier, name) ;
	      		}
	      		break ;
	      	case NOMINAL:
	      		if (dataType != null)
	      		{
	      			switch (dataType)
	      			{
	      				case DOUBLE:
	      					variable = new DoubleNominalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case FLOAT:
	      					variable = new FloatNominalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case INTEGER:
	      					variable = new IntegerNominalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case LONG:
	      					variable = new LongNominalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case SHORT:
	      					variable = new ShortNominalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case STRING:
	      					variable = new StringNominalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case BOOLEAN:
	      				default:
	      					throw new CoreHunterException("Invalid data type : " + dataType + " for type : " + type) ;
	      			}
	      		}
	      		else
	      		{
	      			variable = new StringNominalVariable(uniqueIdentifier, name) ;
	      		}
	      		break ;
	      	case ORDINAL:
	      		if (dataType != null)
	      		{
	      			switch (dataType)
	      			{
	      				case DOUBLE:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new DoubleOrdinalVariable(uniqueIdentifier, name, Double.valueOf(minimumStringValue), Double.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new DoubleOrdinalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case FLOAT:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new FloatOrdinalVariable(uniqueIdentifier, name, Float.valueOf(minimumStringValue), Float.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new FloatOrdinalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case INTEGER:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new IntegerOrdinalVariable(uniqueIdentifier, name, Integer.valueOf(minimumStringValue), Integer.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new IntegerOrdinalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case LONG:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new LongOrdinalVariable(uniqueIdentifier, name, Long.valueOf(minimumStringValue), Long.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new LongOrdinalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case SHORT:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new ShortOrdinalVariable(uniqueIdentifier, name, Short.valueOf(minimumStringValue), Short.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new ShortOrdinalVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case STRING:
	      				case BOOLEAN:
	      				default:
	      					throw new CoreHunterException("Invalid data type : " + dataType + " for type : " + type) ;
	      			}
	      		}
	      		else
	      		{
	      			variable =  new IntegerOrdinalVariable(uniqueIdentifier, name) ;
	      		}
	      		break ;
	      	case RATIO:
	      		if (dataType != null)
	      		{
	      			switch (dataType)
	      			{
	      				case DOUBLE:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new DoubleRatioVariable(uniqueIdentifier, name, Double.valueOf(minimumStringValue), Double.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new DoubleRatioVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case FLOAT:
	      					if (minimumStringValue != null && maximumStringValue != null)
	      						variable = new FloatRatioVariable(uniqueIdentifier, name, Float.valueOf(minimumStringValue), Float.valueOf(maximumStringValue)) ;
	      					else
	      						variable = new FloatRatioVariable(uniqueIdentifier, name) ;
	      					break ;
	      				case INTEGER:
	      				case LONG:
	      				case SHORT:
	      				case STRING:
	      				case BOOLEAN:
	      				default:
	      					throw new CoreHunterException("Invalid data type : " + dataType + " for type : " + type) ;
	      			}
	      		}
	      		else
	      		{
	      			variable = new DoubleRatioVariable(uniqueIdentifier, name) ;
	      		}
	      		break ;
	      	default:
	      		variable = createDefault(uniqueIdentifier, name) ;
	      		break ;
	      }
			}
			catch (NumberFormatException e)
			{
				throw new CoreHunterException("Problem creating variable : " + name + " reason : Unable to parse string to number " + e.getLocalizedMessage()) ;
			}
		}
		else
		{
			variable = createDefault(uniqueIdentifier, name) ;
		}
		
		if (description != null)
			variable.setDescription(description) ;
		
		return variable ;
  }
	
	protected Variable createDefault(String uniqueIdentifier, String name)
  {
	  // TODO allow this to be set by user
	  return new StringNominalVariable(uniqueIdentifier, name) ;
  }

	protected Object parseElement(String string, Variable variable, int lineNumber,
      int coluumNumber) throws CoreHunterException
  {
		if (string == null || string.trim().equals(EMPTY_STRING))
			return null ;
		
		switch (variable.getDataType())
		{
			case BOOLEAN :
				if (TRUE.contains(string.toLowerCase()))
					return Boolean.TRUE ;
				else
					if (FALSE.contains(string.toLowerCase()))
						return Boolean.FALSE ;
					else
						throw new CoreHunterException("Dataset is not properly formatted on line "
						    + lineNumber + " Please refer to the CoreHunter manual. "
						        + "value " + string + " can not be parsed to Boolean at column " + coluumNumber);
			case SHORT :
				try
        {
	        return Short.parseShort(string) ;
        }
        catch (NumberFormatException e)
        {
					throw new CoreHunterException("Dataset is not properly formatted on line "
					    + lineNumber + " Please refer to the CoreHunter manual. "
					        + "value " + string + " can not be parsed to Short at column " + coluumNumber, e);
        }
			case INTEGER :
				try
        {
	        return Integer.parseInt(string) ;
        }
        catch (NumberFormatException e)
        {
					throw new CoreHunterException("Dataset is not properly formatted on line "
					    + lineNumber + " Please refer to the CoreHunter manual. "
					        + "value " + string + " can not be parsed to Integer at column " + coluumNumber, e);
        }
			case LONG :
				try
        {
					// TODO BigInteger
	        return Long.parseLong(string) ;
        }
        catch (NumberFormatException e)
        {
					throw new CoreHunterException("Dataset is not properly formatted on line "
					    + lineNumber + " Please refer to the CoreHunter manual. "
					        + "value " + string + " can not be parsed to Long at column " + coluumNumber, e);
        }
			case FLOAT :
				try
        {
	        return Float.parseFloat(string) ;
        }
        catch (NumberFormatException e)
        {
					throw new CoreHunterException("Dataset is not properly formatted on line "
					    + lineNumber + " Please refer to the CoreHunter manual. "
					        + "value " + string + " can not be parsed to Float at column " + coluumNumber, e);
        }
			case DOUBLE :
				try
        {
					// TODO BigDecimal
	        return Double.parseDouble(string) ;
        }
        catch (NumberFormatException e)
        {
					throw new CoreHunterException("Dataset is not properly formatted on line "
					    + lineNumber + " Please refer to the CoreHunter manual. "
					        + "value " + string + " can not be parsed to Double at column " + coluumNumber, e);
        }
			case STRING :
			default :
				return string ;
		}
  }

	@SuppressWarnings({ "unchecked", "rawtypes" })
  protected Object updateVariable(Variable variable, Object value, boolean validateRanges, boolean validateValues, int lineNumber, int columnNumber) throws CoreHunterException
  {
		if (variable instanceof RangedVariable)
		{
			if (validateRanges) 
			{
				switch(variable.getDataType())
				{
					case BOOLEAN:
						break;
					case DOUBLE:
						if (!inRangeDouble((RangedVariable<Double>)variable, (Double)value))
							throw new CoreHunterException("Dataset is not properly formatted on line "
							    + lineNumber + " Please refer to the CoreHunter manual. "
							        + "value " + value + " not in range at column " + columnNumber);
						break;
					case FLOAT:
						if (!inRangeFloat((RangedVariable<Float>)variable, (Float)value))
							throw new CoreHunterException("Dataset is not properly formatted on line "
							    + lineNumber + " Please refer to the CoreHunter manual. "
							        + "value " + value + " not in range at column " + columnNumber);
						break;
					case INTEGER:
						if (!inRangeInteger((RangedVariable<Integer>)variable, (Integer)value))
							throw new CoreHunterException("Dataset is not properly formatted on line "
							    + lineNumber + " Please refer to the CoreHunter manual. "
							        + "value " + value + " not in range at column " + columnNumber);
						break;
					case LONG:
						if (!inRangeLong((RangedVariable<Long>)variable, (Long)value))
							throw new CoreHunterException("Dataset is not properly formatted on line "
							    + lineNumber + " Please refer to the CoreHunter manual. "
							        + "value " + value + " not in range at column " + columnNumber);
						break;
					case SHORT:
						if (!inRangeShort((RangedVariable<Short>)variable, (Short)value))
							throw new CoreHunterException("Dataset is not properly formatted on line "
							    + lineNumber + " Please refer to the CoreHunter manual. "
							        + "value " + value + " not in range at column " + columnNumber);
						break;
					case STRING:
					default:
						throw new CoreHunterException("Dataset is not properly formatted on line "
						    + lineNumber + " Please refer to the CoreHunter manual. "
						        + "value " + value + " not in range at column " + columnNumber);
					
				}
			}
			else
			{
				switch(variable.getDataType())
				{
					case BOOLEAN:
						break;
					case DOUBLE:
						if (((RangedVariable<Double>)variable).getMaximumValue() == null || ((Double)value) > ((RangedVariable<Double>)variable).getMaximumValue())
								((RangedVariable<Double>)variable).setMaximumValue(((Double)value)) ;
						
						if (((RangedVariable<Double>)variable).getMinimumValue() == null || ((Double)value) < ((RangedVariable<Double>)variable).getMinimumValue())
							((RangedVariable<Double>)variable).setMinimumValue(((Double)value)) ;
						break;
					case FLOAT:
						if (((RangedVariable<Float>)variable).getMaximumValue() == null || ((Float)value) > ((RangedVariable<Float>)variable).getMaximumValue())
							((RangedVariable<Float>)variable).setMaximumValue(((Float)value)) ;
					
						if (((RangedVariable<Double>)variable).getMinimumValue() == null || ((Float)value) < ((RangedVariable<Float>)variable).getMinimumValue())
							((RangedVariable<Float>)variable).setMinimumValue(((Float)value)) ;
						break;
					case INTEGER:
						if (((RangedVariable<Integer>)variable).getMaximumValue() == null || ((Integer)value) > ((RangedVariable<Integer>)variable).getMaximumValue())
							((RangedVariable<Integer>)variable).setMaximumValue(((Integer)value)) ;
					
						if (((RangedVariable<Integer>)variable).getMinimumValue() == null || ((Integer)value) < ((RangedVariable<Integer>)variable).getMinimumValue())
							((RangedVariable<Integer>)variable).setMinimumValue(((Integer)value)) ;
						break;
					case LONG:
						if (((RangedVariable<Long>)variable).getMaximumValue() == null || ((Long)value) > ((RangedVariable<Long>)variable).getMaximumValue())
							((RangedVariable<Long>)variable).setMaximumValue(((Long)value)) ;
					
						if (((RangedVariable<Long>)variable).getMinimumValue() == null || ((Long)value) < ((RangedVariable<Long>)variable).getMinimumValue())
							((RangedVariable<Long>)variable).setMinimumValue(((Long)value)) ;
						break;
					case SHORT:
						if (((RangedVariable<Double>)variable).getMaximumValue() == null || ((Short)value) > ((RangedVariable<Short>)variable).getMaximumValue())
							((RangedVariable<Short>)variable).setMaximumValue(((Short)value)) ;
					
						if (((RangedVariable<Double>)variable).getMinimumValue() == null || ((Short)value) < ((RangedVariable<Short>)variable).getMinimumValue())
							((RangedVariable<Short>)variable).setMinimumValue(((Short)value)) ;
						break;
					case STRING:
					default:
						break;
					
				}
			}
		}
		
		if (variable instanceof CategoricalVariable)
		{
			if (validateValues) 
			{
				if (variable instanceof CategoricalVariable)
				{
					if (!((CategoricalVariable)variable).getValues().contains(value))
						throw new CoreHunterException("Dataset is not properly formatted on line "
						    + lineNumber + " Please refer to the CoreHunter manual. "
						        + "value " + value + " not valid value at column " + columnNumber);
				}
			}
			else
			{
				if (!((CategoricalVariable)variable).getValues().contains(value))
					((CategoricalVariable)variable).addValue(value) ;
			}
		}

	  return value ;
  }

	protected boolean inRangeDouble(RangedVariable<Double> variable, Double value)
  {
	  return value >= variable.getMinimumValue() && value <= variable.getMaximumValue();
  }
	
	protected boolean inRangeFloat(RangedVariable<Float> variable, Float value)
  {
	  return value >= variable.getMinimumValue() && value <= variable.getMaximumValue();
  }

	protected boolean inRangeInteger(RangedVariable<Integer> variable, Integer value)
  {
	  return value >= variable.getMinimumValue() && value <= variable.getMaximumValue();
  }

	protected boolean inRangeLong(RangedVariable<Long> variable, Long value)
  {
	  return value >= variable.getMinimumValue() && value <= variable.getMaximumValue();
  }

	protected boolean inRangeShort(RangedVariable<Short> variable, Short value)
  {
	  return value >= variable.getMinimumValue() && value <= variable.getMaximumValue();
  }

	protected Matrix<Integer, Object, Accession, Variable> createAccessionVariableMatrixDataset(
			String name,
			EntityIndexedDataset<Integer, Accession> accessionDataset,
			EntityIndexedDataset<Integer, Variable> variableDataset,
      List<List<Object>> elements)
  {
	  return new EntityMatrixListImpl<Object, Accession, Variable>(name, accessionDataset, variableDataset, elements) ;
  }
	
	protected EntityIndexedDataset<Integer, Accession> createAccessionDataset(String name, List<Accession> accessions)
  {
	  return new OrderedEntityDatasetListImpl<Accession>(name, accessions) ;
  }

	protected EntityIndexedDataset<Integer, Variable> createVariableDataset(String name, List<Variable> variables)
  {
	  return new OrderedEntityDatasetListImpl<Variable>(name, variables) ;
  }
	
	protected VariableType createVariableType(String string, int lineNumber, int coluumNumber) throws CoreHunterException
  {
		VariableType type = VariableType.find(string) ;
		
		if (type != null)
			return type ;
		else
			throw new CoreHunterException("Dataset is not properly formatted on line "
		    + lineNumber + " Please refer to the CoreHunter manual. "
		        + "There should " + TYPE + " code at column " + coluumNumber);
  }
	
	protected VariableDataType createVariableDataType(String string, int lineNumber, int coluumNumber) throws CoreHunterException
  {
		VariableDataType dataType = VariableDataType.find(string) ;
		
		if (dataType != null)
			return dataType ;
		else
			throw new CoreHunterException("Dataset is not properly formatted on line "
		    + lineNumber + " Please refer to the CoreHunter manual. "
		        + "There should " + DATA_TYPE + " code at column " + coluumNumber);
  }

	
}
