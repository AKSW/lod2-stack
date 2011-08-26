<?xml version="1.0" encoding="iso-8859-1"?>
<!-- $Id: wkdsc-brak.mod,v 1.1 2007/03/14 09:04:27 kowando Exp $ -->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC BRAK Modul Zweig						-->
<!-- 									-->
<!--           WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 06.09.2006					-->
<!-- Public Identifier: -//WKD//DTD WKDSC BRAK MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->

<!-- ******************************************************************	-->
<!-- In BRAK werden alle Inhaltsmodelle mit einem wesentlich einfacheren 
     Inhaltsmodell definiert -->
<!-- ******************************************************************	-->



<!-- ================================================================= -->
<!-- Arten von Verweisen fuer die Wiederverwendung  -->
<!ENTITY % verweis.komplett     "verweis-vs | verweis-es | verweis-url | verweis-vtext-id" >

<!-- ================================================================= -->
<!-- gemeinsame Inhaltsmodelle fuer Titel -->
<!ENTITY % titel.einfach     "#PCDATA | hoch | tief | titel-umbruch" >

<!ENTITY % titel.basis       " %titel.einfach;" >

<!ENTITY % titel.komplett    " %titel.basis; | %verweis.komplett; " >


<!-- ================================================================= -->
<!-- gemeinsame Inhaltsmodelle fuer Kennungen -->
<!ENTITY % kennung.einfach	"#PCDATA | hoch | tief | hervor" >

<!ENTITY % kennung.basis	"%kennung.einfach;" >


<!-- ================================================================= -->
<!-- Inlinedefinition ohne Hervorhebung -->
<!ENTITY % inline.einfach     "#PCDATA | hoch | tief " >

<!-- einfache Inlinedefinition mit Hervorhebung und Anmerkungen -->
<!ENTITY % inline.einfach.hervor-fn     "%inline.einfach; | hervor | fn " >

<!-- Inlinedefinition mit Hervorhebung und Zitat -->
<!ENTITY % inline.basis       "%inline.einfach; | hervor " >

<!-- Komplette Abbildung aller Inlineelemente -->
<!ENTITY % inline.komplett    "%inline.basis; | %verweis.komplett; " >


<!-- ================================================================= -->
<!-- Absatzelemente -->
<!ENTITY % absatz.einfach     "absatz" >

<!ENTITY % absatz.basis       "%absatz.einfach; | liste-auto " >

<!ENTITY % absatz.komplett    "%absatz.basis;" >


<!-- ================================================================= -->
<!-- Auflistung von Elementen fuer bestimmte wiederverwendbare Inhaltsmodelle  -->
<!ENTITY % elemente.zitat.kein-block		"zitat-vs | zitat-es" >

<!ENTITY % elemente.zitat.komplett		"zitat-vs | zitat-es | zitat-block" >

<!ENTITY % elemente.vorschrift			"vorschrift | vs-ebene | paragraph | artikel | vs-anlage | vs-absatz | vs-objekt" >

<!ENTITY % elemente.entscheidung		"entscheidung | pressemitteilung" >

<!ENTITY % elemente.titel.einfach		"kennung?, (titel | ohne-titel), titel-zusatz?, toc-titel?" >

<!ENTITY % elemente.li				"(%absatz.komplett;)+" >


<!-- ================================================================= -->
<!-- Definiton der Verzeichnismodelle   -->
<!ENTITY % elemente.verzeichnis.alle		"vz-inhalt-auto " >

<!ENTITY % elemente.verzeichnis.inhalte-auto	"vz-inhalt-auto " >


<!-- ================================================================= -->
<!-- Inhaltsmodell von Aufsaetzen mit Strukturebenen -->
<!ENTITY % elemente.aufsatz			"(aufsatz-ebene | zwischen-titel | anmerkung | %elemente.zitat.kein-block; | %absatz.komplett;)*" >


<!-- ================================================================= -->
<!-- Inhaltsmodell zur Wiedergabe von Entscheidungen in redaktionellen Inhalten -->
<!ENTITY % elemente.es-redaktionelle-wiedergabe		"leitsaetze | 
							 normenkette 
							 tenor | tatbestand | gruende | kostenentscheidung " 
>


<!-- ***************************************************************** -->
<!-- BRAK spezifische Entitaeten -->
<!-- ***************************************************************** -->

<!ENTITY % brak.absatz.einfach     		"absatz | liste-auto" >

<!ENTITY % brak.elemente.gemeinsam		"rechtsgebiet-eintrag, %elemente.metadaten;, brak-titel, brak-normenkette?, brak-kurztext?, brak-praxistipp?, brak-external-links?, brak-internal-links?" >

	<!-- Angabe, ob ein Dokument in den Annex auftauchen soll  -->
<!ENTITY % brak.brak-annex			"brak-annex (ja | nein)" >
<!ENTITY % brak.brak-annex.req			"%brak.brak-annex;  #REQUIRED" >
<!ENTITY % brak.brak-annex.opt			"%brak.brak-annex;  #IMPLIED" >
<!ENTITY % brak.brak-annex.default		"%brak.brak-annex;  'nein'" >


<!-- ***************************************************************** -->
<!-- BRAK spezifische Elemente -->
<!-- ***************************************************************** -->

<!ELEMENT brak-titel		(%titel.einfach;)*  >

<!ELEMENT brak-normenkette	(verweis-vs)+	>

<!ELEMENT brak-leitsatz		(leitsatz)+	>

<!ELEMENT brak-kurztext		(%absatz.basis;)+	>

<!ELEMENT brak-praxistipp	(%absatz.basis;)+	>

<!ELEMENT brak-external-links	(verweis-url)+ >

<!ELEMENT brak-internal-links	(verweis-es | verweis-vtext-id)+ >


<!ELEMENT brak-basis-entscheidung		(%brak.basis.entscheidung;) >

<!ELEMENT brak-basis-fachpresse-beitrag		(%brak.basis.fachpresse-beitrag;) >

<!ELEMENT brak-basis-gesetzgebung-beitrag	(%brak.basis.gesetzgebung-beitrag;) >

<!ELEMENT brak-basis-news-beitrag		(%brak.basis.news-beitrag;) >


<!-- XXX in den Stammzweig von WKDSC News reinnehmen -->
<!ELEMENT news-quelle				(titel, (verweis-url | verweis-vtext-id)?) >
<!ATTLIST news-quelle
		%attr.datum.req;
>


<!-- ***************************************************************** -->
<!-- eine von BRAK erweiterte Entscheidung -->
<!-- ***************************************************************** -->

<!ELEMENT brak-entscheidung	(%brak.elemente.gemeinsam;, brak-leitsatz?,  brak-basis-entscheidung) >

<!ATTLIST brak-entscheidung
		%brak.brak-annex.default;
		%attr.vtext-id.req;		
>


<!-- ***************************************************************** -->
<!-- BRAK Fachpresseauswertung -->
<!-- ***************************************************************** -->
<!-- TODO: he vermerken der Fundstellen zu den originalen Fachpresseaufsaetzen -->
<!ELEMENT brak-fachpresse-beitrag		(%brak.elemente.gemeinsam;, autor?, brak-basis-fachpresse-beitrag) >

<!ATTLIST brak-fachpresse-beitrag
		%brak.brak-annex.default;
		%attr.vtext-id.req;
>


<!-- ***************************************************************** -->
<!-- BRAK Gesetzgebungsvorschau -->
<!-- ***************************************************************** -->

<!ELEMENT brak-gesetzgebung-beitrag		(%brak.elemente.gemeinsam;, brak-basis-gesetzgebung-beitrag) >

<!ATTLIST brak-gesetzgebung-beitrag
		%brak.brak-annex.default;
		%attr.vtext-id.req;
>


<!-- ***************************************************************** -->
<!-- BRAK News -->
<!-- ***************************************************************** -->

<!ELEMENT brak-news-beitrag		(%brak.elemente.gemeinsam;, news-quelle?, brak-basis-news-beitrag) >

<!ATTLIST brak-news-beitrag
		%brak.brak-annex.default;
		%attr.vtext-id.req;
>


<!-- ***************************************************************** -->
<!-- Kopf eines Newsletters als IO  -->
<!-- ***************************************************************** -->

<!ELEMENT brak-newsletter-kopf		(rechtsgebiet-eintrag) >


<!-- Datum ist in der Pflege das Erstellungsdatums des Kopf IOs. In der 
     Ausgabe wird es das Datum sein, an dem der Newsletter zum Versand 
     freigegeben wurde -->
<!ATTLIST brak-newsletter-kopf
		%attr.nr.req;
		%attr.datum.req;		
>

