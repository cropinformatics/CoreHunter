package org.corehunter.test.objectivefunction.variable;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	private static final double DATA6_EVALUATION = 1 ;
	private static final double DATA7_1EVALUATION = 0.8 ;
	private static final double DATA7_2EVALUATION = 0.6 ;
	private static final double DATA7_3EVALUATION = 0.98 ;
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
	public void testCalculate6_1()
	{
		try
    {
	    MeanGowerDistanceVariable objectiveFunction = new MeanGowerDistanceVariable() ;
	    objectiveFunction.setData(data6) ;
	    
	    List<Integer> subset = new ArrayList<Integer>() ;
	    
	    subset.add(1) ;
	    subset.add(3) ;
	    subset.add(4) ;
	    
	    IntegerSubsetSolution solution6 = new IntegerSubsetSolution(data6.getIndices(), subset) ;
	    
	    assertEquals(DATA6_EVALUATION, objectiveFunction.calculate(solution6), PRECISION) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}
	
	@Test
	public void testCalculate6_2()
	{
		try
    {
	    MeanGowerDistanceVariable objectiveFunction = new MeanGowerDistanceVariable() ;
	    objectiveFunction.setData(data6) ;
	    
	    List<Integer> subset = new ArrayList<Integer>() ;
	    
	    subset.add(0) ;
	    subset.add(3) ;
	    subset.add(4) ;
	    
	    IntegerSubsetSolution solution6 = new IntegerSubsetSolution(data6.getIndices(), subset) ;
	    
	    assertEquals(DATA6_EVALUATION, objectiveFunction.calculate(solution6), PRECISION) ;
	    
	    objectiveFunction.setData(data7) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}
	
	@Test
	public void testCalculate7_1()
	{
		try
    {
	    MeanGowerDistanceVariable objectiveFunction = new MeanGowerDistanceVariable() ;

	    objectiveFunction.setData(data7) ;
	    
	    List<Integer> subset = new ArrayList<Integer>() ;
	    
	    subset.add(0) ;
	    subset.add(1) ;
	    
	    IntegerSubsetSolution solution7 = new IntegerSubsetSolution(data7.getIndices(), subset) ;
	    
	    assertEquals(DATA7_1EVALUATION, objectiveFunction.calculate(solution7), PRECISION) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}

	@Test
	public void testCalculate7_2()
	{
		try
    {
	    MeanGowerDistanceVariable objectiveFunction = new MeanGowerDistanceVariable() ;

	    objectiveFunction.setData(data7) ;
	    
	    List<Integer> subset = new ArrayList<Integer>() ;
	    
	    subset.add(0) ;
	    subset.add(2) ;
	    
	    IntegerSubsetSolution solution7 = new IntegerSubsetSolution(data7.getIndices(), subset) ;
	    
	    assertEquals(DATA7_2EVALUATION, objectiveFunction.calculate(solution7), PRECISION) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}
	
	@Test
	public void testCalculate7_3()
	{
		try
    {
	    MeanGowerDistanceVariable objectiveFunction = new MeanGowerDistanceVariable() ;

	    objectiveFunction.setData(data7) ;
	    
	    List<Integer> subset = new ArrayList<Integer>() ;
	    
	    subset.add(3) ;
	    subset.add(4) ;
	    
	    IntegerSubsetSolution solution7 = new IntegerSubsetSolution(data7.getIndices(), subset) ;
	    
	    assertEquals(DATA7_3EVALUATION, objectiveFunction.calculate(solution7), PRECISION) ;
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace();
	    fail(e.getLocalizedMessage()) ;
    }
	}
}
