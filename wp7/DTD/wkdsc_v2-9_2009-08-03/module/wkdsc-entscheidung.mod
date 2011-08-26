<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC ENTSCHEIDUNG DTD Modul					-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 17.04.2009					-->
<!-- Public Identifier: -//WKD//DTD WKDSC ENTSCHEIDUNG MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->



<!-- ***************************************************************** -->
<!-- Volltextentscheidung -->
<!-- ***************************************************************** -->

<!-- 20060926 he v1.1.0 CR 1: titel als optionales Element hinzugefuegt -->
<!-- 20070112 he v1.2.0 CR 36: es-titel-kopf statt titel aufgenommen, 
     um einen spezifischen Titel fuer Entscheidungen aufzunehmen und 
     diesen auch in Produkten verwenden zu koennen -->
<!-- 20070125 he v1.2.0 CR 65: Angabe der redaktionellen Leitsaetze mit eigenem Schachtelungselement -->
<!-- 20070126 he v1.2.0 CR 58: Die pressemitteilung wird als Element rausgenommen und wird ein eigener Zweig -->
<!-- 20070402 ms v1.2.1 CR 79: Element red-leitsaetze entfernt, da mit red-leitsaetze eine Mischung von amtlichen und redaktionellen Leitsaetzen innerhalb einer Entscheidung nicht realisierbar ist-->
<!-- 20070531 ms v2.0 CR 111: verbundene-dokumente hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!-- 20070727 ms v2.1 CR 135: neue Elemente leitsatz-block und orientierungssatz-block, die zusaetzlich normenkette enthalten koennen -->
<!-- 20070919 ms v2.3 CR 135: Aenderung vom 27.07. angepasst: leitsatz-block und orientierungssatz-block wird entfernt. Dafuer werden leitsaetze und orientierungssaetze zusammen mit normenkette mehrfach erlaubt-->
<!-- 20080915 ms v2.6 CR 199: mitgeteiltvon hinzugefuegt-->
<!ELEMENT entscheidung  (zuordnung-produkt?, verbundene-dokumente*, mitgeteiltvon?, es-titel-kopf?,
                                             es-metadaten,
			(leitsaetze?, orientierungssaetze?, normenkette?)*,
			 fundstellen?,
			 (es-inhalt | es-inhalt-eu)?, 
			 schlussantrag?)
>


<!-- 20060704 HE v1.0.0 senat erfasst, Umsetzung Entscheidungen Pkt. 20 -->
<!-- 20070130    v1.2.0 CR 56:  gericht, datum, az, az-zusatz wurden nach es-metadaten verschoben - koennen mehrmals vorkommen -->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!-- 20080307 ms v2.3.5 CR 172: Neuer es-typ "gerichtlicher-hinweis"-->
<!-- 20080915 ms v2.6 CR 202: Neue Typen "schlussantrag", "eugh-vorlage", "gutachten"-->
<!-- 20081029 ms v2.7 CR 204: Attribut es-typ von default auf required gesetzt-->

<!ATTLIST entscheidung
		%attr.es-typ.req;
		%attr.senat.opt;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		%attr.sprache.opt;
>		

<!-- ================================================================= -->
<!-- der Titel einer Entscheidung mit dem einheitlichen Titelmodell -->
<!ELEMENT es-titel-kopf		((titel|ohne-titel),
				 titel-zusatz?,
				 toc-titel?,
				 titel-trefferliste?,
				 anmerkung*) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST es-titel-kopf		%attr.sprache.opt;
>
<!-- ================================================================= -->
<!-- die Reihenfolge der Inhalte wird unterschieden  -->
<!-- 20070726 ms v2.1 CR 133: Neues Element streitwertbeschluss am Ende eingefuegt-->
<!-- 20090417 ms v2.8 CR 254: anmerkung am Ende eingefuegt-->
<!ELEMENT es-inhalt	(rubrum?,
			 tenor?,
			 tatbestand?,
			 gruende?,
			 kostenentscheidung?, 
			 streitwertbeschluss?,
			anmerkung?)
>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST es-inhalt		%attr.sprache.opt;
>

<!-- 20070731 ms v2.1 CR 139: Neues Element es-inhalt-eu-block statt rubrum, tenor, tatbestand, gruende, kostenentscheidung, da diese bei EU-Entscheidungen nicht eindeutig definierbar sind-->
<!ELEMENT es-inhalt-eu	(es-inhalt-eu-block+)
>
<!-- 20081029 ms v2.7 CR 228: titel-rn alternativ zu titel zugelassen, zwischen-titel aufgenommen-->
<!ELEMENT es-inhalt-eu-block	((titel-rn | titel)?, (%absatz.basis; | anmerkung | zwischen-titel)+)	>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST es-inhalt-eu-block		typ	CDATA	#IMPLIED
				%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- Allgemeine Metadaten zur Entscheidung die hier zusammengefasst werden -->
<!-- 20070130    v1.2.0 CR 56:  gericht, datum, az, az-zusatz wurden eingefgt -->
<!-- 20070130 v1.2.0 CR 63: Die Elemente vor- und vor-vorinstanz koennen mehrfach vorkommen -->
<!-- 20070726 ms v2.1 CR 134: stichworte optional am Ende eingefuegt-->
<!ELEMENT es-metadaten		(gericht, datum, az-gruppe, urteilsart?, rechtskraft, rechtsmittel, verfahrensart, 
				 es-liste*, 
				 gerichtsort?, 
				 verfahrensbeteiligte?,
				 verhandlungsdatum?, urteilsname?, 				 
				 %elemente.metadaten;, stichworte?)>

<!ATTLIST es-metadaten
		%attr.bezugsquelle.opt;
		%attr.rechteinhaber.opt;
>

<!-- 20070928 ms v2.3 CR 158: esr-metadaten analog zu es-metadaten aufgenommen in es-wiedergabe und esa-eintrag -->
<!-- 20080915 ms v2.6 CR 200: metadaten, rechtsgebiete, taxonomien aufgenommen -->
<!ELEMENT esr-metadaten		(gericht?, datum?, az-gruppe?, urteilsart?, rechtskraft?, rechtsmittel?, verfahrensart?, 
				 es-liste*, 
				 gerichtsort?, 
				 verfahrensbeteiligte?,
				 verhandlungsdatum?, urteilsname?, 				 
				 %elemente.metadaten;)>

<!-- ================================================================= -->
<!-- 20070130 v1.2.0 Neu eingefuehrte Elemente, siehe CR 56-->
<!ELEMENT gericht              (#PCDATA) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!-- 20080915 ms v2.6 CR 201: Neues Attribut gk fuer die genormte Gerichtsbezeichnung / Gerichtskuerzel-->
<!ATTLIST gericht		%attr.sprache.opt;
			gk	CDATA	#IMPLIED
>

<!-- 20070726 ms v2.1 CR 128: Neues Element az-original, um Text wie z.B. 1 StR 216/85 - 88 aufnehmen zu koennen-->
<!ELEMENT az-gruppe             (az-haupt, az*, az-original?) >

<!ELEMENT az-haupt (az)>

<!-- 20070316 sk v1.2.0 Zusatz ist optional -->
<!-- 20080915 ms v2.6 CR 201: Neues Attribut azk fuer das genormte Aktenzeichen -->
<!ELEMENT az       (wert,zusatz?)>
<!ATTLIST az	azk  CDATA  #IMPLIED>

<!ELEMENT wert       (#PCDATA)>

<!ELEMENT zusatz       (#PCDATA)>

<!ELEMENT az-original	(#PCDATA)>
<!-- ================================================================= -->

<!-- grundlegende Metadaten zu einer Entscheidung in gleichnamige Elemente mit Attributlisten ablegen -->
<!ELEMENT urteilsart		EMPTY >

<!-- 20060704 HE v1.0.0 Listeneintrag unbekannt hinzugefuegt, Umsetzung Entscheidung Pkt. 6 -->
<!-- 20070125 he v1.2.0 CR 68: als Attributwert keinurteil eingefuegt -->
<!-- 20080307 ms v2.3.5 CR 172: Neue urteilsarten hinweisbeschluss, berichtigungsbeschluss, ergaenzungsbeschluss-->
<!ATTLIST urteilsart
		typ	(grundurteil | 
			 zwischenurteil | 
			 teilurteil | 
			 endurteil | 
			 schlussurteil | 
			 versaeumnisurteil | 
			 anerkenntnisurteil | 
			 feststellungsurteil |
			 keinurteil |
			 hinweisbeschluss | 
			 berichtigungsbeschluss |
			 ergaenzungsbeschluss |
			 unbekannt) 			#IMPLIED
>


<!ELEMENT rechtskraft		EMPTY >

<!-- 20060704 HE v1.0.0 Listeneintrag unbekannt hinzugefuegt, Umsetzung Entscheidung Pkt. 6 -->
<!-- 20070510 ms v2.0 CR 90: neue Typen von rechtskraft: vorlaeufignichtrechtskraeftig | rechtsmitteleingelegt | nichtrechtskraeftig-->
<!ATTLIST rechtskraft
		typ	(rechtskraeftig |
			 entschieden |
			vorlaeufignichtrechtskraeftig | 
			rechtsmitteleingelegt | 
			nichtrechtskraeftig | 
			 unbekannt) 			#REQUIRED
>


<!ELEMENT rechtsmittel		EMPTY >

<!-- 20060704 HE v1.0.0 Listeneintrag unbekannt hinzugefuegt, Umsetzung Entscheidung Pkt. 6 -->
<!-- 20070130 v1.2.0 CR 59: erstinstanzlich als Typ der Rechtsmittel zugelassen -->
<!ATTLIST rechtsmittel
		typ	(berufung |
			 revision |
			 beschwerde |
			 nichtzulassungsbeschwerde |
			 sprungrevision |
			 erinnerung |
			 gegenvorstellung |
			 rechtsbeschwerde |
			 erstinstanzlich |
			 unbekannt )			#REQUIRED
>


<!ELEMENT verfahrensart		EMPTY >

<!-- 20060704 HE v1.0.0 Listeneintrag unbekannt hinzugefuegt, Eintrag vorlageverfahren entfernt, Umsetzung Entscheidung Pkt. 6 -->
<!ATTLIST verfahrensart
		typ	(rechtsstreit |
			 normenkontrollverfahren |
			 unbekannt)     		#REQUIRED
>

<!-- ================================================================= -->
<!-- eine Liste mit Entscheidungen die fuer die Aufzaehlung derselbigen verwendet werden kann 
     uber den Typ wird die Art der Liste angegeben -->
<!-- 20070125 he v1.2.0 CR 57: verweis-unbekannt zugelassen -->
<!-- 20070611 ms v2.0 CR 111: Wert verweise in Attribut typ entfernt, da dies mit dem neuen Element verbundene-dokumente realisiert wird-->
<!-- 20080407 ms v2.4 CR 184: Attributwert nachinstanz hinzugefuegt-->
<!ELEMENT es-liste		(verweis-es | verweis-unbekannt)+	>

<!ATTLIST es-liste
		typ		(parallelentscheidung | verbundverfahren | vorinstanz | vorvorinstanz | nachinstanz)		#REQUIRED
>


<!-- ================================================================= -->
<!-- 20060704 HE v1.0.0 Umsetzung Entscheidung Pkt. 20 -->
<!ELEMENT gerichtsort		(#PCDATA)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST gerichtsort
		kuerzel		CDATA			#REQUIRED
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- Alle an dem Verfahren beteiligten Personen koennen hier erfasst werden -->
<!-- 20070126 he v1.2.0 CR 61: Neuaufnahme zur Abloesung der Elemente rechtsanwaelte, richter und parteien sowie um mehr Flexibilitaet zu ermoeglichen -->

<!ELEMENT verfahrensbeteiligte		(verfahrensbeteiligter)+ >

<!-- 20070606 ms v2.0.0 CR 122: organisation zugelassen fuer Firmen oder Gebietskoerperschaften, die verfahrensbeteiligte sind-->
<!ELEMENT verfahrensbeteiligter		((person|organisation)+, verfahrensbeteiligter*) >

<!ATTLIST verfahrensbeteiligter
		%attr.verfahrensbeteiligter-typ.req;
>

<!-- ================================================================= -->
<!-- Datum der Verhandlung zur Entscheidung -->
<!ELEMENT verhandlungsdatum		EMPTY >

<!ATTLIST verhandlungsdatum
		%attr.datum.req;
>


<!-- ================================================================= -->
<!-- 20090417 ms v2.8 CR 253: hoch und tief erlaubt -->
<!ELEMENT urteilsname		(%inline.einfach;)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST urteilsname		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- Klammerung der Leitsaetze  -->
<!-- 20070402 ms v1.2.1 CR 79: Optionales Element autor eingefuegt, fuer den Autor eines redaktionellen Leitsatzes-->
<!ELEMENT leitsaetze		(autor*, leitsatz+) >

<!-- 20070125 he v1.2.0 CR 65: Ablage der redaktionellen Leitsaetze mit dem dazugehoerigen Autoren -->
<!-- 20070402 ms v1.2.1 CR 79: Element red-leitsaetze entfernt, da mit red-leitsaetze eine Mischung von amtlichen und redaktionellen Leitsaetzen innerhalb einer Entscheidung nicht realisierbar ist-->
<!--ELEMENT red-leitsaetze	(autor*, leitsatz+) -->


<!ELEMENT leitsatz      	(absatz | liste | liste-auto | anmerkung)+	>

<!-- 20070125 he v1.2.0 CR 65: Attribut herkunft entfernt, da durch neues Element red-leitsatz die Unterscheidung realisiert wird -->
<!-- 20070402 ms v1.2.1 CR 79: Attribut herkunft wieder eingefuegt, da mit red-leitsaetze eine Mischung von amtlichen und redaktionellen Leitsaetzen innerhalb einer Entscheidung nicht realisierbar ist-->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST leitsatz
          %attr.nr.opt;
          %attr.typ-herkunft.req;
	%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- 20070402 ms v1.2.1 CR 79: Optionales Element autor eingefuegt, fuer den Autor eines redaktionellen Orientierungssatzes-->
<!ELEMENT orientierungssaetze  (autor*, orientierungssatz+)                   >


<!ELEMENT orientierungssatz    (absatz | liste | liste-auto | anmerkung)+        >

<!-- 20070125 he v1.2.0 CR 64: Attribut nr als optional definiert -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST orientierungssatz
          %attr.nr.opt;
          %attr.typ-herkunft.req;
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- Volltextrubrum zur Abbildung der originalen Entscheidungsangaben -->
<!ELEMENT rubrum		(titel?, (zwischen-titel | %absatz.basis; | anmerkung)+)	>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST rubrum		%attr.sprache.opt;
>

<!ELEMENT tenor			(titel?, (%absatz.basis; | anmerkung)+)		>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST tenor		%attr.sprache.opt;
>

<!-- 20070727 ms v2.1 CR 136: Neues Element titel-rn alternativ zu titel, in dem rn zugelassen ist, damit Randnummern zum Titel erfasst werden koennen-->
<!-- 20081029 ms v2.7 CR 228: zwischen-titel aufgenommen-->
<!ELEMENT tatbestand		((titel-rn | titel)?, (%absatz.basis; | anmerkung | zwischen-titel)+)	>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST tatbestand		%attr.sprache.opt;
>

<!-- 20081029 ms v2.7 CR 228: zwischen-titel aufgenommen-->
<!ELEMENT gruende		((titel-rn | titel)?, (%absatz.basis; | anmerkung | zwischen-titel)+)	>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST gruende		%attr.sprache.opt;
>

<!ELEMENT kostenentscheidung	((titel-rn | titel)?, (%absatz.basis; | anmerkung)+)	>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST kostenentscheidung	%attr.sprache.opt;
>

<!ELEMENT streitwertbeschluss	((titel-rn | titel)?, (%absatz.basis; | anmerkung)+)	>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST streitwertbeschluss	%attr.sprache.opt;
>
<!-- ================================================================= -->
<!-- die Normenkette besteht aus Verweisen auf Vorschriften -->
<!-- 20070125 he v1.2.0 CR 60: verweis-unbekannt zugelassen -->
<!-- 20070511 ms v2.0 CR 94: zwischen-zeichen aufgenommen, um Zeichen erfassen zu koennen, die zwischen den Verweisen stehen-->

<!ELEMENT normenkette		(verweis-vs | verweis-unbekannt | zwischen-zeichen)+	>

<!-- ================================================================= -->
<!-- Fundstellen sind Verweise auf Zeitschriften oder Entscheidungssammlungen -->
<!ELEMENT fundstellen		(verweis-esa | verweis-zs | verweis-url)+	>


<!-- ***************************************************************** -->
<!-- schlussantrag als Bestandteil einer Entscheidung -->
<!-- ***************************************************************** -->

<!-- 20081029 ms v2.7 CR 229: anmerkung am Ende eingefuegt-->
<!ELEMENT schlussantrag	(rubrum?, schlussantrag-text, tenor?, anmerkung?)	>

<!-- 20070130 v1.2.0 CR 70: Datum als Attribut vom Schlussantrag zugelassen; kann vom Datum der Entscheidung abweichen--> 
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST schlussantrag
		%attr.datum.opt;
		%attr.sprache.opt;
>

<!ELEMENT schlussantrag-text (zwischen-titel | %absatz.basis;)+ >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST schlussantrag-text	%attr.sprache.opt;
>


<!-- ***************************************************************** -->
<!-- andere allgemeine Elemente die nur zusammen mit einer Entscheidung anwendbar sind  -->
<!-- ***************************************************************** -->

<!-- ================================================================= -->
<!-- 20060627 HE v1.0.0 Erweiterung um stichworte zur Erzeugung von Verzeichnissen und Verweise zur Abbildung von Verlinkungen -->
<!ELEMENT es-stichwort		(#PCDATA | stichworte | %verweis.komplett; )* >
<!ATTLIST es-stichwort		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- 20060926 he v1.1.0 CR 26: es-beschlusstext zur Verwendung in redaktionellen Entscheidungen -->
<!ELEMENT es-beschlusstext	(%inline.basis; | %verweis.komplett; )* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST es-beschlusstext 		%attr.sprache.opt;
>

<!-- 20070606 ms v2.0 CR 111: Fuer JURION Analysen neues Element es-rechtskraft um Text zur Rechtskraft aufzunehmen-->
<!ELEMENT es-rechtskraft		(%inline.basis; | %verweis.komplett; )* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST  es-rechtskraft		%attr.sprache.opt;
>


<!-- ================================================================= -->
<!-- Anmerkung zu einer Entscheidung die in den Elementen der 
     Entscheidungswiedergabe als Container fuer Zwischenanmerkungen vorkommt -->
<!-- 20060926 he v1.1.0 CR 26: zwischen-titel eingefuegt und Attribut altdaten
                               zur Uebernahme selbiger (z.B. XHLV redaktionshinweis) -->
<!ELEMENT es-anmerkung		(titel?, (%absatz.basis; | zwischen-titel)+, autor*) >

<!ATTLIST es-anmerkung
		altdaten		CDATA		#IMPLIED
>

