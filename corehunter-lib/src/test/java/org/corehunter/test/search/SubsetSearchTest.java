// Copyright 2012 Herman De Beukelaer, Guy Davenport
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

package org.corehunter.test.search;

import static org.corehunter.Constants.SECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Random;

import org.corehunter.CoreHunterException;
import org.corehunter.model.impl.AbstractFileUtility;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImplDataFileReader;
import org.corehunter.objectivefunction.impl.ObjectiveFunctionWithData;
import org.corehunter.search.ObjectiveSearch;
import org.corehunter.search.Search;
import org.corehunter.search.SearchListener;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.impl.ExhaustiveSubsetSearch;
import org.corehunter.search.impl.PrintWriterSubsetSearchListener;
import org.corehunter.search.impl.RandomSearch;
import org.corehunter.search.solution.SubsetSolution;
import org.corehunter.search.solution.impl.IntegerSubsetSolution;
import org.corehunter.test.search.ssr.SSRLocalSearchTest;
import org.junit.BeforeClass;

public abstract class SubsetSearchTest<IndexType, SolutionType extends SubsetSolution<IndexType>> {

    protected final long DEFAULT_RUNTIME = 2*SECOND;
    protected final Long DEFAULT_STUCKTIME = null;
    protected final double DEFAULT_MINIMUM_PROGRESSION = 0;
    protected final int DEFAULT_MINIMUM_SIZE = 20;
    protected final int DEFAULT_MAXIMUM_SIZE = 50;
    protected final int DEFAULT_TABU_HIST_SIZE = 20;
    protected final double DEFAULT_MINIMUM_TEMPERATURE = 50.0;
    protected final double DEFAULT_MAXIMUM_TEMPERATURE = 200.0;
    protected final Long DEFAULT_NUMBER_OF_STEPS = null;
    protected final int DEFAULT_NUMBER_OF_REPLICAS = 20;
    private static final String SSR_DATA_NAME_FULL = "bul.csv";
    protected static AccessionSSRMarkerMatrix<Integer> dataFull;
    private static final String SSR_DATA_NAME_10 = "bul10.csv";
    protected static AccessionSSRMarkerMatrix<Integer> data10;
    
    protected static final Random rg = new Random();

    @BeforeClass
    public static void beforeClass() {
        try {
            dataFull = new AccessionSSRMarkerMatrixListImplDataFileReader(new File(SSRLocalSearchTest.class.getResource("/" + SSR_DATA_NAME_FULL).getFile()), AbstractFileUtility.COMMA_DELIMITER).readData();
            data10 = new AccessionSSRMarkerMatrixListImplDataFileReader(new File(SSRLocalSearchTest.class.getResource("/" + SSR_DATA_NAME_10).getFile()), AbstractFileUtility.COMMA_DELIMITER).readData();
        } catch (CoreHunterException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    public void testSearch(Search<SolutionType> search) {
        try {

            search.addSearchListener(createSearchListener());

            search.start();

            assertEquals("Not completed", SearchStatus.COMPLETED, search.getStatus());
            assertNotNull("No result", search.getBestSolution());
            if (search instanceof ObjectiveSearch && !((ObjectiveSearch) search).getObjectiveFunction().isMinimizing()) {
                assertTrue("Not completed", -Double.MAX_VALUE < search.getBestSolutionEvaluation());
            } else {
                assertTrue("Not completed", Double.MAX_VALUE > search.getBestSolutionEvaluation());
            }

            System.out.println(search.getBestSolution());
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

    protected SearchListener<SolutionType> createSearchListener() {
        return new PrintWriterSubsetSearchListener<IndexType, SolutionType>();
    }

    protected void testCopy(Search<SolutionType> search) {
        try {
            assertEquals(search, search.copy());
        } catch (CoreHunterException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

    protected final SubsetSolution<Integer> findOptimalSolution(int minimumSize, int maximumSize, ObjectiveFunctionWithData<SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> objectiveFunction, AccessionSSRMarkerMatrix<Integer> data) throws CoreHunterException
    {
        return findExhaustiveSolution(minimumSize, maximumSize, objectiveFunction, data);
    }
    
    protected final SubsetSolution<Integer> findRandomSolution(int minimumSize, int maximumSize, ObjectiveFunctionWithData<SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> objectiveFunction, AccessionSSRMarkerMatrix<Integer> data) throws CoreHunterException
    {
      // sample random subset of set size
      
      RandomSearch<Integer, SubsetSolution<Integer>> randomSearch = new RandomSearch<Integer, SubsetSolution<Integer>>();
      randomSearch.setInitialSolution(new IntegerSubsetSolution(data.getIndices()));
      randomSearch.setObjectiveFunction(objectiveFunction);
      objectiveFunction.setData(data);
      randomSearch.setIndices(data.getIndices());
      randomSearch.setSubsetMinimumSize(minimumSize);
      randomSearch.setSubsetMaximumSize(maximumSize);
      
      randomSearch.start();
      
      return randomSearch.getBestSolution() ;
      
    }
    
    protected final SubsetSolution<Integer> findExhaustiveSolution(int minimumSize, int maximumSize, ObjectiveFunctionWithData<SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> objectiveFunction, AccessionSSRMarkerMatrix<Integer> data) throws CoreHunterException
    {
    	// exhaustively create best subset of set size
      
      ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>> exhaustiveSubsetSearch = new ExhaustiveSubsetSearch<Integer, SubsetSolution<Integer>>();
      exhaustiveSubsetSearch.setInitialSolution(new IntegerSubsetSolution(data.getIndices()));
      exhaustiveSubsetSearch.setObjectiveFunction(objectiveFunction);
      objectiveFunction.setData(dataFull);
      exhaustiveSubsetSearch.setIndices(data.getIndices());
      exhaustiveSubsetSearch.setSubsetMinimumSize(minimumSize);
      exhaustiveSubsetSearch.setSubsetMaximumSize(maximumSize);
      
      exhaustiveSubsetSearch.start();
      
      return exhaustiveSubsetSearch.getBestSolution() ;
      
    }
}
