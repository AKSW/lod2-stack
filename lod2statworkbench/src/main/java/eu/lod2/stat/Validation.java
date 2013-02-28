package eu.lod2.stat;

import java.util.Iterator;

import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eu.lod2.LOD2DemoState;

public class Validation extends CustomComponent {
	
	private LOD2DemoState state;
	private String testDataCubeModel;
	private String testProvenance;
	private String testLinkToDSD;
	private String testCodedProperties;
	private String testCodeLists;
	private String testDSDSpecified;
	
	public Validation(LOD2DemoState state){
		this.state = state;
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("select ?d ?o\nfrom <").append(state.getCurrentGraph()).append(">\n{\n");
		strBuilder.append("  ?d a <http://purl.org/linked-data/cube#DataSet>.\n");
		strBuilder.append("  ?o a <http://purl.org/linked-data/cube#Observation>.\n");
		strBuilder.append("} limit 1");
		testDataCubeModel = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("select ?sub ?pub ?cre \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  [] <http://purl.org/dc/terms/subject> ?sub . \n");
		strBuilder.append("  [] <http://purl.org/dc/elements/1.1/publisher> ?pub . \n");
		strBuilder.append("  [] <http://purl.org/dc/elements/1.1/creator> ?cre . \n}");
		testProvenance = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("select ?dataSet ?struct \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dataSet <http://purl.org/linked-data/cube#structure> ?struct . \n");
		strBuilder.append("} limit 1");
		testLinkToDSD = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("select ?property \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  { ?property a  <http://purl.org/linked-data/cube#DimensionProperty> } union \n");
		strBuilder.append("  { ?property a  <http://purl.org/linked-data/cube#ComponentProperty> } union \n");
		strBuilder.append("  { ?property a  <http://purl.org/linked-data/cube#AttributeProperty> } union \n");
		strBuilder.append("  { ?property a  <http://purl.org/linked-data/cube#MeasureProperty> } \n");
		strBuilder.append("} limit 1");
		testCodedProperties = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("select ?codeList \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  [] <http://purl.org/linked-data/cube#codeList> ?codeList . \n}");
		testCodeLists = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("select ?dsdDefinition \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dsdDefinition a <http://purl.org/linked-data/cube#DataStructureDefinition> . \n}");
		testDSDSpecified = strBuilder.toString();
		
//		strBuilder = new StringBuilder("ASK FROM <http://elpo.stat.gov.rs/lod2/demo> { ?s ?p ?o . }");
//		strBuilder.append("ask from <").append(state.getCurrentGraph()).append("> { ?s ?p ?o }");
//		strBuilder.append("  ?s ?p ?o. \n");
//		strBuilder.append("  [] a <http://purl.org/linked-data/cube#Observation>. \n");
//		strBuilder.append("}");
//		String boolStr = strBuilder.toString();
		
		String resDataCube = validate(testDataCubeModel);
		String resProvenance = validate(testProvenance);
		String resLinkToDSD = validate(testLinkToDSD);
		String resCodedProperties = validate(testCodedProperties);
		String resCodeLists = validate(testCodeLists);
		String resDSDSpecified = validate(testDSDSpecified);
		
		StringBuilder validationResults = new StringBuilder();
		validationResults.append("<p>These are the validation results: </p><p><ol>");
		validationResults.append("<li>Is the model an RDF Data Cube model? - ").append(resDataCube!=null).append("</li>");
		validationResults.append("<li>Does the model contain provenance information? - ").append(resProvenance!=null).append("</li>");
		validationResults.append("<li>Is link to DSD given? - ").append(resLinkToDSD!=null).append("</li>");
		validationResults.append("<li>Are coded properties specified? - ").append(resCodedProperties!=null).append("</li>");
		validationResults.append("<li>Were code lists defined? - ").append(resCodeLists!=null).append("</li>");
		validationResults.append("<li>Is DSD specified? - ").append(resDSDSpecified!=null).append("</li>");
		validationResults.append("</ol></p>");
		
		VerticalLayout validationTab = new VerticalLayout();
		Label testLbl = new Label("Some content...", Label.CONTENT_XHTML);
		testLbl.setValue(validationResults.toString());
		validationTab.addComponent(testLbl);
		Label text = new Label("Validation", Label.CONTENT_XHTML);
		text.setValue("<p>" + resDataCube + "</p><p>" + resProvenance + "</p><p>" + 
				resLinkToDSD + "</p><p>" + resCodedProperties + "</p><p>" + resCodeLists + "</p><p>" + 
				resDSDSpecified + "</p>");
		validationTab.addComponent(text);
		setCompositionRoot(validationTab);
	}
	
	private String validateBoolean(String query){
		StringBuilder res = new StringBuilder("{").append(query).append("}[");
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			BooleanQuery booleanQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, query);
			res.append(booleanQuery.evaluate());
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); res.append(e.getMessage());
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); res.append(e.getMessage());
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); res.append(e.getMessage());
		}
		return res.toString();
	}
	
	private String validate(String query){
		StringBuilder res = new StringBuilder();
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
			TupleQueryResult tupleResult = tupleQuery.evaluate();
			if (!tupleResult.hasNext()) return null;
			while (tupleResult.hasNext()){
				BindingSet set = tupleResult.next();
				Iterator<Binding> it = set.iterator();
				res.append("[");
				while (it.hasNext()){
					Binding binding = it.next();
					res.append(binding.getName()).append(":").append(binding.getValue().stringValue());
					res.append(", ");
				} 
				res.replace(res.length()-2, res.length(), "]<br>");
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); res.append(e.getMessage());
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); res.append(e.getMessage());
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); res.append(e.getMessage());
		}
		return res.toString();
	}

}
