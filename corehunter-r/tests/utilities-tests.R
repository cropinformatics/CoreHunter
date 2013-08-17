# Scripts for testing corehunter while in development
# 
# Author: Guy Davenport
###############################################################################

library(rJava)
library(testthat)

.jinit() # this starts the JVM

setwd("/Users/daveneti/Repositories/CoreHunter/corehunter-r")
.jaddClassPath("/Users/daveneti/Repositories/CoreHunter/corehunter-r/corehunter/inst/java/corehunter-cli.jar")

source("corehunter/R/utilities.R")
source("corehunter/R/corehunter.R")

measureNames <- .jrcall("org/corehunter/measures/MeasureFactory", "getMeasureNames") ;

for (measure in measureNames)
{
	objectiveFunction <- .create.objectiveFunction(measure, 10)  
	
	name <- .jrcall(objectiveFunction, "getName")
	
	expect_that(measure == name, is_true())
}

df = data.frame(c(0.1, 0.1, 0.1, 0.1, 0.2, 0.2, 0.2)) 
colnames(df) <- c("weights")
rownames(df) <- c("MR", "CE", "SH", "HE", "NE", "PN", "CV")

objectiveFunction <- .create.objectiveFunction(df, collectionSize = 10)  