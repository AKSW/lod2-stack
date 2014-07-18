#!/bin/bash 
#
# if a parameter is given then this is considered a file.

echo "Starting valiant tool..."

CONFIG="/etc/valiant/valiant.properties"
LOG="/var/log/valiant.log"

if [ $# > 0 ] ;  then 
java -Xms512M -Xmx1024M -cp $VALIANT:$VALIANT_XML_LIB -Dvaliant.config="file://$CONFIG" -Dvaliant.log=$LOG -jar valiant-1.0-SNAPSHOT.jar $1 ;
else
java -Xms512M -Xmx2048M -cp $VALIANT:$VALIANT_XML_LIB -Dvaliant.config="file://$CONFIG" -Dvaliant.log=$LOG -jar valiant-1.0-SNAPSHOT.jar  ;
fi


