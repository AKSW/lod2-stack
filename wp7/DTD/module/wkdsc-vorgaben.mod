<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC VORGABE DTD Modul						-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 28.04.2009					-->
<!-- Public Identifier: -//WKD//DTD WKDSC VORGABE MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->

<!-- ******************************************************************	-->
<!-- Vorgabedefinition aller Inhaltsmodelle -->
<!-- ******************************************************************	-->



<!-- ================================================================= -->
<!-- Arten von Verweisen fuer die Wiederverwendung  -->
<!-- 20060926 he v1.1.0 CR 3: neues Element verweis-vtext-id hinzugefuegt -->
<!-- 20081029 ms v2.7 CR 224: Neuer Verweis-typ verweis-lex-->

<!ENTITY % verweis.komplett     "verweis-vs | verweis-es | verweis-esa | verweis-komhbe | verweis-zs | verweis-url | verweis-obj | verweis-vtext-id | verweis-lex | verweis-va" >

<!-- ================================================================= -->
<!-- gemeinsame Inhaltsmodelle fuer Titel -->
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     
<!ENTITY % titel.einfach     "#PCDATA | hoch | tief | titel-umbruch | sprache" >

<!-- 20070507 ms v1.2.2 CR 100: Element vu (Verweisunterdrueckung) neu aufgenommen, um Fundstellen zu markieren, die nicht verlinkt werden sollen -->     
<!-- 20070522 ms v2.0 CR 88: statt stichworte jetzt inline-stichwort-->
<!-- 20070531 ms v2.0 CR 111: Element fundstelle neu aufgenommen, um Fundstellen zu markieren und Text, der nicht verweis-text darin auch erfassen zu koennen -->  
<!-- 20070531 ms v1.2.4 CR 118: korrektur aufgenommen-->   
<!-- 20080915 ms v2.6 CR 198: abbildung aufgenommen -->
<!-- 20090417 ms v2.8 CR 247: Element red-zusatz hinzugefuegt fuer redaktionelle Zusaetze in amtlichen Texten z.B. Vorschriften -->     
<!ENTITY % titel.basis       " %titel.einfach; | zitat | fn | ref-anmerkung | variante | luecke | inline-stichwort | vu | fundstelle | korrektur | abbildung | red-zusatz" >

<!ENTITY % titel.komplett    " %titel.basis; | %verweis.komplett; " >


<!-- ================================================================= -->
<!-- gemeinsame Inhaltsmodelle fuer Kennungen -->
<!ENTITY % kennung.einfach	"#PCDATA | hoch | tief | hervor" >

<!-- 20070604 ms 1.2.4a CR 121: Element vu (Verweisunterdrueckung) neu aufgenommen, um Fundstellen zu markieren, die nicht verlinkt werden sollen (nur temporaer in Migrationsphase ZVR)-->
<!ENTITY % kennung.basis	"%kennung.einfach; | fn | ref-anmerkung | vu" >


<!-- ================================================================= -->
<!-- Basisinlineelemente -->
	
<!-- Inlinedefinition ohne Hervorhebung -->
<!ENTITY % inline.einfach     "#PCDATA | hoch | tief " >

<!-- einfache Inlinedefinition mit Hervorhebung und Anmerkungen -->
<!ENTITY % inline.einfach.hervor-fn     "%inline.einfach; | hervor | fn | ref-anmerkung" >


<!-- Inlinedefinition mit Hervorhebung und Zitat -->
<!-- 20070507 ms v1.2.2 CR 100: Element vu (Verweisunterdrueckung) neu aufgenommen, um Fundstellen zu markieren, die nicht verlinkt werden sollen -->     
<!-- 20070531 ms v2.0 CR 111: Element fundstelle neu aufgenommen, um Fundstellen zu markieren und Text, der nicht verweis-text darin auch erfassen zu koennen -->     
<!-- 20070919 ms v2.3 CR 123: Element sprache aufgenommen, um Worte in anderer Sprache zu kennzeichnen (insbesondere fuer Barrierefreiheit-->     

<!ENTITY % inline.basis       "%inline.einfach; | hervor | fn | ref-anmerkung | satz | rn | marginalie | zitat | korrektur | vu | fundstelle | sprache" >


<!-- Komplette Abbildung aller Inlineelemente -->
<!-- 20070522 ms v2.0 CR 88: statt stichworte jetzt inline-stichwort-->
<!-- 20090417 ms v2.8 CR 247: Element red-zusatz hinzugefuegt fuer redaktionelle Zusaetze in amtlichen Texten z.B. Vorschriften -->     
<!ENTITY % inline.komplett    "%inline.basis; | %verweis.komplett; | bruch | formel | luecke | feld | abbildung | inline-stichwort | konstante | datum | zahl | variante | text-variante | red-zusatz" >


<!-- ================================================================= -->
<!-- Absatzelemente -->

<!-- 20060629 HE v1.0.0 objekt-block hinzugefuegt -->
<!ENTITY % absatz.einfach     "absatz | absatz-rechts | abbildung-block | objekt-block" >

<!-- 20060627 HE v1.0.0 zitat-block hinzugefuegt -->
<!-- 20080312 ms v2.3.5 container-auspraegung hinzugefuegt-->
<!ENTITY % absatz.basis       "%absatz.einfach; | liste | liste-auto | tabelle | zitat-block | container-auspraegung" >


<!ENTITY % absatz.komplett    "%absatz.basis; | container-block" >


<!-- ================================================================= -->
<!-- Auflistung von Elementen fuer bestimmte wiederverwendbare Inhaltsmodelle  -->

<!-- da zitat-block im Absatzbasismodell enthalten ist, wird dies nicht mehr ueberall direkt benoetigt -->
<!-- 20090428 ms v2.8 CR 256: Neuer DTD-Zweig fuer Verwaltungsanweisungen: zitat-va eingefuegt-->

<!ENTITY % elemente.zitat.kein-block		"zitat-vs | zitat-es | zitat-va" >

<!ENTITY % elemente.zitat.komplett		"zitat-vs | zitat-es | zitat-block | zitat-va" >



<!-- 20060704 HE v1.0.0 vs-objekt hinzugefuegt Umsetzung Kommentar Pkt. 18 -->
<!ENTITY % elemente.vorschrift		"vorschrift | vs-ebene | paragraph | artikel | vs-anlage | vs-absatz | vs-objekt" >


<!ENTITY % elemente.entscheidung	"entscheidung | pressemitteilung" >


<!ENTITY % elemente.titel.einfach	"kennung?, (titel | ohne-titel), titel-zusatz?, toc-titel?" >


<!ENTITY % elemente.li			"(%absatz.komplett; | aufhebung | anmerkung)+" >


<!-- ================================================================= -->
<!-- Definiton der Verzeichnismodelle   -->

<!ENTITY % elemente.verzeichnis.alle		"vz-inhalt-auto | vz-tabelle-auto | vz-abbildung-auto | vz-stw-auto | vz-stw-manuell | vz-abk-auto | vz-abk-manuell | vz-autor-auto | vz-autor-manuell | vz-literatur-manuell" >

<!ENTITY % elemente.verzeichnis.inhalte-auto	"vz-inhalt-auto | vz-tabelle-auto | vz-abbildung-auto" >

<!ENTITY % elemente.verzeichnis.kommentierung	"vz-inhalt-auto | vz-stw-auto | vz-stw-manuell | vz-abk-auto | vz-abk-manuell | vz-literatur-manuell" >

<!-- ================================================================= -->
<!-- Inhaltsmodell von Aufsaetzen mit Strukturebenen -->
<!-- 20060627 HE v1.0.0 elemente.zitat fuer das Inhaltsmodell aufgenommen, Umsetzung ZS Pkt. 10 -->
<!ENTITY % elemente.aufsatz		"(aufsatz-ebene | zwischen-titel | anmerkung | %elemente.zitat.kein-block; | %absatz.komplett;)*" >


<!-- ================================================================= -->

<!-- Inhaltsmodell zur Wiedergabe von Entscheidungen in redaktionellen Inhalten -->
<!-- 20060926 he v1.1.0 CR 26: es-beschlusstext eingefuegt -->
<!-- 20070112 he v1.2.0 CR 36: es-titel-kopf aus dem Entscheidungszweig aufgenommen -->
<!-- 20070125 he v1.2.0 CR 65: Aufnahme der redaktionellen Leitsaetze nach der Differenzierung durch das Schachtelungselement -->
<!-- 20070222 sk v1.2.0 CR 29: rechtskraft eingefuegt -->
<!-- 20070402 ms v1.2.1 CR 79: Element red-leitsaetze entfernt, da mit red-leitsaetze eine Mischung von amtlichen und redaktionellen Leitsaetzen innerhalb einer Entscheidung nicht realisierbar ist-->
<!-- 20070606 ms v2.0 CR 111: Fuer JURION Analysen neues Element es-rechtskraft um Text zur Rechtskraft aufzunehmen-->
<!-- 20070928 ms v2.3 CR 158: esr-metadaten analog zu es-metadaten in entscheidung aufgenommen, es-liste und urteilsname entfernt, weil jetzt Teil von esr-metadaten-->
<!-- 20080307 ms v2.3.5 CR 168: Zur Angleichung an entscheidung wurden die Element schlussantrag |  es-inhalt-eu-block | streitwertbeschluss eingefuegt-->
<!ENTITY % elemente.es-redaktionelle-wiedergabe		"leitsaetze | orientierungssaetze | es-stichwort |
							 normenkette | fundstellen | 
							 tenor | tatbestand | gruende | kostenentscheidung | 
							 es-anmerkung | 
							 container-block | anmerkung |
							 es-beschlusstext |
							 es-titel-kopf | es-rechtskraft | esr-metadaten | schlussantrag |  								es-inhalt-eu-block | streitwertbeschluss">
