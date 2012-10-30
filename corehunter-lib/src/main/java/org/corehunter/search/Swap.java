//  Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer, Guy Davenport
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

import java.util.List;
import org.corehunter.Accession;

/**
 *
 * @author hermandebeukelaer
 */
public class Swap implements SinglePerturbation {

    // position of swap in the core
    private int coreIndex;
    // position of swap in the unselected list
    private int unselIndex;

    public Swap(int coreIndex, int unselIndex){
        this.coreIndex = coreIndex;
        this.unselIndex = unselIndex;
    }

    public void undo(List<Accession> core, List<Accession> unselected) {
        // To undo a swap: swap again!
        Accession a = core.set(coreIndex, unselected.get(unselIndex));
        unselected.set(unselIndex, a);
    }

}
