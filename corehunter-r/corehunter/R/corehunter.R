# Base R functions for corehunter
# 
# Author: Guy Davenport, Herman De Beukelaer 2013
#  Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer
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


library(rJava)
.jinit() # this starts the JVM

coresubset.random <- function(x, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- .create.parameters.without.measure(x, minSize=minSize, maxSize=maxSize, intensity=intensity)

	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "randomSearch", parameters$collection, as.integer(parameters$minSize), as.integer(parameters$maxSize))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.exhaustive <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "exhaustiveSearch", parameters$collection, 
			parameters$objective, as.integer(parameters$minSize), as.integer(parameters$maxSize))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.lr <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, l=2, r=1) 
{
	#if(Math.abs(lr_l-lr_r) > 1){
	#System.err.println("\n!!! Warning: current (l,r) setting may result" +
	#					"in core size slightly different from desired size");
	#}
	
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "lrSearch", parameters$collection, 
			parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), as.integer(l), as.integer(r))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.semiLr <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, l=2, r=1) 
{
	#if(Math.abs(lr_l-lr_r) > 1){
	#System.err.println("\n!!! Warning: current (l,r) setting may result" +
	#					"in core size slightly different from desired size");
	#}
	
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "semiLrSearch", parameters$collection, 
			parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), as.integer(l), as.integer(r))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.forward <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "forwardSelection", parameters$collection, 
			parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.semiForward <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "semiForwardSelection", parameters$collection, 
			parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.backward <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "backwardSelection", parameters$collection, 
			parameters$objective, as.integer(parameters$minSize), as.integer(parameters$maxSize))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.remc <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0, stuckTime = 60.0, 
		remcReplicas = 10, remcMinT = 50.0, remcMaxT = 200.0, remcMcSteps = 50) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.random.neighourhood(parameters$minSize, parameters$maxSize) 
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "remcSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			as.integer(runtime), as.integer(minProg), as.integer(stuckTime), as.integer(remcReplicas), 
			remcMinT, remcMaxT, as.integer(remcMcSteps))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.mixedReplica <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0, stuckTime = 60.0, nrOfTabuReplicas = 2, nrOfNonTabuReplicas = 3,
		roundsWithoutTabu = 10, nrOfTabuSteps = 5, tournamentSize = 2, tabuListSize = NULL,
		boostNr, boostMinProg = 10e-9, boostTimeFactor = 15, minBoostTime = 0.25,
		minMCTemp = 50.0, maxMCTemp = 100.0) 
{	
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	if (!is.null(tabuListSize))
	{
		if (tabuListSize >= parameters$collectionSize)
			stop("\nSpecified tabu list size is larger than or equal to max core size.");
	}
	else
	{
		tabuListSize = 0.3 * sampleMin
		
		if (tabuListSize < 1)
			tabuListSize = 1
	}
	
	neighbourhood <- .create.random.neighourhood(parameters$minSize, parameters$maxSize) 
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "mixedReplicaSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg, stuckTime, as.integer(mixrepNrOfTabuReplicas), as.integer(mixrepNrOfNonTabuReplicas),
			as.integer(mixrepRoundsWithoutTabu), as.integer(mixrepNrOfTabuSteps), as.integer(mixrepTournamentSize), as.integer(tabuListSize),
			as.integer(mixrepBoostNr), mixrepBoostMinProg, mixrepBoostTimeFactor, mixrepMinBoostTime,
			mixrepMinMCTemp, mixrepMaxMCTemp)
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.local <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0, stuckTime = 60.0) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.random.neighourhood(parameters$minSize, parameters$maxSize) 
		
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "localSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg, stuckTime)
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.steepestDescent <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.random.neighourhood(parameters$minSize, parameters$maxSize) 

	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "steepestDescentSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg)
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.mstrat <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.heuristic.neighourhood(parameters$minSize, parameters$maxSize) 
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "steepestDescentSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg)
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.tabu <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, 
		runtime = 60.0, minProg = 0.0, stuckTime = 60, tabuListSize = NULL) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.random.neighourhood(parameters$minSize, parameters$maxSize) 
	
	if (!is.null(tabuListSize))
	{
		if (tabuListSize >= parameters$collectionSize)
			stop("\nSpecified tabu list size is larger than or equal to max core size.");
	}
	else
	{
		tabuListSize = 0.3 * sampleMin
		
		if (tabuListSize < 1)
			tabuListSize = 1
	}
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "tabuSearch", parameters$collection, 
			neighbourhood, parameters$objectiveFunction, as.integer(parameters$minSize), as.integer(parameters$maxSize), 
			runtime, minProg, stuckTime, as.integer(tabuListSize))
	
	return(.create.coresubset(x, parameters, subset))
}

