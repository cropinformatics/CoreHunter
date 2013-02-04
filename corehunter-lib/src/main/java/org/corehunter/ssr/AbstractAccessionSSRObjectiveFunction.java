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

package org.corehunter.ssr;

import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.objectivefunction.impl.AbstractSubsetObjectiveFunction;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public abstract class AbstractAccessionSSRObjectiveFunction<IndexType>
	extends AbstractSubsetObjectiveFunction<IndexType, AccessionSSRMarkerMatrix<IndexType>> implements SSROjectiveFunction<IndexType>
{
	public AbstractAccessionSSRObjectiveFunction(String name, String description)
	{
		super (name, description) ;
	}
}