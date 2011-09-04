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
 Module: Vorschrift module
 -->

<xsl:import href="content.xsl"/>

<xsl:output encoding="UTF-8"/>

<!-- VORSCHRIFT DOCUMENT TYPE  -->
<xsl:template match="vorschrift">
	<dcterms:identifier><xsl:value-of select="@vsk"/></dcterms:identifier>
	<wkd:legislationType>
		<skos:Concept rdf:about="{$v-base-uri}{fun:deu2eng('vorschrift/typ')}/{@vs-typ}"/>
	</wkd:legislationType>
	<dcterms:source>
		<skos:Concept rdf:about="{$v-base-uri}{fun:deu2eng('herkunft')}/{@vs-herkunft}"/>
	</dcterms:source>
	<xsl:if test="string-length(@fna) &gt; 0">
		<wkd:fna><xsl:value-of select="@fna"/></wkd:fna>
	</xsl:if>
	<dcterms:title><xsl:apply-templates select="vs-titel-kopf/titel"  mode="plain-literal"/></dcterms:title>
	<xsl:if test="vs-titel-kopf/vs-kurztitel">
		<bibo:shortTitle><xsl:value-of select="string(vs-titel-kopf/vs-kurztitel)"/></bibo:shortTitle>
	</xsl:if>
	<xsl:if test="vs-vorspann">
		<dcterms:description rdf:parseType="Literal">
			<xsl:apply-templates select="vs-vorspann/(text() | *)" mode="xml-literal"/>
		</dcterms:description>
	</xsl:if>
	<!-- build the list of all containd parts, the list is flat, not hierarchical -->
	<xsl:call-template name="doc-parts-vs"/>
	<xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="vorschrift" mode="top-level">
	<xsl:apply-templates select="*" mode="top-level"/>
</xsl:template>

<xsl:template match="vs-metadaten">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:if test="vs-aenderung/@datum">
		<dcterms:modified><xsl:value-of select="fun:dateDe2Iso(string(vs-aenderung/@datum))"/></dcterms:modified>
	</xsl:if>
	<xsl:for-each select="vs-fassung">
		<dcterms:issued><xsl:value-of select="fun:dateDe2Iso(string(@datum))"/></dcterms:issued>
		<xsl:if test="@inkraft">
			<dcterms:temporal><xsl:value-of select="fun:dateDe2Iso(string(@inkraft))"/></dcterms:temporal>
		</xsl:if>
	</xsl:for-each>
	<xsl:for-each select="vs-verfasser">
		<xsl:apply-templates select="organisation">
			<xsl:with-param name="namespace" select="$wkd" as="xs:string"/>
			<xsl:with-param name="property" select="'sourcePublisher'" as="xs:string"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="vs-vertragsparteien"/>
	</xsl:for-each>
	<xsl:apply-templates select="metadaten | rechtsgebiete | taxonomien"/>
</xsl:template>

<xsl:template match="vs-metadaten" mode="top-level">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:apply-templates select="stichworte" mode="top-level">
		<xsl:with-param name="subject" select="$r-uri" as="xs:string"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="vs-verfasser/vs-vertragsparteien">
	<wkd:treatyParties rdf:parseType="Literal">
		<xsl:apply-templates select="." mode="xml-literal"/>
	</wkd:treatyParties>
</xsl:template>

<xsl:template name="doc-parts-vs">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:for-each-group select="/wkdsc/vorschrift//(paragraph | artikel | vs-ebene | vs-anlage)" group-by="name()">
		<xsl:for-each select="current-group()">
			<xsl:choose>
				<xsl:when test="./ancestor::zitat-vs">
					<xsl:message terminate="no">found vs structure element in a zitat-vs - skipped this structure</xsl:message>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)" as="xs:string"/>
					<metalex:fragment rdf:resource="{$uri}"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:for-each-group>
</xsl:template>

<!-- ve-ebene -->
<xsl:template match="vs-ebene">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<dcterms:hasPart>
		<metalex:Fragment rdf:about="{$uri}">
			<wkd:fragmentType rdf:resource="{$v-base-uri}FragmentType/{name()}"/>
			<xsl:if test="titel-kopf/titel">
				<dcterms:title><xsl:apply-templates select="titel-kopf/titel" mode="plain-literal"/></dcterms:title>
			</xsl:if>
			<xsl:apply-templates select="*">
				<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
			</xsl:apply-templates>
		</metalex:Fragment>
	</dcterms:hasPart>
</xsl:template>

<xsl:template match="vs-ebene" mode="top-level">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<xsl:apply-templates select="*" mode="top-level">
		<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="paragraph">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<dcterms:hasPart>
		<metalex:Fragment rdf:about="{$uri}">
			<wkd:fragmentType rdf:resource="{$v-base-uri}FragmentType/{name()}"/>
			<xsl:if test="titel-kopf/titel">
				<dcterms:title><xsl:apply-templates select="titel-kopf/titel" mode="plain-literal"/></dcterms:title>
			</xsl:if>
			<xsl:apply-templates select="*">
				<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
			</xsl:apply-templates>
		</metalex:Fragment>
	</dcterms:hasPart>
</xsl:template>

<xsl:template match="paragraph" mode="top-level">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<xsl:apply-templates select="*" mode="top-level">
		<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="vs-absatz">
	<xsl:param name="p-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="abs-uri" select="concat($p-uri,'/section/',fun:percentEncode(string(@nr)))" as="xs:string"/>
	<dcterms:hasPart>
		<metalex:Fragment rdf:about="{$abs-uri}">
			<wkd:fragmentType rdf:resource="{$v-base-uri}FragmentType/{name()}"/>
			<xsl:apply-templates select="*">
				<xsl:with-param name="p-uri" select="$abs-uri" as="xs:string" tunnel="yes"/>
			</xsl:apply-templates>
		</metalex:Fragment>
	</dcterms:hasPart>
</xsl:template>

<xsl:template match="vs-absatz" mode="top-level">
	<xsl:param name="p-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="abs-uri" select="concat($p-uri,'/section/',fun:percentEncode(string(@nr)))" as="xs:string"/>
	<xsl:apply-templates select="*" mode="top-level">
		<xsl:with-param name="p-uri" select="$abs-uri" as="xs:string" tunnel="yes"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="absatz/satz | absatz/rn"/>

<xsl:template match="absatz/satz | absatz/rn" mode="top-level"/>

<xsl:template match="absatz" mode="#default top-level">
	<xsl:apply-templates select="*" mode="#current"/>
</xsl:template>

<xsl:template match="artikel">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<dcterms:hasPart>
		<metalex:Fragment rdf:about="{$uri}">
			<wkd:fragmentType rdf:resource="{$v-base-uri}FragmentType/{name()}"/>
			<xsl:if test="titel-kopf/titel">
				<dcterms:title><xsl:apply-templates select="titel-kopf/titel" mode="plain-literal"/></dcterms:title>
			</xsl:if>
			<xsl:apply-templates select="*">
				<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
			</xsl:apply-templates>
		</metalex:Fragment>
	</dcterms:hasPart>
</xsl:template>

<xsl:template match="artikel" mode="top-level">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<xsl:apply-templates select="*" mode="top-level">
		<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="artikel-ebene">
	<xsl:param name="p-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="id" as="xs:string">
		<xsl:value-of select="concat(@bez,'/',@wert)"/>
	</xsl:variable>
	<xsl:variable name="ae-uri" select="concat($p-uri,'/art-ebene/',fun:percentEncode($id))" as="xs:string"/>
	<dcterms:hasPart>
		<metalex:Fragment rdf:about="{$ae-uri}">
		<wkd:fragmentType rdf:resource="{$v-base-uri}FragmentType/{name()}"/>
			<xsl:apply-templates select="*"/>
		</metalex:Fragment>
	</dcterms:hasPart>
</xsl:template>

<xsl:template match="artikel-ebene" mode="top-level">
	<xsl:param name="p-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="id" as="xs:string">
		<xsl:value-of select="concat(@bez,'/',@wert)"/>
	</xsl:variable>
	<xsl:variable name="ae-uri" select="concat($p-uri,'/art-ebene/',fun:percentEncode($id))" as="xs:string"/>
	<xsl:apply-templates select="*" mode="top-level">
		<xsl:with-param name="p-uri" select="$ae-uri" as="xs:string" tunnel="yes"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="vs-anlage | vs-anlage-ebene">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<dcterms:hasPart>
		<metalex:Fragment rdf:about="{$uri}">
			<wkd:fragmentType rdf:resource="{$v-base-uri}FragmentType/{name()}"/>
			<xsl:if test="titel-kopf/titel">
				<dcterms:title><xsl:apply-templates select="titel-kopf/titel" mode="plain-literal"/></dcterms:title>
			</xsl:if>
			<xsl:apply-templates select="*">
				<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
			</xsl:apply-templates>
		</metalex:Fragment>
	</dcterms:hasPart>
</xsl:template>

<xsl:template match="vs-anlage | vs-anlage-ebene" mode="top-level">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<xsl:apply-templates select="*" mode="top-level">
		<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="vs-objekt">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<dcterms:hasPart>
		<metalex:Fragment rdf:about="{$uri}">
			<wkd:fragmentType rdf:resource="{$v-base-uri}FragmentType/{name()}"/>
			<xsl:if test="titel-kopf/titel">
				<dcterms:title><xsl:apply-templates select="titel-kopf/titel" mode="plain-literal"/></dcterms:title>
			</xsl:if>
			<xsl:apply-templates select="*">
				<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
			</xsl:apply-templates>
		</metalex:Fragment>
	</dcterms:hasPart>
</xsl:template>

<xsl:template match="vs-objekt" mode="top-level">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="fun:getPartId(.,$r-uri)"/>
	<xsl:apply-templates select="*" mode="top-level">
		<xsl:with-param name="p-uri" select="$uri" as="xs:string" tunnel="yes"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="vz-inhalt-auto" mode="#all"/>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2007. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="bd_baugb" userelativepaths="yes" externalpreview="no" url="..\..\Data\Legislation\Schlichter_Berliner_K_BauGB-BauGB_2004.xml" htmlbaseurl="" outputurl="..\..\result\Schlichter_Berliner_K_BauGB-BauGB_2004.rdf"
		          processortype="saxon8" useresolver="no" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="webdav aag" userelativepaths="yes" externalpreview="no" url="http://wp7.lod2.eu:8890/DAV/home/wkd/rdf_sink/aag.xml" htmlbaseurl="" outputurl="..\..\result\aag.rdf" processortype="saxon8" useresolver="yes"
		          profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="abbergv" userelativepaths="yes" externalpreview="no" url="..\..\Data\Legislation\abbergv.xml" htmlbaseurl="" outputurl="..\..\result\abbergv.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="webdav aentg" userelativepaths="yes" externalpreview="no" url="http://wp7.lod2.eu:8890/DAV/home/wkd/rdf_sink/aentg.xml" htmlbaseurl="" outputurl="..\..\result\aentg.rdf" processortype="saxon8" useresolver="yes"
		          profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="ustae" userelativepaths="yes" externalpreview="no" url="..\..\Data\Legislation\ustae.xml" htmlbaseurl="" outputurl="..\..\result\ustae.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="abkg" userelativepaths="yes" externalpreview="no" url="..\..\Data\Legislation\abkg_be.xml" htmlbaseurl="" outputurl="..\..\result\abkg.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="nato-zusabk" userelativepaths="yes" externalpreview="no" url="..\..\Data\Legislation\nato-zusabk.xml" htmlbaseurl="" outputurl="..\..\result\nato-zusabk.rdf" processortype="saxon8" useresolver="yes" profilemode="0"
		          profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="aentg" userelativepaths="yes" externalpreview="no" url="..\..\Data\Legislation\aentg.xml" htmlbaseurl="" outputurl="..\..\result\aentg.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="no" name="Schlichter_Berliner_K_BauGB-BauGB_2004" userelativepaths="yes" externalpreview="no" url="..\..\Data\Legislation\Schlichter_Berliner_K_BauGB-BauGB_2004.xml" htmlbaseurl=""
		          outputurl="..\..\result\Schlichter_Berliner_K_BauGB-BauGB_2004.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
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
		<scenario default="no" name="Schlichter_Berliner_K_BauGB-BauNVO" userelativepaths="yes" externalpreview="no" url="..\..\Data\Legislation\Schlichter_Berliner_K_BauGB-BauNVO.xml" htmlbaseurl=""
		          outputurl="..\..\result\Schlichter_Berliner_K_BauGB-BauNVO.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
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
	</scenarios>
	<MapperMetaTag>
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
		<MapperBlockPosition></MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->