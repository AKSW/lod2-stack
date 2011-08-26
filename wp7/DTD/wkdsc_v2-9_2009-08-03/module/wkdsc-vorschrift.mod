<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC VORSCHRIFT DTD Modul						-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 03.08.2009					-->
<!-- Public Identifier: -//WKD//DTD WKDSC VORSCHRIFT MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->



<!-- ***************************************************************** -->
<!-- Vorschriften -->
<!-- ***************************************************************** -->


<!-- ================================================================= -->
<!-- Entitaetendefinition fuer den Vorschriftenzweig -->

<!-- 20060205 SK v.1.2.0 Umsetzung CR 15: zu jedem Paragraph, Artikel und  Vorschriftenobjekt soll eine Aenderungshistorie erfasst werden koennen-->
<!ENTITY % elemente.paragraph                                         "(paragraph | paragraphversion)" >

<!ENTITY % elemente.artikel                                                "(artikel | artikelversion)" >

<!ENTITY % elemente.vs-objekt                                            "(vs-objekt | vs-objektversion)" >

<!-- 20090417 ms v2.8 CR 236: Version fuer vs-anlage-ebene, vs-anlage und vs-ebene eingefuehrt-->
<!ENTITY % elemente.vs-ebene                                            "(vs-ebene | vs-ebeneversion)" >


<!-- Inhaltsmodell von Anlagen und Anlagenebenen -->

<!-- 20090417 ms v2.8 CR 236: Version fuer vs-anlage-ebene, vs-anlage und vs-ebene eingefuehrt-->
<!ENTITY % elemente.vs-anlage-ebene                                 "(vs-anlage-ebene | vs-anlage-ebeneversion)" >
<!ENTITY % elemente.vs-anlageversion	"(vs-anlage | vs-anlageversion)">

<!-- 20060705 HE v1.0.0 Umsetzung Vorschriften Pkt. 14 -->
<!-- 20071114 ms v2.3.1 CR 159: container-block zugelassen-->
<!-- 20080116 ms v2.3.4 CR 166: zwischen-titel eingefuegt-->
<!-- 20080704 ms v2.4 CR 179: paragraphversion, artikelversion, vs-objektversion eingefuegt-->
<!-- 20080915 ms v2.6 CR 169: vs-absatz in vs-anlage eingefuegt (nur fuer nummerierte Absaetze)-->
<!-- 20090417 ms v2.8 CR 236: Version fuer vs-anlage-ebene, vs-anlage und vs-ebene eingefuehrt-->
<!-- 20090803 ms v2.9 CR 263: protokollnotiz in vs-anlage eingefuegt-->
<!ENTITY % elemente.vs-anlage		"(aufhebung | (vs-absatz | %elemente.vs-anlageversion; | %elemente.vs-anlage-ebene; | %elemente.paragraph; | 
					%elemente.artikel; | %elemente.vs-objekt; | protokollnotiz | anmerkung | zitat-vs | %absatz.basis; | container-block | zwischen-titel)*)" >

<!-- Angabe der Attribute um den Versionsstand eines Vorschriftenanteils 
     zu definieren, je nach DTD Fragment kann dies und die Erforderlichkeit
     ueberschrieben werden -->
<!ENTITY % attribute.vs-stand	"
		%attr.inkraft.opt;
		%attr.ausserkraft.opt;
		%attr.fundstelle.opt;
		%attr.stand.opt;	"
>


<!-- Angabe der Zieldefinition fuer die Fundstelle von Vorschriften  -->
<!ENTITY % attribute.vs-fundstelle  "
		%attr.bezeichnung.req;
		%attr.jahr.opt;
		%attr.band.opt;
		%attr.heft.opt;
		%attr.start-seite.opt;
		%attr.fund-seite.opt;
		%attr.pos-auf-seite.opt;
		%attr.gl-nr.opt;
		%attr.beilage.opt;	"
>

<!-- ================================================================= -->
<!-- generische Ablage der Informationen zu Vorschriften in einem 
     allgemeinen Inhaltsmodell 
     * vs-metadaten beinhaltet die Systemseitig in einer DB gehaltenen 
     Informationen zur Vorschrift
     * Das Inhaltsverzeichnis in inh-vz wird generiert und steht zur Verfuegung
     um das Generieren nicht fuer jede Verwendung selbsstaendig realisieren zu muessen  -->
     
<!-- 20060704 HE v1.0.0 vs-vorspann und vz-inhalt-auto alternativ zugelassen, Umsetzung Kommentar Pkt. 13 -->
<!-- 20080116 ms v2.3.4 CR 167: protokollnotiz auch am Ende von Vorschriften, hinter Anlagen erlaubt (geklammert mit vs-anlage, damit ambigious content model vermieden wird) -->
<!-- 20080915 ms v2.6 CR 169: Am Ende von Vorschriften auch vs-objekt zugelassen fuer Musterboegen etc. -->

<!ELEMENT vorschrift	(vs-titel-kopf, vs-metadaten, 
			 ((vz-inhalt-auto, vs-vorspann?) | (vs-vorspann, vz-inhalt-auto?))?,
			 (aufhebung | 
			  ((%elemente.vs-ebene; | %elemente.paragraph; | %elemente.artikel; | %elemente.vs-objekt; | protokollnotiz)*, 
			(%elemente.vs-anlageversion;, ( protokollnotiz | %elemente.vs-objekt; )*)*)
			 ) 
			) >


<!-- das Kuerzel ist zur eindeutigen Identifizierung unbedingt erforderlich, 
     der typ zur Klassifizierung ebenso, die FNA ebenfalls wegen der 
     Eindeutigkeit ein Hauptattribut -->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!-- 20070919 ms v2.3 CR 156: Attribut auszug hinzugefuegt-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!-- 20081029 ms v2.7 CR 204: Attribut auszug von default auf optional gesetzt-->

<!ATTLIST vorschrift
		%attr.vsk.req;
		%attr.vs-typ.req;
		%attr.vs-herkunft.req;
		%attr.fna.opt;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		%attr.rechteinhaber.opt;
		%attr.sprache.opt;
		%attr.auszug.opt;
>


<!-- ================================================================= -->
<!-- Ableitung des Titelkopfes fuer Vorschriften aus dem Standardtitelkopf,
     zusaetzlich wurden die VS spezifischen Titelelemente ergaenzt -->
<!-- 20060613 HE Reihenfolge geaendert -->     
<!ELEMENT vs-titel-kopf    (titel, titel-zusatz?, vs-kurztitel?, vs-abk?,
                            toc-titel?, anmerkung*) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vs-titel-kopf	%attr.sprache.opt; 
>

<!-- Kurztitel der Vorschrift -->
<!ELEMENT vs-kurztitel		(%inline.einfach;)* >

<!-- 20060705 HE v1.0.0 Angabe der Herkunft erforderlich, Umsetzung Vorschriften Pkt. 2 -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vs-kurztitel
		%attr.typ-herkunft.req;
		%attr.sprache.opt;
>


<!-- Kuerzel der Vorschrift -->
<!ELEMENT vs-abk		(%inline.einfach;)* >

<!-- 20060705 HE v1.0.0 Angabe der Herkunft erforderlich, Umsetzung Vorschriften Pkt. 2 -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vs-abk
		%attr.typ-herkunft.req;
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- Ablage der verfuegbaren Metadaten zu einer Vorschrift -->
<!-- 20060205 v1.2.0 CR 45: Verfasser der Vorschrift wurde als optionales Element aufgenommen -->
<!-- 20070726 ms v2.1 CR 134: stichworte optional am Ende eingefuegt-->
<!ELEMENT vs-metadaten		(vs-verfasser?, vs-ursprung?, vs-fassung?, vs-aenderung?,  aenderungsregister?, %elemente.metadaten;, stichworte?) >

<!-- 
Metadaten:

* Rechtsgebiete
** Rechtsgebietkategorien zur Gliederung von Rechtsgebieten
** Rechtsgebiet: jede Vorschrift kann mehreren Rechtsgebieten zugeordnet werden

* Taxonomien: Baum
** Bildung mit Nummern und Beschriftung zur Angabe der einzelnen Taxonomieeintraege
** mehrere Eintraege sind auswaehlbar, auf jedem Weg ist nur ein Eintrag moeglich, d.h. am Ende kann jeder Eintrag ausgewaehlt werden
** der komplette Baum fuer alle gewaehlten Eintraege muss mitgegeben werden
** Thema als oberster Eintrag einer Taxonomie
** bei allen IOs zuordenbar (Beschraenkung auf Ebenen sinnvoll SLA)

* Geltungsbereiche
** raumlich und saechlich jeweils fuer Dokumentkoepfe
** jeweils optionale Liste
** DB seitig mehr als 250 Zeichen zulassen

-->

<!-- ================================================================= -->
<!-- Verfasser der Vorschrift -->
<!-- 20060205 v1.2.0 CR 45: Verfasser der Vorschrift wurde aufgenommen -->
<!ELEMENT vs-verfasser                            (vs-vertragsparteien | organisation)+>


<!-- ================================================================= -->
<!-- Ursprungsfassung der Vorschrift -->
<!ELEMENT vs-ursprung		(vs-fundstelle, anmerkung*) >

<!ATTLIST vs-ursprung
		%attr.datum.req;
		%attr.inkraft.opt;
>


<!-- ================================================================= -->
<!-- aktuell vorliegende Fassung/letzte Bekanntmachung der Vorschrift -->
<!ELEMENT vs-fassung		(vs-fundstelle, anmerkung*) >

<!ATTLIST vs-fassung
		%attr.datum.req;
		%attr.inkraft.opt;
>


<!-- ================================================================= -->
<!-- die letzte Aenderung der Vorschrift -->
<!ELEMENT vs-aenderung		(vs-fundstelle, anmerkung*) >

<!-- 20070919 ms v2.3 CR 154: Attribut inkraft entfernt-->
<!ATTLIST vs-aenderung
		%attr.datum.req;
>


<!-- ================================================================= -->
<!-- Angabe der Fundstelle fuer Verkuendungen und Aenderungen von Vorschriften 
     im Inhalt wird als Fliesstext die textuelle Repraesentation angegeben, 
     in den Attributen die Adresse dazu -->
<!-- 20081029 ms v2.7 CR 220: fn zugelassen-->

<!ELEMENT vs-fundstelle		(#PCDATA | fn)* >

<!ATTLIST vs-fundstelle
		%attribute.vs-fundstelle;
>


<!-- ================================================================= -->
<!-- Aenderungsregister wie sie aus der VerwaltungsDB herausgerechnet werden -->

<!ELEMENT aenderungsregister		(register-eintrag+) >


<!-- ein Eintrag im Aenderungsregister mit Bezug auf eine Aenderungsvorschrift 
     und Aenderungsstellen -->
<!ELEMENT register-eintrag		(aenderungs-vorschrift, aenderungs-stelle*) >


<!-- Angaben zu einer Aenderungsvorschrift entsprechen den Vorschriftenangaben -->
<!-- 20070830 ms v2.2 CR 150: Element fundstellen durch vs-fundstelle ersetzt-->
<!ELEMENT aenderungs-vorschrift		(vs-titel-kopf, vs-fundstelle?)>

<!ATTLIST aenderungs-vorschrift
		%attr.datum.opt;
>

                                    
                                    
<!-- Stelle in der Aenderungsvorschrift mit Angaben zur Fundstellen und 
     deren jeweiligen zu Aenderungen -->
<!-- 20070830 ms v2.2 CR 150: Element fundstellen durch vs-fundstelle ersetzt-->
<!ELEMENT aenderungs-stelle      (vs-fundstelle?, anmerkung?, aenderungs-vermerk*) >

<!-- Details zu einer einzelnen Aenderung die sich auf mehrere Details 
     der Vorschrift beziehen kann -->
<!ELEMENT aenderungs-vermerk     (verweis-vs*, aenderungs-anweisung) >

<!ATTLIST aenderungs-vermerk
          %attr.inkraft.opt;
          typ			(eingefuegt | geaendert | entfallen | unbekannt)	#REQUIRED
>

<!-- die Aenderungsanweisung als Text -->          
<!ELEMENT aenderungs-anweisung   (%inline.einfach;)*                    >


<!-- ================================================================= -->
<!-- Vorspann zur Aufnahme vorgelagerter Informationen der Vorschriften -->
<!-- 20060719 HE v1.0.0 Umsetzung Vorschriften Pkt 5 vs-vertragsparteien hinzugefuegt -->
<!ELEMENT vs-vorspann		(vs-vertragsparteien | zwischen-titel | %absatz.basis; | anmerkung)+ >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vs-vorspann	%attr.sprache.opt; 
>

<!ELEMENT vs-vertragsparteien		(%absatz.basis; | anmerkung)+ >
                          
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vs-vertragsparteien	%attr.sprache.opt; 
>

<!-- ================================================================= -->
<!-- Ebene innerhalb von Vorschriften -->
<!-- 20060717 HE v1.0.0 zitat-vs Umsetzung Vorschriften Pkt. 24 -->
<!-- 20060131 v1.2.0 CR 48:  'kennung?, (titel | ohne-titel)' wurde durch Element titel-kopf ersetzt -->
<!-- 20060205 v1.2.0 CR 51: Element 'aufhebung' wird optional -->
<!-- 20070402 ms v1.2.1 CR 72: Element anmerkung hinzugefuegt-->
<!-- 20071120 ms v2.3.3 CR 162: vs-anlage am Ende von vs-ebene aufgenommen-->
<!-- 20080407 ms v2.4 CR 180: Metadaten, Rechtsgebiete, Taxonomien und stichworte zugeordnet-->
<!ELEMENT vs-ebene		(titel-kopf, %elemente.metadaten;, stichworte?, vorab-inhalt?,
				 (aufhebung? | ((%elemente.vs-ebene; | %elemente.paragraph; | %elemente.artikel; | %elemente.vs-objekt; | 
				zitat-vs | protokollnotiz | anmerkung)*, (%elemente.vs-anlageversion;)*)))
> 

<!-- NOTE: ZVR in den wenigsten Faellen sollen die kennung und der titel in einer Zeile stehen -->
<!-- 20070731 ms v2.1 CR 140: Attribute bez und wert hinzugefuegt-->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vs-ebene
		%attr.vs-ebene.req;
		%attribute.vs-stand;
		%attr.vtext-id.opt;
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.sprache.opt;
>

<!-- 20090417 ms v2.8 CR 236: Version fuer vs-anlage-ebene, vs-anlage und vs-ebene eingefuehrt-->
<!ELEMENT vs-ebeneversion             (aenderung, vs-ebene*)>

<!-- ================================================================= -->
<!-- vorab-inhalt umfasst Inhalte die bei Vorschriftenelementen vorab gestellt sind 
     das Inhaltsmodell sind Zwischenueberschriten und Absatzelemente -->
<!ELEMENT vorab-inhalt		( zwischen-titel | %absatz.basis; | anmerkung)+ >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vorab-inhalt	%attr.sprache.opt; 
>
<!-- ================================================================= -->
<!-- die Anlage einer Vorschrift mit den Standardbestandteilen der DTD -->
<!-- 20080407 ms v2.4 CR 180: Metadaten, Rechtsgebiete, Taxonomien und stichworte zugeordnet-->
<!-- 20090417 ms v2.8 CR 240: Platzhalter fuer Inhaltsverzeichnis vz-inhalt-auto eingefuegt-->
<!ELEMENT vs-anlage		(titel-kopf, %elemente.metadaten;, stichworte?, vz-inhalt-auto?, 
				 vorab-inhalt?,
				 %elemente.vs-anlage;
				)
>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!-- 20081029 ms v2.7 CR 204: Default-Wert "anlage" von Attribut typ entfernt und auf REQUIRED gesetzt-->

<!ATTLIST vs-anlage
		typ	(anlage | anhang) #REQUIRED
		%attr.anlage-nr.opt;
		%attribute.vs-stand;
		%attr.vtext-id.opt;
		%attr.sprache.opt;
>

<!-- 20090417 ms v2.8 CR 236: Version fuer vs-anlage-ebene, vs-anlage und vs-ebene eingefuehrt-->
<!ELEMENT vs-anlageversion             (aenderung, vs-anlage*)>

<!-- ================================================================= -->
<!-- Ebene innerhalb einer Anlage -->
<!-- 20070731 ms v2.1 CR 140: Attribute bez und wert hinzugefuegt-->
<!-- 20080407 ms v2.7 CR 209: Metadaten, Rechtsgebiete, Taxonomien und stichworte zugeordnet-->
<!-- 20090417 ms v2.8 CR 240: Platzhalter fuer Inhaltsverzeichnis vz-inhalt-auto eingefuegt-->

<!ELEMENT vs-anlage-ebene	(titel-kopf, %elemente.metadaten;, stichworte?, vz-inhalt-auto?,
				 vorab-inhalt?,
				 %elemente.vs-anlage;
				)
>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vs-anlage-ebene
		%attr.anlage-nr.opt;
		%attribute.vs-stand;
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.sprache.opt;
>

<!-- 20090417 ms v2.8 CR 236: Version fuer vs-anlage-ebene, vs-anlage und vs-ebene eingefuehrt-->
<!ELEMENT vs-anlage-ebeneversion             (aenderung, vs-anlage-ebene*)>
<!-- ================================================================= -->
<!-- Allgemeines Inhaltsobjekt zur Aufnahme beliebigen Inhaltes von 
     Vorschriften, die sich nicht als Artikel oder Paragraph klassifizieren lassen -->
<!-- 20060607 HE@SL absatz.basis statt absatz.einfach um Listen und Tabellen zu erhalten -->
<!-- 20060704 HE v1.0.0 
		* rekursion von vs-objekt, Umsetzung Kommentar Pkt. 16
		* vs-absatz eingefuegt, Umsetzung Vorschriften Pkt. 11
		* vs-container-block mit aufgenommen
		* titel-zusatz aufgenommen, Umsetzung Vorschriften Pkt. 25
		* zitat-vs Umsetzung Vorschriften Pkt. 24
-->
<!-- 20060131 v1.2.0 CR 48 :  'kennung?, (titel | ohne-titel)' wurde durch Element titel-kopf ersetzt -->
<!-- 20070131 v1.2.0 CR 53: vs-container-block durch Element container-block ersetzt  -->
<!-- 20060205 v1.2.0 CR 51: Element 'aufhebung' wird optional -->
<!-- 20070516 ms v1.2.3 CR 109: vs-container-block in vs-objekt einfuegen, stattdessen container-block in vs-objekt wieder entfernen (CR 53 wieder rueckgaengig gemacht) -->
<!-- 20080307 ms v2.3.5 CR 173: vs-anlage am Ende zugelassen-->
<!-- 20080407 ms v2.4 CR 180: Metadaten, Rechtsgebiete, Taxonomien und stichworte zugeordnet-->
<!-- 20080425 ms 2.5 CR 178: Artikel und Paragraph zugelassen, vs-objekt innerhalb von vs-objekt nicht mehr nur am Ende zugelassen, sondern auch nach aufhebung und auf gleicher Ebene wie die anderen Elemente vs-absatz etc.  -->

<!ELEMENT vs-objekt	(titel-kopf, titel-zusatz?, %elemente.metadaten;, stichworte?,
			(
			(aufhebung, (vs-absatz | vs-container-block | anmerkung | zitat-vs | protokollnotiz | artikel | paragraph | %elemente.vs-objekt;)*)?
			| 
			(vs-absatz | vs-container-block | anmerkung | zitat-vs | protokollnotiz | artikel | paragraph | %elemente.vs-objekt;)+
			), (%elemente.vs-anlageversion;)*
			)> 


<!-- 20060705 HE v1.0.0 Attribut typ hinzugefuegt, Umsetzung Vorschriften Pkt. 8, 17 -->
<!-- 20070402 ms v1.2.1 CR 73: Attributwert info zu typ hinzugefuegt -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!-- 20080116 ms v2.3.4 CR 165: Attributwert muster zu typ hinzugfuegt-->
<!-- VORSICHT: Wenn hier ein typ ergaenzt wird, muss auch das darauf verweisende Attribut vs-obj-typ in verweis-vs entsprechend ergaenzt werden-->
<!ATTLIST vs-objekt
		%attr.vsobj-typ.req;
		%attr.nr.opt;
		%attribute.vs-stand;
		%attr.vtext-id.opt;
		%attr.sprache.opt;
>

<!-- Element zur Erfassung von Aenderungshistorie (Versionen) der Vorschriftenobjekten -->
<!-- 20060205 SK v1.2.0 Umsetzung von CR 15 --> 
<!ELEMENT vs-objektversion             (aenderung, vs-objekt*)>


<!-- ================================================================= -->
<!-- Protokollnotiz -->
<!-- 20060705 HE v1.0.0, Umsetzung Vorschrift Pkt. 9 -->
<!-- 20060205 ms v1.2.0 CR 51: Element 'aufhebung' wird optional -->
<!-- 20080116 ms v2.3.4 CR 166: zwischen-titel eingefuegt-->
<!-- 20081029 ms v2.7 CR 216: vs-container-block in protokollnotiz erlaubt-->

<!ELEMENT protokollnotiz	(kennung?, (titel | ohne-titel), 
			 	 (aufhebung? | (%absatz.basis; | anmerkung | zwischen-titel | vs-container-block)+ )
			 	)
> 

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST protokollnotiz
		%attr.prot-nr.opt;
		%attribute.vs-stand;
		%attr.sprache.opt;
>



<!-- ================================================================= -->
<!-- in der ZVR werden keine TOC Titel gepflegt, da die Anforderungen hier drann produktspezifisch ausfallen wuerden -->
<!-- 20060131 v.1.2.0 CR 48:  'kennung?, (titel | ohne-titel)' wurde durch Element titel-kopf ersetzt -->
<!-- 20060205 v1.2.0 CR 51: Element 'aufhebung' wird optional -->
<!-- 20080307 ms v2.3.5 CR 173: Aufgehobene Paragraphen u.a. in aufgehobenen Artikeln, sollen nach der aufhebung ebenfalls erfassbar sein, vs-anlage zugelassen-->
<!-- 20080407 ms v2.4 CR 180: Metadaten, Rechtsgebiete, Taxonomien und stichworte zugeordnet-->
<!-- 20080915 ms v2.6 CR 174: neues Element artikel-ebene eingefuegt -->

<!ELEMENT artikel		(titel-kopf, %elemente.metadaten;, stichworte?,
			(
			(aufhebung, (vs-absatz | vs-container-block | protokollnotiz | %elemente.paragraph;)*)? 
			| 
			((artikel-ebene | vs-absatz | vs-container-block | anmerkung | protokollnotiz | %elemente.paragraph;)+ , 
			(%elemente.vs-anlageversion;)*))
			) > 

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST artikel
		%attr.art.req;
		%attribute.vs-stand;
		%attr.vtext-id.opt;
		%attr.sprache.opt;
>

<!-- Element zur Erfassung von Aenderungshistorie (Versionen) der Vorschriftenartikeln -->
<!-- 20060205 SK v1.2.0 Umsetzung von CR 15 --> 
<!ELEMENT artikelversion             (aenderung, artikel*)>

<!-- Element fuer hierarchische Ebenen in Artikeln-->
<!-- 20090626 ms v2.8.2 CR 260: Inhalt von artikel-ebene optional gemacht-->
<!ELEMENT artikel-ebene	(titel-kopf, (aufhebung? | (artikel-ebene | vs-absatz | anmerkung | %elemente.paragraph; | vs-objekt)*)) > 

<!ATTLIST artikel-ebene
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- 20060131 v1.2.0 CR 48 :  'kennung?, (titel | ohne-titel)' wurde durch Element titel-kopf ersetzt -->
<!-- 20060205 v1.2.0 CR 51: Element 'aufhebung' wird optional -->
<!-- 20060222 v1.2.0 Aufhebung ist jetzt erforderliches Element -->
<!-- 20070510 v2.0 ms CR 83: Element aufhebung wird wieder optional, weil es auch Paragraphen gibt, die nur aus einem titel-kopf bestehen -->
<!-- 20080307 ms v2.3.5 CR 173: vs-anlage am Ende zugelassen-->
<!-- 20080407 ms v2.4 CR 180: Metadaten, Rechtsgebiete, Taxonomien und stichworte zugeordnet-->
<!ELEMENT paragraph	(titel-kopf, %elemente.metadaten;, stichworte?,
			 (aufhebung? | 
			((vs-absatz | vs-container-block | anmerkung | protokollnotiz)+ ,(%elemente.vs-anlageversion;)*))
)> 

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST paragraph
		%attr.par.req;
		%attribute.vs-stand;
		%attr.vtext-id.opt;
		%attr.sprache.opt;
>

<!-- Element zur Erfassung von Aenderungshistorie (Versionen) der Vorschriftenparagraphen -->
<!-- 20060205 SK v1.2.0 Umsetzung von CR 15 --> 
<!ELEMENT paragraphversion             (aenderung, paragraph*)>

<!-- ================================================================= -->
<!-- semantische Auszeichnung der Aufhebung eines Vorschriftenbestandteils -->
<!ELEMENT aufhebung		(absatz | anmerkung)* >

<!-- 20060205 v1.2.0 CR 51:  Bereichsmarkierung der aufgehobenen Textstellen -->
<!ATTLIST aufhebung
                          von CDATA #IMPLIED
                           bis CDATA #IMPLIED
 >



<!-- ================================================================= -->
<!-- der semantische Absatz einer Vorschrift -->
<!-- die Angabe der Nummer als Attribut entspricht der Zieldefinition 
     fuer die Absatzverlinking -->
<!ELEMENT vs-absatz		
			(abs-nr?, (aufhebung | (%absatz.basis; | container-block | anmerkung)*)) > 

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vs-absatz
		%attr.nr.req;
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- Teil innerhalb eines Vorschriftenelementes zur weiteren inneren Gliederung  -->
<!-- 20070829 ms v2.2 CR 147: paragraph in vs-container-block zugelassen (fuer Artikelgesetze)-->
<!-- 20080704 ms v2.4 CR 179: paragraphversion eingefuegt-->
<!ELEMENT vs-container-block
				(kennung?, titel?, (aufhebung | (vs-absatz | anmerkung | %elemente.paragraph;)+)) > 
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST vs-container-block
		%attr.nr.opt;
		%attr.sprache.opt;
>


<!-- Absatznummer als eigenestaendiges Element -->
<!ELEMENT abs-nr		(%kennung.basis;)* >


<!-- ================================================================= -->
<!-- 20060205 SK v.1.2.0 Umsetzung CR 15: Aenderungshistorie als Element und Unterelemente -->

<!ELEMENT aenderung            (ae-info)* >

<!ELEMENT ae-info                    (#PCDATA)>

