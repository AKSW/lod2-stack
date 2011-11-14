<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC BASIS DTD Modul						-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 03.08.2009					-->
<!-- Public Identifier: -//WKD//DTD WKDSC BASIS MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->

<!-- 20070507 MS v1.2.2: Character Entity Sets werden in jedem Zweig eingebunden statt im Modul Basis, weil es sonst beim Parsen mit Omnimark u.a. Systemen Probleme gibt-->

<!-- ***************************************************************** -->
<!-- Entitaetendefinition -->
<!-- ***************************************************************** -->

<!-- ================================================================= -->
<!-- gemeinsame Attributdefinitionen  -->
<!-- nr als Attributwert -->
<!ENTITY % attr.nr.req			"nr	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.nr.opt			"nr	 	CDATA 	#IMPLIED" >


<!-- Randnummer als Attributwert wenn sie gebraucht wird -->
<!ENTITY % attr.rn.req			"rn	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.rn.opt			"rn	 	CDATA 	#IMPLIED" >

<!-- Falls ein Randziffernbereich angegeben werden soll -->
<!ENTITY % attr.rn-bis.opt		"rn-bis 	CDATA 	#IMPLIED" >


<!-- Text zu einem Element mit dann jeweils spezifischer Bedeutung -->
<!ENTITY % attr.text.req		"text	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.text.opt		"text	 	CDATA 	#IMPLIED" >

<!-- Volltext-ID fuer die eindeutige Referenzierung von Texten in Onlineangeboten von WKD  -->
<!ENTITY % attr.vtext-id.req		"vtext-id 	CDATA 	#REQUIRED" >
<!ENTITY % attr.vtext-id.opt		"vtext-id 	CDATA 	#IMPLIED" >

<!-- 20080703 ms v2.3.5 CR 170: Neues Attribut id-typ -->
<!ENTITY % attr.id-typ.req		"id-typ 	CDATA 	#REQUIRED" >
<!ENTITY % attr.id-typ.opt		"id-typ 	CDATA 	#IMPLIED" >

<!-- 20060926 he v1.1.0 CR 3: zur Typisierung des neues Element verweis-vtext-id hinzugefuegt -->
<!-- Dokumenttypen auf die mit Hilfe der Volltext-ID verwiesen werden kann -->
<!ENTITY % attr.vtext-typ		"vtext-typ 	(aufsatz | entscheidung | entscheidungssammlung | kommentar | news | vorschrift)" >
<!ENTITY % attr.vtext-typ.req		"%attr.vtext-typ; 	#REQUIRED" >
<!ENTITY % attr.vtext-typ.opt		"%attr.vtext-typ; 	#IMPLIED" >


<!-- 20070125 he v1.2.0 CR 57: Typen zum neuen Element verweis-unbekannt in zentraler Definition -->
<!-- die Werte der Typen entsprechen den Suffixen der bisher definierten Verweiselemente -->
<!ENTITY % attr.verweis-typ		"typ 	(vs | es | esa | komhbe | zs | bmf | url | obj | vtext-id | unbekannt)" >
<!ENTITY % attr.verweis-typ.req		"%attr.verweis-typ; 	#REQUIRED" >
<!ENTITY % attr.verweis-typ.opt		"%attr.verweis-typ; 	#IMPLIED" >


<!-- Vergabe einer ID in Elementen um darauf mittels id-ref referenzieren zu koennen -->
<!ENTITY % attr.ref-id.req		"ref-id 	CDATA 	#REQUIRED" >
<!ENTITY % attr.ref-id.opt		"ref-id 	CDATA 	#IMPLIED" >


<!-- dies ist das Attribut um auf eine vergebene ref-id verweisen zu koemmen -->
<!ENTITY % attr.id-ref.req		"id-ref 	CDATA 	#REQUIRED" >
<!ENTITY % attr.id-ref.opt		"id-ref 	CDATA 	#IMPLIED" >

<!-- das Attribut bezeichnung wird immer dort benutzt, wo eindeutige Bezeichnungen von Elementen erwartet werden -->
<!ENTITY % attr.bezeichnung.req		"bezeichnung 	CDATA 	#REQUIRED" >
<!ENTITY % attr.bezeichnung.opt		"bezeichnung 	CDATA 	#IMPLIED" >


<!-- Angabe der Herkunft von Inhalten -->
<!ENTITY % attr.typ-herkunft		"typ		(redaktionell | amtlich | unbekannt)" >
<!ENTITY % attr.typ-herkunft.req	"%attr.typ-herkunft;	#REQUIRED" >
<!ENTITY % attr.typ-herkunft.opt	"%attr.typ-herkunft;	#IMPLIED" >
<!ENTITY % attr.typ-herkunft.default	"%attr.typ-herkunft;	'amtlich'" >

<!-- 20070507 ms v1.2.2 CR 98: Neues Attribut "herkunft" bei Elementen bei denen "typ" schon anderweitig definiert ist (z.B. hervor oder rn)-->
<!ENTITY % attr.herkunft		"herkunft		(redaktionell | amtlich | unbekannt)" >
<!ENTITY % attr.herkunft.req		"%attr.herkunft;	#REQUIRED" >
<!ENTITY % attr.herkunft.opt		"%attr.herkunft;	#IMPLIED" >
<!ENTITY % attr.herkunft.default	"%attr.herkunft;	'amtlich'" >

<!ENTITY % attr.typ-besetzt		"typ		(besetzt | unbesetzt)" >
<!ENTITY % attr.typ-besetzt.req		"%attr.typ-besetzt;	#REQUIRED" >
<!ENTITY % attr.typ-besetzt.opt		"%attr.typ-besetzt;	#IMPLIED" >


<!ENTITY % attr.typ-zahl		"typ		(zahl | telefon | plz | blz | ktnr | iban | prozent | promille | eur | usd )" >
<!ENTITY % attr.typ-zahl.req		"%attr.typ-zahl;	#REQUIRED" >
<!ENTITY % attr.typ-zahl.opt		"%attr.typ-zahl;	#IMPLIED" >
<!ENTITY % attr.typ-zahl.default	"%attr.typ-zahl;	'zahl'" >
<!ENTITY % attr.typ-zahl.default.eur	"%attr.typ-zahl;	'eur'" >

<!-- 20070531 ms v2.0 CR 111: Neues Attribut typ zu Verweisen, um parallelfundstellen zu kennzeichnen-->
<!ENTITY % attr.typ-fundstelle.req	"typ	(wkd-fundstelle | parallelfundstelle | unbekannt)	#REQUIRED">
<!ENTITY % attr.typ-fundstelle.opt	"typ	(wkd-fundstelle | parallelfundstelle | unbekannt)	#IMPLIED">

<!--20080312 ms v2.3.6 CR 175: Attribut typ fuer medienspezifische Ausgabe -->
<!ENTITY % attr.typ-medien.req	"typ 	(print | online | offline | emedia | barriere) 	#REQUIRED">
<!ENTITY % attr.typ-medien.opt	"typ 	(print | online | offline | emedia | barriere) 	#IMPLIED">


<!ENTITY % attr.zielgruppe.req	"zielgruppe	CDATA		#REQUIRED">
<!ENTITY % attr.zielgruppe.opt	"zielgruppe 	CDATA		#IMPLIED">

<!ENTITY % attr.rechteinhaber.req	"rechteinhaber	CDATA		#REQUIRED">
<!ENTITY % attr.rechteinhaber.opt	"rechteinhaber	CDATA		#IMPLIED">

<!-- 20071114 ms v2.3.1 CR 160: Attribut bezugsquelle hier definiert, damit es mehrfach verwendet werden kann-->
<!ENTITY % attr.bezugsquelle.req	"bezugsquelle	CDATA		#REQUIRED">
<!ENTITY % attr.bezugsquelle.opt	"bezugsquelle	CDATA		#IMPLIED">

<!-- Festlegung wie Abstaende im Layout abgebildet sind -->
<!ENTITY % attr.abstand			"abstand	(normal| klein| gross)" >
<!ENTITY % attr.abstand.opt		"%attr.abstand;		#IMPLIED" >


<!-- Ausrichtung -->
<!ENTITY % attr.ausrichtung		"ausrichtung	(links | rechts | mitte | block)" >
<!ENTITY % attr.ausrichtung.opt		"%attr.ausrichtung;	#IMPLIED" >


<!-- Orientierung -->
<!ENTITY % attr.orientierung		"orientierung	(normal | gedreht)" >
<!ENTITY % attr.orientierung.opt	"orientierung	(normal | gedreht)	#IMPLIED" >


<!-- Monat -->
<!ENTITY % attr.monat.req		"monat 		CDATA 	#REQUIRED" >
<!ENTITY % attr.monat.opt		"monat 		CDATA 	#IMPLIED" >


<!-- Tag -->
<!ENTITY % attr.tag.req			"tag 		CDATA 	#REQUIRED" >
<!ENTITY % attr.tag.opt			"tag 		CDATA 	#IMPLIED" >

<!-- 20070531 ms v2.0 CR 111: neues Attribut prioritaet-->
<!-- Prioritaet-->
<!ENTITY % attr.prio.req		"prioritaet		(wichtig | sehrwichtig | ueberragendwichtig | unbekannt)	#REQUIRED">
<!ENTITY % attr.prio.opt		"prioritaet		(wichtig | sehrwichtig | ueberragendwichtig | unbekannt)	#IMPLIED">

<!-- 20070727 ms v2.1 CR 123: neues Attribut sprache mit ISO 639-3 Codes-->
<!-- 20080915 ms v2.6 CR 204: Default "deu" entfernt-->
<!ENTITY % attr.sprache		"sprache 		(sqi | ara | arg | hye | eus | bos | bre | bul | zho | dan | 
						deu | eng | epo | est | fao | fin | fra | glg | kat | ell | heb | gle | isl | ita | jpn | 
						cat | kor | cos | hrv | lat | lav | lit | ltz | mlt | mkd | mol | nld | nor | pol | por | 
						ron | rus | swe | srp | slk | slv | spa | ces | tur | ukr | hun | cym | bel)">
<!ENTITY % attr.sprache.req		"%attr.sprache;	#REQUIRED" >
<!ENTITY % attr.sprache.opt		"%attr.sprache;	#IMPLIED">

<!ENTITY % attr.auszug.req		"auszug		(ja | nein)	#REQUIRED">
<!ENTITY % attr.auszug.opt		"auszug		(ja | nein)	#IMPLIED">
<!ENTITY % attr.auszug.default	"auszug		(ja | nein)	'nein'">


<!-- ================================================================= -->
<!-- Typisierung von Containerbloecken -->

<!-- 20060926 he v1.1.0 CR 27: arbeitsmitteln als neuen Wert hinzugefuegt -->
<!-- 20070402 ms v1.2.1 CR 71: vorschrift als neuen Wert hinzugefuegt -->
<!-- 20070402 ms v1.2.2 CR 102: rechtsgrundlage als neuen Wert hinzugefuegt -->
<!-- 20070402 ms v2.0 CR 111: handlungsbedarf, fundstellen, analyse als neuen Wert hinzugefuegt -->
<!-- 20070726 ms v2.1 CR 131: checkliste als neuen Wert hinzugefuegt-->
<!-- 20070829 ms v2.2 CR 111: zusammenfassung als neuen Wert hinzugefuegt-->
<!-- 20070919 ms v2.3 CR 157: aenderung als neuen Wert hinzugefuegt-->
<!-- 20090803 ms 2.9 CR 259: adresse als neuen Wert hinzugefuegt-->
<!ENTITY % attr.container-block		"typ	(achtung |
						 adresse | 
						 aenderung | 
						 analyse |
			 			 arbeitsmittel |
			 			 ausnahme |
			 			 beispiel |
			 			 berechnungsschema |
						 checkliste | 
						 fundstellen | 
						 handlungsbedarf |
			 			 hilfe |
						 hinweis |
			 			 info |
			 			 querverweis |
						 rechtsgrundlage | 
			 			 tipp |
			 			 uebersicht |
						 vorschrift | 
			 			 warnung |
			 			 wichtig |
						 zusammenfassung | 
			 			 unbekannt )"
>

<!ENTITY % attr.container-block.req		" %attr.container-block; #REQUIRED">
<!ENTITY % attr.container-block.default		" %attr.container-block; 'hinweis' ">

<!ENTITY % attr.thema.opt		"thema		CDATA	#IMPLIED">
<!ENTITY % attr.thema.req		"thema		CDATA	#REQUIRED">


<!-- Attributdefinitionen fuer Beitrag -->
<!-- 20070809 ms v2.1.1 CR 142: Neuer typ werksuebersicht eingefuegt-->
<!-- 20080915 ms v2.6 CR 207: Neuer typ externes-dokument eingefuegt-->
<!-- 20090417 ms v2.8 CR 233: Neuer typ arbeitshilfe eingefuegt-->
<!ENTITY % attr.beitragtyp.req	"typ (beitrag | einleitung | vorwort | lexikon | verzeichnis | impressum | editorial | titelseite | zitat | nachricht | werksuebersicht | externes-dokument | arbeitshilfe)  #REQUIRED">


<!-- ================================================================= -->
<!-- Attributdefinitionen fuer Verlinkung und Referenzen -->
<!-- ================================================================= -->

<!-- ================================================================= -->
<!-- Entscheidungen -->
<!ENTITY % attr.gericht.req		"gericht 	CDATA 	#REQUIRED" >
<!ENTITY % attr.gericht.opt		"gericht 	CDATA 	#IMPLIED" >


<!ENTITY % attr.datum.req		"datum	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.datum.opt		"datum 		CDATA 	#IMPLIED" >

<!-- 20060607 HE@SL az statt akz, da bisher immer Verwendung fand -->
<!ENTITY % attr.az.req			"az	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.az.opt			"az	 	CDATA 	#IMPLIED" >


<!-- Zusatz zum Aktenzeichen, um eine Entscheidung darueber eindeutig zu machen -->
<!ENTITY % attr.az-zusatz.req		"az-zusatz 	CDATA 	#REQUIRED" >
<!ENTITY % attr.az-zusatz.opt		"az-zusatz 	CDATA 	#IMPLIED" >


<!-- Typ einer Entscheidung -->
<!-- 20080307 ms v2.3.5 CR 172: Neuer es-typ "gerichtlicher-hinweis"-->
<!-- 20080915 ms v2.6 CR 202: Neue Typen "schlussantrag", "eugh-vorlage", "gutachten"-->
<!-- 20081029 ms v2.7 CR 204: Version des Attributs mit #REQUIRED angelegt-->
<!ENTITY % attr.es-typ		"es-typ 	(urteil | beschluss | gerichtsbescheid | entscheidung | gerichtlicher-hinweis | schlussantrag | eugh-vorlage | gutachten) " >
<!ENTITY % attr.es-typ.opt		"%attr.es-typ; 	#IMPLIED" >
<!ENTITY % attr.es-typ.default	"%attr.es-typ; 	'urteil' " >
<!ENTITY % attr.es-typ.req		"%attr.es-typ; 	#REQUIRED" >

<!-- originale und offizielle Randnummer in einer Volltextentscheidung -->
<!ENTITY % attr.es-rn.req		"es-rn	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.es-rn.opt		"es-rn	 	CDATA 	#IMPLIED" >


<!-- 20070126 he v1.2.0 CR 61: zentrale Typisierung fuer das neue Element verfahrensbeteiligter -->
<!-- 20070727 ms v2.1 CR 137: neuer Typ richter-->
<!-- Auspraegung eines Verfahrensbeteiligten -->
<!ENTITY % attr.verfahrensbeteiligter-typ		"typ 	(antragsgegner | antragsteller | beklagter | beschwerdefuehrer | klaeger | nebenklaeger | opfer | prozessbevollmaechtigter  | richter | sachverstaendiger | zeuge | unbekannt) " >
<!ENTITY % attr.verfahrensbeteiligter-typ.opt		"%attr.verfahrensbeteiligter-typ; 	#IMPLIED" >
<!ENTITY % attr.verfahrensbeteiligter-typ.req		"%attr.verfahrensbeteiligter-typ; 	#REQUIRED" >

<!ENTITY % attr.senat.req		"senat	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.senat.opt		"senat	 	CDATA 	#IMPLIED" >

<!-- 20090417 ms v2.8 CR 253: neues Attribut urteilsname -->
<!ENTITY % attr.urteilsname.req	"urteilsname	CDATA	#REQUIRED">
<!ENTITY % attr.urteilsname.opt	"urteilsname	CDATA	#IMPLIED">


<!-- ================================================================= -->
<!-- Adressen von Vorschriftenteilen und auch Bestandteilen von Kommentaren -->

<!-- vsk = Vorschriftenkuerzel -->
<!ENTITY % attr.vsk.req			"vsk	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.vsk.opt			"vsk	 	CDATA 	#IMPLIED" >


<!-- art = Artikel Ziffer und Buchstabe zusammen -->
<!ENTITY % attr.art.req			"art	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.art.opt			"art	 	CDATA 	#IMPLIED" >


<!-- art-bis = Fuer die Bereichsangabe bei Artikelverlinkung -->
<!ENTITY % attr.art-bis.req		"art-bis 	CDATA 	#REQUIRED" >
<!ENTITY % attr.art-bis.opt		"art-bis 	CDATA 	#IMPLIED" >


<!-- par = Paragraph Ziffer und Buchstabe zusammen -->
<!ENTITY % attr.par.req			"par	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.par.opt			"par	 	CDATA 	#IMPLIED" >


<!-- par-bis = Fuer die Bereichsangabe bei Paragraphenverlinkung -->
<!ENTITY % attr.par-bis.req		"par-bis 	CDATA 	#REQUIRED" >
<!ENTITY % attr.par-bis.opt		"par-bis 	CDATA 	#IMPLIED" >


<!-- vs-abs = Nummer eines Vorschriftenabsatzes -->
<!ENTITY % attr.abs.req			"abs 	CDATA 	#REQUIRED" >
<!ENTITY % attr.abs.opt			"abs 	CDATA 	#IMPLIED" >


<!-- vs-abs = Bis zu der Nummer eines Vorschriftenabsatzes -->
<!ENTITY % attr.abs-bis.req		"abs-bis 	CDATA 	#REQUIRED" >
<!ENTITY % attr.abs-bis.opt		"abs-bis 	CDATA 	#IMPLIED" >


<!-- 20070402 ms v1.2.1 CR 76: Attribut vs-obj-nr und vs-obj-typ zum Verweis auf vs-objekt hinzugefuegt-->
<!-- 20080116 ms v2.3.4 CR 165: Attributwert muster zu vs-obj-typ hinzugfuegt-->
<!-- vs-obj-nr = Nummer des vs-objektes -->
<!ENTITY % attr.vs-obj-nr.req		"vs-obj-nr	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.vs-obj-nr.opt			"vs-obj-nr	 	CDATA 	#IMPLIED" >

<!ENTITY % attr.vs-obj-typ.req			"vs-obj-typ	(praeamble | kennziffer | info | muster)	  #REQUIRED">
<!ENTITY % attr.vs-obj-typ.opt			"vs-obj-typ	(praeamble | kennziffer | info | muster)	  #IMPLIED">

<!ENTITY % attr.vsobj-typ.req			"typ	(praeamble | kennziffer | info | muster)	  #REQUIRED">
<!ENTITY % attr.vsobj-typ.opt			"typ	(praeamble | kennziffer | info | muster)	  #IMPLIED">


<!-- 20060926 he v1.1.0 CR 5: Attribut zur Angabe des Linkankers in Listen  -->
<!-- li-pkt = Bis zu der Nummer einer Liste in Vorschriften -->
<!ENTITY % attr.li-pkt.req		"li-pkt 	CDATA 	#REQUIRED" >
<!ENTITY % attr.li-pkt.opt		"li-pkt 	CDATA 	#IMPLIED" >


<!-- satz = Satzziffer in einer Vorschrift und allgemeiner Satz -->
<!ENTITY % attr.satz.req		"satz	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.satz.opt		"satz	 	CDATA 	#IMPLIED" >


<!-- Verweis auf eine Abbildung ueber deren Nummernangabe -->
<!ENTITY % attr.abb-nr.req		"abb-nr	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.abb-nr.opt		"abb-nr	 	CDATA 	#IMPLIED" >


<!-- Verweis auf eine Tabelle ueber deren Nummernangabe -->
<!ENTITY % attr.tab-nr.req		"tab-nr	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.tab-nr.opt		"tab-nr	 	CDATA 	#IMPLIED" >


<!-- Verweis auf eine Protokollnotiz ueber deren Nummernangabe -->
<!ENTITY % attr.prot-nr.req		"prot-nr	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.prot-nr.opt		"prot-nr	 	CDATA 	#IMPLIED" >


<!-- Referenz auf eine Anlage -->
<!ENTITY % attr.anlage-nr.req		"anlage-nr 	CDATA 	#REQUIRED" >
<!ENTITY % attr.anlage-nr.opt		"anlage-nr 	CDATA 	#IMPLIED" >


<!-- Angabe des Typs der Anlage -->
<!ENTITY % attr.anlage-typ		"anlage-typ 	(anlage | anhang)" >
<!ENTITY % attr.anlage-typ.req		"%attr.anlage-typ; 	#REQUIRED" >
<!ENTITY % attr.anlage-typ.opt		"%attr.anlage-typ; 	#IMPLIED" >


<!-- Referenz auf eine Ebene -->
<!ENTITY % attr.vs-ebene.req		"vs-ebene 	CDATA 	#REQUIRED" >
<!ENTITY % attr.vs-ebene.opt		"vs-ebene 	CDATA 	#IMPLIED" >

<!-- 20081029 ms v2.7 CR 211: Neues Attribut anlage-ebene-->
<!-- Referenz auf eine Anlage-Ebene -->
<!ENTITY % attr.anlage-ebene.req		"anlage-ebene 	CDATA 	#REQUIRED" >
<!ENTITY % attr.anlage-ebene.opt		"anlage-ebene 	CDATA 	#IMPLIED" >

<!-- Stand des Bestandteils einer Vorschrift bzw. der Vorschrift selber -->
<!ENTITY % attr.vs-stand.req		"vs-stand 	CDATA 	#REQUIRED" >
<!ENTITY % attr.vs-stand.opt		"vs-stand 	CDATA 	#IMPLIED" >


<!-- Gueltigkeit (hier Inkrafttreten) des Bestandteils einer Vorschrift bzw. der Vorschrift selber -->
<!ENTITY % attr.vs-inkraft.req		"vs-inkraft 	CDATA 	#REQUIRED" >
<!ENTITY % attr.vs-inkraft.opt		"vs-inkraft 	CDATA 	#IMPLIED" >

<!-- Versionsnummer (Conware) des Bestandteils einer Vorschrift bzw. der Vorschrift selber -->
<!ENTITY % attr.vs-ver.req		"ver 	CDATA 	#REQUIRED" >
<!ENTITY % attr.vs-ver.opt		"ver 	CDATA 	#IMPLIED" >

<!-- ================================================================= -->
<!-- Vorschriften  -->
<!-- 20070507 ms v1.2.2 CR 106: Neuen Typ "Besprechungsergebnis" aufgenommen  -->
<!-- 20070516 ms v1.2.3 CR 110: Neue Typen: anordnung, anwendungsbestimmung, bericht, beschluss, gebuehrentabelle, hinweis, leitlinie, uebereinkommen, unterrichtung, verfahrensregelung, verlautbarung, versicherungsbedingung, verzeichnisse aufgenommen-->
<!-- 20090417 ms v2.8 CR 238: Neue Typen : entscheidung, abkommen, standard fuer EU-Recht-->
<!ENTITY % attr.vs-typ			"vs-typ		(
							 anordnung |
							 anwendungsbestimmung |
							 bekanntmachung |
							 bericht |
							 beschluss |
							 besprechungsergebnis | 
							 empfehlung |
							 erlass |
							 gebuehrentabelle |
							 gesetz |
							 grundsatz |
							 hinweis |
							 leitlinie |
							 richtlinie |
							 rundschreiben |
							 satzung |
							 tarifvertrag |
							 uebereinkommen |
							 unterrichtung |
							 vereinbarung |
							 verfahrensregelung |
							 verlautbarung |
							 verordnung |
							 versicherungsbedingung|
							 vertrag |
							 verwaltungsvorschrift |
							 verzeichnisse |
							entscheidung |
							abkommen |
							standard	)"
>

<!ENTITY % attr.vs-typ.req		" %attr.vs-typ;	#REQUIRED" >
<!ENTITY % attr.vs-typ.opt		" %attr.vs-typ;	#IMPLIED" >

<!-- Landeskuerzel gemaess www.bundestag.de/parlament/wahlen/sitzverteilung/ -->

<!ENTITY % attr.vs-herkunft			"vs-herkunft	(bund |
								 land_bb |
								 land_be |
								 land_bw |
								 land_by |
								 land_hb |
								 land_he |
								 land_hh |
								 land_mv |
								 land_ni |
								 land_nw |
								 land_rp |
								 land_sh |
								 land_sl |
								 land_sn |
								 land_st |
								 land_th |
								 eu |
								 ia )"
>

<!ENTITY % attr.vs-herkunft.req			"%attr.vs-herkunft;	#REQUIRED" >
<!ENTITY % attr.vs-herkunft.opt			"%attr.vs-herkunft;	#IMPLIED" >


<!-- 20090428 ms v2.8 CR 256: Neuer DTD-Zweig fuer Verwaltungsanweisungen-->
<!ENTITY % attr.va-herkunft			"va-herkunft	(bund |
								 land_bb |
								 land_be |
								 land_bw |
								 land_by |
								 land_hb |
								 land_he |
								 land_hh |
								 land_mv |
								 land_ni |
								 land_nw |
								 land_rp |
								 land_sh |
								 land_sl |
								 land_sn |
								 land_st |
								 land_th )"
>

<!ENTITY % attr.va-herkunft.req			"%attr.va-herkunft;	#REQUIRED" >
<!ENTITY % attr.va-herkunft.opt			"%attr.va-herkunft;	#IMPLIED" >

<!ENTITY % attr.va-typ	"typ	(erlass |
				schreiben | 
				verfuegung |
				verordnung |
				berichtigung |
				bekanntmachung)"
>
<!ENTITY % attr.va-typ.req			"%attr.va-typ;	#REQUIRED" >
<!ENTITY % attr.va-typ.opt			"%attr.va-typ;	#IMPLIED" >

<!ENTITY % attr.bmf-doknr.req			"bmf-doknr 	CDATA 	#REQUIRED" >
<!ENTITY % attr.bmf-doknr.opt			"bmf-doknr 	CDATA 	#IMPLIED" >

<!ENTITY % attr.behoerde.req			"behoerde	CDATA 	#REQUIRED" >
<!ENTITY % attr.behoerde.opt			"behoerde 	CDATA 	#IMPLIED" >

<!-- ================================================================= -->
<!ENTITY % attr.fna.req			"fna 	CDATA 	#REQUIRED" >
<!ENTITY % attr.fna.opt			"fna 	CDATA 	#IMPLIED" >


<!-- ================================================================= -->
<!-- Versionsinfos von Vorschriften  -->

<!ENTITY % attr.inkraft.req		"inkraft 	CDATA 	#REQUIRED" >
<!ENTITY % attr.inkraft.opt		"inkraft 	CDATA 	#IMPLIED" >


<!ENTITY % attr.ausserkraft.req		"ausserkraft 	CDATA 	#REQUIRED" >
<!ENTITY % attr.ausserkraft.opt		"ausserkraft 	CDATA 	#IMPLIED" >


<!ENTITY % attr.fundstelle.req		"fundstelle 	CDATA 	#REQUIRED" >
<!ENTITY % attr.fundstelle.opt		"fundstelle 	CDATA 	#IMPLIED" >


<!ENTITY % attr.stand.req		"stand 	CDATA 	#REQUIRED" >
<!ENTITY % attr.stand.opt		"stand 	CDATA 	#IMPLIED" >


<!-- ================================================================= -->
<!-- Bestandteile von Kommentaren, Handbuechern und Zeitschriften -->

<!-- produkt = genrelles eindeutiges Kuerzel eines Produktes -->
<!ENTITY % attr.produkt.req		"produkt 	CDATA 	#REQUIRED" >
<!ENTITY % attr.produkt.opt		"produkt 	CDATA 	#IMPLIED" >


<!-- jahrgang = Jahrgang in dem eine Printpublikation erscheint, vornehmlich Zeitschriften -->
<!ENTITY % attr.jahrgang.req		"jahrgang 	CDATA 	#REQUIRED" >
<!ENTITY % attr.jahrgang.opt		"jahrgang 	CDATA 	#IMPLIED" >


<!-- band = Band in dem eine Printpublikation erscheint, vornehmlich Zeitschriften -->
<!ENTITY % attr.band.req		"band	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.band.opt		"band	 	CDATA 	#IMPLIED" >


<!-- heft = heft in dem eine Printpublikation erscheint, vornehmlich Zeitschriften -->
<!ENTITY % attr.heft.req		"heft	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.heft.opt		"heft	 	CDATA 	#IMPLIED" >


<!-- vorschrift-zusatz = Zusatz zu einer Vorschrift, wie sie in produktspezifischen 
     Auspraegungen zur Unterscheidung dient z.B. BGB 2002, hier waere 2002 der Zusatz -->
<!-- 20070830 ms v2.2 CR 143: Das Attribut vs-zusatz wird umbenannt in vs-zuordnung, darin soll kuenftig sowohl Gesetzeskuerzel als auch Jahr enthalten sein.-->
<!ENTITY % attr.vs-zuordnung.req		"vs-zuordnung 	CDATA 	#REQUIRED" >
<!ENTITY % attr.vs-zuordnung.opt		"vs-zuordnung 	CDATA 	#IMPLIED" >


<!-- stichwort = Das Stichwort unter dem z.B. Entscheidungen oder Beitraege in Printpublikation eingeordnet werden -->
<!ENTITY % attr.stichwort.req		"stichwort 	CDATA 	#REQUIRED" >
<!ENTITY % attr.stichwort.opt		"stichwort 	CDATA 	#IMPLIED" >


<!-- fassung = Fassung -->
<!ENTITY % attr.fassung.req		"fassung 	CDATA 	#REQUIRED" >
<!ENTITY % attr.fassung.opt		"fassung 	CDATA 	#IMPLIED" >


<!-- fach = Fach in dem z.B. Entscheidungen oder Beitraege in Printpublikation eingeordnet werden -->
<!ENTITY % attr.fach.req		"fach	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.fach.opt		"fach	 	CDATA 	#IMPLIED" >

<!-- vorbem = Vorbemerkung zu einem Paragraphen oder Artikel, zu der Entscheidungen in einer Entscheidungssammlung zugeordnet werden -->
<!ENTITY % attr.vorbem.req		"vorbem	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.vorbem.opt		"vorbem	 	CDATA 	#IMPLIED" >

<!-- gruppe = Gruppe von zusammenhaengenden Bestandteilen einer Printpublikation -->
<!ENTITY % attr.gruppe.req		"gruppe 	CDATA 	#REQUIRED" >
<!ENTITY % attr.gruppe.opt		"gruppe	CDATA 	#IMPLIED" >


<!-- beilage = Beilage zu einer Printpublikation die zusaetzlich vorkommt -->
<!ENTITY % attr.beilage.req		"beilage 	CDATA 	#REQUIRED" >
<!ENTITY % attr.beilage.opt		"beilage	CDATA 	#IMPLIED" >


<!-- land = Angabe des (Bundes)Landes unter dem z.B. eine Entscheidung in einer Publikation zugeordnet wird -->
<!ENTITY % attr.land.req		"land 		CDATA 	#REQUIRED" >
<!ENTITY % attr.land.opt		"land		CDATA 	#IMPLIED" >


<!-- auflage = Auflage einer Publikation -->
<!ENTITY % attr.auflage.req		"auflage 	CDATA 	#REQUIRED" >
<!ENTITY % attr.auflage.opt		"auflage	CDATA 	#IMPLIED" >


<!-- al = Aktualisierungslieferung einer Publikation -->
<!ENTITY % attr.al.req			"al 		CDATA 	#REQUIRED" >
<!ENTITY % attr.al.opt			"al		CDATA 	#IMPLIED" >


<!-- jahr = Jahr in dem eine Printpublikation erscheint, vornehmlich Zeitschriften -->
<!ENTITY % attr.jahr.req		"jahr	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.jahr.opt		"jahr	 	CDATA 	#IMPLIED" >


<!-- start-seite = Startseite eines Beitrages in einer Publikation -->
<!ENTITY % attr.start-seite.req		"start-seite 	CDATA 	#REQUIRED" >
<!ENTITY % attr.start-seite.opt		"start-seite	CDATA 	#IMPLIED" >

<!-- end-seite = Endseite eines Beitrages in einer Publikation -->
<!ENTITY % attr.end-seite.req		"end-seite 	CDATA 	#REQUIRED" >
<!ENTITY % attr.end-seite.opt		"end-seite	CDATA 	#IMPLIED" >

<!-- fund-seite = Seite einer konkreten Fundstelle in einer Publikation -->
<!ENTITY % attr.fund-seite.req		"fund-seite 	CDATA 	#REQUIRED" >
<!ENTITY % attr.fund-seite.opt		"fund-seite	CDATA 	#IMPLIED" >


<!-- pos-auf-seite = Position auf der Seite fuer eine konkreten Fundstelle 
     falls die Angabe der Seite nicht eindeutig genug ist -->
<!ENTITY % attr.pos-auf-seite.req	"pos-auf-seite 	CDATA 	#REQUIRED" >
<!ENTITY % attr.pos-auf-seite.opt	"pos-auf-seite	CDATA 	#IMPLIED" >


<!-- autor = Angabe des Autor um eine Fundstelle in einer Publikation anzugeben  -->
<!ENTITY % attr.autor.req		"autor 		CDATA 	#REQUIRED" >
<!ENTITY % attr.autor.opt		"autor		CDATA 	#IMPLIED" >


<!-- gl-nr = vollstaendige Gliederungsnummer als Adressangabe eines Beitrages  -->
<!ENTITY % attr.gl-nr.req		"gl-nr 		CDATA 	#REQUIRED" >
<!ENTITY % attr.gl-nr.opt		"gl-nr		CDATA 	#IMPLIED" >

<!-- Bezeichnung fuer das generische Verweiskonzept fuer Ebenen -->
<!ENTITY % attr.bez.req		"bez 		CDATA 	#REQUIRED" >
<!ENTITY % attr.bez.opt		"bez		CDATA 	#IMPLIED" >


<!-- bez enthaelt die Bezeichnung des Bezugspunktes, wert den Wert fuer den realen Verweis -->
<!ENTITY % attr.wert.req			"wert	 	CDATA 	#REQUIRED" >
<!ENTITY % attr.wert.opt			"wert	 	CDATA 	#IMPLIED" >

<!ENTITY % attr.vz-tiefe.opt		"vz-tiefe		CDATA 	#IMPLIED" >
<!ENTITY % attr.vz-tiefe.req		"vz-tiefe		CDATA 	#REQUIRED" >


<!ENTITY % attr.vz-typ			"vz-typ		(gliederung | randnummer | literatur | abkuerzung )" >

<!ENTITY % attr.vz-typ.opt		"%attr.vz-typ;		#IMPLIED" >
<!ENTITY % attr.vz-typ.req		"%attr.vz-typ;		#REQUIRED" >

<!-- Alternativtext und Titel fuer Barrierefreiheit  -->
<!ENTITY % attr.text-bitv.req		"text-bitv 		CDATA 	#REQUIRED" >
<!ENTITY % attr.text-bitv.opt		"text-bitv		CDATA 	#IMPLIED" >

<!ENTITY % attr.titel-bitv.req		"titel-bitv		CDATA 	#REQUIRED" >
<!ENTITY % attr.titel-bitv.opt		"titel-bitv		CDATA 	#IMPLIED" >

<!-- 20070130 v1.2.0 CR 40: Zur Differenzierung des Artes einer Anmerkung -->
<!-- 20090417 v2.8 ms CR 214: Neuer Attributwert "jw" fuer Jahreswert (ZVR) eingefuegt-->
<!-- 20090803 v2.9 ms CR 261: Neuer Attributwert "prot" fuer Protokollnotiz-Anmerkungen (ZVR) eingefuegt-->
<!ENTITY % attr.anm-fn-typ                "anm | vor | historie | jw | prot" >
<!ENTITY % attr.anmerkung-typ                "anmerkung-typ    (%attr.anm-fn-typ;)" >
<!ENTITY % attr.anmerkung-typ.opt         "%attr.anmerkung-typ;	#IMPLIED" >
<!ENTITY % attr.anmerkung-typ.req         "%attr.anmerkung-typ;	#REQUIRED" >

<!-- 20081029 ms v2.7 CR 217: Analog zu anmerkung-typ Attribut fuer fn-typ angelegt-->
<!ENTITY % attr.fn-typ                "fn-typ    (%attr.anm-fn-typ;)" >
<!ENTITY % attr.fn-typ.opt         "%attr.fn-typ;	#IMPLIED" >
<!ENTITY % attr.fn-typ.req         "%attr.fn-typ;	#REQUIRED" >

<!-- -20070222 v.1.2.0 : Definition der Rechtskraft einer Entscheidung in Enscheidungssammlung -->
<!ENTITY % attr.rechtskraft                        "rechtskraft          (rechtskraeftig | entschieden | unbekannt)" 			>
<!ENTITY % attr.rechtskraft.opt                  "%attr.rechtskraft;         #IMPLIED">
<!ENTITY % attr.rechtskraft.req                 "%attr.rechtskraft;        #REQUIRED">