# Utility R functions for corehunter
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

.onLoad <- function(libname, pkgname) {
	
	rJava::.jpackage(pkgname, lib.loc = libname)
	
}

createParametersWithoutMeasure <- function(x, minSize=NULL, maxSize=NULL, intensity=NULL) 
{
	parameters <- list() 
	
	parameters$collection <- .jnew("org/corehunter/AccessionCollection")
	parameters$accessionNames <- colnames(x)[3:ncol(x)]
	parameters$markerNames <- as.character(x[,1])
	parameters$alleleNames <- as.character(x[,2])
	parameters$scores <- x[,3:ncol(x)]
	parameters$dataset <- .jrcall("org/corehunter/SSRDataset", "createFromArray",
			.jarray(as.array(parameters$accessionNames), dispatch = TRUE),
			.jarray(as.array(parameters$markerNames), dispatch = TRUE),
			.jarray(as.array(parameters$alleleNames), dispatch = TRUE),
			.jarray(as.matrix(parameters$scores), dispatch = TRUE)
			)
	
	.jrcall(parameters$collection, "addDataset", parameters$dataset)
	
	parameters$collectionSize <- .jrcall(parameters$collection, "size")
	
	parameters$minSize <- 0 
	parameters$maxSize <- 0 
	
	if (!is.null(minSize) && !is.null(maxSize))
	{
		parameters$minSize <- as.integer(minSize)
		parameters$maxSize <- as.integer(maxSize)
	}
	else
	{
		if (!is.null(intensity))
		{	
			parameters$minSize <- as.integer(intensity * parameters$collectionSize)
			parameters$maxSize <- parameters$minSize
		}
		else
		{
			stop("Either intensity or, min and max must be defined!") 
		}
	}
	
	if (parameters$minSize <= 0)
		stop("min subset size must be greater than zero!") 
	
	if (parameters$maxSize <= 0)
		stop("max subset size must be greater than zero!") 
	
	if (parameters$minSize >= parameters$collectionSize)
		stop("min subset size must be less than size!") 
	
	if (parameters$maxSize >= parameters$collectionSize)
		stop("max subset size must be less than size!") 
	
	if (parameters$maxSize < parameters$minSize)
		stop("max subset size can not be less than min!") 
	
	return (parameters)
}

createParameters <- function(x, minSize=NULL, maxSize=NULL, intensity=NULL, measure=NULL) 
{
	parameters <- createParametersWithoutMeasure(x, minSize, maxSize, intensity) ;
	
	if (!is.null(measure))
	{
		parameters$objectiveFunction = createObjectiveFunction(measure, collectionSize=parameters$collectionSize) ;
	}
	else
	{
		stop("Measure not defined!") 	
	}
	
	if (is.null(parameters$objectiveFunction))
	{
		stop("Measure not defined!") 	
	}
	
	return (parameters)
}

createCoresubset <- function(x, parameters, subset)
{
	selected <- .jrcall(subset, "getAccessionNamesAsArray") ;
	
	core <- data.frame(row.names = parameters$accessionNames) 
	
	core$selected <- parameters$accessionNames %in% selected
	
	return(core)
}

createRandomNeighourhood <- function(minSize=NULL, maxSize=NULL)
{
	neighborhood <- .jnew("org/corehunter/search/RandomSingleNeighborhood", as.integer(minSize), as.integer(maxSize)) ;

	return(neighborhood)
}

createHeuristicNeighourhood <- function(minSize=NULL, max=NULL)
{
	neighborhood <- .jnew("org/corehunter/search/HeuristicSingleNeighborhood", as.integer(minSize), as.integer(maxSize)) ;
	
	return(neighborhood)
}

createObjectiveFunction <- function(measure, collectionSize = NULL) 
{
	measureNames <- .jrcall("org/corehunter/measures/MeasureFactory", "getMeasureNames") ;
	availableMeasureNames <- measureNames
	
	if (is.null(measure))
		stop("Measure not defined!") 
	
	if (is.null(collectionSize))
		stop("Collection size not defined!") 
	
	if (collectionSize <= 0)
		stop("Collection size must be greater than zero!") 
	
	if (is.data.frame(measure))
	{
		objectiveFunction <- .jnew("org/corehunter/measures/PseudoMeasure") ;
		
		measures = rownames(measure) ;
		
		for (m in measures) 
		{
			if (m %in% measureNames)
			{
				if (m %in% availableMeasureNames)
				{
					obj <- .jrcall("org/corehunter/measures/MeasureFactory", "createMeasure", m, as.integer(collectionSize)) ;
					
					.jrcall(objectiveFunction, "addMeasure", obj,  measure[m,] ) ;
					
					availableMeasureNames <- availableMeasureNames[availableMeasureNames != m]
				}
				else
				{
					stop(paste("Meaure already in use : ", m)) 
				}
			}
			else
			{
				stop(paste("Unknown measure : ", m)) 
			}
		}
	}
	else
	{
		if (measure %in% measureNames)
		{
			objectiveFunction <- .jnew("org/corehunter/measures/PseudoMeasure") ;
			
			obj <- .jrcall("org/corehunter/measures/MeasureFactory", "createMeasure", measure, as.integer(collectionSize)) ;
			
			.jrcall(objectiveFunction, "addMeasure", obj,  1.0) ;
		}
		else
		{
				stop("Unknown measure : " + measure) 
		}	
	}

	return (objectiveFunction)
}

