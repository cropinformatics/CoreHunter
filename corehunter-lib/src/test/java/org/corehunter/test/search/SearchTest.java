package org.corehunter.test.search;

import static org.corehunter.Constants.NANO_SECONDS_IN_MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.corehunter.CoreHunterException;
import org.corehunter.search.ObjectiveSearch;
import org.corehunter.search.Search;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.Solution;

public abstract class SearchTest<SolutionType extends Solution, SearchType extends Search<SolutionType>>
{
	protected final long DEFAULT_RUNTIME = 60 * NANO_SECONDS_IN_MILLISECONDS ;
	protected final long DEFAULT_STUCKTIME = NANO_SECONDS_IN_MILLISECONDS ;
	protected final long DEFAULT_MINIMUM_PROGRESSION_TIME = NANO_SECONDS_IN_MILLISECONDS ;
	
	@SuppressWarnings("rawtypes")
  public void testSearch(Search<SolutionType> search)
	{
		try
    {
	   search.start() ;
	   
	   assertEquals("Not completed", SearchStatus.COMPLETED, search.getStatus()) ;
	   assertNotNull("No result", search.getBestSolution()) ;
	   if (search instanceof ObjectiveSearch && !((ObjectiveSearch)search).getObjectiveFunction().isMinimizing())
	  	 assertTrue("Not completed", Double.MIN_VALUE < search.getBestSolutionEvaluation()) ;
	   else
	  	 assertTrue("Not completed", Double.MAX_VALUE > search.getBestSolutionEvaluation()) ;
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
