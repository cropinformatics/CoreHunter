package org.corehunter.test.objectivefunction.variable;

import static org.junit.Assert.*;

import java.io.File;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Matrix;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.impl.AbstractFileUtility;
import org.corehunter.model.variable.Variable;
import org.corehunter.model.variable.impl.AccessionVariableMatrixListImplDataFileReader;
import org.corehunter.objectivefunction.variable.MeanGowerDistanceVariable;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.junit.Before;
import org.junit.Test;

public class MeanGowerDistanceVariableTest
{
	private static final String VARIABLE_DATA6= "/variabledata6.txt";
	private static final String VARIABLE_DATA7 = "/variabledata7.txt";
	private static final double DATA6_EVALUATION = 0 ;
	private static final double DATA7_EVALUATION = 0 ;
	private static final double PRECISION = 0.000000001;
	private Matrix<Integer, Object, Accession, Variable> data6;
	private Matrix<Integer, Object, Accession, Variable> data7;
	
	@Before
	public void setUpBefore() throws Exception
	{
		data6 = new AccessionVariableMatrixListImplDataFileReader(new File(getClass().getResource(VARIABLE_DATA6).getFile()), AbstractFileUtility.COMMA_DELIMITER).readData() ;
		data7 = new AccessionVariableMatrixListImplDataFileReader(new File(getClass().getResource(VARIABLE_DATA7).getFile()), AbstractFileUtility.COMMA_DELIMITER).readData() ;
	}
	
	@Test
	public void testCalculate()
	{
		try
    {
	    MeanGowerDistanceVariable objectiveFunction = new MeanGowerDistanceVariable() ;
	    objectiveFunction.setData(data6) ;
	    
	    IntegerSubsetSolution solution6 = new IntegerSubsetSolution(data6.getIndices(), data6.getIndices()) ;
	    
	    assertEquals(DATA6_EVALUATION, objectiveFunction.calculate(solution6), PRECISION) ;
	    
	    objectiveFunction.setData(data7) ;
	    
	    IntegerSubsetSolution solution7 = new IntegerSubsetSolution(data7.getIndices(), data7.getIndices()) ;
	    
	    assertEquals(DATA7_EVALUATION, objectiveFunction.calculate(solution7), PRECISION) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}

}
