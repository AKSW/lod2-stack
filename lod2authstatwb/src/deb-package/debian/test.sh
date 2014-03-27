#!/bin/sh

    username=`grep Username /etc/virtuoso-opensource/bd.ini | sed -i -e "s/Username.*=//"`
    password=`grep Password /etc/virtuoso-opensource/bd.ini | sed -i -e "s/Password.*=//"`
    address=`grep Address /etc/virtuoso-opensource/bd.ini | sed -e "s/Address.*=//"`

VIRTUOSOVERSION=virtuoso-opensource

env ODBCINI=/etc/$VIRTUOSOVERSION/bd.ini isql-iodbc DSN=LocalVirtDBA < test.sql

connect() {
    c=`env ODBCINI=/etc/$VIRTUOSOVERSION/bd.ini isql-iodbc DSN=LocalVirtDBA < test.sql`;
    if [ -z "$c" ] ; then
	return 1;
    else 
	return 0;
    fi;
}


	attempts=0;
	until connect || [ "$attempts" -gt 5 ] ; do
		sudo service ${VIRTUOSOVERSION} start ;
		wait ;
		sleep 1;
		attempts=$(($attempts+1));
        done;
