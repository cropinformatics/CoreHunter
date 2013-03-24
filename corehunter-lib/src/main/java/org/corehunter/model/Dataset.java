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
package org.corehunter.model;

import java.util.Collection;

public interface Dataset<ValueType> extends Data
{
	/**
	 * Gets the elements of the dataset. If the dataset has a predefined order
	 * ordered then this method should can return a list, otherwise a set. In
	 * any case the collection return should be a true set and have no duplicate elements
	 * @return
	 */
	public Collection<ValueType> getElements();
}
