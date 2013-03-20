package eu.lod2.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

import eu.lod2.ConfigurationTab;
import eu.lod2.LOD2DemoState;

public class Validation extends CustomComponent {
	
	private LOD2DemoState state;
	private String testDataCubeModel;
	private String testProvenance;
	private String testLinkToDSD;
	private String testCodedProperties;
	private String testCodeLists;
	private String testDSDSpecified;
	private String testLinkToDataSet;
	private ListSelect criteriaList;
	private VerticalLayout validationTab;
	private HorizontalLayout mainContrainer;
	private VerticalLayout criteriaLayout;
	private String testDimensionRange;
	private String testCodesFromCodeLists;
	private String errorMsg;
	
	private void createTestQueries(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("select ?o\nfrom <").append(state.getCurrentGraph()).append(">\n{\n");
		strBuilder.append("  ?o a <http://purl.org/linked-data/cube#Observation>.\n");
		strBuilder.append("}");
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
		strBuilder.append("  ?dataSet a <http://purl.org/linked-data/cube#DataSet> . ");
		strBuilder.append("  OPTIONAL { ");
		strBuilder.append("    ?dataSet <http://purl.org/linked-data/cube#structure> ?struct . \n");
		strBuilder.append("    ?struct a <http://purl.org/linked-data/cube#DataStructureDefinition> . \n");
		strBuilder.append("  } ");
		strBuilder.append("}");
		testLinkToDSD = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("select ?obs ?dataSet \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs a <http://purl.org/linked-data/cube#Observation> . ");
		strBuilder.append("  OPTIONAL { ");
		strBuilder.append("    ?obs <http://purl.org/linked-data/cube#dataSet> ?dataSet . \n");
		strBuilder.append("    ?dataSet a <http://purl.org/linked-data/cube#DataSet> . \n");
		strBuilder.append("  } ");
		strBuilder.append("}");
		testLinkToDataSet = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("select ?dim \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dim a <http://purl.org/linked-data/cube#DimensionProperty> . ");
		strBuilder.append("  FILTER NOT EXISTS { ?dim rdfs:range [] } ");
		strBuilder.append("}");
		testDimensionRange = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		strBuilder.append("select ?dim ?val ?list \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs qb:dataSet ?ds . \n");
		strBuilder.append("  ?ds qb:structure ?dsd . \n");
		strBuilder.append("  ?dsd qb:component ?cs . \n");
		strBuilder.append("  ?cs qb:dimension ?dim . \n");
		strBuilder.append("  ?dim a qb:DimensionProperty . \n");
		strBuilder.append("  ?dim qb:codeList ?list . \n");
		strBuilder.append("  ?list a <http://www.w3.org/2004/02/skos/core#ConceptScheme> . \n");
		strBuilder.append("  ?obs ?dim ?val . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?val a skos:Concept . ?val skos:inScheme ?list . } ");
		strBuilder.append("}");
		testCodesFromCodeLists = strBuilder.toString();
		
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
	}
	
	public Validation(LOD2DemoState state){
		this.state = state;
		
//		String curGraph = state.getCurrentGraph();
//		if (curGraph == null || curGraph.isEmpty())
//			showGraphChooser();
		
		createTestQueries();
		
		// TODO: popup if the graph was not selected already
				
		final String resDataCube = validate(testDataCubeModel);
		final String resProvenance = validate(testProvenance);
		final String resLinkToDSD = validate(testLinkToDSD);
		final String resCodedProperties = validate(testCodedProperties);
		final String resCodeLists = validate(testCodeLists);
		final String resDSDSpecified = validate(testDSDSpecified);
		
		final StringBuilder validationResults = new StringBuilder();
		validationResults.append("<p>These are the validation results: </p><p><ol>");
		validationResults.append("<li>Is the model an RDF Data Cube model? - ").append(resDataCube!=null).append("</li>");
		validationResults.append("<li>Does the model contain provenance information? - ").append(resProvenance!=null).append("</li>");
		validationResults.append("<li>Is link to DSD given? - ").append(resLinkToDSD!=null).append("</li>");
		validationResults.append("<li>Are coded properties specified? - ").append(resCodedProperties!=null).append("</li>");
		validationResults.append("<li>Were code lists defined? - ").append(resCodeLists!=null).append("</li>");
		validationResults.append("<li>Is DSD specified? - ").append(resDSDSpecified!=null).append("</li>");
		validationResults.append("</ol></p>");
		
		mainContrainer = new HorizontalLayout();
		mainContrainer.setSizeFull();
		
		criteriaList = new ListSelect("Validation criteria");
		criteriaList.setNullSelectionAllowed(false);
		final Object itemSummary = criteriaList.addItem();
		criteriaList.setItemCaption(itemSummary, "Summary");
		final Object itemProvenance = criteriaList.addItem();
		criteriaList.setItemCaption(itemProvenance, "Provenance information");
		final Object itemObsLinks = criteriaList.addItem();
		criteriaList.setItemCaption(itemObsLinks, "Observations linked to DataSets");
		final Object itemDataSetLinks = criteriaList.addItem();
		criteriaList.setItemCaption(itemDataSetLinks, "DataSets linked to DSDs");
		final Object itemDimDefined = criteriaList.addItem();
		criteriaList.setItemCaption(itemDimDefined, "Dimensions - codes from code lists");
//		final Object itemCodeLists = criteriaList.addItem();
//		criteriaList.setItemCaption(itemCodeLists, "Cide lists defined for coded properties");
		final Object itemZbz = criteriaList.addItem();
		criteriaList.setItemCaption(itemZbz, "Old view - for debugging/testing");
		
		validationTab = new VerticalLayout();
		validationTab.setMargin(false, false, false, true);
		validationTab.setSpacing(true);
		Label testLbl = new Label("Some content...", Label.CONTENT_XHTML);
		testLbl.setValue(validationResults.toString());
		validationTab.addComponent(testLbl);
		Label text = new Label("Validation", Label.CONTENT_XHTML);
		text.setValue("<p>" + resDataCube + "</p><p>" + resProvenance + "</p><p>" + 
				resLinkToDSD + "</p><p>" + resCodedProperties + "</p><p>" + resCodeLists + "</p><p>" + 
				resDSDSpecified + "</p>");
		validationTab.addComponent(text);
		validationTab.setSizeFull();
		
		mainContrainer.addComponent(criteriaList);
		mainContrainer.setExpandRatio(criteriaList, 0.0f);
		mainContrainer.addComponent(validationTab);
		mainContrainer.setExpandRatio(validationTab, 2.0f);
		
		setCompositionRoot(mainContrainer);
		criteriaList.setImmediate(true);
		criteriaList.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
//				mainContrainer.getWindow().showNotification("LALA");
				Object selectedItem = event.getProperty().getValue();
//				validationTab.removeAllComponents();
				if (selectedItem == itemSummary)
					summary();
				else if (selectedItem == itemProvenance)
					provenance();
				else if (selectedItem == itemObsLinks)
					observationLinks();
				else if (selectedItem == itemDataSetLinks)
					dataSetLinks();
				else if (selectedItem == itemDimDefined)
					dimensionDefinitions();
				else if (selectedItem == itemZbz) {
					validationTab.removeAllComponents();
					Label testLbl = new Label("Some content...", Label.CONTENT_XHTML);
					testLbl.setValue(validationResults.toString());
					validationTab.addComponent(testLbl);
					Label text = new Label("Validation", Label.CONTENT_XHTML);
					text.setValue("<p>" + resDataCube + "</p><p>" + resProvenance + "</p><p>" + 
							resLinkToDSD + "</p><p>" + resCodedProperties + "</p><p>" + resCodeLists + "</p><p>" + 
							resDSDSpecified + "</p>");
					validationTab.addComponent(text);
//					validationTab.setSizeFull();
					showContent();
				}
				else {
					validationTab.removeAllComponents();
					Label testLbl = new Label("Some content...", Label.CONTENT_XHTML);
					testLbl.setValue("Detailed info for the criteria, quick fixes, and pointers to Ontowiki go here");
					validationTab.addComponent(testLbl);
//					validationTab.setSizeFull();
					showContent();
				}
//				mainContrainer.setExpandRatio(criteriaList, 0.0f);
//				mainContrainer.addComponent(validationTab);
//				mainContrainer.setExpandRatio(validationTab, 2.0f);
//				mainContrainer.setSizeFull();
			}
		});
	}
	
	private void showGraphChooser(){
		final Window window = new Window("Choose graph");
		window.setModal(true);
		
		VerticalLayout content = new VerticalLayout();
		content.setSpacing(true);
		content.addComponent(new Label("First you need to select a graph"));
		List<String> graphs = null;
		try {
			graphs = ConfigurationTab.request_graphs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final ComboBox comboGraphs = new ComboBox("Select working graph: ", graphs);
		content.addComponent(comboGraphs);
		Button ok = new Button("OK");
		ok.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String theChosenOne = (String)comboGraphs.getValue();
				if (theChosenOne != null && !theChosenOne.isEmpty()){
					state.setCurrentGraph(theChosenOne);
					state.cGraph.setValue(theChosenOne);
				}
				Validation.this.getWindow().removeWindow(window);
			}
		});
		content.addComponent(ok);
		state.cGraph.getWindow().addWindow(window);
	}
	
	private void summary(){
		validationTab.removeAllComponents();
		Label label = new Label("", Label.CONTENT_XHTML);
		List<String> obsList = getObservations();
		List<String> dsList = getDataSets();
		List<String> dsdList = getDataStructureDefinitions();
		StringBuilder sb = new StringBuilder();
		sb.append("<h2>Summary</h2>");
		sb.append("This page contains summary information about the working graph, i.e. ");
		sb.append("number of observations, data sets, DSDs, dimensions, etc. ");
		sb.append("Therefore, this page only detects if some resources are missing, for more information, e.g. ");
		sb.append(" missing links refer to other validation criteria.");
		sb.append("<p>Summary information: <ul><li>");
		if (obsList.size() == 0) sb.append("ERROR - the graph is missing observations");
		else sb.append("There are ").append(obsList.size()).append(" observations");
		sb.append("</li><li>");
		if (dsList.size() == 0) sb.append("ERROR - the graph is missing data sets");
		else sb.append("There are ").append(dsList.size()).append(" data sets");
		sb.append("</li><li>");
		if (dsdList.size() == 0) sb.append("ERROR - the graph is missing data structure definitions");
		else sb.append("There are ").append(dsdList.size()).append(" data structure definitions");
		sb.append("</li></ul></p>");
		sb.append("<p>TODO: add info about dimensions, maybe include pointers on cubeviz possibilities</p>");
		label.setValue(sb.toString());
		validationTab.addComponent(label);
		showContent();
	}
	
	private void provenance(){
		validationTab.removeAllComponents();
		Label label = new Label("Under construction ...",Label.CONTENT_TEXT);
		validationTab.addComponent(label);
		showContent();
	}
	
	private void observationLinks(){
		validationTab.removeAllComponents();
		TupleQueryResult res = executeTupleQuery(testLinkToDataSet);
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - there are no observations in the graph");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		ArrayList<String> observations = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				if (set.getValue("dataSet") == null)
					observations.add(set.getValue("obs").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		if (observations.size() == 0){
			Label label = new Label();
			label.setValue("All observations have links to data sets");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Below is the list of observations that are not linked to any data set. Click on any of them to get more information and choose a quick solution");
		validationTab.addComponent(label);
		
		final ListSelect listObs = new ListSelect("Observations", observations);
		listObs.setNullSelectionAllowed(false);
		validationTab.addComponent(listObs);
		listObs.setImmediate(true);
		final TextArea details = new TextArea("Details");
		details.setSizeFull();
		validationTab.addComponent(details);
		validationTab.setExpandRatio(details, 2.0f);
		details.setValue("Properties of the selected observation");
		final Button fix = new Button("Quick Fix");
		validationTab.addComponent(fix);
		
		listObs.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				StringBuilder sb = new StringBuilder();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						sb.append("[").append(set.getValue("p").stringValue());
						sb.append(", ").append(set.getValue("o").stringValue()).append("]\n");
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
				details.setValue(sb.toString());
			}
		});
		
		showContent();
	}
	
	private void dataSetLinks(){
		validationTab.removeAllComponents();
		TupleQueryResult res = executeTupleQuery(testLinkToDSD);
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - there are no data sets in the graph");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		ArrayList<String> dataSets = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				if (set.getValue("struct") == null)
					dataSets.add(set.getValue("dataSet").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		if (dataSets.size() == 0){
			Label label = new Label();
			label.setValue("All data sets have links to data sets");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Below is the list of data sets that are not linked to any DSD. Click on any of them to get more information and choose a quick solution");
		validationTab.addComponent(label);
		
		final ListSelect listDataSets= new ListSelect("Data Sets", dataSets);
		listDataSets.setNullSelectionAllowed(false);
		validationTab.addComponent(listDataSets);
		listDataSets.setImmediate(true);
		final TextArea details = new TextArea("Details");
		details.setSizeFull();
		validationTab.addComponent(details);
		validationTab.setExpandRatio(details, 2.0f);
		details.setValue("Properties of the selected data set");
		final Button fix = new Button("Quick Fix");
		validationTab.addComponent(fix);
		
		listDataSets.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				StringBuilder sb = new StringBuilder();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						sb.append("[").append(set.getValue("p").stringValue());
						sb.append(", ").append(set.getValue("o").stringValue()).append("]\n");
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
				details.setValue(sb.toString());
			}
		});
		
		showContent();
	}
	
	private void dimensionDefinitions(){
		validationTab.removeAllComponents();
		final TupleQueryResult res = executeTupleQuery(testCodesFromCodeLists);
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - " + errorMsg);
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, String> map = new HashMap<String, String>();
//		ArrayList<String> values = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				map.put(set.getValue("val").stringValue(), set.getValue("list").stringValue());
//				values.add(set.getValue("val").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		if (map.size() == 0){
			Label label = new Label();
			label.setValue("All values of coded dimensions are linked to the code lists");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Following resources should be of type skos:Concept and linked to the appropriate code list");
		validationTab.addComponent(label);
		
		final ListSelect listValues= new ListSelect("Resources", map.keySet());
		listValues.setNullSelectionAllowed(false);
		validationTab.addComponent(listValues);
		
		Button fix = new Button("Quick Fix");
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String resource = (String)listValues.getValue();
				String codeList = map.get(resource);
				getWindow().addWindow(new QuickFixCodesFromCodeLists(resource, codeList));
			}
		});
		
		showContent();
	}
	
	private void showContent(){
		validationTab.setSizeFull();
		mainContrainer.setExpandRatio(criteriaList, 0.0f);
		mainContrainer.setExpandRatio(validationTab, 2.0f);
		mainContrainer.setSizeFull();
	}
	
	private String validateBoolean(String query){
		StringBuilder res = new StringBuilder("{").append(query).append("}[");
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			BooleanQuery booleanQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, query);
			res.append(booleanQuery.evaluate());
		} catch (RepositoryException e) {
			e.printStackTrace(); res.append(e.getMessage());
		} catch (MalformedQueryException e) {
			e.printStackTrace(); res.append(e.getMessage());
		} catch (QueryEvaluationException e) {
			e.printStackTrace(); res.append(e.getMessage());
		}
		return res.toString();
	}
	
	private TupleQueryResult executeTupleQuery(String query){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
			TupleQueryResult tupleResult = tupleQuery.evaluate();
			return tupleResult;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private TupleQueryResult getResourceProperties(String resource){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			StringBuilder q = new StringBuilder();
			q.append("select ?p ?o from <").append(state.getCurrentGraph()).append("> where { <");
			q.append(resource).append("> ?p ?o . }");
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			return tupleQuery.evaluate();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<String> getObservations(){
		StringBuilder q = new StringBuilder();
		q.append("select ?o from <").append(state.getCurrentGraph());
		q.append("> where { ?o a <http://purl.org/linked-data/cube#Observation> . }");
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			TupleQueryResult result = tupleQuery.evaluate();
			ArrayList<String> list = new ArrayList<String>();
			while (result.hasNext())
				list.add(result.next().getValue("o").stringValue());
			return list;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<String> getDataSets(){
		StringBuilder q = new StringBuilder();
		q.append("select ?ds from <").append(state.getCurrentGraph());
		q.append("> where { ?ds a <http://purl.org/linked-data/cube#DataSet> . }");
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			TupleQueryResult result = tupleQuery.evaluate();
			ArrayList<String> list = new ArrayList<String>();
			while (result.hasNext())
				list.add(result.next().getValue("ds").stringValue());
			return list;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<String> getDataStructureDefinitions(){
		StringBuilder q = new StringBuilder();
		q.append("select ?dsd from <").append(state.getCurrentGraph());
		q.append("> where { ?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> . }");
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			TupleQueryResult result = tupleQuery.evaluate();
			ArrayList<String> list = new ArrayList<String>();
			while (result.hasNext())
				list.add(result.next().getValue("dsd").stringValue());
			return list;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
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
			e.printStackTrace(); res.append(e.getMessage());
		} catch (MalformedQueryException e) {
			e.printStackTrace(); res.append(e.getMessage());
		} catch (QueryEvaluationException e) {
			e.printStackTrace(); res.append(e.getMessage());
		}
		return res.toString();
	}
	
	private void uploadTriples(String triples){
//		getWindow().showNotification("LALA", triples);
		getWindow().showNotification("Upload", triples, Notification.TYPE_ERROR_MESSAGE);
		Repository r = state.getRdfStore();
		try {
			RepositoryConnection con = r.getConnection();
			// TODO: create statements, and call add on the connection object
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private class QuickFixCodesFromCodeLists extends Window {
		public QuickFixCodesFromCodeLists(final String resource, final String codeList){
			this.setCaption("Quick Fix");
			this.setWidth(400, UNITS_PIXELS);
			this.setHeight(300, UNITS_PIXELS);
			VerticalLayout content = new VerticalLayout();
			content.setSizeFull();
			if (resource == null || resource.isEmpty())
				content.addComponent(new Label("You need to select a resource first"));
			else 
				content.addComponent(new Label("If you choose to apply the fix, the selected resource (" +
						resource + ") will be of type skos:Conept and linked to the code list " + codeList +
						"via skos:inScheme property"));
			HorizontalLayout layoutButtons = new HorizontalLayout();
			content.addComponent(layoutButtons);
			content.setComponentAlignment(layoutButtons, Alignment.MIDDLE_CENTER);
			Button btnOK = new Button("OK");
			layoutButtons.addComponent(btnOK);
			Button btnCancel = new Button("Cancel");
			layoutButtons.addComponent(btnCancel);
			
			btnCancel.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					Validation.this.getWindow().removeWindow(QuickFixCodesFromCodeLists.this);
				}
			});
			
			btnOK.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					StringBuilder triples = new StringBuilder();
					triples.append("<").append(resource).append("> a <http://www.w3.org/2004/02/skos/core#Concept> . <");
					triples.append(resource).append("> <http://www.w3.org/2004/02/skos/core#inScheme> <");
					triples.append(codeList).append("> . ");
//					uploadTriples(triples.toString());
					Validation.this.getWindow().removeWindow(QuickFixCodesFromCodeLists.this);
				}
			});
			setContent(content);
		}
	}

}
