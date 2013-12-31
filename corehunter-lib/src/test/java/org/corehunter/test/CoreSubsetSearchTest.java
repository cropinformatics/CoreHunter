package org.corehunter.test;

import static org.junit.Assert.*;

import org.corehunter.AccessionCollection;
import org.corehunter.DuplicateMeasureException;
import org.corehunter.SSRDataset;
import org.corehunter.UnknownMeasureException;
import org.corehunter.measures.Measure;
import org.corehunter.measures.MeasureFactory;
import org.corehunter.measures.PseudoMeasure;
import org.corehunter.search.CoreSubsetSearch;
import org.corehunter.search.RandomSingleNeighborhood;
import org.junit.Test;

public class CoreSubsetSearchTest
{
	
  private static final String DATA_FILE = "/bul.csv" ;
	private final double DEFAULT_RUNTIME = 1.0;
  private final double DEFAULT_STUCKTIME = 0.0;
  private final double DEFAULT_MINPROG = 0.0;
  
  private final double DEFAULT_REMC_MIN_TEMPERATURE = 50.0;
  private final double DEFAULT_REMC_MAX_TEMPERATURE = 200.0;
  private final int DEFAULT_REMC_REPLICAS = 10;
  private final int DEFAULT_REMC_MC_STEPS = 50;

  private final int DEFAULT_MIXREP_NR_OF_TABU_REPLICAS = 2;
  private final int DEFAULT_MIXREP_NR_OF_NON_TABU_REPLICAS = 3;
  private final int DEFAULT_MIXREP_ROUNDS_WITHOUT_TABU = 10;
  private final int DEFAULT_MIXREP_TOURNAMENT_SIZE = 2;
  private final int DEFAULT_MIXREP_NR_OF_TABU_STEPS = 5;
  private final int DEFAULT_MIXREP_BOOST_NR = 2;
  private final double DEFAULT_MIXREP_BOOST_MIN_PROG = 10e-9;
  private final int DEFAULT_MIXREP_BOOST_TIME_FACTOR = 15;
  private final double DEFAULT_MIXREP_MIN_BOOST_TIME = 0.25;
  private final double DEFAULT_MIXREP_MIN_MC_TEMP = 50.0;
  private final double DEFAULT_MIXREP_MAX_MC_TEMP = 100.0;
  
  private final int MIN_SUBSET_SIZE = 2;
  private final int MAX_SUBSET_SIZE = 3;
  
  private final int DEFAULT_LR_L = 2;
  private final int DEFAULT_LR_R = 1;

	@Test
	public void testRemcSearch()
	{
		try
    {
	    RandomSingleNeighborhood neighborhood = new RandomSingleNeighborhood(MIN_SUBSET_SIZE, MAX_SUBSET_SIZE) ;
	    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	     
	    checkResult(CoreSubsetSearch.remcSearch(accesionCollection, neighborhood, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE, DEFAULT_RUNTIME, DEFAULT_MINPROG, DEFAULT_STUCKTIME, 
	    		DEFAULT_REMC_REPLICAS, DEFAULT_REMC_MIN_TEMPERATURE, DEFAULT_REMC_MAX_TEMPERATURE, DEFAULT_REMC_MC_STEPS)) ;
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testRandomSearch()
	{
		try
    {
	    AccessionCollection accesionCollection = createAccessionCollection() ;

	    checkResult(CoreSubsetSearch.randomSearch(accesionCollection, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE)) ;
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	//@Test
	public void testExhaustiveSearchAccessionCollectionPseudoMeasureIntInt()
	{
		try
    {  
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.exhaustiveSearch(accesionCollection, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE)) ;
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	//@Test
	public void testExhaustiveSearchAccessionCollectionPseudoMeasureIntIntBoolean()
	{
		try
    {  
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.exhaustiveSearch(accesionCollection, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE, false)) ;
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testLocalSearch()
	{
		try
    {
	    RandomSingleNeighborhood neighborhood = new RandomSingleNeighborhood(MIN_SUBSET_SIZE, MAX_SUBSET_SIZE) ;
	    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.localSearch(accesionCollection, neighborhood, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE, DEFAULT_RUNTIME, DEFAULT_MINPROG, DEFAULT_STUCKTIME)) ;
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testSteepestDescentSearch()
	{
		try
    {
	    RandomSingleNeighborhood neighborhood = new RandomSingleNeighborhood(MIN_SUBSET_SIZE, MAX_SUBSET_SIZE) ;
	    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.steepestDescentSearch(accesionCollection, neighborhood, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE, DEFAULT_RUNTIME, DEFAULT_MINPROG)) ;
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testTabuSearch()
	{
		try
    {
	    RandomSingleNeighborhood neighborhood = new RandomSingleNeighborhood(MIN_SUBSET_SIZE, MAX_SUBSET_SIZE) ;
	    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.tabuSearch(accesionCollection, neighborhood, pseudoMeasure, 
	    		MIN_SUBSET_SIZE, MAX_SUBSET_SIZE, DEFAULT_RUNTIME, DEFAULT_MINPROG, DEFAULT_STUCKTIME, Math.max((int) (0.3 * MIN_SUBSET_SIZE), 1))) ;
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testMixedReplicaSearch()
	{
		try
    {    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
 		
	    checkResult(CoreSubsetSearch.mixedReplicaSearch(accesionCollection, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE, DEFAULT_RUNTIME, DEFAULT_MINPROG, DEFAULT_STUCKTIME,
	    		DEFAULT_MIXREP_NR_OF_TABU_REPLICAS, DEFAULT_MIXREP_NR_OF_NON_TABU_REPLICAS, DEFAULT_MIXREP_ROUNDS_WITHOUT_TABU, DEFAULT_MIXREP_NR_OF_TABU_STEPS, 
	    		DEFAULT_MIXREP_TOURNAMENT_SIZE, Math.max((int) (0.3 * MIN_SUBSET_SIZE), 1), DEFAULT_MIXREP_BOOST_NR, DEFAULT_MIXREP_BOOST_MIN_PROG, DEFAULT_MIXREP_BOOST_TIME_FACTOR, 
	    				DEFAULT_MIXREP_MIN_BOOST_TIME, DEFAULT_MIXREP_MIN_MC_TEMP, DEFAULT_MIXREP_MAX_MC_TEMP)) ;
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testLrSearchAccessionCollectionPseudoMeasureIntIntIntIntBoolean()
	{
		try
    {    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.lrSearch(accesionCollection, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE, DEFAULT_LR_L, DEFAULT_LR_R, false)) ;
	    
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testLrSearchAccessionCollectionPseudoMeasureIntIntIntInt()
	{
		try
    {    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.lrSearch(accesionCollection, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE, DEFAULT_LR_L, DEFAULT_LR_R)) ;
	    
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testSemiLrSearch()
	{
		try
    {    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.semiLrSearch(accesionCollection, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE, DEFAULT_LR_L, DEFAULT_LR_R)) ;
	    
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testForwardSelection()
	{
		try
    {    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.forwardSelection(accesionCollection, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE)) ;
	    
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testSemiForwardSelection()
	{
		try
    {    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.semiForwardSelection(accesionCollection, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE)) ;
	    
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testBackwardSelection()
	{
		try
    {    
	    AccessionCollection accesionCollection = createAccessionCollection() ;
	    
	    Measure measure = MeasureFactory.createMeasure("MR", accesionCollection.size()) ;
	    PseudoMeasure pseudoMeasure = new PseudoMeasure() ;
	    
	    pseudoMeasure.addMeasure(measure, 1.0);
	    
	    checkResult(CoreSubsetSearch.backwardSelection(accesionCollection, pseudoMeasure, MIN_SUBSET_SIZE, MAX_SUBSET_SIZE)) ;
	    
    }
    catch (Exception e)
    {
	    e.printStackTrace();
	    
	    fail(e.getLocalizedMessage()) ;
    }
	}

	private AccessionCollection createAccessionCollection()
  {
		AccessionCollection accesionCollection = new AccessionCollection() ;
		
		SSRDataset dataset = SSRDataset.createFromFile(SSRDatasetTest.class.getResource(DATA_FILE).getFile()) ;

		accesionCollection.addDataset(dataset) ;
		
	  return accesionCollection ;
  }
	
	private void checkResult(AccessionCollection collection)
  {
		assertNotNull(collection) ;
  }

}
