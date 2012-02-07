<?xml version='1.0'?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:fun="http://local-function/"
 xmlns:data="http://local-data/"
 xmlns:xs="http://www.w3.org/2001/XMLSchema" 
 xmlns:ppuri="java:at.punkt.commons.uricreator.URIFactory"
 exclude-result-prefixes="xsl xs fun data ppuri"
 xmlns:skos="http://www.w3.org/2004/02/skos/core#" 
 xmlns:dcterms="http://purl.org/dc/terms/" 
>

 <!--
 Project LOD2 - Project number 27943
 Work Package - 7
 Author Johan De Smedt
 Description: extract metadata, classification and links from WKD XML 
 Module: General utility functions
 -->

<!--  TRANSLATION LOGIC -->
<!-- BCP47 - use iso 639-1 if available, else iso 639-2 -->
<xsl:variable name="language-code">
	<data:entry><data:wk>ara</data:wk><data:iso>ar</data:iso></data:entry>
	<data:entry><data:wk>arg</data:wk><data:iso>an</data:iso></data:entry>
	<data:entry><data:wk>bel</data:wk><data:iso>be</data:iso></data:entry>
	<data:entry><data:wk>bos</data:wk><data:iso>bs</data:iso></data:entry>
	<data:entry><data:wk>bre</data:wk><data:iso>br</data:iso></data:entry>
	<data:entry><data:wk>bul</data:wk><data:iso>bg</data:iso></data:entry>
	<data:entry><data:wk>cat</data:wk><data:iso>ca</data:iso></data:entry>
	<data:entry><data:wk>ces</data:wk><data:iso>cs</data:iso></data:entry>
	<data:entry><data:wk>cos</data:wk><data:iso>co</data:iso></data:entry>
	<data:entry><data:wk>cym</data:wk><data:iso>cy</data:iso></data:entry>
	<data:entry><data:wk>dan</data:wk><data:iso>da</data:iso></data:entry>
	<data:entry><data:wk>deu</data:wk><data:iso>de</data:iso></data:entry>
	<data:entry><data:wk>ell</data:wk><data:iso>el</data:iso></data:entry>
	<data:entry><data:wk>eng</data:wk><data:iso>en</data:iso></data:entry>
	<data:entry><data:wk>epo</data:wk><data:iso>eo</data:iso></data:entry>
	<data:entry><data:wk>est</data:wk><data:iso>et</data:iso></data:entry>
	<data:entry><data:wk>eus</data:wk><data:iso>eu</data:iso></data:entry>
	<data:entry><data:wk>fao</data:wk><data:iso>fo</data:iso></data:entry>
	<data:entry><data:wk>fin</data:wk><data:iso>fi</data:iso></data:entry>
	<data:entry><data:wk>fra</data:wk><data:iso>fr</data:iso></data:entry>
	<data:entry><data:wk>gle</data:wk><data:iso>ga</data:iso></data:entry>
	<data:entry><data:wk>glg</data:wk><data:iso>gl</data:iso></data:entry>
	<data:entry><data:wk>heb</data:wk><data:iso>he</data:iso></data:entry>
	<data:entry><data:wk>hrv</data:wk><data:iso>hr</data:iso></data:entry>
	<data:entry><data:wk>hun</data:wk><data:iso>hu</data:iso></data:entry>
	<data:entry><data:wk>hye</data:wk><data:iso>hy</data:iso></data:entry>
	<data:entry><data:wk>isl</data:wk><data:iso>is</data:iso></data:entry>
	<data:entry><data:wk>ita</data:wk><data:iso>it</data:iso></data:entry>
	<data:entry><data:wk>jpn</data:wk><data:iso>ja</data:iso></data:entry>
	<data:entry><data:wk>kat</data:wk><data:iso>ka</data:iso></data:entry>
	<data:entry><data:wk>kor</data:wk><data:iso>ko</data:iso></data:entry>
	<data:entry><data:wk>lat</data:wk><data:iso>la</data:iso></data:entry>
	<data:entry><data:wk>lav</data:wk><data:iso>lv</data:iso></data:entry>
	<data:entry><data:wk>lit</data:wk><data:iso>lt</data:iso></data:entry>
	<data:entry><data:wk>ltz</data:wk><data:iso>lb</data:iso></data:entry>
	<data:entry><data:wk>mkd</data:wk><data:iso>mk</data:iso></data:entry>
	<data:entry><data:wk>mlt</data:wk><data:iso>mt</data:iso></data:entry>
	<data:entry><data:wk>mol</data:wk><data:iso>ro</data:iso></data:entry>
	<data:entry><data:wk>nld</data:wk><data:iso>nl</data:iso></data:entry>
	<data:entry><data:wk>nor</data:wk><data:iso>no</data:iso></data:entry>
	<data:entry><data:wk>pol</data:wk><data:iso>pl</data:iso></data:entry>
	<data:entry><data:wk>por</data:wk><data:iso>pt</data:iso></data:entry>
	<data:entry><data:wk>ron</data:wk><data:iso>ro</data:iso></data:entry>
	<data:entry><data:wk>rus</data:wk><data:iso>ru</data:iso></data:entry>
	<data:entry><data:wk>slk</data:wk><data:iso>sk</data:iso></data:entry>
	<data:entry><data:wk>slv</data:wk><data:iso>sl</data:iso></data:entry>
	<data:entry><data:wk>spa</data:wk><data:iso>es</data:iso></data:entry>
	<data:entry><data:wk>sqi</data:wk><data:iso>sq</data:iso></data:entry>
	<data:entry><data:wk>srp</data:wk><data:iso>sr</data:iso></data:entry>
	<data:entry><data:wk>swe</data:wk><data:iso>sv</data:iso></data:entry>
	<data:entry><data:wk>tur</data:wk><data:iso>tr</data:iso></data:entry>
	<data:entry><data:wk>ukr</data:wk><data:iso>uk</data:iso></data:entry>
	<data:entry><data:wk>zho</data:wk><data:iso>zh</data:iso></data:entry>
</xsl:variable>

<xsl:key name="iso-639-1-2" match="data:entry" use="data:wk"/>

<xsl:function name="fun:wk2iso-lang" as="xs:string">
	<xsl:param name="wk" as="xs:string"/>
	<xsl:value-of>
		<xsl:for-each select="$language-code">
			<xsl:variable name="iso" select="key('iso-639-1-2',$wk)" as="element()*"/>
			<xsl:value-of select="if (count($iso)=0) then $wk else $iso[1]/data:iso"/>
		</xsl:for-each>
	</xsl:value-of>
</xsl:function>

<xsl:variable name="deu2eng">
	<data:entry><data:de>vorschrift</data:de><data:en>legislation</data:en></data:entry>
	<data:entry><data:de>vorschrift/typ</data:de><data:en>legislation/type</data:en></data:entry>
	<data:entry><data:de>entscheidung</data:de><data:en>jurisprudence</data:en></data:entry>
	<data:entry><data:de>entscheidung/typ</data:de><data:en>jurisprudence/type</data:en></data:entry>
	<data:entry><data:de>entscheidungssammlung eintrag</data:de><data:en>jurisprudence_collection_entry</data:en></data:entry>
	<data:entry><data:de>pressemitteilung</data:de><data:en>press_release</data:en></data:entry>
	<data:entry><data:de>pressemitteilung/typ</data:de><data:en>press_release/type</data:en></data:entry>
	<data:entry><data:de>aufsatz</data:de><data:en>journal_article</data:en></data:entry>
	<data:entry><data:de>rezension</data:de><data:en>journal_article</data:en></data:entry>
	<data:entry><data:de>beitrag</data:de><data:en>contribution</data:en></data:entry>
	<data:entry><data:de>beitrag-rn</data:de><data:en>contribution</data:en></data:entry>
	<data:entry><data:de>kommentierung</data:de><data:en>comment</data:en></data:entry>
	<data:entry><data:de>kommentierung-rn</data:de><data:en>comment</data:en></data:entry>
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
	<data:entry><data:de>rezension</data:de><data:bibo>Report</data:bibo></data:entry>
	<data:entry><data:de>aufsatz</data:de><data:bibo>Thesis</data:bibo></data:entry>
	<data:entry><data:de>aufsatz-es</data:de><data:bibo>Thesis</data:bibo></data:entry>
	<data:entry><data:de>pressemitteilung</data:de><data:bibo>LegalCaseDocument</data:bibo></data:entry>
	<data:entry><data:de>beitrag</data:de><data:bibo>Article</data:bibo></data:entry>
	<data:entry><data:de>beitrag-rn</data:de><data:bibo>Article</data:bibo></data:entry>
	<data:entry><data:de>kommentierung</data:de><data:bibo>Note</data:bibo></data:entry>
	<data:entry><data:de>kommentierung-rn</data:de><data:bibo>Note</data:bibo></data:entry>
	<data:entry><data:de>lexikon</data:de><data:bibo>ReferenceSource</data:bibo></data:entry>
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
			<xsl:value-of select="fun:percentEncode(if (count($id)=0) then $name else $id[1]/@technical)"/>
		</xsl:for-each>
	</xsl:value-of>
</xsl:function>

<xsl:template name="court-concept">
	<xsl:param name="name" as="xs:string"/>
	<skos:Concept>
		<skos:prefLabel><xsl:value-of select="$name"/></skos:prefLabel>
		<xsl:for-each select="$courts">
			<xsl:variable name="id" select="key('court',$name)" as="element()*"/>
			<xsl:for-each select="$id">
				<dcterms:identifier><xsl:value-of select="./@technical"/></dcterms:identifier>
			</xsl:for-each>
		</xsl:for-each>
	</skos:Concept>
</xsl:template>

<!-- GENERAL UTILITY FUNCTIONS -->
<xsl:function name="fun:dateDe2Iso" as="xs:string">
	<xsl:param name="date" as="xs:string"/>
	<xsl:choose>
		<xsl:when test="string-length($date) &gt; 0">
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
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="''"/>
		</xsl:otherwise>
	</xsl:choose>
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
	<xsl:value-of select="if (normalize-space($l)=('','deu')) then 'de' else fun:wk2iso-lang(normalize-space($l))"/>
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

<xsl:function name="fun:bez-wert-id" as="xs:string">
	<xsl:param name="bez" as="xs:string"/>
	<xsl:param name="wert" as="xs:string"/>
	<xsl:value-of select="concat('/b.',fun:percentEncode($bez),'_w.',fun:percentEncode(if ($wert='0') then '' else $wert))"/>
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