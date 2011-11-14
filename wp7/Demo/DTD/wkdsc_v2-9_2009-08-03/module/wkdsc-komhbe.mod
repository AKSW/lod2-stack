<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC KOMMENTAR DTD Modul						-->
<!-- 									-->
<!--	WKDSC DTD Version: 2.9 vom 04.08.2009					-->
<!-- 	 letzte Aenderung dieses Moduls: 03.08.2009					-->
<!-- 	Public Identifier: -//WKD//DTD WKDSC KOMMENTAR MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->

<!-- ******************************************************************	-->
<!-- gloable Aenderungen 						-->
<!-- 									-->
<!-- 20070112 he v1.2.0 : Modularisierung fuer Produkt-DTD		-->
<!-- 20070530 ms v2.0: Aufteilung von komhbe in kommentierung und beitrag -->
<!-- ******************************************************************	-->



<!-- ***************************************************************** -->
<!-- redaktioneller Kommentar, Handbuch, Beitrag -->
<!-- ***************************************************************** -->


<!-- ================================================================= -->
<!-- Entitaetendefinition fuer den Zweig komhbe -->

<!-- Inhaltsmodell von komhbe und den Ebenen -->
<!-- 20060628 HE v1.0.0 Umsetzung Beitrag 6 und 7 -->
<!ENTITY % elemente.kommentar		"(kommentierung-ebene | zwischen-titel | lexikon-eintrag | anmerkung | %absatz.komplett; | %elemente.zitat.kein-block; | kommentierung)*" >
<!ENTITY % elemente.beitrag		"(beitrag-ebene | zwischen-titel | lexikon-eintrag | anmerkung | %absatz.komplett; | %elemente.zitat.kein-block; )*" >


<!-- ================================================================= -->
<!-- Basisdefinition einer Kommentierung -->

<!-- 20070112 he v1.2.0 CR 33: komhbe-material mehrfach optional zugelassen -->
<!-- 20070112 he v1.2.0 CR 35: Angabe der Zuordnung von Kommentaren zu 
     Vorschriften oder Entscheidungen ueber das gleichnamige Element -->
<!-- 20070510 ms v2.0 CR 97: zitat-vs mehrfach zugelassen-->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20070531 ms v2.0 CR 111: verbundene-dokumente hinzugefuegt-->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!-- 20080425 ms 2.5 CR 187: Neues Element kommentierung-vs-zusatz-->
<!-- 20081029 ms v2.7 CR 213: kommentierung in kommentierung und kommentierung-ebene erlaubt-->
<!ELEMENT kommentierung	  (zuordnung-produkt?, zitat-vs*, kommentierung-vs-zusatz*, kommentierung-vs-versionen?, kommentierung-bezug, 
			  kommentierung-material*,
			  autor?, bearbeiter?, 
			  %elemente.metadaten;,
			  stichworte?, 
			  werk-steuerung?,
			  verbundene-dokumente*, 
			  (abstract | %elemente.verzeichnis.kommentierung;)*, 
			  %elemente.kommentar;, 
			  anlage*) 
>

<!-- 20070112 he v1.2.0 CR 35: Zuordnung von Kommentaren zu Vorschriften oder Entscheidungen. Ueber die Verweise koennen die Beziehungen zu den kommentierten Texten hergestellt werden. -->
<!ELEMENT kommentierung-bezug (verweis-vs | verweis-es)+ >

<!-- Im Attribut fach wird die Abgabe zur Adressierung des Dokumentes angegeben -->
<!-- 20070112 he v1.2.0 CR 35: Das Attribut fach wird durch die Angabe 
     bez und wert ersetzt. Das Attribut produkt fuer die Zuordnung zum
     Originalenprodukt ist neu -->
<!ATTLIST kommentierung
		%attr.bez.req;
		%attr.wert.req;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		%attr.rechteinhaber.opt;
		%attr.sprache.opt;
>


<!-- ================================================================= -->
<!-- rekursive Ebene eines komhbe mit eigenem Titelkopf, Abstract und 
     den bekannten kompletten Absatzelementen -->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20080915 ms v2.6 CR 210: kommentierung-material hinzugefuegt-->
<!ELEMENT kommentierung-ebene	(titel-kopf, 
			  	 stichworte?, 
				 werk-steuerung?,
				 autor?, bearbeiter?, 
				 abstract?, 
				 (%elemente.verzeichnis.kommentierung; |  kommentierung-material)*, 
				 %elemente.kommentar; ) > 

<!-- die Gliederungsnummer fuer die Zieladresse der Verlinkung -->
<!-- 20070112 he v1.2.0 CR 35: statt nr wird jetzt das Attribut wert zur 
     Angabe des Wertes fuer die Bezeichnung benutzt -->
<!-- 20070510 ms v2.0 CR 22: vtext-id eingefuegt-->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->   
<!ATTLIST kommentierung-ebene
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.vtext-id.opt;
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- 20060927 he v1.1.0 CR 13: Abbildung von Materialien und Versionen von Vorschriften  -->
<!-- 20070112 he v1.2.0 CR 33: zitat-vs im Inhaltsmodell von komhbe-material zugelassen -->
<!-- 20080915 ms v2.6 CR 210: randnummer in kommentierung-material hinzugefuegt-->

<!ELEMENT kommentierung-material		(kennung?, titel?, (%absatz.basis; | zwischen-titel | zitat-vs | randnummer)+) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->   
<!ATTLIST kommentierung-material
		typ		(bt-druck)	#REQUIRED
		%attr.sprache.opt;
>


<!ELEMENT kommentierung-vs-versionen	(kommentierung-vs-version+) >

<!ELEMENT kommentierung-vs-version	((titel?, zitat-vs)+) >

<!-- 20080425 ms 2.5 CR 187: Neues Element kommentierung-vs-zusatz-->
<!ELEMENT kommentierung-vs-zusatz	(kennung?, titel?, (%absatz.basis; | zwischen-titel | zitat-vs)+) >
<!ATTLIST kommentierung-vs-zusatz
		typ		(aenderungsvorschrift | geltungsbereich | hinweis | unbekannt) 	#REQUIRED
		%attr.sprache.opt;
>	

<!-- ***************************************************************** -->
<!-- Randziffernorientierter Kommentar fuer Printpublikationen -->
<!-- ***************************************************************** -->

<!-- ================================================================= -->
<!-- Entitaetendefinition fuer den Zweig des Kommentars -->

<!-- Inhaltsmodell vom Kommentar und dessen Ebenen -->
<!-- 20060615 HE absatz zugelassen -->
<!-- 20060704 HE v1.0.0 zitat-block zugelassen, Umsetzung Kommentar Pkt. 28 -->
<!ENTITY % elemente.kommentar-rn			"(kommentierung-rn-ebene | anmerkung | randnummer | container-block | kommentierung-rn)*" >
<!ENTITY % elemente.beitrag-rn			"(beitrag-rn-ebene | anmerkung | randnummer | container-block )*" >


<!-- ================================================================= -->
<!-- Basisdefinition einer Kommentierung -->
<!-- 20060704 HE v1.0.0
	* titel-kopf oder alternativ zitat-vs zur eindeutigen Abbildung der Ebenenbeschriftung, Umsetzung Kommentar Pkt. 12
	* Verzeichnisse beliebig zugelassen, Umsetzung Kommentar Pkt. 19 -->
<!-- 20060926 he v1.1.0 CR 28: Umbenennung in komhbe-rn -->
<!-- 20070112 he v1.2.0 CR 33: komhbe-material mehrfach optional zugelassen -->
<!-- 20070112 he v1.2.0 CR 35: Angabe der Zuordnung von Kommentaren zu 
     Vorschriften oder Entscheidungen ueber das gleichnamige Element -->
<!-- 20070510 ms v2.0 CR 97: zitat-vs mehrfach zugelassen-->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20070531 ms v2.0 CR 111: verbundene-dokumente hinzugefuegt-->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!-- 20080425 ms 2.5 CR 187: Neues Element kommentierung-vs-zusatz-->
<!-- 20081029 ms v2.7 CR 213: kommentierung-rn in kommentierung-rn und kommentierung-rn-ebene erlaubt-->

<!ELEMENT kommentierung-rn	(zuordnung-produkt?, zitat-vs*, kommentierung-vs-zusatz*, kommentierung-vs-versionen?, kommentierung-bezug, 
			  	kommentierung-material*,
			  	autor?, bearbeiter?, 
			  	%elemente.metadaten;,
			  	stichworte?, 
			  	werk-steuerung?,
			  	verbundene-dokumente*, 
			  	(%elemente.verzeichnis.kommentierung;)*,
			  	%elemente.kommentar-rn;, 
			  	anlage* ) >



<!-- 20070112 he v1.2.0 CR 35: Das Attribut fach wird durch die Angabe 
     bez und wert ersetzt. Das Attribut produkt fuer die Zuordnung zum
     Originalenprodukt ist neu -->
<!ATTLIST kommentierung-rn
		%attr.bez.req;
		%attr.wert.req;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		%attr.rechteinhaber.opt;
		%attr.sprache.opt;
>


<!-- ================================================================= -->
<!-- Inhaltsmodell einer Randziffer in einem Kommentar -->
<!ELEMENT randnummer		(zwischen-titel | %absatz.komplett; | %elemente.zitat.kein-block; | anmerkung)+ >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->   
<!ATTLIST randnummer
		%attr.rn.req;
		%attr.rn-bis.opt;
		%attr.sprache.opt;
>


<!-- ================================================================= -->
<!-- rekursive Ebene eines Kommentares mit eigenem Titelkopf, Abstract und 
     den Randziffernelementen
     Ueber das Attribut kann, falls erforderlich, der Ebene direkt eine 
     Randziffer zugeordnet werden -->
<!-- 20060704 HE v1.0.0
	* autor eingefuegt, Umsetzung Kommentar Pkt. 1 
	* titel-kopf oder alternativ zitat-vs zur eindeutigen Abbildung der Ebenenbeschriftung, Umsetzung Kommentar Pkt. 12
	* Verzeichnisse beliebig zugelassen, Umsetzung Kommentar Pkt. 22 siehe 19 -->
<!-- 20060926 he v1.1.0 CR 28: Umbenennung in komhbe-rn-ebene -->
<!-- 20070112 he v1.2.0 CR 35: statt nr wird jetzt das Attribut wert zur 
     Angabe des Wertes fuer die Bezeichnung benutzt -->
<!-- 20070510 ms v2.0 CR 22: vtext-id eingefuegt-->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20080915 ms v2.6 CR 210: kommentierung-material hinzugefuegt-->

<!ELEMENT kommentierung-rn-ebene	(titel-kopf, 
				 stichworte?, 
				 werk-steuerung?,
				 autor?, bearbeiter?, 
				 abstract?, 
				 (%elemente.verzeichnis.kommentierung; |  kommentierung-material)*, 
				 %elemente.kommentar-rn;) > 

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->   
<!ATTLIST kommentierung-rn-ebene
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.vtext-id.opt;
		%attr.sprache.opt;
>


<!-- ================================================================= -->
<!-- Basisdefinition eines Beitrags -->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20070531 ms v2.0 CR 111: verbundene-dokumente hinzugefuegt-->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!-- 20071116 ms v2.3.2 CR 161: Neues Element beitrag-quelle hinzugefuegt-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!ELEMENT beitrag	  (zuordnung-produkt?, 
			  titel-kopf, 
			  autor?, bearbeiter?, 
			  beitrag-quelle?, 
			  %elemente.metadaten;,
			  stichworte?, 
			  werk-steuerung?,
			  verbundene-dokumente*, 
			  (abstract | %elemente.verzeichnis.alle;)*, 
			  %elemente.beitrag;, 
			  anlage*) 
>

<!ATTLIST beitrag
		%attr.beitragtyp.req;
		%attr.bez.req;
		%attr.wert.req;
		%attr.start-seite.opt;
		%attr.end-seite.opt;
		%attr.pos-auf-seite.opt;
		%attr.beilage.opt;
		%attr.prio.opt;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		%attr.datum.opt;
		%attr.rechteinhaber.opt;
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- rekursive Ebene eines komhbe mit eigenem Titelkopf, Abstract und 
     den bekannten kompletten Absatzelementen -->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20090803 ms v2.9 CR 258: beitrag-quelle hinzugefuegt-->
<!ELEMENT beitrag-ebene	(titel-kopf,  stichworte?, werk-steuerung?,
				 autor?, bearbeiter?, 
			  	 beitrag-quelle?, 
				 abstract?, 
				 (%elemente.verzeichnis.kommentierung;)*, 
				 %elemente.beitrag;) > 

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->   
<!ATTLIST beitrag-ebene
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.sprache.opt;
>

<!-- ***************************************************************** -->
<!-- Randziffernorientierter Beitrag fuer Printpublikationen -->
<!-- ***************************************************************** -->

<!-- Basisdefinition eines Beitrages -->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20070531 ms v2.0 CR 111: verbundene-dokumente hinzugefuegt-->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!-- 20071116 ms v2.3.2 CR 161: Neues Element beitrag-quelle hinzugefuegt-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!ELEMENT beitrag-rn		(zuordnung-produkt?, 
				titel-kopf,
			  	autor?, bearbeiter?, 
				beitrag-quelle?, 
			  	%elemente.metadaten;,
			  	stichworte?, 
			  	werk-steuerung?,
			  	verbundene-dokumente*, 
			  	(%elemente.verzeichnis.alle;)*,
			  	%elemente.beitrag-rn;, 
			  	anlage* ) >

<!ATTLIST beitrag-rn
		%attr.beitragtyp.req;
		%attr.bez.req;
		%attr.wert.req;
		%attr.start-seite.opt;
		%attr.end-seite.opt;
		%attr.pos-auf-seite.opt;
		%attr.beilage.opt;
		%attr.prio.opt;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		%attr.datum.opt;
		%attr.rechteinhaber.opt;
		%attr.sprache.opt;
>


<!-- ================================================================= -->
<!-- rekursive Ebene eines Kommentares mit eigenem Titelkopf, Abstract und 
     den Randziffernelementen
     Ueber das Attribut kann, falls erforderlich, der Ebene direkt eine 
     Randziffer zugeordnet werden -->
<!-- 20070619 ms v2.0 CR 88: stichworte hinzugefuegt -->
<!-- 20090803 ms v2.9 CR 258: beitrag-quelle hinzugefuegt-->
<!ELEMENT beitrag-rn-ebene	(titel-kopf, 
				  stichworte?, 
				 werk-steuerung?,
				 autor?, bearbeiter?, 
			  	 beitrag-quelle?, 
				 abstract?, 
				 (%elemente.verzeichnis.kommentierung;)*, 
				 %elemente.beitrag-rn;) > 

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->   
<!ATTLIST beitrag-rn-ebene
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.sprache.opt;
>

<!-- 20071116 ms v2.3.2 CR 161: Neues Element beitrag-quelle hinzugefuegt-->
<!ELEMENT beitrag-quelle	(quelle?, 	(verweis-url | verweis-unbekannt)*, organisation*) >

<!ATTLIST beitrag-quelle 	typ	CDATA	#IMPLIED
>

