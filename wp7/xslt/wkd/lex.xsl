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
 Module: lexikon module
 -->

<!-- todo
all
 -->

<xsl:template match="lexikon">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<rdf:type rdf:resource="{$wkd}TopicMap"/>
	<rdf:type rdf:resource="{$skos}ConceptScheme"/>
	<xsl:if test="string-length(@rechteinhaber) &gt; 0">
		<bibo:owner>
			<dcterms:Agent>
				<rdf:type rdf:resource="{$skos}Concept"/>
				<skos:notation><xsl:value-of select="@rechteinhaber"/></skos:notation>
			</dcterms:Agent>
		</bibo:owner>
	</xsl:if>
	<xsl:call-template name="doc-parts-lex"/>
	<xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="lexikon" mode="top-level">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:apply-templates select="*" mode="top-level"/>
</xsl:template>

<xsl:template match="lexikon/lexikon-ebene">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<dcterms:hasPart rdf:parseType="Resource">
		<rdf:type rdf:resource="{$skos}Collection"/>
		<xsl:apply-templates select="*"/>
	</dcterms:hasPart>
</xsl:template>

<xsl:template match="lexikon-ebene/lexikon-ebene">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<skos:member rdf:parseType="Resource">
		<rdf:type rdf:resource="{$skos}Collection"/>
		<xsl:apply-templates select="*"/>
	</skos:member>
</xsl:template>

<xsl:template match="lexikon-ebene" mode="top-level"/>

<xsl:template match="lexikon/lexikon-eintrag">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="le-uri" as="xs:string" select="concat($r-uri,'#',fun:lex-eintrag-id(.))"/>
	<skos:hasTopConcept>
		<xsl:call-template name="handle-lex-eintrag">
			<xsl:with-param name="le-uri" select="$le-uri" as="xs:string"/>
		</xsl:call-template>
	</skos:hasTopConcept>
</xsl:template>

<xsl:template match="lexikon-ebene/lexikon-eintrag">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:param name="p-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="le-uri" as="xs:string" select="concat($r-uri,'#',fun:lex-eintrag-id(.))"/>
	<skos:member>
		<xsl:if test="not($p-uri = $r-uri)">
			<skos:broader rdf:resource="{$p-uri}"/>
		</xsl:if>
		<xsl:call-template name="handle-lex-eintrag">
			<xsl:with-param name="le-uri" select="$le-uri" as="xs:string"/>
		</xsl:call-template>
	</skos:member>
</xsl:template>

<xsl:template match="lexikon-eintrag/lexikon-eintrag">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="le-uri" as="xs:string" select="concat($r-uri,'#',fun:lex-eintrag-id(.))"/>
	<skos:narrower>
		<xsl:call-template name="handle-lex-eintrag">
			<xsl:with-param name="le-uri" select="$le-uri" as="xs:string"/>
		</xsl:call-template>
	</skos:narrower>
</xsl:template>

<!-- context is assumed to be lexikon-eintrag -->
<xsl:template name="handle-lex-eintrag">
	<xsl:param name="le-uri" as="xs:string"/>
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:param name="p-uri" as="xs:string" tunnel="yes"/>
	<wkd:Topic rdf:about="{$le-uri}">
		<xsl:if test="parent::lexikon-ebene">
			<xsl:if test="not($p-uri = $r-uri)">
				<skos:broader rdf:resource="{$p-uri}"/>
			</xsl:if>
		</xsl:if>
		<rdf:type rdf:resource="{$skos}Concept"/>
		<skos:prefLabel xml:lang="{fun:language(@sprache)}">
			<xsl:value-of select="string(lexikon-begriff)"/>
		</skos:prefLabel>
		<xsl:if test="lexikon-eintrag-text">
			<skos:definition rdf:parseType="Literal">
				<xsl:apply-templates select="lexikon-eintrag-text" mode="xml-literal"/>
			</skos:definition>
		</xsl:if>
		<xsl:apply-templates select="*">
			<xsl:with-param name="p-uri" as="xs:string" select="$le-uri" tunnel="yes"/>
		</xsl:apply-templates>
	</wkd:Topic>
</xsl:template>

<xsl:template match="lexikon-eintrag" mode="top-level"/>

<xsl:template name="doc-parts-lex">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<!--xsl:for-each-group select="/wkdsc/(beitrag | beitrag-rn)//(beitrag-ebene | beitrag-rn-ebene)" group-by="name()">
		<xsl:for-each select="current-group()">
			<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)" as="xs:string"/>
			<metalex:fragment rdf:resource="{$uri}"/>
		</xsl:for-each>
	</xsl:for-each-group-->
</xsl:template>

<xsl:function name="fun:lex-eintrag-id" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:value-of select="fun:percentEncode(if ($e/@begriff-normiert) then string($e/@begriff-normiert) else string($e/lexikon-begriff))"/>
</xsl:function>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2007. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="lex 011_Assenmacher_KostO_16_teil_I_buchstabe_F" userelativepaths="yes" externalpreview="no" url="..\..\Data\lexikon\011_Assenmacher_KostO_16_teil_I_buchstabe_F.xml" htmlbaseurl=""
		          outputurl="..\..\result\lex\011_Assenmacher_KostO_16_teil_I_buchstabe_F.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
		          additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bSchemaAware" value="false"/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="bWarnings" value="true"/>
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