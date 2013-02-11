:: The purpose of this script is to build CoreHunter and CoreAnalyser
:: on WINDOWS machines. The jar file for each will be placed in a 'bin'
:: subdirectory of this project.
::
:: We assume the availability of maven, java, and a JDK
:: installation for this to work.
::
:: Author: Herman De Beukelaer
:: Date: Feb 11, 2013


echo "Building Core Hunter"

:: build package using Maven
call mvn package

echo "Copying jar files to bin directory"

:: remove bin directory
IF EXIST bin rd \s\q bin

:: create new empty bin directory
md bin

:: copy jar files to bin directory
copy corehunter-cli\target\corehunter-cli-1.0-SNAPSHOT-jar-with-dependencies.jar bin\corehunter-cli.jar
copy coreanalyser-cli\target\coreanalyser-cli-1.0-SNAPSHOT-jar-with-dependencies.jar bin\coreanalyser-cli.jar


echo "Core Hunter successfully built"