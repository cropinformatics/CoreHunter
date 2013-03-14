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

/**
 * Tabu Manager keeps track of moves which are tabu, i.e. which are not
 * allowed for a certain period of time.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public interface TabuManager<MoveType extends Move> {
    
    /**
     * Check whether a specific move is allowed in the current Tabu Manager state.
     * The evaluation of the new solution and the current best solution can be used
     * to override the tabu criterion in specific cases.
     * 
     * @param move
     * @param newEvaluation
     * @param currentBestEvaluation
     * @param minimizing
     * @return 
     */
    public boolean moveAllowed(MoveType move, double newEvaluation, double currentBestEvaluation, boolean minimizing);
    
    /**
     * Register a move which was taken. Note: this cannot be undone, so please
     * only register final moves, i.e. moves which will not be undone later.
     * 
     * @param move 
     */
    public void registerMoveTaken(MoveType move);
    
    /**
     * Set the size of the tabu history.
     * 
     * @param size 
     */
    public void setTabuHistorySize(int size);
    
    public int getTabuHistorySize();
    
    /**
     * Reset the Tabu manager's state, making all neighbours non-tabu again.
     */
    public void reset();

}
