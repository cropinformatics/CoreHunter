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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Data;
import org.corehunter.model.DataReader;
import org.corehunter.model.DuplicateEntityException;
import org.corehunter.model.UnknownIndexException;
import org.junit.Test;

public abstract class DataReaderTest<DataType extends Data>
{
	@Test
	public void testReadData()
	{
		try
    {
	    DataReader<DataType> dataReader ;
	    
	    DataType dataset ;
	    
	    int testCount = getTextCount() ;
	    
			for (int i = 0 ; i < testCount ; ++i)
			{
	    	dataReader = createReader(i) ;
	    
	    	dataset = dataReader.readData() ;
	    
	    	assertNotNull("Dataset " + i + " is null!", dataset) ;
	    
	    	assertDatasetEquals(getExpectedData(i), dataset, i) ;
			}
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}

	protected int getTextCount()
  {
	  return 0 ;
  }

	protected void assertDatasetEquals(DataType expectedData, DataType actualData, int index) throws UnknownIndexException
  {
    assertEquals("Dataset as expected equal!", expectedData, actualData) ;
  }

	protected abstract DataType getExpectedData(int index) throws DuplicateEntityException ;

	protected abstract DataReader<DataType> createReader(int index) ;
}
