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

import org.corehunter.objectivefunction.DistanceMeasureType;
import org.corehunter.objectivefunction.UnknownMeasureException;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @author Guy Davenport <g.davenport@cgiar.org>
 * @version $Rev$
 */
public final class MeasureFactorySSR
{

	private static final String[]	measureNames	= { "MR", "MRmin", "CE", "CEmin",
	    "SH", "HE", "NE", "PN", "CV", "EX"	   };

	public static SSROjectiveFunction<Integer> createMeasure(String measureName)
	    throws UnknownMeasureException
	{
		if (measureName.equals("MR"))
		{
			return new ModifiedRogersDistanceSSR();
		}
		else
			if (measureName.equals("MRmin"))
			{
				return new ModifiedRogersDistanceSSR(DistanceMeasureType.MIN_DISTANCE);
			}
			else
				if (measureName.equals("CE"))
				{
					return new CavalliSforzaEdwardsDistanceSSR();
				}
				else
					if (measureName.equals("CEmin"))
					{
						return new CavalliSforzaEdwardsDistanceSSR(DistanceMeasureType.MIN_DISTANCE);
					}
					else
						if (measureName.equals("SH"))
						{
							return new ShannonsDiversitySSR<Integer>();
						}
						else
							if (measureName.equals("HE"))
							{
								return new HeterozygousLociDiversitySSR<Integer>();
							}
							else
								if (measureName.equals("NE"))
								{
									return new NumberEffectiveAllelesSSR<Integer>();
								}
								else
									if (measureName.equals("PN"))
									{
										return new ProportionNonInformativeAllelesSSR<Integer>();
									}
									else
										if (measureName.equals("CV"))
										{
											return new CoverageSSR<Integer>();
										}
										else
											if (measureName.equals("EX"))
											{
												return new ExternalDistanceMeasureSSR<Integer>();
											}
											else
											{
												throw new UnknownMeasureException("Unknown measure '"
												    + measureName + "'");
											}
	}

	public static String[] getMeasureNames()
	{
		return measureNames;
	}
}
