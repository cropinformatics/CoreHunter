package org.corehunter.test.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.corehunter.search.impl.IntegerSubsetGenerator;
import org.junit.Test;

public class IntegerSubsetGeneratorTest
{

	private static List<Integer> indices = new ArrayList<Integer>();
	
	static
	{
		for (int i = 0 ; i < 10; ++i)
			indices.add(i) ;
	}
	
	@Test
	public void test()
	{
		IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator() ;
		KSubsetGenerator kSubsetGenerator = new KSubsetGenerator(5, indices.size()) ;

		try
    {
			integerSubsetGenerator.setIndices(indices) ;
			integerSubsetGenerator.setSubsetSize(5) ;
			
			assertEquals(kSubsetGenerator.getNrOfKSubsets(), integerSubsetGenerator.getNumberOfSubsets()) ;
			
			Integer[] kSubset = kSubsetGenerator.first() ;
			List<Integer> subset = integerSubsetGenerator.first() ;

			assertSubsetEquals(0, kSubset, subset.toArray(new Integer[subset.size()])) ;
	    
	    for (long i = 1 ; i < integerSubsetGenerator.getNumberOfSubsets() ; ++i)
	    {
	    	kSubsetGenerator.successor(kSubset) ;
				integerSubsetGenerator.next(subset) ;

		    assertSubsetEquals(i+1, kSubset, subset.toArray(new Integer[subset.size()])) ;
	    }
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}

	private void assertSubsetEquals(long index, Integer[] expected, Integer[] actual)
  {
    assertEquals("Size expected: " + expected.length + " but actually " + expected.length, expected.length, actual.length) ;
    
    for (int i = 1 ; i < expected.length ; ++i)
    {
	    assertEquals("In subset " + index +" Value at position " + i, expected[i] - 1, actual[i].intValue()) ;
    }
  }
}
