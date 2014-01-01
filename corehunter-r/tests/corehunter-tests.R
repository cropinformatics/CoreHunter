# Scripts for testing corehunter while in development
# 
# Author: Guy Davenport
###############################################################################

###############################################################################
# Setup
###############################################################################

library(rJava)

.jinit() # this starts the JVM

library(testthat)

packageDirectory <- "/Users/daveneti/Repositories/CoreHunter/corehunter-r" ;

setwd(packageDirectory)
.jaddClassPath(file.path(packageDirectory, "corehunter", "inst", "java", "corehunter.jar")) 

source(file.path("corehunter", "R", "utilities.R"))
source(file.path("corehunter", "R", "corehunter.R")) 

x <- read.csv(file.path("corehunter", "data", "bul.csv"),header=T) 

measures = data.frame(c(0.5, 0.5)) 
colnames(measures) <- c("weights")
rownames(measures) <- c("MR", "CE")

source(file.path("corehunter", "tests", "corehunter-tests.R"))

library(roxygen2)

roxygenize(file.path(packageDirectory, "corehunter"), roclets = "rd")
