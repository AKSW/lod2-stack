#!/bin/bash
#

echo "Starting valiant tool..."

inputFile=input.xml
configFile="`pwd`/valiant.properties"
logFile="log/valiant.log"

$JAVA_HOME/bin/java -Xms512M -Xmx1024M -Dvaliant.config="file://$configFile" -Dvaliant.log=$logFile -jar valiant-1.0-SNAPSHOT.jar $inputFile

