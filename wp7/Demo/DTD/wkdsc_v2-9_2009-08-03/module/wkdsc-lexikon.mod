<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC DTD Modul fuer Lexikon	 		-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 15.09.2008				-->
<!-- Public Identifier: -//WKD//DTD WKDSC LEXIKON MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->


<!-- ***************************************************************** -->
<!-- Lexikon -->
<!-- ***************************************************************** -->


<!-- Inhaltsmodell von komhbe und den Ebenen -->
<!-- 20060628 HE v1.0.0 Umsetzung Beitrag 6 und 7 -->
<!ENTITY % elemente.lexikon		"(lexikon-ebene | lexikon-eintrag | anmerkung )*" >


<!-- ================================================================= -->
<!-- Basisdefinition eines Lexikons -->
<!-- 20070112 he v1.2.0 CR 35: Das Attribut fach wird durch das Attribut produkt ersetzt -->
<!-- 20070510 ms v2.0 CR 82: anlage am Ende mehrfach optional erlaubt-->
<!-- 20070726 ms v2.1 CR 130: Attribut rechteinhaber hinzugefuegt-->
<!-- 20070727 ms v2.1 CR 123: Attribut sprache hinzuegefuegt-->
<!-- 20080307 ms v2.3.5 CR 170: Attribut id-typ hinzugefuegt-->
<!ELEMENT lexikon	(zuordnung-produkt?, titel-kopf, 
			 autor?, bearbeiter?, 
			 %elemente.metadaten;,
			 werk-steuerung?,
			 abstract?, 
			 (%elemente.verzeichnis.alle;)*, 
			 %elemente.lexikon;, 
			  anlage* )
>

<!ATTLIST lexikon
		%attr.produkt.opt;
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		%attr.rechteinhaber.opt;
		%attr.sprache.opt;
>


<!-- ================================================================= -->
<!-- rekursive Ebene eines komhbe mit eigenem Titelkopf, Abstract und 
     den bekannten kompletten Absatzelementen -->
<!-- 20070112 he v1.2.0 CR 35: statt nr wird jetzt das Attribut wert zur 
     Angabe des Wertes fuer die Bezeichnung benutzt -->     
<!ELEMENT lexikon-ebene		(titel-kopf, 
				 werk-steuerung?,
				 autor?,
				 abstract?,
				 (vz-inhalt-auto | vz-autor-auto |vz-autor-manuell)*,
				 %elemente.lexikon;)
>

<!-- 20070919 ms v2.3 CR 123: Attribut sprache aufgenommen-->     
<!ATTLIST lexikon-ebene
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.sprache.opt;
>
