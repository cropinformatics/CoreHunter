package org.corehunter.test.search.ssr;

import static org.junit.Assert.fail;

import java.io.File;

import org.corehunter.CoreHunterException;
import org.corehunter.model.impl.AbstractFileUtility;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImplDataFileReader;
import org.corehunter.neighbourhood.impl.RandomSingleNeighbourhood;
import org.corehunter.search.SubsetSolution;
import org.corehunter.search.impl.ExhaustiveSearch;
import org.corehunter.search.impl.IntegerSubsetGenerator;
import org.corehunter.search.impl.MetropolisSearch;
import org.corehunter.search.impl.MixedReplicaSearch;
import org.corehunter.search.solution.UnorderedIntegerListSubsetSolution;
import org.corehunter.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.test.search.SearchTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class SSRMixedReplicaSearchSearchTest extends SearchTest<SubsetSolution<Integer>, ExhaustiveSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>>
{
	private static final String SSR_DATA_NAME = "bul.csv";
	private static AccessionSSRMarkerMatrix<Integer> data;

	@BeforeClass
	public static void beforeClass()
	{
		try
    {
	    data = new AccessionSSRMarkerMatrixListImplDataFileReader(new File(SSRMixedReplicaSearchSearchTest.class.getResource("/" + SSR_DATA_NAME).getFile()), AbstractFileUtility.COMMA_DELIMITER).readData() ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
    }
	}
	
	@Test
	public void test()
	{	
		MixedReplicaSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>> 
		search = new MixedReplicaSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>>() ;
		
		try
    {
	    search.setSolution(new UnorderedIntegerListSubsetSolution(data.getIndices())) ;
	    search.setData(data) ;
	    search.setObjectiveFunction(new ModifiedRogersDistanceSSR()) ;
	    RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>() ;
	    neighbourhood.setSubsetMinimumSize(2) ;
	    neighbourhood.setSubsetMaximumSize(5) ;
	    search.setNeighbourhood(neighbourhood) ;
	    search.setStuckTime(DEFAULT_RUNTIME) ;
	    search.setRuntime(DEFAULT_STUCKTIME) ;
	    search.setMinimumProgressionTime(DEFAULT_MINIMUM_PROGRESSION_TIME) ;
	    
	    // TODO set mix rep test parameters
	    // search.setBoostMinimumProgressionTime(boostMinimumProgressionTime) ;
	    // search.setBoostNumber(boostNumber) ;
	    // search.setBoostTimeFactor(boostTimeFactor) ;

	    // search.setMinimumBoostTime(minimumBoostTime) ;
	    // search.setNumberOfNonTabuReplicas(numberOfNonTabuReplicas) ;
	    // search.setNumberOfTabuReplicas(numberOfTabuReplicas);
	    // search.setNumberOfTabuSteps(numberOfTabuSteps) ;
	    // search.setRoundsWithoutTabu(roundsWithoutTabu)
	    // search.setTabuListSize(tabuListSize) ;
	    // search.setTournamentSize(tournamentSize) ;

	    // search.setLrSearchTemplate(lrSearchTemplate) ;
	    // search.setLocalSearchTemplate(localSearchTemplate) ;
	    // search.setMetropolisSearchTemplate(metropolisSearchTemplate) ;
	    // searchsetTabuSearchTemplate(tabuSearchTemplate)
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
		
		testSearch(search) ;
	}

	
}
