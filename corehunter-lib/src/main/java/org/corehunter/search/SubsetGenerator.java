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
package org.corehunter.search;

import java.util.List;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Validatable;

public interface SubsetGenerator<IndexType> extends Validatable
{
	public abstract long getNumberOfSubsets();

	public abstract List<IndexType> first();

	public abstract List<Integer> next(List<IndexType> subset);

	public abstract List<IndexType> getIndices() ;
	
	public abstract void setIndices(List<IndexType> indices) throws CoreHunterException ;
	
	public abstract int getSubsetSize();
	
	public abstract void setSubsetSize(int subsetSize) throws CoreHunterException ;

}