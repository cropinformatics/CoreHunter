package org.corehunter.test.objectivefunction.ssr;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.corehunter.objectivefunction.DistanceMeasureType;
import org.corehunter.objectivefunction.ssr.AbstractAccessionSSRDistanceMeasure;
import org.corehunter.objectivefunction.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.corehunter.test.model.ssr.impl.AccessionSSRMarkerMatrixListImplWrapperForTests;
import org.junit.Before;
import org.junit.Test;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public final class TestModifiedRogersDistanceSSR
{
	private static final double	precision	= 0.00001;
	private AccessionSSRMarkerMatrixListImplWrapperForTests	        ssrData, singleAccessionSSRData;
	private AbstractAccessionSSRDistanceMeasure<Integer>	    mr;

	@Before
	public void setUpBefore() throws Exception
	{
		mr = new ModifiedRogersDistanceSSR<Integer>();

		Collection<String> accessionNames = new HashSet<String>();
		accessionNames.add("A1");
		accessionNames.add("A2");
		accessionNames.add("A3");
		accessionNames.add("A4");

		Map<String, List<String>> markersToAlleles = new HashMap<String, List<String>>();
		markersToAlleles.put("M1", new ArrayList<String>());
		markersToAlleles.get("M1").add("allele1");
		markersToAlleles.get("M1").add("allele2");
		markersToAlleles.get("M1").add("allele3");
		markersToAlleles.put("M2", new ArrayList<String>());
		markersToAlleles.get("M2").add("allele1");
		markersToAlleles.get("M2").add("allele2");

		ssrData = new AccessionSSRMarkerMatrixListImplWrapperForTests(accessionNames, markersToAlleles);

		// A1
		ssrData.setValue("A1", "M1", "allele1", 0.3);
		ssrData.setValue("A1", "M1", "allele2", 0.2);
		ssrData.setValue("A1", "M1", "allele3", 0.5);

		ssrData.setValue("A1", "M2", "allele1", 0.8);
		ssrData.setValue("A1", "M2", "allele2", 0.2);

		// A2
		ssrData.setValue("A2", "M1", "allele1", 0.1);
		ssrData.setValue("A2", "M1", "allele2", 0.0);
		ssrData.setValue("A2", "M1", "allele3", 0.9);

		ssrData.setValue("A2", "M2", "allele1", 0.4);
		ssrData.setValue("A2", "M2", "allele2", 0.6);

		// A3
		ssrData.setValue("A3", "M1", "allele1", 0.3);
		ssrData.setValue("A3", "M1", "allele2", 0.3);
		ssrData.setValue("A3", "M1", "allele3", 0.4);

		ssrData.setValue("A3", "M2", "allele1", null);
		ssrData.setValue("A3", "M2", "allele2", null);

		// A4
		ssrData.setValue("A4", "M1", "allele1", 0.8);
		ssrData.setValue("A4", "M1", "allele2", 0.0);
		ssrData.setValue("A4", "M1", "allele3", 0.2);

		ssrData.setValue("A4", "M2", "allele1", 0.5);
		ssrData.setValue("A4", "M2", "allele2", 0.5);
		
		mr.setData(ssrData) ;
                
                // single accession dataset
                
		accessionNames = new HashSet<String>();
		accessionNames.add("A1");

		markersToAlleles = new HashMap<String, List<String>>();
		markersToAlleles.put("M1", new ArrayList<String>());
		markersToAlleles.get("M1").add("allele1");
		markersToAlleles.get("M1").add("allele2");
		markersToAlleles.get("M1").add("allele3");
		markersToAlleles.put("M2", new ArrayList<String>());
		markersToAlleles.get("M2").add("allele1");
		markersToAlleles.get("M2").add("allele2");
                
                singleAccessionSSRData = new AccessionSSRMarkerMatrixListImplWrapperForTests(accessionNames, markersToAlleles);
                
                // A1
		singleAccessionSSRData.setValue("A1", "M1", "allele1", 0.3);
		singleAccessionSSRData.setValue("A1", "M1", "allele2", 0.2);
		singleAccessionSSRData.setValue("A1", "M1", "allele3", 0.5);

		singleAccessionSSRData.setValue("A1", "M2", "allele1", 0.8);
		singleAccessionSSRData.setValue("A1", "M2", "allele2", 0.2);
                
 	}

	@Test
	public void verifyMRBetweenAccessions() throws Exception
	{
		assertEquals(0.374165738677394,
                             mr.calculate(
                                ssrData.getRowHeaders().getIndexByName("A1"),
                                ssrData.getRowHeaders().getIndexByName("A2")
                             ), precision);
		assertEquals(0.070710678118655,
                             mr.calculate(
                                ssrData.getRowHeaders().getIndexByName("A1"),
                                ssrData.getRowHeaders().getIndexByName("A3")
                             ), precision);
		assertEquals(0.374165738677394,
                             mr.calculate(
                                ssrData.getRowHeaders().getIndexByName("A1"),
                                ssrData.getRowHeaders().getIndexByName("A4")
                             ), precision);
		assertEquals(0.308220700148449,
                             mr.calculate(
                                ssrData.getRowHeaders().getIndexByName("A2"),
                                ssrData.getRowHeaders().getIndexByName("A3")
                             ), precision);
		assertEquals(0.500000000000000,
                             mr.calculate(
                                ssrData.getRowHeaders().getIndexByName("A2"),
                                ssrData.getRowHeaders().getIndexByName("A4")
                             ), precision);
		assertEquals(0.308220700148449,
                             mr.calculate(
                                ssrData.getRowHeaders().getIndexByName("A3"),
                                ssrData.getRowHeaders().getIndexByName("A4")
                             ), precision);
	}

	@Test
	public void verifyAverageMRAllAccessions() throws Exception
	{
                // compute average MR of all accession pairs
		assertEquals(0.322580592628, mr.calculate(null), precision);
                // compute score for subset
                Set<Integer> indices = new HashSet<Integer>();
                indices.add(ssrData.getRowHeaders().getIndexByName("A1"));
                indices.add(ssrData.getRowHeaders().getIndexByName("A3"));
                SubsetSolution<Integer> subset = new IntegerSubsetSolution(ssrData.getIndices(), indices);
                assertEquals(0.070710678118655, mr.calculate(subset), precision);
                // again compute overall average MR to check cache funtionality
                assertEquals(0.322580592628, mr.calculate(null), precision);
	}
        
        @Test
	public void verifyZeroDistanceForSingleAccession() throws Exception
	{
            
            // 1) average MR
            
            ModifiedRogersDistanceSSR<Integer> mrSingleAccession = new ModifiedRogersDistanceSSR<Integer>();
            mrSingleAccession.setData(singleAccessionSSRData);
            // compute average MR on dataset with one single accession -- should be zero
            assertEquals(0.0, mrSingleAccession.calculate(null), precision);
            // compute average MR of empty subset -- should also be zero
            assertEquals(0.0, mrSingleAccession.calculate(new IntegerSubsetSolution(singleAccessionSSRData.getIndices())), precision);
            
            
            // 2) repeat experiments for min MR
            
            mrSingleAccession = new ModifiedRogersDistanceSSR<Integer>(DistanceMeasureType.MIN_DISTANCE);
            mrSingleAccession.setData(singleAccessionSSRData);
            // compute minimum MR on dataset with one single accession -- should be zero
            assertEquals(0.0, mrSingleAccession.calculate(null), precision);
            // compute minimum MR of empty subset -- should also be zero
            assertEquals(0.0, mrSingleAccession.calculate(new IntegerSubsetSolution(singleAccessionSSRData.getIndices())), precision);
            
        }
        
        @Test
	public void testCacheFlushUponNewData() throws Exception
	{
            // TO DO test cache flush upon setting new dataset
        }

}
