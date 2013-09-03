#!/bin/bash

if [ $# -lt 2 ]
then
    # need more info
    echo this script requires at least two arguments
    echo - the first one is the regex to use to select the graphs to add to the group, e.g. *localhost*. This regex is applied to all non system graph URIs in the database
    echo - the second one is the URI of the group to create.
    echo - A third optional argument is the location of the service and defaults to http://localhost/lod2webapi.
    exit
else
    # continuing
    if [ $# -ge 3 ]
    then 
	service=$3
    else
	service=http://localhost/lod2webapi
    fi
fi

regex="$1"
groupname="$2"

if [ "$1" == "-r" ]
then
#going for dataset removal here
    echo -n removing the group graph $groupname...
    curl -s $service/drop_group --data "group=$groupname" &> /dev/null
    curl -s $service/remove_graphs --data "graphs=<$groupname>" &> /dev/null
    echo done
else
#adding a new dataset here
    echo -n fetching graphs from virtuoso...
    graphs=`curl -s $service/graphsregex -G --data "regex=$regex&all=true"`
    graphs=`echo $graphs| sed -e "s/.*resultList\"\:\[//" | sed -e "s/].*//" | sed -e "s/\"//g" |sed -e "s/,/> </g" `
    echo done

    count=0
    for g in $graphs ; do
	count=$(( $count+1 ))
    done
    echo found $count graphs matching the given pattern

    graphs="<$graphs>"

    echo -n building graph group...
    curl -s $service/build_group --data "group=$groupname&graphs=$graphs" &> /dev/null
    echo done

    echo -n publishing the graph group...
    curl -s $service/register_graph --data "graph=$groupname" &> /dev/null
    echo done
fi
