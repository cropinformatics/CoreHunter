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
package org.corehunter.search.impl;

import java.util.List;

import org.apache.commons.math.util.MathUtils;
import org.corehunter.CoreHunterException;
import org.corehunter.search.SubsetGenerator;
import org.corehunter.search.solution.SubsetSolution;

/**
 * Evaluate all possible core sets and return best one
 */
public class ExhaustiveSubsetSearch<IndexType, SolutionType extends SubsetSolution<IndexType>>
                                                                        extends AbstractSubsetSearch<IndexType, SolutionType> {

    private SubsetGenerator<IndexType> subsetGenerator = new IndexSubsetGenerator<IndexType>();

    public ExhaustiveSubsetSearch() {
        super();
    }

    protected ExhaustiveSubsetSearch(ExhaustiveSubsetSearch<IndexType, SolutionType> exhaustiveSearch) throws CoreHunterException {
        super(exhaustiveSearch);
    }

    @Override
    public ExhaustiveSubsetSearch<IndexType, SolutionType> copy() {
        try {
            return new ExhaustiveSubsetSearch<IndexType, SolutionType>(this);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void validate() throws CoreHunterException {
        super.validate();

        if (subsetGenerator == null) {
            throw new CoreHunterException("Subset generator must be defined!");
        }

        subsetGenerator.setCompleteSet(getIndices());
        subsetGenerator.setSubsetSize(getSubsetMinimumSize());
        subsetGenerator.validate();
    }

    @Override
    protected void runSearch() throws CoreHunterException {
        double score;

        double progress = 0;
        double newProgress;

        // Calculate pseudomeasure for all possible core sets and return best core

        long nr = getNumberOfSubsets();

        fireSearchMessage("Nr of possible core sets: " + nr + "!");

        for (int i = getSubsetMinimumSize(); i <= getSubsetMaximumSize(); i++) {
            subsetGenerator.setSubsetSize(i);

            List<IndexType> subsetIndices;
            long j = 1;
            nr = subsetGenerator.getNumberOfSubsets();
            while (subsetGenerator.hasNext()) {
                // create next subset
                subsetIndices = subsetGenerator.next();

                // fire progress
                newProgress = (double) j / (double) nr;
                if (newProgress > progress) {
                    fireSearchProgress(newProgress);
                    progress = newProgress;
                }

                // create and evaluate next solution
                getCurrentSolution().setSubsetIndices(subsetIndices);
                score = getObjectiveFunction().calculate(getCurrentSolution());
                setCurrentSolutionEvaluation(score);

                // check if new best solution found
                if (isNewBestSolution(getCurrentSolutionEvaluation(), getCurrentSolution().getSubsetSize())) {
                    handleNewBestSolution(getCurrentSolution(), getCurrentSolutionEvaluation());
                }
            }
        }
    }

    private long getNumberOfSubsets() {
        long numberOfSubsets = 0;

        for (int i = getSubsetMinimumSize(); i <= getSubsetMaximumSize(); ++i) {
            numberOfSubsets = numberOfSubsets + MathUtils.binomialCoefficient(getIndices().size(), i);
        }

        return numberOfSubsets;
    }
}
