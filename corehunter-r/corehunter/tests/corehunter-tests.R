# Scripts for testing corehunter while in development
# 
# Author: Guy Davenport
###############################################################################

library(corehunter)
library(testthat)

x <- read.csv(file.path("corehunter", "data", "bul.csv"),header=T) 

measures = data.frame(c(0.5, 0.5)) 
colnames(measures) <- c("weights")
rownames(measures) <- c("MR", "CE")

###############################################################################
# Exhaustive Search
###############################################################################

#core <- exhaustiveSearch(x, minSize=2, maxSize=3, measure = "MR")

#expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
#expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

#core <- exhaustiveSearch(x, minSize=2, maxSize=3, measure = measures)

#expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
#expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# LR Search
###############################################################################

core <- lrSearch(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- lrSearch(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Semi-LR Search
###############################################################################

core <- semiLrSearch(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- semiLrSearch(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Forward Search
###############################################################################

core <- forwardSearch(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- forwardSearch(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Semi-Forward Search
###############################################################################

core <- semiForwardSearch(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- semiForwardSearch(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Backward Search
###############################################################################

core <- backwardSearch(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- backwardSearch(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# REMC Search
###############################################################################

core <- remcSearch(x, minSize=2, maxSize=3, measure = "MR", runtime = 1)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- remcSearch(x, minSize=2, maxSize=3, measure = measures, runtime = 1)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())


###############################################################################
# Mixed Replica Search
###############################################################################

core <- mixedReplicaSearch(x, minSize=2, maxSize=3, measure = "MR", runtime = 1)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- mixedReplicaSearch(x, minSize=2, maxSize=3, measure = measures, runtime = 1)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Local Search
###############################################################################

core <- localSearch(x, minSize=2, maxSize=3, measure = "MR", runtime = 1)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- localSearch(x, minSize=2, maxSize=3, measure = measures, runtime = 1)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Steepest Descent Search
###############################################################################

core <- steepestDescentSearch(x, minSize=2, maxSize=3, measure = "MR") 

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- steepestDescentSearch(x, minSize=2, maxSize=3, measure = measures) 

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# MStrat Search
###############################################################################

core <- mstratSearch(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- mstratSearch(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

###############################################################################
# Tabu Search
###############################################################################

core <- tabuSearch(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- tabuSearch(x, minSize=2, maxSize=3, measure = measures)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())