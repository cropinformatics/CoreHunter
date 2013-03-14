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
import java.io.FileOutputStream;
import java.io.PrintStream;
import org.corehunter.CoreHunterException;
import org.corehunter.search.Search;
import org.corehunter.search.SearchListener;
import org.corehunter.search.solution.Solution;

public class PrintWriterSearchListener<SolutionType extends Solution> implements SearchListener<SolutionType> {

    private PrintStream printStream;

    public PrintWriterSearchListener() {
        this(System.out);
    }

    public PrintWriterSearchListener(PrintStream printStream) {
        super();
        this.printStream = printStream;
    }

    public PrintWriterSearchListener(File file) throws FileNotFoundException {
        this(new PrintStream(new FileOutputStream(file)));
    }

    @Override
    public void searchStarted(Search<SolutionType> search) {
        printStream.println("Search Started : " + search.getName());
    }

    @Override
    public void searchCompleted(Search<SolutionType> search) {
        printStream.println("Search Completed : " + search.getName());
    }

    @Override
    public void searchStopped(Search<SolutionType> search) {
        printStream.println("Search Stopped : " + search.getName());
    }

    @Override
    public void searchFailed(Search<SolutionType> search, CoreHunterException exception) {
        printStream.println("Search failed : " + search.getName());
    }

    @Override
    public void newBestSolution(Search<SolutionType> search, SolutionType bestSolution, double bestSolutionEvaluation) {
        printStream.println("New best solution for: " + search.getName() + " evaluation: " + bestSolutionEvaluation + " solution: " + bestSolution);
    }

    @Override
    public void searchProgress(Search<SolutionType> search, double progress) {
        printStream.println("Search progress for search : " + search.getName() + " progress: " + formatProgress(progress));
    }

    @Override
    public void searchMessage(Search<SolutionType> search, String message) {
        printStream.println("Message from search : " + search.getName() + " meassage:" + message);
    }

    public final PrintStream getPrintStream() {
        return printStream;
    }

    protected String formatProgress(double progress) {
        // TODO use formatted
        return "" + progress;
    }
}
