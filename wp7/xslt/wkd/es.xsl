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
 Module: entscheidung module
 -->

<xsl:template match="entscheidung">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<wkd:jurisprudenceType>
		<skos:Concept rdf:about="{$v-base-uri}{fun:deu2eng('entscheidung/typ')}/{@es-typ}"/>
	</wkd:jurisprudenceType>
	<xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="es-titel-kopf">
	<xsl:if test="string-length(normalize-space(titel)) &gt; 0">
		<dcterms:title xml:lang="{fun:language(titel/@sprache)}"><xsl:apply-templates select="titel" mode="plain-literal"/></dcterms:title>
	</xsl:if>
	<xsl:if test="string-length(titel-zusatz) &gt; 0">
		<wkd:subTitle xml:lang="{fun:language(titel-zusatz/@sprache)}"><xsl:value-of select="string(titel-zusatz)"/></wkd:subTitle>
	</xsl:if>
	<xsl:for-each select="titel-trefferliste">
		<dcterms:alternative xml:lang="{fun:language(@sprache)}"><xsl:apply-templates select="." mode="plain-literal"/></dcterms:alternative>
	</xsl:for-each>
	<xsl:apply-templates select="*/*"/>
</xsl:template>


<xsl:template match="entscheidung" mode="top-level">
	<xsl:apply-templates select="*" mode="top-level"/>
</xsl:template>

<!-- todo: es-inhalt-eu -->

<!-- leitsatze -->
<xsl:template match="leitsaetze | orientierungssaetze">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="uri" select="$r-uri"/>
	<xsl:for-each select="leitsatz | orientierungssatz">
		<dcterms:description>
			<bibo:DocumentPart>
				<wkd:fragmentType rdf:resource="{$v-base-uri}FragmentType/{name()}.{@typ}"/>
				<xsl:if test="@nr">
					<bibo:number><xsl:value-of select="@nr"/></bibo:number>
				</xsl:if>
				<xsl:apply-templates select="autor/*">
					<xsl:with-param name="namespace" select="$dcterms" as="xs:string"/>
					<xsl:with-param name="property" select="'creator'" as="xs:string"/>
				</xsl:apply-templates>
				<rdf:value rdf:parseType="Literal">
					<xsl:apply-templates mode="xml-literal"/>
				</rdf:value>
			</bibo:DocumentPart>
		</dcterms:description>
	</xsl:for-each>
</xsl:template>

<xsl:template match="leitsaetze | orientierungssaetze" mode="top-level"/>

<xsl:template match="es-metadaten">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<bibo:court>
		<skos:Concept rdf:about="{$v-base-uri}Court/{fun:courtId(gericht)}"/>
	</bibo:court>
	<wkd:decisionDate rdf:datatype="{$xsd}date"><xsl:value-of select="fun:dateDe2Iso(datum)"/></wkd:decisionDate>
	<xsl:if test="urteilsart/@typ">
		<wkd:judgementType>
			<wkd:JudgementType rdf:about="{$v-base-uri}JudgementType/{fun:percentEncode(urteilsart/@typ)}"/>
		</wkd:judgementType>
	</xsl:if>
	<xsl:if test="urteilsname">
		<dcterms:alternative rdf:parseType="Literal">
			<xsl:apply-templates mode="xml-literal"/>
		</dcterms:alternative>
	</xsl:if>
	<xsl:apply-templates select="metadaten | rechtsgebiete | taxonomien"/>
</xsl:template>

<xsl:template match="es-metadaten" mode="top-level">
	<xsl:param name="r-uri" as="xs:string" tunnel="yes"/>
	<xsl:apply-templates select="stichworte" mode="top-level"/>
</xsl:template>

<!-- Set base reference -->
<xsl:template match="entscheidung/normenkette">
	<xsl:variable name="refType" as="xs:string" select="name()"/>
	<xsl:apply-templates select="verweis-vs">
		<xsl:with-param name="referenceType" tunnel="yes" select="$refType" as="xs:string"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="entscheidung/normenkette" mode="top-level"/>

<xsl:template match="entscheidung/fundstellen">
	<xsl:variable name="refType" as="xs:string" select="name()"/>
	<xsl:apply-templates select="*">
		<xsl:with-param name="referenceType" tunnel="yes" select="$refType" as="xs:string"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="entscheidung/fundstellen" mode="top-level"/>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2007. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="no" name="fg_st_2003-04-16_2-k-38-02_lsr" userelativepaths="yes" externalpreview="no" url="..\..\Data\Jurisprudence\fg_st_2003-04-16_2-k-38-02_lsr.xml" htmlbaseurl=""
		          outputurl="..\..\result\jur\fg_st_2003-04-16_2-k-38-02_lsr.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
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
		<scenario default="no" name="fg_st_2010-11-03_3-k-1350-03_vt" userelativepaths="yes" externalpreview="no" url="..\..\Data\Jurisprudence\fg_st_2010-11-03_3-k-1350-03_vt.xml" htmlbaseurl=""
		          outputurl="..\..\result\jur\fg_st_2010-11-03_3-k-1350-03_vt.rdf" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2"
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
		<scenario default="no" name="bgh_-_2008-11-05_i-zr-39-06" userelativepaths="yes" externalpreview="no" url="..\..\Data\Jurisprudence\bgh_-_2008-11-05_i-zr-39-06.xml" htmlbaseurl="" outputurl="..\..\result\jur\bgh_-_2008-11-05_i-zr-39-06.rdf"
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
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
		<scenario default="no" name="bgh_-_2009-10-23_v-zr-141-08" userelativepaths="yes" externalpreview="no" url="..\..\Data\Jurisprudence\bgh_-_2009-10-23_v-zr-141-08.xml" htmlbaseurl="" outputurl="..\..\result\jur\bgh_-_2009-10-23_v-zr-141-08.rdf"
		          processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
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
		<scenario default="yes" name="eugh_-_2009-11-12_c-518-07" userelativepaths="yes" externalpreview="no" url="..\..\Data\Jurisprudence\eugh_-_2009-11-12_c-518-07.xml" htmlbaseurl="" outputurl="..\..\result\jur\eugh_-_2009-11-12_c-518-07.rdf"
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