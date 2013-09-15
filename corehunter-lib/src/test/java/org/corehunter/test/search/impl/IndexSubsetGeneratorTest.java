// Copyright 2013 Herman De Beukelaer, Guy Davenport
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

package org.corehunter.test.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.corehunter.CoreHunterException;
import org.corehunter.search.impl.IndexSubsetGenerator;
import org.junit.Test;

public class IndexSubsetGeneratorTest
{

	private static List<Integer> completeSet = new ArrayList<Integer>();
        private static Random rg = new Random();
	
	static
	{
		for (int i = 0 ; i < 10; ++i)
			completeSet.add(rg.nextInt(100)) ;
	}
	
	@Test
	public void test()
	{
                int k = rg.nextInt(10)+1;
                // generates subsets of complete set
		IndexSubsetGenerator<Integer> integerSubsetGenerator = new IndexSubsetGenerator<Integer>() ;
                // generates subsets of {1..k} with k equal to the size of the complete set
		KSubsetGenerator kSubsetGenerator = new KSubsetGenerator(k, completeSet.size()) ;

		try
    {
			integerSubsetGenerator.setCompleteSet(completeSet) ;
			integerSubsetGenerator.setSubsetSize(k) ;
			
			assertEquals(kSubsetGenerator.getNrOfKSubsets(), integerSubsetGenerator.getNumberOfSubsets()) ;
			
			Integer[] kSubset = kSubsetGenerator.first() ;
			List<Integer> subset = integerSubsetGenerator.next() ;
                        
                        //System.out.println("kSubset: " + Arrays.toString(kSubset));
                        //System.out.println("subset" + Arrays.toString(subset.toArray()));

			assertSubsetEquals(1, createSubsetFromIndices(kSubset), subset.toArray(new Integer[subset.size()])) ;
	    
	    for (long i = 1 ; i < integerSubsetGenerator.getNumberOfSubsets() ; ++i)
	    {
                    kSubsetGenerator.successor(kSubset) ;
                    subset = integerSubsetGenerator.next() ;
                    
                    //System.out.println("kSubset: " + Arrays.toString(kSubset));
                    //System.out.println("subset" + Arrays.toString(subset.toArray()));

		    assertSubsetEquals(i+1, createSubsetFromIndices(kSubset), subset.toArray(new Integer[subset.size()])) ;
	    }
            
            assertFalse(integerSubsetGenerator.hasNext());
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
    
    for (int i = 0 ; i < expected.length ; ++i)
    {
	    assertEquals("In subset " + index +" Value at position " + i, expected[i].intValue(), actual[i].intValue()) ;
    }
  }
        
        /**
         * Create subset of complete set, from subset of indices in {1..k}.
         * 
         * @param indices
         * @return 
         */
        private Integer[] createSubsetFromIndices(Integer[] indices){
            Integer[] subset = new Integer[indices.length];
            for(int i=0; i<indices.length; i++){
                subset[i] = completeSet.get(indices[i]-1);
            }
            return subset;
        }
        
        @Test
	public void testNoNext() throws CoreHunterException {
            
            IndexSubsetGenerator<Integer> generator = new IndexSubsetGenerator<Integer>();
            generator.setCompleteSet(completeSet);
            generator.setSubsetSize(4);
            
            // generate all subsets
            for(int i=0; i<210; i++){
                generator.next();
            }
            
            // check if error thrown when trying to get next subset
            boolean thrown = false;
            try{
                generator.next();
            }catch(NoSuchElementException ex){
                thrown = true;
            }
            assertTrue(thrown);
            
        }
}
