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

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.corehunter.CoreHunterException;
import org.corehunter.model.DataWriter;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.impl.AbstractDataFileWriter;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.SSRAllele;
import org.corehunter.model.ssr.SSRMarker;
import org.corehunter.utils.EntityUtils;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * SSRAccessionMatrixDataset writer that writes the complete matrix from memory
 * to a file. Not suitable for big datasets.
 * 
 * @author daveneti
 */
public class AccessionSSRMarkerMatrixListImplDataFileWriter extends
    AbstractDataFileWriter<AccessionSSRMarkerMatrix<Integer>> implements
    DataWriter<AccessionSSRMarkerMatrix<Integer>>
{
	private List<Integer>	indices	= null;

	public AccessionSSRMarkerMatrixListImplDataFileWriter(File file)
	{
		super(file);
	}

	public final List<Integer> getIndices()
	{
		return indices;
	}

	public final void setIndices(List<Integer> indices)
	{
		this.indices = indices;
	}

	@Override
	public void writeData(AccessionSSRMarkerMatrix<Integer> dataset)
	    throws CoreHunterException
	{
		if (indices != null)
		{
			writeData(dataset, indices);
		}
		else
		{
			writeData(dataset, dataset.getIndices());
		}
	}

	protected void writeData(AccessionSSRMarkerMatrix<Integer> dataset,
	    List<Integer> indices) throws CoreHunterException
	{
		try
		{
			CSVWriter writer = new CSVWriter(new FileWriter(getFile()), ',',
			    CSVWriter.NO_QUOTE_CHARACTER);

			String[] line = new String[indices.size() + 2];
			line[0] = "Marker";
			line[1] = "Allele";
			String[] accessionNames = getLabels(dataset.getRowHeaders().getElements(
			    indices));
			System.arraycopy(accessionNames, 0, line, 2, accessionNames.length);

			writer.writeNext(line);

			// write external distances line if specified
			if (dataset.externalDistancesSpecified())
			{
				line[0] = "DIST";
				line[1] = "";
				for (int i = 0; i < indices.size(); i++)
				{
					line[2 + i] = String.valueOf(dataset.getExternalDistance(i));
				}
				writer.writeNext(line);
			}

			Iterator<SSRMarker> markers = dataset.getColumnHeaders().getElements()
			    .iterator();
			Iterator<SSRAllele> alleles;
			Iterator<Integer> iterator;
			SSRMarker marker;
			SSRAllele allele;

			List<Double> values;
			int i;
			int a;
			int m;

			m = 0;
			while (markers.hasNext())
			{
				marker = markers.next();

				alleles = marker.getAlleles().iterator();
				line[0] = getLabel(marker);

				a = 0;

				while (alleles.hasNext())
				{
					allele = alleles.next();
					line[1] = getLabel(allele);

					iterator = indices.iterator();
					i = 2;

					while (iterator.hasNext())
					{
						values = dataset.getElement(iterator.next(), m);
						line[i] = getLabel(values.get(a));

						++i;
					}

					writer.writeNext(line);
					++a;
				}

				m++;
			}

		}
		catch (Exception e)
		{
			System.err.println("");
			System.err.println(e.getMessage());
		}
	}

	private String[] getLabels(Set<Accession> elementsAsList)
	{
		return EntityUtils.getNames(elementsAsList);
	}

	private String getLabel(Double value)
	{
		return String.valueOf(value);
	}

	private String getLabel(SSRAllele allele)
	{
		return allele.getName();
	}

	private String getLabel(SSRMarker marker)
	{
		return marker.getName();
	}
}
