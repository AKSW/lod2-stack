<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC ABSATZ DTD Modul						-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 15.09.2008					-->
<!-- Public Identifier: -//WKD//DTD WKDSC ABSATZ MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->



<!-- ***************************************************************** -->
<!-- Listen -->
<!-- ***************************************************************** -->

<!-- Liste mit manueller Vergabe des Listenpunktes und rekursivem Inhaltsmodell -->
<!-- Die Abkuerzung auf li entspricht einer redaktionellen Optimierung -->
<!ELEMENT liste  (li+ )>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->  
<!ATTLIST liste	%attr.sprache.opt; 
>

<!-- 200060613 HE aufhebung fuer Vorschriftenzweig zugelassen  -->
<!-- 20060704 HE v1.0.0 fuer die manuelle Vergabe der kennung muss diese immer angegeben werden, Umsetzung Allgemeines Pkt. 48 -->
<!ELEMENT li  (kennung, %elemente.li;)>

<!-- Listenpunkt um Verweise auf einzelne Listeneintraege zu ermoeglichen -->
<!-- 20060926 he v1.1.0 CR 5: Attribut zur Angabe des Linkankers in Listen geaendert -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->  
<!ATTLIST li
		%attr.li-pkt.opt;
		%attr.sprache.opt; 
>

<!-- ================================================================= -->
<!-- Liste fuer automatisch generierbare Aufzaehlungszeichen -->
<!ELEMENT liste-auto  (li-auto)+ >

<!-- 20060705 HE v1.0.0 Attributwerte angepasst Umsetzung Allgemeines Pkt. -->
<!-- 20070829 ms v2.2 CR 148: Neue Typen hinzugefuegt: kreis, kreisgefuellt, Typ kasten entfernt-->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->  
<!ATTLIST liste-auto
		typ  	(werksteuerung | 
			strich| 
			arabisch | 
			alphaklein | 
			alphagross | 
			doppelalpha | 
			roemklein | 
			roemgross| 
			radiobutton | 
			checkbox | 
			kreis | 
			kreisgefuellt) #REQUIRED
		%attr.abstand.opt;
		kennung-ergaenzung		( punkt | klammerzu | punktklammerzu | klammeraufzu ) #IMPLIED
		einzug-blindtext	CDATA	#IMPLIED
		%attr.sprache.opt; 
>
<!-- 20060705 HE v1.0.0 Inhaltsmodell analog zu li, Umsetzung Allgemein Pkt. 47 -->
<!ELEMENT li-auto  %elemente.li; >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->  
<!ATTLIST li-auto %attr.sprache.opt; 
>

<!-- ***************************************************************** -->
<!-- allgemeine zu verwendende Anmerkungen als Blockcontainer -->
<!-- ***************************************************************** -->

<!-- 20070531 ms v1.2.4 CR 116: container-block hinzugefuegt-->
<!ELEMENT anmerkung         (kennung?, (%absatz.basis; | container-block)+ ) >

<!-- ref-id fuer die Verweismoeglichkeit, der Typ zur semantischen Auszeichnung -->
<!-- 20070130 v1.2.0 CR 40: Zur Differenzierung des Artes einer Anmerkung -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->  
<!ATTLIST anmerkung
		%attr.typ-herkunft.opt;
		%attr.ref-id.opt;
		%attr.anmerkung-typ.req;
		%attr.sprache.opt; 
>


<!-- ueber die Nummer kann aus einem Inlinetext auf eine Anmerkung referenziert werden -->
<!ELEMENT ref-anmerkung EMPTY >
<!-- 20080425 ms v2.5 CR 186: Attribut nr hinzugenommen, damit Nummer separat von ID erfasst werden kann-->
<!ATTLIST ref-anmerkung
		%attr.id-ref.req;
		%attr.nr.opt;
>


<!-- ***************************************************************** -->
<!-- Absatzelemente -->
<!-- ***************************************************************** -->

<!ELEMENT absatz	(%inline.komplett; )* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->  
<!ATTLIST absatz %attr.sprache.opt; 
>

<!-- ================================================================= -->
<!-- Abbildung einer rechtsseitigen Absatzausrueckung -->
<!ELEMENT absatz-rechts (bezugstext, ausrueckung) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->  
<!ATTLIST absatz-rechts %attr.sprache.opt; 
>

<!-- der links aufgefuehrte Text auf den sich die rechte Seite bezieht -->
<!ELEMENT bezugstext    (%inline.komplett;)* >

<!-- und dies ist die Ausrueckung rechtsseitig -->
<!ELEMENT ausrueckung   (%inline.komplett;)* >


<!-- ***************************************************************** -->
<!-- Kennzeichnung von Zitatbloecken fuer die einzelnen Dokumenttypen  -->
<!-- ***************************************************************** -->
<!-- quelle dient der texttuellen Quellangabe, beschriftung der 
     optionalen Beschriftung des Zitatblockes
     ueber die bekannten Verweisattribute kann die Quelle angegeben werden,
     sofern bekannt -->

