package org.corehunter.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.corehunter.SSRDataset;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

public class SSRDatasetTest
{

	@Test
	public void testCreateFromFile()
	{
		SSRDataset dataset = SSRDataset.createFromFile(SSRDatasetTest.class.getResource("/test.csv").getFile()) ;
		
		checkDataset(dataset) ;
	}

	@SuppressWarnings("unchecked")
  @Test
	public void testCreateFromlist()
	{
		CSVReader reader;
		
		List<String[]> lines = null ;
		
    try
    {
	    reader = new CSVReader(new FileReader(SSRDatasetTest.class.getResource("/test.csv").getFile()));
	    
	    lines = reader.readAll();
			reader.close();
    }
    catch (FileNotFoundException e)
    {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
    catch (IOException e)
    {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
		
		SSRDataset dataset = SSRDataset.createFromlist(lines) ;
		
		checkDataset(dataset) ;
	}

	@SuppressWarnings("unchecked")
  @Test
	public void testCreateFromArray()
	{
		CSVReader reader;
		
		String[][] lines = null ;
		
    try
    {
	    reader = new CSVReader(new FileReader(SSRDatasetTest.class.getResource("/test.csv").getFile()));
	    
	    lines = (String[][])reader.readAll().toArray(new String[0][0]);
			reader.close();
    }
    catch (FileNotFoundException e)
    {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
    catch (IOException e)
    {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
		
		SSRDataset dataset = SSRDataset.createFromArray(lines) ;
		
		checkDataset(dataset) ;
	}

	private void checkDataset(SSRDataset dataset)
  {
	  assertNotNull(dataset) ;
	  
	  assertEquals("g1", dataset.getAccessionNames().get(0)) ;
	  assertEquals("g2", dataset.getAccessionNames().get(1)) ;
  }
}
