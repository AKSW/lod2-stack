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
 Module: zeitschrift and rezension module
 -->

<xsl:template match="aufsatz | aufsatz-es | rezension">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:if test="@start-seite">
		<bibo:pageStart><xsl:value-of select="@start-seite"/></bibo:pageStart>
	</xsl:if>
	<xsl:if test="@pos-auf-seite">
		<wkd:pagePosition><xsl:value-of select="@pos-auf-seite"/></wkd:pagePosition>
	</xsl:if>
	<xsl:if test="@end-seite">
		<bibo:pageEnd><xsl:value-of select="@end-seite"/></bibo:pageEnd>
	</xsl:if>
	<xsl:if test="@datum">
		<dcterms:created rdf:datatype="{$xsd}date"><xsl:value-of select="fun:dateDe2Iso(string(@datum))"/></dcterms:created>
	</xsl:if>
	<xsl:if test="@beilage">
		<wkd:supplement><xsl:value-of select="@beilage"/></wkd:supplement>
	</xsl:if>
	<xsl:call-template name="rechteinhaber"/>
	<xsl:call-template name="doc-parts-zs"/>
	<xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="aufsatz | rezension" mode="top-level">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:apply-templates select="*" mode="top-level"/>
</xsl:template>

<!-- todo parts of aufsatz-es -->

<xsl:template name="doc-parts-zs">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:for-each-group select="/wkdsc/*//aufsatz-ebene" group-by="name()">
		<xsl:for-each select="current-group()">
			<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)" as="xs:string"/>
			<metalex:fragment rdf:resource="{$uri}"/>
		</xsl:for-each>
	</xsl:for-each-group>
</xsl:template>

<xsl:template match="aufsatz-ebene">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<dcterms:hasPart>
		<metalex:BlockFragment rdf:about="{$uri}">
			<wkd:fragmentType rdf:resource="{$v-base-uri}FragmentType/{name()}"/>
			<xsl:apply-templates select="*">
				<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
			</xsl:apply-templates>
		</metalex:BlockFragment>
	</dcterms:hasPart>
</xsl:template>

<xsl:template match="aufsatz-ebene" mode="top-level">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<xsl:apply-templates select="*" mode="top-level">
		<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
	</xsl:apply-templates>
</xsl:template>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="no" name="euroas_2009_01-02_10" userelativepaths="yes" externalpreview="no" url="..\..\Data\Doctrine\euroas_2009_01-02_10.xml" htmlbaseurl="" outputurl="..\..\result\doc\euroas_2009_01-02_10.rdf" processortype="saxon8"
		          useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="yes" name="KommP_BY_2010_01_17" userelativepaths="yes" externalpreview="no" url="..\..\Data\Doctrine\KommP_BY_2010_01_17.xml" htmlbaseurl="" outputurl="..\..\result\doc\KommP_BY_2010_01_17.rdf" processortype="saxon8"
		          useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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