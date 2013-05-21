package eu.lod2.stat;

import com.vaadin.ui.AbstractComponent;
import eu.lod2.*;

public class CustomComponentFactory {
	
	public static enum CompType {
		CreateKB,
		ImportCSV,
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
        EditDatasetOW, EditStructureDefOW, EditComponentPropOW, MondecaSPARQLList,
        VisualizeCubeviz,
        MergeDatasets
	}
	
	private LOD2DemoState state;
	
	public CustomComponentFactory(LOD2DemoState state){
		this.state = state;
	}
	
	public AbstractComponent create(CompType type){
		switch (type) {
			case CreateKB: return new OntoWikiPathExtended(state,"/model/create",false); 
			case ImportCSV: return new OntoWikiPathExtended(state,"/csvimport",true); 
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
			case VisualizeCubeviz: return new OntoWikiPathExtended(state, "/cubeviz", true);
            case MergeDatasets: return new MergeDatasets(state);
			default: return null;
		}
	}

}
