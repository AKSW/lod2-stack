#!/bin/sh

# $1 the version passed by the maven call

PACKAGE_NAME=lod2api
VERSION=$1

echo "Building Debian package for ${MODULE_NAME}"
echo

rm -rf ../../target/deb-pkg
mkdir -p ../../target/deb-pkg

# Extract the tarball to the package workspace
#tar xfz data.tar.gz --directory ../../target/deb-pkg

# copy war file to package workspace
# remove the version in the name
cp ../../target/$PACKAGE_NAME-$1.war ../../target/deb-pkg/lod2webapi.war
mkdir ../../target/deb-pkg/configuration
cp -r ../../src/configuration/* ../../target/deb-pkg/configuration
# Add the Debian control files
cp -r debian ../../target/deb-pkg
cp  lod2webapi.xml ../../target/deb-pkg

# Build the package
cd ../../target/deb-pkg
debuild --check-dirname-level 0 -b
