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

import java.util.List;
import java.util.ListIterator;

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
public final class NumberEffectiveAllelesSSR<IndexType> extends AbstractAccessionSSRObjectiveFunction<IndexType>
{
	private NECachedResult<IndexType>	cachedResults;

	public NumberEffectiveAllelesSSR()
	{
		this("NE", "Number of effective alleles");
	}

	public NumberEffectiveAllelesSSR(String name, String description)
	{
		super(name, description);
	}
	
	protected NumberEffectiveAllelesSSR(NumberEffectiveAllelesSSR<IndexType> objectiveFunction) 
	{
		super(objectiveFunction) ;
	}
	
	@Override
  public final ObjectiveFunction<SubsetSolution<IndexType>> copy()
  {
	  return new NumberEffectiveAllelesSSR<IndexType>(this);
  }

	public double calculate(SubsetSolution<IndexType> solution) throws CoreHunterException, CoreHunterException
	{
		if (cachedResults == null)
			cachedResults = new NECachedResult<IndexType>(solution) ;

		List<IndexType> aIndices = cachedResults.getAddedIndices(solution.getIndices());
		List<IndexType> rIndices = cachedResults.getRemovedIndices(solution.getIndices());

		double markerAlleleTotals[][] = cachedResults.getMarkerAlleleTotals();
		double addTotals[][] = getData().getMarkerAlleleTotals(aIndices);
		double remTotals[][] = getData().getMarkerAlleleTotals(rIndices);

		for (int i = 0; i < markerAlleleTotals.length; i++)
		{
			for (int j = 0; j < markerAlleleTotals[i].length; j++)
			{
				double diff = 0.0;
				if (addTotals != null)
				{
					diff += addTotals[i][j];
				}
				if (remTotals != null)
				{
					diff -= remTotals[i][j];
				}
				markerAlleleTotals[i][j] += diff;
			}
		}

		double diversityTotal = 0.0;
		for (int i = 0; i < markerAlleleTotals.length; i++)
		{
			double lociTotal = 0.0;
			double lociTerm = 0.0;
			for (int j = 0; j < markerAlleleTotals[i].length; j++)
			{
				lociTerm += Math.pow(markerAlleleTotals[i][j], 2);
				lociTotal += markerAlleleTotals[i][j];
			}
			diversityTotal += Math.pow(lociTotal, 2) / lociTerm;
		}

		double score = (1.0 / (double) markerAlleleTotals.length) * diversityTotal;
		// recache our results
		cachedResults.setIndices(solution.getIndices());

		return score;
	}

	private class NECachedResult<IndexType2> extends CachedResult<IndexType2>
	{
		private double	pMarkerAlleleTotals[][];

		public NECachedResult(SubsetSolution<IndexType2> solution) throws UnknownIndexException
		{
			super();

			IndexType2 index1 = solution.getIndices().get(0);
			@SuppressWarnings("unchecked")
      int markerCnt = getData().getMarkerCount((IndexType)index1);
			double markerAlleleTotals[][] = new double[markerCnt][];

			@SuppressWarnings("unchecked")
      ListIterator<List<Double>> mItr = getData().getRowElements((IndexType)index1).listIterator();

			int i = 0;
			while (mItr.hasNext())
			{
				List<Double> alleles = mItr.next();

				markerAlleleTotals[i] = new double[alleles.size()];
				for (int j = 0; j < alleles.size(); j++)
				{
					markerAlleleTotals[i][j] = 0.0;
				}
				i++;
			}
			pMarkerAlleleTotals = markerAlleleTotals;
		}

		public double[][] getMarkerAlleleTotals()
		{
			return pMarkerAlleleTotals;
		}
	}
}
