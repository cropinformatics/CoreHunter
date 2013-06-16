# Scripts for testing corehunter while in development
# 
# Author: Guy Davenport
###############################################################################

library(rJava)
.jinit() # this starts the JVM

setwd("/Users/daveneti/Repositories/CoreHunter/corehunter-r")
.jaddClassPath("/Users/daveneti/Repositories/CoreHunter/corehunter-r/corehunter/inst/java/corehunter-cli.jar")

source("corehunter/R/corehunter.R")

x <- read.csv("corehunter/data/bul.csv",header=F) 

coresubset.random(x, min=2,max=3)



