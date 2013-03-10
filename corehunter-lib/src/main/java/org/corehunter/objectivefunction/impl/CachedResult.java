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

package org.corehunter.objectivefunction.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * <<Class summary>>
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 * @version $Rev$
 */
public class CachedResult<IndexType>
{
	protected List<IndexType>	  indices;

	public CachedResult()
	{
		indices = new ArrayList<IndexType>();
	}

	public final void setIndices(Collection<IndexType> indices)
	{
		this.indices.clear();
		this.indices.addAll(indices);
	}

	public final Collection<IndexType> getIndices()
	{
		return indices;
	}

	public final List<IndexType> getAddedIndices(Collection<IndexType> indices)
	{
		List<IndexType> aIndices = new ArrayList<IndexType>(indices);
		aIndices.removeAll(this.indices);
		return aIndices;
	}

	public final List<IndexType> getRemovedIndices(Collection<IndexType> indices)
	{
		List<IndexType> rIndices = new ArrayList<IndexType>(this.indices);
		rIndices.removeAll(indices);
		return rIndices;
	}

	public final List<IndexType> getCommonIndices(Collection<IndexType> indices)
	{
		List<IndexType> cIndices = new ArrayList<IndexType>(this.indices);
		ListIterator<IndexType> iterator = cIndices.listIterator();

		while (iterator.hasNext())
		{
			IndexType a = iterator.next();
			if (!indices.contains(a))
			{
				iterator.remove();
			}
		}

		return cIndices;
	}
}
