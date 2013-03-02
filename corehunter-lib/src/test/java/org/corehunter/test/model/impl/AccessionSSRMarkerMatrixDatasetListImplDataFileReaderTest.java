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
package org.corehunter.test.model.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.corehunter.model.DataReader;
import org.corehunter.model.DuplicateEntityException;
import org.corehunter.model.EntityIndexedDataset;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.accession.impl.AccessionImpl;
import org.corehunter.model.impl.AbstractFileUtility;
import org.corehunter.model.impl.OrderedEntityDatasetListImpl;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.SSRMarker;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImpl;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImplDataFileReader;
import org.corehunter.model.ssr.impl.SSRAlleleImpl;
import org.corehunter.model.ssr.impl.SSRMarkerImpl;

public class AccessionSSRMarkerMatrixDatasetListImplDataFileReaderTest extends DataReaderTest<AccessionSSRMarkerMatrix<Integer>>
{
	private static final String SSR_DATA_NAME = "ssrdata.txt";
	private static final String ACCESSION_DATASET_NAME_PREFIX = "Accessions for " + SSR_DATA_NAME ;
	private static final String MARKER_DATASET_NAME_PREFIX = "Markers for " + SSR_DATA_NAME ;
	
	@Override
	protected int getTextCount()
  {
	  return 1 ;
  }
	
	@Override
  protected AccessionSSRMarkerMatrix<Integer> getExpectedData(int index) throws DuplicateEntityException
  {
    EntityIndexedDataset<Integer, Accession> rowHeaders ;
    EntityIndexedDataset<Integer, SSRMarker> columnHeaders; 
    List<List<List<Double>>> elements ;
    
		switch (index)
		{
			case 0 :
			default :
	      rowHeaders = new OrderedEntityDatasetListImpl<Accession>(ACCESSION_DATASET_NAME_PREFIX, createAcessions(index)) ;
	      columnHeaders = new OrderedEntityDatasetListImpl<SSRMarker>(MARKER_DATASET_NAME_PREFIX, createMarkers(index)) ;

	      elements = createElements(index) ;
	      
			  return new AccessionSSRMarkerMatrixListImpl(SSR_DATA_NAME, rowHeaders, columnHeaders, elements) ;
		}
  }

	private List<Accession> createAcessions(int index)
  {
		List<Accession> accessions = new ArrayList<Accession>(2) ;
		
		//TODO
		
		accessions.add(new AccessionImpl("g1")) ;
		accessions.add(new AccessionImpl("g2")) ;

	  return accessions ;
  }
	
	private List<SSRMarker> createMarkers(int index) throws DuplicateEntityException
  {
		List<SSRMarker> markers = new ArrayList<SSRMarker>(4) ;
		
		markers.add(new SSRMarkerImpl("m1")) ;
		markers.add(new SSRMarkerImpl("m2")) ;
		markers.add(new SSRMarkerImpl("m3")) ;
		markers.add(new SSRMarkerImpl("m4")) ;
		
		markers.get(0).addAllele(new SSRAlleleImpl("a1.1", markers.get(0))) ;
		markers.get(0).addAllele(new SSRAlleleImpl("a1.2", markers.get(0))) ;
		markers.get(1).addAllele(new SSRAlleleImpl("a2.1", markers.get(1))) ;
		markers.get(1).addAllele(new SSRAlleleImpl("a2.2", markers.get(1))) ;
		markers.get(1).addAllele(new SSRAlleleImpl("a2.3", markers.get(1))) ;
		markers.get(2).addAllele(new SSRAlleleImpl("a3.1", markers.get(2))) ;
		markers.get(2).addAllele(new SSRAlleleImpl("a3.2", markers.get(2))) ;
		markers.get(3).addAllele(new SSRAlleleImpl("a4.1", markers.get(3))) ;
		markers.get(3).addAllele(new SSRAlleleImpl("a4.2", markers.get(3))) ;
		
	  return markers ;
  }

	private List<List<List<Double>>> createElements(int index)
  {
		List<List<List<Double>>> elements = new ArrayList<List<List<Double>>>(2) ;
		
		elements.add(new ArrayList<List<Double>>(4)) ;
		elements.add(new ArrayList<List<Double>>(4)) ;
		
		elements.get(0).add(new ArrayList<Double>(2)) ;
		elements.get(0).add(new ArrayList<Double>(3)) ;
		elements.get(0).add(new ArrayList<Double>(2)) ;
		elements.get(0).add(new ArrayList<Double>(2)) ;
		
		elements.get(1).add(new ArrayList<Double>(2)) ;
		elements.get(1).add(new ArrayList<Double>(3)) ;
		elements.get(1).add(new ArrayList<Double>(2)) ;
		elements.get(1).add(new ArrayList<Double>(2)) ;
		
		elements.get(0).get(0).add(1.0) ;
		elements.get(0).get(0).add(0.0) ;
		elements.get(0).get(1).add(0.33) ;
		elements.get(0).get(1).add(0.33) ;
		elements.get(0).get(1).add(0.33) ;
		elements.get(0).get(2).add(0.5) ;
		elements.get(0).get(2).add(0.5) ;
		elements.get(0).get(3).add(0.0) ;
		elements.get(0).get(3).add(1.0) ;
		
		elements.get(1).get(0).add(0.0) ;
		elements.get(1).get(0).add(1.0) ;
		elements.get(1).get(1).add(1.0) ;
		elements.get(1).get(1).add(0.0) ;
		elements.get(1).get(1).add(0.0) ;
		elements.get(1).get(2).add(1.0) ;
		elements.get(1).get(2).add(0.0) ;
		elements.get(1).get(3).add(0.5) ;
		elements.get(1).get(3).add(0.5) ;
		
	  return elements ;
  }

	@Override
  protected DataReader<AccessionSSRMarkerMatrix<Integer>> createReader(int index)
  {
		switch (index)
		{
			case 0 :
			default :
				return new AccessionSSRMarkerMatrixListImplDataFileReader(new File(getClass().getResource("/" + SSR_DATA_NAME).getFile()), AbstractFileUtility.COMMA_DELIMITER) ;
		}
  }

	@Override
  protected void assertDatasetEquals(
      AccessionSSRMarkerMatrix<Integer> expectedData,
      AccessionSSRMarkerMatrix<Integer> actualData, int index) throws UnknownIndexException
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
			assertEquals("Rows " + index + " not equal!", expectedData.getRowElements(index), actualData.getRowElements(index)) ;
		}
  }
}
