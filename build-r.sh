#!/bin/sh

# This purpose of this script is to build CoreHunter R package
#
# We assume the availability of mavan, java, and a JDK
# installation for this to work.
#
# Author: Guy Davenport
# Date: June 15, 2013

ROOT=`dirname $0`

function check_requirements {
 hash R 2>&- || { echo >&2 "R must be installed first.  Aborting."; exit 1; }
}

function build_corehunter_r {
    check_requirements
    
    echo "Building Corehunter R"
    
    if [ -d rm $ROOT/corehunter-r/corehunter*.tar.gz ]; then
		rm $ROOT/corehunter-r/corehunter*.tar.gz
    fi
    
    echo "  - copying jar to r-package of current project"
    
    if [ ! -d $ROOT/corehunter-r/corehunter/inst/java ]; then
		mkdir -p $ROOT/corehunter-r/corehunter/inst/java
    fi
    
    if [ -d $ROOT/corehunter-r/corehunter/inst/java/corehunter-cli.jar ]; then
		rm $ROOT/corehunter-r/corehunter/inst/java/corehunter-cli.jar
    fi
    
    cp -f $ROOT/bin/corehunter-cli.jar $ROOT/corehunter-r/corehunter/inst/java

	cd $ROOT/corehunter-r
	
	R CMD build corehunter
}

if [ -f $ROOT/bin/corehunter-cli.jar ]; then
    build_corehunter_r
else
    echo "CoreHunter is not already built. Run build.sh first"
fi

