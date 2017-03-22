//  Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.corehunter;

import java.util.List;
import java.util.ListIterator;

/**
 * <<Class summary>>
 *
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 */
public /*final*/ class Accession implements Comparable<Accession> {
    protected static final int UNKNOWN_ID = -1;
    protected static int nextAccessionId = 0;

    protected String name;
    protected int id;
    protected Integer dartIndex;
    protected Integer ssrIndex;
    
    protected List<List<Double>> ssrValues;

    protected Double extDistance;
	
    /**
     * 
     */
    public Accession(String name) {
	this.name = name;
	id = nextAccessionId++;	
    }

    public void setName(String name) {
	this.name = name;
    }
	
    public String getName() {
	return name;
    }

    public int getId() {
	return id;
    }

    public static void reset() {
	nextAccessionId = 0;
    }
	
    public void bindTraitValues(AccessionDataset ds) {
	if (ds instanceof SSRDataset) {
	    bindSSRValues((SSRDataset)ds);
	}
    }
	
    public void bindSSRValues(SSRDataset ds) {
	ssrIndex = ds.getAccessionIndex(name);
	ssrValues = ds.getValues(name);
        extDistance = ds.getExtDistance(name);
    }
	
    public List<List<Double>> getSSRValues() {
	return ssrValues;
    }

    public Double getExtDistance(){
        return extDistance;
    }

    public int numSSRAlleles() {
	ListIterator<List<Double>> mItr = getSSRValues().listIterator();
	ListIterator<Double> aItr = null;
	
	int alleleCnt = 0;
	while (mItr.hasNext()) {
	    aItr = mItr.next().listIterator();
	    while (aItr.hasNext()) {
		alleleCnt++;
		aItr.next();
	    }
	}
	return alleleCnt;
    }

    public int numSSRMarkers() {
	return getSSRValues().size();
    }

    public int compareTo(Accession a) {
        return name.compareTo(a.name);
    }

    public static double[][] getMarkerAlleleTotals(List<Accession> accessions) {
	if (accessions.isEmpty()) {
            return null;
        }

	Accession a1 = accessions.get(0);
	int markerCnt = a1.numSSRMarkers();
	double markerAlleleTotals[][] = new double[markerCnt][];

	ListIterator<List<Double>> mItr = a1.getSSRValues().listIterator();
	ListIterator<Double> aItr;

	int i = 0;
	while (mItr.hasNext()) {
	    List<Double> alleles = mItr.next();

	    markerAlleleTotals[i] = new double[alleles.size()];
	    for(int j=0; j<alleles.size(); j++) {
		markerAlleleTotals[i][j] = 0.0;
	    }
	    i++;
	}
	
	for(Accession a : accessions) {
	    mItr = a.getSSRValues().listIterator();

	    i = 0;
	    while (mItr.hasNext()) {
		aItr = mItr.next().listIterator();
		int j = 0;
		while (aItr.hasNext()) {
		    Double val = aItr.next();
		    if(val != null) {
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

    public static double[] getAlleleTotals(List<Accession> accessions) {
	if (accessions.isEmpty()){
            return null;
        }

	Accession a1 = accessions.get(0);
	int alleleCnt = a1.numSSRAlleles();
	double alleleTotals[] = new double[alleleCnt];
	for(int i=0; i<alleleCnt; i++) {
	    alleleTotals[i] = 0.0;
	}
		
	for(Accession a : accessions) {
	    ListIterator<List<Double>> mItr = a.getSSRValues().listIterator();
	    ListIterator<Double> aItr;

	    int i = 0;
	    while (mItr.hasNext()) {
		aItr = mItr.next().listIterator();
		while (aItr.hasNext()) {
		    Double val = aItr.next();
		    if(val != null) {
			double v = val.doubleValue();
			alleleTotals[i] += v;
		    }
		    i++;
		}
	    }
	}
	
	return alleleTotals;
    }

    public static int[] getAlleleCounts(List<Accession> accessions) {
	if (accessions.isEmpty()){
            return null;
        }

	Accession a1 = accessions.get(0);
	int alleleCnt = a1.numSSRAlleles();
	int alleleTotals[] = new int[alleleCnt];
	for(int i=0; i<alleleCnt; i++) {
	    alleleTotals[i] = 0;
	}
		
	for(Accession a : accessions) {
	    ListIterator<List<Double>> mItr = a.getSSRValues().listIterator();
	    ListIterator<Double> aItr;

	    int i = 0;
	    while (mItr.hasNext()) {
		aItr = mItr.next().listIterator();
		while (aItr.hasNext()) {
		    Double val = aItr.next();
		    if(val != null) {
			double v = val.doubleValue();
			if (v > 0) {
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
	public String toString() {
		return id + "";
	}
}

