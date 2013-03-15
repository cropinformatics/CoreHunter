// Copyright 2012 Guy Davenport, Herman De Beukelaer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.corehunter.neighbourhood;

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.impl.IndexedTabuManager;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.PreferredSize;
import org.corehunter.search.solution.SubsetSolution;

public interface SubsetNeighbourhood<IndexType, SolutionType extends SubsetSolution<IndexType>>
        extends Neighbourhood<SolutionType, IndexedMove<IndexType, SolutionType>> {

    /**
     * Change the given solution into its best neighbour. If neighbourhood
     * contains multiple solution with exactly the same score, one of these is
     * arbitrarily selected. This method also accepts a tabu manager.
     *
     * @param solution The current solution
     * @param objectiveFunction The objectiveFunction used to evaluate the
     * solutions
     * @param tabuManager The tabu manager used to check if moves are tabu 
     * @param currentBestEvaluation The current best evaluation
     * @return The move the that was made, or null if no non-tabu neighbours were found
     */
    public IndexedMove<IndexType, SolutionType> performBestMove(SolutionType solution, ObjectiveFunction<SolutionType> objectiveFunction,
                IndexedTabuManager<IndexType> tabuManager, double currentBestEvaluation) throws CoreHunterException;

    public int getSubsetMinimumSize();

    public void setSubsetMinimumSize(int subsetMinimumSize) throws CoreHunterException;

    public int getSubsetMaximumSize();

    public void setSubsetMaximumSize(int subsetMaximumSize) throws CoreHunterException;
    
    public PreferredSize getSubsetPreferredSize();
        
    public void setSubsetPreferredSize(PreferredSize size) throws CoreHunterException;
    
}
