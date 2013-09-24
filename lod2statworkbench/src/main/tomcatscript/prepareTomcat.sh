#!/bin/bash

STARTDIR=`pwd`
SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
TOMCATCONF="/etc/tomcat6"
TOMCATROOT="/usr/share/tomcat6"
VIRTUOSOVERSION=virtuoso-opensource-6.1
LIBSLOCATION=$SCRIPTDIR/lib

#get a password for the key and truststores
PASSWORD=
while [ -z "$PASSWORD" ]; do
    echo
    read -s -p "Please enter a password to use for key- and truststore: " PASSWORD1
    echo
    read -s -p "Re-enter password: " PASSWORD2
    echo
    if [ -z "$PASSWORD1" -o ! "$PASSWORD1" = "$PASSWORD2" ]
    then
	echo "Sorry, try again..."
    else
	if [ ${#PASSWORD1} -lt 6 ]
	then
	    echo "Sorry, the password must be at least 6 characters long..."
	else
	    PASSWORD=$PASSWORD1
	fi
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
if grep -q VirtuosoWebIDDatabaseFactoryImpl server.xml
then
    echo "done"
    echo "Your server is already configured with a webid resource"
else
    echo
    read -s -p "Please enter the dba user for virtuoso: " USERVIRT
    echo
    read -s -p "Please enter the dba password for virtuoso: " PWDVIRT
    echo
    subst1="s/USERVIRT/$USERVIRT/g"
    subst2="s/PWDVIRT/$PWDVIRT/g"
    subst3="s/PWDTRUST/$PASSWORD/g"
    sed -e $subst1 -i $SCRIPTDIR/transformNoResources.xslt
    sed -e $subst2 -i $SCRIPTDIR/transformNoResources.xslt
    sed -e $subst3 -i $SCRIPTDIR/transformNoResources.xslt
    sed -e $subst1 -i $SCRIPTDIR/transformResources.xslt
    sed -e $subst2 -i $SCRIPTDIR/transformResources.xslt
    sed -e $subst3 -i $SCRIPTDIR/transformResources.xslt

#check if already using resources, pick correct xslt accordingly
    if grep -q \<\/GlobalNamingResources server.xml 
    then
# no resources yet, use xslt that adds full resources file
	xsltproc -o server.xml $SCRIPTDIR/transformResources.xslt server.xml
    else
# reuse existing resources yet, simply add new resource
	xsltproc -o server.xml $SCRIPTDIR/transformNoResources.xslt server.xml
    fi
    echo "done"
fi

#add the required libraries to the tomcat instance
echo -n "adding required libraries to /usr/share/tomcat6/lib..."
cp -f $LIBSLOCATION/* $TOMCATROOT/lib
echo "done"

echo -n "uploading the rdf graph holding the user information to virtuoso..."
# need to move to tmp as isql-vt may not have access to the current directory
cp $SCRIPTDIR/tomcat-users.rdf /tmp/tomcat-users.rdf
env ODBCINI=/etc/$VIRTUOSOVERSION/bd.ini isql-iodbc DSN=LocalVirtDBA < $SCRIPTDIR/uploadUserGraph.sql
echo "done"

cd $STARTDIR

echo "your tomcat server should now be ready to accept a secured application"
