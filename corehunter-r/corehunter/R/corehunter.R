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

coresubset.random <- function(x, min=NULL, max=NULL, intensity=NULL) 
{
	parameters <- .create.parameters(x, min, max, intensity)

	subset <- .jrcall("org/corehunter/search/CoreSubsetSearch", "randomSearch", parameters$collection, as.integer(parameters$min), as.integer(parameters$max))
	
	return(.create.coresubset(subset))
}

