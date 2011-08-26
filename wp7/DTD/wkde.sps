<?xml version="1.0" encoding="UTF-8"?>
<structure version="14" html-doctype="HTML4 Transitional" compatibility-view="IE9" relativeto="*SPS" encodinghtml="UTF-8" encodingrtf="ISO-8859-1" encodingpdf="UTF-8" useimportschema="1" embed-images="1" pastemode="xml" enable-authentic-scripts="1" authentic-scripts-in-debug-mode-external="0" generated-file-location="DEFAULT">
	<parameters>
		<parameter name="doc-type" default="name(/wkdsc/*)"/>
		<parameter name="r-base-uri" default="http://resource.wolterskluwer.de/"/>
		<parameter name="r-uri" default="concat($r-base-uri,$doc-type,&apos;/&apos;)"/>
		<parameter name="v-base-uri" default="http://vocab.wolterskluwer.de/"/>
	</parameters>
	<schemasources>
		<namespaces>
			<nspair prefix="dc" uri="http://purl.org/dc/elements/1.1/"/>
			<nspair prefix="dcterms" uri="http://purl.org/dc/terms/"/>
			<nspair prefix="owl" uri="http://www.w3.org/2002/07/owl#"/>
			<nspair prefix="rdf" uri="http://www.w3.org/1999/02/22-rdf-syntax-ns#"/>
			<nspair prefix="rdfs" uri="http://www.w3.org/2000/01/rdf-schema#"/>
			<nspair prefix="skos" uri="http://www.w3.org/2004/02/skos/core#"/>
			<nspair prefix="xl" uri="http://www.w3.org/2008/05/skos-xl#"/>
			<nspair prefix="xsd" uri="http://www.w3.org/2001/XMLSchema#"/>
		</namespaces>
		<schemasources>
			<xsdschemasource name="XML" main="1" schemafile="wkdsc-vs.dtd"/>
		</schemasources>
	</schemasources>
	<modules/>
	<flags>
		<scripts/>
		<globalparts/>
		<designfragments/>
		<pagelayouts/>
		<xpath-functions/>
	</flags>
	<scripts>
		<script language="javascript"/>
	</scripts>
	<script-project>
		<Project version="2" app="AuthenticView"/>
	</script-project>
	<importedxslt/>
	<globalstyles/>
	<mainparts>
		<children>
			<globaltemplate subtype="main" match="/">
				<document-properties/>
				<children>
					<documentsection>
						<properties columncount="1" columngap="0.50in" headerfooterheight="fixed" pagemultiplepages="0" pagenumberingformat="1" pagenumberingstartat="auto" pagestart="next" paperheight="11in" papermarginbottom="0.79in" papermarginfooter="0.30in" papermarginheader="0.30in" papermarginleft="0.60in" papermarginright="0.60in" papermargintop="0.79in" paperwidth="8.50in"/>
					</documentsection>
					<template subtype="source" match="XML">
						<children>
							<userxmlelem openingtagtext="rdf:RDF">
								<children>
									<userxmlelem openingtagtext="rdf:Description rdf:about=&quot;{$r-uri}&quot;">
										<children>
											<userxmlelem openingtagtext="rdf:type rdf:resource=&quot;xx&quot;"/>
										</children>
									</userxmlelem>
								</children>
							</userxmlelem>
						</children>
						<variables/>
					</template>
				</children>
			</globaltemplate>
		</children>
	</mainparts>
	<globalparts/>
	<designfragments/>
	<xmltables/>
	<authentic-custom-toolbar-buttons/>
</structure>
