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

import java.util.Collection;

import org.corehunter.CoreHunterException;
import org.corehunter.search.solution.SubsetSolution;

public interface SubsetSearch<IndexType, SolutionType extends SubsetSolution<IndexType>> extends Search<SolutionType>
{
	public Collection<IndexType> getIndices() ;
	
	public int getSubsetMinimumSize() ;

	public void setSubsetMinimumSize(int subsetMinimumSize) throws CoreHunterException ;

	public int getSubsetMaximumSize() ;
	
	public void setSubsetMaximumSize(int subsetMaximumSize) throws CoreHunterException ;
        
	public PreferredSize getSubsetPreferredSize();
        
	public void setSubsetPreferredSize(PreferredSize size) throws CoreHunterException;
	
}
