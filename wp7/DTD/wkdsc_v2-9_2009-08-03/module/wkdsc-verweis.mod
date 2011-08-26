<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC VERWEIS DTD Modul						-->
<!-- 									-->
<!--           WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 28.04.2009					-->
<!-- Public Identifier: -//WKD//DTD WKDSC VERWEIS MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->



<!-- ***************************************************************** -->
<!-- Verweise auf die verschiedenen Dokumenttypen -->
<!-- ***************************************************************** -->

<!-- ================================================================= -->
<!-- Standarddefinition der Attribute zu einer Vorschrift kann in 
     Modulen ueberschrieben werden -->
<!-- 20060926 he v1.1.0 CR 5: Attribut li-pkt mit aufgenommen zur Angabe des Linkankers in Listen  -->
<!-- 20070402 ms v1.2.1 CR 76: Attribute vs-obj-nr und vs-obj-typ zum Verweis auf vs-objekt hinzugefuegt-->
<!-- 20070507 ms v1.2.2 CR 101: Attribut ver fuer Conware Versionsnummer hinzugefuegt-->
<!-- 20081029 ms v2.7 CR 211: Neues Attribut anlage-ebene-->

<!ENTITY % attribute.verweis-vs  "
		%attr.vsk.req;
		%attr.art.opt;
		%attr.art-bis.opt;
		%attr.par.opt;
		%attr.par-bis.opt;
		%attr.abs.opt;
		%attr.abs-bis.opt;
		%attr.satz.opt;
		%attr.vs-obj-nr.opt;
		%attr.vs-obj-typ.opt;
		%attr.li-pkt.opt;
		%attr.abb-nr.opt;
		%attr.tab-nr.opt;
		%attr.prot-nr.opt;
		%attr.anlage-nr.opt;
		%attr.anlage-typ.opt;
		%attr.vs-ebene.opt;
		%attr.anlage-ebene.opt;
		%attr.vs-stand.opt;
		%attr.vs-inkraft.opt;
		%attr.vs-ver.opt; 	"
>


<!-- ================================================================= -->
<!-- Verweis auf eine Vorschrift -->
<!-- 20070112 he v1.2.0 CR 39: verweis-text aufgenommen, fuer Differenzierung des 
     zu verlinkenden Textes, wenn der Link mehr Text schachtelt als der Link 
     darstellen soll -->
<!-- 20070511 ms v2.0 CR 94: verweis-text wieder entfernt-->
<!-- 20070531 ms v1.2.4 CR 117: hervor und abbildung aufgenommen-->
<!-- 20070727 ms v2.1 CR 123: Attribute text-bitv und titel-bitv entfernt-->
<!-- 20070919 ms v2.3 CR 153: Element variante hinzugefuegt-->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!-- 20080425 ms v2.5 CR 190: Element korrektur hinzugefuegt-->
<!ELEMENT verweis-vs      (%inline.einfach; | hervor | abbildung | variante | sprache | korrektur)* >

<!ATTLIST verweis-vs
		%attribute.verweis-vs;
>


<!-- ================================================================= -->
<!-- Verweis auf eine Entscheidung, Gericht, Datum und Aktenzeichen werden
     auf alle Faelle benoetigt -->
<!-- 20070112 he v1.2.0 CR 39: verweis-text aufgenommen -->     
<!-- 20070511 ms v2.0 CR 94: verweis-text wieder entfernt-->
<!-- 20070531 ms v1.2.4 CR 117: hervor und abbildung aufgenommen-->
<!-- 20070727 ms v2.1 CR 123: Attribute text-bitv und titel-bitv entfernt-->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!-- 20080425 ms v2.5 CR 190: Element korrektur hinzugefuegt-->
<!-- 20090417 ms v2.8 CR 253: Neues Attribut urteilsname -->
<!ELEMENT verweis-es      (%inline.einfach; | hervor | abbildung | sprache | korrektur)* >

<!-- Der Aktenzeichenzusatz, der Typ und die Randziffer der Volltextentscheidung 
     sind optional um eine noch hoehere Eindeutigkeit zu erreichen bzw. wenn 
     grundlegend notwendig -->
<!ATTLIST verweis-es
		%attr.gericht.req;
		%attr.datum.req;
		%attr.az.req;	
		%attr.az-zusatz.opt;
		%attr.es-typ.opt;
		%attr.es-rn.opt;
		%attr.urteilsname.opt;
>

<!-- ================================================================= -->
<!-- maximale Attributauspraegung zu einer Entscheidungssammlung um alle
     Adressierungsmoeglichkeiten vorzuhalten -->
<!-- 20070126 he v1.2.0 CR 5: Attribut li-pkt mit aufgenommen zur Angabe des Linkankers fuer Listenpunkte -->
<!-- 20070112 he v1.2.0 CR 35: Das Attribut fach  wird entfernt, da es kein Gegenstueck mehr aufweist. -->
<!-- 20070402 ms v1.2.1 CR 78: Das Attribut fach wird wieder eingefuegt, da es in zuordnung-esa benoetigt wird, um den esa-eintrag einem Fach zuzuordnen -->
<!-- 20070402 ms v1.2.1 CR 78: Die Attribute start-seite, end-seite, fund-seite und pos-auf-seite werden eingefuegt, um auf Seiten zu verweisen (analog zu verweis-zs) -->
<!-- 20070402 ms v1.2.1 CR 80: Das Attribut vorbem wird eingefuegt, um esa-eintrag einer Vorbemerkungen zu einem Paragraphen in Entscheidungssammlungen zuordnen zu koennen-->
<!-- 20070830 ms v2.2 CR 143: Das Attribut vs-zusatz wird umbenannt in vs-zuordnung-->
<!-- 20090417 ms v2.8 CR 244: Attribut satz eingefuegt-->
<!-- 20090417 ms v2.8 CR 253: Neues Attribut urteilsname -->
<!ENTITY % attribute.verweis-esa  "
		%attr.produkt.req;
		%attr.jahrgang.opt;
		%attr.band.opt;
		%attr.heft.opt;
		%attr.vsk.opt;
		%attr.art.opt;
		%attr.par.opt;
		%attr.abs.opt;
		%attr.li-pkt.opt;
		%attr.vs-zuordnung.opt;
		%attr.vorbem.opt;
		%attr.stichwort.opt;
		%attr.fassung.opt;
		%attr.fach.opt;
		%attr.nr.opt;
		%attr.beilage.opt;
		%attr.land.opt;
		%attr.es-rn.opt;
		%attr.auflage.opt;
		%attr.al.opt;
		%attr.start-seite.opt;
		%attr.end-seite.opt;
		%attr.fund-seite.opt;
		%attr.typ-fundstelle.opt;
		%attr.pos-auf-seite.opt;
		%attr.satz.opt;
		%attr.urteilsname.opt;
		"
>


<!-- Verweis auf eine Entscheidungssammlung -->
<!-- 20070112 he v1.2.0 CR 39: verweis-text aufgenommen -->
<!-- 20070511 ms v2.0 CR 94: verweis-text wieder entfernt-->
<!-- 20070531 ms v1.2.4 CR 117: hervor und abbildung aufgenommen-->
<!-- 20070727 ms v2.1 CR 123: Attribute text-bitv und titel-bitv entfernt-->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!-- 20080425 ms v2.5 CR 190: Element korrektur hinzugefuegt-->
<!ELEMENT verweis-esa      (%inline.einfach; | hervor | abbildung | sprache | korrektur)* >

<!ATTLIST verweis-esa
		%attribute.verweis-esa;
>


<!-- ================================================================= -->
<!-- Verweis auf einen Kommentar, Handbuch Beitrag in einem Werk -->
<!-- 20070112 he v1.2.0 CR 35: Durch die Zuordnung von Vorschriften oder 
     Entscheidungen zu Kommentaren, muss auch der Link die Referenzierung erlauben -->
<!-- 20070112 he v1.2.0 CR 39: verweis-text aufgenommen -->

<!ELEMENT verweis-komhbe      ((verweis-vs | verweis-es)*, verweis-text?) >

<!-- 20060717 he v1.0.0: bezX und nrX Umsetzung Allgemeines Punkt 6  -->
<!-- 20070112 he v1.2.0 CR 35: Die Attribute nrX werden durch wertX ersetzt, 
     um die Gegenstuecke zu den Quellangaben zu haben. Das Attribut fach 
     wird entfernt, da es kein Gegenstueck mehr aufweist. -->
<!-- 20070510 ms v2.0 CR 93: neue Attribute auflage, jahr-->
<!-- 20070727 ms v2.1 CR 123: Attribute text-bitv und titel-bitv entfernt-->
<!-- 20080323 ms v2.3.6 CR 176: Attribut al fuer Aktualisierungslieferung aufgenommen-->
<!ATTLIST verweis-komhbe
		%attr.produkt.req;
		bez1		CDATA		#IMPLIED
		wert1		CDATA		#IMPLIED
		bez2		CDATA		#IMPLIED
		wert2		CDATA		#IMPLIED
		bez3		CDATA		#IMPLIED
		wert3		CDATA		#IMPLIED
		bez4		CDATA		#IMPLIED
		wert4		CDATA		#IMPLIED
		bez5		CDATA		#IMPLIED
		wert5		CDATA		#IMPLIED
		bez6		CDATA		#IMPLIED
		wert6		CDATA		#IMPLIED
		bez7		CDATA		#IMPLIED
		wert7		CDATA		#IMPLIED
		bez8		CDATA		#IMPLIED
		wert8		CDATA		#IMPLIED
		%attr.rn.opt;
		%attr.auflage.opt;
		%attr.jahr.opt;
		%attr.abb-nr.opt;
		%attr.tab-nr.opt;
		%attr.id-ref.opt;
		%attr.typ-fundstelle.opt;
		%attr.al.opt;
>


<!-- ================================================================= -->
<!-- maximale Attributauspraegung um eine Fundstelle in einer 
     Zeitschrift anzugeben -->
<!-- 20060926 he v1.1.0 CR 8: Attribut autor wird entfernt, da Verlinkung ueber Elementangabe -->
<!-- 20070112 he v1.2.0 CR 35: Die Attribute nrX werden durch wertX ersetzt, 
     um die Gegenstuecke zu den Quellangaben zu haben. Das Attribut fach 
     wird entfernt, da es kein Gegenstueck mehr aufweist. -->
<!-- 20070829 ms v2.2 CR 111: Das Attribut fach wird eingefuehrt, weil manche Zeitschriftenbeitraege nach fach zitiert werden-->
<!-- 20090417 ms v2.8 CR 253: Neues Attribut urteilsname -->
<!ENTITY % attribute.verweis-zs  "
		%attr.produkt.req;
		%attr.jahr.opt;
		%attr.band.opt;
		%attr.heft.opt;
		%attr.start-seite.opt;
		%attr.end-seite.opt;
		%attr.fund-seite.opt;
		%attr.pos-auf-seite.opt;
		bez1		CDATA		#IMPLIED
		wert1		CDATA		#IMPLIED
		bez2		CDATA		#IMPLIED
		wert2		CDATA		#IMPLIED
		bez3		CDATA		#IMPLIED
		wert3		CDATA		#IMPLIED
		bez4		CDATA		#IMPLIED
		wert4		CDATA		#IMPLIED
		%attr.beilage.opt;
		%attr.abb-nr.opt;
		%attr.tab-nr.opt;
		%attr.datum.opt;
		%attr.land.opt;
		%attr.gruppe.opt;
		%attr.typ-fundstelle.opt;
		%attr.nr.opt; 
		%attr.fach.opt;	
		%attr.urteilsname.opt;
		"
>

<!-- 20060926 he v1.1.0 CR 8: Abbildung des Verweises auf Autoren ueber das 
                              gleiche Element, wie es in den Zieldokumenten 
                              aufgefuehrt wird -->
<!-- Verweis auf eine Zeitschrift respektive einen Aufsatz dadrin -->
<!ELEMENT verweis-zs      (autor*, verweis-text?) >
<!-- 20070727 ms v2.1 CR 123: Attribute text-bitv und titel-bitv entfernt-->

<!ATTLIST verweis-zs
		%attribute.verweis-zs;
>

<!-- Aufnahme des Verweistextes -->
<!-- 20070531 ms v1.2.4 CR 117: hervor und abbildung aufgenommen-->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!-- 20080425 ms v2.5 CR 190: Element korrektur hinzugefuegt-->
<!ELEMENT verweis-text      (%inline.einfach; | hervor | abbildung  | sprache | korrektur)* >

<!-- ================================================================= -->
<!-- Verweis auf ein BMF Schreiben welches aenhlich wie eine Zeitschrift 
     aber mit Besonderheiten bei den Inhalte aufwartet  -->
<!-- 20070112 he v1.2.0 CR 39: verweis-text aufgenommen -->
<!-- 20070511 ms v2.0 CR 94: verweis-text wieder entfernt-->
<!-- 20070531 ms v1.2.4 CR 117: hervor und abbildung aufgenommen-->
<!-- 20070727 ms v2.1 CR 123: Attribute text-bitv und titel-bitv entfernt-->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!-- 20080425 ms v2.5 CR 190: Element korrektur hinzugefuegt-->
<!-- Version 1 bis 28.04.09:
<!ELEMENT verweis-bmf      (%inline.einfach; | hervor | abbildung | sprache | korrektur)* >
<!ATTLIST verweis-bmf
		%attr.jahr.req;
		%attr.monat.req;
		%attr.tag.req;
		es-jahr		CDATA		#IMPLIED
		%attr.start-seite.opt;
		%attr.gl-nr.opt;
		referat		CDATA		#IMPLIED
		%attr.az.opt;>
-->
<!-- 
jahr,
monat und 
tag 		= geben das Datum des BMF Shreibens an und sind zwingend erforderlich
es-jahr 	= gibt das Erscheinungsjahr an
start-seite 	= ist das universelle Attribut, um die Seitenangabe der Fundstelle zu erfassen
gl-nr 		= wird verwendet um die Teilziffer(=Gliederungsnummer) in dem Schreiben anzugeben
referat 	= erfasst die Angabe des herausgebenden Referates
akz 		= gibt das Aktenzeichen einer Entsheidung an, die in der Fundstelle zitiert wird
-->

<!-- 20090428 ms v2.8 CR 256: Neuer DTD-Zweig fuer Verwaltungsanweisungen: verweis-bmf geaendert in verweis-va-->
<!ELEMENT verweis-va      (%inline.einfach; | hervor | abbildung | sprache | korrektur)* >
<!ATTLIST verweis-va
		%attr.behoerde.opt;
		%attr.datum.req;
		%attr.az.opt;
		%attr.az-zusatz.opt;
		%attr.va-typ.opt;
		%attr.rn.opt;
		%attr.bmf-doknr.opt;
>


<!-- ================================================================= -->
<!-- Verweis auf eine Internetadresse -->
<!-- 20070112 he v1.2.0 CR 39: verweis-text aufgenommen -->
<!-- 20070507 ms v1.2.2 CR 99: Attribut target aus HTML uebernommen -->
<!-- 20070511 ms v2.0 CR 94: verweis-text wieder entfernt-->
<!-- 20070531 ms v1.2.4 CR 117: hervor und abbildung aufgenommen-->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!-- 20080425 ms v2.5 CR 190: Element korrektur hinzugefuegt-->
<!ELEMENT verweis-url      (%inline.einfach; | hervor | abbildung | sprache | korrektur)* >

<!-- der typ der Adresse wird nicht in selbiger angegeben sondern separat -->
<!ATTLIST verweis-url
		typ 		(http | https | ftp | mailto) "http"
		adresse		CDATA		#REQUIRED
		geprueftam	CDATA		#IMPLIED
		target		(_blank | _self | _parent | _top) #IMPLIED 
		%attr.text-bitv.opt;
		%attr.titel-bitv.opt;
>


<!-- ================================================================= -->
<!-- Verweis auf ein Objekt z.B. eine Datei oder ein externes Dokument -->
<!-- 20070112 he v1.2.0 CR 39: verweis-text aufgenommen -->
<!-- 20070511 ms v2.0 CR 94: verweis-text wieder entfernt-->
<!-- 20070531 ms v1.2.4 CR 117: hervor und abbildung aufgenommen-->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!-- 20080425 ms v2.5 CR 190: Element korrektur hinzugefuegt-->
<!ELEMENT verweis-obj      (%inline.einfach; | hervor | abbildung | sprache | korrektur)* >

<!-- Die Referenz ist die direkt verarbeitbare Adresse zum Objekt, z.B. ein Ablagepfad -->
<!-- In der Aktion wird das Verhalten des Objektes im Inhalt angegeben -->
<!-- Der Typ dient zur Klassifizierung in der Repraesentation des Objektes -->
<!ATTLIST verweis-obj
		referenz	CDATA				#REQUIRED
		aktion 		(ausfuehren | einbetten) 	"ausfuehren"
		typ 		(beispiel |
				 formular |
				 vertrag |
				 checkliste |
				 brief |
				 formulierung | 
				 berechnung) 			#IMPLIED
		%attr.text-bitv.opt;
		%attr.titel-bitv.opt;
>

<!-- ================================================================= -->
<!-- Verweis auf ein Dokument ueber die VolltextID -->

<!-- 20060926 he v1.1.0 CR 3: als neues Element fuer Verweise auf die vtext-id hinzugefuegt -->
<!-- 20070112 he v1.2.0 CR 39: verweis-text aufgenommen -->
<!-- 20070511 ms v2.0 CR 94: verweis-text wieder entfernt-->
<!-- 20070531 ms v1.2.4 CR 117: hervor und abbildung aufgenommen-->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!-- 20080425 ms v2.5 CR 190: Element korrektur hinzugefuegt-->
<!ELEMENT verweis-vtext-id      (%inline.einfach; | hervor | abbildung | sprache | korrektur)* >

<!-- Der Typ dient zur optionalen Angabe welches Dokument angegeben und erwartet wird -->
<!ATTLIST verweis-vtext-id
		%attr.vtext-id.req;
		%attr.vtext-typ.opt;
		%attr.text-bitv.opt;
		%attr.titel-bitv.opt;
>


<!-- ================================================================= -->
<!-- Verweis zur Aufnahme von noch nicht ermittelbaren Verweiszielen  -->


<!-- 20070125 he v1.2.0 CR 57: verweis-unbekannt als neues Element definiert -->
<!-- 20070511 ms v2.0 CR 94: verweis-text wieder entfernt-->
<!-- 20070531 ms v1.2.4 CR 117: hervor und abbildung aufgenommen-->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!-- 20080425 ms v2.5 CR 190: Element korrektur hinzugefuegt-->
<!ELEMENT verweis-unbekannt      (%inline.einfach; | hervor | abbildung | sprache | korrektur)* >

<!-- Im Attribut text wird die Zeichekette abgelegt, die noch nicht als Link aufgeloest werden konnte -->
<!-- Der Typ dient zur Angabe um welche Dokumentart es sich beim Link handeln koennte -->
<!ATTLIST verweis-unbekannt
		%attr.text.req;
		%attr.verweis-typ.req;
>
<!-- ================================================================= -->
<!-- Verweis auf einen lexikalischen Beitrag  -->
<!-- 20081029 ms v2.7 CR 224: Neuer Verweis-typ verweis-lex-->
<!ELEMENT verweis-lex      (%inline.einfach; | hervor | abbildung | sprache | korrektur)* >

<!ATTLIST verweis-lex
		%attr.produkt.req;
		%attr.auflage.opt;
		%attr.jahr.opt;
		begriff-normiert	CDATA	#REQUIRED
>


<!-- ================================================================= -->
<!-- Fuer einzelne Zeichen, die zwischen Verweisen stehen  -->
<!-- 20070511 ms v2.0 CR 94: neues Element fuer Zeichen, die z.B. in Normenkette zwischen Verweisen stehen -->
<!ELEMENT zwischen-zeichen	(#PCDATA)>