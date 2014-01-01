# Utility R test functions for corehunter
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

library(corehunter)
library(testthat)

measureNames <- .jrcall("org/corehunter/measures/MeasureFactory", "getMeasureNames") ;

for (measure in measureNames)
{
	objectiveFunction <- .create.objectiveFunction(measure, 10)  
	
	name <- .jrcall(objectiveFunction, "getName")
	
	expect_that(measure == name, is_true())
}

