package org.corehunter.search;

import org.corehunter.neighbourhood.Neighbourhood;
import org.corehunter.search.solution.Solution;

public interface NeighbourhoodSearch<SolutionType extends Solution, NeighbourhoodType extends Neighbourhood<SolutionType>> extends ObjectiveSearch<SolutionType>
{
	public NeighbourhoodType getNeighbourhood() ;
}
