<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC VERZEICHNIS DTD Modul					-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 15.09.2008					-->
<!-- Public Identifier: -//WKD//DTD WKDSC VERZEICHNIS MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->



<!-- ***************************************************************** -->
<!-- ***************************************************************** -->
<!-- Verzeichnisse -->
<!-- ***************************************************************** -->
<!-- ***************************************************************** -->

<!-- ================================================================= -->
<!-- Verzeichnis fuer Abkuerzungen  -->

<!ELEMENT vz-abk-manuell 	(kennung?, titel?, werk-steuerung?, vz-beschreibung?, (abk-eintrag | vz-abk-ebene)+)>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vz-abk-manuell  	%attr.zielgruppe.opt;
			%attr.sprache.opt; 
>

<!ELEMENT vz-abk-ebene 	(titel-kopf, (abk-eintrag | vz-abk-ebene)+)>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vz-abk-ebene		%attr.sprache.opt; 
>

<!ELEMENT abk-eintrag	(abk, abk-def)>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST abk-eintrag		%attr.sprache.opt; 
>

<!ELEMENT abk		(%inline.einfach.hervor-fn;)* >
<!ELEMENT abk-def  	(%inline.einfach.hervor-fn; | %verweis.komplett; )* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST abk-def		%attr.sprache.opt; 
>

<!ELEMENT vz-abk-auto	(werk-steuerung?) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vz-abk-auto
		zielgruppe		CDATA		#IMPLIED
		%attr.sprache.opt; 
>

<!-- ================================================================= -->
<!-- Stichwortverzeichnis -->

<!ELEMENT vz-stw-manuell (kennung?, titel?, werk-steuerung?, vz-beschreibung?, stw-eintrag+)>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vz-stw-manuell 	%attr.zielgruppe.opt;
			%attr.sprache.opt; 
>
<!ELEMENT stw-eintrag	(stw, (unter-stw-eintrag | stw-ziel | stw-querverweis)+) >
<!ELEMENT stw		(%inline.einfach.hervor-fn;)* >
<!ELEMENT unter-stw-eintrag	(stw, (unter-unter-stw-eintrag | stw-ziel | stw-querverweis)+)>
<!ELEMENT unter-unter-stw-eintrag	(stw, (stw-ziel | stw-querverweis)+)>
<!ELEMENT stw-ziel	(%inline.einfach.hervor-fn; | %verweis.komplett; )* >
<!ELEMENT stw-querverweis	(%inline.einfach.hervor-fn; | %verweis.komplett; )* >

<!ELEMENT vz-stw-auto	(werk-steuerung?) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vz-stw-auto	zielgruppe		CDATA		#IMPLIED
			%attr.sprache.opt; 
>

<!-- ================================================================= -->
<!-- Literaturverzeichnisse -->

<!ELEMENT vz-literatur-manuell	(kennung?, titel?, werk-steuerung?, vz-beschreibung?, (literatur-eintrag | vz-lit-ebene)+ )	>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vz-literatur-manuell 	%attr.zielgruppe.opt;
				%attr.sprache.opt; 
>
<!ELEMENT vz-lit-ebene		(titel-kopf, (literatur-eintrag | vz-lit-ebene)+)>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vz-lit-ebene		%attr.sprache.opt; 
>

<!ELEMENT literatur-eintrag		(lit?, lit-def)>
<!-- lit ist optional, damit auch nicht tabellarische Literaturverzeichnisse erfasst werden koennen-->

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST literatur-eintrag	%attr.sprache.opt; 
>

<!ELEMENT lit		(%inline.einfach.hervor-fn;)* >
<!ELEMENT lit-def  	(%inline.einfach.hervor-fn; | %verweis.komplett; )* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST lit-def		%attr.sprache.opt; 
>

<!-- ================================================================= -->
<!-- Platzhalter fuer ein automatisch zu erstellendes Inhaltsverzeichnis -->

<!-- der Typ gibt die moeglichen Darstellungsoptionen an, die Tiefe die 
     Anzahl der zu beruecksichtigenden Ebenen, ohne eine Angabe oder den 
     Wert 0 werden alle Ebenen beruecksichtigt -->

<!-- 20070522 ms v2.0: Inhalt der auto-Verzeichnisse war bisher %elemente.verzeichnis;, was in jedem DTD-Zweig definiert wird. -->

<!-- XXX Einbauen an die entsprechenden Stellen -->
<!ELEMENT vz-inhalt-auto	(werk-steuerung?) >

<!-- 20070829 ms v2.2: Neues Attribut sortierung zu vz-inhalt-auto mit den Werten werk (default) und alpha. -->
<!ATTLIST vz-inhalt-auto
		%attr.vz-tiefe.opt;
		zielgruppe		CDATA		#IMPLIED
		sortierung		(werk | alpha)	"werk"
>

<!ELEMENT vz-tabelle-auto	(werk-steuerung?) >
<!ATTLIST vz-tabelle-auto
		zielgruppe		CDATA		#IMPLIED
>

<!ELEMENT vz-abbildung-auto	(werk-steuerung?) >
<!ATTLIST vz-abbildung-auto
		zielgruppe		CDATA		#IMPLIED
>


<!-- ================================================================= -->
<!-- Verzeichnis fuer Autoren -->
<!ELEMENT vz-autor-auto		(werk-steuerung?) >
<!ATTLIST vz-autor-auto
		zielgruppe		CDATA		#IMPLIED
>

<!ELEMENT vz-autor-manuell	(kennung?, titel?, werk-steuerung?, vz-beschreibung?, autor-eintrag+ ) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vz-autor-manuell 	
		%attr.zielgruppe.opt;
		%attr.sprache.opt; 
>
<!ELEMENT autor-eintrag		((autor, autor-ziel?) | (autor-ziel, autor))>
<!ELEMENT autor-ziel	(%inline.einfach.hervor-fn; | %verweis.komplett; )* >


<!-- ================================================================= -->
<!-- Abbildung der Inhaltsmodelle automatisch erstellter Verzeichnisse -->


<!ENTITY % elemente.verzeichnis.einfach	"kennung?, titel?, (vz-eintrag | vz-ebene)+" >

<!-- ================================================================= -->
<!-- Beschreibender Text zu Anfang eines Verzeichnisses -->


<!ELEMENT vz-beschreibung	(%absatz.basis;)+>

<!-- ================================================================= -->

<!-- Definition eines Inhaltsmodells zu einem allgemeinen Verzeichnis, 
     mit rekursiver Inhaltsstruktur, welches primaer automatisiert fuer
     die Produktion erstellt wird -->

<!ELEMENT verzeichnis	( %elemente.verzeichnis.einfach; ) >

<!ATTLIST verzeichnis
		%attr.vz-typ.req;
		%attr.vz-tiefe.opt;
>


<!-- Rekursion fuer weitere Ebenen im Verzeichnis oder Eintrage oder auch Anmerkungen -->
<!ELEMENT vz-ebene	( (vz-eintrag | vz-ebene | anmerkung)+ ) >


<!-- Orientierung am klassischen Inhaltsmodell mit der Nummern vorneweg, dem Text und der Angabe des Zieles -->
<!ELEMENT vz-eintrag	(kennung?, vz-eintrag-text, vz-eintrag-ziel?) >

<!-- Vergabe einer Nummer fuer den Verzeichniseintrag, um zurueckverweisen zu koennen, wenn es gewuenscht ist -->
<!ATTLIST vz-eintrag
		%attr.ref-id.opt;
>


<!-- Aufnahme des Textes und der Verweismoeglichkeiten -->
<!ELEMENT vz-eintrag-ziel ( %inline.einfach; | %verweis.komplett; )* >


<!ELEMENT vz-eintrag-text ( %inline.einfach; | %verweis.komplett; )* >


