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


#!/bin/sh

# This purpose of this script is to build CoreHunter and CoreAnalyser.
# The jar file for each will be place in a 'bin' subdirectory of this
# project.
#
# We assume the availability of mavan, java, and a JDK
# installation for this to work.
#
# Author: Chris Thachuk, Guy Davenport
# Date: Feb 3, 2011

ROOT=`dirname $0`
VERSION="2.0-SNAPSHOT"

function check_requirements {
 hash mvn 2>&- || { echo >&2 "Maven must be installed first.  Aborting."; exit 1; }
 hash javac 2>&- || { echo >&2 "The Java JDK must be installed first.  Aborting."; exit 1; }
 hash java 2>&- || { echo >&2 "The Java Runtime must be installed first.  Aborting."; exit 1; }
}

function build_corehunter {
    check_requirements
    
    echo "Building Corehunter Java"

    mvn clean package -DskipTests
}

function build_corehunter_r {
    check_requirements
    
    echo "Building Corehunter R"
    
    mkdir -p $ROOT/corehunter-r/corehunter/inst/java
    
    cp -f $ROOT/corehunter-cli/target/corehunter-cli-2.0-SNAPSHOT-jar-with-dependencies.jar $ROOT/corehunter-r/corehunter/inst/java/corehunter.jar
  
    if [ -f $ROOT/corehunter-r/corehunter*.tar.gz ]; then
		rm $ROOT/corehunter-r/corehunter*.tar.gz
    fi
    
    cd $ROOT/corehunter-r 
	
	R CMD build corehunter
}

build_corehunter
    
if [ -f $ROOT/corehunter-cli/target/corehunter-cli-2.0-SNAPSHOT-jar-with-dependencies.jar ]; then
    build_corehunter_r
else
	echo "$ROOT/corehunter-cli/target/corehunter-cli-2.0-SNAPSHOT-jar-with-dependencies.jar not found"
    echo "CoreHunter Java was not built. R version can not be built"
fi
