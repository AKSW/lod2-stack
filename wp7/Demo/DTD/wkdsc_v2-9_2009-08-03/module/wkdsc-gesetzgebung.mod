<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC GESETZGEBUNG DTD Modul						-->
<!-- 									-->
<!--	WKDSC DTD Version: 2.9 vom 04.08.2009					-->
<!-- 	 letzte Aenderung dieses Moduls: 15.09.2008					-->
<!-- 	Public Identifier: -//WKD//DTD WKDSC GESETZGEBUNG MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->



<!-- ***************************************************************** -->
<!-- Gesetzgebungsvorschau-->
<!-- ***************************************************************** -->


<!-- ================================================================= -->

<!-- 20070829 ms v2.2 CR 111: titel-kopf mehrfach zugelassen, weil amtliche und redaktionelle Titel vorkommen koennen, Attribut prioritaet hinzugefuegt-->
<!-- 20071114 ms v2.3.1 CR 160: Attribut bezugsquelle hinzuegfuegt-->
<!ELEMENT gesetzgebung	(titel-kopf+, 
			 zuordnung-produkt?,
			 %elemente.metadaten;,
			stichworte?, 
			 werk-steuerung?,
			 normenkette?, 
			 verfahrensuebersicht?, 
			 verbundene-dokumente*, 
			 teaser?, abstract?, 
			 gesetzgebung-eintrag+) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->   
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!ATTLIST gesetzgebung
		%attr.vsk.opt;
		%attr.vs-herkunft.req;
		%attr.prio.opt;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		%attr.sprache.opt;
		%attr.bezugsquelle.opt;
>

<!-- ================================================================= -->
<!-- Verfahrensuebersicht bei GESTA oder PRELEX-->
<!-- 20070829 ms v2.2 CR 111: verweis-unbekannt alternativ zu verweis-url hinzugefuegt und Wert unbekannt bei Attribut typ-->

<!ELEMENT verfahrensuebersicht	((verweis-url | verweis-unbekannt), legislaturperiode?, verfahrensnummer?) >

<!ATTLIST verfahrensuebersicht
		typ	(gesta| prelex | unbekannt)	#REQUIRED>

<!ELEMENT legislaturperiode	(#PCDATA)>

<!ELEMENT verfahrensnummer	(#PCDATA)>

<!-- ================================================================= -->
<!-- Einzelner Gesetzgebungseintrag -->

<!-- 20070829 ms v2.2 CR 111: Neues Attribut gg-nr, um die offizielle Nummer, z.B. einer Drucksache zu erfassen, neues Element gg-quelle um den Urheber zu erfassen-->
<!ELEMENT gesetzgebung-eintrag	(titel-kopf?,
				autor?,
			 	verbundene-dokumente*, 
				abstract?, 
				(zwischen-titel | anmerkung | %absatz.komplett;)+,
				gg-quelle? )
> 

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->   
<!ATTLIST gesetzgebung-eintrag
		typ		CDATA	#REQUIRED
		%attr.datum.req;
		%attr.prio.opt;
		%attr.vtext-id.opt;
		gg-nr		CDATA		#IMPLIED
		%attr.sprache.opt;
>

<!ELEMENT gg-quelle		((verweis-url | verweis-unbekannt)?, organisation*)	>

