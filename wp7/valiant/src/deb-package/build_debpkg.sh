#!/bin/sh

# $1 the version passed by the maven call

PACKAGE_NAME=valiant
VERSION=$1

echo "Building Debian package for ${MODULE_NAME}"
echo

rm -rf ../../target/deb-pkg
mkdir -p ../../target/deb-pkg

# Extract the tarball to the package workspace
#tar xfz data.tar.gz --directory ../../target/deb-pkg

# copy war file to package workspace
# remove the version in the name
cp ../../tools/* ../../target/deb-pkg/
cp ../../target/${PACKAGE_NAME}-${VERSION}-libraries.zip ../../target/deb-pkg/${PACKAGE_NAME}-${VERSION}.orig.zip
mkdir ../../target/deb-pkg/configuration
cp -r ../../src/configuration/* ../../target/deb-pkg/configuration
# Add the Debian control files
cp -r debian ../../target/deb-pkg
cp Makefile ../../target/deb-pkg
cp -r ../doc ../../target/deb-pkg/doc

# Build the package
cd ../../target/deb-pkg
debuild --check-dirname-level 0 -b
