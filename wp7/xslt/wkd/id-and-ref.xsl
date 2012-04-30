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
			<xsl:when test="$doc-type='esa-eintrag'">
				<xsl:if test="not($doc/zuordnung-produkt/verweis-esa)">
					<xsl:message terminate="yes">No identifier (zuordnung-produkt/verweis-esa) found for this document of type[<xsl:value-of select="$doc-type"/>].</xsl:message>
				</xsl:if>
				<xsl:variable name="identifier" select="$doc/zuordnung-produkt/verweis-esa[1]" as="element()"/>
				<xsl:value-of select="fun:verweis-esa($identifier,true())"/>
			</xsl:when>
			<xsl:when test="$doc-type='aufsatz'">
				<xsl:if test="not($doc/zuordnung-produkt/verweis-zs)">
					<xsl:message terminate="yes">No identifier (zuordnung-produkt/verweis-zs) found for this document of type[<xsl:value-of select="$doc-type"/>].</xsl:message>
				</xsl:if>
				<xsl:variable name="identifier" select="$doc/zuordnung-produkt/verweis-zs[1]" as="element()"/>
				<xsl:value-of select="fun:verweis-zs-id($identifier)"/>
			</xsl:when>
			<xsl:when test="$doc-type='rezension'">
				<xsl:if test="not($doc/zuordnung-produkt/verweis-zs)">
					<xsl:message terminate="yes">No identifier (zuordnung-produkt/verweis-zs) found for this document of type[<xsl:value-of select="$doc-type"/>].</xsl:message>
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
						<xsl:message terminate="yes">No identifier for aufsatz-es found for this document of type[<xsl:value-of select="$doc-type"/>].</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$doc-type='pressemitteilung'">
				<xsl:value-of select="$doc/@vtext-id"/>
			</xsl:when>
			<xsl:when test="$doc-type=('beitrag','beitrag-rn','lexikon')">
			    <!-- take the first link giving a product -->
				<xsl:if test="count($doc/zuordnung-produkt/*[string-length(@produkt) &gt; 0])=0">
					<xsl:message terminate="yes">No identifier (zuordnung-produkt/*[@produkt]) found for this document of type[<xsl:value-of select="$doc-type"/>].</xsl:message>
				</xsl:if>
				<xsl:variable name="product" select="$doc/zuordnung-produkt/*[string-length(@produkt) &gt; 0][1]" as="element()"/>
				<xsl:variable name="identifier">
					<xsl:choose>
						<xsl:when test="name($product)='verweis-esa'">
							<xsl:message terminate="yes">No identifier logic yet for (zuordnung-produkt/verweis-esa for document of type[<xsl:value-of select="$doc-type"/>].</xsl:message>
						</xsl:when>
						<xsl:when test="name($product)='verweis-zs'">
							<xsl:value-of select="fun:verweis-zs-id($product)"/>
						</xsl:when>
						<xsl:when test="name($product)='verweis-komhbe'">
							<xsl:value-of select="concat($product/@produkt, fun:bez-wert-id(if ($doc/@bez) then $doc/@bez else '',if ($doc/@wert) then $doc/@wert else ''))"/>
						</xsl:when>
						<xsl:when test="name($product)='ep-produkt'">
							<xsl:value-of select="concat($product/@produkt,
								if ($product/@newsletter) then concat('/newsletter.',fun:percentEncode($product/@newsletter)) else '',
								if ($product/@datum-gueltig-von) then concat('/from.',fun:dateDe2Iso($product/@datum-gueltig-von)) else ''
								)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:message terminate="yes">Unknown identifier (zuordnung-produkt/<xsl:value-of select="name($product)"/>) for document of type[<xsl:value-of select="$doc-type"/>].</xsl:message>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<!--xsl:value-of select="concat($doc/@typ, fun:bez-wert-id(if ($doc/@bez) then $doc/@bez else '',if ($doc/@wert) then $doc/@wert else ''),$identifier,fun:idOfZuordnungProduktRubrik($doc/zuordnung-produkt))"/-->
				<xsl:value-of select="concat($identifier,fun:idOfZuordnungProduktRubrik($doc/zuordnung-produkt))"/>
			</xsl:when>
			<!-- Todo-->
			<xsl:when test="$doc-type=('kommentierung','kommentierung-rn')">
			    <!-- take the first link giving a product -->
				<xsl:if test="string-length($doc/zuordnung-produkt/verweis-komhbe/@produkt) = 0">
					<xsl:message terminate="yes">No product identifier (zuordnung-produkt/verweis-komhbe/@produkt) found for this document of type[<xsl:value-of select="$doc-type"/>].</xsl:message>
				</xsl:if>
				<xsl:variable name="v-vs" as="element()*" select="
					if ($doc/zuordnung-produkt/verweis-komhbe/verweis-vs[@vsk][not(normalize-space(@vsk)=('','unbekannt'))])
					   then $doc/zuordnung-produkt/verweis-komhbe/verweis-vs[@vsk][not(normalize-space(@vsk)=('','unbekannt'))]
					else if ($doc/kommentierung-bezug/verweis-vs[@vsk][not(normalize-space(@vsk)=('','unbekannt'))])
					   then $doc/kommentierung-bezug/verweis-vs[@vsk][not(normalize-space(@vsk)=('','unbekannt'))]
					else $doc/zitat-vs[not(normalize-space(@vsk)=('','unbekannt'))]"/>
				<xsl:if test="count($v-vs) = 0">
					<xsl:message terminate="yes">No linked legislation found (checked zuordnung-produkt/verweis-komhbe/verweis-vs, kommentierung-bezug/verweis-vs and zitat-vs).</xsl:message>
				</xsl:if>
				<xsl:variable name="identifier-core" as="xs:string">
					<xsl:value-of select="concat($doc/zuordnung-produkt/verweis-komhbe[string-length(@produkt) &gt; 0]/@produkt,'/',
						$v-vs[1]/@vsk,
						if ($v-vs[1]/@par) then 
							concat('/par_',fun:percentEncode($v-vs[1]/@par),
								if ($v-vs[1]/@par-bis) then 
									concat('-',fun:percentEncode($v-vs[1]/@par-bis)) 
								else ''
								) 
						else if ($v-vs[1]/@art) then 
							concat('/art_',fun:percentEncode($v-vs[1]/@art), 
								if ($v-vs[1]/@art-bis) then 
									concat('-',fun:percentEncode($v-vs[1]/@art-bis)) 
								else ''
								)
						else ''
						)"/>
				</xsl:variable>
				<xsl:variable name="identifier-suffix" as="xs:string">
					<xsl:variable name="bez-lc" select="lower-case(normalize-space($doc/@bez))" as="xs:string"/>
					<xsl:variable name="bez"
					   select="if ($bez-lc=('komentierung','kommentirung','kommmentierung'))
					               then 'kommentierung'
							   else if ($bez-lc=('vorbemekung','vorbemerkungen','vorbermerkung','vorwort'))
							       then 'vorbemerkung'
							   else if ($bez-lc='praeambel')
							       then 'praeamble'
							   else if (substring($bez-lc,1,6)='anlage')
							       then 'anlage'
							   else $bez-lc" as="xs:string"/>
					<xsl:choose>
						<xsl:when test="$bez='kommentierung'">
							<xsl:value-of select="''"/>
						</xsl:when>
						<xsl:when test="$bez=('vorbemerkung','praeamble')">
							<xsl:value-of select="fun:bez-wert-id($bez,'')"/>
						</xsl:when>
						<xsl:when test="$bez=('anhang','anlage')">
							<xsl:value-of select="fun:bez-wert-id($bez,if ($doc/@wert) then $doc/@wert else '')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:message terminate="yes">Unknown kommentierung(-rn)/@bez = '<xsl:value-of select="$doc/@bez"/>'.</xsl:message>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:value-of select="concat($identifier-core, $identifier-suffix)"/>
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
		<xsl:when test="$doc-type=('entscheidung','esa-eintrag')">
			<xsl:value-of select="concat($s-base-uri,$doc-type,'/',$doc/@es-typ)"/>
		</xsl:when>
		<xsl:when test="$doc-type=('aufsatz','aufsatz-es')">
			<xsl:value-of select="concat($s-base-uri,$doc-type,if (string-length($doc/type)=0) then '' else concat('/',$doc/type))"/>
		</xsl:when>
		<xsl:when test="$doc-type=('pressemitteilung','rezension')">
			<xsl:value-of select="concat($s-base-uri,$doc-type,'/',$doc/@typ)"/>
		</xsl:when>
		<xsl:when test="$doc-type=('beitrag','beitrag-rn')">
			<xsl:value-of select="concat($s-base-uri,$doc-type,'/',$doc/@typ)"/>
		</xsl:when>
		<xsl:when test="$doc-type=('kommentierung','kommentierung-rn')">
			<xsl:value-of select="concat($s-base-uri,$doc-type)"/>
		</xsl:when>
		<xsl:when test="$doc-type='lexikon'">
			<xsl:value-of select="concat($s-base-uri,$doc-type)"/>
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
		<xsl:when test="name($element)='vorschrift'">
			<xsl:value-of select="$b-uri"/>
		</xsl:when>
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
			<xsl:variable name="id" select="fun:bez-wert-id(if ($element/@bez) then $element/@bez else '' ,if ($element/@wert) then $element/@wert else '')" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/vs-ebene',$id)"/>
		</xsl:when>
		<xsl:when test="name($element)='vs-anlage'">
			<xsl:variable name="n" select="fun:percentEncode(normalize-space($element/@anlage-nr))" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/',fun:deu2eng(if ($element/@typ) then $element/@typ else 'anlage'),
			                             if (string-length($n) = 0) then '' else concat('/',$n))"/>
		</xsl:when>
		<xsl:when test="name($element)='vs-anlage-ebene'">
			<xsl:variable name="id" select="fun:bez-wert-id(if ($element/@bez) then $element/@bez else '' ,if ($element/@wert) then $element/@wert else '')" as="xs:string"/>
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
			<xsl:variable name="id" select="fun:bez-wert-id(if ($element/@bez) then $element/@bez else '' ,if ($element/@wert) then $element/@wert else '')" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/aufsatz-ebene',$id)"/>
		</xsl:when>
		<xsl:when test="name($element)=('beitrag-ebene','beitrag-rn-ebene')">
			<xsl:variable name="a-id" select="$element/ancestor-or-self::*[name()=name($element)]" as="element()*"/>
			<xsl:variable name="id-s" as="xs:string*">
				<xsl:for-each select="$a-id">
					<xsl:value-of select="fun:bez-wert-id(if (./@bez) then ./@bez else '' ,if (./@wert) then ./@wert else '')"/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="ids" select="string-join($id-s,'')" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/beitrag-ebene',$ids)"/>
		</xsl:when>
		<xsl:when test="name($element)=('kommentierung-ebene','kommentierung-rn-ebene')">
			<xsl:variable name="id" select="fun:bez-wert-id(if ($element/@bez) then $element/@bez else '' ,if ($element/@wert) then $element/@wert else '')" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/kommentierung-ebene',$id)"/>
		</xsl:when>
		<xsl:when test="name($element)='anlage'">
			<xsl:variable name="id" select="fun:percentEncode(normalize-space($element/@anlage-nr))" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/anlage_',$id)"/>
		</xsl:when>
		<xsl:when test="name($element)='anlage-ebene'">
			<xsl:variable name="id" select="fun:bez-wert-id(if ($element/@bez) then $element/@bez else '' ,if ($element/@wert) then $element/@wert else '')" as="xs:string"/>
			<xsl:variable name="n" select="fun:percentEncode(normalize-space($element/ancestor::analage[1]/@anlage-nr))" as="xs:string"/>
			<xsl:value-of select="concat($b-uri,'/anlage-ebene',
			                if (string-length($n) = 0) then '' else concat('_',$n),
			                if (string-length($id) = 0) then '' else $id)"/>
		</xsl:when>
		<xsl:when test="name($element)='vs-absatz'">
			<xsl:value-of select="concat($b-uri,'/par/',fun:percentEncode($element/../@par),'/section/',fun:percentEncode(string($element/@nr)))"/>
		</xsl:when>
		<xsl:otherwise>
			<!--
			todo: lex, ...
			 -->
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
	<xsl:param name="isRef" as="xs:boolean"/>
	<!-- 
Here we have different patterns again, like in journal.  The most commons are:
produkt + vsk + par + nr 
produkt + vsk + fach 
produkt + band + nr + start-seite + end-seite
produkt + band + nr + start-seite + end-seite + pos-auf-seite
produkt + band + nr + start-seite + pos-auf-seite
produkt + band + nr + start-seite
produkt + band + heft + nr + start-seite + end-seite + pos-auf-seite
produkt + band + heft + nr + start-seite + end-seite
produkt + band + heft + nr + start-seite + pos-auf-seite
produkt + band + heft + nr + start-seite
produkt + band + heft + nr + start-seite + end-seite
produkt + fach + al + start-seite
produkt + fach + al + start-seite + end-seite

Sometimes there is also "jahrgang" or "al" or others tagged, but this is not necessary for the uri.
If two patterns like band and seite on the one hand and vsk etc. on the other hand are available,
then the uri should be based on the vsk logic only.
-->
	<xsl:variable name="linked-doc-base-uri" as="xs:string" select="concat($r-base-uri,fun:deu2eng('entscheidungssammlung eintrag'),'/')"/>
	<xsl:variable name="uri">
		<xsl:value-of select="fun:percentEncode($e/@produkt)"/>
		<xsl:choose>
			<xsl:when test="$e/@vsk">
				<xsl:value-of select="concat('/vs.',$e/@vsk)"/>
				<xsl:value-of select="if (string-length($e/@fach) &gt; 0) then concat('/sequence.',fun:percentEncode($e/@fach)) else ''"/>
				<xsl:value-of select="if (string-length($e/@par) &gt; 0) then concat('/par.',fun:percentEncode($e/@par)) else ''"/>
				<xsl:value-of select="if (string-length($e/@nr) &gt; 0) then concat('/nbr.',fun:percentEncode($e/@nr)) else ''"/>
			</xsl:when>
			<xsl:when test="$e/@band">
				<xsl:value-of select="if (string-length($e/@band) &gt; 0) then concat('/volume.',fun:percentEncode($e/@band)) else ''"/>
				<xsl:value-of select="if (string-length($e/@heft) &gt; 0) then concat('/issue.',fun:percentEncode($e/@heft)) else ''"/>
				<xsl:value-of select="if (string-length($e/@nr) &gt; 0) then concat('/nbr.',fun:percentEncode($e/@nr)) else ''"/>
				<xsl:value-of select="if (string-length($e/@start-seite) &gt; 0) then concat('/start.',fun:percentEncode($e/@start-seite)) else ''"/>
				<xsl:value-of select="if (string-length($e/@end-seite) &gt; 0) then concat('/end.',fun:percentEncode($e/@end-seite)) else ''"/>
				<xsl:value-of select="if (string-length($e/@pos-auf-seite) &gt; 0) then concat('/position.',fun:percentEncode($e/@pos-auf-seite)) else ''"/>
			</xsl:when>
			<xsl:when test="$e/@fach">
				<xsl:value-of select="if (string-length($e/@fach) &gt; 0) then concat('/sequence.',fun:percentEncode($e/@fach)) else ''"/>
				<xsl:value-of select="if (string-length($e/@al) &gt; 0) then concat('/update.',fun:percentEncode($e/@al)) else ''"/>
				<xsl:value-of select="if (string-length($e/@start-seite) &gt; 0) then concat('/start.',fun:percentEncode($e/@start-seite)) else ''"/>
				<xsl:value-of select="if (string-length($e/@end-seite) &gt; 0) then concat('/end.',fun:percentEncode($e/@end-seite)) else ''"/>
			</xsl:when>
			<xsl:when test="$e/@al">
				<xsl:value-of select="if (string-length($e/@al) &gt; 0) then concat('/update.',fun:percentEncode($e/@al)) else ''"/>
				<xsl:value-of select="if (string-length($e/@nr) &gt; 0) then concat('/nbr.',fun:percentEncode($e/@nr)) else ''"/>
				<xsl:value-of select="if (string-length($e/@start-seite) &gt; 0) then concat('/start.',fun:percentEncode($e/@start-seite)) else ''"/>
				<xsl:value-of select="if (string-length($e/@end-seite) &gt; 0) then concat('/end.',fun:percentEncode($e/@end-seite)) else ''"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="$isRef">
					<xsl:message terminate="yes">the esa-eintrag identifier is not well defined identifer: <xsl:value-of select="fun:print-element($e)"/>.</xsl:message>
				</xsl:if>
				<xsl:message terminate="no">refering an identifier that is not well defined: <xsl:value-of select="fun:print-element($e)"/>.</xsl:message>
				<xsl:value-of select="'/invalid-reference'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:value-of select="concat($linked-doc-base-uri,normalize-space($uri))"/>
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
produkt + vsk + art
produkt + vsk + art + rn
produkt + vsk + par + abs
produkt + vsk + art + abs
-->
	<xsl:variable name="linked-doc-base-uri" as="xs:string" select="concat($r-base-uri,fun:deu2eng('kommentierung'),'/')"/>
	<xsl:variable name="uri">
		<xsl:value-of select="fun:percentEncode($e/@produkt)"/>
		<xsl:choose>
			<xsl:when test="$e/verweis-vs">
				<xsl:variable name="vs" select="$e/verweis-vs[1]" as="element()"/>
				<xsl:value-of select="concat('/vs.',$vs/@vsk)"/>
				<xsl:value-of select="if (string-length($vs/@art) &gt; 0) then concat('/art/',fun:percentEncode($vs/@art)) else ''"/>
				<xsl:value-of select="if (string-length($vs/@par) &gt; 0) then concat('/par/',fun:percentEncode($vs/@par)) else ''"/>
				<xsl:value-of select="if (string-length($vs/@abs) &gt; 0) then concat('/section/',fun:percentEncode($vs/@abs)) else ''"/>
			</xsl:when>
			<xsl:when test="$e/verweis-es">
				<xsl:variable name="es" select="$e/verweis-es[1]" as="element()"/>
				<xsl:value-of select="concat('/es.',fun:verweis-es-id($es))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="if (string-length($e/@jahr) &gt; 0) then concat('/volume.',fun:percentEncode($e/@jahr)) else ''"/>
				<xsl:value-of select="if (string-length($e/@auflage) &gt; 0) then concat('/edition.',fun:percentEncode($e/@auflage)) else ''"/>
				<xsl:value-of select="if (string-length(concat($e/@bez1,$e/@wert1)) &gt; 0) then fun:bez-wert-id(if ($e/@bez1) then $e/@bez1 else '',if ($e/@wert1) then $e/@wert1 else '') else ''"/>
				<xsl:value-of select="if (string-length(concat($e/@bez2,$e/@wert2)) &gt; 0) then fun:bez-wert-id(if ($e/@bez2) then $e/@bez2 else '',if ($e/@wert2) then $e/@wert2 else '') else ''"/>
				<xsl:value-of select="if (string-length(concat($e/@bez3,$e/@wert3)) &gt; 0) then fun:bez-wert-id(if ($e/@bez3) then $e/@bez3 else '',if ($e/@wert3) then $e/@wert3 else '') else ''"/>
				<xsl:value-of select="if (string-length(concat($e/@bez4,$e/@wert4)) &gt; 0) then fun:bez-wert-id(if ($e/@bez4) then $e/@bez4 else '',if ($e/@wert4) then $e/@wert4 else '') else ''"/>
				<xsl:value-of select="if (string-length(concat($e/@bez5,$e/@wert5)) &gt; 0) then fun:bez-wert-id(if ($e/@bez5) then $e/@bez5 else '',if ($e/@wert5) then $e/@wert5 else '') else ''"/>
				<xsl:value-of select="if (string-length(concat($e/@bez6,$e/@wert6)) &gt; 0) then fun:bez-wert-id(if ($e/@bez6) then $e/@bez6 else '',if ($e/@wert6) then $e/@wert6 else '') else ''"/>
				<xsl:value-of select="if (string-length(concat($e/@bez7,$e/@wert7)) &gt; 0) then fun:bez-wert-id(if ($e/@bez7) then $e/@bez7 else '',if ($e/@wert7) then $e/@wert7 else '') else ''"/>
				<xsl:value-of select="if (string-length(concat($e/@bez8,$e/@wert8)) &gt; 0) then fun:bez-wert-id(if ($e/@bez8) then $e/@bez8 else '',if ($e/@wert8) then $e/@wert8 else '') else ''"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="if (string-length($e/@rn) &gt; 0) then concat('/rn.',fun:percentEncode($e/@rn)) else ''"/>
	</xsl:variable>
	<xsl:value-of select="concat($linked-doc-base-uri,normalize-space($uri))"/>
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
	<!-- to do -->
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
	<xsl:call-template name="write-reference">
		<xsl:with-param name="target" select="fun:verweis-esa(.,false())"/>
	</xsl:call-template>
</xsl:template>

<xsl:template match="verweis-komhbe">
	<xsl:call-template name="write-reference">
		<xsl:with-param name="target" select="fun:verweis-komhbe(.)"/>
	</xsl:call-template>
	<xsl:apply-templates select="verweis-vs | verweis-es"/>
</xsl:template>

<xsl:template match="verweis-zs">
	<xsl:call-template name="write-reference">
		<xsl:with-param name="target" select="fun:verweis-zs(.)"/>
	</xsl:call-template>
</xsl:template>

<xsl:template match="verweis-url">
	<xsl:param name="referenceType" tunnel="yes" as="xs:string" select="''"/>
	<xsl:variable name="t" as="xs:string*">
		<xsl:variable name="href" select="fun:verweis-url(.)" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="@typ = 'mailto'">
				<xsl:value-of select="if (contains($href,'://')) then $href else concat('mailto://',$href)"/>
			</xsl:when>
			<xsl:when test="@typ = 'http'">
				<xsl:value-of select="if (contains($href,'://')) then $href else concat('http://',$href)"/>
			</xsl:when>
			<xsl:when test="@typ = 'https'">
				<xsl:value-of select="if (contains($href,'://')) then $href else concat('https://',$href)"/>
			</xsl:when>
			<xsl:when test="@typ = 'ftp'">
				<xsl:value-of select="if (contains($href,'://')) then $href else concat('ftp://',$href)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$href"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="href" select="string-join($t,'')" as="xs:string"/>
	<xsl:call-template name="write-reference">
		<xsl:with-param name="referenceType" tunnel="yes" as="xs:string" select="substring-before($href,'://')"/>
		<xsl:with-param name="target" select="$href" as="xs:string"/>
	</xsl:call-template>
</xsl:template>

<xsl:template match="fundstelle">
	<xsl:param name="referenceType" tunnel="yes" as="xs:string" select="''"/>
	<xsl:variable name="rt" as="xs:string" select="if ($referenceType=('quelle')) then $referenceType else name()"/>
	<xsl:apply-templates select="*">
		<xsl:with-param name="referenceType" as="xs:string" tunnel="yes" select="$rt"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="quelle">
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
	<!-- to do -->

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
			<xsl:when test="$referenceType = ('quelle','beitrag-quelle')">
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
		<xsl:apply-templates select="verweis-vs | verweis-es"/>
	</xsl:for-each>
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
		<xsl:when test="$en = 'verweis-esa'"><xsl:value-of select="fun:verweis-esa($e,false())"/></xsl:when>
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

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="kom" userelativepaths="yes" externalpreview="no" url="..\..\Data\kommentierung\Adam_TarifR_oeD_tvue_vka_kommentierung.xml" htmlbaseurl="" outputurl="" processortype="custom" useresolver="no" profilemode="0"
		          profiledepth="" profilelength="" urlprofilexml="" commandline=" net.sf.saxon.Transform -o %3 %1 %2" additionalpath="C:\Program Files\Java\jdk1.5.0_06\jre\bin\java"
		          additionalclasspath="C:\xml\saxon8-6;C:\xml\jaxp\jaxp-1_3-20060207\jaxp-api.jar;C:\xml\jaxp\jaxp-1_3-20060207\dom.jar;C:\xml\jaxp\jaxp-1_3-20060207;C:\xml\saxon8-6\saxon8sa.jar;C:\xml\saxon8-6\saxon8-dom.jar;C:\xml\saxon8-6\saxon8-jdom.jar;C:\xml\saxon8-6\saxon8-sql.jar;C:\xml\saxon8-6\saxon8-xom.jar;C:\xml\saxon8-6\saxon8-xpath.jar;C:\xml\saxon8-6\saxon8.jar"
		          postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bSchemaAware" value="true"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="xsltVersion" value="2.0"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="fatal"/>
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
