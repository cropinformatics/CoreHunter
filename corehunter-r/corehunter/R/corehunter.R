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
	parameters <- .create.parameters(x, minSize=minSize, maxSize=maxSize, intensity=intensity)

	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "randomSearch", parameters$collection, as.integer(parameters$min), as.integer(parameters$max))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.exhaustive <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "exhaustiveSearch", parameters$collection, 
			parameters$objective, as.integer(parameters$min), as.integer(parameters$max))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.lr <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, l=2, r=1) 
{
	#if(Math.abs(lr_l-lr_r) > 1){
	#System.err.println("\n!!! Warning: current (l,r) setting may result" +
	#					"in core size slightly different from desired size");
	#}
	
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity, l=l, r=r)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "lrSearch", parameters$collection, 
			parameters$objective, as.integer(parameters$min), as.integer(parameters$max), as.integer(l), as.integer(r))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.semiLr <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, l=2, r=1) 
{
	#if(Math.abs(lr_l-lr_r) > 1){
	#System.err.println("\n!!! Warning: current (l,r) setting may result" +
	#					"in core size slightly different from desired size");
	#}
	
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity, l=l, r=r)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "semiLrSearch", parameters$collection, 
			parameters$objective, as.integer(parameters$min), as.integer(parameters$max), as.integer(l), as.integer(r))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.forward <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "forwardSelection", parameters$collection, 
			parameters$objective, as.integer(parameters$min), as.integer(parameters$max))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.semiForward <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "semiForwardSelection", parameters$collection, 
			parameters$objective, as.integer(parameters$min), as.integer(parameters$max))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.backward <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "backwardSelection", parameters$collection, 
			parameters$objective, as.integer(parameters$min), as.integer(parameters$max))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.remc <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, runtime = 60.0, minProg = 0.0, stuckTime = 60.0, 
		remcReplicas = 10, remcMinT = 50.0, remcMaxT = 200.0, remcMcSteps = 50) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.random.neighourhood(parameters$min, parameters$max) 
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "remcSearch", parameters$collection, 
			neighbourhood, parameters$objective, as.integer(parameters$min), as.integer(parameters$max), 
			as.integer(runtime), as.integer(minProg), as.integer(stuckTime), as.integer(remcReplicas), 
			remcMinT, remcMaxT, as.integer(remcMcSteps))
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.mixedReplica <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, runtime = 60.0, minProg = 0.0, stuckTime = 60.0) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.random.neighourhood(parameters$min, parameters$max) 
	
	#mixrepNrOfTabuReplicas, mixrepNrOfNonTabuReplicas,
	#mixrepRoundsWithoutTabu, mixrepNrOfTabuSteps, mixrepTournamentSize, tabuListSize,
	#mixrepBoostNr, mixrepBoostMinProg, mixrepBoostTimeFactor, mixrepMinBoostTime,
	#mixrepMinMCTemp, mixrepMaxMCTemp
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "mixedReplicaSearch", parameters$collection, 
			neighbourhood, parameters$objective, as.integer(parameters$min), as.integer(parameters$max), 
			runtime, minProg, stuckTime)
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.local <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, runtime = 60.0, minProg = 0.0, stuckTime = 60.0) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.random.neighourhood(parameters$min, parameters$max) 
		
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "localSearch", parameters$collection, 
			neighbourhood, parameters$objective, as.integer(parameters$min), as.integer(parameters$max), 
			runtime, minProg, stuckTime)
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.steepestDescent <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, runtime = 60.0, minProg = 0.0) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.random.neighourhood(parameters$min, parameters$max) 

	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "steepestDescentSearch", parameters$collection, 
			neighbourhood, parameters$objective, as.integer(parameters$min), as.integer(parameters$max), 
			runtime, minProg)
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.mstrat <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, runtime = 60.0, minProg = 0.0) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.heuristic.neighourhood(parameters$min, parameters$max) 
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "steepestDescentSearch", parameters$collection, 
			neighbourhood, parameters$objective, as.integer(parameters$min), as.integer(parameters$max), 
			runtime, minProg)
	
	return(.create.coresubset(x, parameters, subset))
}

coresubset.tabu <- function(x, measure, minSize=NULL, maxSize=NULL, intensity=NULL, runtime = 60.0, minProg = 0.0, stuckTime = 60, tabuListSize = 0) 
{
	parameters <- .create.parameters(x, measure=measure, minSize=minSize, maxSize=maxSize, intensity=intensity)
	
	neighbourhood <- .create.random.neighourhood(parameters$min, parameters$max) 
	
	#if (tabuListSizeSpecified && tabuListSize >= sampleMax){
	#	tabuListSize = sampleMax-1;
	#	System.err.println("\nSpecified tabu list size is larger than or equal to max core size.");
	#	System.err.println("List size was changed to 'max core size - 1' = " + (sampleMax-1) + ", to ensure at least one non-tabu neighbor.");
	#}
	
	
	#if (!tabuListSizeSpecified){
	#	// Default tabu list size = 30% of minimum sample size
	#			tabuListSize = Math.max((int) (0.3 * sampleMin), 1);
	#}
	
	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "tabuSearch", parameters$collection, 
			neighbourhood, parameters$objective, as.integer(parameters$min), as.integer(parameters$max), 
			runtime, minProg, stuckTime, as.integer(tabuListSize))
	
	return(.create.coresubset(x, parameters, subset))
}

