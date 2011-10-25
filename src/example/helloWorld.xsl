<?xml version='1.0'?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:fun="http://local-function/"
 xmlns:data="http://local-data/"
 xmlns:wkd="http://schema.wolterskluwer.de/"
 xmlns:xhtml="http://www.w3.org/1999/xhtml"
 xmlns:metalex="http://www.metalex.eu/metalex/2008-05-02#"
 xmlns:dcterms="http://purl.org/dc/terms/" 
 xmlns:dc="http://purl.org/dc/elements/1.1/" 
 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
 xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
 xmlns:owl="http://www.w3.org/2002/07/owl#" 
 xmlns:skos="http://www.w3.org/2004/02/skos/core#" 
 xmlns:xl="http://www.w3.org/2008/05/skos-xl#"
 xmlns:bibo="http://purl.org/ontology/bibo/"
 xmlns:foaf="http://xmlns.com/foaf/0.1/"
 xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
 xmlns:xs="http://www.w3.org/2001/XMLSchema" 
 exclude-result-prefixes="xsl xs fun data">

 <!--
 Project LOD2 - Project number 27943
 Work Package - 7
 Author Johan De Smedt
 Description: extract metadata, classification and links from WKD XML 
 Module: root wrapper module
 -->

<!--
Modes used by the template
- top-level
  top level output is generated in the rdf:RDF wrapper.
  typically used when axioms are generated on the produced statements.
  e.g. to set priority on subject tagging of content parts.
- build-hierarchy
  used for global search on stichwort (STW) ans inline-stichwort to build the stichwort graph 
  (not considering the document - stichwort relationship)
  Typically an STW is given with its hierarchy and synonyms.
  - templates in build-hierarchy reconstruct this structure in SKOS and assign concpt URI
  - top-level mode templates tag an ellement with the most specific STW SKOS concept (occsionally adding priority).
- xml-literal
  Build an RDFs XML-Literal value
- plain-literal
  Build an RDFs plain literal
-->
<!--
Tunnel parameters used
- r-uri
  the document (resource) uri
  Note: one file may contain a sequence of documents.
- p-uri
  the smalest containing document part uri
- referenceType
  the type of reference made to another resource
- token, ns, name
  used for building a hierarchy of specialisations of skos:Concepts
  token: splits a chain of concept terms
  ns: namesapce of the type of term
  name: class/type name of the type of term
-->


<!-- OUTPUT STREAM DEFINITIONS --> 
<xsl:output method="xml" encoding="UTF-8" indent="yes" use-character-maps="NBSP"/>

<xsl:character-map name="NBSP">
	<xsl:output-character character="&#160;" string=" "/>
</xsl:character-map>


    <xsl:variable name="uc">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
    <xsl:variable name="lc">abcdefghijklmnopqrstuvwxyz</xsl:variable>

    <xsl:template match="/">
	<rdf:RDF 
             xmlns:rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
             xmlns:dc  = "http://purl.org/dc/elements/1.1/"
             xmlns:wkd = "http://data.wkd.de/"
             xmlns:wp7 = "http://wp7.lod2.eu/"
             >
            <rdf:Description rdf:about="helloworld">
              <dc:title>Hello World</dc:title>
            </rdf:Description>
	</rdf:RDF>
    </xsl:template>

</xsl:stylesheet>
