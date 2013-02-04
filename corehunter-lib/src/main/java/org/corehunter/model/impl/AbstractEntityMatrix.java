package org.corehunter.model.impl;

import java.util.Iterator;
import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Entity;
import org.corehunter.model.EntityIndexedDataset;
import org.corehunter.model.Matrix;
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
public abstract class AbstractEntityMatrix<	
	IndexType, 
	DataType,
	RowHeaderType extends Entity, 
	ColumnHeaderType extends Entity> extends EntityImpl implements
    Matrix<IndexType, DataType, RowHeaderType, ColumnHeaderType>
{
	private EntityIndexedDataset<IndexType, RowHeaderType> rowHeaders;
	private EntityIndexedDataset<IndexType, ColumnHeaderType> columnHeaders;

	public AbstractEntityMatrix(String name, 
			EntityIndexedDataset<IndexType, RowHeaderType> rowHeaders,
			EntityIndexedDataset<IndexType, ColumnHeaderType> columnHeaders)
  {
	  super(name);
	  this.rowHeaders = rowHeaders;
	  this.columnHeaders = columnHeaders;
  }
	
	public AbstractEntityMatrix(String uniqueIdentifier, String name, 
			EntityIndexedDataset<IndexType, RowHeaderType> rowHeaders,
			EntityIndexedDataset<IndexType, ColumnHeaderType> columnHeaders)
  {
	  super(uniqueIdentifier, name);
	  this.rowHeaders = rowHeaders;
	  this.columnHeaders = columnHeaders;
  }

	@Override
  public void validate() throws CoreHunterException
  {
	  if (rowHeaders == null)
	  	throw new CoreHunterException("Row headers not defined!") ;
	  
	  if (columnHeaders == null)
	  	throw new CoreHunterException("Column headers not defined!") ;
  }

	@Override
  public final int getRowCount()
  {
	  return rowHeaders.getSize() ;
  }

	@Override
  public final int getColumnCount()
  {
	  return columnHeaders.getSize() ;
  }

	@Override
  public final EntityIndexedDataset<IndexType, RowHeaderType> getRowHeaders()
  {
	  return rowHeaders;
  }

	@Override
  public final EntityIndexedDataset<IndexType, ColumnHeaderType> getColumnHeaders()
  {
	  return columnHeaders;
  }

	@Override
  public DataType getElement(RowHeaderType rowHeader,
      ColumnHeaderType columnHeader) throws UnknownEntityException
  {
	  try
    {
	    return getElement(rowHeaders.getIndex(rowHeader), columnHeaders.getIndex(columnHeader)) ;
    }
    catch (UnknownIndexException e)
    {
	    throw new UnknownEntityException(e.getLocalizedMessage(), e) ;
    }
  }
	
	@Override
  public List<DataType> getRowElements(RowHeaderType rowHeader) throws UnknownEntityException
  {
	  try
    {
	    return getRowElements(rowHeaders.getIndex(rowHeader)) ;
    }
    catch (UnknownIndexException e)
    {
	    throw new UnknownEntityException(e.getLocalizedMessage(), e) ;
    }
  }
	
	@SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object object)
  {
		if (object instanceof Matrix)
		{
			return rowHeaderEquals(((Matrix<IndexType, DataType, RowHeaderType, ColumnHeaderType>) object).getRowHeaders()) && 
					columnHeaderEquals(((Matrix<IndexType, DataType, RowHeaderType, ColumnHeaderType>) object).getColumnHeaders()) && 
					elementsEquals(((Matrix<IndexType, DataType, RowHeaderType, ColumnHeaderType>) object)) ;
		}
		else
		{
			return super.equals(object);
		}
  }

	protected boolean rowHeaderEquals(EntityIndexedDataset<IndexType, RowHeaderType> rowHeaders)
  {
	  return getRowHeaders().equals(rowHeaders);
  }
	
	protected boolean columnHeaderEquals(EntityIndexedDataset<IndexType, ColumnHeaderType> columnHeaders)
  {
	  return getColumnHeaders().equals(columnHeaders);
  }
	
	protected boolean elementsEquals(Matrix<IndexType, DataType, RowHeaderType, ColumnHeaderType> matrix)
  {
		Iterator<IndexType> iterator = matrix.getRowHeaders().getIndices().iterator() ;
		
		boolean equals = true ;
		
		while (equals  && iterator.hasNext())
			equals = rowElementsEquals(matrix, iterator.next()) ;
		
	  return equals;
  }

	protected boolean rowElementsEquals(Matrix<IndexType, DataType, RowHeaderType, ColumnHeaderType> matrix, IndexType index)
  {
	  try
    {
	    return getRowElements(index).equals(matrix.getRowElements(index));
    }
    catch (UnknownIndexException e)
    {
	    return false ;
    }
  }
}
