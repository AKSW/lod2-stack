<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC TABELLE DTD Modul						-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009				-->
<!--  letzte Aenderung dieses Moduls: 15.09.2008					-->
<!-- Public Identifier: -//WKD//DTD WKDSC TABELLE MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->

<!-- ******************************************************************	-->
<!-- gloable Aenderungen 						-->
<!-- 									-->
<!-- 20070314 sk v1.2.0 Tabellen haben jetzt CALS Tabellen Modell -->
<!-- 20070329 ms v1.2.1 Modifikationen zum CALS Tabellen Modell:
1) Element entrytbl wird entfernt
2) Das Nicht-CALS-Attribut font-size-rel wird tabelle (nicht table)
zugeordnet
3) Das Nicht-CALS-Attribut bgcolor wird table, row, entry zugeordnet
4) Die Nicht-CALS-Attribute line-bottom, line-top, line-left, line-right
werden row und entry zugeordnet (diese Attribute werden nicht mehr colspec
und spanspec zugeordnet).
5) Zu table werden die CALS-Attribute orient, pgwide, shortentry, tabstyle,
tocentry entfernt. Uebrig bleiben hier somit: colsep, rowsep, frame, bgcolor
6) Zu den Attributen rowsep und colsep und rotate werden folgende Werte vorgegeben:
(yes | no | 0 | 1)-->
<!-- 20080407 ms v2.4 CR 183: Wegen barrierefreien Tabellen Attribut rowhead zu colspec und Attribute id/header zu entry eingefuegt-->

<!-- ******************************************************************	-->


<!-- ================================================================= -->
<!-- Rotation der Tabelle oder von einzelnen von Tabelleninhalten -->
<!ENTITY % attr.rotation.opt
		"rotation	(0 | 90 | 180 | 270) '0' "
>

<!-- ================================================================= -->
<!-- Die Skalierung der Schrift ist in Tabellen fuer eine optimale Printdarstellung oftmals erforderlich -->
<!ENTITY % attr.font-size-rel.opt
		"font-size-rel (x-small | small | normal | large | x-large)		#IMPLIED"
>

<!-- ================================================================= -->
<!-- Hintergrundfarbe nach der HTML Benennung -->
<!ENTITY % attr.bgcolor.opt
		"bgcolor	CDATA		#IMPLIED"
>

<!-- ================================================================= -->
<!-- Pro Zelle oder Zeile kann jede Seite mit einer Liniendefinition versehen werden -->
<!ENTITY % attribute.tab-linien
		"line-left	CDATA		#IMPLIED
		 line-right	CDATA		#IMPLIED
		 line-top	CDATA		#IMPLIED
		 line-bottom	CDATA		#IMPLIED"
>

<!-- ================================================================= -->
<!-- Definition des Inhaltsmodells von entry um dies Modulspezifisch ueberschreiben zu koennen  -->
<!-- 20070530 ms v1.2.3 CR 114: Element HR temporaer eingefuegt fuer Linien in Tabellen (nur fuer Migration, soll danach wieder entfernt werden-->

<!ENTITY % elemente.entry
		"absatz | absatz-rechts | abbildung-block | objekt-block | liste | liste-auto | hr"
>

<!ELEMENT hr	EMPTY>

<!-- ================================================================= -->
<!-- 0 und 1 oder andere Angaben bei Ja Nein Angaben -->
<!-- 20070606 ms v2.0.0 CR 115: Attributwerte yes und no entfernt, weil nicht eindeutig. 0 als default eingesetzt-->
<!-- 20070809 ms v2.1.1 CR 144: Default von 0 wieder entfernt, weil dieser sonst weiter oben definierte Einstellungen ueberschreiben kann -->
<!ENTITY % yesorno 	"(0 | 1)" >

<!-- ================================================================= -->
<!-- 20070516 ms v1.2.3 CR 113: Attibut tab-breite zu Element tabelle mit 4 Werten (ohne, schmal, normal, breit) wobei ohne der Defaultwert ist--> 
<!-- 20080425 ms v2.5 CR 191: Elemente kennung, titel, alt-text insbesondere wegen Barrierefreiheit eingefuegt-->
<!ELEMENT tabelle (kennung?, titel?, beschriftung?, quelle?, table, alt-text?) >

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST tabelle
		tab-nr	 	CDATA 	    #IMPLIED
		tab-breite		(ohne | schmal | normal | breit) "ohne"
		%attr.font-size-rel.opt;
		%attr.rotation.opt;
		%attr.sprache.opt;
>
<!-- 20080415 ms CR 185: tgroup in table mehrfach zugelassen (entspricht dem CALS Modell)-->
<!ELEMENT table  (tgroup+) >

<!ATTLIST table
                                frame           (top|bottom|topbot|all|sides|none)     #IMPLIED
                                colsep          %yesorno;                                     #IMPLIED
                                rowsep          %yesorno;                                     #IMPLIED
		%attr.bgcolor.opt;
>

<!ELEMENT tgroup (colspec*,spanspec*,thead?,tfoot?,tbody) >

<!ATTLIST tgroup
                                cols            CDATA                                           #REQUIRED
                                tgroupstyle CDATA                                          #IMPLIED
                                colsep          %yesorno;                                  #IMPLIED
                                rowsep          %yesorno;                                 #IMPLIED
                                align           (left|right|center|justify|char)      #IMPLIED
                                char            CDATA                                           #IMPLIED
                                charoff         CDATA                                         #IMPLIED
>

<!ELEMENT colspec EMPTY >

<!-- 20080407 ms v2.4 CR 183: Wegen barrierefreien Tabellen Attribut rowhead eingefuegt-->
<!ATTLIST colspec
                                colnum          CDATA                                    #IMPLIED
                                colname         CDATA                                   #IMPLIED
                                colwidth        CDATA                                     #IMPLIED
                                colsep          %yesorno;                               #IMPLIED
                                rowsep          %yesorno;                              #IMPLIED
                                align           (left|right|center|justify|char)   #IMPLIED
                                char            CDATA                                        #IMPLIED
                                charoff         CDATA                                       #IMPLIED
		rowhead	  (ja | nein)		    #IMPLIED
>

<!ELEMENT spanspec EMPTY >

<!ATTLIST spanspec
                                namest          CDATA                                    #REQUIRED
                                nameend         CDATA                                 #REQUIRED
                                spanname        CDATA                                #REQUIRED
                                colsep          %yesorno;                               #IMPLIED
                                rowsep          %yesorno;                              #IMPLIED
                                align           (left|right|center|justify|char)   #IMPLIED
                                char            CDATA                                        #IMPLIED
                                charoff         CDATA                                       #IMPLIED
>

<!ELEMENT thead (colspec*,row+)>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST thead
                                valign          (top|middle|bottom)                     #IMPLIED
		%attr.sprache.opt;
>

<!ELEMENT tfoot (colspec*,row+)>
<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST tfoot
                                valign          (top|middle|bottom)                     #IMPLIED
		%attr.sprache.opt;
>

<!ELEMENT tbody (row+)>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST tbody
                               valign          (top|middle|bottom)                     #IMPLIED
		%attr.sprache.opt;
>

<!ELEMENT row (entry)+>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST row
                                rowsep          %yesorno;                               #IMPLIED
                                valign          (top|middle|bottom)                     #IMPLIED
		%attr.bgcolor.opt;
		%attribute.tab-linien;
		%attr.sprache.opt;
>


<!ELEMENT entry (%elemente.entry;)*>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!-- 20080407 ms v2.4 CR 183: Wegen barrierefreien Tabellen Attribute id + headers eingefuegt-->
<!-- 20080425 ms v2.5 CR 191: Wegen barrierefreien Tabellen Attribut abbr eingefuegt-->
<!ATTLIST entry
                                colname         CDATA                                      #IMPLIED
                                namest          CDATA                                       #IMPLIED
                                nameend         CDATA                                    #IMPLIED
                                spanname        CDATA                                   #IMPLIED
                                morerows        CDATA                                    #IMPLIED
                                colsep          %yesorno;                                  #IMPLIED
                                rowsep          %yesorno;                                 #IMPLIED
                                align           (left|right|center|justify|char)      #IMPLIED
                                char            CDATA                                           #IMPLIED
                                charoff         CDATA                                          #IMPLIED
                                rotate          %yesorno;                                     #IMPLIED
                                valign          (top|middle|bottom)                    #IMPLIED
		%attr.bgcolor.opt;
		%attribute.tab-linien;
		%attr.sprache.opt;
		id	CDATA			#IMPLIED
		headers	CDATA			#IMPLIED
		abbr	CDATA			#IMPLIED
>

