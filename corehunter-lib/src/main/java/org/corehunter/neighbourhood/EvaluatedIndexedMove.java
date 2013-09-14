package org.corehunter.neighbourhood;

import org.corehunter.search.solution.SubsetSolution;

public interface EvaluatedIndexedMove<IndexType, SolutionType extends SubsetSolution<IndexType>>extends EvaluatedMove<SolutionType>,
    IndexedMove<IndexType, SolutionType>
{

}
