package org.corehunter.test.search.ssr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.corehunter.CoreHunterException;
import org.corehunter.model.impl.AbstractFileUtility;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImplDataFileReader;
import org.corehunter.search.impl.ExhaustiveSubsetSearch;
import org.corehunter.search.impl.IntegerSubsetGenerator;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.UnorderedIntegerListSubsetSolution;
import org.corehunter.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.test.search.SubsetSearchTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class SSRExhaustiveSearchTest extends SubsetSearchTest<Integer, SubsetSolution<Integer>, ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>>
{
	@Test
	public void exhaustiveSerchTest()
	{	
		SubsetSolution<Integer> soluiton1 = test(2, 5) ;
		SubsetSolution<Integer> soluiton2 = test(2, 5) ;
		SubsetSolution<Integer> soluiton3 = test(2, 3) ;
		
		assertEquals("Two exhaustive searches are not the same", soluiton1, soluiton2) ;
		
		if (soluiton1.getSubsetSize() <=3)
			assertEquals("Two exhaustive searches are not the same", soluiton1, soluiton3) ;
	}

	public SubsetSolution<Integer> test(int minimumSize, int maximumSize)
	{	
		ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> 
			search = new ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>() ;
		
		try
    {
    	IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator() ;
    	integerSubsetGenerator.setSubsetSize(2) ;
			 
	    search.setSolution(new UnorderedIntegerListSubsetSolution(data10.getIndices(), integerSubsetGenerator.first())) ;
	    search.setData(data10) ;
	    search.setObjectiveFunction(new ModifiedRogersDistanceSSR()) ;
	    search.setSubsetMinimumSize(minimumSize) ;
	    search.setSubsetMaximumSize(maximumSize) ;
	    search.setSubsetGenerator(new IntegerSubsetGenerator()) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
		
		testSearch(search) ;
		
		return search.getBestSolution() ;
	}
}
