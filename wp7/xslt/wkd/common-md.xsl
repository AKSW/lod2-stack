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
 exclude-result-prefixes="xsl xs fun data"
>

<!--
 Project LOD2 - Project number 27943
 Work Package - 7
 Author Johan De Smedt
 Description: extract metadata, classification and links from WKD XML 
 Module: Transforms common wkd metadata into rdf
- language on any literal
- metadaten/metadaten-gruppe[@bezeichnung='createmodifiedat']
- metadaten/metadaten-gruppe[@bezeichnung='handler[[keywords]]']
- metadaten/metadaten-gruppe[@bezeichnung='handler[[categories]]']
- metadaten/metadaten-gruppe[@bezeichnung='handler[[vwdata]]']
- rechtsgebiete/rechtsgebiet-eintrag
- taxonomien
- organisation
- stichworte, stichwort and inline-stichwort
-->

<xsl:import href="functions.xsl"/>
<xsl:import href="aliases.xsl"/>
<xsl:import href="content.xsl"/>

<xsl:variable name="v-base-uri" select="'http://vocab.wolterskluwer.de/'" as="xs:string"/>

<!-- language -->
<xsl:template name="setLanguage">
	<dcterms:language><xsl:value-of select="if (@sprache) then @sprache else 'de'"/></dcterms:language>
</xsl:template>

<xsl:template match="titel" mode="plain-literal">
	<xsl:variable name="title">
		<xsl:for-each select="text()">
			<xsl:value-of select="."/>
			<xsl:if test="not(last() = position())">
				<xsl:text> </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>
	<xsl:value-of select="normalize-space($title)"/>
</xsl:template>

<!-- metadata -->
<xsl:template match="metadaten-gruppe[@bezeichnung='createmodifiedat']">
	<xsl:if test="metadaten-text[@bezeichnung='createdat'][string-length(.)]">
		<dcterms:created><xsl:value-of select="fun:dateDe2Iso(string(metadaten-text[@bezeichnung='createdat']))"/></dcterms:created>
	</xsl:if>
	<xsl:if test="metadaten-text[@bezeichnung='modifiedat'][string-length(.)]">
		<dcterms:modified><xsl:value-of select="fun:dateDe2Iso(string(metadaten-text[@bezeichnung='modifiedat']))"/></dcterms:modified>
	</xsl:if>
</xsl:template>

<xsl:template match="metadaten-gruppe[@bezeichnung='handler[[keywords]]']">
	<xsl:for-each select="descendant-or-self::metadaten-text">
		<xsl:variable name="l" select="normalize-space(.)"/>
		<xsl:if	test="string-length($l)">
			<dcterms:subject>
				<skos:Concept>
					<rdf:type rdf:resource="{$wkd}Keyword"/>
					<skos:prefLabel xml:lang="de"><xsl:value-of select="$l"/></skos:prefLabel>
				</skos:Concept>
			</dcterms:subject>
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template match="metadaten-gruppe[@bezeichnung='handler[[categories]]']">
	<xsl:for-each select="descendant-or-self::metadaten-gruppe[starts-with(@bezeichnung,'add[[')]">
		<xsl:variable name="c" select="substring-before(substring-after(@bezeichnung,'add[['),']]')"/>
		<xsl:for-each select="descendant-or-self::metadaten-text">
			<xsl:variable name="l" select="normalize-space(.)"/>
			<xsl:if	test="string-length($l)">
				<dcterms:subject>
					<skos:Concept>
						<rdf:type rdf:resource="{$wkd}Category{if ($c) then concat('/',$c) else ''}"/>
						<skos:prefLabel xml:lang="de"><xsl:value-of select="$l"/></skos:prefLabel>
					</skos:Concept>
				</dcterms:subject>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>
</xsl:template>

<xsl:template match="metadaten-gruppe[@bezeichnung='handler[[vwdata]]']">
	<xsl:for-each select="descendant-or-self::metadaten-gruppe[starts-with(@bezeichnung,'item[[')]">
		<xsl:variable name="p" select="normalize-space(substring-before(substring-after(@bezeichnung,'item[['),'###]]'))"/>
		<xsl:for-each select="descendant-or-self::metadaten-text">
			<xsl:variable name="v" select="normalize-space(.)"/>
			<xsl:if	test="string-length($v)">
				<wkd:mdProperty rdf:parseType="Resource">
					<rdfs:label><xsl:value-of select="if ($p) then $p else 'item'"/></rdfs:label>
					<rdf:value><xsl:value-of select="$v"/></rdf:value>
				</wkd:mdProperty>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>
</xsl:template>

<xsl:template match="metadaten-text">
	<wkd:mdProperty rdf:parseType="Resource">
   		<rdfs:label><xsl:value-of select="@bezeichnung"/></rdfs:label>
		<xsl:variable name="t" as="xs:string">
			<xsl:choose>
				<xsl:when test="@typ='nummer'"><xsl:value-of select="concat($xsd,'decimal')"/></xsl:when>
				<xsl:when test="@typ='zeichenkette'"><xsl:value-of select="concat($xsd,'string')"/></xsl:when>
				<xsl:when test="@typ='liste'"><xsl:value-of select="concat($xsd,'string')"/></xsl:when>
				<xsl:when test="@typ='datum'"><xsl:value-of select="concat($xsd,'date')"/></xsl:when>
				<xsl:when test="@typ='zahl'"><xsl:value-of select="concat($xsd,'integer')"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="concat($xsd,'string')"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<rdf:value>
			<xsl:if test="string-length($t) &gt; 0">
				<xsl:attribute namespace="{$rdf}" name="datatype" select="$t"/>
			</xsl:if>
			<xsl:value-of select="if (@typ='datum') then fun:dateDe2Iso(string(.)) else ."/>
		</rdf:value>
   	</wkd:mdProperty>
</xsl:template>

<xsl:template match="metadaten-gruppe[not(@bezeichnung=('createmodifiedat','handler[[keywords]]','handler[[categories]]','handler[[vwdata]]'))]">
	<wkd:mdProperty rdf:parseType="Resource">
		<rdfs:label><xsl:value-of select="@bezeichnung"/></rdfs:label>
		<rdf:value rdf:parseType="Resource">
			<xsl:apply-templates select="*"/>
		</rdf:value>
	</wkd:mdProperty>
</xsl:template>

<xsl:template match="metadaten-gruppe[@bezeichnung='rechtsgebiete-red']">
	<xsl:for-each select="metadaten-text[@bezeichnung='rg-eintrag-hierarchie']">
		<wkd:practiceArea>
			<skos:Concept>
				<rdf:type rdf:resource="{$wkd}PracticeArea"/>
				<xsl:call-template name="printHierarchy">
					<xsl:with-param name="label" select="normalize-space(.)" as="xs:string"/>
					<xsl:with-param name="token" select="'-&gt;'" as="xs:string" tunnel="yes"/>
					<xsl:with-param name="ns" select="$wkd" as="xs:string" tunnel="yes"/>
					<xsl:with-param name="name" select="'PracticeArea'" as="xs:string" tunnel="yes"/>
				</xsl:call-template>
			</skos:Concept>
		</wkd:practiceArea>
	</xsl:for-each>
</xsl:template>

<!-- Practice Area -->
<xsl:template match="rechtsgebiete/rechtsgebiet-eintrag">
	<xsl:variable name="p" select="normalize-space(@produkt)"/>
	<xsl:variable name="t" select="normalize-space(@typ)"/>
	<xsl:variable name="e" select="if ($t='haupt') then 'mainPracticeArea' else 'associatedPracticeArea'"/>
	<xsl:if test="string-length(normalize-space(.)) &gt; 0">
		<xsl:element namespace="{$wkd}" name="{$e}">
			<skos:Concept>
				<xsl:call-template name="printHierarchy">
					<xsl:with-param name="label" select="normalize-space(.)" as="xs:string"/>
					<xsl:with-param name="token" select="'-&gt;'" as="xs:string" tunnel="yes"/>
					<xsl:with-param name="ns" select="$wkd" as="xs:string" tunnel="yes"/>
					<xsl:with-param name="name" select="'PracticeArea'" as="xs:string" tunnel="yes"/>
				</xsl:call-template>
			</skos:Concept>
		</xsl:element>
	</xsl:if>
</xsl:template>

<!-- EXTRACTED TAXONOMY TERMS -->
<xsl:template match="taxonomien">
	<xsl:variable name="p" select="normalize-space(@produkt)"/>
	<xsl:variable name="q" select="normalize-space(@quelle)"/>
	<xsl:for-each select="taxonomie-eintrag[string-length(normalize-space(.)) &gt; 0]">
		<dcterms:subject>
			<wkd:TaxonomyTerm>
				<rdf:type rdf:resource="{$skos}Concept"/>
				<xsl:if test="string-length($p) &gt; 0">
					<wkd:product rdf:parseType="Resource">
						<skos:prefLabel xml:lang="de"><xsl:value-of select="$p"/></skos:prefLabel>
					</wkd:product>
				</xsl:if>
				<xsl:if test="string-length($q) &gt; 0">
					<wkd:source rdf:parseType="Resource">
						<skos:prefLabel xml:lang="de"><xsl:value-of select="$q"/></skos:prefLabel>
					</wkd:source>
				</xsl:if>
				<xsl:call-template name="printHierarchy">
					<xsl:with-param name="label" select="normalize-space(.)" as="xs:string"/>
					<xsl:with-param name="token" select="'-&gt;'" as="xs:string" tunnel="yes"/>
					<xsl:with-param name="ns" select="$wkd" as="xs:string" tunnel="yes"/>
					<xsl:with-param name="name" select="'TaxonomyTerm'" as="xs:string" tunnel="yes"/>
				</xsl:call-template>
			</wkd:TaxonomyTerm>
		</dcterms:subject>
	</xsl:for-each>
</xsl:template>

<!-- abstract -->
<xsl:template match="abstract">
	<bibo:abstract rdf:parseType="Literal">
		<xsl:apply-templates mode="xml-literal"/>
	</bibo:abstract>
</xsl:template>

<!-- contributor -->
<xsl:template match="mitgeteiltvon">
	<xsl:apply-templates select="person | organisation">
		<xsl:with-param name="namespace" select="$wkd" as="xs:string"/>
		<xsl:with-param name="property" select="'reportedBy'" as="xs:string"/>
	</xsl:apply-templates>
</xsl:template>

<!-- Organisation Concept -->
<xsl:template match="organisation">
	<xsl:param name="namespace" select="$dcterms" as="xs:string"/>
	<xsl:param name="property" select="'subject'" as="xs:string"/>
	<xsl:element namespace="{$namespace}" name="{$property}">
		<skos:Concept>
			<rdf:type rdf:resource="{$foaf}Organization"/>
			<foaf:name><xsl:value-of select="string(org-bezeichnung)"/></foaf:name>
			<skos:prefLabel xml:lang="de"><xsl:value-of select="string(org-bezeichnung)"/></skos:prefLabel>
			<xsl:if test="org-kuerzel">
				<foaf:nick><xsl:value-of select="org-kuerzel"/></foaf:nick>
				<skos:altLabel xml:lang="de"><xsl:value-of select="org-kuerzel"/></skos:altLabel>
			</xsl:if>
			<xsl:if test="@ref-id">
				<dcterms:identifier><xsl:value-of select="@ref-id"/></dcterms:identifier>
			</xsl:if>
		</skos:Concept>
	</xsl:element>
</xsl:template>

<!-- Organisation Concept -->
<xsl:template match="person">
	<xsl:param name="namespace" select="$dcterms" as="xs:string"/>
	<xsl:param name="property" select="'creator'" as="xs:string"/>
	<xsl:element namespace="{$namespace}" name="{$property}">
		<skos:Concept>
			<rdf:type rdf:resource="{$foaf}Person"/>
			<xsl:for-each select="name/titel-zu-name">
				<foaf:title><xsl:value-of select="string(.)"/></foaf:title>
			</xsl:for-each>
			<xsl:if test="name/vorname">
				<foaf:firstName><xsl:value-of select="string(name/vorname)"/></foaf:firstName>
			</xsl:if>
			<xsl:if test="name/nachname">
				<foaf:lastName><xsl:value-of select="string(name/nachname)"/></foaf:lastName>
			</xsl:if>
			<skos:prefLabel xml:lang="de"><xsl:value-of select="concat(name/vorname,if (name/nachname and name/vorname) then ' ' else '',name/nachname)"/></skos:prefLabel>
			<xsl:if test="org-kuerzel">
				<foaf:nick><xsl:value-of select="org-kuerzel"/></foaf:nick>
				<skos:altLabel xml:lang="de"><xsl:value-of select="org-kuerzel"/></skos:altLabel>
			</xsl:if>
			<xsl:if test="@ref-id">
				<dcterms:identifier><xsl:value-of select="@ref-id"/></dcterms:identifier>
			</xsl:if>
			<xsl:for-each select="person-rolle">
				<xsl:apply-templates select="verweis-url">
					<xsl:with-param name="referenceType" as="xs:string" select="if (@typ) then @typ else 'http'" tunnel="yes"/>
				</xsl:apply-templates>
			</xsl:for-each>
		</skos:Concept>
	</xsl:element>
</xsl:template>

<!-- Create hierarchy and for all used subjects -->
<xsl:template match="stichworte" mode="build-hierarchy">
	<xsl:for-each select="stichwort">
		<xsl:call-template name="stichwortHierarchy">
			<xsl:with-param name="stw" select="."/>
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>

<xsl:template match="inline-stichwort" mode="build-hierarchy">
	<xsl:call-template name="stichwortHierarchy">
		<xsl:with-param name="stw" select="."/>
	</xsl:call-template>
</xsl:template>

<xsl:template name="stichwortHierarchy">
	<xsl:param	name="stw" as="element()"/>
	<xsl:variable name="e" 
	              select="if ($stw/@typ='amtlich') then 'SubjectOfficial' 
						  else if (@typ='redaktionell') then 'SubjectEditorial' 
						  else 'Subject'" as="xs:string"/>
	<xsl:element namespace="{$wkd}" name="{$e}">
		<xsl:variable name="l1-uri" select="concat($v-base-uri,'stw/',fun:stwSegmentId(string(haupt-stw)))" as="xs:string"/>
		<xsl:attribute name="rdf:about" select="$l1-uri"/>
		<rdf:type rdf:resource="{$skos}Concept"/>
		<xsl:call-template name="buildLabel">
			<xsl:with-param name="e" select="haupt-stw" as="element()"/>
		</xsl:call-template>
		<xsl:for-each select="siehe-stw">
			<skos:related>
				<xsl:variable name="r-uri" select="concat($v-base-uri,'stw/',fun:stwSegmentId(string(.)))" as="xs:string"/>
				<wkd:Subject>
					<xsl:call-template name="buildLabel">
						<xsl:with-param name="e" select="." as="element()"/>
						<xsl:with-param name="lt" select="'prefLabel'" as="xs:string"/>
					</xsl:call-template>
				</wkd:Subject>
			</skos:related>
		</xsl:for-each>
		<xsl:for-each select="unter-stw">
			<skos:narrower>
				<xsl:variable name="l2-uri" select="fun:stwUri($l1-uri,string(.))" as="xs:string"/>
				<xsl:element namespace="{$wkd}" name="{$e}">
					<xsl:attribute name="rdf:about" select="$l2-uri"/>
					<rdf:type rdf:resource="{$skos}Concept"/>
					<xsl:call-template name="buildLabel">
						<xsl:with-param name="e" select="." as="element()"/>
					</xsl:call-template>
					<xsl:for-each select="unter-unter-stw">
						<xsl:variable name="l3-uri" select="fun:stwUri($l2-uri,string(.))" as="xs:string"/>
						<skos:narrower>
							<xsl:element namespace="{$wkd}" name="{$e}">
								<xsl:attribute name="rdf:about" select="$l3-uri"/>
								<rdf:type rdf:resource="{$skos}Concept"/>
								<xsl:call-template name="buildLabel">
									<xsl:with-param name="e" select="." as="element()"/>
								</xsl:call-template>
							</xsl:element>
						</skos:narrower>
					</xsl:for-each>
				</xsl:element>
			</skos:narrower>
		</xsl:for-each>
	</xsl:element>
</xsl:template>

<xsl:function name="fun:stwUri" as="xs:string">
	<xsl:param name="base" as="xs:string"/>
	<xsl:param name="segment" as="xs:string"/>
	<xsl:value-of select="concat($base,'_',fun:stwSegmentId($segment))"/>
</xsl:function>

<xsl:template name="buildLabel">
	<xsl:param name="lt" select="'prefLabel'" as="xs:string"/>
	<xsl:param name="e" as="element()"/>
	<xsl:element namespace="{$skos}" name="{$lt}">
		<xsl:attribute name="xml:lang" select="fun:language($e/@sprache)"/>
		<xsl:if test="$e/(hoch | tief)">
			<xsl:attribute name="rdf:parseType" select="Literal"/>
		</xsl:if>
		<xsl:apply-templates select="$e/(text() | hoch | tief)" mode="xml-literal"/>
	</xsl:element>
</xsl:template>

<!-- EXTRACTED KEYWORDS (stichworte) -->
<xsl:template match="stichworte"/>

<xsl:template match="stichworte" mode="top-level">
	<xsl:apply-templates select="stichwort" mode="top-level"/>
</xsl:template>

<xsl:template match="inline-stichwort | stichwort" mode="top-level">
	<xsl:param name="p-uri" as="xs:string" tunnel="yes"/>
	<xsl:variable name="e" select="if (@typ='amtlich') then 'SubjectOfficial' else if (@typ='redaktionell') then 'SubjectEditorial' else 'Subject'" as="xs:string"/>
	<xsl:variable name="l1-uri" select="concat($v-base-uri,'stw/',fun:stwSegmentId(string(haupt-stw)))" as="xs:string"/>
	<xsl:if test="not(unter-stw)">
		<xsl:call-template name="setSubject">
			<xsl:with-param name="subject" select="$p-uri"/>
			<xsl:with-param name="object" select="$l1-uri"/>
			<xsl:with-param name="priority" select="string(haupt-stw/@prioritaet)" as="xs:string"/>
		</xsl:call-template>
	</xsl:if>
	<xsl:for-each select="unter-stw">
		<xsl:variable name="l2-uri" select="fun:stwUri($l1-uri,string(.))" as="xs:string"/>
		<xsl:if test="not(unter-unter-stw)">
			<xsl:call-template name="setSubject">
				<xsl:with-param name="subject" select="$p-uri"/>
				<xsl:with-param name="object" select="$l2-uri"/>
				<xsl:with-param name="priority" select="string(@prioritaet)" as="xs:string"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:for-each select="unter-unter-stw">
			<xsl:variable name="l3-uri" select="fun:stwUri($l2-uri,string(.))" as="xs:string"/>
			<xsl:call-template name="setSubject">
				<xsl:with-param name="subject" select="$p-uri"/>
				<xsl:with-param name="object" select="$l3-uri"/>
				<xsl:with-param name="priority" select="string(@prioritaet)" as="xs:string"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:for-each>
</xsl:template>

<xsl:template match="inline-stichwort | stichwort"/>

<!-- Set subject and its priority -->
<xsl:template name="setSubject">
	<xsl:param name="subject" as="xs:string"/>
	<xsl:param name="object" as="xs:string"/>
	<xsl:param name="priority" as="xs:string"/>
	<rdf:Description rdf:about="{$subject}">
		<dcterms:subject rdf:resource="{$object}"/>
	</rdf:Description>
	<xsl:if test="string-length($priority) &gt; 0">
		<owl:Axiom>
			<owl:annotatedSource rdf:resource="{$subject}"/>
			<owl:annotatedProperty rdf:resource="{$dcterms}subject"/>
			<owl:annotatedTarget rdf:resource="{$object}"/>
			<wkd:priority><xsl:value-of select="$priority"/></wkd:priority>
		</owl:Axiom>
	</xsl:if>
</xsl:template>

<!-- print the hierarchy of labels that are seperated by a token
- the hierarchy is supposed to be given from high to low
- the token is a parameter
- the labels are supposed to be preferred labels of a SKOS Concept
- the namespace and name of the SKOS COncept specialisation are parameters.
-->
<xsl:template name="printHierarchy">
	<xsl:param name="label" as="xs:string"/>
	<xsl:param name="token" as="xs:string" tunnel="yes"/>
	<xsl:param name="ns" as="xs:string" tunnel="yes"/>
	<xsl:param name="name" as="xs:string" tunnel="yes"/>
	<xsl:variable name="seq" select="fun:lastAndHead($label, $token)" as="xs:string*"/>
	<xsl:if test="exists($seq[1])">
		<skos:prefLabel xml:lang="de"><xsl:value-of select="normalize-space($seq[1])"/></skos:prefLabel>
		<xsl:if test="exists($seq[2])">
			<skos:broader>
				<xsl:element namespace="{$ns}" name="{$name}">
					<xsl:call-template name="printHierarchy">
						<xsl:with-param name="label" select="$seq[2]" as="xs:string"/>
					</xsl:call-template>
				</xsl:element>
			</skos:broader>
		</xsl:if>
	</xsl:if>
</xsl:template>

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