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
package org.corehunter.model.ssr;

import java.util.List;

import org.corehunter.model.UnknownEntityException;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.accession.AccessionEntityMatrix;

public interface AccessionSSRMarkerMatrix<IndexType> 
	extends AccessionEntityMatrix<IndexType, List<Double>, SSRMarker>
{

	/**
	 * Gets the total number of alleles for a give accession index
	 * @param index the index of the given accession
	 * @return the total number of alleles for a give accession index
	 * @throws UnknownIndexException 
	 */
	public int getAlleleCount(IndexType index) throws UnknownIndexException;

	/**
	 * Gets the total number of markers for a give accession index
	 * @param index the index of the given accession
	 * @return the total number of markers for a give accession index
	 */
	public int getMarkerCount(IndexType index) throws UnknownIndexException;

	public double[][] getMarkerAlleleTotals(List<IndexType> indices) throws UnknownIndexException;

	public double[] getAlleleTotals(List<IndexType> indices) throws UnknownIndexException;

	public int[] getAlleleCounts(List<IndexType> indices) throws UnknownIndexException;
	
	public Double getValue(Accession accession, SSRMarker marker, SSRAllele alelle) throws UnknownEntityException ;
	
	public void setValue(Accession accession, SSRMarker marker, SSRAllele alelle, Double value) throws UnknownEntityException ;
	
	public List<Double> getValues(Accession accession, SSRMarker marker) throws UnknownEntityException ;
	
	public void setValues(Accession accession, SSRMarker marker, List<Double> values) throws UnknownEntityException;
	
	// TODO what does this do?
	public void normalize() ;
}
