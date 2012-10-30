package org.corehunter.search;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.corehunter.Accession;
import org.corehunter.AccessionCollection;
import org.corehunter.CoreHunterException;
import org.corehunter.Search;
import org.corehunter.measures.PseudoMeasure;

public class LRSearch extends AbstractSubsetSearch
{
	private AccessionCollection ac ;
	private PseudoMeasure pm ;
	private int sampleMin ;
	private int sampleMax ;
	private int l ;
	private int r ;
	private boolean exhaustiveFirstPair ;
	
	public LRSearch(AccessionCollection ac, PseudoMeasure pm, int sampleMin,
			int sampleMax, int l, int r, boolean exhaustiveFirstPair)
	{
		super();
		this.ac = ac;
		this.pm = pm;
		this.sampleMin = sampleMin;
		this.sampleMax = sampleMax;
		this.l = l;
		this.r = r;
		this.exhaustiveFirstPair = exhaustiveFirstPair;
	}

	@Override
	protected void runSearch() throws CoreHunterException
	{
		List<Accession> core, unselected;
		List<Accession> accessions = ac.getAccessions();
		double score, newScore, bestNewScore, dscore;
		String cacheID = PseudoMeasure.getUniqueId();
		int bestAddIndex = -1, bestRemIndex = -1;
		Stack<SinglePerturbation> history = new Stack<SinglePerturbation>();

		ThreadMXBean tb = ManagementFactory.getThreadMXBean();
		double sTime = tb.getCurrentThreadCpuTime();

		boolean skipadd = false;
		if (l > r)
		{
			// Start with minimal set, stepwise increase size
			if (exhaustiveFirstPair)
			{
				// Because distance measures require at least two accessions to
				// be
				// computable, exhaustively select the best core set of size 2
				core = exhaustiveSearch(ac, pm, 2, 2).getAccessions();
			}
			else
			{
				// Random first pair, to save computational cost: this
				// transforms the
				// deterministic lr search into a semi-random method
				core = randomSearch(ac, 2, 2).getAccessions();
			}
			unselected = new ArrayList<Accession>(accessions);
			unselected.removeAll(core);
		}
		else
		{
			// Start with full set, stepwise decrease size
			core = new ArrayList<Accession>(accessions);
			unselected = new ArrayList<Accession>();
			skipadd = true;
		}
		score = pm.calculate(core, cacheID);
		bestNewScore = score;
		System.out.println("best score: " + score + "\tsize: " + core.size()
				+ "\ttime: " + (tb.getCurrentThreadCpuTime() - sTime)
				/ 1000000000);

		boolean cont = true;
		while (cont)
		{
			// Add l new accessions to core
			if (!skipadd)
			{
				for (int i = 0; i < l; i++)
				{
					// Search for best new accession
					bestNewScore = -Double.MAX_VALUE;
					for (int j = 0; j < unselected.size(); j++)
					{
						Accession add = unselected.get(j);
						core.add(add);
						newScore = pm.calculate(core, cacheID);
						if (newScore > bestNewScore)
						{
							bestNewScore = newScore;
							bestAddIndex = j;
						}
						core.remove(core.size() - 1);
					}
					// Add best new accession
					core.add(unselected.remove(bestAddIndex));
					history.add(new Addition(bestAddIndex));
				}
				skipadd = false;
			}
			// Remove r accessions from core
			for (int i = 0; i < r; i++)
			{
				// Search for worst accession
				bestNewScore = -Double.MAX_VALUE;
				for (int j = 0; j < core.size(); j++)
				{
					Accession rem = core.remove(j);
					newScore = pm.calculate(core, cacheID);
					if (newScore > bestNewScore)
					{
						bestNewScore = newScore;
						bestRemIndex = j;
					}
					core.add(j, rem);
				}
				// Remove worst accession
				unselected.add(core.remove(bestRemIndex));
				history.add(new Deletion(bestRemIndex));
			}

			dscore = bestNewScore - score;
			score = bestNewScore;

			// Determine whether to continue search
			if (l > r)
			{
				// Increasing core size
				if (core.size() > sampleMin && dscore <= 0)
				{
					cont = false; // Equal or worse score and size increased
					// Restore previous core
					for (int i = 0; i < l + r; i++)
					{
						history.pop().undo(core, unselected);
					}
				}
				else
					if (core.size() + l - r > sampleMax)
					{
						cont = false; // Max size reached
					}

			}
			else
			{
				// Decreasing core size
				if (core.size() < sampleMax && dscore < 0)
				{
					cont = false; // Worse score
					// Restore previous core
					for (int i = 0; i < l + r; i++)
					{
						history.pop().undo(core, unselected);
					}
				}
				else
					if (core.size() + l - r < sampleMin)
					{
						cont = false; // Min size reached
					}
			}

			// Print core information
			System.out.println("best score: " + score + "\tsize: "
					+ core.size() + "\ttime: "
					+ (tb.getCurrentThreadCpuTime() - sTime) / 1000000000);
		}

	}

	private AccessionCollection exhaustiveSearch(AccessionCollection ac,
			PseudoMeasure pm, int sampleMin, int sampleMax)
	{
		return SimpleSearchRunner.runSearch(new ExhaustiveSearch(ac, pm, sampleMin, sampleMax));
	}

	private AccessionCollection randomSearch(AccessionCollection ac, int i,
			int j)
	{
		return SimpleSearchRunner.runSearch(new RandomSearch(ac, sampleMin, sampleMax));
	}
}
