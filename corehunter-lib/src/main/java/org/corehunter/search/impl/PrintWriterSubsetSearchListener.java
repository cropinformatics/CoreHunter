// Copyright 2012 Guy Davenport, Herman De Beukelaer
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import org.corehunter.search.Search;
import org.corehunter.search.solution.SubsetSolution;

public class PrintWriterSubsetSearchListener<IndexType, SolutionType extends SubsetSolution<IndexType>> extends PrintWriterSearchListener<SolutionType> {

    public PrintWriterSubsetSearchListener() {
        super();
    }

    public PrintWriterSubsetSearchListener(PrintStream printStream) {
        super(printStream);
    }

    public PrintWriterSubsetSearchListener(File file) throws FileNotFoundException {
        super(file);
    }

    @Override
    public void newBestSolution(Search<SolutionType> search, SolutionType bestSolution, double bestSolutionEvaluation) {
        getPrintStream().println("New best solution for: " + search.getName() + " evaluation: " + bestSolutionEvaluation + " size: " + bestSolution.getSubsetSize() + " solution: " + bestSolution);
    }
}
