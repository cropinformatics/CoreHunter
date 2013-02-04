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
import java.util.Random;

public interface SubsetSolution<IndexType> extends Solution
{
	/**
	 * Gets the list of indices in the full set
	 * 
	 * @return the list of indices in the full set
	 */
	public List<IndexType> getIndices() ;
	
	/**
	 * Get the size of the full set 
	 * 
	 * @return the size of the full set 
	 */
	public int getSize();
	
	/**
	 * Gets a list of all the indices in the subset, by default
	 * a cached list should be returned. After any updates to the
	 * solution the cache is renewed, thus any changes to the solution
	 * will not affect the list returned by this method. Call this method
	 * again to get an updated cache.
	 *  
	 * @return a list cached of all the indices in the subset
	 */
	public List<IndexType> getSubsetIndices() ;
	
	/**
	 * Sets the indices in the subset, replacing any existing indices. 
	 * All the indices must be in the full set. Although the implementation may not
	 * check this requirement
	 *  
	 * @param sets the indices in the subset
	 */
	public void setSubsetIndices(List<IndexType> subsetIndices) ;

	/**
	 * Gets the current size of the subset
	 * @return the current size of the subset
	 */
	public int getSubsetSize();
	
	/**
	 * Gets a list of all the indices NOT in the subset, by default
	 * a cached list should be returned. After any updates to the
	 * solution the cache is renewed, thus any changes to the solution
	 * will not affect the list returned by this method. Call this method
	 * again to get an updated cache.
	 *  
	 * @return a list cached of all the indices NOT in the subset
	 */
	public List<IndexType> getRemainingIndices() ;

	/**
	 * Gets the current number of the indices not in the subset
	 * @return the current number of the indices not in the subset
	 */
	public int getRemainingSize();
	
	/**
	 * Adds an index to the subset, removing it from the
	 * list of remaining indices ;
	 * 
	 * @param index an index to add to the subset
	 */
	public void addIndex(IndexType index) ;
	
	/**
	 * Adds a random index to the subset, removing it from the
	 * list of remaining indices ;
	 * 
	 * @param random the optional random to be used 
	 * for selection. If <code>null</code> an new 
	 * Random instance will be created.
	 * @return the index added
	 */
	public IndexType addRandomIndex(Random random) ;
	
	/**
	 * Adds all the indices from the full set to the subset
	 */
	public void addAllIndices() ;
	
	/**
	 * Removed an index form the subset, adding it to the
	 * list of remaining indices ;
	 * 
	 * @param index an index to remove from the subset
	 */
	public void removeIndex(IndexType index) ;
	
	/**
	 * Removes a random index from the subset, adding it to the
	 * list of remaining indices ;
	 * 
	 * @param random the optional random to be used 
	 * for selection. If <code>null</code> an new 
	 * Random instance will be created.
	 * @return the index removed
	 */
	public IndexType removeRandomIndex(Random random) ;
	
	/**
	 * Adds all the indices from the subset set
	 */
	public void removeAllIndices() ;
	
	/**
	 * Swaps two indices, removing one from the subset and
	 * replacing it with another.
	 * 
	 * @param indexToAdd the index that will be added to the subset
	 * @param indexToRemove the index that will be removed from the subset
	 */
	public void swapIndices(IndexType indexToAdd, IndexType indexToRemove) ;
	
	/**
	 * Swaps two randomly selected indices, removing one from the subset and
	 * replacing it with another.
	 * 
	 * @param random the optional random to be used 
	 * for selection. If <code>null</code> an new 
	 * Random instance will be created.
	 * @return a two element array containing the indices of the swapped elements.
	 * the first being the index added and second the index removed
	 */
	public IndexType[] swapRandomIndices(Random random) ;
	
	/**
	 * Gets a position of given index with the subset
	 * 
	 * @param index index with the subset
	 * @return a position of given index with the subset
	 */
	public int getPositionInSubset(IndexType position) ;
	
	/**
	 * Gets an index at a given position with the subset
	 * 
	 * @param position a position with the subset
	 * @return index at a given position with the subset
	 */
	public IndexType getIndexInSubsetAt(int position) ;
	
	/**
	 * Contains a given index 
	 * 
	 * @param index a given index
	 * @return <code>true</code> if the index is in the subset, <code>false</code> otherwise
	 */
	public boolean containsIndexInSubset(IndexType index) ;
}
