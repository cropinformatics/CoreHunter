//  Copyright 2012 Herman De Beukelaer
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

import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

import org.corehunter.neighbourhood.IndexedMove;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.junit.Test;

/**
 *
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class IndexedTabuManagerTest extends TestCase {

    @Test
    public void testIndexedTabuManager() {
        
        System.out.println("");
        System.out.println("#############################");
        System.out.println("# Test Indexed Tabu Manager #");
        System.out.println("#############################");
        System.out.println("");
        
        // create tabu manager
        IndexedTabuManager<Integer, SubsetSolution<Integer>> tabuManager = new IndexedTabuManager<Integer, SubsetSolution<Integer>>(4);
        System.out.println("Initial tabu manager state: " + tabuManager);
        // create initial subset solution
        //  - all indices: {0,1,2,3,4,5}
        //  - selected: {5,2,3}
        //  - unselected: {0,1,4}
        Collection<Integer> indices = new HashSet<Integer>();
        for(int i=0; i<6; i++){
            indices.add(i);
        }
        Collection<Integer> selected = new HashSet<Integer>();
        selected.add(5);
        selected.add(2);
        selected.add(3);
        IntegerSubsetSolution solution = new IntegerSubsetSolution(indices, selected);
        
        // 1st move: swap(0,5)
        //  - new selected: {0,2,3}
        //  - new tabu: [0,5]
        IndexedMove<Integer, SubsetSolution<Integer>> move = new SwapMove<Integer, SubsetSolution<Integer>>(0,5);
        // ensure move is not tabu
        assertFalse("Move should NOT be Tabu!", tabuManager.moveIsTabu(move));
        move.apply(solution);
        tabuManager.registerMoveTaken(move);
        System.out.println("New tabu manager state: " + tabuManager);
        // check some moves that should now be tabu
        move = new SwapMove<Integer, SubsetSolution<Integer>> (5,0);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new AdditionMove<Integer, SubsetSolution<Integer>> (5);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new DeletionMove<Integer, SubsetSolution<Integer>> (0);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        
        // 2nd move: swap(4,2)
        //  - new selected: {0,4,3}
        //  - new tabu: [0,5,2,4]
        move = new SwapMove<Integer, SubsetSolution<Integer>>(4,2);
        // ensure move is not tabu
        assertFalse("Move should NOT be Tabu!", tabuManager.moveIsTabu(move));
        move.apply(solution);
        tabuManager.registerMoveTaken(move);
        System.out.println("New tabu manager state: " + tabuManager);
        // check some moves that should now be tabu
        move = new AdditionMove<Integer, SubsetSolution<Integer>> (5);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new AdditionMove<Integer, SubsetSolution<Integer>> (2);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new DeletionMove<Integer, SubsetSolution<Integer>> (0);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new DeletionMove<Integer, SubsetSolution<Integer>> (4);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (5,0);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (5,4);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (2,0);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (2,4);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        
        // increase tabu history size by one
        tabuManager.setTabuHistorySize(tabuManager.getTabuHistorySize()+1);
        
        // 3rd move: rem(3)
        //  - new selected: {0,4}
        //  - new tabu: [0,5,2,4,3]
        move = new DeletionMove<Integer, SubsetSolution<Integer>>(3);
        // ensure move is not tabu
        assertFalse("Move should NOT be Tabu!", tabuManager.moveIsTabu(move));
        move.apply(solution);
        tabuManager.registerMoveTaken(move);
        System.out.println("New tabu manager state: " + tabuManager);
        // check some moves that should now be tabu
        move = new AdditionMove<Integer, SubsetSolution<Integer>> (5);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new AdditionMove<Integer, SubsetSolution<Integer>> (2);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new AdditionMove<Integer, SubsetSolution<Integer>> (3);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new DeletionMove<Integer, SubsetSolution<Integer>> (0);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new DeletionMove<Integer, SubsetSolution<Integer>> (4);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (5,0);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (5,4);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (2,0);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (2,4);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (3,0);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (3,4);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (1,0);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        move = new SwapMove<Integer, SubsetSolution<Integer>> (1,4);
        assertTrue("Move should be Tabu!", tabuManager.moveIsTabu(move));
        
        // only adding "1" should not be tabu
        move = new AdditionMove<Integer, SubsetSolution<Integer>> (1);
        assertFalse("Move should NOT be Tabu!", tabuManager.moveIsTabu(move));
        
    }

}