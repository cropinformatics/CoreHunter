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

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.corehunter.CoreHunterException;
import org.corehunter.model.DataReader;
import org.corehunter.model.EntityIndexedDataset;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.accession.impl.AccessionImpl;
import org.corehunter.model.impl.AbstractDataFileReader;
import org.corehunter.model.impl.OrderedEntityDatasetListImpl;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.SSRAllele;
import org.corehunter.model.ssr.SSRMarker;

/**
 * SSRAccessionMatrixDataset reader that reads the complete matrix into memory before
 * creating the Dataset. Accessions and Marker are indexed from 0 to n-1.
 * Not suitable for big datasets.
 *  
 * @author daveneti
 *
 */
public class AccessionSSRMarkerMatrixListImplDataFileReader
        extends AbstractDataFileReader<AccessionSSRMarkerMatrix<Integer>>
        implements DataReader<AccessionSSRMarkerMatrix<Integer>> {

    private static final String ACCESSION_DATASET_NAME_PREFIX = "Accessions for ";
    private static final String MARKER_DATASET_NAME_PREFIX = "Markers for ";
    private char delimiter;

    public AccessionSSRMarkerMatrixListImplDataFileReader(File file) {
        super(file);
        setDelimiter(TAB_DELIMITER);
    }

    public AccessionSSRMarkerMatrixListImplDataFileReader(String dataName, File file) {
        super(dataName, file);
        setDelimiter(TAB_DELIMITER);
    }

    public AccessionSSRMarkerMatrixListImplDataFileReader(File file, char delimiter) {
        super(file);
        setDelimiter(delimiter);
    }

    public AccessionSSRMarkerMatrixListImplDataFileReader(String dataName, File file, char delimiter) {
        super(dataName, file);
        setDelimiter(delimiter);
    }

    public final char getDelimiter() {
        return delimiter;
    }

    public final void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public AccessionSSRMarkerMatrix<Integer> readData() throws CoreHunterException {
        AccessionSSRMarkerMatrix<Integer> dataset = null;
        List<Accession> accessions = new ArrayList<Accession>();
        List<SSRMarker> markers = new ArrayList<SSRMarker>();

        String nextLine[];

        try {
            // TODO allow for other delimiters
            CSVReader reader = new CSVReader(new FileReader(getFile()), delimiter);

            List lines = reader.readAll();
            reader.close();

            Iterator iterator = lines.iterator();

            if (iterator.hasNext()) {
                nextLine = (String[]) iterator.next();
                for (int i = 2; i < nextLine.length; i++) {
                    accessions.add(createAccession(nextLine[i], i - 2));
                }
            }

            int lineNumber = 1;
            int markerIndex = 0;
            int alleleIndex = 0;
            // Normal marker/allele line
            String markerName = null;
            String alleleName = null;
            SSRMarker marker = null;

            while (iterator.hasNext()) {
                nextLine = (String[]) iterator.next();
                lineNumber++;

                if (nextLine.length < 2) {
                    throw new CoreHunterException("Dataset is not properly formatted on line "
                            + lineNumber + " Please refer to the CoreHunter manual.  "
                            + "There should be a marker name and allele name separated by a " + delimiter
                            + "followed by values for each accession also separated by a " + delimiter + ". '" + nextLine[0] + "'");
                }

                // Ignore (possible) external distances line at this point
                if (!(nextLine[0].equalsIgnoreCase("DIST"))) {
                    if (!nextLine[0].equals(markerName)) {
                        markerName = nextLine[0];
                        marker = createSSRMarker(markerName, markerIndex);
                        markers.add(marker);
                        alleleIndex = 0;
                        ++markerIndex;
                    }

                    alleleName = nextLine[1];
                    marker.addAllele(createSSRAllele(alleleName, marker, alleleIndex));
                    ++alleleIndex;
                }
            }

            if (accessions.size() < 2) {
                throw new CoreHunterException("Dataset must contain at least 2 accessions");
            }

            if (markers.size() < 1) {
                throw new CoreHunterException("Dataset must contain at least 1 marker/allele");
            }

            List<List<List<Double>>> elements = new ArrayList<List<List<Double>>>(accessions.size());
            List<Double> externalDistances = null;

            for (int i = 0; i < accessions.size(); ++i) {
                elements.add(new ArrayList<List<Double>>(markers.size()));
            }

            markerName = null;
            markerIndex = -1;

            iterator = lines.iterator();
            iterator.next();

            // add the allele values for each genotype
            while (iterator.hasNext()) {
                nextLine = (String[]) iterator.next();

                // Check for external distances line
                if (nextLine[0].equalsIgnoreCase("DIST")) {
                    // ext. dist. line
                    externalDistances = new ArrayList<Double>(accessions.size());
                    for (int i = 2; i < nextLine.length; i++) {
                        Accession accession = accessions.get(i - 2);
                        try {
                            externalDistances.add(new Double(nextLine[i]));
                        } catch (NumberFormatException numberFormatException) {
                            throw new CoreHunterException("Invalid external distance value for accession '" + accession, numberFormatException);
                        }
                    }
                } else {
                    List<Double> markerValues;

                    if (!nextLine[0].equals(markerName)) {
                        ++markerIndex;
                        marker = markers.get(markerIndex);
                        markerName = marker.getName();

                        for (int i = 2; i < nextLine.length; i++) {
                            markerValues = new ArrayList<Double>(marker.getAlleles().size());
                            elements.get(i - 2).add(markerValues);

                            if (nextLine[i].equals("")) {
                                markerValues.add(null);
                            } else {
                                try {
                                    markerValues.add(new Double(nextLine[i]));
                                } catch (NumberFormatException numberFormatException) {
                                    Accession accession = accessions.get(i - 2);

                                    throw new CoreHunterException("Invalid value for accession '" + accession + "' marker '" + markerName + "' allele '" + alleleName
                                            + "'", numberFormatException);
                                }
                            }
                        }
                    } else {
                        for (int i = 2; i < nextLine.length; i++) {
                            markerValues = elements.get(i - 2).get(markerIndex);

                            if (nextLine[i].equals("")) {
                                markerValues.add(null);
                            } else {
                                try {
                                    markerValues.add(new Double(nextLine[i]));
                                } catch (NumberFormatException numberFormatException) {
                                    Accession accession = accessions.get(i - 2);

                                    throw new CoreHunterException("Invalid value for accession '" + accession + "' marker '" + markerName + "' allele '" + alleleName
                                            + "'", numberFormatException);
                                }

                            }
                        }
                    }


                }
            }

            // create the SSRDataset object
            double[] extDistArray;
            if(externalDistances != null){
                 extDistArray = new double[externalDistances.size()];
                for(int ext=0; ext<externalDistances.size(); ext++){
                    extDistArray[ext] = externalDistances.get(ext);
                }
            } else {
                extDistArray = null;
            }
            dataset = createSSRAccessionMatrixDataset(createDataName(), createAccessionDataset(createAccessionDatasetName(), accessions), createMarkerDataset(createMarkerDatasetName(), markers), elements, extDistArray);

        } catch (IOException e) {
            throw new CoreHunterException(e);
        }

        return dataset;
    }

    protected String createDataName() {
        return getDataName() != null ? getDataName() : getFile().getName();
    }

    protected String createAccessionDatasetName() {
        return ACCESSION_DATASET_NAME_PREFIX + createDataName();
    }

    protected String createMarkerDatasetName() {
        return MARKER_DATASET_NAME_PREFIX + createDataName();
    }

    protected Accession createAccession(String accessionName, int index) {
        return new AccessionImpl(accessionName);
    }

    protected SSRMarker createSSRMarker(String markerName, int index) {
        return new SSRMarkerImpl(markerName);
    }

    protected SSRAllele createSSRAllele(String alleleName, SSRMarker marker, int index) {
        return new SSRAlleleImpl(alleleName, marker);
    }

    protected AccessionSSRMarkerMatrix<Integer> createSSRAccessionMatrixDataset(
            String name,
            EntityIndexedDataset<Integer, Accession> accessionDataset,
            EntityIndexedDataset<Integer, SSRMarker> markerDataset,
            List<List<List<Double>>> elements,
            double[] externalDistances) {
        return new AccessionSSRMarkerMatrixListImpl(name, name, accessionDataset, markerDataset, elements, externalDistances);
    }

    protected EntityIndexedDataset<Integer, Accession> createAccessionDataset(String name, List<Accession> accessions) {
        return new OrderedEntityDatasetListImpl<Accession>(name, accessions);
    }

    protected EntityIndexedDataset<Integer, SSRMarker> createMarkerDataset(String name, List<SSRMarker> markers) {
        return new OrderedEntityDatasetListImpl<SSRMarker>(name, markers);
    }
}
