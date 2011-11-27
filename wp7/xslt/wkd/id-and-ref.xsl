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

<xsl:import href="functions.xsl"/>
<xsl:import href="aliases.xsl"/>

<!--
 Project LOD2 - Project number 27943
 Work Package - 7
 Author Johan De Smedt
 Description: extract metadata, classification and links from WKD XML 
 Module: id and references module
 Dependencies: the including stylesheet must set 
- v-base-uri
 -->

<!-- DOCUMENT ID MANAGEMENT -->
<xsl:variable name="r-base-uri" select="'http://resource.wolterskluwer.de/'" as="xs:string"/>

<!-- Vorschrift ID and type management -->
<xsl:variable name="s-base-uri" select="'http://schema.wolterskluwer.de/'" as="xs:string"/>

<!-- current document resource URI -->
<xsl:function name="fun:rUri" as="xs:string">
	<xsl:param name="doc" as="element()"/>
	<xsl:variable name="doc-type" select="name($doc)" as="xs:string"/>
	<xsl:variable name="base" select="concat($r-base-uri,fun:deu2eng($doc-type),'/')" as="xs:string"/>
	<xsl:variable name="id" as="xs:string">
		<xsl:choose>
			<xsl:when test="$doc-type='vorschrift'">
				<xsl:value-of select="fun:percentEncode($doc/@vsk)"/>
			</xsl:when>
			<xsl:when test="$doc-type='entscheidung'">
				<xsl:variable name="court" select="fun:courtId(normalize-space($doc/es-metadaten/gericht))" as="xs:string"/>
				<xsl:variable name="file"
				              select="concat(
				                       fun:percentEncode(normalize-space($doc/es-metadaten/az-gruppe/az-haupt/az/wert)),
									   if ($doc/es-metadaten/az-gruppe/az-haupt/az/zusatz) 
									       then concat('-',fun:percentEncode(normalize-space($doc/es-metadaten/az-gruppe/az-haupt/az/zusatz)))
										   else ''
									   )" 
						      as="xs:string"/>
				<xsl:variable name="date" select="fun:dateDe2Iso(normalize-space($doc/es-metadaten/datum))" as="xs:string"/>
				<xsl:value-of select="concat($court,'_',$file,'_',$date)"/>
			</xsl:when>
			<xsl:when test="$doc-type='aufsatz'">
				<xsl:if test="not($doc/zuordnung-produkt/verweis-zs)">
					<xsl:message terminate="yes">No identifier (zuordnung-produkt/verweis-zs) found for this document.</xsl:message>
				</xsl:if>
				<xsl:variable name="identifier" select="$doc/zuordnung-produkt/verweis-zs[1]" as="element()"/>
				<xsl:value-of select="fun:verweis-zs-id($identifier)"/>
			</xsl:when>
			<xsl:when test="$doc-type='aufsatz-es'">
				<xsl:choose>
					<xsl:when test="$doc/zuordnung-produkt/verweis-zs">
						<xsl:variable name="identifier" select="$doc/zuordnung-produkt/verweis-zs[1]" as="element()"/>
						<xsl:value-of select="fun:verweis-zs-id($identifier)"/>
					</xsl:when>
					<xsl:when test="$doc/@bezugsquelle and $doc/verbundene-dokumente/verweis-es">
						<xsl:variable name="identifier" select="$doc/verbundene-dokumente/verweis-es[1]" as="element()"/>
						<xsl:value-of select="concat(fun:verweis-es-id($identifier),'_',$doc/@bezugsquelle)"/>
					</xsl:when>
					<xsl:when test="$doc/metadaten/metadaten-text[@bezeichnung='link-id']">
						<xsl:value-of select="fun:percentEncode(normalize-space($doc/metadaten/metadaten-text[@bezeichnung='link-id']))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">No identifier for aufsatz-es found for this document.</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$doc-type='pressemitteilung'">
				<xsl:value-of select="$doc/@vtext-id"/>
			</xsl:when>
			<!-- Todo-->
			<xsl:when test="$doc-type=('beitrag','beitrag-rn')">
			    <!-- take the first link giving a product -->
				<xsl:variable name="product" select="$doc/zuordnung-produkt/*[string-length(@produkt) &gt; 0][1]" as="element()"/>
				<xsl:if test="count($product)=0">
					<xsl:message terminate="yes">No identifier (zuordnung-produkt/*[@produkt]) found for this document.</xsl:message>
				</xsl:if>
				<xsl:variable name="identifier">
					<xsl:choose>
						<xsl:when test="name($product)='verweis-esa'">
							<xsl:message terminate="yes">No identifier logic yet for (zuordnung-produkt/verweis-esa.</xsl:message>
						</xsl:when>
						<xsl:when test="name($product)='verweis-zs'">
							<xsl:value-of select="fun:verweis-zs-id($product)"/>
						</xsl:when>
						<xsl:when test="name($product)='verweis-komhbe'">
							<xsl:message terminate="yes">No identifier logic yet for (zuordnung-produkt/verweis-komhbe.</xsl:message>
						</xsl:when>
						<xsl:when test="name($product)='ep-produkt'">
							<xsl:value-of select="concat('/', $product/@produkt,
								if ($product/@newsletter) then concat('/newsletter.',fun:percentEncode($product/@newsletter)) else '',
								if ($product/@datum-gueltig-von) then concat('/from.',fun:dateDe2Iso($product/@datum-gueltig-von)) else ''
								)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:message terminate="yes">Unknown identifier (zuordnung-produkt/<xsl:value-of select="name($product)"/>.</xsl:message>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:value-of select="concat($doc/@typ, fun:bez-wert-id($doc/@bez,$doc/@wert),'/',$identifier,fun:idOfZuordnungProduktRubrik($doc/zuordnung-produkt))"/>
			</xsl:when>
			<!-- next type -->
			<xsl:otherwise>
				<xsl:message terminate="yes">ERROR: invalid document type - got <xsl:value-of select="$doc-type"/>.</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:value-of select="concat($base,$id)"/>
</xsl:function>

<xsl:function name="fun:idOfZuordnungProduktRubrik" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:choose>
		<xsl:when test="$e/zuordnung-rubrik">
			<xsl:variable name="this-part" select="fun:bez-wert-id($e/zuordnung-rubrik/@bez,$e/zuordnung-rubrik/@wert)"/>
			<xsl:value-of select="concat($this-part, fun:idOfZuordnungProduktRubrik($e/zuordnung-rubrik))"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="''"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:function>

<!-- current document sub-type - typically used in wkd.xsl -->
<xsl:function name="fun:rType" as="xs:string">
	<xsl:param name="doc" as="element()"/>
	<xsl:variable name="doc-type" select="name($doc)" as="xs:string"/>
	<xsl:choose>
		<xsl:when test="$doc-type='vorschrift'">
			<xsl:value-of select="concat($s-base-uri,$doc-type,'/',$doc/@vs-typ)"/>
		</xsl:when>
		<xsl:when test="$doc-type='entscheidung'">
			<xsl:value-of select="concat($s-base-uri,$doc-type,'/',$doc/@es-typ)"/>
		</xsl:when>
		<xsl:when test="$doc-type=('aufsatz','aufsatz-es')">
			<xsl:value-of select="concat($s-base-uri,$doc-type)"/>
		</xsl:when>
		<xsl:when test="$doc-type='pressemitteilung'">
			<xsl:value-of select="concat($s-base-uri,$doc-type,'/',$doc/@typ)"/>
		</xsl:when>
		<xsl:when test="$doc-type='beitrag'">
			<xsl:value-of select="concat($s-base-uri,$doc-type,'/',$doc/@typ)"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:message terminate="yes">ERROR: invalid document type - "<xsl:value-of select="$doc-type"/>" is not supported yet.</xsl:message>
		</xsl:otherwise>
	</xsl:choose>
</xsl:function>

<!-- fragment identifiers  -->
<xsl:function name="fun:getPartId">
	<xsl:param name="element" as="element()"/>
	<xsl:param name="b-uri" as="xs:string"/>
	<xsl:choose>
		<xsl:when test="name($element)='vorschrift'"/>
		<xsl:when test="name($element)='vs-vorspann'">
			<xsl:value-of select="concat($b-uri,'/description')"/>
		</xsl:when>
		<xsl:when test="name($element)='paragraph'">
			<xsl:value-of select="concat($b-uri,'/par/',fun:percentEncode($element/@par))"/>
		</xsl:when>
		<xsl:when test="name($element)='artikel'">
			<xsl:value-of select="concat($b-uri,'/art/',fun:percentEncode($element/@art))"/>
		</xsl:when>
		<xsl:when test="name($element)='vs-ebene'">
			<xsl:variable name="id" select="fun:bez-wert-id($element/@bez,$element/@wert)" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/vs-ebene',$id)"/>
		</xsl:when>
		<xsl:when test="name($element)='vs-anlage'">
			<xsl:variable name="n" select="fun:percentEncode(normalize-space($element/@anlage-nr))" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/',fun:deu2eng(if ($element/@typ) then $element/@typ else 'anlage'),
			                             if (string-length($n) = 0) then '' else concat('/',$n))"/>
		</xsl:when>
		<xsl:when test="name($element)='vs-anlage-ebene'">
			<xsl:variable name="id" select="fun:bez-wert-id($element/@bez,$element/@wert)" as="xs:string"/>
			<xsl:variable name="n" select="fun:percentEncode(normalize-space($element/@anlage-nr))" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/vs-anlage-ebene',
			                if (string-length($n) = 0) then '' else concat('/',$n),
			                if (string-length($id) = 0) then '' else $id)"/>
		</xsl:when>
		<xsl:when test="name($element)='vs-objekt'">
			<xsl:variable name="n" select="fun:percentEncode(normalize-space($element/@nr))" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/vs-obj/',fun:deu2eng($element/@typ),
			                if (string-length($n) = 0) then '' else concat('/',$n))"/>
		</xsl:when>
		<xsl:when test="name($element)='aufsatz-ebene'">
			<xsl:variable name="id" select="fun:bez-wert-id($element/@bez,$element/@wert)" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/aufsatz-ebene',$id)"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:message terminate="yes">ERROR - unknown identifier type for element: <xsl:value-of select="name($element)"/>.</xsl:message>
		</xsl:otherwise>
	</xsl:choose>
</xsl:function>

<!-- link targets -->
<xsl:function name="fun:verweis-vs" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:variable name="linked-doc-uri" as="xs:string" select="concat($r-base-uri,fun:deu2eng('vorschrift'),'/',fun:percentEncode($e/@vsk))"/>
	<xsl:variable name="linked-main-part" as="xs:string">
		<xsl:choose>
			<xsl:when test="$e/@par"><xsl:value-of select="concat('/par/',fun:percentEncode($e/@par))"/></xsl:when>
			<xsl:when test="$e/@art"><xsl:value-of select="concat('/art/',fun:percentEncode($e/@art))"/></xsl:when>
			<xsl:when test="$e/@anlage-typ">
				<xsl:variable name="n" select="fun:percentEncode(normalize-space($e/@anlage-nr))" as="xs:string"/>
				<xsl:value-of select="concat('/',fun:deu2eng($e/@anlage-typ),if (string-length($n) = 0) then '' else concat('/',$n))"/>
			</xsl:when>
			<xsl:when test="$e/@vs-obj-typ">
				<xsl:variable name="n" select="fun:percentEncode(normalize-space($e/@vs-obj-nr))" as="xs:string"/>
				<xsl:value-of select="concat('/vs-obj/',fun:deu2eng($e/@vs-obj-typ),
				                if (string-length($n) = 0) then '' else concat('/',$n))"/>
			</xsl:when>
			<xsl:when test="$e/@vs-ebene">
				<xsl:variable name="w" select="fun:percentEncode(normalize-space($e/@vs-ebene))" as="xs:string"/>
				<xsl:value-of select="concat('/vs-ebene/',fun:percentEncode($e/@vsk),
				                if (string-length($w) = 0) then '' else concat('/',$w))"/>
			</xsl:when>
			<xsl:when test="$e/@anlage-ebene">
				<xsl:variable name="w" select="fun:percentEncode(normalize-space($e/@anlage-ebene))" as="xs:string"/>
				<xsl:variable name="n" select="fun:percentEncode(normalize-space($e/@anlage-nr))" as="xs:string"/>
				<xsl:value-of select="concat('/vs-anlage-ebene/',
				                if (string-length($n) = 0) then '' else concat('/',$n),
								concat('/',fun:percentEncode($e/@vsk)),
				                if (string-length($w) = 0) then '' else concat('/',$w))"/>
			</xsl:when>
			<xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="linked-section">
		<xsl:if test="$e/@abs and (string-length($linked-main-part) &gt; 0)">
			<xsl:value-of select="concat('/section/',$e/@abs)"/>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="linked-sentence">
		<xsl:if test="$e/@satz and (string-length($linked-section) &gt; 0)">
			<xsl:value-of select="concat('#satz',$e/@nr)"/>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="target" as="xs:string">
		<xsl:choose>
			<xsl:when test="$e/@satz and (string-length($linked-sentence) &gt; 0)">
				<xsl:value-of select="concat($linked-doc-uri,$linked-main-part,$linked-section,$linked-sentence)"/>
			</xsl:when>
			<xsl:when test="$e/@abs and not($e/@satz) and (string-length($linked-section) &gt; 0)">
				<xsl:value-of select="concat($linked-doc-uri,$linked-main-part,$linked-section)"/>
			</xsl:when>
			<xsl:when test="not ($e/@abs) and not($e/@satz) and (string-length($linked-main-part) &gt; 0)">
				<xsl:value-of select="concat($linked-doc-uri,$linked-main-part)"/>
			</xsl:when>
			<xsl:when test="string-length($linked-main-part) = 0">
				<xsl:value-of select="$linked-doc-uri"/>
			</xsl:when>
			<xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:value-of select="$target"/>
</xsl:function>

<xsl:function name="fun:verweis-es-id" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:variable name="linked-doc-id" as="xs:string">
		<xsl:variable name="court" select="fun:courtId($e/@gericht)" as="xs:string"/>
		<xsl:variable name="file"
		              select="concat(
		                       fun:percentEncode(normalize-space($e/@az)),
							   if (string-length(normalize-space($e/@zusatz)) &gt; 0)
							       then concat('-',fun:percentEncode(normalize-space($e/@zusatz)))
								   else ''
							   )" 
				      as="xs:string"/>
		<xsl:variable name="date" select="if (string-length($e/@datum)=0) then '' else concat('_',fun:dateDe2Iso(normalize-space($e/@datum)))" as="xs:string"/>
		<xsl:value-of select="concat($court,'_',$file,$date)"/>
	</xsl:variable>
	<xsl:value-of select="$linked-doc-id"/>
</xsl:function>

<xsl:function name="fun:verweis-es" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:variable name="linked-doc-base-uri" as="xs:string" select="concat($r-base-uri,fun:deu2eng('entscheidung'),'/')"/>
	<xsl:variable name="target" as="xs:string">
		<xsl:value-of select="concat($linked-doc-base-uri,fun:verweis-es-id($e))"/>
	</xsl:variable>
	<xsl:value-of select="$target"/>
</xsl:function>

<xsl:function name="fun:verweis-esa" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:value-of select="''"/>
</xsl:function>

<xsl:function name="fun:verweis-komhbe" as="xs:string">
	<xsl:param name="e" as="element()"/>
<!--
The target document of that link is either beitrag or kommentierung.
- If you want to know about link patterns for that kind of link, I would say the following:
produkt + rn
produkt + bez + wert
produkt + bez + wert + rn
produkt + vsk + rn
produkt + vsk + par + rn
produkt + vsk  + art
produkt + vsk + art + rn
produkt + vsk + par + abs
produkt + vsk + art + abs
-->
	<xsl:value-of select="''"/>
</xsl:function>

<xsl:function name="fun:verweis-zs-id" as="xs:string">
	<xsl:param name="e" as="element()"/>
<!--
    produkt, jahr, heft, beilage, start-seite [, pos-auf-seite][, end-seite]
    produkt, jahr, heft, start-seite [, pos-auf-seite][, end-seite]
    produkt, jahr, start-seite [, pos-auf-seite][, end-seite]
    produkt, band, heft, beilage, start-seite [, pos-auf-seite][, end-seite]
    produkt, band, heft, start-seite [, pos-auf-seite][, end-seite]
-->
	<xsl:variable name="uri">
		<xsl:value-of select="concat(fun:percentEncode($e/@produkt),'/volume.',fun:percentEncode(concat($e/@jahr,$e/@band)))"/>
		<xsl:value-of select="if (string-length($e/@heft) &gt; 0) then concat('/issue.',fun:percentEncode($e/@heft)) else ''"/>
		<xsl:value-of select="if (string-length($e/@beilage) &gt; 0) then concat('/supplement.',fun:percentEncode($e/@beilage)) else ''"/>
		<xsl:value-of select="if (string-length($e/@start-seite) &gt; 0) then concat('/start-p.',fun:percentEncode($e/@start-seite)) else ''"/>
		<xsl:value-of select="if (string-length($e/@pos-auf-seite) &gt; 0) then concat('/position.',fun:percentEncode($e/@pos-auf-seite)) else ''"/>
		<xsl:value-of select="if (string-length($e/@end-seite) &gt; 0) then concat('/end-p.',fun:percentEncode($e/@end-seite)) else ''"/>
	</xsl:variable>
	<xsl:value-of select="normalize-space($uri)"/>
</xsl:function>

<xsl:function name="fun:verweis-zs" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:variable name="uri">
		<xsl:value-of select="concat($r-base-uri,'journal_article/',fun:verweis-zs-id($e))"/>
	</xsl:variable>
	<xsl:value-of select="normalize-space($uri)"/>
</xsl:function>

<xsl:function name="fun:verweis-url" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:variable name="a" select="string($e)" as="xs:string"/>
	<xsl:variable name="h" select="string($e/@adresse)" as="xs:string"/>
	<xsl:value-of select="if (starts-with($a,'http://') and (string-length($h)=0)) then $a else $h"/>
</xsl:function>

<xsl:function name="fun:verweis-obj" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:variable name="uri">
		<xsl:value-of select="concat($r-base-uri,'object/',$e/@referenz)"/>
	</xsl:variable>
	<xsl:value-of select="normalize-space($uri)"/>
</xsl:function>

<xsl:function name="fun:verweis-vtext-id" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:value-of select="''"/>
</xsl:function>

<xsl:function name="fun:verweis-lex" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:value-of select="''"/>
</xsl:function>

<xsl:function name="fun:verweis-va" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:value-of select="''"/>
</xsl:function>

<!-- links -->
<xsl:template match="verweis-vs">
	<xsl:call-template name="write-reference">
		<xsl:with-param name="target" select="fun:verweis-vs(.)"/>
	</xsl:call-template>
</xsl:template>

<xsl:template match="verweis-es">
	<xsl:call-template name="write-reference">
		<xsl:with-param name="target" select="fun:verweis-es(.)"/>
	</xsl:call-template>
</xsl:template>

<xsl:template match="verweis-esa">
</xsl:template>

<xsl:template match="verweis-komhbe">
</xsl:template>

<xsl:template match="verweis-zs">
	<xsl:call-template name="write-reference">
		<xsl:with-param name="target" select="fun:verweis-zs(.)"/>
	</xsl:call-template>
</xsl:template>

<xsl:template match="verweis-url">
	<xsl:call-template name="write-reference">
		<xsl:with-param name="target" select="fun:verweis-url(.)"/>
	</xsl:call-template>
</xsl:template>

<xsl:template match="fundstelle">
	<xsl:apply-templates select="*">
		<xsl:with-param name="referenceType" as="xs:string" tunnel="yes" select="name()"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="verweis-obj">
	<xsl:call-template name="write-reference">
		<xsl:with-param name="target" select="fun:verweis-obj(.)"/>
	</xsl:call-template>
</xsl:template>

<xsl:template match="verweis-vtext-id"/>

<xsl:template match="verweis-lex"/>

<xsl:template match="verweis-va"/>

<xsl:template match="zwischen-zeichen"/>

<xsl:template name="write-reference">
	<xsl:param name="target" as="xs:string"/>
	<xsl:param name="referenceType" as="xs:string" tunnel="yes" select="''"/>
	<xsl:if test="string-length($target) &gt; 0">
		<xsl:choose>
			<xsl:when test="$referenceType = 'fundstelle'">
				<dcterms:source rdf:resource="{$target}"/>
			</xsl:when>
			<xsl:when test="$referenceType = 'pm-quelle'">
				<dcterms:source rdf:resource="{$target}"/>
			</xsl:when>
			<xsl:when test="$referenceType = 'fundstellen'">
				<bibo:reproducedIn rdf:resource="{$target}"/>
			</xsl:when>
			<xsl:when test="$referenceType = 'normenkette'">
				<wkd:legalBase rdf:resource="{$target}"/>
			</xsl:when>
			<xsl:when test="$referenceType = 'zuordnung-produkt'">
				<wkd:reproducedAs rdf:resource="{$target}"/>
			</xsl:when>
			<xsl:when test="$referenceType = 'mailto'">
				<foaf:mbox rdf:resource="{$target}"/>
			</xsl:when>
			<xsl:when test="$referenceType = 'pm-entscheidung'">
				<dcterms:references>
					<bibo:ReferenceSource rdf:about="{$target}"/>
				</dcterms:references>
			</xsl:when>
			<xsl:otherwise>
				<dcterms:references rdf:resource="{$target}"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:if>
</xsl:template>

<!-- Set parameterized reference -->
<xsl:template match="zuordnung-produkt">
	<xsl:variable name="rt" select="name()" as="xs:string"/>
	<xsl:for-each select="*">
		<xsl:variable name="object" as="xs:string" select="fun:link-target(.)"/>
		<xsl:if test="string-length($object) &gt; 0">
			<xsl:call-template name="write-reference">
				<xsl:with-param name="referenceType" select="$rt" tunnel="yes" as="xs:string"/>
				<xsl:with-param name="target" select="$object" as="xs:string"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:for-each>
	<xsl:apply-templates select="*/autor"/>
</xsl:template>

<xsl:template match="zuordnung-produkt" mode="top-level"/>

<xsl:template match="verbundene-dokumente" mode="top-level">
	<xsl:param name="p-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="typ" select="@typ"/>
	<xsl:variable name="priority" select="@prioritaet"/>
	<xsl:for-each select="*">
		<xsl:variable name="object" as="xs:string" select="fun:link-target(.)"/>
		<xsl:if test="string-length($object) &gt; 0">
			<rdf:Description rdf:about="{$p-uri}">
				<dcterms:references rdf:resource="{$object}"/>
			</rdf:Description>
			<owl:Axiom>
				<owl:annotatedSource rdf:resource="{$p-uri}"/>
				<owl:annotatedProperty rdf:resource="{$dcterms}references"/>
				<owl:annotatedTarget rdf:resource="{$object}"/>
				<wkd:referenceType>
					<wkd:ReferenceType rdf:about="{$v-base-uri}verbundene-dokumente/{fun:percentEncode($typ)}">
						<skos:prefLabel xml:lang="de"><xsl:value-of select="$typ"/></skos:prefLabel>
					</wkd:ReferenceType>
				</wkd:referenceType>
				<xsl:if test="string-length($priority) &gt; 0">
					<wkd:priority><xsl:value-of select="$priority"/></wkd:priority>
				</xsl:if>
			</owl:Axiom>
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template match="verbundene-dokumente"/>

<xsl:function name="fun:link-target" as="xs:string">
	<xsl:param name="e" as="element()"/>
	<xsl:variable name="en" select="name($e)"/>
	<xsl:choose>
		<xsl:when test="$en = 'verweis-vs'"><xsl:value-of select="fun:verweis-vs($e)"/></xsl:when>
		<xsl:when test="$en = 'verweis-es'"><xsl:value-of select="fun:verweis-es($e)"/></xsl:when>
		<xsl:when test="$en = 'verweis-esa'"><xsl:value-of select="fun:verweis-esa($e)"/></xsl:when>
		<xsl:when test="$en = 'verweis-komhbe'"><xsl:value-of select="fun:verweis-komhbe($e)"/></xsl:when>
		<xsl:when test="$en = 'verweis-zs'"><xsl:value-of select="fun:verweis-zs($e)"/></xsl:when>
		<xsl:when test="$en = 'verweis-url'"><xsl:value-of select="fun:verweis-url($e)"/></xsl:when>
		<xsl:when test="$en = 'verweis-obj'"><xsl:value-of select="fun:verweis-obj($e)"/></xsl:when>
		<xsl:when test="$en = 'verweis-vtext-id'"><xsl:value-of select="fun:verweis-vtext-id($e)"/></xsl:when>
		<xsl:when test="$en = 'verweis-lex'"><xsl:value-of select="fun:verweis-lex($e)"/></xsl:when>
		<xsl:when test="$en = 'verweis-va'"><xsl:value-of select="fun:verweis-va($e)"/></xsl:when>
		<xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
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