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

package org.corehunter.ssr;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.corehunter.CoreHunterException;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.objectivefunction.CachedResult;
import org.corehunter.search.SubsetSolution;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public final class ShannonsDiversitySSR<IndexType> extends AbstractAccessionSSRObjectiveFunction<IndexType>
{
	private Map<String, SHCachedResult<IndexType>>	cachedResults;

	public ShannonsDiversitySSR()
	{
		this("SH", "Shannons Diversity Index");
	}

	public ShannonsDiversitySSR(String name, String description)
	{
		super(name, description);
		cachedResults = Collections
		    .synchronizedMap(new HashMap<String, SHCachedResult<IndexType>>());
	}

	@Override
	public double calculate(SubsetSolution<IndexType> solution) throws CoreHunterException, CoreHunterException
	{
		return calculate(solution, new SHCachedResult<IndexType>(solution));
	}

	@Override
	public double calculate(SubsetSolution<IndexType> solution, String cacheId) throws CoreHunterException
	{
		SHCachedResult<IndexType> cache = cachedResults.get(cacheId);

		if (cache == null)
		{
			cache = new SHCachedResult<IndexType>(solution);
			cachedResults.put(cacheId, cache);
		}

		return calculate(solution, cache);
	}

	protected double calculate(SubsetSolution<IndexType> solution, SHCachedResult<IndexType> cache) throws CoreHunterException
	{
		List<IndexType> aIndices = cache.getAddedIndices(solution.getIndices());
		List<IndexType> rIndices = cache.getRemovedIndices(solution.getIndices());

		double total = cache.getTotal();
		double alleleTotals[] = cache.getAlleleTotals();
		double addTotals[] = getData().getAlleleTotals(aIndices);
		double remTotals[] = getData().getAlleleTotals(rIndices);

		for (int i = 0; i < alleleTotals.length; i++)
		{
			double diff = 0.0;
			if (addTotals != null)
			{
				diff += addTotals[i];
			}
			if (remTotals != null)
			{
				diff -= remTotals[i];
			}
			alleleTotals[i] += diff;
			total += diff;
		}

		double sum = 0.0;
		for (int i = 0; i < alleleTotals.length; i++)
		{
			double fraction = alleleTotals[i] / total;
			if (!Double.isNaN(fraction) && fraction != 0)
			{
				// for some reason, java's precision isn't as good as C++
				// so needed to add this check in the port
				double t = fraction * Math.log(fraction);
				if (!Double.isNaN(t))
				{
					sum += t;
				}
			}
		}

		// recache our results under this id
		cache.setTotal(total);
		cache.setIndices(solution.getIndices());

		return -sum;
	}

	private class SHCachedResult<IndexType2> extends CachedResult<IndexType2>
	{
		private double	pTotal;
		private double	pAlleleTotals[];

		public SHCachedResult(SubsetSolution<IndexType2> solution) throws UnknownIndexException
		{
			super();
			IndexType2 index1 = solution.getIndices().get(0);
			@SuppressWarnings("unchecked")
      int alleleCount = getData().getAlleleCount((IndexType) index1);

			pAlleleTotals = new double[alleleCount];
			for (int i = 0; i < alleleCount; i++)
			{
				pAlleleTotals[i] = 0.0;
			}

			pTotal = 0.0;
		}

		public double getTotal()
		{
			return pTotal;
		}

		public double[] getAlleleTotals()
		{
			return pAlleleTotals;
		}

		public void setTotal(double total)
		{
			pTotal = total;
		}
	}
}