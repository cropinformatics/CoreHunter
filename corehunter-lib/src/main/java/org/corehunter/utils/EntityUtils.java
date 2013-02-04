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
package org.corehunter.utils;

import java.util.Collection;
import java.util.Iterator;

import org.corehunter.model.Entity;

public class EntityUtils
{
	public static final String[] getNames(Collection<? extends Entity> entities)
	{
		String[] names = new String[entities.size()] ;
		
		Iterator<? extends Entity> iterator = entities.iterator() ;
		
		int i = 0 ;
		
		while (iterator.hasNext())
		{
			names[i] = iterator.next().getName() ;
			++i ;
		}
		
		return names ;
	}
	
	public static final Entity findByName(String name, Collection<? extends Entity> entities)
	{
		Entity entity = null ;
		
		Iterator<? extends Entity> iterator = entities.iterator() ;
		
		while (entity == null && iterator.hasNext())
		{
			entity = iterator.next();
			
			if (!entity.getName().equals(name))
				entity = null ;
		}
		
		return entity ;
	}
}
