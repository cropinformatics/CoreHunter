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
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.neighbourhood.impl.ExactSingleNeighbourhood;
import org.corehunter.neighbourhood.impl.IndexedTabuManager;
import org.corehunter.objectivefunction.impl.ObjectiveFunctionWithData;
import org.corehunter.objectivefunction.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.search.impl.IndexSubsetGenerator;
import org.corehunter.search.impl.TabuSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.corehunter.test.search.SubsetSearchTest;
import org.junit.Test;

public class SSRTabuSearchTest extends SubsetSearchTest<Integer, SubsetSolution<Integer>> {

	@Test
	public void testDefaultsOnDataFull() {

		System.out.println("");
		System.out.println("################################################");
		System.out.println("# SSR Tabu Search - Test Defaults -- Data Full #");
		System.out.println("################################################");
		System.out.println("");

		try
		{
			IndexSubsetGenerator<Integer> integerSubsetGenerator = new IndexSubsetGenerator<Integer>();
			integerSubsetGenerator.setSubsetSize(2);
			integerSubsetGenerator.setCompleteSet(dataFull.getIndices());

			testSearch(createTabuSearch(DEFAULT_MINIMUM_SIZE, DEFAULT_MAXIMUM_SIZE,
					new ModifiedRogersDistanceSSR<Integer>(), dataFull,
					new IntegerSubsetSolution(dataFull.getIndices(),
							integerSubsetGenerator.next()),
							new IndexedTabuManager<Integer, SubsetSolution<Integer>>(
									DEFAULT_TABU_HIST_SIZE)));

		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}

	}

	@Test
	public void test20PercentSamplingIntensityOnDataFull() {

		System.out.println("");
		System.out.println("#####################################################################");
		System.out.println("# SSR Tabu Search - Test 20 Percent Sampling Intensity -- Data Full #");
		System.out.println("#####################################################################");
		System.out.println("");

		try
		{
			int size = (int) (0.2 * dataFull.getSize());

			IndexSubsetGenerator<Integer> integerSubsetGenerator = new IndexSubsetGenerator<Integer>();
			integerSubsetGenerator.setSubsetSize(size);
			integerSubsetGenerator.setCompleteSet(dataFull.getIndices());

			testSearch(createTabuSearch(size, size,
					new ModifiedRogersDistanceSSR<Integer>(), dataFull,
					new IntegerSubsetSolution(dataFull.getIndices(),
							integerSubsetGenerator.next()),
							new IndexedTabuManager<Integer, SubsetSolution<Integer>>(
									DEFAULT_TABU_HIST_SIZE)));

		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}

	}

	@Test
	public void testOversizedTabuHistoryOnDataFull() {

		System.out.println("");
		System.out.println("##############################################################");
		System.out.println("# SSR Tabu Search - Test Oversized Tabu History -- Data Full #");
		System.out.println("##############################################################");
		System.out.println("");

		try
		{

			IndexSubsetGenerator<Integer> integerSubsetGenerator = new IndexSubsetGenerator<Integer>();
			integerSubsetGenerator.setSubsetSize(2);
			integerSubsetGenerator.setCompleteSet(dataFull.getIndices());

			testSearch(createTabuSearch(DEFAULT_MINIMUM_SIZE, DEFAULT_MAXIMUM_SIZE,
					new ModifiedRogersDistanceSSR<Integer>(), dataFull,
					new IntegerSubsetSolution(dataFull.getIndices(),
							integerSubsetGenerator.next()),
							new IndexedTabuManager<Integer, SubsetSolution<Integer>>(
									10 * DEFAULT_MAXIMUM_SIZE)));

		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}

	}

	protected final TabuSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>> createTabuSearch(
			int minimumSize, int maximumSize,
			ObjectiveFunctionWithData<SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> objectiveFunction,
			AccessionSSRMarkerMatrix<Integer> data, SubsetSolution<Integer> seed, IndexedTabuManager<Integer, SubsetSolution<Integer>> tabuManager)
					throws CoreHunterException
					{
		TabuSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>> search = new TabuSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>>();

		search.setInitialSolution(seed);

		search.setObjectiveFunction(objectiveFunction);
		objectiveFunction.setData(data);
		ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>();
		neighbourhood.setSubsetMinimumSize(minimumSize);
		neighbourhood.setSubsetMaximumSize(maximumSize);
		search.setIndices(data.getIndices());
		search.setNeighbourhood(neighbourhood);
		search.setRuntimeLimit(DEFAULT_RUNTIME);
		search.setMaxTimeWithoutImprovement(DEFAULT_STUCKTIME);
		search.setMinimumProgression(DEFAULT_MINIMUM_PROGRESSION);
		search.setMaximumNumberOfSteps(DEFAULT_NUMBER_OF_STEPS);
		search.setTabuManager(tabuManager);

		return search;

					}


}
