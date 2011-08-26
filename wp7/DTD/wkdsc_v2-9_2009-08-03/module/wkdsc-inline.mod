<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC INLINE DTD Modul						-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009			-->
<!--  letzte Aenderung dieses Moduls: 17.04.2009					-->
<!-- Public Identifier: -//WKD//DTD WKDSC INLINE MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->





<!-- ***************************************************************** -->
<!-- Inline Elemente -->
<!-- ***************************************************************** -->

<!-- Hoch und Tiefstellung nur mit PCDATA -->
<!ELEMENT hoch  (#PCDATA)* >


<!ELEMENT tief  (#PCDATA)* >


<!-- ================================================================= -->
<!-- hervorzuhebender Inlinetext -->

<!-- 20060704 HE v1.0.0 gs und kap rausgenommen, Umsetzung Allgemeines Pkt. 18-->
<!-- 20060926 he v1.1.0 CR 26: Wert kl fuer die Abbildung von XHLV petit mit aufgenommen -->
<!-- 20070402 ms v1.2.1 CR 75: variante und text-variante in hervor aufgenommen -->
<!-- 20070507 ms v1.2.2 CR 87: Werte sp (Sperrung), kp (Kapitaelchen), gr (gross), rot, gruen, blau, gelb, cyan, magenta aufgenommen-->
<!-- 20070507 ms v1.2.2 CR 98: Neues Attribut "herkunft" zu hervor, um die Herkunft der Hevorhebung kennzeichnen zu koennen-->
<!-- 20070531 ms v1.2.4 CR 119: bruch aufgenommen-->
<!ELEMENT hervor  (%inline.basis; | %verweis.komplett; | variante | text-variante | bruch)* >
<!ATTLIST hervor
		typ (f| k| fk| kl| u| u2x| dgs | npr| ver| nor| ma | sp | kp | gr | rot | gruen | blau | gelb | cyan | magenta) #REQUIRED
		%attr.herkunft.opt;
>


<!-- 	f   = fett
	k   = kursiv
	fk  = fettkursiv
	kl  = klein
	u   = unterstrichen
	u2x = 2x unterstrichen
	npr = nicht-proportional
	ver = versalien
	nor = normal, wenn in einer Hervorhebung wieder auf normal zurueckgeschaltet werden soll
	ma  = markierung, z.B. Aenderungsmarkierung
	sp = Sperrung
	kp = Kapitaelchen
	gr = gross
-->	



<!-- ================================================================= -->
<!-- Marginalie -->
<!-- 20070619 ms v2.0 CR 88: stichworte umbenannt in inline-stichwort -->
<!-- 20080312 ms v2.3.5 CR 177: abbildung aufgenommen-->
<!ELEMENT marginalie	(%inline.einfach; | inline-stichwort | abbildung)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST marginalie	%attr.sprache.opt; 
>
<!-- ================================================================= -->
<!-- 20060628 HE v1.0.0 Vorgabe durch Publishingstandards erfordern keine Typangabe mehr -->
<!ELEMENT zitat		(%inline.basis;)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST zitat	%attr.sprache.opt; 
>

<!-- ================================================================= -->
<!-- Element zur Abbildung von einfachen Formeln und Bruechen -->
<!ELEMENT formel ( (bruch | ausdruck), operator, (bruch | ausdruck) )>


<!ELEMENT ausdruck (#PCDATA) >


<!ELEMENT bruch ( (zaehler | bruch), (nenner | bruch) )>
<!ATTLIST bruch
		anordnung (bruchstrich | schraegstrich) "schraegstrich"
> 

<!-- 20090417 ms v2.8 CR 237: hoch, tief und hervor in zaehler und nenner aufgenommen-->
<!ELEMENT zaehler (%inline.einfach; | hervor)* >


<!ELEMENT nenner (%inline.einfach; | hervor)* >


<!ELEMENT operator (#PCDATA) >


<!-- ================================================================= -->
<!-- Auszeichnung einer Textluecke bzw. Auslassung im Inlinecontent -->
<!ELEMENT luecke        (alt-text?) >

<!-- 	linienart: symbolische Darstellung der Luecke
	breite: blindtext Laenge der Luecke gemaess dem Blindtext; zeile = bis zum Ende der Zeile -->
<!ATTLIST luecke
		linienart     ( linie |
				punkt| 
				schwarz |
				blind)			"linie"
		breite        ( blindtext | zeile)	"blindtext"
		blindtext	CDATA			#IMPLIED
>


<!-- ================================================================= -->
<!-- Satz zur Nummierung selbiger als generell verwendbares Element mit 
     der Nummer als Attribut -->
<!-- Die Satznummer wird stets eingegeben, um die Ueberpruefung zu ermoeglich -->
<!ELEMENT satz  EMPTY >

<!ATTLIST satz
		%attr.nr.req;
>


<!-- ================================================================= -->
<!-- Randziffer mit der erforderlichen Nummernangabe wie sie in 
     Absatzinhalten Verwendung findet -->

<!-- Ueber die nr-bis Angabe kann auch ein Bereich und ueber den Status 
     besetzt kann dessen Besetzung noch feiner angegeben werden -->

<!ELEMENT rn  EMPTY >
<!-- 20070731 ms v2.1 CR 136: Das Element rn erhaelt ein Attribut herkunft=amtlich| redaktionell| unbekannt, damit Randnnummern in Entscheidungen als amtlich markiert werden koennen, und somit von von redaktionellen Randnnummern in Entscheidungssammlungen unterschieden werden koennen-->
<!ATTLIST rn
		%attr.nr.req;
		nr-bis	CDATA	#IMPLIED
		%attr.typ-besetzt.opt;
		%attr.herkunft.opt;
>


<!-- ================================================================= -->
<!-- Element zur Erfassung von alternativ darzustellenden Text zu 
     einem Elementinhalt -->

<!ELEMENT alt-text  (%titel.einfach;)* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST alt-text	%attr.sprache.opt; 
>

<!-- ================================================================= -->
<!-- Ablage von Stichwoertern in den Ausgaben und Produkten zur Produktion,
     Erfassung und Verwaltung erfolgt DB seitig 
     zu einem Hauptstichwort gibt es mehrere Siehe Auch Verweise und 
     mehrere Unterstichworte -->

<!--20070522 ms CR 88 v2.0 inline soll stichwort kuenftig inline-stichwort heissen-->
<!ELEMENT inline-stichwort  (haupt-stw, siehe-stw*, unter-stw*) >
<!ATTLIST inline-stichwort
          %attr.produkt.opt;
>

<!ELEMENT stichworte  (stichwort+) >

<!-- 20061897 HE v1.0.0 Umsetzung Allgemeines Pkt. 21 -->
<!-- 20070726 ms v2.1 CR 134: Attribut typ mit herkunft und neues Attribut quelle zu stichwort eingefuegt -->

<!ELEMENT stichwort  (haupt-stw, siehe-stw*, unter-stw*) >
<!ATTLIST stichwort
          %attr.produkt.opt;
          %attr.typ-herkunft.opt;
	quelle	CDATA	#IMPLIED
>

<!-- Nur die Begriffe selbst als Text aufnehmen -->
<!ELEMENT haupt-stw  (%inline.einfach;)* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!-- 20080425 ms v2.5 CR 189: Attribut prioritaet aufgenommen-->
<!-- 20090417 ms v2.8 CR 252: Elemente hoch / tief aufgenommen-->
<!ATTLIST haupt-stw	%attr.sprache.opt; 
			%attr.prio.opt;
>
<!-- 20090417 ms v2.8 CR 252: Elemente hoch / tief aufgenommen-->
<!ELEMENT siehe-stw  (%inline.einfach;)* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST siehe-stw	%attr.sprache.opt; 
>

<!-- 20070522 ms CR 88 v2.0 unter-unter-stw eingefuehrt, weil lt. Publishing Standards 2 Ebenen im Stichwortverzeichnis erlaubt sind -->
<!-- 20090417 ms v2.8 CR 252: Elemente hoch / tief aufgenommen-->
<!ELEMENT unter-stw  (%inline.einfach; | unter-unter-stw)* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!-- 20080425 ms v2.5 CR 189: Attribut prioritaet aufgenommen-->
<!ATTLIST unter-stw 	%attr.sprache.opt; 
			%attr.prio.opt;
>
<!-- 20090417 ms v2.8 CR 252: Elemente hoch / tief aufgenommen-->
<!ELEMENT unter-unter-stw  (%inline.einfach;)* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!-- 20080425 ms v2.5 CR 189: Attribut prioritaet aufgenommen-->
<!ATTLIST unter-unter-stw	%attr.sprache.opt; 
			%attr.prio.opt;
>

<!-- ***************************************************************** -->
<!-- Fussnoten fuer den Inline Bereich -->
<!-- ***************************************************************** -->

<!-- Die Fussnote wird dort erfasst wo sie vorkommt, Referenzierung 
     wird wegen Problemen beim Handling und Mehrfachverwendung nicht unterstuetzt -->

<!ELEMENT fn  (kennung?, ( %absatz.basis; | %elemente.zitat.kein-block; )+ )>


<!-- Angabe der Herkunft ist optional und dient der Semantikaufbewahrung -->
<!-- 20060628 HE v1.0.0 ref-id als Adresse fuer die Verlinkung -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->   
<!-- 20081029 ms v2.7 CR 217: Analog zu anmerkung-typ Attribut fn-typ-->

<!ATTLIST fn
		%attr.typ-herkunft.opt;
		%attr.ref-id.opt;
		%attr.sprache.opt; 
		%attr.fn-typ.opt;
>



<!-- ================================================================= -->
<!-- Abbildung als Inlineelement -->
<!ELEMENT abbildung   (bild, alt-text?)>

<!-- Attribute analog zu abbildung-block -->
<!ATTLIST abbildung
		%attr.abb-nr.opt;
		%attr.orientierung.opt;
>
            

<!-- Verweis auf die eigentliche Grafik -->
<!ELEMENT bild  EMPTY>                                              

<!-- Angabe der Dateireferenz in den aufgeteilten Attributen name ist verpflichtend, pfad und endung dienen der Angabe der original Infos -->
<!ATTLIST bild
		name 	CDATA 	#REQUIRED
		pfad 	CDATA 	#IMPLIED
		endung 	CDATA 	#IMPLIED
>



<!-- ================================================================= -->
<!-- Konstante direkt aus WKDSC uebernommen -->

<!-- 20060628 HE v1.0.0 in Verbindung mit zahl muss auch hier die Typisierung angegeben werden, Umsetzung Allgemein Pkt. -->

<!ELEMENT konstante     EMPTY                                         >
                   <!-- extern definierte, zeitabhaengige Konstante -->
  <!-- Anmerkung: Das Referenzjahr, fuer das der Wert der Konstante
   ermittelt wird, ergibt sich aus dem Bezugsjahr oder Prognosejahr
             (festgelegt durch das Attribut jahr) verschoben um die

                        im Attribut abstand festgelegte Jahreszahl. -->
<!ATTLIST konstante
          %attr.bezeichnung.req;
          %attr.typ-zahl.default.eur;
          jahr          (bezugsjahr |
                         prognosejahr)      "bezugsjahr"
          abstand       (-9 | -8 | -7 |
                         -6 | -5 | -4 |
                         -3 | -2 | -1 |
                          0 |
                          1 |  2 |  3 |
                          4 |  5 |  6 |
                          7 |  8 |  9)      "0"
          vergleich      (vorjahr |
                          folgejahr |
                          kein-vergleich)   "kein-vergleich"          >
                        <!-- jahr: Jahr, auf das die Ermittlung des
                                       Wertes der Konstante basiert -->
    <!-- abstand: Verschiebung gegenueber dem Basisjahr (in Jahren)
                      -9 ... -1 = Verschiebung in die Vergangenheit
                                             0 = keine Verschiebung
                              1 ... 9 = Verschiebung in die Zukunft -->
      <!-- vergleich: kennzeichnet, ob ein Vergleichswert neben dem
                aktuellen Wert der Konstante ausgegeben werden soll
                              vorjahr = Vergleich zum Vorjahreswert
                          folgejahr = Vergleich zum Folgejahreswert
               kein-vergleich = kein Vergleichswert wird ausgegeben -->



<!-- ================================================================= -->
<!-- semantische Auszeichnung besonderer Datumsangaben, die in Produkten 
     weitere Verwendung finden sollen -->

<!ELEMENT datum  (#PCDATA) >
<!ATTLIST datum
		typ (frist | steuern | versicherung | inkraft) #IMPLIED
>


<!-- ================================================================= -->
<!-- Erfassung von Zahlen, um die Pub-Standards zu realisieren -->
<!-- 20060628 HE v1.0.0, Umsetzung Allgemein Pkt. 7 -->
<!ELEMENT zahl (#PCDATA) >

<!ATTLIST zahl
		%attr.typ-zahl.default;
		bezeichner	CDATA		#IMPLIED
>


<!-- ================================================================= -->
<!-- Kennzeichnung von redaktionellen Korrekturstellen im originalen Inhalt -->
<!-- 20070919 ms v2.3 CR 155: Neues Attribut typ zu korrektur eingefuehrt-->
<!ELEMENT korrektur     (original, richtig)                           >
<!ATTLIST korrektur	typ	(fehler | bereinigung)	#IMPLIED>

<!-- Originalfassung des Textes -->
<!-- 20070531 ms v1.2.3 CR 120: variante aufgenommen-->
<!ELEMENT original      (%inline.basis; | %verweis.komplett; | variante)* >


<!-- korrigierte Fassung des Textes -->
<!-- 20070531 ms v1.2.3 CR 120: variante aufgenommen-->
<!ELEMENT richtig       (%inline.basis; | %verweis.komplett; | variante)* >

<!-- ================================================================= -->
<!-- Ablage von Textvarianten die in der DB produktspezifisch gepflegt werden -->
<!-- 20060628 HE v1.0.0 neu aufgenommen, Umsetzung Beitrag Pkt. 6 -->     
<!ELEMENT text-variante     (text-auspraegung+) >

<!-- eine Auspraegung der Textes -->
<!ELEMENT text-auspraegung       (%inline.basis; | %verweis.komplett;)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->   
<!-- 20080312 ms v2.3.5 CR 175: Attribut typ in Entity uebernommen, damit es auch in container-auspraegung verwendet werden kann-->
<!ATTLIST text-auspraegung
		%attr.typ-medien.req;
		%attr.sprache.opt; 
>


<!-- ================================================================= -->
<!-- Kennzeichnung von mehreren Textvarianten zu einer Ursprungsstelle 
     im originalen Inhalt
     wird z.B. in der ZVR fuer die Lang und Kurzform von Vorschriftenbezeichnungen benutzt -->
<!ELEMENT variante     (ursprung, auspraegung+) >
<!ATTLIST variante
		typ	(abk |
			 sprache |
			 anrede) #IMPLIED
>

<!-- 
	abk 	 = Es handelt sich um eine Akuerzungsvariante
	sprache	 = Es wird eine andere Sprachvariante hinterlegt, wobei Sprache universell ist
	anrede   = Es wird eine andere Zielgruppe angesprochen
-->


<!-- dies ist die urspruengliche Textestelle zu der die Varianten gebildet werden sollen -->
<!ELEMENT ursprung      (%inline.basis; | %verweis.komplett;)* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST ursprung	%attr.sprache.opt; 
>

<!-- eine Auspraegung des urspruenglichen Textes -->
<!ELEMENT auspraegung       (%inline.basis; | %verweis.komplett;)* >

<!-- im Typ wird die Art der Auspaegung angegeben, diese ist Fall und 
     Zweckbezogen und daher frei definierbar aber stets erforderlich
     um die Eindeutigkeit der Auspraegung zu gewaehrleisten -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST auspraegung
		typ 	CDATA 	#REQUIRED
		%attr.sprache.opt; 
>


<!-- ***************************************************************** -->
<!-- einfaches Feld zur Abbildung der normalen Formularfunktionen -->
<!-- ***************************************************************** -->

<!-- feld in inline oder Tabellen zur Nachbildung von einfachen Formularinhalten
     mit Beschriftung und einem Alternativtext fuer Online Medien -->
<!ELEMENT feld          (beschriftung?, alt-text?)      >

<!-- optische Ausgestaltung des Feldes -->
<!ATTLIST feld
		typ		(text | kasten)	#REQUIRED
		linienart	(linie |
				 punkt |
				 rahmen |
				 ohne)		#IMPLIED
		kastenart	(checkbox | radiobox)  #IMPLIED
		breite		CDATA		#IMPLIED
		blindtext	CDATA		#IMPLIED
>


<!ELEMENT beschriftung  (%inline.basis;)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST beschriftung  
		%attr.ausrichtung.opt;
		%attr.sprache.opt; 
>


<!-- ================================================================= -->
<!-- Verweisunterdrueckung -->
<!-- 20070507 ms v1.2.2 CR 100: Element vu (Verweisunterdrueckung) neu aufgenommen, um Fundstellen zu markieren, die nicht verlinkt werden sollen -->     
<!ELEMENT vu	EMPTY >

<!-- ================================================================= -->
<!-- Redaktioneller Zusatz -->
<!-- 20090417 ms v2.8 CR 247: Element red-zusatz fuer redaktionelle Zusaetze in amtlichen Texten z.B. Vorschriften -->     
<!ELEMENT red-zusatz	(%inline.einfach; | %verweis.komplett; | vu | hervor)*  >


<!-- ================================================================= -->
<!-- Fundstelle -->
<!-- 20070531 ms v2.0 CR 111: Element fundstelle neu aufgenommen, um Fundstellen zu markieren und Text, der nicht verweis-text darin auch erfassen zu koennen -->     
<!ELEMENT fundstelle	(%verweis.komplett; | zwischen-zeichen)+ >

<!-- ================================================================= -->
<!-- Sprachkennzeichnung -->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!ELEMENT sprache	(%inline.einfach.hervor-fn;)* >
<!ATTLIST sprache	%attr.sprache.req;>
