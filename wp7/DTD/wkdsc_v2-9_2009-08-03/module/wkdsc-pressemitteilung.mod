<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC PRESSEMITTEILUNG DTD Modul					-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 29.10.2008					-->
<!-- Public Identifier: -//WKD//DTD WKDSC PRESSEMITTEILUNG MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- ***************************************************************** -->
<!-- Eine universelle Pressemitteilung -->
<!-- ***************************************************************** -->

<!-- 20060926 he v1.1.0 CR 9: pm-quelle als Quelle einer Pressemitteilung jetzt mit flexibleren Inhaltsmodell ersetzt Attribut herkunft -->
<!-- 20070130 v1.2.0 CR 58: Verweis auf Entscheidungen aufgenommen --> 
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20070531 ms v2.0 CR 111: verbundene-dokumente hinzugefuegt-->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!-- 20070829 ms v2.2 CR 111: Attributwert eigenepresse zu Attribut typ hinzugefuegt, um WKD-Pressemitteilungen aufnehmen zu koennen-->
<!-- 20070829 ms v2.2 CR 111: Attribut pm-nr hinzugefuegt, um Nummer der Pressemitteilung erfassen zu koennen-->
<!-- 20071114 ms v2.3.1 CR 160: Attribut bezugsquelle hinzugefuegt-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!-- 20080915 ms v2.6 CR 195: rechtsgebiete, metadaten, taxonomien hinzugefuegt-->
<!-- 20081029 ms v2.7 CR 204: Attribut typ mit #REQUIRED statt mit Default "presse" angelegt-->

<!ELEMENT pressemitteilung	(verbundene-dokumente*, titel?, %elemente.metadaten;, stichworte?, pm-quelle?, normenkette?, 
				pm-kurztext?, pm-volltext, pm-entscheidung*)	>
<!ATTLIST pressemitteilung
		%attr.datum.req;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		typ		(dpa | presse | eigenepresse)		#REQUIRED
		%attr.rechteinhaber.opt;
		%attr.bezugsquelle.opt;
		%attr.sprache.opt;
		pm-nr		CDATA		#IMPLIED

>

<!-- Angabe der Quelle als URL, wenn es eine Internetquelle war, sonst ueber die textuelle Angabe im verweis-text -->
<!-- 20070829 ms v2.2 CR 111: organisation aufgenommen, um Urheber der Pressemitteilung erfassen zu koennen, verweis-text durch verweis-unbekannt ersetzt-->
<!ELEMENT pm-quelle		((verweis-url | verweis-unbekannt)?, organisation*)	>

<!-- 20080915 ms v2.6 CR 194: autor eingefuegt-->
<!ELEMENT pm-kurztext		((%absatz.basis; | anmerkung)+, autor?)	>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST pm-kurztext	%attr.sprache.opt; 
>

<!ELEMENT pm-volltext		(zwischen-titel | %absatz.basis; | anmerkung)+	>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST pm-volltext	%attr.sprache.opt; 
>

<!-- 20070130 v1.2.0 CR 58: Verweis auf Entscheidungen aufgenommen --> 
<!ELEMENT pm-entscheidung    (verweis-es)*     >