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

setwd("/Users/daveneti/Repositories/CoreHunter/corehunter-r")
.jaddClassPath("/Users/daveneti/Repositories/CoreHunter/corehunter-r/corehunter/inst/java/corehunter-cli.jar")

source("corehunter/R/utilities.R")
source("corehunter/R/corehunter.R")

x <- read.csv("corehunter/data/bul.csv",header=T) 

###############################################################################
# Random Search
###############################################################################

core <- coresubset.random(x, minSize=2, maxSize=3)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- coresubset.random(x, intensity=0.01)

expect_that(nrow (subset (core, core$selected)) == 2, is_true())

#core <- coresubset.exhaustive(x, minSize=2, maxSize=3, measure = "MR")

#expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
#expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

measures = data.frame(c(0.5, 0.5)) 
colnames(measures) <- c("weights")
rownames(measures) <- c("MR", "CE")

###############################################################################
# Exhaustive Search
###############################################################################

#core <- coresubset.exhaustive(x, minSize=2, maxSize=3, measure = measures)

#expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
#expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# LR Search
###############################################################################

core <- coresubset.lr(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- coresubset.lr(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Semi-LR Search
###############################################################################

core <- coresubset.semiLr(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- coresubset.semiLr(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Forward Search
###############################################################################

core <- coresubset.forward(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- coresubset.forward(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Semi-Forward Search
###############################################################################

core <- coresubset.semiForward(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- coresubset.semiForward(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Backward Search
###############################################################################

core <- coresubset.backward(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- coresubset.backward(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# REMC Search
###############################################################################

core <- coresubset.remc(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Mixed Replica Search
###############################################################################

core <- coresubset.mixedReplica(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Local Search
###############################################################################

core <- coresubset.local(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Steepest Descent Search
###############################################################################

core <- coresubset.steepestDescent(x, minSize=2, maxSize=3, measure = "MR") 

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# MStrat Search
###############################################################################

core <- coresubset.mstrat(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Tabu Search
###############################################################################

core <- coresubset.tabu(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())
