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

package org.corehunter.search;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.corehunter.Accession;
import org.corehunter.AccessionCollection;
import org.corehunter.measures.PseudoMeasure;

/**
 *
 * @author hermandebeukelaer
 */
public class SimpleMonteCarloReplica extends Replica {
    private final static double K_b  = 7.213475e-7;

    private double T;

    private List<Accession> bestCore;
    private double bestScore;

    private int accepts;
    private int rejects;
    private int improvements;
    private int totSteps;

    public SimpleMonteCarloReplica(AccessionCollection ac, PseudoMeasure pm, Neighborhood nh,
                       int mcSteps, int repTime, int minSize, int maxSize, double T) {
        super("MC", ac, pm, nh, mcSteps, repTime, minSize, maxSize);

        this.T = T;
        accepts = rejects = improvements = totSteps = 0;
    }

    @Override
    public String type(){
        DecimalFormat df = new DecimalFormat("#.##");
        return type + " (T = " + df.format(T) + ")";
    }

    @Override
    public void init(){
        super.init();
        bestCore = new ArrayList<Accession>();
        bestCore.addAll(core);
        bestScore = score;
    }

    @Override
    public void init(List<Accession> core){
        super.init(core);
        bestCore = new ArrayList<Accession>();
        bestCore.addAll(core);
        bestScore = score;
    }

    @Override
    public void run(){
        doSteps();
    }

    public void doSteps() {
        stuck = true;
        double etime = System.currentTimeMillis() + repTime;
        int i=0;

        while((  (nrOfSteps > 0 && i < nrOfSteps)
                  || (repTime > 0 && System.currentTimeMillis() < etime) )){

            nh.genRandomNeighbor(core, unselected);
            newScore = pm.calculate(core, cacheId);
            newSize = core.size();

            double deltaScore = newScore - score;

            if (deltaScore > 0) {
                // accept new core!
                improvements++;
                score = newScore;
                size = newSize;
            } else {
                double deltaSize = newSize - size;

                if (deltaSize > 0) {
                    // new core is bigger than old core and has no better
                    // score --> reject new core, stick with old core
                    rejects++;
                    nh.undoLastPerturbation(core, unselected);
                } else {
                    // new core is not bigger, but has lower score
                    // accept or reject new core based on temperature
                    double P = Math.exp(deltaScore/(T*K_b));
                    double Q = rg.nextDouble();
                    if ( Q > P ) {
                        rejects++;
                        nh.undoLastPerturbation(core, unselected);
                    } else {
                        // accept new core!
                        // reassign newCore to the old core, which can now be overwritten
                        accepts++;
                        score = newScore;
                        size = newSize;
                    }
                }
            }

            // check if new best core was found
            if (score > bestScore || (score == bestScore && size < bestCore.size())) {
                stuck = false;
                bestScore = score;
                bestCore.clear();
                bestCore.addAll(core);
            }

            totSteps++;
            i++;
        }
    }

    @Override
    public List<Accession> getBestCore(){
        return bestCore;
    }

    @Override
    public double getBestScore(){
        return bestScore;
    }

    public void printStats() {
        System.out.println("steps: " + totSteps + "\timprovements: " + improvements + "\taccepts: " +
                           accepts + "\trejects: " + rejects);
    }

    public double getScore() {
        return score;
    }

    public double getTemperature() {
        return T;
    }

    public void setTemperature(double temp) {
        // temperature cannot be negative
        T = Math.max(0,temp);
    }

    public void swapTemperature(SimpleMonteCarloReplica other) {
        double temp = T;
        T = other.T;
        other.T = temp;
    }

}
