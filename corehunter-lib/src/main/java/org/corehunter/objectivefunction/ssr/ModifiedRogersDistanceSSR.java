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

import java.util.List;
import java.util.ListIterator;

import org.corehunter.CoreHunterException;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.objectivefunction.DistanceMeasureType;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.search.solution.SubsetSolution;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public final class ModifiedRogersDistanceSSR<IndexType> extends AbstractAccessionSSRDistanceMeasure<IndexType>
{

	public ModifiedRogersDistanceSSR()
	{
		this(DistanceMeasureType.MEAN_DISTANCE);
	}

	public ModifiedRogersDistanceSSR(DistanceMeasureType type)
	{
		this("MR" + type.getNameSuffix(), "Modified Rogers Distance"
		    + type.getDescriptionSuffix(), type);
	}

	public ModifiedRogersDistanceSSR(String name, String description,
	    DistanceMeasureType type)
	{
		super(name, description, type);
	}
	
	protected ModifiedRogersDistanceSSR(ModifiedRogersDistanceSSR<IndexType> objectiveFunction) throws CoreHunterException 
	{
		super(objectiveFunction) ;
	}
	
	@Override
        public final ObjectiveFunction<SubsetSolution<IndexType>> copy() throws CoreHunterException
        {
                return new ModifiedRogersDistanceSSR<IndexType>(this);
        }

	@Override
	public double calculate(IndexType index1, IndexType index2) throws UnknownIndexException 
	{
		double value = getMemoizedValue(index1, index2);
		if (value != MISSING_VAL)
		{
			return value;
		}

		ListIterator<List<Double>> m1Itr = getData().getRowElements(index1).listIterator();
		ListIterator<List<Double>> m2Itr = getData().getRowElements(index2).listIterator();

		double markerCnt = 0;
		double sumMarkerSqDiff = 0;
		while (m1Itr.hasNext() && m2Itr.hasNext())
		{
			ListIterator<Double> a1Itr = m1Itr.next().listIterator();
			ListIterator<Double> a2Itr = m2Itr.next().listIterator();

			double markerSqDiff = 0;
			while (a1Itr.hasNext() && a2Itr.hasNext())
			{
				Double Pxla = a1Itr.next();
				Double Pyla = a2Itr.next();

				if (Pxla != null && Pyla != null)
				{
					markerSqDiff += (Pxla - Pyla) * (Pxla - Pyla);
				}
			}

			sumMarkerSqDiff += markerSqDiff;
			markerCnt++;
		}

		value = 1.0 / (Math.sqrt(2.0 * markerCnt)) * Math.sqrt(sumMarkerSqDiff);
		setMemoizedValue(index1, index2, value);
		return value;
	}

}
