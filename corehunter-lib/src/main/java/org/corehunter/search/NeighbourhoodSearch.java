package org.corehunter.search;

import org.corehunter.neighbourhood.Neighbourhood;

public interface NeighbourhoodSearch<SolutionType extends Solution, NeighbourhoodType extends Neighbourhood<SolutionType>> extends ObjectiveSearch<SolutionType>
{
	public NeighbourhoodType getNeighbourhood() ;
}
