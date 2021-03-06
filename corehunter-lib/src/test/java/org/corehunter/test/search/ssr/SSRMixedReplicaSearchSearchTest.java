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
import org.corehunter.search.impl.IndexSubsetGenerator;
import org.corehunter.search.impl.MixedReplicaSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.corehunter.test.search.SubsetSearchTest;
import org.junit.Test;

public class SSRMixedReplicaSearchSearchTest extends SubsetSearchTest<Integer, SubsetSolution<Integer>> {

    //@Test
    public void testDefaults() {
        MixedReplicaSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>> search = 
        		new MixedReplicaSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>>();

        try {
            IndexSubsetGenerator<Integer> integerSubsetGenerator = new IndexSubsetGenerator<Integer>();
            integerSubsetGenerator.setSubsetSize(2);
            integerSubsetGenerator.setCompleteSet(dataFull.getIndices());

            search.setInitialSolution(new IntegerSubsetSolution(dataFull.getIndices(), integerSubsetGenerator.next()));

            search.setObjectiveFunction(new ModifiedRogersDistanceSSR<Integer>());
            ((ModifiedRogersDistanceSSR<Integer>)search.getObjectiveFunction()).setData(dataFull);
            ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>();
            neighbourhood.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE);
            neighbourhood.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE);
            search.setIndices(dataFull.getIndices()) ;
            search.setNeighbourhood(neighbourhood);
            search.setRuntimeLimit(DEFAULT_RUNTIME);
            search.setMaxTimeWithoutImprovement(DEFAULT_STUCKTIME);
            search.setMinimumProgression(DEFAULT_MINIMUM_PROGRESSION);

            // TODO set mix rep test parameters
            // search.setBoostMinimumProgressionTime(boostMinimumProgressionTime) ;
            // search.setBoostNumber(boostNumber) ;
            // search.setBoostTimeFactor(boostTimeFactor) ;

            // search.setMinimumBoostTime(minimumBoostTime) ;
            // search.setNumberOfNonTabuReplicas(numberOfNonTabuReplicas) ;
            // search.setNumberOfTabuReplicas(numberOfTabuReplicas);
            // search.setNumberOfTabuSteps(numberOfTabuSteps) ;
            // search.setRoundsWithoutTabu(roundsWithoutTabu)
            // search.setTabuListSize(tabuListSize) ;
            // search.setTournamentSize(tournamentSize) ;

            // search.setLrSearchTemplate(lrSearchTemplate) ;
            // search.setLocalSearchTemplate(localSearchTemplate) ;
            // search.setMetropolisSearchTemplate(metropolisSearchTemplate) ;
            // searchsetTabuSearchTemplate(tabuSearchTemplate)
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }

        testSearch(search);
    }
}
