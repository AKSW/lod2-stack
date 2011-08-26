<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->
<!-- 									-->
<!-- WKDSC VERWALTUNGSANWEISUNG DTD Modul					-->
<!-- 									-->
<!--  WKDSC DTD Version: 2.9 vom 04.08.2009						-->
<!--  letzte Aenderung dieses Moduls: 28.04.2009					-->
<!-- Public Identifier: -//WKD//DTD WKDSC VERWALTUNGSANWEISUNG MODUL v2.9//DE-->
<!-- 									-->
<!-- ******************************************************************	-->
<!-- ******************************************************************	-->

<!-- Inhaltsmodell von Verwaltungsanweisungen und den Ebenen -->

<!ENTITY % elemente.va		"(va-ebene | zwischen-titel | anmerkung | %absatz.komplett; | %elemente.zitat.kein-block;)*" >

<!-- ***************************************************************** -->
<!-- Verwaltungsanweisung -->
<!-- ***************************************************************** -->

<!ELEMENT verwaltungsanweisung 	(zuordnung-produkt?, verbundene-dokumente*, titel-kopf,
                                             		va-metadaten, 
				va-beschlusstext, 
				normenkette?,
				((vz-inhalt-auto, va-vorspann?) | (va-vorspann, vz-inhalt-auto?))?,
			  	%elemente.va;, 
				 anlage*) >

<!ATTLIST verwaltungsanweisung
		%attr.va-herkunft.req;
		%attr.inkraft.opt;
		%attr.ausserkraft.opt;
		%attr.vtext-id.opt;
		%attr.id-typ.opt;
		%attr.sprache.opt;
		%attr.rechteinhaber.opt;
		%attr.bezugsquelle.opt;
>		

<!-- ================================================================= -->
<!-- Allgemeine Metadaten zur Verwaltungsanweisung die hier zusammengefasst werden -->

<!ELEMENT va-metadaten		((gleichlautender-erlass | 
				(behoerde, va-typ, datum, az-gruppe, 
				bmf-doknr?, va-fundstelle?)), 
				 %elemente.metadaten;, stichworte?)>

<!ELEMENT gleichlautender-erlass	(datum, va-fundstelle?, gleichlautender-erlass-land*) >

<!ELEMENT gleichlautender-erlass-land	(behoerde, az-gruppe)>

<!ELEMENT behoerde 	(#PCDATA) >
<!ATTLIST behoerde	%attr.sprache.opt;
			bk	CDATA	#IMPLIED
>

<!ELEMENT va-typ		EMPTY >
<!ATTLIST va-typ		%attr.va-typ.req;
>

<!ELEMENT bmf-doknr	(#PCDATA)>

<!ELEMENT va-fundstelle	(#PCDATA) >
<!ATTLIST va-fundstelle
		%attribute.vs-fundstelle;
>

<!-- ================================================================= -->
<!-- Vorspann zur Aufnahme vorgelagerter Informationen der Verwaltungsanweisungen-->
<!ELEMENT va-vorspann	(zwischen-titel | %absatz.basis; | anmerkung)+ >

<!ATTLIST va-vorspann	%attr.sprache.opt; 
>

<!-- ================================================================= -->
<!-- Zur Erfassung des Beschlusstextes in Verwaltungsanweisungen. Enthält die Urteilszeile, also beispielsweise "BMF-Schreiben vom ..." als Fliesstext. -->
<!ELEMENT va-beschlusstext	(%inline.basis; | %verweis.komplett; )* >
<!ATTLIST va-beschlusstext 		%attr.sprache.opt;
>

<!-- ================================================================= -->
<!-- Rekursion zur Angabe von strukturierten Ebenen in Verwaltungsanweisungen-->
<!ELEMENT va-ebene		(titel-kopf, 
				 stichworte?, 
				 werk-steuerung?,
				 %elemente.va;) >

<!-- wert und bez fuer die Verlinkung auf die Ebenen -->
<!ATTLIST va-ebene
		%attr.bez.opt;
		%attr.wert.opt;
		%attr.sprache.opt;
>
