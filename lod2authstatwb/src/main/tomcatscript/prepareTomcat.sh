#!/bin/bash

STARTUPDIR=`pwd`
TOMCATCONF="/etc/tomcat6"
#TOMCATCONF="/tmp/tctmp"
TOMCATROOT="/usr/share/tomcat6"
#TOMCATROOT="/tmp/tctmproot"
LIBSLOCATION=$STARTUPDIR/lib

#get a password for the key and truststores
PASSWORD=
while [ -z "$PASSWORD" ]; do
    read -s -p "Please enter a password to use for key- and truststore: " PASSWORD1
    echo
    read -s -p "Re-enter password: " PASSWORD2
    echo
    if [ -z "$PASSWORD1" -o ! "$PASSWORD1" = "$PASSWORD2" ]
    then
	echo "Sorry, try again..."
    else
	PASSWORD=$PASSWORD1
    fi
done


cd $TOMCATCONF
# setup keystore and truststore
echo -n "creating keystore..."
`keytool -genkey -v -alias tomcat -keyalg RSA -validity 3650 -storepass $PASSWORD -keypass $PASSWORD -keystore $TOMCATCONF/tomcat.keystore -dname "CN=demo.lod2.eu, OU=demo, O=lod2" >/dev/null 2>/dev/null`
echo "done"
echo -n "creating truststore..."
`keytool -genkey -v -alias demoKey -keyalg RSA -storetype PKCS12 -storepass $PASSWORD -keystore demo.p12 -dname "CN=demoKey, OU=demo, O=DieOrg" >/dev/null 2>/dev/null`
echo "done"
echo -n "creating server certificate..."
`keytool -export -alias demoKey -keystore demo.p12 -storetype PKCS12 -storepass $PASSWORD -keypass $PASSWORD -rfc -file demo.cer >/dev/null 2>/dev/null`
echo "done"
echo -n "import certificate in truststore..."
`keytool -import -noprompt -file demo.cer -storepass $PASSWORD -keypass $PASSWORD -keystore tomcat.keystore >/dev/null 2>/dev/null`
echo "done"

echo -n "configuring server.xml..."
#check if server already configured correctly
hasWebID= `grep -q com\.turnguard\.webid\.tomcat\.database\.impl\.virtuoso\.VirtuosoWebIDDatabaseFactoryImpl server.xml`
if [ hasWebID ]
then
    echo "done"
    echo "Your server is already configured with a webid resource"
else
#check if already using resources, pick correct xslt accordingly
    RESOURCEINPUT=`grep -q \<\/GlobalNamingResources server.xml`
    if [ $RESOURCEINPUT ]
    then
# no resources yet, use xslt that adds full resources file
	xsltproc -o server.xml $STARTUPDIR/transformNoResources.xslt server.xml
    else
# reuse existing resources yet, simply add new resource
	xsltproc -o server.xml $STARTUPDIR/transformResources.xslt server.xml
    fi
    echo "done"
fi

#add the required libraries to the tomcat instance
echo -n "adding required libraries to /usr/share/tomcat6/lib..."
cp -f $LIBSLOCATION/* $TOMCATROOT/lib
echo "done"

cd $STARTUPDIR

echo -n "uploading the rdf graph holding the user information to virtuoso..."
# need to move to tmp as isql-vt may not have access to the current directory
cp $STARTUPDIR/tomcat-users.rdf /tmp/tomcat-users.rdf
isql-vt -U dba -P dba -H localhost < uploadUserGraph.sql # > /dev/null
echo "done"

echo "your tomcat server should now be ready to accept a secured application"
