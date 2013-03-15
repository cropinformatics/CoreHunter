//  Copyright 2012 Herman De Beukelaer, Guy Davenport
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

/**
 * Used to specify a preferred solution size. May be used to break ties when
 * considering multiple solutions with equal evaluation.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public enum PreferredSize {
    
    SMALLEST,        // prefer smaller cores
    LARGEST,         // prefer larger cores
    DONT_CARE       // don't care about size

}
