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
package org.corehunter.model.variable;

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
	private static final String UNIQUE_IDENTIFIER = null;
	private static final String TYPE = "type";
	private static final String DATA_TYPE = "dataType";
	private static final List<String> TRUE = new ArrayList<String>(4);
	private static final List<String> FALSE = new ArrayList<String>(4);
	private static final String EMPTY_STRING = "";
	
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

			String nextLine[];

			try
			{
				// TODO allow for other delimiters
				CSVReader reader = new CSVReader(new FileReader(getFile()), delimiter);

				List lines = reader.readAll();
				reader.close();

				Iterator iterator = lines.iterator();
				
				boolean names = false ;
				boolean uniqueIdentifiers = false ;
				int firstColumn = 0 ;
				int lineNumber = 1;
				
				if (iterator.hasNext())
				{
					nextLine = (String[]) iterator.next();
					
					if (nextLine[0].equalsIgnoreCase(NAME))
					{
						names = true ;
						firstColumn = 1 ;
					}
					else
					{
						if (nextLine[0].equalsIgnoreCase(UNIQUE_IDENTIFIER))
						{
							uniqueIdentifiers = true ;
							
							if (nextLine[1].equalsIgnoreCase(NAME))
							{
								names = true ;
								
								firstColumn = 2 ;
							}
							else
							{
								firstColumn = 1 ;
							}
						}
					}
					
					for (int i = firstColumn; i < nextLine.length; i++)
					{
						variables.add(createVariable(nextLine[i] , i -2));
					}
				}
				
				if (variables.isEmpty())
				{
					throw new CoreHunterException("Dataset must contain at least 1 variable");
				}
				
				if (iterator.hasNext())
				{
					nextLine = (String[]) iterator.next();
					++lineNumber ;
					
					if (firstColumn == 0 || nextLine[0].equalsIgnoreCase(TYPE))
					{
						for (int i = firstColumn; i < nextLine.length; i++)
						{
							variables.get(i - firstColumn).setType(createVariableType(nextLine[i], lineNumber, i)) ;
						}
					}
					else
					{
						throw new CoreHunterException("Dataset is not properly formatted on line "
						    + lineNumber + " Please refer to the CoreHunter manual.  "
						        + "There should be a row labelled " + TYPE + " at this line");
					}
				}

				if (iterator.hasNext())
				{
					nextLine = (String[]) iterator.next();
					++lineNumber ;
					
					if (firstColumn == 0 || nextLine[0].equalsIgnoreCase(DATA_TYPE))
					{
						for (int i = firstColumn; i < nextLine.length; i++)
						{
							variables.get(i - firstColumn).setDataType(createVariableDataType(nextLine[i], lineNumber, i)) ;
						}
					}
					else
					{
						throw new CoreHunterException("Dataset is not properly formatted on line "
						    + lineNumber + " Please refer to the CoreHunter manual.  "
						        + "There should be a row labelled " + DATA_TYPE + " at this line");
					}
				}
				
				List<List<Object>> elements = new ArrayList<List<Object>>() ; // TODO find better List structure
				List<Object> row ;
				
				while (iterator.hasNext())
				{
					nextLine = (String[]) iterator.next();
					lineNumber++;

					if (names)
						if (uniqueIdentifiers)
							accessions.add(createAccession(nextLine[0], nextLine[1], lineNumber)) ;
						else
							accessions.add(createAccession(nextLine[0],lineNumber)) ;
					else
						accessions.add(createAccession(lineNumber)) ;
					
					row = new ArrayList<Object>(variables.size()) ;
					elements.add(row) ;
					
					for (int i = 0 ; i < variables.size() ; ++i)
					{
						row.add(parseElement(nextLine[firstColumn + i], variables.get(i), lineNumber, firstColumn + i)) ;
					}
					
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
	
	protected Variable createVariable(String variableName, int index)
  {
	  return new VariableImpl(variableName) ; 
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
