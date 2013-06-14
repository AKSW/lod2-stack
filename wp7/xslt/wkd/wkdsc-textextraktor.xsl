<?xml version="1.0" encoding="UTF-8"?>
<!-- Skript zum Entfernen XML-Markups in WKDSC-Dokumenten, Marion Spengler-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8"/>

<!-- Root und Dokumenttypen ausgeben -->
<xsl:template match="aufsatz | aufsatz-es | entscheidung | vorschrift | esa-eintrag | kommentierung | kommentierung-rn | beitrag | beitrag-rn | rezension | lexikon | pressemitteilung | gesetzgebung | verwaltungsanweisung" mode="extract-text in-extract-text">
	<xsl:copy><xsl:apply-templates mode="in-extract-text"/></xsl:copy>
</xsl:template>

<xsl:template match="metadaten | es-metadaten | esr-metadaten | vs-metadaten | rechtsgebiete | taxonomien | stichworte | verbundene-dokumente | zuordnung-produkt | werk-steuerung | toc-titel | titel-trefferliste | kurztitel | auspraegung | text-auspraegung | container-auspraegung | korrektur | alt-text | red-zusatz | anmerkung[@typ='redaktionell'] | fn[@typ='redaktionell']" mode="extract-text in-extract-text">
	<!-- Metadaten, Produktdaten und redaktionelle Zusaetze nicht ausgeben-->
</xsl:template>

<xsl:template match="vz-abbildung-auto | vz-abk-auto | vz-abk-manuell | vz-autor-auto | vz-autor-manuell | vz-inhalt-auto | vz-literatur-manuell | vz-stw-auto | vz-stw-manuell | vz-tabelle-auto | verzeichnis"  mode="extract-text in-extract-text">
	<!-- Verzeichnisse nicht ausgeben-->
</xsl:template>

<!-- Strukturelemente mit Tagging ausgeben -->
<xsl:template match="abstract | aufsatz-ebene | beitrag-ebene | beitrag-rn-ebene | kommentierung-ebene | kommentierung-rn-ebene | lexikon-ebene | lexikon-eintrag | es-besprechung | es-inhalt | es-inhalt-eu | es-wiedergabe | leitsaetze | normenkette | orientierungssaetze | pm-kurztext | pm-volltext | va-ebene | vs-ebene | vs-vorspann | artikel | paragraph | vs-objekt | anlage-ebene | anlage | vs-anlage | vs-anlage-ebene" mode="extract-text">
	<xsl:copy><xsl:apply-templates mode="in-extract-text"/></xsl:copy>
</xsl:template>
<xsl:template match="abstract | aufsatz-ebene | beitrag-ebene | beitrag-rn-ebene | kommentierung-ebene | kommentierung-rn-ebene | lexikon-ebene | lexikon-eintrag | es-besprechung | es-inhalt | es-inhalt-eu | es-wiedergabe | leitsaetze | normenkette | orientierungssaetze | pm-kurztext | pm-volltext | va-ebene | vs-vorspann | anlage-ebene | anlage" mode="in-extract-text">
	<xsl:copy><xsl:apply-templates mode="in-extract-text"/></xsl:copy>
</xsl:template>
<xsl:template match="paragraph | artikel | vs-ebene | vs-anlage | vs-anlage-ebene | vs-objekt" mode="in-extract-text"/>

<!-- Leerzeichen ausgeben, damit Inhalte korrekt getrennt erhalten bleiben-->
<xsl:template match="absatz | absatz-rechts | ueberschrift | zeilenumbruch | titel | lexikon-begriff | vs-kurztitel | vs-abk | beschriftung | quelle | es-beschlusstext | es-rechtskraft | titel-zusatz | titel-bezug | bezugstext | kennung | abs-nr | verweis-vs[parent::normenkette]" mode="extract-text in-extract-text">
	<xsl:apply-templates mode="in-extract-text"/><xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="fn | marginalie" mode="extract-text in-extract-text">
			<xsl:text> </xsl:text><xsl:apply-templates mode="in-extract-text"/>
</xsl:template>

<xsl:template match="*[parent::name | parent::person-rolle | parent::organisation]" mode="extract-text in-extract-text">
			<xsl:apply-templates mode="in-extract-text"/><xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="*" mode="in-extract-text">
	<xsl:apply-templates mode="in-extract-text"/>
</xsl:template>

</xsl:stylesheet>
<!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="vs bd_baugb" userelativepaths="yes" externalpreview="no" url="..\..\Data\Legislation\Schlichter_Berliner_K_BauGB-BauGB_2004.xml" htmlbaseurl=""
		          outputurl="..\..\Data\Legislation\Schlichter_Berliner_K_BauGB-BauGB_2004-text.xml" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath=""
		          additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bSchemaAware" value="true"/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="xsltVersion" value="2.0"/>
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