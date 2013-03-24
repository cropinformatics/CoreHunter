package org.corehunter.objectivefunction.variable;

import java.util.ArrayList;
import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Matrix;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.variable.Variable;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.objectivefunction.impl.AbstractSubsetObjectiveFunction;
import org.corehunter.search.solution.SubsetSolution;

public class MeanGowerDistanceVariable extends AbstractSubsetObjectiveFunction<Integer, Matrix<Integer, Object, Accession, Variable>>
{
	
	@Override
  public double calculate(SubsetSolution<Integer> solution)
      throws CoreHunterException
  {
		List<Integer> subsetIndices = new ArrayList<Integer>(solution.getSubsetIndices()) ;
		
		
		
	  return 0;
  }

	@Override
  public ObjectiveFunction<SubsetSolution<Integer>> copy()
      throws CoreHunterException
  {
	  return new MeanGowerDistanceVariable(this);
  }

	protected MeanGowerDistanceVariable(MeanGowerDistanceVariable objectiveFuncton)
  {
	  super(objectiveFuncton);
  }
}
