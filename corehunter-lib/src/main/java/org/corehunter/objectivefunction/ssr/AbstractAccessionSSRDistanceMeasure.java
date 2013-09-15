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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.corehunter.CoreHunterException;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.objectivefunction.DistanceMeasureType;
import org.corehunter.objectivefunction.impl.AbstractSubsetObjectiveFunction;
import org.corehunter.objectivefunction.impl.CachedResult;
import org.corehunter.search.solution.SubsetSolution;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
// TODO separate into two sub-classes; one for MEAN_DISTANCE and one for MIN_DISTANCE
public abstract class AbstractAccessionSSRDistanceMeasure<IndexType> extends 
	AbstractSubsetObjectiveFunction<IndexType, AccessionSSRMarkerMatrix<IndexType>> implements SSROjectiveFunction<IndexType>
{
	private Map<IndexType, Map<IndexType, Double>> M; // pairwise distance cache            -- NOT synchronized
	private DistanceCachedResult        cachedResult; // cached solution distance score     -- NOT synchronized

	protected static final double	MISSING_VAL	= -1.0;

	protected DistanceMeasureType	type;	            // minimum/mean distance
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
		this.type = type;
	}

	protected AbstractAccessionSSRDistanceMeasure(
			AbstractAccessionSSRDistanceMeasure<IndexType> objectiveFuncton) throws CoreHunterException
  {
                super(objectiveFuncton);
		setType(objectiveFuncton.getType()) ;
  }
        
        @Override
        public void flushCachedResults(){
            cachedResult = new DistanceCachedResult();
            M = new HashMap<IndexType, Map<IndexType, Double>>();
        }

        /**
         * Calculate distance measure score for given integer subset solution. If
         * no solution is given (null), score is computed for entire dataset.
         * 
         * @param solution
         * @return
         * @throws CoreHunterException 
         */
	@Override
	public final double calculate(SubsetSolution<IndexType> solution) throws CoreHunterException
	{
                
                Collection<IndexType> indices;
                // if solution is null, compute for entire dataset
                if(solution == null){
                    indices = getData().getIndices();
                } else {
                    indices = solution.getSubsetIndices();
                }
		
		List<IndexType> aIndices = cachedResult.getAddedIndices(indices);
		List<IndexType> rIndices = cachedResult.getRemovedIndices(indices);
		List<IndexType> cIndices = cachedResult.getCommonIndices(indices);

		double dist;

		if (type == DistanceMeasureType.MEAN_DISTANCE)
		{

			double total = cachedResult.getTotal();
			double count = cachedResult.getCount();

			for (IndexType a : aIndices)
			{
				for (IndexType b : cIndices)
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

			for (IndexType a : rIndices)
			{
				for (IndexType b : cIndices)
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
			cachedResult.setTotal(total);
			cachedResult.setCount(count);
			cachedResult.setIndices(indices);

                        // return 0.0 if no distances left (< 2 items)
                        if(count == 0){
                            return 0.0;
                        } else {
                            return total / count;
                        }

		}
		else
			if (type == DistanceMeasureType.MIN_DISTANCE)
			{

				TreeMap<Double, Integer> minFreqTable = cachedResult.getMinFreqTable();

				// add new distances

				for (IndexType a : aIndices)
				{
					for (IndexType b : cIndices)
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

				for (IndexType a : rIndices)
				{
					for (IndexType b : cIndices)
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
				cachedResult.setIndices(indices);

				// System.out.println("Min cache size: " + minFreqTable.size());
                                if(!minFreqTable.isEmpty()){
                                    return minFreqTable.firstKey();
                                } else {
                                    return 0.0;
                                }

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

	public abstract double calculate(IndexType index1, IndexType index2) throws UnknownIndexException;
	
	public final DistanceMeasureType getType()
	{
		return type;
	}

	public final void setType(DistanceMeasureType type)
	{
		this.type = type;
	}

	protected double getMemoizedValue(IndexType id1, IndexType id2)
	{
                if(M.containsKey(id1) && M.get(id1).containsKey(id2)){
                    return M.get(id1).get(id2);
                } else if(M.containsKey(id2) && M.get(id2).containsKey(id1)){
                    return M.get(id2).get(id1);
                } else {
                    return MISSING_VAL;
                }
	}

	protected void setMemoizedValue(IndexType id1, IndexType id2, double v)
	{
                if(getMemoizedValue(id1, id2) != MISSING_VAL){
                    if(!M.containsKey(id1)){
                        M.put(id1, new HashMap<IndexType, Double>());
                    }
                    M.get(id1).put(id2, v);
                }
	}

	private class DistanceCachedResult extends CachedResult<IndexType>
	{
		private double		               pTotal;
		private double		               pCnt;

		private TreeMap<Double, Integer>	minFreqTable;

		public DistanceCachedResult()
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
