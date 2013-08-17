# Scripts for testing corehunter while in development
# 
# Author: Guy Davenport
###############################################################################

library(rJava)

.jinit() # this starts the JVM

library(testthat)

setwd("/Users/daveneti/Repositories/CoreHunter/corehunter-r")
.jaddClassPath("/Users/daveneti/Repositories/CoreHunter/corehunter-r/corehunter/inst/java/corehunter-cli.jar")

source("corehunter/R/utilities.R")
source("corehunter/R/corehunter.R")

x <- read.csv("corehunter/data/bul.csv",header=T) 

core <- coresubset.random(x, minSize=2, maxSize=3)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

#core <- coresubset.random(x, intensity=0.01)

#core <- coresubset.exhaustive(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

measures = data.frame(c(0.5, 0.5)) 
colnames(measures) <- c("weights")
rownames(measures) <- c("MR", "CE")

#core <- coresubset.exhaustive(x, minSize=2, maxSize=3, measure = measures)

core <- coresubset.lr(x, minSize=2, maxSize=3, measure = "MR")

core <- coresubset.lr(x, minSize=2, maxSize=3, measure = measures)

core <- coresubset.semiLr(x, minSize=2, maxSize=3, measure = measures)

core <- coresubset.forward(x, minSize=2, maxSize=3, measure = measures)

core <- coresubset.semiForward(x, minSize=2, maxSize=3, measure = measures)

core <- coresubset.backward(x, minSize=2, maxSize=3, measure = measures)

