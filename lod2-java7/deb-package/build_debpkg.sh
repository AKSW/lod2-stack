#!/bin/sh

# $1 the version passed by the maven call

PACKAGE_NAME=lod2-java7
VERSION=$1

debuild --check-dirname-level 0 -b
