// Copyright 2012 Herman De Beukelaer, Guy Davenport
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

package org.corehunter.test.search.ssr;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import org.corehunter.CoreHunterException;
import org.corehunter.objectivefunction.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.search.impl.ExhaustiveSubsetSearch;
import org.corehunter.search.impl.IndexSubsetGenerator;
import org.corehunter.search.impl.LRSearch;
import org.corehunter.search.impl.RandomSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.corehunter.test.search.SubsetSearchTest;
import org.junit.Test;

public class SSRLRSearchTest extends SubsetSearchTest<Integer, SubsetSolution<Integer>> {

    @Test
    public void lr21SearchTestWithRandomSeed() {
        
        LRSearch<Integer, SubsetSolution<Integer>> search = new LRSearch<Integer, SubsetSolution<Integer>>();

        try {
            
            // sample random subset of size 2
            
            RandomSearch<Integer, SubsetSolution<Integer>> random = new RandomSearch<Integer, SubsetSolution<Integer>>();
            random.setInitialSolution(new IntegerSubsetSolution(dataFull.getIndices()));
            random.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            ((ModifiedRogersDistanceSSR)random.getObjectiveFunction()).setData(dataFull);
            random.setIndices(dataFull.getIndices());
            random.setSubsetMinimumSize(2);
            random.setSubsetMaximumSize(2);
            
            random.start();
            
            SubsetSolution<Integer> seed = random.getBestSolution();
            
            // run LR search, seeded with random solution
            
            search.setInitialSolution(seed);

            search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            ((ModifiedRogersDistanceSSR)search.getObjectiveFunction()).setData(dataFull);
            search.setIndices(dataFull.getIndices()) ;
            search.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE);
            search.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE);
            search.setL(2);
            search.setR(1);
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
        
        testSearch(search);
    }

    @Test
    public void lr21SearchTestWithExhaustiveSeed() {
        
        LRSearch<Integer, SubsetSolution<Integer>> search = new LRSearch<Integer, SubsetSolution<Integer>>();

        try {
            
            // exhaustively create best subset of size 2
            
            ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>> exh = new ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>>();
            exh.setInitialSolution(new IntegerSubsetSolution(dataFull.getIndices()));
            exh.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            ((ModifiedRogersDistanceSSR)exh.getObjectiveFunction()).setData(dataFull);
            exh.setIndices(dataFull.getIndices());
            exh.setSubsetMinimumSize(2);
            exh.setSubsetMaximumSize(2);
            
            exh.start();
            
            SubsetSolution<Integer> seed = exh.getBestSolution();

            // run LR search, seeded with exhaustive solution

            search.setInitialSolution(seed);

            search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            ((ModifiedRogersDistanceSSR)search.getObjectiveFunction()).setData(dataFull);
            search.setIndices(dataFull.getIndices()) ;
            search.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE);
            search.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE);
            search.setL(2);
            search.setR(1);

        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }

        testSearch(search);
    }
    
    @Test
    public void lrSearchTestInvalidInitialSolution() {

        LRSearch<Integer, SubsetSolution<Integer>> search;
                
        try {
        
            /******************************/
            /* Case 1: L > R (increasing) */
            /******************************/

            for(int k=0; k<25; k++){

                // random L and R; with L > R
                
                int r = rg.nextInt(10) + 1;
                int l = 2*r;

                // create too large initial subset
                int size = rg.nextInt(dataFull.getSize() - DEFAULT_MAXIMUM_SIZE) + DEFAULT_MAXIMUM_SIZE + 1;
                IndexSubsetGenerator<Integer> generator = new IndexSubsetGenerator<Integer>();
                generator.setCompleteSet(dataFull.getIndices());
                generator.setSubsetSize(size);
                SubsetSolution<Integer> subset = new IntegerSubsetSolution(dataFull.getIndices(), generator.next());
                
                // seed LR with this subset
                search = new LRSearch<Integer, SubsetSolution<Integer>>();
                search.setInitialSolution(subset);
                
                search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
                ((ModifiedRogersDistanceSSR)search.getObjectiveFunction()).setData(dataFull);
                search.setIndices(dataFull.getIndices());
                search.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE);
                search.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE);
                search.setL(l);
                search.setR(r);
                
                boolean thrown = false;
                try{
                    search.start();
                } catch (CoreHunterException ex){
                    thrown = true;
                }
                assertTrue(thrown);

            }
            
            /******************************/
            /* Case 2: R > L (decreasing) */
            /******************************/

            for(int k=0; k<25; k++){

                // random L and R; with L < R
                
                int l = rg.nextInt(10) + 1;
                int r = 2*l;

                // create too small initial subset
                int size = rg.nextInt(DEFAULT_MINIMUM_SIZE-1) + 1;
                IndexSubsetGenerator<Integer> generator = new IndexSubsetGenerator<Integer>();
                generator.setCompleteSet(dataFull.getIndices());
                generator.setSubsetSize(size);
                SubsetSolution<Integer> subset = new IntegerSubsetSolution(dataFull.getIndices(), generator.next());
                
                // seed LR with this subset
                search = new LRSearch<Integer, SubsetSolution<Integer>>();
                search.setInitialSolution(subset);
                
                search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
                ((ModifiedRogersDistanceSSR)search.getObjectiveFunction()).setData(dataFull);
                search.setIndices(dataFull.getIndices());
                search.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE);
                search.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE);
                search.setL(l);
                search.setR(r);
                
                boolean thrown = false;
                try{
                    search.start();
                } catch (CoreHunterException ex){
                    thrown = true;
                }
                assertTrue(thrown);

            }
        
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
        
    }
    
}
