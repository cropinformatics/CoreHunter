// Copyright 2012 Guy Davenport, Herman De Beukelaer
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
package org.corehunter.model;

import java.util.List;

public interface Matrix<
	IndexType,
	DataType extends Object,
	RowHeaderType extends Entity, 
	ColumnHeaderType extends Entity> extends Data, IndexedData<IndexType>
{
	public int getRowCount() ;
	
	public int getColumnCount() ;
	
	public EntityIndexedDataset<IndexType, RowHeaderType> getRowHeaders() ;
	
	public EntityIndexedDataset<IndexType, ColumnHeaderType> getColumnHeaders() ;
	
	public DataType getElement(IndexType rowIndex, IndexType columnIndex) throws UnknownIndexException ;
	
	public DataType getElement(RowHeaderType rowHeader, ColumnHeaderType columnHeader) throws UnknownEntityException ;
	
	public List<DataType> getRowElements(IndexType index) throws UnknownIndexException ;
	
	public List<DataType> getRowElements(RowHeaderType rowHeader) throws UnknownEntityException ;
	
	//public List<Double> getColumnElements(IndexType index) throws UnknownIndexException ;
	
	//public List<Double> getColumnElements(RowHeaderType rowHeader) throws UnknownIndexException ;
}
