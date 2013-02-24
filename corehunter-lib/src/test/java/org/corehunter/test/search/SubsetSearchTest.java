package org.corehunter.test.search;

import static org.corehunter.Constants.MINUTE;
import static org.corehunter.Constants.SECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.corehunter.CoreHunterException;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.search.ObjectiveSearch;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.impl.ExhaustiveSubsetSearch;
import org.corehunter.search.impl.IntegerSubsetGenerator;
import org.corehunter.search.impl.PrintWriterSubsetSearchListener;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.UnorderedIntegerListSubsetSolution;
import org.corehunter.ssr.ModifiedRogersDistanceSSR;

public abstract class SubsetSearchTest<IndexType, SolutionType extends SubsetSolution<IndexType>, SearchType extends Search<SolutionType>>
{
	protected final long DEFAULT_RUNTIME = MINUTE ;
	protected final long DEFAULT_STUCKTIME = SECOND ;
	protected final double DEFAULT_MINIMUM_PROGRESSION =  0 ;
	protected final int DEFAULT_MINIMUM_SIZE = 20 ;
	protected final int DEFAULT_MAXIMUM_SIZE = 50 ;
	protected final int DEFAULT_TABU_LIST_SIZE = 10 ;
	protected final double DEFAULT_MINIMUM_TEMPERATURE = 50.0;
	protected final double DEFAULT_MAXIMUM_TEMPERATURE = 200.0 ;
	protected final int DEFAULT_NUMBER_OF_STEPS = 10000 ;
  protected final int DEFAULT_NUMBER_OF_REPLICAS = 20 ;
	
	@SuppressWarnings("rawtypes")
  public void testSearch(Search<SolutionType> search)
	{
		try
    {
			
	   search.addSearchListener(new PrintWriterSubsetSearchListener<IndexType, SolutionType>()) ;	
			
	   search.start() ;
	   
	   assertEquals("Not completed", SearchStatus.COMPLETED, search.getStatus()) ;
	   assertNotNull("No result", search.getBestSolution()) ;
	   if (search instanceof ObjectiveSearch && !((ObjectiveSearch)search).getObjectiveFunction().isMinimizing())
	  	 assertTrue("Not completed", Double.MIN_VALUE < search.getBestSolutionEvaluation()) ;
	   else
	  	 assertTrue("Not completed", Double.MAX_VALUE > search.getBestSolutionEvaluation()) ;
	   
	   System.out.println(search.getBestSolution()) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}

	protected void testCopy(Search<SolutionType> search)
	{
		try
    {
	    assertEquals(search, search.copy()) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}

	public static final SubsetSolution<Integer> findOptimalSolution(int minimumSize, int maximumSize, AccessionSSRMarkerMatrix<Integer> data)
	{	
		ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> 
			search = new ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>() ;
		
		try
    {
    	search = getExhaustiveSubsetSearch(minimumSize, maximumSize, data) ;
	    search.start() ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }

		return search.getBestSolution() ;
	}
	
	public static final ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> getExhaustiveSubsetSearch(int size, AccessionSSRMarkerMatrix<Integer> data)
	{	
		ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> 
			search = new ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>() ;
		
		try
    {
    	IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator() ;
    	integerSubsetGenerator.setSubsetSize(2) ;
			 
	    search.setSolution(new UnorderedIntegerListSubsetSolution(data.getIndices(), integerSubsetGenerator.first())) ;
	    search.setData(data) ;
	    search.setObjectiveFunction(new ModifiedRogersDistanceSSR()) ;
	    search.setSubsetMinimumSize(size) ;
	    search.setSubsetMaximumSize(size) ;
	    search.setSubsetGenerator(new IntegerSubsetGenerator()) ;

    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }

		return search ;
	}
	
	public static final ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> getExhaustiveSubsetSearch(int minimumSize, int maximumSize, AccessionSSRMarkerMatrix<Integer> data)
	{	
		ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> 
			search = new ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>() ;
		
		try
    {
    	IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator() ;
    	integerSubsetGenerator.setSubsetSize(2) ;
			 
	    search.setSolution(new UnorderedIntegerListSubsetSolution(data.getIndices(), integerSubsetGenerator.first())) ;
	    search.setData(data) ;
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

		return search ;
	}
}
