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
package org.corehunter.model.impl;

import org.corehunter.model.IndexedEntity;

public class IndexedEntityImpl<IndexType> extends EntityImpl implements
    IndexedEntity<IndexType>
{
	private IndexType index ;

	public IndexedEntityImpl(String name, IndexType index)
  {
	  super(name);
	  setIndex(index) ;
  }
	
	public IndexedEntityImpl(String uniqueIdentifier, String name, IndexType index)
  {
	  super(uniqueIdentifier, name);
	  setIndex(index) ;
  }

	@Override
	public final IndexType getIndex()
  {
  	return index;
  }
	
	private final void setIndex(IndexType index)
  {
  	this.index = index;
  }
}
