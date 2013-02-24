package org.corehunter.test.search.ssr;

import static org.junit.Assert.fail;

import java.io.File;

import org.corehunter.CoreHunterException;
import org.corehunter.model.impl.AbstractFileUtility;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImplDataFileReader;
import org.corehunter.search.impl.ExhaustiveSubsetSearch;
import org.corehunter.search.impl.IntegerSubsetGenerator;
import org.corehunter.search.impl.LRSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.UnorderedIntegerListSubsetSolution;
import org.corehunter.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.test.search.SubsetSearchTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class SSRLRSearchTest extends SubsetSearchTest<Integer, SubsetSolution<Integer>, ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>>
{
	private static final String SSR_DATA_NAME = "bul.csv";
	private static AccessionSSRMarkerMatrix<Integer> data;

	@BeforeClass
	public static void beforeClass()
	{
		try
    {
	    data = new AccessionSSRMarkerMatrixListImplDataFileReader(new File(SSRLRSearchTest.class.getResource("/" + SSR_DATA_NAME).getFile()), AbstractFileUtility.COMMA_DELIMITER).readData() ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
    }
	}
	
	@Test
	public void lr21SearchTestWithRandomSeed()
	{	
		LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> 
			search = new LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>() ;
	
		try
    {
    	IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator() ;
    	integerSubsetGenerator.setSubsetSize(2) ;
			 
	    search.setSolution(new UnorderedIntegerListSubsetSolution(data.getIndices(), integerSubsetGenerator.first())) ;
	
	    search.setData(data) ;
	    search.setObjectiveFunction(new ModifiedRogersDistanceSSR()) ;
	    search.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE) ;
	    search.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE) ;
	    search.setL(2) ;
	    search.setR(1) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void lr21SearchTestWithExhaustiveSeed()
	{	
		LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> 
			search = new LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>() ;
	
		try
    {
    	IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator() ;
    	integerSubsetGenerator.setSubsetSize(2) ;
			 
	    search.setSolution(new UnorderedIntegerListSubsetSolution(data.getIndices(), integerSubsetGenerator.first())) ;
	
	    search.setData(data) ;
	    search.setObjectiveFunction(new ModifiedRogersDistanceSSR()) ;
	    search.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE) ;
	    search.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE) ;
	    search.setL(2) ;
	    search.setR(1) ;
	    search.setExhaustiveSearch(SubsetSearchTest.getExhaustiveSubsetSearch(2, data)) ;
	   
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
		
		testSearch(search) ;
	}
}
