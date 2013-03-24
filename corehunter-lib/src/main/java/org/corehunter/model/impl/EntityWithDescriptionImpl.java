// Copyright 2012 Herman De Beukelaer, Guy Davenport
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

import org.corehunter.model.EntityWithDescription;

public class EntityWithDescriptionImpl extends EntityImpl implements EntityWithDescription {

    private String description;

    public EntityWithDescriptionImpl(String name) {
        super(name);
    }

    public EntityWithDescriptionImpl(String uniqueIdentifier, String name) {
        super(uniqueIdentifier, name);
    }

    public EntityWithDescriptionImpl(String uniqueIdentifier, String name, String description) {
        super(uniqueIdentifier, name);
        setDescription(description);
    }

    protected EntityWithDescriptionImpl(EntityWithDescription entityWithDescription) {
        super(entityWithDescription);
        setDescription(entityWithDescription.getDescription());
    }

    @Override
    public final String getDescription() {
        return description;
    }

    @Override
    public final void setDescription(String description) {
        this.description = description;
    }

		@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = super.hashCode();
	    result = prime * result
	        + ((description == null) ? 0 : description.hashCode());
	    return result;
    }

		@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (!super.equals(obj))
		    return false;
	    if (!EntityWithDescription.class.isAssignableFrom(obj.getClass()))
		    return false;
	    EntityWithDescriptionImpl other = (EntityWithDescriptionImpl) obj;
	    if (description == null)
	    {
		    if (other.description != null)
			    return false;
	    }
	    else
		    if (!description.equals(other.description))
			    return false;
	    return true;
    }
}
