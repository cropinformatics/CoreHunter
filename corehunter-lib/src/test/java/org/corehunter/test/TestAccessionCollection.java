package org.corehunter.test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.corehunter.AccessionCollection;
import org.corehunter.SSRDataset;
import org.corehunter.measures.UnknownMeasureException;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * <<Class summary>>
 *
 * @author Chris Thachuk &lt;&gt;
 * @version $Rev$
 */
public final class TestAccessionCollection {
   	private static final double precision = 0.00001;
	private AccessionCollection ac;
	private SSRDataset ssrData;
	Collection<String> ssrAccessionNames;
	Map<String, List<String>> ssrMarkersToAlleles;
	Collection<String> dartAccessionNames;
	Collection<String> dartMarkerNames;

	@Before
	public void setUpBefore() throws Exception {
		ac = new AccessionCollection();
		
		ssrAccessionNames = new HashSet<String>();
		ssrAccessionNames.add("A1");
		ssrAccessionNames.add("A2");
		ssrAccessionNames.add("A3");
		ssrAccessionNames.add("A4");
				
		ssrMarkersToAlleles = new HashMap<String, List<String>>();
		ssrMarkersToAlleles.put("M1", new ArrayList<String>());
		ssrMarkersToAlleles.get("M1").add("allele1");
		ssrMarkersToAlleles.get("M1").add("allele2");
		ssrMarkersToAlleles.get("M1").add("allele3");
		ssrMarkersToAlleles.put("M2", new ArrayList<String>());
		ssrMarkersToAlleles.get("M2").add("allele1");
		ssrMarkersToAlleles.get("M2").add("allele2");
		
		ssrData = new SSRDataset(ssrAccessionNames, ssrMarkersToAlleles);
		
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
	}
	
	@Test
	public void verifyAddDataset() throws Exception {
		ac.addDataset(ssrData);
		assertTrue(ssrAccessionNames.equals(ac.getAccessionNames()));
	}
	
	@Ignore @Test
	public void verifyCopyConstructor() throws Exception {

	}
	
	@Ignore @Test(expected=UnknownMeasureException.class)
	public void verifyGetMeasureValueForMissingMeasure() throws Exception {
		ac.addDataset(ssrData);
		//ac.getMeasureValue("MR");
	}
	
	@Ignore @Test
	public void verifyGetMeasureValue() throws Exception {
		ac.addDataset(ssrData);
		//ac.addMeasure(new ModifiedRogersDistance("MR", "Modified Rogers Distance"));
		//assertEquals(0.322580592628, ac.getMeasureValue("MR"), precision);
	}
}
