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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.corehunter.model.Entity;

public class EntityCollection<ElementType extends Entity> {

    private List<ElementType> elements;
    private Map<String, ElementType> elementsNameMap;

    public EntityCollection() {
        elements = new ArrayList<ElementType>();
        elementsNameMap = new HashMap<String, ElementType>();
    }

    public EntityCollection(EntityCollection<ElementType> entityCollection) {
        elements = new ArrayList<ElementType>(entityCollection != null ? entityCollection.getElements().size() : 0);
        elements.addAll(entityCollection.getElements());
        elementsNameMap = new HashMap<String, ElementType>(entityCollection.getElementNameMap());
    }

    public void add(ElementType element) {
        if (!elementsNameMap.containsKey(element.getName())) {
            elements.add(element);
            elementsNameMap.put(element.getName(), element);
        }
    }

    public void addAll(List<ElementType> elements) {
        for (ElementType a : elements) {
            add(a);
        }
    }

    public Set<String> getEntityNames() {
        return elementsNameMap.keySet();
    }

    public List<ElementType> getElements() {
        return elements;
    }

    public int size() {
        return elements.size();
    }

    /**
     * Get a subset of this entity collection, given an array of indices in
     * the range [0..#elements-1]
     *
     * @param indices
     * @return
     */
    public EntityCollection<ElementType> subset(Integer[] indices) {
        EntityCollection<ElementType> subset = new EntityCollection<ElementType>();
        for (int i : indices) {
            subset.add(elements.get(i));
        }
        return subset;
    }

    protected final Map<String, ElementType> getElementNameMap() {
        return elementsNameMap;
    }
}
