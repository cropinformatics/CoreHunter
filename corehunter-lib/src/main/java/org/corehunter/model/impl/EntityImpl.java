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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.corehunter.model.Entity;

public class EntityImpl implements Entity {

    private String uniqueIdentifier;
    private String name;

    public EntityImpl(String name) {
        this(name, name);
    }

    public EntityImpl(String uniqueIdentifier, String name) {
        setUniqueIdentifier(uniqueIdentifier);
        setName(name);
    }

    public EntityImpl(Entity entity) {
        setUniqueIdentifier(entity.getUniqueIdentifier());
        setName(entity.getName());
    }

    @Override
    public final String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    @Override
    public final void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Entity) {
            return super.equals(object) || (getUniqueIdentifier().equals(((Entity) object).getUniqueIdentifier()) && getName().equals(((Entity) object).getName()));
        } else {
            return super.equals(object);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (getUniqueIdentifier() != null ? getUniqueIdentifier().hashCode() : 0);
        hash = 89 * hash + (getName() != null ? getName().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
