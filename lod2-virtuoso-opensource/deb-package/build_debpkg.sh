#!/bin/sh

# $1 the version passed by the maven call

PACKAGE_NAME=lod2-virtuoso-opensource
VERSION=$1

debuild --check-dirname-level 0 -b
