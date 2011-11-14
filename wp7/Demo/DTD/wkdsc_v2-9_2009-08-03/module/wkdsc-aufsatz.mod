<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC AUFSATZ DTD Modul						-->
<!-- 									-->
<!--	WKDSC DTD Version: 2.9 vom 04.08.2009					-->
<!-- 	 letzte Aenderung dieses Moduls: 29.10.2008					-->
<!-- 	Public Identifier: -//WKD//DTD WKDSC AUFSATZ MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->

<!-- ******************************************************************	-->
<!-- globale Aenderungen 						-->
<!-- 									-->
<!-- 20060926 he v1.1.0 CR 2: Komma nach den Referenzen von 
                              elemente.werk-steuerung eingefuegt 	-->
<!-- 20060926 he v1.1.0 CR 26: zuordnung-zs fuer die Zuordnung von 
                               Aufsaetzen zu Zeitschriften eingefuegt 
                               und in den Elementen als erforderlich 
                               ergaenzt -->
<!-- 20070222 sk v1.2.0: Attribut rechtskraft bei esa-eintrag eingefuehrt  -->
<!-- ******************************************************************	-->


<!-- ******************************************************************	-->
<!-- ein Zeitschriftenaufsatz mit normalen Inhaltsmodell 		-->
<!-- ******************************************************************	-->


<!-- ================================================================= -->
<!-- Unbedingt anzugeben ist die Seite auf welcher der Aufsatz beginnt, wenn es 
     mehrere Aufsaetze auf einer Seite geben sollte, ist diese Position 
     zusaetzlich zu definieren. -->
<!-- 20060627 HE v1.0.0 aufsatz-teil fuer Wiedergabe von gesplitteten Beitraegen, Umsetzung Pkt. 21 -->
<!-- 20070126 he v1.2.0 CR 67: teaser hinzugefuegt -->
<!-- 20070510 ms v2.0 CR 84: aufsatz-teil entfernt, Attribut typ und Element zuordnung-fortsetzung hinzugefuegt-->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20070531 ms v2.0 CR 111: Attribut beilage, datum, prioritaet, typ hinzugefuegt, start-seite optional gemacht, verbundene-dokumente hinzugefuegt -->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!-- 20080915 ms v2.6 CR 208: vz-inhalt-auto hinzugefuegt-->
<!-- 20081029 ms v2.7 CR 204: Attribut umfang wird auf #IMPLIED gesetzt, Default Wert "komplett" wird entfernt-->
<!ELEMENT aufsatz		(titel-kopf,
				 zuordnung-produkt?,
				 zuordnung-fortsetzung?, 
				 autor, 
				 %elemente.metadaten;,
				 stichworte?, 
				 werk-steuerung?,
				 verbundene-dokumente*, 
				 teaser?, abstract?, vz-inhalt-auto?, (%elemente.aufsatz;)+ ) >

<!ATTLIST aufsatz
		%attr.start-seite.opt;
		%attr.end-seite.opt;
		%attr.pos-auf-seite.opt;
		%attr.beilage.opt;
		%attr.prio.opt;
		%attr.datum.opt;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		umfang	(komplett | fortsetzung) 	#IMPLIED
		typ 	CDATA		#IMPLIED
		%attr.rechteinhaber.opt;
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- Rekursion zur Angabe von strukturierten Ebenen in Aufsaetzen -->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!ELEMENT aufsatz-ebene		(titel-kopf, 
				 stichworte?, 
				 werk-steuerung?,
				 %elemente.aufsatz;) >

<!-- wert und bez fuer die Verlinkung auf die Ebenen -->
<!-- 20070112 he v1.2.0 CR 35: bez und wert sorgen fuer die Referenzierung -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST aufsatz-ebene
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.sprache.opt;
>

<!-- ******************************************************************	-->
<!-- Aufsatz der eine Entscheidung behandelt				-->
<!-- ******************************************************************	-->

<!-- ================================================================= -->
<!-- Die Entscheidung wird in ihren Teilen und Anmerkungen abgebildet -->
<!-- 20060627 HE v1.0.0 
	* mehrere Entscheidungen in einem Aufsatz moeglich, Pkt. 4
	* es-besprechung ist optional und mehrfach zugelassen, Umsetzung Pkt. 14
-->     
<!-- 20070126 he v1.2.0 CR 67: teaser hinzugefuegt -->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20070531 ms v2.0 CR 111: Attribut beilage, datum, prioritaet, typ hinzugefuegt, start-seite optional gemacht, abstract mehrfach optional hinzugefuegt, verbundene-dokumente  hinzugefuegt-->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!-- 20070731 ms v2.1 CR 138: autor optional gesetzt-->
<!-- 20071114 ms v2.3.1 CR 160: Attribut bezugsquelle hinzuegfuegt-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!-- 20080915 ms v2.6 CR 193: Attribut end-seite hinzugefuegt-->
<!-- 20080915 ms v2.6 CR 208: vz-inhalt-auto hinzugefuegt-->
<!ELEMENT aufsatz-es		(titel-kopf,
				 zuordnung-produkt?,
				 autor?, 
				 %elemente.metadaten;,
				 stichworte?, 
				 werk-steuerung?,
	 			 verbundene-dokumente*, 
				 teaser?, abstract*, vz-inhalt-auto?, 
				 (es-wiedergabe, es-besprechung*)+ ) >

<!ATTLIST aufsatz-es
		%attr.start-seite.opt;
		%attr.end-seite.opt;
		%attr.pos-auf-seite.opt;
		%attr.beilage.opt;
		%attr.prio.opt;
		%attr.datum.opt;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		typ 	CDATA		#IMPLIED
		%attr.rechteinhaber.opt;
		%attr.bezugsquelle.opt;
		%attr.sprache.opt;
>

<!-- Wiedergabe der Entscheidung in dem fuer die Zeitschrift benoetigten Umfang -->
<!-- 20060627 HE v1.0.0 
     * Reihenfolge der Elemente variabel gestaltet
     * mitgeteiltvon aufgenommen, die Ausgabeposition wird dann bei der Erzeugung festgelegt, Umsetzung ZS Pkt. 5
     * es-anmerkung fuer kleinere Anmerkungen die sich zwischen den Elementen der Entscheidung befinden, Umsetzung ZS Pkt. 6
     * fundstellen hinzugefuegt, Umsetzung ZS Pkt. 7
     * tenor zugelassen, Umsetzung ZS Pkt. 19
     * Attribut nr zur Nummerierung zugelassen, Umsetzung Pkt. 24
-->
<!ELEMENT es-wiedergabe		(mitgeteiltvon?, (%elemente.es-redaktionelle-wiedergabe;)+ )>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!-- 20070928 ms v2.3 CR 158: Attribute gericht, datum, az, az-zusatz entfernt, denn diese Informationen werden jetzt innerhalb von esr-metadaten definiert-->
<!-- 20080915 ms v2.6 CR 203: Attribut senat aus entscheidung uebernommen -->
<!ATTLIST es-wiedergabe
		%attr.es-typ.opt;
		%attr.senat.opt;
		%attr.nr.opt;
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- Die Besprechung der Entscheidung mit einfachem Titelmodell und der 
     Moeglicheit direkt oder mit Strukturierung Inhalte wiederzugeben -->
<!ELEMENT es-besprechung		(%elemente.titel.einfach;, %elemente.aufsatz;, autor*) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST es-besprechung	%attr.sprache.opt; 
>

<!-- ***************************************************************** -->
<!-- Eintrag einer Entscheidungssammlung -->
<!-- ***************************************************************** -->

<!-- ================================================================= -->
<!-- Eintrag einer Entscheidungssammlung mit freiem Inhaltsmodell der 
     Entscheidung aber der direkten Wiederverwendung der Inhalte -->
<!-- 20060926 he v1.1.0 CR 26: mitgeteiltvon aufgenommen -->
<!-- 20070531 ms v2.0 CR 111: verbundene-dokumente hinzugefuegt-->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!ELEMENT esa-eintrag		(zuordnung-produkt?, verbundene-dokumente*, 
				mitgeteiltvon?, zitat-vs?, (%elemente.es-redaktionelle-wiedergabe; | esa-haupteintrag)+, es-besprechung*) >


<!-- Attribut der beinhalteten Entscheidung fuer den Kurzzugriff -->
<!-- 20070222 v1.2.0 optionales Attribut rechtskraft hinzugefuegt -->
<!-- 20070928 ms v2.3 CR 158: Attribute gericht, datum, az, az-zusatz, rechtskraft entfernt, denn diese Informationen werden jetzt innerhalb von esr-metadaten definiert-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!-- 20080915 ms v2.6 CR 203: Attribut senat aus entscheidung uebernommen -->
<!ATTLIST esa-eintrag
		%attr.es-typ.opt;
		%attr.vtext-id.opt;
		%attr.senat.opt;
		%attr.id-typ.opt;
		%attr.rechteinhaber.opt;
		%attr.sprache.opt;
>

<!-- Verweis auf den Haupteintrag, wenn eine Nebenfundstelle mit verkuerztem Inhalt abgebildet wird -->

<!ELEMENT esa-haupteintrag	(%absatz.basis;)* >

<!-- Attribute des Verweises von Entscheidungssammlung fuer die Referenz auf den Eintrag -->
<!ATTLIST esa-haupteintrag
		%attribute.verweis-esa;
>


