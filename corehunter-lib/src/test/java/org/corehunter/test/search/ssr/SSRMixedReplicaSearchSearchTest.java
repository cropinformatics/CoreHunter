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
import org.corehunter.search.impl.MixedReplicaSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.UnorderedIntegerListSubsetSolution;
import org.corehunter.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.test.search.SubsetSearchTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class SSRMixedReplicaSearchSearchTest extends SubsetSearchTest<Integer, SubsetSolution<Integer>, ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>>
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
	public void testDefaults()
	{	
		MixedReplicaSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>> 
		search = new MixedReplicaSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>>() ;
		
		try
    {
    	IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator() ;
    	integerSubsetGenerator.setSubsetSize(2) ;
			 
	    search.setSolution(new UnorderedIntegerListSubsetSolution(data.getIndices(), integerSubsetGenerator.first())) ;
	
	    search.setData(data) ;
	    search.setObjectiveFunction(new ModifiedRogersDistanceSSR()) ;
	    RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>() ;
	    neighbourhood.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE) ;
	    neighbourhood.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE) ;
	    search.setNeighbourhood(neighbourhood) ;
	    search.setRuntime(DEFAULT_RUNTIME) ;
	    search.setStuckTime(DEFAULT_STUCKTIME) ;
	    search.setMinimumProgression(DEFAULT_MINIMUM_PROGRESSION) ;
	    
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
