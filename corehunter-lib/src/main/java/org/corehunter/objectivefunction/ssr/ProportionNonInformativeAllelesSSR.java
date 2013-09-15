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
package org.corehunter.objectivefunction.ssr;

import java.util.List;
import org.corehunter.CoreHunterException;
import org.corehunter.model.UnknownIndexException;
import org.corehunter.objectivefunction.ObjectiveFunction;
import org.corehunter.objectivefunction.impl.CachedResult;
import org.corehunter.search.solution.SubsetSolution;

/**
 * <<Class summary>>
 *
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public final class ProportionNonInformativeAllelesSSR<IndexType> extends AbstractAccessionSSRObjectiveFunction<IndexType> {

    private PNCachedResult cachedResult;

    public ProportionNonInformativeAllelesSSR() {
        this("PN", "Proportion of non-informative alleles");
    }

    public ProportionNonInformativeAllelesSSR(String name, String description) {
        super(name, description);
    }

    protected ProportionNonInformativeAllelesSSR(ProportionNonInformativeAllelesSSR<IndexType> objectiveFunction)  throws CoreHunterException{
        super(objectiveFunction);
    }

    @Override
    public void flushCachedResults() throws CoreHunterException {
        cachedResult = new PNCachedResult();
    }

    @Override
    public ObjectiveFunction<SubsetSolution<IndexType>> copy()  throws CoreHunterException {
        return new ProportionNonInformativeAllelesSSR<IndexType>(this);
    }

    @Override
    public boolean isMinimizing() {
        return true;
    }

    @Override
    public double calculate(SubsetSolution<IndexType> solution) throws CoreHunterException, CoreHunterException {
        List<IndexType> aIndices = cachedResult.getAddedIndices(solution.getIndices());
        List<IndexType> rIndices = cachedResult.getRemovedIndices(solution.getIndices());

        int alleleCounts[] = cachedResult.getAlleleCounts();

        int addTotals[] = getData().getAlleleCounts(aIndices);
        int remTotals[] = getData().getAlleleCounts(rIndices);

        int alleleCnt = 0;
        for (int i = 0; i < alleleCounts.length; i++) {
            int diff = 0;
            if (addTotals != null) {
                diff += addTotals[i];
            }
            if (remTotals != null) {
                diff -= remTotals[i];
            }
            alleleCounts[i] += diff;
            if (alleleCounts[i] <= 0) {
                alleleCnt += 1;
            }
        }

        cachedResult.setIndices(solution.getIndices());

        return (double) alleleCnt / (double) alleleCounts.length;
    }

    private class PNCachedResult extends CachedResult<IndexType> {

        private int pAlleleCounts[];

        public PNCachedResult() throws UnknownIndexException {
            super();
            int alleleCnt = getData().getAlleleCount(getData().getIndices().get(0));

            pAlleleCounts = new int[alleleCnt];
            for (int i = 0; i < alleleCnt; i++) {
                pAlleleCounts[i] = 0;
            }
        }

        public int[] getAlleleCounts() {
            return pAlleleCounts;
        }
    }
}
