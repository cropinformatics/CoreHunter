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
package org.corehunter.test.model.variable.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.corehunter.model.DataReader;
import org.corehunter.model.DuplicateEntityException;
import org.corehunter.model.EntityIndexedDataset;
import org.corehunter.model.Matrix;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.accession.impl.AccessionImpl;
import org.corehunter.model.impl.AbstractFileUtility;
import org.corehunter.model.impl.EntityMatrixListImpl;
import org.corehunter.model.impl.OrderedEntityDatasetListImpl;
import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.impl.AccessionVariableMatrixListImplDataFileReader;
import org.corehunter.model.variable.impl.BinaryVariable;
import org.corehunter.model.variable.interval.IntegerIntervalVariable;
import org.corehunter.model.variable.nominal.ShortNominalVariable;
import org.corehunter.model.variable.ordinal.LongOrdinalVariable;
import org.corehunter.model.variable.ratio.DoubleRatioVariable;
import org.corehunter.test.model.impl.DataReaderTest;

public class AccessionVariableMatrixDatasetListImplDataFileReaderTest extends DataReaderTest<Matrix<Integer, Object, Accession, Variable>>
{
	private static final String VARIABLE_DATA_NAME = "variabledata";
	private static final String VARIABLE_DATA_EXTN = ".txt";
	private static final String ACCESSION_DATASET_NAME_PREFIX = "Accessions for " + VARIABLE_DATA_NAME ;
	private static final String MARKER_DATASET_NAME_PREFIX = "Variables for " + VARIABLE_DATA_NAME ;
	
	@Override
	protected int getTextCount()
  {
	  return 1 ;
  }
	
	@Override
  protected Matrix<Integer, Object, Accession, Variable> getExpectedData(int index) throws DuplicateEntityException
  {
    EntityIndexedDataset<Integer, Accession> rowHeaders ;
    EntityIndexedDataset<Integer, Variable> columnHeaders; 
    List<List<Object>> elements ;
    
		switch (index)
		{
			case 0 :
			default :
	      rowHeaders = new OrderedEntityDatasetListImpl<Accession>(ACCESSION_DATASET_NAME_PREFIX + index + VARIABLE_DATA_EXTN, createAcessions(index)) ;
	      columnHeaders = new OrderedEntityDatasetListImpl<Variable>(MARKER_DATASET_NAME_PREFIX + index + VARIABLE_DATA_EXTN, createVariables(index)) ;

	      elements = createElements(index) ;
	      
			  return new EntityMatrixListImpl<Object, Accession, Variable>(VARIABLE_DATA_NAME + index + VARIABLE_DATA_EXTN, rowHeaders, columnHeaders, elements) ;
		}
  }

	private List<Accession> createAcessions(int index)
  {
		List<Accession> accessions = new ArrayList<Accession>(2) ;
			
		accessions.add(new AccessionImpl("a1")) ;
		accessions.add(new AccessionImpl("a2")) ;
		accessions.add(new AccessionImpl("a3")) ;
		accessions.add(new AccessionImpl("a4")) ;
		accessions.add(new AccessionImpl("a5")) ;

	  return accessions ;
  }
	
	private List<Variable> createVariables(int index) throws DuplicateEntityException
  {
		List<Variable> variables ;
		
		switch (index)
		{
			case 0 :
			default :
				variables = new ArrayList<Variable>(5) ;
				
				variables.add(new BinaryVariable("v1")) ;
				List<Short> shortValues = new ArrayList<Short>(1);
				shortValues.add((short)1) ;
				variables.add(new ShortNominalVariable("v2", shortValues)) ;
				variables.add(new IntegerIntervalVariable("v3",1,1)) ;
				List<Long> longValues = new ArrayList<Long>(1);
				longValues.add(1l) ;
				variables.add(new LongOrdinalVariable("v4", 1l, 1l, longValues)) ;
				variables.add(new DoubleRatioVariable("v5", 1.0, 1.0)) ;
		}
		
	  return variables ;
  }

	private List<List<Object>> createElements(int index)
  {
		List<List<Object>> elements ;
		
		switch (index)
		{
			case 0 :
			default :
				elements = new ArrayList<List<Object>>(5) ;
				elements.add(new ArrayList<Object>(5)) ;
				elements.add(new ArrayList<Object>(5)) ;
				elements.add(new ArrayList<Object>(5)) ;
				elements.add(new ArrayList<Object>(5)) ;
				elements.add(new ArrayList<Object>(5)) ;
				
				elements.get(0).add(true) ;
				elements.get(0).add((short)1) ;
				elements.get(0).add(1) ;
				elements.get(0).add(1l) ;
				elements.get(0).add(1.0) ;
		
				elements.get(1).add(true) ;
				elements.get(1).add((short)1) ;
				elements.get(1).add(1) ;
				elements.get(1).add(1l) ;
				elements.get(1).add(1.0) ;
		
				elements.get(2).add(true) ;
				elements.get(2).add((short)1) ;
				elements.get(2).add(1) ;
				elements.get(2).add(1l) ;
				elements.get(2).add(1.0) ;
		
				elements.get(3).add(true) ;
				elements.get(3).add((short)1) ;
				elements.get(3).add(1) ;
				elements.get(3).add(1l) ;
				elements.get(3).add(1.0) ;
				
				elements.get(4).add(true) ;
				elements.get(4).add((short)1) ;
				elements.get(4).add(1) ;
				elements.get(4).add(1l) ;
				elements.get(4).add(1.0) ;
				
		}
		
	  return elements ;
  }

	@Override
  protected DataReader<Matrix<Integer, Object, Accession, Variable>> createReader(int index)
  {
		switch (index)
		{
			case 0 :
			default :
				return new AccessionVariableMatrixListImplDataFileReader(new File(getClass().getResource("/" + VARIABLE_DATA_NAME + index + VARIABLE_DATA_EXTN).getFile()), AbstractFileUtility.COMMA_DELIMITER) ;
		}
  }

	@Override
  protected void assertDatasetEquals(
  		Matrix<Integer, Object, Accession, Variable> expectedData,
  		Matrix<Integer, Object, Accession, Variable> actualData, int index) throws UnknownIndexException
  {
		assertEquals("Unique Identifier not equal!", expectedData.getUniqueIdentifier(),actualData.getUniqueIdentifier()) ;
		
		assertEquals("Name not equal!", expectedData.getName(), actualData.getName()) ;
		
		assertEquals("Indices not equal!", expectedData.getIndices(), actualData.getIndices()) ;
		
		assertEquals("Row headers not equal!", expectedData.getRowHeaders(), actualData.getRowHeaders()) ;
		
		assertEquals("Column headers not equal!", expectedData.getColumnHeaders(), actualData.getColumnHeaders()) ;
		
		Iterator<Integer> iterator = expectedData.getRowHeaders().getIndices().iterator() ;
		
		boolean equals = true ;
		
		while (equals  && iterator.hasNext())
		{
			index = iterator.next() ;
			assertEquals("Row " + index + " not equal!", expectedData.getRowElements(index), actualData.getRowElements(index)) ;
		}
  }
}
