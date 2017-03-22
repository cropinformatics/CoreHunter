package org.corehunter.measures;

import org.corehunter.Accession;

import java.util.HashMap;
import java.util.Map;

public class NearestEntryEvaluation {

    // maps accessions to closest other entries and the corresponding distance
    private final Map<Accession, NearestEntry> nearestEntryMap;
    // sum of distances from items to respective closest entries
    private double minDistSum;
    // value when no distances have been registered
    private final double emptyValue;

    public NearestEntryEvaluation(double emptyValue) {
        nearestEntryMap = new HashMap<Accession, NearestEntry>();
        minDistSum = 0.0;
        this.emptyValue = emptyValue;
    }

    /**
     * Deep copy constructor
     *
     * @param toCopy evaluation to copy
     */
    public NearestEntryEvaluation(NearestEntryEvaluation toCopy){
        nearestEntryMap = new HashMap<Accession, NearestEntry>(toCopy.nearestEntryMap);
        minDistSum = toCopy.minDistSum;
        emptyValue = toCopy.emptyValue;
    }

    /**
     * Register the closest entry of a given item.
     *
     * @param acc accession
     * @param nearestEntry closest other selected accession and corresponding distance
     */
    public void add(Accession acc, NearestEntry nearestEntry){
        // update minimum distance sum
        minDistSum += nearestEntry.getDistance();
        // update metadata
        nearestEntryMap.put(acc, nearestEntry);
    }

    /**
     * Remove item and the registered closest entry (if any).
     *
     * @param acc accession
     * @return <code>true</code> if the item had been registered and is now removed
     */
    public boolean remove(Accession acc){
        if(nearestEntryMap.containsKey(acc)){
            // update minimum distance sum
            minDistSum -= nearestEntryMap.get(acc).getDistance();
            // update metadata
            nearestEntryMap.remove(acc);
            return true;
        }
        return false;
    }

    /**
     * Update the closest entry of a previously registered item.
     *
     * @param acc accession
     * @param nearestEntry closest other selected accession and corresponding distance
     * @return <code>true</code> if the item had been registered and is now updated
     */
    public boolean update(Accession acc, NearestEntry nearestEntry){
        if(nearestEntryMap.containsKey(acc)){
            // update minimum distance sum
            minDistSum -= nearestEntryMap.get(acc).getDistance();
            minDistSum += nearestEntry.getDistance();
            // update metadata
            nearestEntryMap.put(acc, nearestEntry);
            return true;
        }
        return false;
    }

    /**
     * Get the closest other selected accession and corresponding distance for a given accession.
     *
     * @param acc accession
     * @return closest other selected accession and corresponding distance;
     *         <code>null</code> if no nearest entry has been registered
     */
    public NearestEntry getClosest(Accession acc){
        return nearestEntryMap.get(acc);
    }

    /**
     * Compute average distance from each registered item to closest selected item.
     *
     * @return average distance; if no distances have been registered the value
     *         specified at construction is returned
     */
    public double getValue() {
        int n = nearestEntryMap.size();
        return n > 0 ? minDistSum/n : emptyValue;
    }

}
