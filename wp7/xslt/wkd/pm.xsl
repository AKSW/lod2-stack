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

<xsl:import href="content.xsl"/>

<xsl:output encoding="UTF-8"/>

 <!--
 Project LOD2 - Project number 27943
 Work Package - 7
 Author Johan De Smedt
 Description: extract metadata, classification and links from WKD XML 
 Module: pressemitteilung module
 -->

<xsl:template match="pressemitteilung">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<wkd:pressReleaseType>
		<skos:Concept rdf:about="{$v-base-uri}{fun:deu2eng('pressemitteilung/typ')}/{@typ}"/>
	</wkd:pressReleaseType>
	<xsl:if test="string-length(@datum) &gt; 0">
		<dcterms:issued><xsl:value-of select="fun:dateDe2Iso(string(@datum))"/></dcterms:issued>
	</xsl:if>
	<xsl:if test="@pm-nr">
		<bibo:number><xsl:value-of select="@pm-nr"/></bibo:number>
	</xsl:if>
	<xsl:call-template name="rechteinhaber"/>
	<xsl:if test="string-length(@bezugsquelle) &gt; 0">
		<dcterms:source>
			<bibo:ReferenceSource>
				<bibo:identifier><xsl:value-of select="@bezugsquelle"/></bibo:identifier>
			</bibo:ReferenceSource>
		</dcterms:source>
	</xsl:if>
	<xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="pressemitteilung" mode="top-level">
	<xsl:apply-templates select="*" mode="top-level"/>
</xsl:template>

<!-- titel -->
<xsl:template match="pressemitteilung/titel">
	<dcterms:title><xsl:apply-templates select="." mode="plain-literal"/></dcterms:title>
</xsl:template>

<xsl:template match="pm-quelle">
	<xsl:apply-templates select="verweis-url">
		<xsl:with-param name="referenceType" as="xs:string" tunnel="yes" select="name()"/>
	</xsl:apply-templates>
	<xsl:apply-templates select="organisation">
		<xsl:with-param name="namespace" select="$dcterms" as="xs:string"/>
		<xsl:with-param name="property" select="'source'" as="xs:string"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="pm-entscheidung">
	<xsl:apply-templates select="verweis-es">
		<xsl:with-param name="referenceType" as="xs:string" tunnel="yes" select="name()"/>
	</xsl:apply-templates>
</xsl:template>

<!-- Set base reference -->
<xsl:template match="pressemitteilung/normenkette">
	<xsl:variable name="refType" as="xs:string" select="name()"/>
	<xsl:apply-templates select="verweis-vs">
		<xsl:with-param name="referenceType" tunnel="yes" select="$refType" as="xs:string"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="pressemitteilung/normenkette" mode="top-level"/>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="pm-lag_bw_2006-06-12_4-sa-68-05_pm" userelativepaths="yes" externalpreview="no" url="..\..\Data\pm\lag_bw_2006-06-12_4-sa-68-05_pm.xml" htmlbaseurl="" outputurl="..\..\result\pm\lag_bw_2006-06-12_4-sa-68-05_pm.rdf"
		          processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bSchemaAware" value="false"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="xsltVersion" value="2.0"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
	</scenarios>
	<MapperMetaTag>
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
		<MapperBlockPosition></MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->