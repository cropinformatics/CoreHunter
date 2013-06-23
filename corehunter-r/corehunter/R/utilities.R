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

.create.parameters <- function(x, min=NULL, max=NULL, intensity=NULL) 
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
	
	parameters$size <- .jrcall(parameters$collection, "size")
	
	if (!is.null(min) && !is.null(max))
	{
		parameters$min <- as.integer(min)
		parameters$max <- as.integer(max)
	}
	else
	{
		if (!is.null(intensity))
		{
			parameters$min <- as.integer(intensity * parameters$size)
			parameters$max <- parameters$min
		}
		else
		{
			stop("Either intensity or, min and max must be defined!") 
		}
	}
	
	if (parameters$min <= 0)
		stop("min must be greater than zero!") 
	
	if (parameters$max <= 0)
		stop("max must be greater than zero!") 
	
	if (parameters$min >= parameters$size)
		stop("min must be less than size!") 
	
	if (parameters$max >= parameters$size)
		stop("max must be less than size!") 
	
	if (parameters$max < parameters$min)
		stop("max can not be less than min!") 
	
	return (parameters)
}

.create.coresubset <- function(x, parameters, subset)
{
	selected <- .jrcall(subset, "getAccessionNamesAsArray") ;
	
	core <- data.frame(row.names = parameters$accessionNames) 
	
	core$selected <- parameters$accessionNames %in% selected
	
	return(core)
}

.create.random.neighourhood(min=NULL, max=NULL) <- function(x, parameters, subset)
{
	neighborhood <- .jnew("org/corehunter/search/RandomSingleNeighborhood", as.integer(min), as.integer(max)) ;

	return(neighborhood)
}

.create.heuristic.neighourhood(min=NULL, max=NULL) <- function(x, parameters, subset)
{
	neighborhood <- .jnew("org/corehunter/search/HeuristicSingleNeighborhood", as.integer(min), as.integer(max)) ;
	
	return(neighborhood)
}

