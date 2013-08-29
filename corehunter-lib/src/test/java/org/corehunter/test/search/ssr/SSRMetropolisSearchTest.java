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

import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.impl.ExactSingleNeighbourhood;
import org.corehunter.objectivefunction.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.search.SearchListener;
import org.corehunter.search.impl.IntegerSubsetGenerator;
import org.corehunter.search.impl.MetropolisSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.corehunter.test.search.SubsetSearchTest;
import org.corehunter.test.search.impl.CachedSolutionPrintWriterSubsetSearchListener;
import org.junit.Test;

public class SSRMetropolisSearchTest extends SubsetSearchTest<Integer, SubsetSolution<Integer>> {

    @Test
    public void testDefaultsOnDataFull() {
        
        System.out.println("");
        System.out.println("######################################################");
        System.out.println("# SSR Metropolis Search - Test Defaults -- Data Full #");
        System.out.println("######################################################");
        System.out.println("");
        
        MetropolisSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>> search = 
        		new MetropolisSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>>();

        try {
            IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator();
            integerSubsetGenerator.setSubsetSize(2);
            integerSubsetGenerator.setCompleteSet(dataFull.getIndices());

            search.setInitialSolution(new IntegerSubsetSolution(dataFull.getIndices(), integerSubsetGenerator.next()));

            search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            ((ModifiedRogersDistanceSSR)search.getObjectiveFunction()).setData(dataFull);
            ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>();
            neighbourhood.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE);
            neighbourhood.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE);
            search.setIndices(dataFull.getIndices()) ;
            search.setNeighbourhood(neighbourhood);
            search.setRuntimeLimit(DEFAULT_RUNTIME);
            search.setMaxTimeWithoutImprovement(DEFAULT_STUCKTIME);
            search.setMinimumProgression(DEFAULT_MINIMUM_PROGRESSION);
            search.setMaximumNumberOfSteps(DEFAULT_NUMBER_OF_STEPS);
            search.setTemperature(DEFAULT_MAXIMUM_TEMPERATURE);
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }

        testSearch(search);
    }

    @Test
    public void testSmallSubsetOnDataFull() {
        
        System.out.println("");
        System.out.println("##########################################################");
        System.out.println("# SSR Metropolis Search - Test Small Subset -- Data Full #");
        System.out.println("##########################################################");
        System.out.println("");
        
        MetropolisSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>> search = 
        		new MetropolisSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>>();

        try {
            IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator();
            integerSubsetGenerator.setSubsetSize(2);
            integerSubsetGenerator.setCompleteSet(dataFull.getIndices());

            search.setInitialSolution(new IntegerSubsetSolution(dataFull.getIndices(), integerSubsetGenerator.next()));

            search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            ((ModifiedRogersDistanceSSR)search.getObjectiveFunction()).setData(dataFull);
            ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>();
            neighbourhood.setSubsetMinimumSize(2);
            neighbourhood.setSubsetMaximumSize(5);
            search.setIndices(dataFull.getIndices()) ;
            search.setNeighbourhood(neighbourhood);
            search.setRuntimeLimit(DEFAULT_RUNTIME);
            search.setMaxTimeWithoutImprovement(DEFAULT_STUCKTIME);
            search.setMinimumProgression(DEFAULT_MINIMUM_PROGRESSION);
            search.setMaximumNumberOfSteps(DEFAULT_NUMBER_OF_STEPS);
            search.setTemperature(DEFAULT_MAXIMUM_TEMPERATURE);
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }

        testSearch(search);
    }
    
    @Test
    public void testSmallSubsetOnDataFullNoCache() {
        
        System.out.println("");
        System.out.println("#####################################################################");
        System.out.println("# SSR Metropolis Search - Test Small Subset -- Data Full (no cache) #");
        System.out.println("#####################################################################");
        System.out.println("");
        
        MetropolisSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>> search = 
        		new MetropolisSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>>();

        try {
            IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator();
            integerSubsetGenerator.setSubsetSize(2);
            integerSubsetGenerator.setCompleteSet(dataFull.getIndices());

            search.setInitialSolution(new IntegerSubsetSolution(dataFull.getIndices(), integerSubsetGenerator.next()));

            search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            ((ModifiedRogersDistanceSSR)search.getObjectiveFunction()).setData(dataFull);
            ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>();
            neighbourhood.setSubsetMinimumSize(2);
            neighbourhood.setSubsetMaximumSize(5);
            search.setIndices(dataFull.getIndices()) ;
            search.setNeighbourhood(neighbourhood);
            search.setRuntimeLimit(DEFAULT_RUNTIME);
            search.setMaxTimeWithoutImprovement(DEFAULT_STUCKTIME);
            search.setMinimumProgression(DEFAULT_MINIMUM_PROGRESSION);
            search.setMaximumNumberOfSteps(DEFAULT_NUMBER_OF_STEPS);
            search.setTemperature(DEFAULT_MAXIMUM_TEMPERATURE);
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }

        testSearch(search);
    }
    
    @Test
    public void testSmallSubsetOnData10() {
        
        System.out.println("");
        System.out.println("########################################################");
        System.out.println("# SSR Metropolis Search - Test Small Subset -- Data 10 #");
        System.out.println("########################################################");
        System.out.println("");
        
        MetropolisSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>> search = 
        		new MetropolisSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>>();

        try {
            IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator();
            integerSubsetGenerator.setSubsetSize(2);
            integerSubsetGenerator.setCompleteSet(data10.getIndices());

            search.setInitialSolution(new IntegerSubsetSolution(data10.getIndices(), integerSubsetGenerator.next()));

            search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            ((ModifiedRogersDistanceSSR)search.getObjectiveFunction()).setData(data10);
            ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>();
            neighbourhood.setSubsetMinimumSize(2);
            neighbourhood.setSubsetMaximumSize(5);
            search.setIndices(data10.getIndices()) ;
            search.setNeighbourhood(neighbourhood);
            search.setRuntimeLimit(DEFAULT_RUNTIME);
            search.setMaxTimeWithoutImprovement(DEFAULT_STUCKTIME);
            search.setMinimumProgression(DEFAULT_MINIMUM_PROGRESSION);
            search.setMaximumNumberOfSteps(DEFAULT_NUMBER_OF_STEPS);
            search.setTemperature(DEFAULT_MAXIMUM_TEMPERATURE);
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }

        testSearch(search);
    }

    @Test
    public void testSmallSubsetSeededWithBestExhaustiveSolutionOnData10() {
        
        System.out.println("");
        System.out.println("#########################################################################");
        System.out.println("# SSR Metropolis Search - Test Small Subset Seeded With Best -- Data 10 #");
        System.out.println("#########################################################################");
        System.out.println("");
        
        MetropolisSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>> search = 
        		new MetropolisSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>>();

        try {
            IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator();
            integerSubsetGenerator.setSubsetSize(2);
            integerSubsetGenerator.setCompleteSet(data10.getIndices());

            search.setInitialSolution(findOptimalSolution(2, 5, data10));

            search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            ((ModifiedRogersDistanceSSR)search.getObjectiveFunction()).setData(data10);
            ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>();
            neighbourhood.setSubsetMinimumSize(2);
            neighbourhood.setSubsetMaximumSize(5);
            search.setIndices(data10.getIndices()) ;
            search.setNeighbourhood(neighbourhood);
            search.setRuntimeLimit(DEFAULT_RUNTIME);
            search.setMaxTimeWithoutImprovement(DEFAULT_STUCKTIME);
            search.setMinimumProgression(DEFAULT_MINIMUM_PROGRESSION);
            search.setMaximumNumberOfSteps(DEFAULT_NUMBER_OF_STEPS);
            search.setTemperature(DEFAULT_MAXIMUM_TEMPERATURE);
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }

        testSearch(search);
    }

    @Override
    protected SearchListener<SubsetSolution<Integer>> createSearchListener() {
        return new CachedSolutionPrintWriterSubsetSearchListener<Integer, SubsetSolution<Integer>>();
    }
}
