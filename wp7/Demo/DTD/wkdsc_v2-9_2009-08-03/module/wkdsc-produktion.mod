<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC DTD Modul fuer Produktzusammenstellungen	 		-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 11.11.2008					-->
<!-- Public Identifier: -//WKD//DTD WKDSC PRODUKTION MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->

<!-- ***************************************************************** -->
<!-- Produkt-Import  -->
<!-- ***************************************************************** -->

<!-- 20070829 ms v2.2 CR 149: Neues Element lexikon-werk in produkt-import eingefuegt-->
<!-- 20081029 ms v2.7 CR 221: Neues Element vorschriftensammlung in produkt-import eingefuegt-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->

<!ELEMENT produkt-import  (produkt-steuerung?, externe-daten?, (kommentar | handbuch | entscheidungssammlung | zeitschrift | lexikon-werk | vorschriftensammlung)) >

<!-- 20070726 ms v2.1 CR 125: autor optional eingefuegt-->
<!-- 20070809 ms v2.1.1 CR 145: statt kommentierung, beitrag, etc. werden mit Hilfe von dokument und dessen Attribut href entsprechende Instanzen referenziert-->
<!-- 20080915 ms v2.6 CR 192: Alternativ zu dokument ein neues Element io-import zur Einbindung von bereits vorhandenen IOs wie z.B. ZVR Vorschriften-IOs-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT kommentar  (werk-steuerung?,titel-kopf, autor?, %elemente.metadaten;,(kommentar-ebene | dokument | io-import | werk-steuerung)+) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST kommentar
		%attr.produkt.req;
		%attr.sprache.opt;
>

<!-- 20070726 ms v2.1 CR 125: autor optional eingefuegt-->
<!-- 20070809 ms v2.1.1 CR 145: statt kommentierung, beitrag, etc. werden mit Hilfe von dokument und dessen Attribut href entsprechende Instanzen referenziert-->
<!-- 20080915 ms v2.6 CR 192: Alternativ zu dokument ein neues Element io-import zur Einbindung von bereits vorhandenen IOs wie z.B. ZVR Vorschriften-IOs-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT handbuch  (werk-steuerung?,titel-kopf, autor?, %elemente.metadaten;,(handbuch-ebene | dokument | io-import | werk-steuerung)+) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST handbuch
		%attr.produkt.req;
		%attr.sprache.opt;
>

<!-- 20070726 ms v2.1 CR 125: autor optional eingefuegt-->
<!-- 20080915 ms v2.6 CR 188: dokument eingefuegt-->
<!-- 20080915 ms v2.6 CR 192: Alternativ zu dokument ein neues Element io-import zur Einbindung von bereits vorhandenen IOs wie z.B. ZVR Vorschriften-IOs-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT entscheidungssammlung  (werk-steuerung?,titel-kopf, autor?, %elemente.metadaten;,(esa-ebene | 
               esa-eintrag | io-import | dokument | werk-steuerung)+) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST entscheidungssammlung
		%attr.produkt.req;
		%attr.sprache.opt;
>
<!-- Inhaltsmodell einer Zeitschrift mit Aufsaetzen  -->

<!-- Entweder die Zeitschrift besteht nur aus einfachen Aufsaetzen oder 
     aus in Rubriken eingeteilten Aufsaetzen.
     
     Der Produktname der Zeitschrift sowie das Jahr sind stets erforderlich, 
     die anderen Attribute dienen zur erweiterten Qualifizierung der Instanz -->

<!-- 20070809 ms v2.1.1 CR 145: statt aufsatz, beitrag etc. werden mit Hilfe von dokument und dessen Attribut href entsprechende Instanzen referenziert-->
<!-- 20080915 ms v2.6 CR 192: Alternativ zu dokument ein neues Element io-import zur Einbindung von bereits vorhandenen IOs wie z.B. ZVR Vorschriften-IOs-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT zeitschrift  (werk-steuerung?,titel-kopf, %elemente.metadaten;,(rubrik | dokument | io-import | werk-steuerung)+) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST zeitschrift
		%attr.produkt.req;
		%attr.jahrgang.opt;
		%attr.heft.opt;
		%attr.datum.opt;
		%attr.sprache.opt;
>

<!-- 20070829 ms v2.2 CR 149: Neues Element lexikon-werk in produkt-import eingefuegt-->
<!-- 20080915 ms v2.6 CR 192: Alternativ zu dokument ein neues Element io-import zur Einbindung von bereits vorhandenen IOs wie z.B. ZVR Vorschriften-IOs-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT lexikon-werk (werk-steuerung?,titel-kopf, autor?, %elemente.metadaten;,(lexikon-werk-ebene | dokument | io-import | werk-steuerung)+) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST lexikon-werk
		%attr.produkt.req;
		%attr.sprache.opt;
>

<!-- 20081029 ms v2.7 CR 221: Neues Element vorschriftensammlung in produkt-import eingefuegt-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT vorschriftensammlung (werk-steuerung?,titel-kopf, autor?, %elemente.metadaten;,(vorschriftensammlung-ebene | dokument | io-import | werk-steuerung)+) >
<!ATTLIST vorschriftensammlung 
		%attr.produkt.req;
		%attr.sprache.opt;
>

<!-- 20070731 ms v2.1 CR 141: In kommentar-ebene, esa-ebene, handbuch-ebene, rubrik werden Blockelemente nach dem Titel-kopf auf optional (mehrfach) gesetzt. -->
<!-- 20070809 ms v2.1.1 CR 145: statt kommentierung, beitrag, etc. werden mit Hilfe von dokument und dessen Attribut href entsprechende Instanzen referenziert-->
<!-- 20080915 ms v2.6 CR 192: Alternativ zu dokument ein neues Element io-import zur Einbindung von bereits vorhandenen IOs wie z.B. ZVR Vorschriften-IOs-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT kommentar-ebene  (werk-steuerung?,titel-kopf,(kommentar-ebene | dokument | io-import | werk-steuerung)*) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST kommentar-ebene
		%attr.wert.opt;
		%attr.bez.opt;		
		%attr.sprache.opt;
>

<!-- 20070809 ms v2.1.1 CR 145: statt kommentierung, beitrag, etc. werden mit Hilfe von dokument und dessen Attribut href entsprechende Instanzen referenziert-->
<!-- 20080915 ms v2.6 CR 192: Alternativ zu dokument ein neues Element io-import zur Einbindung von bereits vorhandenen IOs wie z.B. ZVR Vorschriften-IOs-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT handbuch-ebene  (werk-steuerung?,titel-kopf,(handbuch-ebene |dokument| io-import | werk-steuerung)*) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST handbuch-ebene
		%attr.wert.opt;
		%attr.bez.opt;		
		%attr.sprache.opt;
>

<!-- 20080915 ms v2.6 CR 188: dokument eingefuegt-->
<!-- 20080915 ms v2.6 CR 192: Alternativ zu dokument ein neues Element io-import zur Einbindung von bereits vorhandenen IOs wie z.B. ZVR Vorschriften-IOs-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT esa-ebene  (werk-steuerung?,titel-kopf,(esa-ebene | esa-eintrag | io-import | dokument | werk-steuerung)*) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST esa-ebene
		%attr.wert.opt;
		%attr.bez.opt;		
		%attr.sprache.opt;
>

<!-- 20070809 ms v2.1.1 CR 145: statt aufsatz, beitrag etc. werden mit Hilfe von dokument und dessen Attribut href entsprechende Instanzen referenziert-->
<!-- 20080915 ms v2.6 CR 192: Alternativ zu dokument ein neues Element io-import zur Einbindung von bereits vorhandenen IOs wie z.B. ZVR Vorschriften-IOs-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT rubrik  (werk-steuerung?, titel-kopf,(rubrik |dokument | io-import | werk-steuerung)*) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST rubrik
		%attr.wert.opt;
		%attr.bez.opt;		
		%attr.sprache.opt;
>

<!-- 20080915 ms v2.6 CR 192: Alternativ zu dokument ein neues Element io-import zur Einbindung von bereits vorhandenen IOs wie z.B. ZVR Vorschriften-IOs-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT lexikon-werk-ebene (werk-steuerung?, titel-kopf,(lexikon-werk-ebene |dokument | io-import | werk-steuerung)*) >
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST lexikon-werk-ebene
		%attr.wert.opt;
		%attr.bez.opt;		
		%attr.sprache.opt;
>

<!-- 20081029 ms v2.7 CR 221: Neues Element vorschriftensammlung-ebene in produkt-import eingefuegt-->
<!-- 20081029 ms v2.7 CR 222: werk-steuerung auf der Ebene von dokument mehrfach zugelassen-->
<!ELEMENT vorschriftensammlung-ebene (werk-steuerung?, titel-kopf,(vorschriftensammlung-ebene |dokument | io-import | werk-steuerung)*) >
<!ATTLIST vorschriftensammlung-ebene
		%attr.wert.opt;
		%attr.bez.opt;		
		%attr.sprache.opt;
>

<!ELEMENT dokument   EMPTY >
<!ATTLIST dokument
		href 	CDATA	#REQUIRED>

<!-- 20081111 ms v2.7.1 CR 231: Attribut ioclass auf implied gesetzt statt required-->
<!ELEMENT io-import EMPTY >
<!ATTLIST io-import
               	linkid  CDATA    #REQUIRED
               ioclass  CDATA    #IMPLIED
               ioversion  CDATA    #IMPLIED
		href 	CDATA	#IMPLIED
		dtd 	CDATA   #IMPLIED  >

<!-- ***************************************************************** -->
<!-- Produkt-Export  -->
<!-- ***************************************************************** -->

<!-- 20080915 ms v2.6 CR 197: Fuer den Export von Produktzusammenstellungen werden auch Ebenen ausgegeben, die dort erfasst wurden. Neues Element produkt-ebene-->
<!ELEMENT produkt-export  (produkt-steuerung?, externe-daten?, (produkt-ebene | io)+) >
<!ATTLIST produkt-export
               ausgabe (print | online | offline)   #REQUIRED 
               produkt  CDATA    #REQUIRED 
               typ (kommentar | handbuch | entscheidungssammlung | zeitschrift | lexikon)  #REQUIRED  >

<!--Produktsteuerung: Diese Werksteuerung ueberschreibt die Werksteuerungen in den IO-Dokumenten-->
<!ELEMENT produkt-steuerung  (werk-steuerung*, layout) >

<!ELEMENT io   (io*)  >
<!ATTLIST io
               linkid  CDATA    #REQUIRED 
               ioclass  CDATA    #REQUIRED 
               ioversion  CDATA    #REQUIRED
		href 	CDATA	#REQUIRED
		dtd 	CDATA   #REQUIRED  >

<!ELEMENT externe-daten   (externe-datei+)  >
<!ELEMENT externe-datei 	EMPTY>
<!ATTLIST externe-datei
               name  CDATA    #REQUIRED 
               pfad  CDATA    #REQUIRED  >

<!ELEMENT layout   EMPTY  >
<!ATTLIST layout
               name  CDATA    #REQUIRED 
               pfad  CDATA    #REQUIRED  >

<!-- 20080915 ms v2.6 CR 197: Fuer den Export von Produktzusammenstellungen werden auch Ebenen ausgegeben, die dort erfasst wurden. Neues Element produkt-ebene-->
<!ELEMENT produkt-ebene   (produkt-ebene | io)*>
<!ATTLIST produkt-ebene 		
		%attr.wert.opt;
		%attr.bez.req; >