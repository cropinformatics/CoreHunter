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

package org.corehunter.measures;

import java.util.List;
import org.corehunter.Accession;

/**
 *
 * @author hermandebeukelaer
 */
public class ExternalDistanceMeasure extends Measure {

    public ExternalDistanceMeasure() {
	this("EX", "External Distance Measure");
    }

    public ExternalDistanceMeasure(String name, String description) {
	super(name, description);
    }

    public double calculate(List<Accession> accessions) {
        try{
            double sum = 0.0;
            for(Accession a : accessions){
                sum += a.getExtDistance();
            }
            sum = sum/accessions.size();
            return sum;
        } catch (NullPointerException ne){
            System.err.println("No external distances present in dataset! Cannot use EX measure.");
            System.exit(1);
        }
        return -1;
    }

}
