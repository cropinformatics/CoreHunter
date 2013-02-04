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
import java.util.TreeMap;

import org.corehunter.CoreHunterException;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.objectivefunction.CachedResult;
import org.corehunter.objectivefunction.DistanceMeasureType;
import org.corehunter.objectivefunction.impl.AbstractSubsetObjectiveFunction;
import org.corehunter.search.SubsetSolution;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public abstract class AbstractAccessionSSRDistanceMeasure extends 
	AbstractSubsetObjectiveFunction<Integer, AccessionSSRMarkerMatrix<Integer>> implements SSROjectiveFunction<Integer>
{
	private double[][]	                      M;
	private Map<String, DistanceCachedResult>	cachedResults;

	protected static final double	            MISSING_VAL	= -1.0;

	protected DistanceMeasureType	            type;	            // states
																																// whether mean
																																// or min
																																// distance

	// should be computed

	public AbstractAccessionSSRDistanceMeasure()
	{
		this("UM", "Unknown Measure",
		    DistanceMeasureType.MEAN_DISTANCE);
	}

	public AbstractAccessionSSRDistanceMeasure(String name, String description,
	    DistanceMeasureType type)
	{
		super(name, description);

		cachedResults = Collections
		    .synchronizedMap(new HashMap<String, DistanceCachedResult>());

		this.type = type;
	}

	@Override
  protected void handleDataSet() throws CoreHunterException
  {
	  super.handleDataSet();
	  
		// M = new ArrayList<List<Double>>(accessionCount);
		/*
		 * for(int i=0; i<accessionCount; i++) { M.add(new ArrayList<Double>(i+1));
		 * for(int j=0; j<=i; j++) { M.get(i).add(new Double(MISSING_VAL)); } }
		 */
		M = new double[getData().getSize()][];
		for (int i = 0; i < getData().getSize(); i++)
		{
			M[i] = new double[i + 1];
			for (int j = 0; j <= i; j++)
			{
				M[i][j] = MISSING_VAL;
			}
		}
  }

	@Override
	public final double calculate(SubsetSolution<Integer> solution, String cacheId) throws CoreHunterException
	{
		DistanceCachedResult cache = cachedResults.get(cacheId);

		if (cache == null)
		{
			cache = new DistanceCachedResult(solution);
			cachedResults.put(cacheId, cache);
		}

		return calculate(solution, cache);
	}

	@Override
	public final double calculate(SubsetSolution<Integer> solution) throws CoreHunterException
	{
		return calculate(solution, new DistanceCachedResult(solution));
	}

	public final double calculate(SubsetSolution<Integer> solution, DistanceCachedResult cache) throws CoreHunterException
	{
		List<Integer> aIndices = cache.getAddedIndices(solution.getIndices());
		List<Integer> rIndices = cache.getRemovedIndices(solution.getIndices());
		List<Integer> cIndices = cache.getCommonIndices(solution.getIndices());

		double dist;

		if (type == DistanceMeasureType.MEAN_DISTANCE)
		{

			double total = cache.getTotal();
			double count = cache.getCount();

			for (Integer a : aIndices)
			{
				for (Integer b : cIndices)
				{
					dist = calculate(a, b);
					total += dist;
					count++;
				}
			}

			int size = aIndices.size();
			for (int i = 0; i < size - 1; i++)
			{
				for (int j = i + 1; j < size; j++)
				{
					dist = calculate(aIndices.get(i), aIndices.get(j));
					total += dist;
					count++;
				}
			}

			for (Integer a : rIndices)
			{
				for (Integer b : cIndices)
				{
					dist = calculate(a, b);
					total -= dist;
					count--;
				}
			}

			size = rIndices.size();
			for (int i = 0; i < size - 1; i++)
			{
				for (int j = i + 1; j < size; j++)
				{
					dist = calculate(rIndices.get(i), rIndices.get(j));
					total -= dist;
					count--;
				}
			}

			// recache our results under this id
			cache.setTotal(total);
			cache.setCount(count);
			cache.setIndices(solution.getIndices());

			return total / count;

		}
		else
			if (type == DistanceMeasureType.MIN_DISTANCE)
			{

				TreeMap<Double, Integer> minFreqTable = cache.getMinFreqTable();

				// add new distances

				for (Integer a : aIndices)
				{
					for (Integer b : cIndices)
					{
						dist = calculate(a, b);
						Integer freq = minFreqTable.get(dist);
						if (freq == null)
						{
							minFreqTable.put(dist, 1);
						}
						else
						{
							minFreqTable.put(dist, freq + 1);
						}
					}
				}

				int size = aIndices.size();
				for (int i = 0; i < size - 1; i++)
				{
					for (int j = i + 1; j < size; j++)
					{
						dist = calculate(aIndices.get(i), aIndices.get(j));
						Integer freq = minFreqTable.get(dist);
						if (freq == null)
						{
							minFreqTable.put(dist, 1);
						}
						else
						{
							minFreqTable.put(dist, freq + 1);
						}
					}
				}

				// remove old distances

				for (Integer a : rIndices)
				{
					for (Integer b : cIndices)
					{
						dist = calculate(a, b);
						Integer freq = minFreqTable.get(dist);
						freq--;
						if (freq == 0)
						{
							minFreqTable.remove(dist);
						}
						else
							if (freq > 0)
							{
								minFreqTable.put(dist, freq);
							}
							else
							{
								System.err.println("Error in minimum distance cacheing scheme!"
								    + "\nThis is a bug, please contact authors!");
							}
					}
				}

				size = rIndices.size();
				for (int i = 0; i < size - 1; i++)
				{
					for (int j = i + 1; j < size; j++)
					{
						dist = calculate(rIndices.get(i), rIndices.get(j));
						Integer freq = minFreqTable.get(dist);
						freq--;
						if (freq == 0)
						{
							minFreqTable.remove(dist);
						}
						else
							if (freq > 0)
							{
								minFreqTable.put(dist, freq);
							}
							else
							{
								System.err.println("Error in minimum distance cacheing scheme!"
								    + "\nThis is a bug, please contact authors!");
							}
					}
				}

				// recache results
				cache.setIndices(solution.getIndices());

				// System.out.println("Min cache size: " + minFreqTable.size());
				return minFreqTable.firstKey();

				/*
				 * //implementation without cache double minDist = Double.MAX_VALUE; int
				 * size = Indices.size(); for(int i=0; i<size-1; i++) { for(int
				 * j=i+1; j<size; j++) { dist = calculate(Indices.get(i),
				 * Indices.get(j)); if(dist<minDist){ minDist = dist; } } } return
				 * minDist;
				 */

			}
			else
			{
				// THIS SHOULD NOT HAPPEN
				System.err
				    .println("Unkown distance measure type -- this is a bug! Please contact authors.");
				System.exit(1);
				return -1;
			}

	}

	public abstract double calculate(Integer index1, Integer index2) throws UnknownIndexException;

	protected double getMemoizedValue(int id1, int id2)
	{

		int a = Math.max(id1, id2);
		int b = Math.min(id1, id2);

		/*
		 * double ret; if (a >= M.size()) { ret = MISSING_VAL; } else { ret =
		 * M.get(a).get(b).doubleValue(); } return ret;
		 */

		if (a >= M.length)
		{
			return MISSING_VAL;
		}
		else
		{
			return M[a][b];
		}
	}

	protected void setMemoizedValue(int id1, int id2, double v)
	{

		int a = Math.max(id1, id2);
		int b = Math.min(id1, id2);

		/*
		 * if (a >= M.size()) { if (a >= MAX_ACCESSION_COUNT) { return; } for(int
		 * i=M.size(); i<=a; i++) { M.add( new ArrayList<Double>(i+1) ); for(int
		 * j=0; j<=i; j++) { M.get(i).add(new Double(MISSING_VAL)); } } }
		 * M.get(a).set(b, new Double(v));
		 */
		if (a >= M.length)
		{
			return;
		}
		else
		{
			M[a][b] = v;
		}
	}

	private class DistanceCachedResult extends CachedResult<Integer>
	{
		private double		               pTotal;
		private double		               pCnt;

		private TreeMap<Double, Integer>	minFreqTable;

		// TODO why does this take SubsetSolution<IndexType> as parameter?
		public DistanceCachedResult(SubsetSolution<Integer> solution)
		{
			super();
			pTotal = 0.0;
			pCnt = 0.0;

			minFreqTable = new TreeMap<Double, Integer>();
		}

		public double getTotal()
		{
			return pTotal;
		}

		public double getCount()
		{
			return pCnt;
		}

		public TreeMap<Double, Integer> getMinFreqTable()
		{
			return minFreqTable;
		}

		public void setTotal(double total)
		{
			pTotal = total;
		}

		public void setCount(double count)
		{
			pCnt = count;
		}

	}

}
