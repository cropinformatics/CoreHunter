library(corehunter)
library(testthat)

measureNames <- .jrcall("org/corehunter/measures/MeasureFactory", "getMeasureNames") ;

for (measure in measureNames)
{
	objectiveFunction <- .create.objectiveFunction(measure, 10)  
	
	name <- .jrcall(objectiveFunction, "getName")
	
	expect_that(measure == name, is_true())
}

