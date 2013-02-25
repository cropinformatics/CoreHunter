package org.corehunter.test.search.ssr;

import static org.junit.Assert.fail;

import java.io.File;

import org.corehunter.CoreHunterException;
import org.corehunter.model.impl.AbstractFileUtility;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImplDataFileReader;
import org.corehunter.neighbourhood.impl.RandomSingleNeighbourhood;
import org.corehunter.search.SearchListener;
import org.corehunter.search.impl.ExhaustiveSubsetSearch;
import org.corehunter.search.impl.IntegerSubsetGenerator;
import org.corehunter.search.impl.MetropolisSearch;
import org.corehunter.search.impl.REMCSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.UnorderedIntegerListSubsetSolution;
import org.corehunter.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.test.search.SubsetSearchTest;
import org.corehunter.test.search.impl.CachedSolutionPrintWriterSubsetSearchListener;
import org.junit.BeforeClass;
import org.junit.Test;

public class SSRMetropolisSearchTest extends SubsetSearchTest<Integer, SubsetSolution<Integer>, ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>>
{
	@Test
	public void testDefaults()
	{	
		MetropolisSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>> 
		search = new MetropolisSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>>() ;
		
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
	    search.setNumberOfSteps(DEFAULT_NUMBER_OF_STEPS) ;
	    search.setRuntime(DEFAULT_RUNTIME) ;
	    search.setTemperature(DEFAULT_MAXIMUM_TEMPERATURE) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
		
		testSearch(search) ;
	}
	
	@Test
	public void testSmallSubset()
	{	
		MetropolisSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>> 
		search = new MetropolisSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>>() ;
		
		try
    {
    	IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator() ;
    	integerSubsetGenerator.setSubsetSize(2) ;
			 
	    search.setSolution(new UnorderedIntegerListSubsetSolution(dataFull.getIndices(), integerSubsetGenerator.first())) ;
	
	    search.setData(dataFull) ;
	    search.setObjectiveFunction(new ModifiedRogersDistanceSSR()) ;
	    RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>() ;
	    neighbourhood.setSubsetMinimumSize(2) ;
	    neighbourhood.setSubsetMaximumSize(5) ;
	    search.setNeighbourhood(neighbourhood) ;
	    search.setNumberOfSteps(DEFAULT_NUMBER_OF_STEPS) ;
	    search.setRuntime(DEFAULT_RUNTIME) ;
	    search.setTemperature(DEFAULT_MAXIMUM_TEMPERATURE) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
		
		testSearch(search) ;
	}
	
	@Test
	public void testSmallSubsetSeededWithBestExhaustiveSolution()
	{	
		MetropolisSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>> 
		search = new MetropolisSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>>() ;
		
		try
    {
    	IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator() ;
    	integerSubsetGenerator.setSubsetSize(2) ;
			 
	    search.setSolution(findOptimalSolution(2, 5, data10)) ;
	
	    search.setData(data10) ;
	    search.setObjectiveFunction(new ModifiedRogersDistanceSSR()) ;
	    RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>() ;
	    neighbourhood.setSubsetMinimumSize(2) ;
	    neighbourhood.setSubsetMaximumSize(5) ;
	    search.setNeighbourhood(neighbourhood) ;
	    search.setNumberOfSteps(DEFAULT_NUMBER_OF_STEPS) ;
	    search.setRuntime(DEFAULT_RUNTIME) ;
	    search.setTemperature(DEFAULT_MAXIMUM_TEMPERATURE) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
		
		testSearch(search) ;
	}

	@Override
	protected SearchListener<SubsetSolution<Integer>> createSearchListener()
  {
	  return new CachedSolutionPrintWriterSubsetSearchListener<Integer, SubsetSolution<Integer>>();
  }
}
