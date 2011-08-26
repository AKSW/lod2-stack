<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC GEMEINSAM DTD Modul						-->
<!-- 									-->
<!--           WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 03.08.2009				-->
<!-- Public Identifier: -//WKD//DTD WKDSC GEMEINSAM MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->


<!-- ******************************************************************	-->
<!-- Gemeinsam benoetigte Zuordnungen zu Zeitschriften, Entscheidungssammlungen, Kommentierungen, Beitraegen -->
<!-- ******************************************************************	-->

<!-- 20070112 he v1.2.0 CR 35: verweis-zs statt der Attribute fuer die Referenz auf die Zeitschrift -->
<!-- <!ELEMENT zuordnung-zs (verweis-zs, zuordnung-rubrik?) > -->
<!-- Wird ersetzt durch zuordnung-produkt-->

<!-- Wird ein Aufsatz einer Rubrik zugeordnet, kann dies in rekursiver Form 
     hierueber abgebildet werden. -->
<!-- 20070112 he v1.2.0 CR 35: bez und wert sorgen fuer die Referenzierung 
    auf die verschiedenen Ebenen -->
<!ELEMENT zuordnung-rubrik (zuordnung-rubrik?) >
<!ATTLIST zuordnung-rubrik
		%attr.bez.req;
		%attr.wert.req;
>

<!-- Einordnung der Entscheidung in die Gliederung der Entscheidungssammlung so als wenn es der Verweis selbst ist -->
<!-- 20070402 ms v1.2.1 CR 80: verweis-esa wird mehrfach zugelassen, damit ein esa-eintrag mehreren Vorschriften zugeordnet werden kann-->
<!--  <!ELEMENT zuordnung-esa		( (verweis-esa+ | verweis-zs | verweis-url), zuordnung-rubrik?) >  -->
<!-- Wird ersetzt durch zuordnung-produkt-->

<!-- 20070510 ms v2.0 CR 84: neues Element zuordnung-fortsetzung fuer die Zuordnung eines aufsatzes zu dessen fortsetzungen in anderen Zeitschriftenausgaben-->
<!ELEMENT zuordnung-fortsetzung (verweis-zs)+ >

<!ELEMENT zuordnung-produkt 	( ((verweis-esa | verweis-zs | verweis-komhbe | verweis-url | verweis-vtext-id | ep-produkt)+, zuordnung-rubrik?) | themen-bezug) >
<!ELEMENT themen-bezug		(#PCDATA)>

<!-- 20070606 ms v2.0 CR 111: neues Element ep-produkt fuer die zuordnung zu einem elektronischen Produkt wie z.B. einem JURION Modul oder Newsletter-->
<!ELEMENT ep-produkt		EMPTY>
<!ATTLIST ep-produkt
		%attr.produkt.req;
		newsletter	CDATA	#IMPLIED
		datum-gueltig-von	CDATA	#IMPLIED
		datum-gueltig-bis	CDATA   #IMPLIED
>

<!-- ******************************************************************	-->
<!-- Gemeinsam benoetigte Verweise auf verbundene Dokumente -->
<!-- ******************************************************************	-->
<!-- 20070726 ms v2.1 CR 129: verweis-unbekannt eingefuegt-->
<!ELEMENT verbundene-dokumente (%verweis.komplett; | verweis-unbekannt)+>
<!ATTLIST verbundene-dokumente 	typ 	(pressemitteilung | nachricht | amtlichesdokument | analyse | schlussantrag | rezensiertes-dok | weblinks | gesetzgebungsvorschau | unbekannt) 	#REQUIRED
				%attr.prio.opt;>

<!-- ***************************************************************** -->
<!-- allgemeines Titelmodell fuer Blockelemente -->
<!-- ***************************************************************** -->

<!-- Allgemeines Titelmodell mit allen fuer die Produktion
     erforderlichen Informationen -->
<!-- 20070829 ms v2.2 CR 111: kurztitel eingefuegt-->

<!ELEMENT titel-kopf    (kennung?, 
			 titel-bezug?,
			 (titel|ohne-titel),
			 titel-zusatz?,
			 kurztitel?, 
			 toc-titel?,
			 titel-trefferliste?,
			 anmerkung*)
>

<!-- Angabe ob der Titelkopf im Verzeichnis aufscheinen sollte oder nicht 
     Angabe, ob die Kennung automatisch generiert werden soll oder aus dem 
     gleichnamigen Element zu nehmen ist -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST titel-kopf
		im-vz		(ja | nein)		"ja" 
		kennung		(manuell | auto)	#IMPLIED
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- Kennzeichnung eines Umbruchs in titel -->
<!-- 20060705 HE v1.0.0 Umsetzung Allgemeines Pkt. 49 -->
<!ELEMENT titel-umbruch         EMPTY >

<!-- 20081029 ms v2.7 CR 204: Defaultwert "nein" zu Attribut leerzeichen-ersetzung entfernt und auf optional gesetzt-->
<!ATTLIST titel-umbruch
		leerzeichen-ersetzung		(ja | nein) 	#IMPLIED
>


<!-- ================================================================= -->
<!-- ein normaler Titel -->
<!ELEMENT titel         (%titel.komplett;)* >

<!-- Angabe des Typen des Titels fuer semantische Hinterlegung -->
<!-- 20070522 ms v2.0 CR 112: Attribut spitzmarke zu titel-->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!-- 20081029 ms v2.7 CR 204: Defaultwert "nein" zu Attribut spitzmarke entfernt und auf optional gesetzt-->

<!ATTLIST titel
		%attr.typ-herkunft.opt; 
		spitzmarke (ja | nein) #IMPLIED
		%attr.sprache.opt;
>
          

<!-- explizite Angabe ueber dieses Element das es keinen Titel gibt -->
<!ELEMENT ohne-titel       EMPTY>

<!-- 20070829 ms v2.2 CR 111: kurztitel eingefuegt-->
<!ELEMENT kurztitel 	(%inline.einfach;)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST kurztitel
		%attr.typ-herkunft.opt;
		%attr.sprache.opt;
>

<!ELEMENT titel-bezug         (%titel.komplett;)* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST titel-bezug
		%attr.sprache.opt;
>

<!-- 20070727 ms v2.1 CR 136: Neues Element titel-rn, in dem rn zugelassen ist, damit Randnummern zum Titel in Entscheidungen erfasst werden koennen-->
<!ELEMENT titel-rn	(%titel.komplett; | rn)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST titel-rn
		%attr.typ-herkunft.opt; 
		spitzmarke (ja | nein)  "nein" 
		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- ueber z-umbruch wird das Umbrechen des nachfolgenden Textes angegeben -->
<!ELEMENT kennung         (%kennung.basis;)* >

<!-- 20060705 HE v1.0.0 Umsetzung Vorschriften Pkt. 10 -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST kennung
		%attr.typ-herkunft.opt;
		z-umbruch	(ja | nein)	#IMPLIED
		%attr.sprache.opt;
>

<!-- Untertitel bzw. ein Zusatz -->
<!ELEMENT titel-zusatz  (%titel.komplett;)*                    >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST titel-zusatz	%attr.sprache.opt; 
>

<!-- alternative Angabe fuer den TOC Eintrag in E-Produkten -->
<!ELEMENT toc-titel     (#PCDATA)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST toc-titel		%attr.sprache.opt; 
>


<!-- XXX in Typisierung in werkssteuerung auslagern um es erweiterbar zu gestalten -->

<!-- Titel fuer die Trefferliste mit Angabe des Typen der aus den Elementen gemappt werden muss -->
<!ELEMENT titel-trefferliste  (%titel.einfach; | hervor)*                    >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST titel-trefferliste
		dokument-typ		(vorschrift | entscheidung | kommentar | handbuch | beitrag) 	#REQUIRED
		%attr.sprache.opt;
>



<!-- ***************************************************************** -->
<!-- Titelmodell fuer Zwischenueberschriften  -->
<!-- ***************************************************************** -->

<!-- Zwischentitel kommen in nicht strukturbildenden Konstellationen vor 
     und bedienen sich der Standardelemente aus titel-block
     der Titel ist bei einem Zwischentitel immer erforderlich -->
<!-- 20090417 ms v2.8 CR 251: ohne-titel alternativ zu titel vorgesehen -->
<!ELEMENT zwischen-titel	(kennung?, (titel|ohne-titel), titel-bezug?, titel-zusatz?) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST zwischen-titel	%attr.sprache.opt; 
>



<!-- ***************************************************************** -->
<!-- Das Abstract umfasst einen einfachen Titel, Zwischenueberschriften 
     und Absatzelemente fuer einen Kurzabriss zum Dokument bzw. zu Ebenen davon -->
<!-- ***************************************************************** -->
<!-- 20070604 ms v2.0 CR 111: abstract erhaelt Attribut bezeichnung, falls mehrere abstracts von unterschiedlichen Quellen und mit unterschiedlicher Ausrichtung vorhanden sind-->
<!-- 20070726 ms v2.1 CR 126: autor optional zu abstract eingefuegt, damit hier auch z.B. ein Autorenkuerzel zugeteilt werden kann-->
<!ELEMENT abstract	(kennung?, titel?, 
			 (zwischen-titel | %absatz.basis; | anmerkung)+, 
			autor?
			) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST abstract	bezeichnung 	CDATA	#IMPLIED
			%attr.sprache.opt;
>

<!-- ***************************************************************** -->
<!-- Ein Teaser bekommt optional eine Kennung und einen Titel. Der Inhalt 
     ist ein einfaches Absatzmodell zur Abbildung der zu teasernden Informationen. -->
<!-- ***************************************************************** -->

<!-- 20070126 he v1.2.0 CR 67: teaser als allgemeines Element hinzugefuegt -->
<!ELEMENT teaser		(kennung?, titel?,
				 (%absatz.basis; | anmerkung)+
				) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     	
<!ATTLIST teaser	%attr.sprache.opt; 
>

<!-- ***************************************************************** -->
<!-- Vordefinition der Metadaten fuer die freie Verwendung auf IO und 
     Sub-IO Ebene in den einzelnen Zweigen -->
<!-- ***************************************************************** -->




<!-- ***************************************************************** -->
<!-- feste WKD Metadaten -->
<!-- ***************************************************************** -->

<!ELEMENT rechtsgebiete			(rechtsgebiet-eintrag)+ >
<!-- 20070510 ms v2.0 CR 91: Attribut ref-id ersetzt durch produkt und typ -->
<!ELEMENT rechtsgebiet-eintrag		(#PCDATA)* >
<!ATTLIST rechtsgebiet-eintrag
		%attr.produkt.opt;
		typ	(haupt | neben)	#IMPLIED
>


<!-- ================================================================= -->
<!ELEMENT taxonomien			(taxonomie-eintrag)+ >
<!-- 20080116 ms v2.3.4 CR 164: Element taxonomien die Attribute produkt und quelle zugeordnet-->
<!ATTLIST taxonomien
		%attr.produkt.opt;
		quelle	CDATA	#IMPLIED
>

<!-- 20060926 he v1.1.0 CR 4: fehlende Elementdefinition hinzugefuegt  -->
<!-- 20080116 ms v2.3.4 CR 164: Attribut refid optional gemacht-->
<!ELEMENT taxonomie-eintrag		(#PCDATA) >
<!ATTLIST taxonomie-eintrag
		%attr.ref-id.opt;
>


<!-- ***************************************************************** -->
<!-- frei definierbare Metadatenablage -->
<!-- ***************************************************************** -->

<!-- frei definierbare, strukturierbare Metadaten-Sammlung 
     besteht aus einzelnen Metadateneintraegen oder Gruppen von Metadaten -->
<!ELEMENT metadaten          (metadaten-text | metadaten-gruppe)* >


<!-- Gruppe von Metadaten die ueber den Bezeichner eindeutig gestellt wird -->
<!ELEMENT metadaten-gruppe   (metadaten-text | metadaten-gruppe)*                         >

<!ATTLIST metadaten-gruppe
		%attr.bezeichnung.req;
>


<!ELEMENT metadaten-text          (#PCDATA)        >

<!-- ueber den Bezeichner wird die Angabe des Metadatums eindeutig festgelegt -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST metadaten-text
		%attr.bezeichnung.req;
		typ	(zeichenkette | nummer | zahl | datum | liste )	"zeichenkette"
		%attr.sprache.opt; 
>

<!--
	zeichenkette	= Beliebige alphanumerische Zeichen zur Abbildung von textuellen Inhalten
	nummer		= ganzzahlige Nummer
	zahl		= Zahl aus dem reellen Zahlenbereich
	datum		= Datumsangabe, Format sollte ISO formatiert sein YYYY-MM-DD
	liste		= Liste von einzelenen durch ein Trennzeichen aufgefuehrten Begriffen
-->


<!-- ***************************************************************** -->
<!-- Abbildung lexikalischer Eintraege in einem Kommentar -->
<!-- ***************************************************************** -->


<!-- 20070507 ms v1.2.2 CR 105: lexikon-eintrag-text optional gemacht, wegen Lexikon-Eintraegen, die keinen direkten Text enthalten, sondern weiter unterteilt sind -->
<!-- 20070919 ms v2.3 CR 152: autor hinter lexikon-eintrag-text zugelassen-->
<!-- 20081029 ms v2.7 CR 224: Neues Attribut begriff für normierten Begriff (um darauf zu verweisen)-->
<!ELEMENT lexikon-eintrag	(lexikon-begriff, lexikon-eintrag-text?, autor?, lexikon-eintrag*) > 
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST lexikon-eintrag	%attr.sprache.opt; 
			begriff-normiert  CDATA	#IMPLIED
>

<!ELEMENT lexikon-begriff		(%titel.einfach;)* >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST lexikon-begriff	%attr.sprache.opt; 
>
<!-- separate Auszeichnung des Textes zum Begriff -->
<!-- 20070112 he v1.2.0 CR 31: zitat-vs hinzugefuegt und statt absatz.basis wird absatz.komplett verwendet -->
<!-- 20070919 ms v2.3 CR 151: Literaturverzeichnis vz-literatur-manuell hinzugefuegt-->
<!ELEMENT lexikon-eintrag-text		(zwischen-titel | %absatz.komplett; | anmerkung | zitat-vs | vz-literatur-manuell)+ >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST lexikon-eintrag-text	%attr.sprache.opt; 
>



<!-- ***************************************************************** -->
<!-- Glossar zur Verwaltung von Definitionen -->
<!-- ***************************************************************** -->

<!ELEMENT glossar		(titel-kopf?, glossar-eintrag+) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST glossar	%attr.sprache.opt; 
>

<!ELEMENT glossar-eintrag	(term, definition) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST glossar-eintrag	%attr.sprache.opt; 
>

<!ELEMENT term			(%inline.basis; | %verweis.komplett;)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST term	%attr.sprache.opt; 
>

<!ELEMENT definition		(%inline.basis; | %verweis.komplett;)* >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST definition	%attr.sprache.opt; 
>
<!-- ***************************************************************** -->
<!-- Personen und Organisationsbezogene Daten -->
<!-- ***************************************************************** -->

<!-- Generelle Daten zu Personen und Organisationen zur Abbildung von 
     Informationen die in entsprechenden Produkten Verwendung finden sollen -->


<!-- 20060712 HE v1.0.0 verweis-url fuer die Aufnahme von E-Mail oder Websiteinfomationen hinzugefuegt -->
<!-- 20060926 he v1.1.0 CR 7: Element person-rolle zur Aufnahme aller Angaben einer Taetigkeit neu definiert -->
<!-- 20070126 he v1.2.0 CR 61: Attribut ref-id zur Moeglichkeit der Ablage einer Datenbank Referenz-ID hinzugefuegt -->
<!-- 20090803 ms v2.9 CR 265: fn alternativ zu anmerkung zugelassen -->
<!ELEMENT person		(name, person-rolle*, (anmerkung | fn)*) >

<!ATTLIST person
		%attr.ref-id.opt;
>

<!-- ================================================================ --> 
<!-- 20070726 ms v2.1 CR 126: Zusaetzlich neues Element pers-kuerzel eingefuegt -->
<!-- 20080407 ms v2.4 CR 182: titel-zu-name mehrfach zugelassen plus Attribut position -->
<!ELEMENT name			(titel-zu-name*, vorname?, nachname?, pers-kuerzel*) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST name	%attr.sprache.opt; 
>

<!ELEMENT titel-zu-name		(#PCDATA) >
<!ATTLIST titel-zu-name 		position	(vorangestellt | nachgestellt)  "vorangestellt" >
<!ELEMENT vorname		(#PCDATA) >
<!ELEMENT nachname		(#PCDATA) >

<!ELEMENT pers-kuerzel		(#PCDATA) >
<!ATTLIST pers-kuerzel		%attr.produkt.opt;>

<!-- ================================================================ --> 
<!-- 20060926 he v1.1.0 CR 7: Element person-rolle zur Aufnahme aller Angaben einer Taetigkeit neu definiert -->
<!-- eine Rolle nimmt all diejenigen Informationen auf die eine Person 
     zur Wahrnehmung einer Taetigkeit, z.B. Beruf oder einer anderen 
     Taetigkeit beschreiben
     Die Bezeichnung gibt die Beschreibung der Rolle an -->
<!ELEMENT person-rolle		(person-beruf?, person-ort?, person-funktion?, zu-organisation?, verweis-url*) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST person-rolle
		%attr.bezeichnung.req;
		%attr.sprache.opt;
>

<!ELEMENT person-beruf		(#PCDATA) >

<!ELEMENT person-ort		(#PCDATA) >

<!ELEMENT person-funktion	(#PCDATA) >

<!ELEMENT zu-organisation	(#PCDATA)>

<!-- 20081029 ms v2.7 CR 206: Neues Element org-rolle analog zu person-rolle-->
<!ELEMENT organisation		(org-bezeichnung, org-kuerzel?, org-rolle*) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST organisation	%attr.sprache.opt; 
>
<!-- 20070516 ms v1.2.3 CR 108: korrektur in org-bezeichnung und org-kuerzel eingefuegt-->
<!-- 20081029 ms v2.7 CR 219: variante in org-bezeichnung eingefuegt-->
<!ELEMENT org-bezeichnung	(#PCDATA | korrektur | variante)*>
<!ELEMENT org-kuerzel		(#PCDATA | korrektur)*>

<!ELEMENT org-rolle		EMPTY>
<!ATTLIST org-rolle
		%attr.bezeichnung.req;
		%attr.sprache.opt;
>
<!-- ***************************************************************** -->
<!-- Im Container autor werden alle Autoren des Aufsatzes mit dem 
     Inhaltsmodell der person aufgefuehrt -->

<!-- 20081029 ms v2.7 CR 206: organisation zusaetzlich zu person aufgenommen-->

<!ELEMENT autor			(person | organisation)+ >

<!ATTLIST autor
		%attr.ref-id.opt;
>


<!-- Bearbeiter ist die bearbeitende Person eines Dokumentes bzw. eines Teiles davon  -->
<!ELEMENT bearbeiter		(person)+ >

<!-- ================================================================= -->
<!-- Anlage  -->

<!-- 20060621 HE Erweiterung fuer PUBLIC Kommentare -->
<!-- 20060704 HE v1.0.0 Umsetzung Kommentar Pkt. 17 -->
<!ENTITY % elemente.anlage	"(anlage-ebene | zwischen-titel | anmerkung | %absatz.komplett; | %elemente.zitat.kein-block; | glossar)*" >

<!ELEMENT anlage		(titel-kopf, 
				 werk-steuerung?, 
				 (%elemente.verzeichnis.inhalte-auto;)*, %elemente.anlage;) >

<!-- fuer die Referenzierung bei Verweisen kann der Anlage eine Nummer zugewiesen werden -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->  
<!ATTLIST anlage
		%attr.anlage-nr.opt;
		%attr.vtext-id.opt;
		%attr.sprache.opt; 
>


<!-- ================================================================= -->
<!-- rekursive Ebene fuer die Anlagenstrukturierung mit Titelkopf und 
     den bekannten kompletten Absatzelementen -->
<!ELEMENT anlage-ebene		(titel-kopf, %elemente.anlage;) > 

<!-- 20060717 HE v1.0.0: nr und bez ergaenzt, Umsetzung Allgemeines Pkt. 16 -->
<!-- 20070112 he v1.2.0 CR 35: statt nr wird jetzt das Attribut wert zur 
     Angabe des Wertes fuer die Bezeichnung benutzt -->
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->  
<!ATTLIST anlage-ebene
		%attr.bez.opt;
		%attr.wert.opt;		
		%attr.sprache.opt; 
>

<!-- ================================================================= -->
<!-- Zusendung der Entscheidung -->
<!-- 20080915 ms v2.6 CR 199: Alternativ zu person auch organisation zugelassen -->
<!ELEMENT mitgeteiltvon		(person | organisation) >
