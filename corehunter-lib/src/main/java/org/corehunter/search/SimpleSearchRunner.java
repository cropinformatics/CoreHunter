package org.corehunter.search;

import org.corehunter.AccessionCollection;
import org.corehunter.CoreHunterException;
import org.corehunter.Search;

public class SimpleSearchRunner
{
	public static AccessionCollection runSearch(
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
