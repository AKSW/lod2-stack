package eu.lod2.stat;

import com.vaadin.ui.CustomComponent;

import eu.lod2.CKAN;
import eu.lod2.D2RCordis;
import eu.lod2.DBpedia;
import eu.lod2.ELoadRDFFile;
import eu.lod2.EXML;
import eu.lod2.EXMLExtended;
import eu.lod2.GeoSpatial;
import eu.lod2.LOD2DemoState;
import eu.lod2.LODCloud;
import eu.lod2.Limes;
import eu.lod2.LinkingTab;
import eu.lod2.Lodrefine;
import eu.lod2.MondecaSPARQLList;
import eu.lod2.OnlinePoolParty;
import eu.lod2.OntoWikiQuery;
import eu.lod2.SPARQLPoolParty;
import eu.lod2.SameAsLinking;
import eu.lod2.Sparqled;
import eu.lod2.SparqledManager;
import eu.lod2.VirtuosoISPARQL;
import eu.lod2.VirtuosoSPARQL;

public class CustomComponentFactory {
	
	public static enum CompType {
		CreateKB,
		ImportCSV,
		Validation,
		UploadRDF,
		ExtractFromXML,
		ExtractFromXMLExtended,
		D2R,
		// local sparql
		Sparqled,
		SparqledManager,
		SparqlOW,
		SparqlVirtuoso,
		SparqlIVirtuoso,
		// exploration
		EditWithOW,
		OnlinePoolParty,
		CKAN,
		GeoSpatial,
		// interlinking components
		Silk,
		LodRefine,
		Limes,
		SameAs,
		// online sparql endpoints
		LODCloud,
		DBPedia,
		SPARQLPoolParty,
		MondecaSPARQLList
	}
	
	private LOD2DemoState state;
	
	public CustomComponentFactory(LOD2DemoState state){
		this.state = state;
	}
	
	public CustomComponent create(CompType type){
		switch (type) {
			case CreateKB: return new OntoWikiPathExtended(state,"/model/create",false); 
			case ImportCSV: return new OntoWikiPathExtended(state,"/csvimport",true); 
			case Validation: return new Validation(state);
			case UploadRDF: return new ELoadRDFFile(state);
			case ExtractFromXML: return new EXML(state);
			case ExtractFromXMLExtended: return new EXMLExtended(state);
			case D2R: return new D2RCordis(state);
			case Sparqled: return new Sparqled(state);
			case SparqledManager: return new SparqledManager(state);
			case SparqlOW: return new OntoWikiQuery(state);
			case SparqlVirtuoso: return new VirtuosoSPARQL(state);
			case SparqlIVirtuoso: return new VirtuosoISPARQL(state);
			case EditWithOW: return new OntoWikiPathExtended(state,"/model/info",true);
			case OnlinePoolParty: return new OnlinePoolParty(state);
			case CKAN: return new CKAN(state);
			case GeoSpatial: return new GeoSpatial(state);
			case Silk: return new LinkingTab(state);
			case LodRefine: return new Lodrefine(state);
			case Limes: return new Limes(state);
			case SameAs: return new SameAsLinking(state);
			case LODCloud: return new LODCloud(state);
			case DBPedia: return new DBpedia(state);
			case SPARQLPoolParty: return new SPARQLPoolParty(state);
			case MondecaSPARQLList: return new MondecaSPARQLList(state);
			default: return null;
		}
	}

}
