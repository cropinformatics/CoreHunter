package org.corehunter.test.search.impl;

import java.util.HashMap;
import org.corehunter.search.Search;
import org.corehunter.search.impl.PrintWriterSubsetSearchListener;
import org.corehunter.search.solution.SubsetSolution;

public class CachedSolutionPrintWriterSubsetSearchListener<
        IndexType,
        SolutionType extends SubsetSolution<IndexType>>
            extends PrintWriterSubsetSearchListener<IndexType, SolutionType> {

    HashMap<SolutionType, Double> solutions = new HashMap<SolutionType, Double>();

    @Override
    public void newBestSolution(Search<SolutionType> search, SolutionType bestSolution, double bestSolutionEvaluation) {
        if (solutions.containsKey(bestSolution)) {
            if (solutions.get(bestSolution) == bestSolutionEvaluation) {
                getPrintStream().println("*** Existing solution with same evaluation for: " + search.getName() + " evaluation: " + bestSolutionEvaluation + " size: " + bestSolution.getSubsetSize() + " solution: " + bestSolution);
            } else {
                getPrintStream().println("*** Existing solution with different evaluation for: " + search.getName() + " new evaluation: " + bestSolutionEvaluation + " previous evaluation: " + solutions.get(bestSolution) + " size: " + bestSolution.getSubsetSize() + " solution: " + bestSolution);
            }
        } else {
            super.newBestSolution(search, bestSolution, bestSolutionEvaluation);
            solutions.put(bestSolution, bestSolutionEvaluation);
        }
    }
}
