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

package org.corehunter.search;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.corehunter.Accession;
import org.corehunter.AccessionCollection;
import org.corehunter.CoreHunterException;
import org.corehunter.measures.PseudoMeasure;

/**
 * * Steepest Descent search.
 * 
 * Always continue with the best of all neighbors, if it is better than the
 * current core set, and stop search if no improvement can be made. This is also
 * called an "iterative improvement" strategy.
 * 
 */
public class SteepestDescentSearch extends AbstractSubsetSearch
{

	private AccessionCollection ac ;
	private Neighborhood nh ;
	private PseudoMeasure pm ;
	private int sampleMin ;
	private int sampleMax ;
	private double runtime ;
	private double minProg ;
	
	public SteepestDescentSearch(AccessionCollection ac, Neighborhood nh,
			PseudoMeasure pm, int sampleMin, int sampleMax, double runtime,
			double minProg)
	{
		super();
		this.ac = ac;
		this.nh = nh;
		this.pm = pm;
		this.sampleMin = sampleMin;
		this.sampleMax = sampleMax;
		this.runtime = runtime;
		this.minProg = minProg;
	}
	
	@Override
	protected void runSearch() throws CoreHunterException
	{
		double score, newScore;
		int size, newSize;
		List<Accession> core, unselected;

		String cacheId = PseudoMeasure.getUniqueId();

		Random r = new Random();

		List<Accession> accessions = ac.getAccessions();

		// create unselected list
		unselected = new ArrayList<Accession>(accessions);
		// select an initial core
		core = new ArrayList<Accession>();
		int j;
		Accession a;
		for (int i = 0; i < sampleMax; i++)
		{
			j = r.nextInt(unselected.size());
			a = unselected.remove(j);
			core.add(a);
		}
		score = pm.calculate(core, cacheId);
		size = core.size();

		ThreadMXBean tb = ManagementFactory.getThreadMXBean();
		double sTime = tb.getCurrentThreadCpuTime();
		double eTime = sTime + runtime * 1000000000;

		handleNewBestSolution(core, score) ;
		
		boolean cont = true;
		while (cont)
		{
			// run Steepest Descent search step
			nh.genBestNeighbor(core, unselected, pm, cacheId);
			newScore = pm.calculate(core, cacheId);
			newSize = core.size();

			if (newScore > score || (newScore == score && newSize < size))
			{
				// check min progression
				if (newSize >= size && newScore - score < minProg)
				{
					cont = false;
				}
				// report BETTER solution was found
				System.out.println("best score: " + newScore + "\tsize: "
						+ newSize + "\ttime: "
						+ (tb.getCurrentThreadCpuTime() - sTime) / 1000000000);
				// accept new core!
				score = newScore;
				size = newSize;
				// continue if time left
				cont = cont && tb.getCurrentThreadCpuTime() < eTime;

				handleNewBestSolution(core, newScore) ;
			}
			else
			{
				// Don't accept new core
				nh.undoLastPerturbation(core, unselected);
				// All neighbors are worse than current core, so stop search
				cont = false;
			}
		}
	}

}
