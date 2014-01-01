# Base R functions for corehunter
# 
# Author: Guy Davenport, Herman De Beukelaer 2013
#  Copyright 2013 Guy Davenport, Herman De Beukelaer
#
#  Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
###############################################################################

#' Performs an exhaustive subset search
#' 
#' Iterates through all subsets within the given size range, evaulating each subset
#' using the given measure and returns the best subset
#' 
#' @param x A data frame containing the allele frequences for the each accession. The first
#' column is the marker name, second column the allele name and one or more subsequent columns 
#' providing the allele frequencies for one or more accessions.
#' @param measure the code of the measure to use or a data frame containing two or measure codes 
#' in the first column and the relative weights of the measure in the second column
#' @param minSize the minimum size of the required subset.
#' @param maxSize the maximum size of the required subset if a range of subsets sizes is required. 
#' Can not be used in conjunction with intensity
#' @param intensity the required subset as a relative proportion of the complete set size. Can not be 
#' used in conjunction with minSize or maxSize
#' 
#' @return A data frame containing a row for each accession in the input file, with row names are
#' the accessions names provided in the input file and the only column call 'selected' is
#' is TRUE or FALSE if selected as part of the subset.
#' @author Guy Davenport and Herman De Beukelaer
#' @export
#' 
exhaustiveSubsetSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "exhaustiveSearch", parameters$collection, 
			parameters$objective, as.integer(parameters$minSize), as.integer(parameters$maxSize))
	
	return(createCoresubset(x, parameters, subset))
}

lrSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, l=2, r=1) 
{
	if(abs(l-r) > 1) {
		warning("Warning: current (l,r) setting may result" +
						"in core size slightly different from desired size");
	}
	
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "lrSearch", parameters$collection, 
			parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), as.integer(l), as.integer(r))
	
	return(createCoresubset(x, parameters, subset))
}

semiLrSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, l=2, r=1) 
{
	if(abs(l-r) > 1) {
		warning("Warning: current (l,r) setting may result" +
					"in core size slightly different from desired size");
	}
	
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "semiLrSearch", parameters$collection, 
			parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), as.integer(l), as.integer(r))
	
	return(createCoresubset(x, parameters, subset))
}

forwardSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "forwardSelection", parameters$collection, 
			parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize))
	
	return(createCoresubset(x, parameters, subset))
}

semiForwardSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "semiForwardSelection", parameters$collection, 
			parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize))
	
	return(createCoresubset(x, parameters, subset))
}

backwardSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "backwardSelection", parameters$collection, 
			parameters$objective, as.integer(parameters$minSize), as.integer(parameters$maxSize))
	
	return(createCoresubset(x, parameters, subset))
}

remcSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0, stuckTime = 60.0, 
		replicas = 10, minTemp = 50.0, maxTemp = 200.0, steps = 50) 
{
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- createRandomNeighourhood(parameters$minSize, parameters$maxSize) 
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "remcSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg, stuckTime, as.integer(replicas), 
			minTemp, maxTemp, as.integer(steps))
	
	return(createCoresubset(x, parameters, subset))
}

mixedReplicaSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0, stuckTime = 60.0, nrOfTabuReplicas = 2, nrOfNonTabuReplicas = 3,
		roundsWithoutTabu = 10, nrOfTabuSteps = 5, tournamentSize = 2, tabuListSize = NULL,
		boostNr = 2, boostMinProg = 10e-9, boostTimeFactor = 15, minBoostTime = 0.25,
		minTemp = 50.0, maxTemp = 100.0) 
{	
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	if (!is.null(tabuListSize))
	{
		if (tabuListSize >= parameters$collectionSize)
			stop("\nSpecified tabu list size is larger than or equal to max core size.");
	}
	else
	{
		tabuListSize = 0.3 * minSize
		
		if (tabuListSize < 1)
			tabuListSize = 1
	}

	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "mixedReplicaSearch", parameters$collection, 
			parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg, stuckTime, as.integer(nrOfTabuReplicas), as.integer(nrOfNonTabuReplicas),
			as.integer(roundsWithoutTabu), as.integer(nrOfTabuSteps), as.integer(tournamentSize), as.integer(tabuListSize),
			as.integer(boostNr), boostMinProg, as.integer(boostTimeFactor), minBoostTime,
			minTemp, maxTemp)
	
	return(createCoresubset(x, parameters, subset))
}

localSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0, stuckTime = 60.0) 
{
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- createRandomNeighourhood(parameters$minSize, parameters$maxSize) 
		
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "localSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg, stuckTime)
	
	return(createCoresubset(x, parameters, subset))
}

steepestDescentSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0) 
{
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- createRandomNeighourhood(parameters$minSize, parameters$maxSize) 

	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "steepestDescentSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg)
	
	return(createCoresubset(x, parameters, subset))
}

mstratSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0) 
{
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- createHeuristicNeighourhood(parameters$minSize, parameters$maxSize) 
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "steepestDescentSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg)
	
	return(createCoresubset(x, parameters, subset))
}

tabuSearch <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0, stuckTime = 60, tabuListSize = NULL) 
{
	parameters <- createParameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- createRandomNeighourhood(parameters$minSize, parameters$maxSize) 
	
	if (!is.null(tabuListSize))
	{
		if (tabuListSize >= parameters$collectionSize)
			stop("\nSpecified tabu list size is larger than or equal to max core size.");
	}
	else
	{
		tabuListSize = 0.3 * minSize
		
		if (tabuListSize < 1)
			tabuListSize = 1
	}
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "tabuSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg, stuckTime, as.integer(tabuListSize))
	
	return(createCoresubset(x, parameters, subset))
}

