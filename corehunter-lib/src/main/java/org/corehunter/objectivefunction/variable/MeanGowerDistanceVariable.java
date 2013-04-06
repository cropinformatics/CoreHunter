package org.corehunter.objectivefunction.variable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.ObjectUtils;
import org.corehunter.CoreHunterException;
import org.corehunter.model.Matrix;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.variable.CategoricalVariable;
import org.corehunter.model.variable.RangedVariable;
import org.corehunter.model.variable.Variable;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.objectivefunction.impl.AbstractSubsetObjectiveFunction;
import org.corehunter.search.solution.SubsetSolution;

public class MeanGowerDistanceVariable extends AbstractSubsetObjectiveFunction<Integer, Matrix<Integer, Object, Accession, Variable>>
{
	private static final String NAME = "GW";
	private static final String DESCRIPTION = "Mean Gower's Distance";
	private Double[][] distanceMatrix;
	private ArrayList<Variable> discriminateVariables;
	private int variableCount;
	private int nonDiscriminateVariableCount;

	public MeanGowerDistanceVariable()
  {
	  super(NAME, DESCRIPTION);
  }
	
	@Override
  protected void handleDataSet() throws CoreHunterException
  {
	  super.handleDataSet();
	  
	  int size = getData().getIndices().size() ;
	  
	  distanceMatrix = new Double[size][] ;
	  
		for (int i = 0 ; i < size ; ++i)
		{
		  distanceMatrix[i] = new Double[i] ;
		}
		
		discriminateVariables = new ArrayList<Variable>(getData().getColumnHeaders().getElements()) ;
		
		// checks if any variables can not be used in the distance
		// those not valid are assume to have a distance of 1
		ListIterator<Variable> iterator = discriminateVariables.listIterator() ;
		
		variableCount = discriminateVariables.size() ;
		nonDiscriminateVariableCount = 0 ;
		
		while (iterator.hasNext())
		{
			if (!isDiscriminateVariable(iterator.next()))
			{
				++nonDiscriminateVariableCount ;
				iterator.remove() ;
			}
		}
  }

	@SuppressWarnings("rawtypes")
  private boolean isDiscriminateVariable(Variable variable)
  {
		switch(variable.getType())
		{
			case BINARY:
				return true ;
			case NOMINAL:
				return isCategoricalVariableDiscriminate((CategoricalVariable)variable) ;
			case INTERVAL:
				return isRangedVariableDiscriminate((RangedVariable)variable) ;
			case ORDINAL:
				return isCategoricalVariableDiscriminate((CategoricalVariable)variable) && isRangedVariableDiscriminate((RangedVariable)variable) ;
			case RATIO:
				return isRangedVariableDiscriminate(((RangedVariable)variable)) ;
			default:
				return false;	
		}
  }

	@SuppressWarnings("rawtypes")
  private boolean isCategoricalVariableDiscriminate(CategoricalVariable variable)
  {
	  // Categorical Variables must have at least one value
	  return variable.getValues().size() > 1;
  }
	
	@SuppressWarnings("rawtypes")
  private boolean isRangedVariableDiscriminate(RangedVariable rangedVariable)
  {
		// Ranged Variables must have different max and mins
	  return rangedVariable.getMaximumValue().doubleValue() > rangedVariable.getMinimumValue().doubleValue();
  }

	protected MeanGowerDistanceVariable(MeanGowerDistanceVariable objectiveFuncton)
  {
	  super(objectiveFuncton);
  }
	
	@Override
  public double calculate(SubsetSolution<Integer> solution)
      throws CoreHunterException
  {
		List<Integer> subsetIndices = new ArrayList<Integer>(solution.getSubsetIndices()) ;
		
		double evaluation = 0 ;
		int count = 0 ;
		
		for (int i = 0 ; i < subsetIndices.size() ; ++i)
		{
			for (int j = 0 ; j < i ; ++j)
			{
				if (distanceMatrix[i][j] == null)
					distanceMatrix[i][j] = calculate(subsetIndices.get(i), subsetIndices.get(j)) ;
				
				evaluation = evaluation + distanceMatrix[i][j] ;
				
				++count ;
			}
		}
		
	  return evaluation / count;
  }

	@Override
  public ObjectiveFunction<SubsetSolution<Integer>> copy()
      throws CoreHunterException
  {
	  return new MeanGowerDistanceVariable(this);
  }

	private double calculate(int indexA, int indexB) throws UnknownIndexException
  {
		double evaluation = 0 ;

		Iterator<Variable> iterator = discriminateVariables.iterator() ;
		int columnIndex = 0 ;
		
		while (iterator.hasNext())
		{
			evaluation = evaluation + calculate(iterator.next(), getData().getElement(indexA, columnIndex), getData().getElement(indexB, columnIndex)) ;
			
			++columnIndex ;
		}
		
		evaluation = evaluation + nonDiscriminateVariableCount ; // assume discriminate variables all evaluate to 1
		 
		return evaluation / variableCount ;
  }

	@SuppressWarnings("rawtypes")
  private double calculate(Variable variable, Object elementA, Object elementB)
  {
		if (elementA != null &&  elementB != null)
		{
			switch(variable.getType())
			{
				case BINARY:
					return calculateBooleanVariable((Boolean)elementA, (Boolean)elementB) ;
				case NOMINAL:
					return calculateDiscreateVariable((CategoricalVariable)variable, elementA, elementB) ;
				case INTERVAL:
				case ORDINAL:
				case RATIO:
					return calculateRangedVariable(((RangedVariable)variable), (Number)elementA, (Number)elementB) ;
				default:
					break;
			}
		}

	  return 0;
  }

	private double calculateBooleanVariable(Boolean elementA, Boolean elementB)
  {
		if (elementA && elementB)
			return 1;
		else
			return 0 ;
  }

	@SuppressWarnings("rawtypes") 
	private double calculateDiscreateVariable(CategoricalVariable variable,
      Object elementA, Object elementB)
  {
		if (ObjectUtils.equals(elementA, elementB))
			return 1;
		else
			return 0 ;
  }

	@SuppressWarnings("rawtypes") 
	private double calculateRangedVariable(RangedVariable variable,
      Number elementA, Number elementB)
  {
	  return 1 - (Math.abs(elementA.doubleValue() - elementB.doubleValue()) / 
	  		(variable.getMaximumValue().doubleValue() - variable.getMinimumValue().doubleValue()));
  }
}
