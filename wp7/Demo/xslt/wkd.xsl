<?xml version='1.0'?>
<!DOCTYPE stylesheet [
    <!ENTITY wkd_xslt "http://xslt.wolterskluwer.de/" >
]>

<xsl:stylesheet version="2.0" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:fun="http://local-function/"
 xmlns:data="http://local-data/"
 xmlns:wkd="http://schema.wolterskluwer.de/"
 xmlns:wkd_xslt="http://xslt.wolterskluwer.de/"
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
  top level output is generated in the rdf:RDF wrapper, not in the document rdf:Description wrapper.
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

<xsl:import href="&wkd_xslt;functions.xsl"/>
<xsl:import href="&wkd_xslt;aliases.xsl"/>
<xsl:import href="&wkd_xslt;content.xsl"/>
<!-- metadata, practice area, taxonomy -->
<xsl:include href="&wkd_xslt;common-md.xsl"/>
<!-- document id -->
<xsl:include href="&wkd_xslt;id-and-ref.xsl"/>

<!-- vorschrift DOCUMENT TYPE  -->
<xsl:include href="&wkd_xslt;vs.xsl"/>
<!-- entscheidung DOCUMENT TYPE  -->
<xsl:include href="&wkd_xslt;es.xsl"/>
<!-- pressemitteilung DOCUMENT TYPE  -->
<xsl:include href="&wkd_xslt;pm.xsl"/>
<!-- aufsatz DOCUMENT TYPE  -->
<xsl:include href="&wkd_xslt;zs.xsl"/>

<!-- OUTPUT STREAM DEFINITIONS --> 
<xsl:output method="xml" encoding="UTF-8" indent="yes" use-character-maps="NBSP"/>

<xsl:param name="file-uri" select="base-uri(.)" as="xs:string"/>

<xsl:character-map name="NBSP">
	<xsl:output-character character="&#160;" string=" "/>
</xsl:character-map>

<xsl:template match="/">
	<rdf:RDF>
		<xsl:for-each select="wkdsc/*">
			<xsl:variable name="doc" select="." as="element()"/>
			<xsl:variable name="r-uri" select="fun:rUri($doc)" as="xs:string"/>
			<xsl:if test="string-length($r-uri) &gt; 0">
				<xsl:variable name="r-type" select="fun:rType($doc)" as="xs:string"/>
				<rdf:Description rdf:about="{$r-uri}">
					<xsl:call-template name="setLanguage"/>
					<xsl:if test="string-length($file-uri) &gt; 0">
						<xsl:variable select="tokenize(replace($file-uri,'\\','/'),'/')" name="base" as="xs:string*"/>
						<wkd:fileName><xsl:value-of select="$base[last()]"/></wkd:fileName>
					</xsl:if>
					<bibo:issuer rdf:resource="{$wkd}LOD-2-project/WP07"/>
					<rdf:type rdf:resource="{$r-type}"/>
					<rdf:type rdf:resource="{fun:getBiboClass(name())}"/>
					<!-- in this track we typically will walk down the xml document and catch wathever can be processed on the hierarchical path -->
					<xsl:apply-templates select=".">
						<xsl:with-param name="r-uri" as="xs:string" tunnel="yes" select="$r-uri"/>
						<xsl:with-param name="p-uri" as="xs:string" tunnel="yes" select="$r-uri"/>
					</xsl:apply-templates>
				</rdf:Description>
				<!-- build the hierarchy of the used STW -->
				<xsl:apply-templates select="//stichworte" mode="build-hierarchy"/>
				<xsl:apply-templates select="//inline-stichwort" mode="build-hierarchy"/>
				<!-- This track builds metadata outside the document,
				     e.g. set the subject relations between the document and its subject and annotations on it
					      stichwort
						  inline-stichwort
					  -->
				<xsl:apply-templates select="." mode="top-level">
					<xsl:with-param name="r-uri" as="xs:string" tunnel="yes" select="$r-uri"/>
					<xsl:with-param name="p-uri" as="xs:string" tunnel="yes" select="$r-uri"/>
				</xsl:apply-templates>
			</xsl:if>
		</xsl:for-each>
	</rdf:RDF>
</xsl:template>

<!-- cites -->
<xsl:template match="zitat-vs">
	<xsl:variable name="cited-doc-uri" as="xs:string" select="concat($r-base-uri,fun:deu2eng('vorschrift'),'/',@vsk)"/>
	<xsl:variable name="cited-base-part" as="xs:string">
		<xsl:choose>
			<xsl:when test="@par"><xsl:value-of select="concat('/par/',fun:percentEncode(@par))"/></xsl:when>
			<xsl:when test="@art"><xsl:value-of select="concat('/art/',fun:percentEncode(@art))"/></xsl:when>
			<xsl:when test="@anlage-typ">
				<xsl:variable name="n" select="fun:percentEncode(normalize-space(@anlage-nr))" as="xs:string"/>
				<xsl:value-of select="concat('/',fun:deu2eng(@anlage-typ),if (string-length($n) = 0) then '' else concat('/',$n))"/>
			</xsl:when>
			<xsl:when test="@vs-obj-typ">
				<xsl:variable name="n" select="fun:percentEncode(normalize-space(@vs-obj-nr))" as="xs:string"/>
				<xsl:value-of select="concat('/vs-obj/',fun:deu2eng(@vs-obj-typ),
				                if (string-length($n) = 0) then '' else concat('/',$n))"/>
			</xsl:when>
			<xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:for-each select="*">
		<xsl:variable name="cited-target" as="xs:string">
			<xsl:choose>
				<xsl:when test="name(.) = 'vs-absatz'">
					<xsl:value-of select="if (string-length($cited-base-part) = 0) then ''
					                      else concat($cited-doc-uri,$cited-base-part,'/section/',fun:percentEncode(string(@nr)))"/>
				</xsl:when>
				<xsl:when test="name(.) = ('quelle','beschriftung')">
					<xsl:value-of select="''"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="fun:getPartId(.,$cited-doc-uri)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$cited-target != ''">
			<xsl:call-template name="write-cite">
				<xsl:with-param name="cite-type-name" select="name()" as="xs:string"/>
				<xsl:with-param name="cited-target" select="$cited-target" as="xs:string"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template match="zitat-es">
	<xsl:for-each select="entscheidung">
		<xsl:variable name="doc" select="." as="element()"/>
		<xsl:variable name="target-uri" select="fun:rUri($doc)" as="xs:string"/>
		<xsl:call-template name="write-cite">
			<xsl:with-param name="cite-type-name" select="name()" as="xs:string"/>
			<xsl:with-param name="cited-target" select="$target-uri" as="xs:string"/>
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>

<!-- the following three cites do not contain a reference, just the cited quote -->
<xsl:template match="zitat-va" mode="#default top-level"/>
<xsl:template match="zitat" mode="#default top-level"/>
<xsl:template match="zitat-block" mode="#default top-level"/>

<xsl:template name="write-cite">
	<xsl:param name="cite-type-name" as="xs:string"/>
	<xsl:param name="cited-target" as="xs:string"/>
	<xsl:if test="string-length($cited-target) &gt; 0">
		<dcterms:hasPart>
			<metalex:Fragment>
				<wkd:fragmentType rdf:resource="{$v-base-uri}FragmentType/{$cite-type-name}"/>
				<rdf:type rdf:resource="{$bibo}Excerpt"/>
				<bibo:cites rdf:resource="{$cited-target}"/>
			</metalex:Fragment>
		</dcterms:hasPart>
	</xsl:if>
</xsl:template>

<!-- GENERAL XSL PROCESSING -->
<!-- just always walk down all elements -->
<xsl:template match="*">
	<xsl:apply-templates mode="#current"/>
</xsl:template>

<!-- general policy will be that if text is needed, it is selected explicitly in the template ... -->
<xsl:template match="text()" mode="#default top-level build-hierarchy"/>
<!-- ... except when building an xml-literal value -->
<xsl:template match="text()" mode="xml-literal plain-literal">
	<xsl:value-of select="normalize-space(.)"/>
</xsl:template>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2007. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="no" name="bd_baugb" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\Schlichter_Berliner_K_BauGB-BauGB_2004.xml" htmlbaseurl="" outputurl="..\result\Schlichter_Berliner_K_BauGB-BauGB_2004_tn3.rdf"
		          processortype="saxon8" useresolver="no" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Legislation/Schlichter_Berliner_K_BauGB-BauGB_2004.xml'"/>
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bSchemaAware" value="false"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
		<scenario default="no" name="webdav aag" userelativepaths="yes" externalpreview="no" url="http://wp7.lod2.eu:8890/DAV/home/wkd/rdf_sink/aag.xml" htmlbaseurl="" outputurl="..\result\aag-dav.rdf" processortype="saxon8" useresolver="yes"
		          profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
		<scenario default="no" name="abbergv" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\abbergv.xml" htmlbaseurl="" outputurl="..\result\abbergv.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Legislation/abbergv.xml'"/>
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
		<scenario default="no" name="webdav aentg" userelativepaths="yes" externalpreview="no" url="http://wp7.lod2.eu:8890/DAV/home/wkd/rdf_sink/aentg.xml" htmlbaseurl="" outputurl="..\result\aentg.rdf" processortype="saxon8" useresolver="yes"
		          profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
		<scenario default="no" name="ustae" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\ustae.xml" htmlbaseurl="" outputurl="..\result\ustae-t.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Legislation/ustae.xml'"/>
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
		<scenario default="no" name="abkg" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\abkg_be.xml" htmlbaseurl="" outputurl="..\result\abkg.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="nato-zusabk" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\nato-zusabk.xml" htmlbaseurl="" outputurl="..\result\nato-zusabk.rdf" processortype="saxon8" useresolver="yes" profilemode="0"
		          profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="aentg" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\aentg.xml" htmlbaseurl="" outputurl="..\result\aentg.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="Schlichter_Berliner_K_BauGB-BauGB_2004" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\Schlichter_Berliner_K_BauGB-BauGB_2004.xml" htmlbaseurl=""
		          outputurl="..\result\Schlichter_Berliner_K_BauGB-BauGB_2004.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
		          additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
		<scenario default="no" name="Schlichter_Berliner_K_BauGB-BauNVO" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\Schlichter_Berliner_K_BauGB-BauNVO.xml" htmlbaseurl=""
		          outputurl="..\result\Schlichter_Berliner_K_BauGB-BauNVO.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
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
		<scenario default="yes" name="jur fg_st_2003-04-16_2-k-38-02_lsr" userelativepaths="yes" externalpreview="no" url="..\Data\Jurisprudence\fg_st_2003-04-16_2-k-38-02_lsr.xml" htmlbaseurl=""
		          outputurl="..\result\jur\fg_st_2003-04-16_2-k-38-02_lsr-tn3.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
		          additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Jurisprudence/fg_st_2003-04-16_2-k-38-02_lsr.xml'"/>
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
		<scenario default="no" name="jur fg_st_2010-11-03_3-k-1350-03_vt" userelativepaths="yes" externalpreview="no" url="..\Data\Jurisprudence\fg_st_2010-11-03_3-k-1350-03_vt.xml" htmlbaseurl=""
		          outputurl="..\result\jur\fg_st_2010-11-03_3-k-1350-03_vt.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
		          additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Jurisprudence/fg_st_2010-11-03_3-k-1350-03_vt.xml'"/>
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bSchemaAware" value="false"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
		<scenario default="no" name="jur bgh_-_2008-11-05_i-zr-39-06" userelativepaths="yes" externalpreview="no" url="..\Data\Jurisprudence\bgh_-_2008-11-05_i-zr-39-06.xml" htmlbaseurl="" outputurl="..\result\jur\bgh_-_2008-11-05_i-zr-39-06.rdf"
		          processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Jurisprudence/bgh_-_2008-11-05_i-zr-39-06.xml'"/>
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
		<scenario default="no" name="jur bgh_-_2009-10-23_v-zr-141-08" userelativepaths="yes" externalpreview="no" url="..\Data\Jurisprudence\bgh_-_2009-10-23_v-zr-141-08.xml" htmlbaseurl="" outputurl="..\result\jur\bgh_-_2009-10-23_v-zr-141-08.rdf"
		          processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Jurisprudence/bgh_-_2009-10-23_v-zr-141-08.xml'"/>
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bSchemaAware" value="false"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
		<scenario default="no" name="eugh_-_2009-11-12_c-518-07" userelativepaths="yes" externalpreview="no" url="..\Data\Jurisprudence\eugh_-_2009-11-12_c-518-07.xml" htmlbaseurl="" outputurl="..\result\jur\eugh_-_2009-11-12_c-518-07.rdf"
		          processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Jurisprudence/eugh_-_2009-11-12_c-518-07.xml'"/>
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
		<scenario default="no" name="bmv-ae" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\bmv-ae.xml" htmlbaseurl="" outputurl="..\result\bmv-ae.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="fg_st_2003-02-10_1-k-30456-99_jurion" userelativepaths="yes" externalpreview="no" url="http://wp7.lod2.eu:8890/DAV/home/wkd/rdf_sink/fg_st_2003-02-10_1-k-30456-99_jurion.xml" htmlbaseurl=""
		          outputurl="..\result\fg_st_2003-02-10_1-k-30456-99_jurion.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
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
		<scenario default="no" name="aufs euroas_2009_01-02_10" userelativepaths="yes" externalpreview="no" url="..\Data\Doctrine\euroas_2009_01-02_10.xml" htmlbaseurl="" outputurl="..\result\doc\euroas_2009_01-02_10.rdf" processortype="saxon8"
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
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
		<scenario default="no" name="aufs KommP_BY_2010_01_17" userelativepaths="yes" externalpreview="no" url="..\Data\Doctrine\KommP_BY_2010_01_17.xml" htmlbaseurl="" outputurl="..\result\doc\KommP_BY_2010_01_17_tn3.rdf" processortype="saxon8"
		          useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="aus lag_bw_2003-11-04_17-sa-113-98_jurion" userelativepaths="yes" externalpreview="no" url="..\Data\Doctrine\lag_bw_2003-11-04_17-sa-113-98_jurion.xml" htmlbaseurl=""
		          outputurl="..\result\doc\lag_bw_2003-11-04_17-sa-113-98_jurion.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
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
		<scenario default="no" name="lag_bw_2007-04-10_12-ta-7-07_jurion" userelativepaths="yes" externalpreview="no" url="..\Data\Doctrine\lag_bw_2007-04-10_12-ta-7-07_jurion.xml" htmlbaseurl=""
		          outputurl="..\result\lag_bw_2007-04-10_12-ta-7-07_jurion.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
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
		<scenario default="no" name="lag_bw_2011-04-01_3-sa-19-10_jurion" userelativepaths="yes" externalpreview="no" url="http://wp7.lod2.eu:8890/DAV/home/wkd/rdf_sink/lag_bw_2011-04-01_3-sa-19-10_jurion.xml" htmlbaseurl=""
		          outputurl="..\result\lag_bw_2011-04-01_3-sa-19-10_jurion.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
		          additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
		<scenario default="no" name="aduebk" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\aduebk.xml" htmlbaseurl="" outputurl="..\result\aduebk.rdf" processortype="saxon8" useresolver="no" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Legislation/Schlichter_Berliner_K_BauGB-BauGB_2004.xml'"/>
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
		<scenario default="no" name="PM lag_bw_2011-04-29_7-tabv-7-10_pm" userelativepaths="yes" externalpreview="no" url="..\Data\pm\lag_bw_2011-04-29_7-tabv-7-10_pm.xml" htmlbaseurl="" outputurl="..\result\lag_bw_2011-04-29_7-tabv-7-10_pm.rdf"
		          processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Legislation/abbergv.xml'"/>
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bSchemaAware" value="false"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
		<scenario default="no" name="030_Assenmacher_KostO_16_teil_II_bd_kosto" userelativepaths="yes" externalpreview="no" url="..\Data\Legislation\030_Assenmacher_KostO_16_teil_II_bd_kosto.xml" htmlbaseurl=""
		          outputurl="..\result\030_Assenmacher_KostO_16_teil_II_bd_kosto.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
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
		<scenario default="no" name="error" userelativepaths="yes" externalpreview="no" url="..\Data\xslterror\Fielitz_PBefG_C4.xml" htmlbaseurl="" outputurl="..\result\error.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<parameterValue name="file-uri" value="'file:///c:/Project/LOD2/WP07-WKDE/Data/Legislation/abbergv.xml'"/>
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
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no">
			<SourceSchema srcSchemaPath="..\Data\Legislation\Schlichter_Berliner_K_BauGB-BauGB_2004.xml" srcSchemaRoot="wkdsc" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/">
				<block path="rdf:RDF/xsl:for-each" x="395" y="0"/>
				<block path="rdf:RDF/xsl:for-each/xsl:if/&gt;[0]" x="459" y="0"/>
				<block path="rdf:RDF/xsl:for-each/xsl:if/&gt;[0]/string-length[0]" x="413" y="0"/>
				<block path="rdf:RDF/xsl:for-each/xsl:if" x="505" y="0"/>
				<block path="rdf:RDF/xsl:for-each/xsl:if/rdf:Description/xsl:call-template" x="495" y="40"/>
				<block path="rdf:RDF/xsl:for-each/xsl:if/rdf:Description/wkd:fileName/xsl:value-of" x="455" y="80"/>
				<block path="rdf:RDF/xsl:for-each/xsl:if/rdf:Description/xsl:apply-templates" x="415" y="40"/>
				<block path="rdf:RDF/xsl:for-each/xsl:if/xsl:apply-templates" x="455" y="20"/>
				<block path="rdf:RDF/xsl:for-each/xsl:if/xsl:apply-templates[1]" x="335" y="20"/>
				<block path="rdf:RDF/xsl:for-each/xsl:if/xsl:apply-templates[2]" x="295" y="20"/>
			</template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->
