// Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer, Guy Davenport
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

package org.corehunter.textui;

import org.corehunter.CoreHunterException;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.neighbourhood.impl.HeuristicSingleNeighbourhood;
import org.corehunter.neighbourhood.impl.RandomSingleNeighbourhood;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.SubsetSolution;
import org.corehunter.search.impl.ExhaustiveSearch;
import org.corehunter.search.impl.LRSearch;
import org.corehunter.search.impl.LocalSearch;
import org.corehunter.search.impl.MetropolisSearch;
import org.corehunter.search.impl.MixedReplicaSearch;
import org.corehunter.search.impl.REMCSearch;
import org.corehunter.search.impl.RandomSearch;
import org.corehunter.search.impl.SteepestDescentSearch;
import org.corehunter.search.impl.TabuSearch;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk &lt;&gt;
 * @version $Rev$
 */
public final class CoreSubsetSearch
{

	// this class should not be instantiable from outside class
	private CoreSubsetSearch()
	{

	}

	public static REMCSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> remcSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    SubsetNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int sampleMinimum, int sampleMaximum, long runtime,
	    long minimumProgression, long stuckTime, int numberOfReplicas,
	    double minimumTemperature, double maximumTemperature, int steps)
	    throws CoreHunterException
	{
		REMCSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> search = new REMCSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>>();

		search.setData(data);
		search.setNeighbourhood(neighbourhood);
		search.setObjectiveFunction(objectiveFunction);
		search.setSubsetMaximumSize(sampleMaximum);
		search.setRuntime(runtime);
		search.setMinimumProgressionTime(minimumProgression);
		search.setStuckTime(stuckTime);
		search.setNumberOfReplicas(numberOfReplicas);
		search.setMinimumTemperature(minimumTemperature);
		search.setMaximumTemperature(maximumTemperature);
		search.setSteps(steps);

		search.start();

		return search;
	}
	
	public static MetropolisSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> metropolisSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    SubsetNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int runtime, int numberOfSteps)
	    throws CoreHunterException
	{
		MetropolisSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> search = 
					new MetropolisSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>>();

		search.setData(data);
		search.setNeighbourhood(neighbourhood);
		search.setObjectiveFunction(objectiveFunction);
		search.setRuntime(runtime);
		search.setNumberOfSteps(numberOfSteps) ;

		search.start();

		return search;
	}

	public static ExhaustiveSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> exhaustiveSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int subsetMinimumSize, int subsetMaximumSize) throws CoreHunterException
	{
		ExhaustiveSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> search = new ExhaustiveSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>();

		search.setData(data);
		search.setObjectiveFunction(objectiveFunction);
		search.setSubsetMinimumSize(subsetMinimumSize);
		search.setSubsetMaximumSize(subsetMaximumSize);

		return search;
	}

	public static LocalSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> localSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    SubsetNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    long runtime, long minimumProgressionTime, long stuckTime)
	    throws CoreHunterException
	{
		LocalSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> search = new LocalSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>>();

		search.setData(data);
		search.setObjectiveFunction(objectiveFunction);
		search.setNeighbourhood(neighbourhood);
		search.setRuntime(runtime);
		search.setMinimumProgressionTime(minimumProgressionTime);
		search.setStuckTime(stuckTime);

		return search;
	}

	/**
	 * Steepest Descent search. Always continue with the best of all neighbours,
	 * if it is better than the current core set, and stop search if no
	 * improvement can be made. This is also called an "iterative improvement"
	 * strategy.
	 * 
	 * @param ac
	 * @param nh
	 * @param pm
	 * @param sampleMin
	 * @param sampleMax
	 * @param runtime
	 * @param minProg
	 * @return
	 * @throws CoreHunterException
	 */
	public static SteepestDescentSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> steepestDescentSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    SubsetNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    long runtime, long minimumProgressionTime) throws CoreHunterException
	{
		SteepestDescentSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> search = new SteepestDescentSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>>();

		search.setData(data);
		search.setObjectiveFunction(objectiveFunction);
		search.setNeighbourhood(neighbourhood);
		search.setRuntime(runtime);
		search.setMinimumProgressionTime(minimumProgressionTime);

		return search;

	}

	/**
	 * TABU Search. Tabu list is a list of indices at which the current core set
	 * cannot be perturbed (delete, swap) to form a new core set as long as the
	 * index is contained in the tabu list. After each perturbation step, the
	 * index of the newly added accession (if it exists) is added to the tabu
	 * list, to ensure this accesion is not again removed from the core set (or
	 * replaced) during the next few rounds. If no new accession was added (pure
	 * deletion), a value "-1" is added to the tabu list. As long as such values
	 * are contained in the tabu list, adding a new accesion without removing one
	 * (pure addition) is considered tabu, to prevent immediately re-adding the
	 * accession which was removed in the previous step.
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
	public static TabuSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> tabuSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    SubsetNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    long runtime, long minimumProgressionTime, long stuckTime,
	    int tabuListSize) throws CoreHunterException
	{
		TabuSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> search = new TabuSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>>();

		search.setData(data);
		search.setObjectiveFunction(objectiveFunction);
		search.setNeighbourhood(neighbourhood);
		search.setRuntime(runtime);
		search.setMinimumProgressionTime(minimumProgressionTime);

		return search;
	}

	public static MixedReplicaSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> mixedReplicaSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int subsetMinimumSize, int subsetMaximumSize, long runtime,
	    long minimumProgressionTime, long stuckTime, int numberOfTabuReplicas, int numberOfNonTabuReplicas,
	    int roundsWithoutTabu, int numberOfTabuSteps, int tournamentSize,
	    int tabuListSize, int boostNumber, long boostMinimumProgressionTime,
	    int boostTimeFactor, long minBoostTime, double minMCTemp,
	    double maxMCTemp) throws CoreHunterException
	{
		MixedReplicaSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>> search = new MixedReplicaSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>, SubsetNeighbourhood<Integer, SubsetSolution<Integer>>>();

		search.setData(data);
		search.setObjectiveFunction(objectiveFunction);
		search.setSubsetMinimumSize(subsetMinimumSize);
		search.setSubsetMaximumSize(subsetMaximumSize);
		search.setRuntime(runtime);
		search.setMinimumProgressionTime(minimumProgressionTime) ;
		search.setStuckTime(stuckTime) ;
		search.setNumberOfTabuReplicas(numberOfTabuReplicas);
		search.setNumberOfNonTabuReplicas(numberOfNonTabuReplicas);
		search.setRoundsWithoutTabu(roundsWithoutTabu);
		search.setNumberOfTabuSteps(numberOfTabuSteps);
		search.setTournamentSize(tournamentSize);
		search.setTabuListSize(tabuListSize);
		search.setBoostNumber(boostNumber);
		search.setBoostMinimumProgressionTime(boostMinimumProgressionTime);
		search.setBoostTimeFactor(boostTimeFactor);

		// TODO set defaults for sub searches
		search.setLrSearchTemplate(lrSearch(null, null, boostTimeFactor,
		    boostTimeFactor, boostTimeFactor, boostTimeFactor));
		search.setLocalSearchTemplate(localSearch(data, null, objectiveFunction, runtime, minimumProgressionTime, stuckTime));
		search.setMetropolisSearchTemplate(metropolisSearch(data, null, objectiveFunction, boostTimeFactor, boostTimeFactor));
		search.setTabuSearchTemplate(tabuSearch(data, null, objectiveFunction, runtime, minimumProgressionTime, stuckTime, boostTimeFactor));

		return search;
	}

	public static LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> lrSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int subsetMinimumSize, int subsetMaximumSize, int l, int r,
	    boolean exhaustiveFirstPair) throws CoreHunterException
	{
		LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> search = new LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>();

		search.setData(data);
		search.setObjectiveFunction(objectiveFunction);
		search.setSubsetMinimumSize(subsetMinimumSize);
		search.setSubsetMaximumSize(subsetMaximumSize);
		search.setL(l);
		search.setR(r);
		return search;

	}

	/**
	 * Pick a random core set
	 * 
	 * @param ac
	 * @param sampleMin
	 * @param sampleMax
	 * @return
	 * @throws CoreHunterException
	 */
	public static RandomSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> randomSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int subsetMinimumSize, int subsetMaximumSize) throws CoreHunterException
	{
		RandomSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> search = new RandomSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>();

		search.setData(data);
		search.setObjectiveFunction(objectiveFunction);
		search.setSubsetMinimumSize(subsetMinimumSize);
		search.setSubsetMaximumSize(subsetMaximumSize);
		
		return search;
	}

	public static LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> lrSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int subsetMinimumSize, int subsetMaximumSize, int l, int r)
	    throws CoreHunterException
	{
		return lrSearch(data, objectiveFunction, subsetMinimumSize,
		    subsetMaximumSize, l, r, true);
	}

	public static LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> semiLrSearch(
	    AccessionSSRMarkerMatrix<Integer> data,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int subsetMinimumSize, int subsetMaximumSize, int l, int r)
	    throws CoreHunterException
	{
		return lrSearch(data, objectiveFunction, subsetMinimumSize,
		    subsetMaximumSize, l, r, false);
	}

	public static LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> forwardSelection(
	    AccessionSSRMarkerMatrix<Integer> data,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int subsetMinimumSize, int subsetMaximumSize)
	    throws CoreHunterException
	{
		return lrSearch(data, objectiveFunction, subsetMinimumSize,
		    subsetMaximumSize, 1, 0);
	}

	public static LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> semiForwardSelection(
	    AccessionSSRMarkerMatrix<Integer> data,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int subsetMinimumSize, int subsetMaximumSize)
	    throws CoreHunterException
	{
		return semiLrSearch(data, objectiveFunction, subsetMinimumSize,
		    subsetMaximumSize, 1, 0);
	}

	public static LRSearch<Integer, SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> backwardSelection(
	    AccessionSSRMarkerMatrix<Integer> data,
	    ObjectiveFunction<SubsetSolution<Integer>> objectiveFunction,
	    int subsetMinimumSize, int subsetMaximumSize)
	    throws CoreHunterException
	{
		return lrSearch(data, objectiveFunction, subsetMinimumSize,
		    subsetMaximumSize, 0, 1);
	}

	public static RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>> randomSingleNeighbourhood(
      int sampleMin, int sampleMax) throws CoreHunterException
  {
		RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new RandomSingleNeighbourhood<Integer, SubsetSolution<Integer>>();
		
		neighbourhood.setSubsetMinimumSize(sampleMin) ;
		neighbourhood.setSubsetMaximumSize(sampleMax) ;

	  return neighbourhood;
  }
	
	public static HeuristicSingleNeighbourhood<Integer, SubsetSolution<Integer>> heuristicSingleNeighbourhood(
      int sampleMin, int sampleMax) throws CoreHunterException
  {
		HeuristicSingleNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood = new HeuristicSingleNeighbourhood<Integer, SubsetSolution<Integer>>();
		
		neighbourhood.setSubsetMinimumSize(sampleMin) ;
		neighbourhood.setSubsetMaximumSize(sampleMax) ;

	  return neighbourhood;
  }
}