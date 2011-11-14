<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC CONTAINER DTD Modul						-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 28.04.2009					-->
<!-- Public Identifier: -//WKD//DTD WKDSC CONTAINER MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->



<!-- ***************************************************************** -->
<!-- Einbettung eines Objektes in einen eigenen absatzbildenden Block,
     uber die ref-id koennen Verweise ausgefuehrt werden -->
<!-- ***************************************************************** -->

     
<!-- 20060628 HE v1.0.0 Element objekt entfallen und statt dessen 
     verweis-obj benutzt, um die Verwendung der Objektreferenz zu ermoeglichen -->
<!-- 20080407 ms v2.4 CR 181: quelle hinzugefuegt-->
<!ELEMENT objekt-block (kennung?, titel?, verweis-obj, beschriftung?, quelle?, alt-text?, anmerkung?) >
                        
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST objekt-block
		%attr.ref-id.opt;
		%attr.sprache.opt;
>



<!-- ***************************************************************** -->
<!-- Abbildung als Blockelement -->
<!-- ***************************************************************** -->

<!-- Abbildung als Blockelement mit der Moeglichkeit einen Titel zu vergeben, Anmerkungen und einen Alternativtext zu hinterlegen -->
<!-- anmerkung dient zur Erfassung von Quellangaben oder aehnlichem -->
<!-- 20080407 ms v2.4 CR 181: quelle hinzugefuegt-->
<!ELEMENT abbildung-block   (kennung?, titel?, bild, beschriftung?, quelle?, alt-text?, anmerkung?)>

<!-- die nr zur Zielangabe von Verlinkungen 
     orientierung zur Darstellung der Abbildung
     breite gibt den Fluss und die Darstellung auf die Seite bezogen an -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST abbildung-block
		%attr.abb-nr.opt;
		%attr.orientierung.opt;
		%attr.sprache.opt;
>



<!-- ***************************************************************** -->
<!-- allgemeines Zitat -->
<!-- ***************************************************************** -->


<!-- 20060629 HE v1.0.0 
	* zwischen-titel eingefuegt, Umsetzung Kommentar Pkt. 8 
	* Attribut produkt entfernt, Umsetzung Kommentar Pkt. 9 -->
<!ELEMENT zitat-block   (quelle?, beschriftung?, (zwischen-titel | %absatz.komplett;)+) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST zitat-block 	%attr.sprache.opt;
>

<!-- ***************************************************************** -->
<!-- Zitierung einer Vorschrift -->
<!-- ***************************************************************** -->

<!-- 20060926 he v1.1.0 CR 14: Attribut vs-herkunft mit aufgenommen -->
<!ELEMENT zitat-vs   (quelle?, beschriftung?, (%elemente.vorschrift;)+) >

<!-- ueber die Obermenge aller Attribute zum verweisen auf eine Vorschrift 
     kann bei einem Zitat die Quelle angegeben werden, sollte das 
     Vorschriftenkuerzel nicht bekannt sein, ist ein 
     Standardplatzhalterwert anzugeben der dies anzeigt "unknown" -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!-- 20070919 ms v2.3 CR 156: Attribut auszug hinzugefuegt-->
<!ATTLIST zitat-vs
		%attribute.verweis-vs;
		%attr.vs-herkunft.opt;
		%attr.sprache.opt;
		%attr.auszug.default;
>


<!ELEMENT quelle        (%inline.basis;| %verweis.komplett;)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST quelle		%attr.sprache.opt;
>
<!-- ***************************************************************** -->
<!-- Zitierung einer Entscheidung -->
<!-- ***************************************************************** -->


<!ELEMENT zitat-es (quelle?, beschriftung?, (%elemente.entscheidung;)+) >

<!-- sofern einige Adressangaben des Zitates der Entscheidung bekannt sind,
     sind diese hier aufzufuehren, so das eine Verlinkung moeglich waere -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST zitat-es
		%attr.gericht.opt;
		%attr.datum.opt;
		%attr.az.opt;
		%attr.az-zusatz.opt;
		%attr.es-typ.opt;
		%attr.es-rn.opt;
		%attr.sprache.opt;
>

<!-- ***************************************************************** -->
<!-- Zitierung einer Verwaltungsanweisung-->
<!-- ***************************************************************** -->
<!-- 20090428 ms v2.8 CR 256: Neuer DTD-Zweig fuer Verwaltungsanweisungen: zitat-va eingefuegt-->

<!ELEMENT zitat-va (quelle?, beschriftung?, verwaltungsanweisung+) >

<!ATTLIST zitat-va
		%attr.behoerde.opt;
		%attr.datum.opt;
		%attr.az.opt;
		%attr.az-zusatz.opt;
		%attr.va-typ.opt;
		%attr.rn.opt;
		%attr.sprache.opt;
		%attr.bmf-doknr.opt;
>

<!-- ***************************************************************** -->
<!-- Containerblock fuer eine typisierte Blockstruktur im Text -->
<!-- ***************************************************************** -->

<!-- Hervorgehobene Blockstruktur im Textfluss -->
<!-- 20070516 ms v1.2.3 CR 107: anmerkung in container-block eingefuegt-->
<!ELEMENT container-block   (kennung?, titel?, (%absatz.basis; | zwischen-titel | container-block | anmerkung)+) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST container-block
		%attr.container-block.req;
		%attr.thema.opt;
		%attr.ref-id.opt;
		%attr.sprache.opt;
>

<!-- ***************************************************************** -->
<!-- Container fuer eine bestimmte Auspraegung / Medienbezug -->
<!-- ***************************************************************** -->

<!-- 20080312 ms v2.3.6 CR 175: Neues Element container-auspraegung-->
<!ELEMENT container-auspraegung   (kennung?, titel?, (%absatz.basis; | zwischen-titel | container-block | anmerkung)+) >

<!ATTLIST container-auspraegung
		%attr.sprache.opt;
		%attr.typ-medien.req;
>
