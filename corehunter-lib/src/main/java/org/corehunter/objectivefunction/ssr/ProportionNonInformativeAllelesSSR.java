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

package org.corehunter.objectivefunction.ssr;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.corehunter.CoreHunterException;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.objectivefunction.impl.CachedResult;
import org.corehunter.search.solution.SubsetSolution;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public final class ProportionNonInformativeAllelesSSR<IndexType> extends AbstractAccessionSSRObjectiveFunction<IndexType>
{
	private Map<String, PNCachedResult<IndexType>>	cachedResults;

	public ProportionNonInformativeAllelesSSR()
	{
		this("PN", "Proportion of non-informative alleles");
	}

	public ProportionNonInformativeAllelesSSR(String name, String description)
	{
		super(name, description);
		cachedResults = Collections
		    .synchronizedMap(new HashMap<String, PNCachedResult<IndexType>>());
	}

	protected ProportionNonInformativeAllelesSSR(ProportionNonInformativeAllelesSSR<IndexType> objectiveFunction) 
	{
		super(objectiveFunction) ;
	}
	
	@Override
  public ObjectiveFunction<SubsetSolution<IndexType>> copy()
  {
	  return new ProportionNonInformativeAllelesSSR<IndexType>(this);
  }
	
	@Override
  public boolean isMinimizing()
  {
	  return true;
  }
	
	public double calculate(SubsetSolution<IndexType> solution) throws CoreHunterException, CoreHunterException
	{
		return calculate(solution, new PNCachedResult<IndexType>(solution));
	}

	// TODO: not currently working, so use the slow method for now
	//
	public double calculate(SubsetSolution<IndexType> solution, String cacheId) throws CoreHunterException
	{
		PNCachedResult<IndexType> cache = cachedResults.get(cacheId);

		if (cache == null)
		{
			cache = new PNCachedResult<IndexType>(solution);
			cachedResults.put(cacheId, cache);
		}

		return calculate(solution, cache);
	}

	protected double calculate(SubsetSolution<IndexType>solution, PNCachedResult<IndexType> cache) throws CoreHunterException
	{
		List<IndexType> aIndices = cache.getAddedIndices(solution.getIndices());
		List<IndexType> rIndices = cache.getRemovedIndices(solution.getIndices());

		int alleleCounts[] = cache.getAlleleCounts();
		
		int addTotals[] = getData().getAlleleCounts(aIndices);
		int remTotals[] = getData().getAlleleCounts(rIndices);

		int alleleCnt = 0;
		for (int i = 0; i < alleleCounts.length; i++)
		{
			int diff = 0;
			if (addTotals != null)
			{
				diff += addTotals[i];
			}
			if (remTotals != null)
			{
				diff -= remTotals[i];
			}
			alleleCounts[i] += diff;
			if (alleleCounts[i] <= 0)
			{
				alleleCnt += 1;
			}
		}

		cache.setIndices(solution.getIndices());

		return (double) alleleCnt / (double) alleleCounts.length;
	}

	private class PNCachedResult<IndexType2> extends CachedResult<IndexType2>
	{
		private int	pAlleleCounts[];

		public PNCachedResult(SubsetSolution<IndexType2> solution) throws UnknownIndexException
		{
			super();
			IndexType2 index1 = solution.getIndices().iterator().next();
			@SuppressWarnings("unchecked")
      int alleleCnt = getData().getAlleleCount((IndexType) index1);

			pAlleleCounts = new int[alleleCnt];
			for (int i = 0; i < alleleCnt; i++)
			{
				pAlleleCounts[i] = 0;
			}
		}

		public int[] getAlleleCounts()
		{
			return pAlleleCounts;
		}
	}
}
