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

public class LocalSearch extends AbstractSubsetSearch
{
	private AccessionCollection ac ;
	private Neighborhood nh ;
	private PseudoMeasure pm ;
	private int sampleMin ;
	private int sampleMax ;
	private double runtime ;
	private double minProg ;
	private double stuckTime ;

	public LocalSearch(AccessionCollection ac, Neighborhood nh,
			PseudoMeasure pm, int sampleMin, int sampleMax, double runtime,
			double minProg, double stuckTime)
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

		boolean cont = true;
		double lastImprTime = 0.0;

		ProgressWriter pw;
		handleNewBestSolution(core, score) ;

		while (cont && tb.getCurrentThreadCpuTime() < eTime)
		{
			// run Local Search step
			nh.genRandomNeighbor(core, unselected);
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
				lastImprTime = tb.getCurrentThreadCpuTime() - sTime;
				
				// accept new core!
				score = newScore;
				size = newSize;
				
				handleNewBestSolution(core, newScore) ;
			}
			else
			{
				// Reject new core
				nh.undoLastPerturbation(core, unselected);
				// check stuckTime
				if ((tb.getCurrentThreadCpuTime() - sTime - lastImprTime) / 1000000000 > stuckTime)
				{
					cont = false;
				}
			}
		}
	}

}
