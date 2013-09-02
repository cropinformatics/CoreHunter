// Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer, Guy Davenport
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

package org.corehunter.test;

import java.util.List;
import java.util.ListIterator;

import org.corehunter.CoreHunterException;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.objectivefunction.DistanceMeasureType;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.objectivefunction.impl.AbstractSubsetObjectiveFunction;
import org.corehunter.objectivefunction.ssr.SSROjectiveFunction;
import org.corehunter.search.solution.SubsetSolution;

/**
 * Uncached SSR Modified Rogers distance.
 */
public final class UncachedModifiedRogersDistanceSSR extends AbstractSubsetObjectiveFunction<Integer, AccessionSSRMarkerMatrix<Integer>> implements SSROjectiveFunction<Integer> {

    private DistanceMeasureType type;	            // states
    // whether mean
    // or min

    public UncachedModifiedRogersDistanceSSR() {
        this(DistanceMeasureType.MEAN_DISTANCE);
    }

    public UncachedModifiedRogersDistanceSSR(DistanceMeasureType type) {
        this("MR" + type.getNameSuffix(), "Modified Rogers Distance"
                + type.getDescriptionSuffix(), type);
    }

    public UncachedModifiedRogersDistanceSSR(String name, String description,
            DistanceMeasureType type) {
        super(name, description);

        this.type = type;
    }

    protected UncachedModifiedRogersDistanceSSR(UncachedModifiedRogersDistanceSSR objectiveFunction) {
        super(objectiveFunction);

        this.type = objectiveFunction.getType();
    }
    
    @Override
    public void flushCachedResults() throws CoreHunterException {
        // nothing to flush here!
    }

    @Override
    public final ObjectiveFunction<SubsetSolution<Integer>> copy() {
        return new UncachedModifiedRogersDistanceSSR(this);
    }

    public final DistanceMeasureType getType() {
        return type;
    }

    public final void setType(DistanceMeasureType type) {
        this.type = type;
    }

    @Override
    public final double calculate(SubsetSolution<Integer> solution) throws CoreHunterException {
        if (type == DistanceMeasureType.MEAN_DISTANCE) {
            double dist;
            double total = 0.0;
            int count = 0;

            for (Integer a : solution.getSubsetIndices()) {
                for (Integer b : solution.getSubsetIndices()) {
                    if (a < b) {
                        dist = calculate(a, b);
                        total += dist;
                        count++;
                    }
                }
            }

            return total / count;

        } else {
            if (type == DistanceMeasureType.MIN_DISTANCE) {
                double minimumDist = Double.MAX_VALUE;
                double dist = 0.0;

                for (Integer a : solution.getSubsetIndices()) {
                    for (Integer b : solution.getSubsetIndices()) {
                        if (a < b) {
                            dist = calculate(a, b);
                            if (dist < minimumDist) {
                                minimumDist = dist;
                            }
                        }
                    }
                }
                return minimumDist;
            } else {
                // THIS SHOULD NOT HAPPEN
                System.err
                        .println("Unkown distance measure type -- this is a bug! Please contact authors.");
                System.exit(1);
                return -1;
            }
        }
    }

    public double calculate(Integer index1, Integer index2) throws UnknownIndexException {
        double value;

        ListIterator<List<Double>> m1Itr = getData().getRowElements(index1).listIterator();
        ListIterator<List<Double>> m2Itr = getData().getRowElements(index2).listIterator();

        double markerCnt = 0;
        double sumMarkerSqDiff = 0;
        while (m1Itr.hasNext() && m2Itr.hasNext()) {
            ListIterator<Double> a1Itr = m1Itr.next().listIterator();
            ListIterator<Double> a2Itr = m2Itr.next().listIterator();

            double markerSqDiff = 0;
            while (a1Itr.hasNext() && a2Itr.hasNext()) {
                Double Pxla = a1Itr.next();
                Double Pyla = a2Itr.next();

                if (Pxla != null && Pyla != null) {
                    markerSqDiff += (Pxla - Pyla) * (Pxla - Pyla);
                }
            }

            sumMarkerSqDiff += markerSqDiff;
            markerCnt++;
        }

        value = 1.0 / (Math.sqrt(2.0 * markerCnt)) * Math.sqrt(sumMarkerSqDiff);

        return value;
    }
}
