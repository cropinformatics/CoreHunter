# Scripts for testing corehunter while in development
# 
# Author: Guy Davenport
###############################################################################

library(corehunter)
library(testthat)

x <- read.csv("corehunter/data/bul.csv",header=T) 

core <- coresubset.random(x, minSize=2, maxSize=3)

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

core <- coresubset.random(x, intensity=0.01)
expect_that(nrow (subset (core, core$selected)) == 2, is_true())

core <- coresubset.exhaustive(x, minSize=2, maxSize=3, measure = "MR")

expect_that(nrow (subset (core, core$selected)) >= 2, is_true())
expect_that(nrow (subset (core, core$selected)) <= 3, is_true())

measures = data.frame(c(0.5, 0.5)) 
colnames(measures) <- c("weights")
rownames(measures) <- c("MR", "CE")

core <- coresubset.exhaustive(x, minSize=2, maxSize=3, measure = measures)
