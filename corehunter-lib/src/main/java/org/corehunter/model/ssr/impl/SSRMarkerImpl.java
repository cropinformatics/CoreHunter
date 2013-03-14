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
package org.corehunter.model.ssr.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.corehunter.model.DuplicateEntityException;
import org.corehunter.model.UnknownEntityException;
import org.corehunter.model.impl.EntityImpl;
import org.corehunter.model.ssr.SSRAllele;
import org.corehunter.model.ssr.SSRMarker;
import org.corehunter.utils.EntityUtils;


public class SSRMarkerImpl extends EntityImpl implements SSRMarker {

    private List<SSRAllele> alleles;

    public SSRMarkerImpl(String name) {
        super(name);
        alleles = new ArrayList<SSRAllele>();
    }

    @Override
    public List<SSRAllele> getAlleles() {
        return alleles;
    }

    @Override
    public boolean hasAllele(SSRAllele allele) {
        return alleles.contains(allele);
    }

    @Override
    public void addAllele(SSRAllele allele) throws DuplicateEntityException {
        if (!alleles.contains(allele)) {
            alleles.add(allele);
        } else {
            throw new DuplicateEntityException("Allele already present :" + allele.getName());
        }
    }

    @Override
    public void removeAllele(SSRAllele allele) throws UnknownEntityException {
        if (alleles.contains(allele)) {
            alleles.remove(allele);
        } else {
            throw new UnknownEntityException("Unknown allele :" + allele.getName());
        }
    }

    @Override
    public int indexOfAllele(SSRAllele allele) throws UnknownEntityException {
        int index = alleles.indexOf(allele);

        if (index >= 0) {
            return index;
        } else if (allele != null) {
            throw new UnknownEntityException("Unknown allele :" + allele.getName());
        } else {
            throw new UnknownEntityException("Undefined allele!");
        }

    }

    @Override
    public SSRAllele getAlleleByName(String name) {
        return (SSRAllele) EntityUtils.findByName(name, alleles);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SSRMarker) {
            return super.equals(object) && ObjectUtils.equals(getAlleles(), ((SSRMarker) object).getAlleles());
        } else {
            return super.equals(object);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (getAlleles() != null ? getAlleles().hashCode() : 0);
        return hash;
    }
}
