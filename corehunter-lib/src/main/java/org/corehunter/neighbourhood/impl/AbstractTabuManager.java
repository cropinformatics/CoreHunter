//  Copyright 2012 Herman De Beukelaer, Guy Davenport
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.corehunter.neighbourhood.impl;

import org.corehunter.neighbourhood.Move;
import org.corehunter.neighbourhood.TabuManager;

/**
 *
 */
public abstract class AbstractTabuManager<MoveType extends Move>
            implements TabuManager<MoveType>
{
    
    // aspiration criterion delta
    protected static final double MIN_TABU_ASPIRATION_DELTA = 10e-10;
    
    // tabu history size
    protected int histSize;
    
    /**
     * Check if move allowed, with aspiration criterion: if a move leads to a solution
     * which offers an improvement w.r.t. the current best solution, the move is allowed
     * regardless of whether its involved indices are tabu or not.
     * 
     * @param move
     * @param newEvaluation Evaluation of the new solution, obtained by taking the move.
     * @param currentBestEvaluation Evaluation of the current best solution.
     * @param minimizing Tells whether the evaluation is being minimized or not.
     * @return 
     */
    public boolean moveAllowed(MoveType move, double newEvaluation, double currentBestEvaluation, boolean minimizing){
        return checkAspiration(newEvaluation, currentBestEvaluation, minimizing) || !moveIsTabu(move);
    }
    
    /**
     * Check whether a move is Tabu.
     * 
     * @param move
     * @return 
     */
    public abstract boolean moveIsTabu(MoveType move);
    
    /**
     * Check if the new evaluation improves on the current best evaluation, in this
     * case the new solution should be accepted regardless of whether the move is tabu or not.
     * 
     * @param newEvaluation
     * @param currentBestEvaluation
     * @param minimizing
     * @return 
     */
    private boolean checkAspiration(double newEvaluation, double currentBestEvaluation, boolean minimizing){
        double delta = minimizing ? currentBestEvaluation - newEvaluation : newEvaluation - currentBestEvaluation;
        return delta >= MIN_TABU_ASPIRATION_DELTA;
    }
    
    public void setTabuHistorySize(int size) {
        histSize = size;
        // handle new hist size
        handleTabuHistSizeSet();
    }
    
    protected abstract void handleTabuHistSizeSet();

    public int getTabuHistorySize() {
        return histSize;
    }

}
