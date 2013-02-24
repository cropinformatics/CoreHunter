package org.corehunter.model.impl;

import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Entity;
import org.corehunter.model.EntityIndexedDataset;
import org.corehunter.model.UnknownEntityException;
import org.corehunter.model.UnknownIndexException;

//Copyright 2012 Guy Davenport, Herman De Beukelaer
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
public abstract class EntityMatrixListImpl< 
	DataType,
	RowHeaderType extends Entity, 
	ColumnHeaderType extends Entity> extends
		AbstractEntityMatrix<Integer, DataType, RowHeaderType, ColumnHeaderType>
{
	private List<List<DataType>> elements ;
	
	public EntityMatrixListImpl(String name,
			EntityIndexedDataset<Integer, RowHeaderType> rowHeaders,
			EntityIndexedDataset<Integer, ColumnHeaderType> columnHeaders, List<List<DataType>> elements)
  {
	  super(name, rowHeaders, columnHeaders);
	  
	  this.elements = elements ;
  }
	
	public EntityMatrixListImpl(String uniqueIdentifier, String name,
			EntityIndexedDataset<Integer, RowHeaderType> rowHeaders,
			EntityIndexedDataset<Integer, ColumnHeaderType> columnHeaders, List<List<DataType>> elements)
  {
	  super(uniqueIdentifier, name, rowHeaders, columnHeaders);
	  
	  this.elements = elements ;
  }
	
	@Override
  public void validate() throws CoreHunterException
  {
	  if (elements == null)
	  	throw new CoreHunterException("Elements not defined!") ;
	  
	  if (elements.size() != getRowHeaders().getSize())
	  	throw new CoreHunterException("Number of rows in elements does not match number of row headers!") ;
	  
	  if (!elements.isEmpty() && elements.get(0).size() != getColumnHeaders().getSize())
	  	throw new CoreHunterException("Number of columns in first row of elements does not match number of column headers!") ;
  }

	@Override
  public List<Integer> getIndices()
  {
	  return this.getRowHeaders().getIndices();
  }
	
	@Override
  public int getSize()
  {
	  return getRowHeaders().getSize();
  }

	@Override
  public DataType getElement(Integer rowIndex, Integer columnIndex) throws UnknownIndexException
  {
	  try
    {
	    return getRowElements(rowIndex).get(columnIndex);
    }
    catch (UnknownIndexException e)
    {
	    throw e ;
    }
    catch (Exception e)
    {
	    throw new UnknownIndexException("Unknown column index " + columnIndex) ;
    }
  }

	@Override
  public List<DataType> getRowElements(Integer index) throws UnknownIndexException
  {
	  try
    {
	    return elements.get(index);
    }
    catch (Exception e)
    {
	    throw new UnknownIndexException("Unknown row index : " + index) ;
    }
  }
	
  public void setElement(Integer rowIndex, Integer columnIndex, DataType element) throws UnknownEntityException
  {
	  elements.get(rowIndex).set(columnIndex, element);
  }
  
  public void setElement(RowHeaderType rowHeader,
      ColumnHeaderType columnHeader, DataType element) throws UnknownEntityException
  {
	  setElement(getRowHeaders().getIndex(rowHeader), getColumnHeaders().getIndex(columnHeader), element) ;
  }
  
  public void setRowElements(Integer index, List<DataType> values) throws UnknownEntityException
  {
	  elements.set(index, values);
  }
}
