package org.corehunter.test.search.ssr;

import static org.junit.Assert.fail;

import java.io.File;

import org.corehunter.CoreHunterException;
import org.corehunter.model.impl.AbstractFileUtility;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImplDataFileReader;
import org.corehunter.neighbourhood.impl.RandomSingleNeighbourhood;
import org.corehunter.search.impl.ExhaustiveSubsetSearch;
import org.corehunter.search.impl.IntegerSubsetGenerator;
import org.corehunter.search.impl.SteepestDescentSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.UnorderedIntegerListSubsetSolution;
import org.corehunter.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.test.search.SubsetSearchTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class SSRSteepestDescentSearchTest extends SubsetSearchTest<Integer, SubsetSolution<Integer>, ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>>
{
	@Test
	public void testDefaults()
	{	
		SteepestDescentSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>> 
			search = new SteepestDescentSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>>() ;
	
		try
    {
    	IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator() ;
    	integerSubsetGenerator.setSubsetSize(2) ;
			 
	    search.setSolution(new UnorderedIntegerListSubsetSolution(dataFull.getIndices(), integerSubsetGenerator.first())) ;
	
	    search.setData(dataFull) ;
	    search.setObjectiveFunction(new ModifiedRogersDistanceSSR()) ;
	    RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>() ;
	    neighbourhood.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE) ;
	    neighbourhood.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE) ;
	    search.setNeighbourhood(neighbourhood) ;
	    search.setRuntime(DEFAULT_RUNTIME) ;
	    search.setMinimumProgression(DEFAULT_MINIMUM_PROGRESSION) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
		
		testSearch(search) ;
	}

	
}
