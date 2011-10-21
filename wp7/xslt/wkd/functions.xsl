<?xml version='1.0'?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:fun="http://local-function/"
 xmlns:data="http://local-data/"
 xmlns:xs="http://www.w3.org/2001/XMLSchema" 
 xmlns:ppuri="java:at.punkt.commons.uricreator.URIFactory"
 exclude-result-prefixes="xsl xs fun data"
>

 <!--
 Project LOD2 - Project number 27943
 Work Package - 7
 Author Johan De Smedt
 Description: extract metadata, classification and links from WKD XML 
 Module: General utility functions
 -->

<!--  TRANSLATION LOGIC -->
<xsl:variable name="deu2eng">
	<data:entry><data:de>vorschrift</data:de><data:en>legislation</data:en></data:entry>
	<data:entry><data:de>vorschrift/typ</data:de><data:en>legislation/type</data:en></data:entry>
	<data:entry><data:de>entscheidung</data:de><data:en>jurisprudence</data:en></data:entry>
	<data:entry><data:de>entscheidung/typ</data:de><data:en>jurisprudence/type</data:en></data:entry>
	<data:entry><data:de>pressemitteilung</data:de><data:en>press_release</data:en></data:entry>
	<data:entry><data:de>pressemitteilung/typ</data:de><data:en>press_release/type</data:en></data:entry>
	<data:entry><data:de>aufsatz</data:de><data:en>journal_article</data:en></data:entry>
	<data:entry><data:de>herkunft</data:de><data:en>origin</data:en></data:entry>
	<data:entry><data:de>organisation</data:de><data:en>organisation</data:en></data:entry>
	<data:entry><data:de>anhang</data:de><data:en>appendix</data:en></data:entry>
	<data:entry><data:de>anlage</data:de><data:en>annex</data:en></data:entry>
	<data:entry><data:de>kennziffer</data:de><data:en>reference_number</data:en></data:entry>
	<data:entry><data:de>muster</data:de><data:en>model</data:en></data:entry>
	<data:entry><data:de>praeamble</data:de><data:en>preamble</data:en></data:entry>
	<data:entry><data:de>info</data:de><data:en>info</data:en></data:entry>
</xsl:variable>

<xsl:key name="eng-term" match="data:entry" use="data:de"/>

<xsl:function name="fun:deu2eng" as="xs:string">
	<xsl:param name="deu" as="xs:string"/>
	<xsl:value-of>
		<xsl:for-each select="$deu2eng">
			<xsl:variable name="eng" select="key('eng-term',$deu)" as="element()*"/>
			<xsl:value-of select="if (count($eng)=0) then $deu else $eng[1]/data:en"/>
		</xsl:for-each>
	</xsl:value-of>
</xsl:function>

<!-- BIBO DOCUMENT TYPE IDENTIFICATION -->
<xsl:variable name="deuDT2biboDT">
	<data:entry><data:de>vorschrift</data:de><data:bibo>Legislation</data:bibo></data:entry>
	<data:entry><data:de>entscheidung</data:de><data:bibo>LegalDecision</data:bibo></data:entry>
	<data:entry><data:de>aufsatz</data:de><data:bibo>Article</data:bibo></data:entry>
	<data:entry><data:de>aufsatz-es</data:de><data:bibo>Article</data:bibo></data:entry>
	<data:entry><data:de>pressemitteilung</data:de><data:bibo>LegalCaseDocument</data:bibo></data:entry>
</xsl:variable>

<xsl:key name="bibo-class" match="data:entry" use="data:de"/>

<xsl:function name="fun:getBiboClass" as="xs:string">
	<xsl:param name="doc-type" as="xs:string"/>
	<xsl:value-of>
		<xsl:for-each select="$deuDT2biboDT">
			<xsl:variable name="biboDT" select="key('bibo-class',$doc-type)" as="element()*"/>
			<xsl:choose>
				<xsl:when test="count($biboDT)=0">
					<xsl:value-of select="concat($bibo,'Document')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($bibo,$biboDT[1]/data:bibo)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:value-of>
</xsl:function>

<!-- court id functions -->
<xsl:variable name="courts" select="doc('courts.xml')/courts"/>
<xsl:key name="court" match="courts/name" use="@meta"/>

<xsl:function name="fun:courtId" as="xs:string">
	<xsl:param name="name" as="xs:string"/>
	<xsl:value-of>
		<xsl:for-each select="$courts">
			<xsl:variable name="id" select="key('court',$name)" as="element()*"/>
			<xsl:value-of select="if (count($id)=0) then '' else $id[1]/@technical"/>
		</xsl:for-each>
	</xsl:value-of>
</xsl:function>

<!-- GENERAL UTILITY FUNCTIONS -->
<xsl:function name="fun:dateDe2Iso" as="xs:string">
	<xsl:param name="date" as="xs:string"/>
	<xsl:analyze-string select="$date" regex="^([0-9]{{1,2}})\.([0-9]{{1,2}})\.([0-9]{{4}})$">
		<xsl:matching-substring>
			<xsl:variable name="m" select="if (string-length(regex-group(2)) = 2) then '' else '0'"/>
			<xsl:variable name="d" select="if (string-length(regex-group(1)) = 2) then '' else '0'"/>
			<xsl:value-of select="concat(regex-group(3),'-',$m,regex-group(2),'-',$d,regex-group(1))"/>
		</xsl:matching-substring>
		<xsl:non-matching-substring>
			<xsl:value-of select="."/>
		</xsl:non-matching-substring>
	</xsl:analyze-string>
</xsl:function>

<xsl:function name="fun:percentEncode" as="xs:string">
	<!-- placeholder function to get more sophistivated later (with java function)
	 handled: %, space, :, /, #, ?, [, ]
	 to do: @, !, $, &, ', ", (, ), *, +, ,, ;, =, \, <, >
	 -->
	<xsl:param name="in" as="xs:string"/>
	<xsl:value-of select="replace(replace(replace(replace(replace(replace(replace(replace($in,'%','%25'),'\s','%20'),'/','%2F'),'#','%23'),'\?','%3F'),'\[','%5B'),'\]','%5D'),':','%3A')"/>
</xsl:function>

<xsl:variable name="poolpart-in"  select="'ÀÁÂÃÅÆàáâãåæÈÉÊËèéêëÌÍÎÏìíîïÒÓÔÕòóôõÙÚÛùúûÇç'" as="xs:string"/>
<xsl:variable name="poolpart-out" select="'AAAAAAaaaaaaEEEEeeeeIIIIiiiiOOOOooooUUUuuuCc'" as="xs:string"/>

<xsl:function name="fun:stwSegmentId" as="xs:string">
	<!-- ÄäÜüÖööß -> Ae ae Ue ue Oe oe ss -->
	<xsl:param name="segment" as="xs:string"/>
	<!-- xsl:variable name="s" select="translate($segment,$poolpart-in,$poolpart-out)"/>
	<xsl:variable name="r" select="replace(replace(replace(replace(replace(replace(replace(replace($s,' ','_'),'ß','ss'),'ö','oe'),'Ö','Oe'),'Ä','Ae'),'ä','ae'),'Ü','Ue'),'ü','ue')"/>
	<xsl:value-of select="fun:percentEncode($r)"/-->
	<xsl:value-of select="substring-after(ppuri:createURIFromLabel('http://x/','x',$segment),'http://x/x/')"/>
</xsl:function>

<xsl:function name="fun:language" as="xs:string">
	<xsl:param name="l"/>
	<xsl:value-of select="if (normalize-space($l)=('','deu')) then 'de' else normalize-space($l)"/>
</xsl:function>

<xsl:function name="fun:firstAndRest" as="xs:string*">
	<xsl:param name="label" as="xs:string"/>
	<xsl:param name="token" as="xs:string"/>
	<xsl:variable name="segments">
		<xsl:sequence select="tokenize($label,$token)"/>
	</xsl:variable>
	<xsl:choose>
		<xsl:when test="count($segments) &lt; 2">
			<xsl:sequence select="$segments"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:sequence select="($segments[1], string-join(subsequence($segments, 2),$token))"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:function>

<xsl:function name="fun:lastAndHead" as="xs:string*">
	<xsl:param name="label" as="xs:string"/>
	<xsl:param name="token" as="xs:string"/>
	<xsl:variable name="segments" as="xs:string*">
		<xsl:sequence select="tokenize($label,$token)"/>
	</xsl:variable>
	<xsl:variable name="l" select="count($segments)" as="xs:integer"/>
	<xsl:choose>
		<xsl:when test="$l &lt; 2">
			<xsl:sequence select="$segments"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:sequence select="($segments[$l], string-join(subsequence($segments, 1, $l - 1),$token))"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:function>

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