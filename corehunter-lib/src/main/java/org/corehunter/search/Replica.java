//  Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer, Guy Davenport
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

package org.corehunter.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.corehunter.Accession;
import org.corehunter.AccessionCollection;
import org.corehunter.measures.PseudoMeasure;

/**
 *
 * @author hermandebeukelaer
 */
public abstract class Replica implements Runnable {

    protected String type;

    protected Neighborhood nh;
    protected PseudoMeasure pm;
    protected AccessionCollection ac;
    protected List<Accession> accessions;
    protected int sampleMin, sampleMax;
    protected int nrOfSteps;    //nr of steps to take in one run, -1 if not applied
    protected int repTime;  //amount of time (milliseconds) available for one run, -1 if not applied

    protected List<Accession> core, unselected;
    protected double score, newScore;
    protected int size, newSize;
    protected String cacheId;

    protected boolean stuck;

    protected static final Random rg = new Random();

    /**
     * Create a new Replica.
     *
     * @param String type
     * @param ac
     * @param pm
     * @param nh
     * @param nrOfSteps
     * @param repTime
     * @param sampleMin
     * @param sampleMax
     */
    public Replica(String type, AccessionCollection ac, PseudoMeasure pm, Neighborhood nh,
                   int nrOfSteps, int repTime, int sampleMin, int sampleMax){
        this.type = type;
        this.ac = ac;
        this.accessions = ac.getAccessions();
        this.pm = pm;
        this.nh = nh;
        this.nrOfSteps = nrOfSteps;
        this.repTime = repTime;
        this.sampleMin = sampleMin;
        this.sampleMax = sampleMax;
        stuck = false;
    }

    /**
     * Standard init procedure: select random core
     */
    public void init(){
        cacheId = PseudoMeasure.getUniqueId();
        // create unselected list
        unselected = new ArrayList<Accession>(accessions);
        // select an initial core
        core = new ArrayList<Accession>();
        int j;
        Accession a;
        for (int i=0; i<sampleMax; i++){
            j = rg.nextInt(unselected.size());
            a = unselected.remove(j);
            core.add(a);
        }
        score = pm.calculate(core, cacheId);
        size = core.size();
    }

    public void init(List<Accession> core){
        cacheId = PseudoMeasure.getUniqueId();
        // create unselected list
        unselected = new ArrayList<Accession>(accessions);
        unselected.removeAll(core);
        // store score and size
        score = pm.calculate(core, cacheId);
        size = core.size();
        // store core
        this.core = core;
    }

    public abstract void doSteps();
    
    public void run(){
        doSteps();
    }

    public boolean stuck(){
        return stuck;
    }

    public double getBestScore(){
        return score;
    }

    public List<Accession> getBestCore(){
        return core;
    }

    public String type(){
        return shortType();
    }

    public String shortType(){
        return type;
    }
}
