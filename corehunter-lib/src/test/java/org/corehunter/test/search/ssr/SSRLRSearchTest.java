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

import org.corehunter.CoreHunterException;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.search.impl.IntegerSubsetGenerator;
import org.corehunter.search.impl.LRSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.corehunter.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.test.search.SubsetSearchTest;
import static org.junit.Assert.fail;
import org.junit.Test;

public class SSRLRSearchTest extends SubsetSearchTest<Integer, SubsetSolution<Integer>> {

    @Test
    public void lr21SearchTestWithRandomSeed() {
        LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> search = new LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>();

        try {
            IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator();
            integerSubsetGenerator.setSubsetSize(2);

            search.setInitialSolution(new IntegerSubsetSolution(dataFull.getIndices(), integerSubsetGenerator.first()));

            search.setData(dataFull);
            search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            search.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE);
            search.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE);
            search.setL(2);
            search.setR(1);
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    public void lr21SearchTestWithExhaustiveSeed() {
        LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> search = new LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>();

        try {
            IntegerSubsetGenerator integerSubsetGenerator = new IntegerSubsetGenerator();
            integerSubsetGenerator.setSubsetSize(2);

            search.setInitialSolution(new IntegerSubsetSolution(dataFull.getIndices(), integerSubsetGenerator.first()));

            search.setData(dataFull);
            search.setObjectiveFunction(new ModifiedRogersDistanceSSR());
            search.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE);
            search.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE);
            search.setL(2);
            search.setR(1);
            search.setExhaustiveSearch(SubsetSearchTest.getExhaustiveSubsetSearch(2, dataFull));

        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }

        testSearch(search);
    }
}
