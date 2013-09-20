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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.corehunter.CoreHunterException;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.objectivefunction.impl.ObjectiveFunctionWithData;
import org.corehunter.objectivefunction.ssr.ModifiedRogersDistanceSSR;
import org.corehunter.search.impl.IndexSubsetGenerator;
import org.corehunter.search.impl.LRSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.corehunter.test.search.SubsetSearchTest;
import org.junit.Test;

public class SSRLRSearchTest extends
    SubsetSearchTest<Integer, SubsetSolution<Integer>>
{
	@Test
	public void lr21SearchTestWithRandomSeed()
	{
		
		System.out.println("");
		System.out.println("######################################################################");
		System.out.println("# SSR LR 21 Search - size 5 -- Data full -- Random Seed              #");
		System.out.println("######################################################################");
		System.out.println("");
		
		try
		{
			// sample random subset of size 2
			SubsetSolution<Integer> seed = findRandomSolution(2, 2, new ModifiedRogersDistanceSSR<Integer>(),
			    dataFull);

			// run LR search, seeded with random solution
			testSearch(createLRSearch(5, 5,
			    new ModifiedRogersDistanceSSR<Integer>(), dataFull, seed, 2, 1));

		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void lr21SearchSize5TestWithExhaustiveSeed()
	{
		try
		{
			System.out.println("");
			System.out.println("######################################################################");
			System.out.println("# SSR LR 21 Search - size 5 -- Data full -- Exh. Seed                #");
			System.out.println("######################################################################");
			System.out.println("");
			
			// exhaustively create best subset of size 2
			SubsetSolution<Integer> seed = findExhaustiveSolution(2, 2,
			    new ModifiedRogersDistanceSSR<Integer>(), dataFull);

			// run LR search, seeded with exhaustive solution
			testSearch(createLRSearch(5, 5,
			    new ModifiedRogersDistanceSSR<Integer>(), dataFull, seed, 2, 1));
		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void lr21SearchSezie5to7TestWithExhaustiveSeed()
	{
		try
		{
			System.out.println("");
			System.out.println("######################################################################");
			System.out.println("# SSR LR 21 Search - size 5 to 7-- Data full -- Exh. Seed            #");
			System.out.println("######################################################################");
			System.out.println("");
			
			// exhaustively create best subset of size 2
			SubsetSolution<Integer> seed = findExhaustiveSolution(2, 2,
			    new ModifiedRogersDistanceSSR<Integer>(), dataFull);

			// run LR search, seeded with exhaustive solution
			
			testSearch(createLRSearch(5, 7,
			    new ModifiedRogersDistanceSSR<Integer>(), dataFull, seed, 2, 1));
		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void lr12SearchTestWithCompleteSeed()
	{
		try
		{
			System.out.println("");
			System.out.println("######################################################################");
			System.out.println("# SSR LR 12 Search - size 5 less than all -- Data full -- Full Seed  #");
			System.out.println("######################################################################");
			System.out.println("");
			
			// start with all selected
			SubsetSolution<Integer> seed = new IntegerSubsetSolution(dataFull.getIndices(), dataFull.getIndices());
			
			// run LR search, seeded with exhaustive solution
			testSearch(createLRSearch(dataFull.getSize() - 5, dataFull.getSize() - 5,
			    new ModifiedRogersDistanceSSR<Integer>(), dataFull, seed, 1, 2));
		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}
        
        @Test
	public void lr62SearchTest()
	{
		
		System.out.println("");
		System.out.println("######################################################################");
		System.out.println("# SSR LR 62 Search - size 8 -- Data full -- Empty Seed               #");
		System.out.println("######################################################################");
		System.out.println("");
		
		try
		{
			// create empty seed solution
			SubsetSolution<Integer> seed = new IntegerSubsetSolution(dataFull.getIndices());

			// run LR search, seeded with empty solution
			testSearch(createLRSearch(8, 8,
			    new ModifiedRogersDistanceSSR<Integer>(), dataFull, seed, 6, 2));

		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}
        
        @Test
	public void lr62SearchTestSize57()
	{
		
		System.out.println("");
		System.out.println("######################################################################");
		System.out.println("# SSR LR 62 Search - size 5-7 -- Data full -- Empty Seed             #");
		System.out.println("######################################################################");
		System.out.println("");
		
		try
		{
			// create empty seed solution
			SubsetSolution<Integer> seed = new IntegerSubsetSolution(dataFull.getIndices());

			// run LR search, seeded with empty solution
			testSearch(createLRSearch(5, 7,
			    new ModifiedRogersDistanceSSR<Integer>(), dataFull, seed, 6, 2));

		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void lrSearchTestInvalidInitialSolution()
	{

		System.out.println("");
		System.out.println("######################################################################");
		System.out.println("# SSR LR Search - Invalid initial solution -- Data full              #");
		System.out.println("######################################################################");
		System.out.println("");
		
		LRSearch<Integer, SubsetSolution<Integer>> search;

		try
		{

			/******************************/
			/* Case 1: L > R (increasing) */
			/******************************/

			for (int k = 0; k < 25; k++)
			{

				// random L and R; with L > R

				int r = rg.nextInt(10) + 1;
				int l = 2 * r;

				// create too large initial subset
				int size = rg.nextInt(dataFull.getSize() - DEFAULT_MAXIMUM_SIZE)
				    + DEFAULT_MAXIMUM_SIZE + 1;
				IndexSubsetGenerator<Integer> generator = new IndexSubsetGenerator<Integer>();
				generator.setCompleteSet(dataFull.getIndices());
				generator.setSubsetSize(size);
				SubsetSolution<Integer> subset = new IntegerSubsetSolution(
				    dataFull.getIndices(), generator.next());

				// seed LR with this subset
				search = new LRSearch<Integer, SubsetSolution<Integer>>();
				search.setInitialSolution(subset);

				search.setObjectiveFunction(new ModifiedRogersDistanceSSR<Integer>());
				((ModifiedRogersDistanceSSR<Integer>) search.getObjectiveFunction())
				    .setData(dataFull);
				search.setIndices(dataFull.getIndices());
				search.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE);
				search.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE);
				search.setL(l);
				search.setR(r);

				boolean thrown = false;
				try
				{
					search.start();
				}
				catch (CoreHunterException ex)
				{
					thrown = true;
				}
				assertTrue(thrown);

			}

			/******************************/
			/* Case 2: R > L (decreasing) */
			/******************************/

			for (int k = 0; k < 25; k++)
			{

				// random L and R; with L < R

				int l = rg.nextInt(10) + 1;
				int r = 2 * l;

				// create too small initial subset
				int size = rg.nextInt(DEFAULT_MINIMUM_SIZE - 1) + 1;
				IndexSubsetGenerator<Integer> generator = new IndexSubsetGenerator<Integer>();
				generator.setCompleteSet(dataFull.getIndices());
				generator.setSubsetSize(size);
				SubsetSolution<Integer> subset = new IntegerSubsetSolution(
				    dataFull.getIndices(), generator.next());

				// seed LR with this subset
				search = new LRSearch<Integer, SubsetSolution<Integer>>();
				search.setInitialSolution(subset);

				search.setObjectiveFunction(new ModifiedRogersDistanceSSR<Integer>());
				((ModifiedRogersDistanceSSR<Integer>) search.getObjectiveFunction())
				    .setData(dataFull);
				search.setIndices(dataFull.getIndices());
				search.setSubsetMinimumSize(DEFAULT_MINIMUM_SIZE);
				search.setSubsetMaximumSize(DEFAULT_MAXIMUM_SIZE);
				search.setL(l);
				search.setR(r);

				boolean thrown = false;
				try
				{
					search.start();
				}
				catch (CoreHunterException ex)
				{
					thrown = true;
				}
				assertTrue(thrown);

			}

		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}

	}

	protected final LRSearch<Integer, SubsetSolution<Integer>> createLRSearch(
	    int minimumSize, int maximumSize,
	    ObjectiveFunctionWithData<SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> objectiveFunction,
	    AccessionSSRMarkerMatrix<Integer> data, SubsetSolution<Integer> seed,
	    int l, int r) throws CoreHunterException
	{
		LRSearch<Integer, SubsetSolution<Integer>> search = new LRSearch<Integer, SubsetSolution<Integer>>();
		
		search.setInitialSolution(seed);
		search.setObjectiveFunction(objectiveFunction);
		objectiveFunction.setData(data);
		search.setIndices(data.getIndices());
		search.setSubsetMinimumSize(minimumSize);
		search.setSubsetMaximumSize(maximumSize);
		search.setL(l);
		search.setR(r);

		return search;

	}
}
