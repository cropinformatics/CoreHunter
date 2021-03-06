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

import java.util.Collection;
import java.util.HashSet;
import org.corehunter.CoreHunterException;
import org.corehunter.neighbourhood.EvaluatedIndexedMove;
import org.corehunter.neighbourhood.EvaluatedMove;
import org.corehunter.neighbourhood.impl.ExactSingleNeighbourhood;
import org.corehunter.search.Search;
import org.corehunter.search.SearchException;
import org.corehunter.search.SearchStatus;
import org.corehunter.search.solution.SubsetSolution;

public class LRSearch<IndexType, SolutionType extends SubsetSolution<IndexType>>
    extends AbstractSubsetSearch<IndexType, SolutionType>
{

    private int	l	= -1 ;
    private int	r	= -1 ;
    private ExactSingleNeighbourhood<IndexType, SolutionType> exactSingleNeighbourhood = new ExactSingleNeighbourhood<IndexType, SolutionType>();

    public LRSearch()
    {
            super();
    }

    protected LRSearch(LRSearch<IndexType, SolutionType> search)
        throws CoreHunterException
    {
            super(search);
            setL(search.getL());
            setR(search.getR());
    }

    @Override
    public Search<SolutionType> copy() throws CoreHunterException
    {
            return new LRSearch<IndexType, SolutionType>(this);
    }

    public final int getL()
    {
            return l;
    }

    public final void setL(int l) throws CoreHunterException
    {
            if (this.l != l)
            {
                    this.l = l;
                    handleLSet();
            }
    }

    public final int getR()
    {
            return r;
    }

    public final void setR(int r) throws CoreHunterException
    {
            if (this.r != r)
            {
                    this.r = r;
                    handleRSet();
            }
    }

    protected void handleLSet() throws CoreHunterException
    {
            if (SearchStatus.STARTED.equals(getStatus()))
            {
                    throw new CoreHunterException("L can not be set while search in process");
            }
            if (l < 0)
            {
                    throw new CoreHunterException("L can not be less than zero!");
            }
    }

    protected void handleRSet() throws CoreHunterException
    {
            if (SearchStatus.STARTED.equals(getStatus()))
            {
                    throw new CoreHunterException("R can not be set while search in process");
            }
            if (r < 0)
            {
                    throw new CoreHunterException("R can not be less than zero!");
            }
    }

    /**
     * Check whether subset size is increasing (L > R).
     */
    private boolean increasingSubsetSize()
    {
            return l > r;
    }

    /**
     * Check whether subset size is decreasing (R > L).
     */
    private boolean decreasingSubsetSize()
    {
            return r > l;
    }

    private boolean insideValidSizeRegion(SolutionType solution)
    {
            return solution.getSubsetSize() >= getSubsetMinimumSize()
                && solution.getSubsetSize() <= getSubsetMaximumSize();
    }
    
    private boolean minMaxSubsetSizeReached(SolutionType solution){
        if(increasingSubsetSize()){
            return solution.getSubsetSize() >= getSubsetMaximumSize();
        } else {
            return solution.getSubsetSize() <= getSubsetMinimumSize();
        }
    }

    @Override
    protected void runSearch() throws CoreHunterException
    {

        // stop if can not continue or maxmimum/minimum subset size reached if increasing/decreasing respectively
        while(canContinue() && !minMaxSubsetSizeReached(getCurrentSolution())){
            
            if(increasingSubsetSize()){
                // increasing: first add l, then remove r
                for (int i = 0 ; i < l ; ++i)
                {
                        performBestAdditionMove() ;
                }

                for (int i = 0 ; i < r ; ++i)
                {
                        performBestDeletionMove() ;
                }
            } else {
                // decreasing: first remove r, then add l
                for (int i = 0 ; i < r ; ++i)
                {
                        performBestDeletionMove() ;
                }

                for (int i = 0 ; i < l ; ++i)
                {
                        performBestAdditionMove() ;
                }
            }

        }

        // check if valid solution
        if (getBestSolution() == null || !insideValidSizeRegion(getBestSolution()))
        {
                throw new SearchException("Search stopped before it could find a valid solution");
        }

    }

    // this code is similar to some Neighbourhood it should be combine, once this is working well
    private void performBestAdditionMove() throws CoreHunterException {
        Collection<IndexType> unselected = new HashSet<IndexType>(getCurrentSolution().getRemainingIndices());

        EvaluatedIndexedMove<IndexType, SolutionType> bestMove = exactSingleNeighbourhood.findBestAdditionMove(getCurrentSolution(), getObjectiveFunction(), null, getCurrentSolutionEvaluation(), unselected);

        performMove(bestMove);
    }

    private void performBestDeletionMove() throws CoreHunterException {
        Collection<IndexType> selected = new HashSet<IndexType>(getCurrentSolution().getSubsetIndices());

        EvaluatedIndexedMove<IndexType, SolutionType> bestMove = exactSingleNeighbourhood.findBestDeletionMove(getCurrentSolution(), getObjectiveFunction(), null, getCurrentSolutionEvaluation(), selected);

        performMove(bestMove);
    }

    @SuppressWarnings("rawtypes")
    private void performMove(EvaluatedMove<SolutionType> move) throws CoreHunterException {
        move.apply(getCurrentSolution());

        setCurrentSolutionEvaluation(((EvaluatedMove) move).getEvaluation());

        if (insideValidSizeRegion(getCurrentSolution()) && isNewBestSolution(getCurrentSolutionEvaluation(), getCurrentSolution().getSize())) {
            handleNewBestSolution(getCurrentSolution(), getCurrentSolutionEvaluation());
        }
    }

    @Override
    protected void validate() throws CoreHunterException {
        super.validate();

        // check L and R
        if (l < 0) {
            throw new CoreHunterException("L can not be less than zero");
        }
        if (r < 0) {
            throw new CoreHunterException("R can not be less than zero");
        }
        if (l == r) {
            throw new CoreHunterException("L and R can not be equal");
        }

        // if subset size is increasing, initial size can not be too large
        if (increasingSubsetSize()
                && getCurrentSolution().getSubsetSize() > getSubsetMaximumSize()) {
            throw new CoreHunterException(
                    "L > R (increasing subset size): initial subset size can not be larger than maximum subset size");
        }

        // if subset size is decreasing, initial size can not be too small
        if (decreasingSubsetSize()
                && getCurrentSolution().getSubsetSize() < getSubsetMinimumSize()) {
            throw new CoreHunterException(
                    "L < R (decreasing subset size): initial subset size can not be smaller than minimum subset size");
        }
    }
}
