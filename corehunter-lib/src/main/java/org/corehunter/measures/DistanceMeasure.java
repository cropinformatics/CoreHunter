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

package org.corehunter.measures;

import org.corehunter.Accession;

import java.util.*;

/**
 * <<Class summary>>
 *
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public abstract class DistanceMeasure extends Measure {
    //private static final int DEFAULT_ACCESSION_COUNT = 512;
    //private static final int MAX_ACCESSION_COUNT = 8192;
    //private List<List<Double>> M;
    private double[][] M;
    private Map<String,DistanceCachedResult> cachedResults;

    protected static final double MISSING_VAL = -1.0;

    protected DistanceMeasureType type; // states whether mean or min distance should be computed

    public DistanceMeasure(int accessionCount) {
	this("UM", "Unknown Measure", accessionCount, DistanceMeasureType.MEAN_DISTANCE);
    }
    
    /*public DistanceMeasure(String name, String description) {
	this(name, description, DEFAULT_ACCESSION_COUNT);
    }*/

    public DistanceMeasure(String name, String description, int accessionCount, DistanceMeasureType type) {
	super(name, description);

	//M = new ArrayList<List<Double>>(accessionCount);
	/*for(int i=0; i<accessionCount; i++) {
	    M.add(new ArrayList<Double>(i+1));
	    for(int j=0; j<=i; j++) {
		M.get(i).add(new Double(MISSING_VAL));
	    }
	}*/
        M = new double[accessionCount][];
        for(int i=0; i<accessionCount; i++) {
	    M[i] = new double[i+1];
	    for(int j=0; j<=i; j++) {
		M[i][j] = MISSING_VAL;
	    }
	}

	cachedResults = Collections.synchronizedMap(new HashMap<String,DistanceCachedResult>());

        this.type = type;
    }

    public double calculate(List<Accession> accessions, String id) {
	DistanceCachedResult cache = cachedResults.get(id);

	if (cache == null) {
	    cache = this.new DistanceCachedResult(accessions);
	    cachedResults.put(id, cache);
	}
	
	return calculate(accessions, cache);
    }

    public double calculate(List<Accession> accessions) {
	return calculate(accessions, new DistanceCachedResult(accessions));
    }

    public double calculate(List<Accession> accessions, DistanceCachedResult cache) {
        
        double dist;
        
        if (type == DistanceMeasureType.MEAN_DISTANCE){

            List<Accession> aAccessions = cache.getAddedAccessions(accessions);
            List<Accession> rAccessions = cache.getRemovedAccessions(accessions);
            List<Accession> cAccessions = cache.getCommonAccessions(accessions);

            double total = cache.getTotal();
            double count = cache.getCount();

            for(Accession a : aAccessions) {
                for(Accession b : cAccessions) {
                    dist = calculate(a,b);
                    total += dist;
                    count++;
                }
            }

            int size = aAccessions.size();
            for(int i=0; i<size-1; i++) {
                for(int j=i+1; j<size; j++) {
                    dist = calculate(aAccessions.get(i), aAccessions.get(j));
                    total += dist;
                    count++;
                }
            }

            for(Accession a : rAccessions) {
                for(Accession b : cAccessions) {
                    dist = calculate(a,b);
                    total -= dist;
                    count--;
                }
            }           

            size = rAccessions.size();
            for(int i=0; i<size-1; i++) {
                for(int j=i+1; j<size; j++) {
                    dist = calculate(rAccessions.get(i), rAccessions.get(j));
                    total -= dist;
                    count--;
                }
            }

            // recache our results under this id
            cache.setTotal(total);
            cache.setCount(count);
            cache.setAccessions(accessions);

            return total/count;

        } else if (type == DistanceMeasureType.MIN_DISTANCE) {

            List<Accession> aAccessions = cache.getAddedAccessions(accessions);
            List<Accession> rAccessions = cache.getRemovedAccessions(accessions);
            List<Accession> cAccessions = cache.getCommonAccessions(accessions);

            TreeMap<Double, Integer> minFreqTable = cache.getMinFreqTable();

            // add new distances

            for (Accession a : aAccessions) {
                for (Accession c : cAccessions) {
                    dist = calculate(a, c);
                    Integer freq = minFreqTable.get(dist);
                    if (freq == null) {
                        minFreqTable.put(dist, 1);
                    } else {
                        minFreqTable.put(dist, freq + 1);
                    }
                }
            }

            int size = aAccessions.size();
            for (int i = 0; i < size - 1; i++) {
                for (int j = i + 1; j < size; j++) {
                    dist = calculate(aAccessions.get(i), aAccessions.get(j));
                    Integer freq = minFreqTable.get(dist);
                    if (freq == null) {
                        minFreqTable.put(dist, 1);
                    } else {
                        minFreqTable.put(dist, freq + 1);
                    }
                }
            }

            // remove old distances

            for (Accession r : rAccessions) {
                for (Accession c : cAccessions) {
                    dist = calculate(r, c);
                    Integer freq = minFreqTable.get(dist);
                    freq--;
                    if (freq == 0) {
                        minFreqTable.remove(dist);
                    } else if (freq > 0) {
                        minFreqTable.put(dist, freq);
                    } else {
                        System.err.println("Error in minimum distance caching scheme!"
                                + "\nThis is a bug, please contact authors!");
                    }
                }
            }

            size = rAccessions.size();
            for (int i = 0; i < size - 1; i++) {
                for (int j = i + 1; j < size; j++) {
                    dist = calculate(rAccessions.get(i), rAccessions.get(j));
                    Integer freq = minFreqTable.get(dist);
                    freq--;
                    if (freq == 0) {
                        minFreqTable.remove(dist);
                    } else if (freq > 0) {
                        minFreqTable.put(dist, freq);
                    } else {
                        System.err.println("Error in minimum distance caching scheme!"
                                + "\nThis is a bug, please contact authors!");
                    }
                }
            }

            // recache results
            cache.setAccessions(accessions);

            //System.out.println("Min cache size: " + minFreqTable.size());
            return minFreqTable.firstKey();

            /*
            //implementation without cache
            double minDist = Double.MAX_VALUE;
            int size = accessions.size();
            for(int i=0; i<size-1; i++) {
                for(int j=i+1; j<size; j++) {
                    dist = calculate(accessions.get(i), accessions.get(j));
                    if(dist<minDist){
                        minDist = dist;
                    }
                }
            }
            return minDist;*/

        } else if (type == DistanceMeasureType.ENE_DISTANCE) {

            // entry-to-nearest-entry distance

            // old code for full evaluation

//            double ene = 0.0;
//            for(int i = 0; i < accessions.size(); i++){
//                double min = Double.MAX_VALUE;
//                for(int j = 0; j < accessions.size(); j++){
//                    dist = calculate(accessions.get(i), accessions.get(j));
//                    if(j != i && dist < min){
//                        min = dist;
//                    }
//                }
//                ene += min;
//            }
//            ene /= accessions.size();
//
//            return ene;

            // code using cache for efficient delta evaluation

            // find added and removed accessions (sets for efficiency!)
            Set<Accession> aAccessions = new HashSet<Accession>(accessions);
            for(Accession prev : cache.getAccessions()){
                aAccessions.remove(prev);
            }
            Set<Accession> rAccessions = new HashSet<Accession>(cache.getAccessions());
            for(Accession cur : accessions){
                rAccessions.remove(cur);
            }
//            if(aAccessions.size() != 1 || rAccessions.size() != 1) {
//            System.out.println("---");
//            System.out.println(aAccessions.size() + ", " + rAccessions.size());
//            System.out.println("Accessions: " + accessions);
//            System.out.println("Cache: " + cache.getAccessions());
//            System.out.println("Added: " + aAccessions);
//            System.out.println("Removed: " + rAccessions);
//            System.out.println("---");
//            }

            // retrieve cached evaluation
            NearestEntryEvaluation eval = cache.getEntryToNearestEntry();

            // copy to initialize new evaluation
            NearestEntryEvaluation newEval = new NearestEntryEvaluation(eval);

            // discard contribution of removed items
            for(Accession item : rAccessions){
                newEval.remove(item);
            }

            // update closest items in new selection
            for(Accession item : accessions){
                NearestEntry curClosest = newEval.getClosest(item);
                if(curClosest == null){
                    // case 1: previously unselected or no closest item set (less than two items were selected);
                    //         search for closest item in new selection
                    NearestEntry newClosest = findClosest(item, accessions);
                    // register, if any
                    if(newClosest != null){
                        newEval.add(item, newClosest);
                    }
                } else {
                    // case 2: current closest item needs to be updated
                    if(rAccessions.contains(curClosest.getAccession())){
                        // case 2A: current closest item removed, rescan entire new selection
                        NearestEntry newClosest = findClosest(item, accessions);
                        // update, if any
                        if(newClosest != null){
                            newEval.update(item, newClosest);
                        } else {
                            // no closest item left (new selection consists of single item);
                            // discard contribution
                            newEval.remove(item);
                        }
                    } else {
                        // case 2B: current closest item retained; only check if any newly
                        //          added item is closer
                        NearestEntry closestAddedItem = findClosest(item, aAccessions);
                        if(closestAddedItem != null && closestAddedItem.getDistance() < curClosest.getDistance()){
                            // update closest item
                            newEval.update(item, closestAddedItem);
                        }
                    }
                }
            }

            // recache results
            cache.setAccessions(accessions);
            cache.setEntryToNearestEntry(newEval);

            return newEval.getValue();

        } else {
            // THIS SHOULD NOT HAPPEN
            System.err.println("Unknown distance measure type -- this is a bug! Please contact authors.");
            System.exit(1);
            return -1;
        }
        
    }

    /**
     * Find the item in the given group that is closest to and different from the given item.
     *
     * @param acc given accession
     * @param group other accessions
     * @return closest other accession and corresponding distance;
     *         <code>null</code> if the group does not contain any items other than the given item
     */
    private NearestEntry findClosest(Accession acc, Collection<Accession> group){
        double dist;
        Double minDist = Double.POSITIVE_INFINITY;
        Accession closest = null;
        for(Accession other : group){
            if(other != acc){
                dist = calculate(acc, other);
                if(dist < minDist){
                    minDist = dist;
                    closest = other;
                }
            }
        }
        return closest != null ? new NearestEntry(closest, minDist) : null;
    }
	
    public abstract double calculate(Accession a1, Accession a2);

    protected double getMemoizedValue(int id1, int id2) {

	int a = Math.max(id1, id2);
	int b = Math.min(id1, id2);

        /*double ret;
	if (a >= M.size()) { 
	    ret = MISSING_VAL;
	} else {
	    ret = M.get(a).get(b).doubleValue();
	}

        return ret;*/

        if(a >= M.length){
            return MISSING_VAL;
        } else {
            return M[a][b];
        }
    }

    protected void setMemoizedValue(int id1, int id2, double v) {

	int a = Math.max(id1, id2);
	int b = Math.min(id1, id2);

        /*if (a >= M.size()) {
	    if (a >= MAX_ACCESSION_COUNT) {
		return;
	    }

	    for(int i=M.size(); i<=a; i++) {
		M.add( new ArrayList<Double>(i+1) );
		for(int j=0; j<=i; j++) {
		    M.get(i).add(new Double(MISSING_VAL));
		}
	    }
	} 
	
	M.get(a).set(b, new Double(v));*/
        if(a >= M.length){
            return;
        } else {
            M[a][b] = v;
        }
    }

    private class DistanceCachedResult extends CachedResult {
	    private double pTotal;
	    private double pCnt;

        private TreeMap<Double, Integer> minFreqTable;
        private NearestEntryEvaluation entryToNearestEntry;

        public DistanceCachedResult(List<Accession> accessions) {
            super();
            pTotal = 0.0;
            pCnt = 0.0;
            minFreqTable = new TreeMap<Double, Integer>();
            entryToNearestEntry = new NearestEntryEvaluation(0.0);
        }

        public double getTotal() {
            return pTotal;
        }

        public double getCount() {
            return pCnt;
        }

        public TreeMap<Double, Integer> getMinFreqTable(){
                return minFreqTable;
            }

        public NearestEntryEvaluation getEntryToNearestEntry(){
            return entryToNearestEntry;
        }

        public void setEntryToNearestEntry(NearestEntryEvaluation ene){
            entryToNearestEntry = ene;
        }

        public void setTotal(double total) {
            pTotal = total;
        }

        public void setCount(double count) {
	    pCnt = count;
	}

    }
    
}