#!/usr/bin/env bash
# @(#) takes a ckan dataset name and generates a debian package from it
#
# all packages: http://ckan.net/api/1/search/package?tags=format-rdf&limit=1000

datasetName="$1"

### start
if [ "$datasetName" == "" ];
then
    echo "Usage: `basename $0` {ckan dataset name}"
    exit 1;
fi

PREFIXES="PREFIX moat:<http://moat-project.org/ns#> PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dct:<http://purl.org/dc/terms/> PREFIX dcat:<http://www.w3.org/ns/dcat#> "
today=$(date +%Y%m%d)
package="ckan-dataset-$datasetName"
dirname="$package-$today"

echo "Create a new package directory: $dirname"
rm -rf $dirname
mkdir -p $dirname
cd $dirname

echo -n "Fetch dataset JSON and search for id: "
jsonuri="http://ckan.net/api/2/rest/package/$datasetName"
wget -q $jsonuri -O ckan.json || exit 1
datasetId=$(php5 -r "\$json = json_decode(file_get_contents('ckan.json'));echo \$json->id;")
echo "$datasetId"

echo -n "Fetch dataset RDF and search for dump URL: "
rdfuri="http://semantic.ckan.net/record/$datasetId.rdf"
wget -q $rdfuri -O ckan.rdf || exit 1
dumpurl=$(roqet -q -i sparql -e "${PREFIXES} SELECT ?url WHERE {?s  a dcat:Distribution. ?s dcat:accessURL ?url. ?s dct:format ?f. ?f moat:taggedWithTag ?t. ?t moat:name \"rdf+xml\"}" -D ckan.rdf -r csv | tail -1 | cut -d "(" -f 2 | cut -d ")" -f 1)
echo $dumpurl
cwm ckan.rdf --n3 >ckan.n3

echo -n "Try to download the dump: "
wget -q $dumpurl -O data.rdf || exit 1
echo "Done"

echo -n "Validate the dump: "
rapper --count data.rdf 2>data.rdf.count || exit 1
echo -n "valid "
count=$(cat data.rdf.count | tail -1 | cut -d " " -f 4) 
echo "(found $count triple)"
rm data.rdf.count

cp -R ../debian.stub debian
cd debian

echo "Replace package specific values"
grep -rl "%%package%%" . | xargs sed -i "s/%%package%%/${package}/g"
grep -rl "%%name%%" . | xargs sed -i "s/%%name%%/${datasetName}/g"
grep -rl "%%date%%" . | xargs sed -i "s/%%date%%/${today}/g"
grep -rl "%%count%%" . | xargs sed -i "s/%%count%%/${count}/g"

echo -n "Build the package: "
debuild >../$package.log || exit 1
echo "done"


