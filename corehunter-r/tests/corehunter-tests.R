# Scripts for testing corehunter while in development
# 
# Author: Guy Davenport
###############################################################################

library(rJava)
.jinit() # this starts the JVM

setwd("/Users/daveneti/Repositories/CoreHunter/corehunter-r")
.jaddClassPath("/Users/daveneti/Repositories/CoreHunter/corehunter-r/corehunter/inst/java/corehunter-cli.jar")

source("corehunter/R/utilities.R")
source("corehunter/R/corehunter.R")

x <- read.csv("corehunter/data/bul.csv",header=T) 

core <- coresubset.random(x, minSize=2, maxSize=3)
