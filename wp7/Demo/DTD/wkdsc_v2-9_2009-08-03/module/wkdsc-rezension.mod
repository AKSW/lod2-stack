<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC REZENSION DTD Modul						-->
<!-- 									-->
<!--	WKDSC DTD Version: 2.9 vom 04.08.2009					-->
<!-- 	 letzte Aenderung dieses Moduls: 15.09.2008					-->
<!-- 	Public Identifier: -//WKD//DTD WKDSC REZENSION MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->

<!-- ******************************************************************	-->
<!-- Rezension eines Dokuments / Werks 		-->
<!-- ******************************************************************	-->


<!-- ================================================================= -->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzugefuegt-->
<!-- 20071114 ms v2.3.1 CR 160: Attribut bezugsquelle hinzuegfuegt-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!ELEMENT rezension	(titel-kopf,
			 zuordnung-produkt?,
			 rezension-quelle,
			 normenkette?, 
			 autor?, 
			 %elemente.metadaten;,
			stichworte?, 
			 werk-steuerung?,
			 verbundene-dokumente*, 
			 teaser?, abstract?, 
			 (zwischen-titel | anmerkung | %absatz.komplett;)+) >

<!ATTLIST rezension
		%attr.start-seite.opt;
		%attr.end-seite.opt;
		%attr.pos-auf-seite.opt;
		%attr.beilage.opt;
		%attr.prio.opt;
		%attr.datum.opt;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		typ 	(aufsatz | es-anm | erwiderung | kurzbeitrag | buch | unbekannt)		#REQUIRED
		%attr.rechteinhaber.opt;
		%attr.bezugsquelle.opt;
		%attr.sprache.opt;
>

<!-- 20070829 ms v2.2 CR 111: titel-zusatz hinzugefuegt-->
<!ELEMENT rezension-quelle	(autor?, titel?, titel-zusatz?, es-beschlusstext?)>