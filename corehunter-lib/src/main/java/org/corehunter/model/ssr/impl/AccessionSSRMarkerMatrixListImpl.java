// Copyright 2012 Guy Davenport, Herman De Beukelaer
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
package org.corehunter.model.ssr.impl;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.ObjectUtils;
import org.corehunter.CoreHunterException;
import org.corehunter.model.EntityIndexedDataset;
import org.corehunter.model.UnknownEntityException;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.accession.AccessionEntityMatrix;
import org.corehunter.model.impl.EntityMatrixListImpl;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.SSRAllele;
import org.corehunter.model.ssr.SSRMarker;

public class AccessionSSRMarkerMatrixListImpl 
	extends EntityMatrixListImpl<List<Double>, Accession, SSRMarker>
	implements AccessionSSRMarkerMatrix<Integer>	
{
	private double[] externalDistances;
	
	public AccessionSSRMarkerMatrixListImpl(String name,
			EntityIndexedDataset<Integer, Accession> rowHeaders,
			EntityIndexedDataset<Integer, SSRMarker> columnHeaders,
      List<List<List<Double>>> elements)
  {
	  super(name, rowHeaders, columnHeaders, elements);
  }
	
	public AccessionSSRMarkerMatrixListImpl(String uniqueIdentifier, String name,
			EntityIndexedDataset<Integer, Accession> rowHeaders,
			EntityIndexedDataset<Integer, SSRMarker> columnHeaders,
      List<List<List<Double>>> elements, double[] externalDistances)
  {
	  super(uniqueIdentifier, name, rowHeaders, columnHeaders, elements);
	  
	  this.externalDistances = externalDistances ;
  }

	@Override
  public void validate() throws CoreHunterException
  {
	  if (externalDistances != null && externalDistances.length != getColumnHeaders().getSize())
	  	throw new CoreHunterException("Number of external distances does not match number of column headers!") ;
  }
	
	@Override
	public final int getAlleleCount(Integer index) throws UnknownIndexException
	{
		ListIterator<List<Double>> mItr = getRowElements(index).listIterator();
		ListIterator<Double> aItr = null;

		int alleleCnt = 0;
		while (mItr.hasNext())
		{
			aItr = mItr.next().listIterator();
			while (aItr.hasNext())
			{
				alleleCnt++;
				aItr.next();
			}
		}
		return alleleCnt;
	}

	@Override
	public final int getMarkerCount(Integer index) throws UnknownIndexException
	{
		return getRowElements(index).size();
	}
	
	// TODO can be optimised, unnecessary code here 
	@Override
	public final double[][] getMarkerAlleleTotals(List<Integer> indices) throws UnknownIndexException
	{
		if (indices.isEmpty())
		{
			return null;
		}

		Integer index1 = indices.get(0);
		int markerCnt = getMarkerCount(index1) ; 
		double markerAlleleTotals[][] = new double[markerCnt][];

		ListIterator<List<Double>> markerIterator = getRowElements(index1).listIterator();
		ListIterator<Double> alleleIterator;

		int i = 0;
		while (markerIterator.hasNext())
		{
			List<Double> alleles = markerIterator.next();

			markerAlleleTotals[i] = new double[alleles.size()];
			
			for (int j = 0; j < alleles.size(); j++)
			{
				markerAlleleTotals[i][j] = 0.0;
			}
			i++;
		}

		for (Integer index : indices)
		{
			markerIterator = getRowElements(index).listIterator();

			i = 0;
			while (markerIterator.hasNext())
			{
				alleleIterator = markerIterator.next().listIterator();
				int j = 0;
				while (alleleIterator.hasNext())
				{
					Double val = alleleIterator.next();
					if (val != null)
					{
						double v = val.doubleValue();
						markerAlleleTotals[i][j] += v;
					}
					j++;
				}
				i++;
			}
		}

		return markerAlleleTotals;
	}

	// TODO can be optimised, unnecessary code here 
	@Override
	public final double[] getAlleleTotals(List<Integer> indices) throws UnknownIndexException
	{
		if (indices.isEmpty())
		{
			return null;
		}

		Integer index1 = indices.get(0);
		int alleleCount = getAlleleCount(index1);
		double alleleTotals[] = new double[alleleCount];
		for (int i = 0; i < alleleCount; i++)
		{
			alleleTotals[i] = 0.0;
		}

		for (Integer index : indices)
		{
			ListIterator<List<Double>> markerIterator = getRowElements(index).listIterator();
			ListIterator<Double> alleleIterator;

			int i = 0;
			while (markerIterator.hasNext())
			{
				alleleIterator = markerIterator.next().listIterator();
				while (alleleIterator.hasNext())
				{
					Double value = alleleIterator.next();
					if (value != null)
					{
						double v = value.doubleValue();
						alleleTotals[i] += v;
					}
					i++;
				}
			}
		}

		return alleleTotals;
	}

	// TODO can be optimised, unnecessary code here 
	@Override
	public final int[] getAlleleCounts(List<Integer> indices) throws UnknownIndexException
	{
		if (indices.isEmpty())
		{
			return null;
		}

		Integer index1 = indices.get(0);
		int alleleCount = getAlleleCount(index1);
		int alleleTotals[] = new int[alleleCount];
		
		for (int i = 0; i < alleleCount; i++)
		{
			alleleTotals[i] = 0;
		}

		for (Integer index : indices)
		{
			ListIterator<List<Double>> markerIterator = getRowElements(index).listIterator();
			ListIterator<Double> alleleIterator;

			int i = 0;
			while (markerIterator.hasNext())
			{
				alleleIterator = markerIterator.next().listIterator();
				while (alleleIterator.hasNext())
				{
					Double value = alleleIterator.next();
					if (value != null)
					{
						double v = value.doubleValue();
						if (v > 0)
						{
							alleleTotals[i] += 1;
						}
					}
					i++;
				}
			}
		}

		return alleleTotals;
	}

	@Override
  public double getExternalDistance(Integer index)
  {
	  return externalDistances != null ? externalDistances[index] : 0 ;
  }
	
  public void setExternalDistance(Integer index, double distance) throws UnknownEntityException
  {
	  externalDistances[index] = distance ;
  }
  
  public double[] getExternalDistances()
  {
	  return externalDistances ;
  }

	@Override
  public Double getValue(Accession accession, SSRMarker marker, SSRAllele alelle) throws UnknownEntityException
  {
	  return getElement(accession, marker).get(marker.indexOfAllele(alelle)) ;
  }

	@Override
  public void setValue(Accession accession, SSRMarker marker, SSRAllele alelle,
      Double value) throws UnknownEntityException
  {
	  getElement(accession, marker).set(marker.indexOfAllele(alelle), value) ; 
  }

	@Override
  public List<Double> getValues(Accession accession, SSRMarker marker) throws UnknownEntityException
  {
	  return getElement(accession, marker) ; 
  }
	
	@Override
  public void setValues(Accession accession, SSRMarker marker, List<Double> values) throws UnknownEntityException
  {
	  setElement(accession, marker, values) ; 
  }
	
	@Override
  public void normalize()
  {
	  // TODO normalize() what is this for?
	  
  }

	@SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object object)
  {
		if (object instanceof AccessionEntityMatrix)
		{
			if (object instanceof AccessionSSRMarkerMatrixListImpl)
			{
				return super.equals(object) && ObjectUtils.equals(getExternalDistances(), ((AccessionSSRMarkerMatrixListImpl)object).getExternalDistances()) ;
			}
			else
			{
				boolean equals = super.equals(object) ;
				
				Iterator<Integer> iterator = getIndices().iterator() ;

				while (equals  && iterator.hasNext())
					equals = externalDistancesEquals((AccessionEntityMatrix<Integer, List<Double>, SSRMarker>)object, iterator.next()) ;
				
			  return equals;
			}
		}
		else
		{
			return super.equals(object) ;
		}
  }

	private boolean externalDistancesEquals(AccessionEntityMatrix<Integer, List<Double>, SSRMarker> matrix,
      Integer index)
  {
	  return getExternalDistance(index) == matrix.getExternalDistance(index) ;
  }
}
