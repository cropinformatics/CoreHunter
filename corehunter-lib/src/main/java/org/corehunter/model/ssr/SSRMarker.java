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
package org.corehunter.model.ssr;

import java.util.List;

import org.corehunter.model.DuplicateEntityException;
import org.corehunter.model.Entity;
import org.corehunter.model.UnknownEntityException;

public interface SSRMarker extends Entity
{
	public List<SSRAllele> getAlleles();
	
	public void addAllele(SSRAllele allele) throws DuplicateEntityException ;

	public void removeAllele(SSRAllele allele) throws UnknownEntityException ;
	
	public boolean hasAllele(SSRAllele allele) ;

	public int indexOfAllele(SSRAllele alelle) throws UnknownEntityException ;

	public SSRAllele getAlleleByName(String name);
}
