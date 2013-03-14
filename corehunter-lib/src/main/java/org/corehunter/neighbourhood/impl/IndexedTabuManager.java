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


package org.corehunter.neighbourhood.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import org.corehunter.neighbourhood.IndexedMove;


public class IndexedTabuManager<IndexType> extends AbstractTabuManager<IndexedMove<IndexType, ?>>
{
    
    // history of indices which are currently tabu
    private Queue<IndexType> tabu;
    
    public IndexedTabuManager(int histSize){
        this.histSize = histSize;
        tabu = new LinkedList<IndexType>();
    }
    
    /**
     * Check tabu indices.
     */
    @Override
    public boolean moveIsTabu(IndexedMove<IndexType, ?> move) {
        // move is tabu if at least one of its involved indices
        // are contained in the current tabu list
        boolean isTabu = false;
        Iterator<IndexType> it = move.getInvolvedIndices().iterator();
        while(!isTabu && it.hasNext()){
            isTabu = tabu.contains(it.next());
        }
        return isTabu;
    }

    public void registerMoveTaken(IndexedMove<IndexType, ?> move) {
        // register all indices involved in the indexed move as tabu
        for(IndexType index : move.getInvolvedIndices()){
            registerTabuIndex(index);
        }
    }
    
    private void registerTabuIndex(IndexType index){
        // check if history size limit reached
        if(tabu.size() == histSize){
            // remove oldest index from queue
            tabu.poll();
        }
        // now add new index
        tabu.offer(index);
    }
    
    protected void handleTabuHistSizeSet(){
        // remove indices from tabu queue to respect new size limit
        while(tabu.size() > histSize){
            tabu.poll();
        }
    }

    public void reset() {
        tabu.clear();
    }
    
    @Override
    public String toString(){
        return "Tabu indices: " + tabu.toString();
    }

}
