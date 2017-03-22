package org.corehunter.measures;

import org.corehunter.Accession;

public class NearestEntry {

    private final Accession acc;
    private final double distance;

    public NearestEntry(Accession acc, double distance) {
        this.acc = acc;
        this.distance = distance;
    }

    public Accession getAccession() {
        return acc;
    }

    public double getDistance() {
        return distance;
    }

}
