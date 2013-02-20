package org.corehunter.test.search;

import static org.corehunter.Constants.SECOND ;
import static org.corehunter.Constants.MINUTE ;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.corehunter.CoreHunterException;
import org.corehunter.search.ObjectiveSearch;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.impl.PrintWriterSearchListener;
import org.corehunter.search.solution.Solution;

public abstract class SearchTest<SolutionType extends Solution, SearchType extends Search<SolutionType>>
{
	protected final long DEFAULT_RUNTIME = MINUTE ;
	protected final long DEFAULT_STUCKTIME = SECOND ;
	protected final double DEFAULT_MINIMUM_PROGRESSION =  0 ;
	protected final int DEFAULT_MINIMUM_SIZE = 2 ;
	protected final int DEFAULT_MAXIMUM_SIZE = 5 ;
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
			
	   search.addSearchListener(new PrintWriterSearchListener<SolutionType>()) ;	
			
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

}
