package org.corehunter.search;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.corehunter.Accession;
import org.corehunter.AccessionCollection;
import org.corehunter.CoreHunterException;
import org.corehunter.measures.PseudoMeasure;

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
 */
public class TabuSearch extends AbstractSubsetSearch
{
	private AccessionCollection ac ;
	private Neighborhood nh ;
	private PseudoMeasure pm ;
	private int sampleMin ;
	private int sampleMax ;
	private double runtime ;
	private double minProg ;
	private double stuckTime ;
	private int tabuListSize ;
	
	public TabuSearch(AccessionCollection ac, Neighborhood nh,
			PseudoMeasure pm, int sampleMin, int sampleMax, double runtime,
			double minProg, double stuckTime, int tabuListSize)
	{
		super();
		this.ac = ac;
		this.nh = nh;
		this.pm = pm;
		this.sampleMin = sampleMin;
		this.sampleMax = sampleMax;
		this.runtime = runtime;
		this.minProg = minProg;
		this.stuckTime = stuckTime;
		this.tabuListSize = tabuListSize;
	}

	@Override
	protected void runSearch() throws CoreHunterException
	{
		double score, bestScore;
		List<Accession> core, bestCore, unselected;
		LinkedList<Integer> tabuList;

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

		bestCore = new ArrayList<Accession>();
		bestCore.addAll(core);
		bestScore = score;

		// initialize tabu list
		tabuList = new LinkedList<Integer>();

		ThreadMXBean tb = ManagementFactory.getThreadMXBean();
		double sTime = tb.getCurrentThreadCpuTime();
		double eTime = sTime + runtime * 1000000000;

		int addIndex;
		boolean cont = true;
		double lastImprTime = 0.0;

		handleNewBestSolution(core, score) ;
		
		while (cont && tb.getCurrentThreadCpuTime() < eTime)
		{
			// run TABU search step

			// ALWAYS accept new core, even it is not an improvement
			addIndex = nh.genBestNeighbor(core, unselected, tabuList,
					bestScore, pm, cacheId);
			score = pm.calculate(core, cacheId);

			// check if new best core was found
			if (score > bestScore
					|| (score == bestScore && core.size() < bestCore.size()))
			{
				// check min progression
				if (core.size() >= bestCore.size()
						&& score - bestScore < minProg)
				{
					cont = false;
				}
				// store new best core
				bestScore = score;
				bestCore.clear();
				bestCore.addAll(core);

				lastImprTime = tb.getCurrentThreadCpuTime() - sTime;
				System.out.println("best score: " + bestScore + "\tsize: "
						+ bestCore.size() + "\ttime: " + lastImprTime
						/ 1000000000);
				
				handleNewBestSolution(bestCore, bestScore) ;
			}
			else
			{
				// check stuckTime
				if ((tb.getCurrentThreadCpuTime() - sTime - lastImprTime) / 1000000000 > stuckTime)
				{
					cont = false;
				}
			}

			// finally, update tabu list
			if (tabuList.size() == tabuListSize)
			{
				// capacity reached, remove oldest tabu index
				tabuList.poll();
			}
			// add new tabu index
			// tabuList.offer(addIndex);
			tabuList.offer(addIndex);

		}
	}

}
