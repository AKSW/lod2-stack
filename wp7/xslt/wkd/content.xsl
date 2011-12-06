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
 exclude-result-prefixes="xsl xs fun data"
>

 <!--
 Project LOD2 - Project number 27943
 Work Package - 7
 Author Johan De Smedt
 Description: extract metadata, classification and links from WKD XML 
 Module: Content transformation - ROUGH XML-literal constructions
 -->

<xsl:template match="anmerkung" mode="xml-literal">
	<xhtml:div xml:lang="{fun:language(@sprache)}">
		<xsl:apply-templates mode="xml-literal"/>
	</xhtml:div>
</xsl:template>

<!-- vs-vertragsparteien is part of "vorspann" content -->
<xsl:template match="vs-vertragsparteien" mode="xml-literal">
	<xhtml:div>
		<xsl:apply-templates mode="xml-literal"/>
	</xhtml:div>
</xsl:template>

<xsl:template match="absatz" mode="xml-literal">
	<xhtml:p>
		<xsl:apply-templates mode="xml-literal"/>
	</xhtml:p>
</xsl:template>

<xsl:template match="absatz-rechts" mode="xml-literal">
	<xhtml:p>
		<xsl:for-each select="*">
			<xsl:apply-templates select="." mode="xml-literal"/>
			<xsl:if test="not(last())">
				<xhtml:br/>
			</xsl:if>
		</xsl:for-each>
	</xhtml:p>
</xsl:template>

<xsl:template match="abbildung-block | objekt-block | zitat-block | container-auspraegung | tabelle" mode="xml-literal">
	<xhtml:div>
		<xsl:apply-templates mode="xml-literal"/>
	</xhtml:div>
</xsl:template>

<xsl:template match="satz" mode="xml-literal">
	<xsl:value-of select="string(.)"/>
	<xhtml:br/>
</xsl:template>

<xsl:template match="table" mode="xml-literal">
	<xsl:for-each select="tgroup">
		<xhtml:table>
			<xsl:apply-templates select="thead | tfoot | tbody" mode="xml-literal"/>
		</xhtml:table>
	</xsl:for-each>
</xsl:template>

<xsl:template match="thead" mode="xml-literal">
	<xhtml:thead>
		<xsl:apply-templates select="row" mode="xml-literal"/>
	</xhtml:thead>
</xsl:template>

<xsl:template match="tfoot" mode="xml-literal">
	<xhtml:tfoot>
		<xsl:apply-templates select="row" mode="xml-literal"/>
	</xhtml:tfoot>
</xsl:template>

<xsl:template match="tbody" mode="xml-literal">
	<xhtml:tbody>
		<xsl:apply-templates select="row" mode="xml-literal"/>
	</xhtml:tbody>
</xsl:template>

<xsl:template match="row" mode="xml-literal">
	<xhtml:tr>
		<xsl:apply-templates select="entry" mode="xml-literal"/>
	</xhtml:tr>
</xsl:template>

<xsl:template match="entry" mode="xml-literal">
	<xhtml:td>
		<xsl:apply-templates mode="xml-literal"/>
	</xhtml:td>
</xsl:template>

<xsl:template match="liste | liste-auto" mode="xml-literal">
	<xhtml:ul>
		<xsl:apply-templates select="li" mode="xml-literal"/>
	</xhtml:ul>
</xsl:template>

<xsl:template match="li | li-auto" mode="xml-literal">
	<xhtml:li>
		<xsl:apply-templates mode="xml-literal"/>
	</xhtml:li>
</xsl:template>

<xsl:template match="hoch" mode="xml-literal">
	<xhtml:sup><xsl:value-of select="."/></xhtml:sup>
</xsl:template>

<xsl:template match="tief" mode="xml-literal">
	<xhtml:sub><xsl:value-of select="."/></xhtml:sub>
</xsl:template>

<xsl:template match="sprache" mode="xml-literal">
	<xhtml:span xml:lang="{fun:language(@sprache)}"><xsl:apply-templates mode="xml-literal"/></xhtml:span>
</xsl:template>

<xsl:template match="* | text()" mode="xml-literal">
	<xsl:value-of select="string(.)"/>
</xsl:template>

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