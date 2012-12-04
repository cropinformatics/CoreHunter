package org.corehunter.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.corehunter.Accession;
import org.corehunter.AccessionCollection;
import org.corehunter.CoreHunterException;
import org.corehunter.measures.PseudoMeasure;

public class RandomSearch extends AbstractSubsetSearch
{
	private AccessionCollection ac ;
	private int sampleMin ;
	private int sampleMax ;
        private PseudoMeasure pm;
	
	public RandomSearch(AccessionCollection ac, PseudoMeasure pm, int sampleMin, int sampleMax)
	{
		super();
		this.ac = ac;
		this.sampleMin = sampleMin;
		this.sampleMax = sampleMax;
                this.pm = pm;
	}

	@Override
	protected void runSearch() throws CoreHunterException
	{
		List<Accession> accessions = new ArrayList<Accession>(ac.getAccessions());
		List<Accession> core = new ArrayList<Accession>();

		Random r = new Random();

		boolean cont = true;

		while (cont)
		{
			int ai = r.nextInt(accessions.size());
			Accession a = accessions.remove(ai);
			core.add(a);
			cont = core.size() < sampleMax
					&& (core.size() < sampleMin || r.nextDouble() > 1.0 / (sampleMax - sampleMin));
		}
		// register solution
                handleNewBestSolution(core, pm.calculate(core));
	}

}
