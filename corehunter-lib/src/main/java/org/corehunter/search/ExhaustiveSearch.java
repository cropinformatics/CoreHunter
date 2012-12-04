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

package org.corehunter.search;

import org.corehunter.AccessionCollection;
import org.corehunter.CoreHunterException;
import org.corehunter.SearchException;
import org.corehunter.measures.PseudoMeasure;

/**
 * Evaluate all possible core sets and return best one
 *
 */
public class ExhaustiveSearch extends AbstractSearch<AccessionCollection>
{
	private AccessionCollection ac;
	private PseudoMeasure pm;
	private int sampleMin;
	private int sampleMax;

	/**
	 * Evaluate all possible core sets and return best one
	 * 
	 * @param ac
	 * @param pm
	 * @param sampleMin
	 * @param sampleMax
	 */
	public ExhaustiveSearch(AccessionCollection ac, PseudoMeasure pm,
			int sampleMin, int sampleMax)
	{
		this.ac = ac;
		this.pm = pm;
		this.sampleMin = sampleMin;
		this.sampleMax = sampleMax;
	}

	@Override
	protected void runSearch() throws CoreHunterException
	{
		// Check if sampleMin and sampleMax are equal (required for this exh
		// search)
		if (sampleMin != sampleMax)
		{
			throw new SearchException("Minimum and maximum sample size should be equal for exhaustive search.\n");
		}
		int coreSize = sampleMin;
		
		AccessionCollection temp;
		AccessionCollection core;
		double score, bestScore = -Double.MAX_VALUE;
		double progress = 0 ;
		double newProgress ;
		String cacheID = PseudoMeasure.getUniqueId();

		// Calculate pseudomeasure for all possible core sets and return best core

		KSubsetGenerator ksub = new KSubsetGenerator(coreSize, ac.size());
		long nr = ksub.getNrOfKSubsets();

		fireSearchMessage("Nr of possible core sets: " + nr + "!");

		Integer[] icore = ksub.first();
		
		for (long i = 1; i <= nr; i++)
		{
			newProgress = (double) i / (double) nr;
			
			if (newProgress > progress)
			{
				fireSearchProgress(newProgress);
				progress = newProgress;
			}
			
			temp = ac.subset(icore);
			// Calculate pseudomeasure
			score = pm.calculate(temp.getAccessions(), cacheID);
			if (score > bestScore)
			{
				core = temp;
				bestScore = score;
				fireNewBestSolution(core, bestScore);
			}
			ksub.successor(icore);
		}
	}
}
