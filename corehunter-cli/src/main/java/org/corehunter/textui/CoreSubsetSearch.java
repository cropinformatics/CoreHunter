//  Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer, Guy Davenport
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

package org.corehunter.textui;

import org.corehunter.AccessionCollection;
import org.corehunter.CoreHunterException;
import org.corehunter.Search;
import org.corehunter.measures.PseudoMeasure;
import org.corehunter.search.ExhaustiveSearch;
import org.corehunter.search.LRSearch;
import org.corehunter.search.LocalSearch;
import org.corehunter.search.MixedReplicaSearch;
import org.corehunter.search.Neighborhood;
import org.corehunter.search.REMCSearch;
import org.corehunter.search.RandomSearch;
import org.corehunter.search.SteepestDescentSearch;
import org.corehunter.search.TabuSearch;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk &lt;&gt;
 * @version $Rev$
 */
public final class CoreSubsetSearch
{

	// Progress Writer settings
	private final static boolean WRITE_PROGRESS_FILE = true;
	private final static String PROGRESS_FILE_PATH = "progress";
	private final static long PROGRESS_WRITE_PERIOD = 100;

	// this class should not be instantiable from outside class
	private CoreSubsetSearch()
	{

	}

	public static AccessionCollection remcSearch(AccessionCollection ac,
			Neighborhood nh, PseudoMeasure pm, int sampleMin, int sampleMax,
			double runtime, double minProg, double stuckTime, int numReplicas,
			double minT, double maxT, int mcSteps)
	{
		return runSearch(new REMCSearch(ac, nh, pm, sampleMin, sampleMax,
				runtime, minProg, stuckTime, numReplicas, minT, maxT, mcSteps));
	}

	public static AccessionCollection exhaustiveSearch(AccessionCollection ac,
			PseudoMeasure pm, int sampleMin, int sampleMax)
	{
		return runSearch(new ExhaustiveSearch(ac, pm, sampleMin, sampleMax));
	}

	public static AccessionCollection localSearch(AccessionCollection ac,
			Neighborhood nh, PseudoMeasure pm, int sampleMin, int sampleMax,
			double runtime, double minProg, double stuckTime)
	{

		return runSearch(new LocalSearch(ac, nh, pm, sampleMin, sampleMax, runtime, minProg, stuckTime));
	}

	/**
	 * Steepest Descent search.
	 * 
	 * Always continue with the best of all neighbors, if it is better than the
	 * current core set, and stop search if no improvement can be made. This is
	 * also called an "iterative improvement" strategy.
	 * 
	 * @param ac
	 * @param nh
	 * @param pm
	 * @param sampleMin
	 * @param sampleMax
	 * @param runtime
	 * @param minProg
	 * @return
	 */
	public static AccessionCollection steepestDescentSearch(
			AccessionCollection ac, Neighborhood nh, PseudoMeasure pm,
			int sampleMin, int sampleMax, double runtime, double minProg)
	{
		return runSearch(new SteepestDescentSearch(ac, nh, pm, sampleMin, sampleMax, runtime, minProg));
	}

	/**
	 * TABU Search.
	 * 
	 * Tabu list is a list of indices at which the current core set cannot be
	 * perturbed (delete, swap) to form a new core set as long as the index is
	 * contained in the tabu list. After each perturbation step, the index of
	 * the newly added accession (if it exists) is added to the tabu list, to
	 * ensure this accesion is not again removed from the core set (or replaced)
	 * during the next few rounds.
	 * 
	 * If no new accession was added (pure deletion), a value "-1" is added to
	 * the tabu list. As long as such values are contained in the tabu list,
	 * adding a new accesion without removing one (pure addition) is considered
	 * tabu, to prevent immediately re-adding the accession which was removed in
	 * the previous step.
	 * 
	 * @param ac
	 * @param nh
	 * @param pm
	 * @param sampleMin
	 * @param sampleMax
	 * @param runtime
	 * @param minProg
	 * @param stuckTime
	 * @param tabuListSize
	 * @return
	 */
	public static AccessionCollection tabuSearch(AccessionCollection ac,
			Neighborhood nh, PseudoMeasure pm, int sampleMin, int sampleMax,
			double runtime, double minProg, double stuckTime, int tabuListSize)
	{
		return runSearch(new TabuSearch(ac, nh, pm, sampleMin, sampleMax, runtime, minProg, stuckTime, tabuListSize));
	}

	public static AccessionCollection mixedReplicaSearch(
			AccessionCollection ac, PseudoMeasure pm, int sampleMin,
			int sampleMax, double runtime, double minProg, double stuckTime,
			int nrOfTabuReplicas, int nrOfNonTabuReplicas,
			int roundsWithoutTabu, int nrOfTabuSteps, int tournamentSize,
			int tabuListSize, int boostNr, double boostMinProg,
			int boostTimeFactor, double minBoostTime, double minMCTemp,
			double maxMCTemp)
	{
		return runSearch(new MixedReplicaSearch(ac, pm, sampleMin, 
				sampleMax, runtime, minProg, stuckTime, 
				nrOfTabuReplicas, nrOfNonTabuReplicas, 
				roundsWithoutTabu, nrOfTabuSteps, tournamentSize,
				tabuListSize, boostNr, boostMinProg,
				boostTimeFactor, minBoostTime, minMCTemp,
				maxMCTemp));
	}


	public static AccessionCollection lrSearch(AccessionCollection ac,
			PseudoMeasure pm, int sampleMin, int sampleMax, int l, int r,
			boolean exhaustiveFirstPair)
	{
		return runSearch(new LRSearch(ac, pm, sampleMin, sampleMax, l, r, exhaustiveFirstPair));
	}

	/**
	 * Pick a random core set
	 * 
	 * @param ac
	 * @param sampleMin
	 * @param sampleMax
	 * @return
	 */
	public static AccessionCollection randomSearch(AccessionCollection ac,
			int sampleMin, int sampleMax)
	{
		return runSearch(new RandomSearch(ac, sampleMin, sampleMax));
	}

	public static AccessionCollection lrSearch(AccessionCollection ac,
			PseudoMeasure pm, int sampleMin, int sampleMax, int l, int r)
	{

		return lrSearch(ac, pm, sampleMin, sampleMax, l, r, true);

	}

	public static AccessionCollection semiLrSearch(AccessionCollection ac,
			PseudoMeasure pm, int sampleMin, int sampleMax, int l, int r)
	{

		return lrSearch(ac, pm, sampleMin, sampleMax, l, r, false);

	}

	public static AccessionCollection forwardSelection(AccessionCollection ac,
			PseudoMeasure pm, int sampleMin, int sampleMax)
	{

		return lrSearch(ac, pm, sampleMin, sampleMax, 1, 0);

	}

	public static AccessionCollection semiForwardSelection(
			AccessionCollection ac, PseudoMeasure pm, int sampleMin,
			int sampleMax)
	{

		return semiLrSearch(ac, pm, sampleMin, sampleMax, 1, 0);

	}

	public static AccessionCollection backwardSelection(AccessionCollection ac,
			PseudoMeasure pm, int sampleMin, int sampleMax)
	{

		return lrSearch(ac, pm, sampleMin, sampleMax, 0, 1);

	}

	private static AccessionCollection runSearch(
			Search<AccessionCollection> search)
	{
		try
		{
			search.start();

			return search.getBestSolution();
		}
		catch (CoreHunterException e)
		{
			e.printStackTrace();

			return null;
		}
	}
}
