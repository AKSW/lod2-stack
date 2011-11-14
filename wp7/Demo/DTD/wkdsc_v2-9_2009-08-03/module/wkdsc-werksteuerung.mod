<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC WERKSTEUERUNG DTD Modul					-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 12.03.2008					-->
<!-- Public Identifier: -//WKD//DTD WKDSC WERKSTEUERUNG MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->




<!-- ***************************************************************** -->
<!-- Werksteuerung -->
<!-- ***************************************************************** -->

<!-- 20060717 HE v1.0.0  Umsetzung allgemeines Pkt. 1 -->
<!ELEMENT werk-steuerung		(werk-steuerung-eintrag)+ >

<!ATTLIST werk-steuerung
		%attr.produkt.req;
		ioref	CDATA	#IMPLIED
>

<!ELEMENT werk-steuerung-eintrag	(produkt-eigenschaft | pagina-steuerung | kolumnen-titel-steuerung | kustode | liste-steuerung)+ >

<!-- ================================================================= -->
<!-- Angabe von Eigenschaften des Inhaltes in dem jeweiligen Produkt -->
<!ELEMENT produkt-eigenschaft	(produkt-titel | produkt-ausgabeform) >



<!ELEMENT produkt-ausgabeform	EMPTY >

<!-- 20080312 ms v2.3.5 CR 175: Attribut typ in Entity uebernommen, wird auch von text-auspraegung und container-auspraegung verwendet-->
<!ATTLIST produkt-ausgabeform
		%attr.typ-medien.req;
>


<!-- Angaben zum Produkttitel -->
<!ELEMENT produkt-titel	(#PCDATA)* >

<!ATTLIST produkt-titel
		typ			(titel | kurztitel | abkuerzung) 	#REQUIRED
>


<!-- ================================================================= -->
<!-- Definition der Seitenzahlen -->
<!ELEMENT pagina-steuerung	EMPTY >

<!ATTLIST pagina-steuerung
		typ			(arabisch | alphaklein | alphagross | doppelalpha | roemklein | roemgross) #REQUIRED
		typ-schaltseite		(arabisch | alphaklein | alphagross | doppelalpha | roemklein | roemgross) #IMPLIED
		%attr.start-seite.opt;
		element-neustart		CDATA		#IMPLIED
>


<!-- ================================================================= -->
<!-- Festlegung wie der Kolumnentitel definiert wird -->
<!-- 20070510 ms v2.0 CR 89: kolumen-titel-steuerung umbenannt in kolumnen-titel-steuerung-->
<!-- 20070510 ms v2.0 CR 104: neuer Attributwert linksrechts zu seite in kolumnent-titel-steuerung-->
<!ELEMENT kolumnen-titel-steuerung	(titel | regel+ | pagina-ref) >

<!ATTLIST kolumnen-titel-steuerung
		position		(aussen	| aussenmitte | mitte | innenmitte | innen)	#REQUIRED
		seite			(links | rechts | linksrechts)					#REQUIRED
		typ			(lebend | redaktionell)					#REQUIRED
>


<!-- ================================================================= -->
<!-- Regeldefinition zur Ermittlung lebender Kolumentitel -->
<!ELEMENT regel				EMPTY >

<!ATTLIST regel
		element			CDATA				#REQUIRED
		attribut		CDATA				#IMPLIED
		ebene			CDATA				#REQUIRED
>

<!-- ================================================================= -->
<!-- Verwendung der Seitennummerierung zur Titelgestaltung -->
<!-- 20070112 he v1.2.0 CR 34: Typisierung von pagina-ref um mehrere 
     Informationen in der Produktion ueber diese Referenzierung adressieren 
     zu koennen  -->
<!ELEMENT pagina-ref				EMPTY >

<!ATTLIST pagina-ref
		referenz	(seite| lieferzeile| al)	#REQUIRED
>
<!--
Typbedeutung:
	seite 		= verweist auf die aktuelle Seitennummerierung
	lieferzeile 	= verweist auf den Text der die Lieferung beschreibt
	al		= verweist auf die Nummer der Austauschlieferung
-->

<!-- ================================================================= -->
<!-- Definition Fusszeile -->
<!-- 20070510 ms v2.0 CR 104: neuer Attributwert linksrechts zu seite -->
<!ELEMENT kustode			(titel | regel+ | pagina-ref)+ >

<!ATTLIST kustode
		position		(aussen	| mitte | innen)	#REQUIRED
		seite			(links | rechts | linksrechts)		#REQUIRED
		typ			(lebend | redaktionell)		#REQUIRED
>


<!ELEMENT liste-steuerung		EMPTY >

<!ATTLIST liste-steuerung
		zeichen			CDATA				#REQUIRED
		ebene			CDATA				#REQUIRED
>