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

import static org.corehunter.Constants.SECOND;
import static org.junit.Assert.fail;

import org.corehunter.CoreHunterException;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.neighbourhood.impl.ExactSingleNeighbourhood;
import org.corehunter.objectivefunction.impl.ObjectiveFunctionWithData;
import org.corehunter.objectivefunction.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.search.SearchListener;
import org.corehunter.search.impl.IndexSubsetGenerator;
import org.corehunter.search.impl.LocalSearch;
import org.corehunter.search.impl.REMCSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.corehunter.test.UncachedModifiedRogersDistanceSSR;
import org.corehunter.test.search.SubsetSearchTest;
import org.corehunter.test.search.impl.CachedSolutionPrintWriterSubsetSearchListener;
import org.junit.Test;

public class SSRREMCSearchTest extends
    SubsetSearchTest<Integer, SubsetSolution<Integer>>
{
  protected final long DEFAULT_RUNTIME = 10*SECOND;

	@Test
	public void testDefaults()
	{
		System.out.println("");
		System.out.println("######################################################################");
		System.out.println("# SSR REMC Search - Defaults                            -- Data Full #");
		System.out.println("######################################################################");
		System.out.println("");
		
		try
		{
			IndexSubsetGenerator<Integer> integerSubsetGenerator = new IndexSubsetGenerator<Integer>();
			integerSubsetGenerator.setSubsetSize(2);
			integerSubsetGenerator.setCompleteSet(dataFull.getIndices());
			
			testSearch(createREMCSearch(DEFAULT_MINIMUM_SIZE, DEFAULT_MINIMUM_SIZE, new ModifiedRogersDistanceSSR<Integer>(), dataFull, new IntegerSubsetSolution(
			    dataFull.getIndices(), integerSubsetGenerator.next()), 10, 4));
		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testSmallSubset()
	{
		System.out.println("");
		System.out.println("######################################################################");
		System.out.println("# SSR REMC Search - size 2 to 5                         -- Data Full #");
		System.out.println("######################################################################");
		System.out.println("");
		
		try
		{
			IndexSubsetGenerator<Integer> integerSubsetGenerator = new IndexSubsetGenerator<Integer>();
			integerSubsetGenerator.setSubsetSize(2);
			integerSubsetGenerator.setCompleteSet(dataFull.getIndices());

			testSearch(createREMCSearch(2, 5, new ModifiedRogersDistanceSSR<Integer>(), dataFull, new IntegerSubsetSolution(
			    dataFull.getIndices(), integerSubsetGenerator.next()), 10, 4));
		
		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testSmallSubsetNoCache()
	{
		System.out.println("");
		System.out.println("######################################################################");
		System.out.println("# SSR REMC Search - size 2 to 5 -- Small (no cache)                  #");
		System.out.println("######################################################################");
		System.out.println("");
		
		try
		{
			IndexSubsetGenerator<Integer> integerSubsetGenerator = new IndexSubsetGenerator<Integer>();
			integerSubsetGenerator.setSubsetSize(2);
			integerSubsetGenerator.setCompleteSet(dataFull.getIndices());

			testSearch(createREMCSearch(2, 5, new UncachedModifiedRogersDistanceSSR(), dataFull, new IntegerSubsetSolution(
			    dataFull.getIndices(), integerSubsetGenerator.next()), 10, 4));
		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}
	
	protected final REMCSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>> createREMCSearch(
			int minimumSize, int maximumSize,
			ObjectiveFunctionWithData<SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> objectiveFunction,
			AccessionSSRMarkerMatrix<Integer> data, SubsetSolution<Integer> seed, int numberOfMetropolisStepsPerRound, int numberOfReplicas)
					throws CoreHunterException
	{
		REMCSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>> search = new REMCSearch<Integer, SubsetSolution<Integer>, ExactSingleNeighbourhood<Integer, SubsetSolution<Integer>>>();

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
		search.setNumberOfMetropolisStepsPerRound(numberOfMetropolisStepsPerRound);
		search.setNumberOfReplicas(numberOfReplicas);

		return search;

	}

	@Override
	protected SearchListener<SubsetSolution<Integer>> createSearchListener()
	{
		return new CachedSolutionPrintWriterSubsetSearchListener<Integer, SubsetSolution<Integer>>();
	}
}
