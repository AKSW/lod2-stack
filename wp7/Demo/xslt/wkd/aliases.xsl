<?xml version='1.0'?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xs="http://www.w3.org/2001/XMLSchema" 
 exclude-result-prefixes="xsl xs">

 <!--
 Project LOD2 - Project number 27943
 Work Package - 7
 Author Johan De Smedt
 Description: extract metadata, classification and links from WKD XML 
 Module:  ALIAS MANAGEMENT 
aliases can be used in attribute values (e.g. rdf:resource="{$wkd}xyz"
-->
<xsl:variable name="wkd" select="'http://schema.wolterskluwer.de/'" as="xs:string"/>
<xsl:variable name="dc" select="'http://purl.org/dc/elements/1.1/'" as="xs:string"/>
<xsl:variable name="dcterms" select="'http://purl.org/dc/terms/'" as="xs:string"/>
<xsl:variable name="rdfs" select="'http://www.w3.org/2000/01/rdf-schema#'" as="xs:string"/>
<xsl:variable name="rdf" select="'http://www.w3.org/1999/02/22-rdf-syntax-ns#'" as="xs:string"/>
<xsl:variable name="owl" select="'http://www.w3.org/2002/07/owl#'" as="xs:string"/>
<xsl:variable name="skos" select="'http://www.w3.org/2004/02/skos/core#'" as="xs:string"/>
<xsl:variable name="xl" select="'http://www.w3.org/2008/05/skos-xl#'" as="xs:string"/>
<xsl:variable name="xsd" select="'http://www.w3.org/2001/XMLSchema#'" as="xs:string"/>
<xsl:variable name="bibo" select="'http://purl.org/ontology/bibo/'" as="xs:string"/>
<xsl:variable name="foaf" select="'http://xmlns.com/foaf/0.1/'" as="xs:string"/>
<xsl:variable name="xml" select="'http://www.w3.org/XML/1998/namespace'" as="xs:string"/>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2007. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios/>
	<MapperMetaTag>
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
		<MapperBlockPosition></MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->