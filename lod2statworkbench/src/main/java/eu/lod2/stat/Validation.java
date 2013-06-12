package eu.lod2.stat;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import eu.lod2.ConfigurationTab;
import eu.lod2.LOD2DemoState;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Validation extends CustomComponent implements LOD2DemoState.CurrentGraphListener {
	
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
	private String testNoDuplicateObservations;
	private String testDimensionsRequired;
	private Panel validationPanel;
	private String testMeasuresInDSD;
	private String testDimensionsHaveRange;
	private AbstractLayout target;
	private String testSliceKeysDeclared;
	private String testSliceKeysConsistentWithDSD;
	private String testSliceStructureUnique;
	private String testSliceDimensionsComplete;
	
	private void createTestQueries(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("select ?o\nfrom <").append(state.getCurrentGraph()).append(">\n{\n");
		strBuilder.append("  ?o a <http://purl.org/linked-data/cube#Observation>.\n");
		strBuilder.append("}");
		testDataCubeModel = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("prefix dct: <http://purl.org/dc/terms/> \n");
		strBuilder.append("select ?ds ?label ?comment ?title ?description ?issued ?modified ?subject ?publisher ?licence \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?ds a qb:DataSet . \n");
		strBuilder.append("  OPTIONAL { ?ds rdfs:label ?label . } \n");
		strBuilder.append("  OPTIONAL { ?ds rdfs:comment ?comment . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:title ?title . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:description ?description . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:issued ?issued . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:modified ?modified . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:subject ?subject . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:publisher ?publisher . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:licence ?licence . } \n");
		strBuilder.append("}");
		testProvenance = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("select ?dataSet (count(?struct) as ?dsdNum) \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dataSet a <http://purl.org/linked-data/cube#DataSet> . ");
		strBuilder.append("  OPTIONAL { ");
		strBuilder.append("    ?dataSet <http://purl.org/linked-data/cube#structure> ?struct . \n");
		strBuilder.append("    ?struct a <http://purl.org/linked-data/cube#DataStructureDefinition> . \n");
		strBuilder.append("  } ");
		strBuilder.append("} group by ?dataSet having (count(?struct) != 1)");
		testLinkToDSD = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("select ?obs (count(?dataSet) as ?dsNum) \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs a <http://purl.org/linked-data/cube#Observation> . ");
		strBuilder.append("  OPTIONAL { ");
		strBuilder.append("    ?obs <http://purl.org/linked-data/cube#dataSet> ?dataSet . \n");
		strBuilder.append("    ?dataSet a <http://purl.org/linked-data/cube#DataSet> . \n");
		strBuilder.append("  } ");
		strBuilder.append("} group by ?obs having (count(?dataSet) != 1)");
		testLinkToDataSet = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?dsd \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dsd a qb:DataStructureDefinition . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?dsd qb:component ?cs . ?cs qb:measure [] . } \n");
		strBuilder.append("}");
		testMeasuresInDSD = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?dim \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dim a qb:DimensionProperty . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?dim rdfs:range [] . } \n");
		strBuilder.append("}");
		testDimensionsHaveRange = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("select ?dim \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dim a <http://purl.org/linked-data/cube#DimensionProperty> . ");
		strBuilder.append("  FILTER NOT EXISTS { ?dim rdfs:range [] } ");
		strBuilder.append("}");
		testDimensionRange = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?sliceKey \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?sliceKey a qb:SliceKey . \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?dsd a qb:DataStructureDefinition . \n");
		strBuilder.append("    ?dsd qb:sliceKey ?sliceKey . \n");
		strBuilder.append("  } \n");
		strBuilder.append("}");
		testSliceKeysDeclared = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?sliceKey \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?sliceKey a qb:SliceKey . \n");
		strBuilder.append("  ?sliceKey qb:componentProperty ?prop . \n");
		strBuilder.append("  ?dsd qb:sliceKey ?sliceKey . \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?dsd qb:component ?cs . \n");
		strBuilder.append("    ?cs qb:dimension ?prop . \n");
		strBuilder.append("  } \n");
		strBuilder.append("}");
		testSliceKeysConsistentWithDSD = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select distinct ?slice \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  { \n");
		strBuilder.append("    ?slice a qb:Slice . \n");
		strBuilder.append("    FILTER NOT EXISTS { ?slice qb:sliceStructure ?key } \n");
		strBuilder.append("  } UNION { \n");
		strBuilder.append("    ?slice a qb:Slice . \n");
		strBuilder.append("    ?slice qb:sliceStructure ?key1 . \n");
		strBuilder.append("    ?slice qb:sliceStructure ?key2 . \n");
		strBuilder.append("    FILTER (?key1 != ?key2) \n");
		strBuilder.append("  } \n");
		strBuilder.append("}");
		testSliceStructureUnique = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?slice ?dim \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?slice qb:sliceStructure ?key . \n");
		strBuilder.append("  ?key qb:componentProperty ?dim . \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?slice ?dim ?val . \n");
		strBuilder.append("  } \n");
		strBuilder.append("} order by ?slice");
		testSliceDimensionsComplete = strBuilder.toString();
		
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
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		strBuilder.append("select distinct ?obs \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs qb:dataSet ?ds . \n");
		strBuilder.append("  ?ds qb:structure ?dsd . \n");
		strBuilder.append("  ?dsd qb:component ?cs . \n");
		strBuilder.append("  ?cs qb:dimension ?dim . \n");
		strBuilder.append("  ?dim a qb:DimensionProperty . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?obs ?dim [] } \n");
		strBuilder.append("}");
		testDimensionsRequired = strBuilder.toString();
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		strBuilder.append("select distinct ?obs1 ?obs2 \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs1 qb:dataSet ?dataSet . \n");
		strBuilder.append("  ?obs2 qb:dataSet ?dataSet . \n");
		strBuilder.append("  FILTER (?obs1 != ?obs2) \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?dataSet qb:structure ?dsd . \n");
		strBuilder.append("    ?dsd qb:component ?cs . \n");
		strBuilder.append("    ?cs qb:dimension ?dim . \n");
		strBuilder.append("    ?dim a qb:DimensionProperty . \n");
		strBuilder.append("    ?obs1 ?dim ?val1 . \n");
		strBuilder.append("    ?obs2 ?dim ?val2 . \n");
		strBuilder.append("    FILTER (?val1 != ?val2) \n");
		strBuilder.append("  } \n");
		strBuilder.append("} order by ?obs1");
		testNoDuplicateObservations = strBuilder.toString();
	}
	
	public Validation(LOD2DemoState state, AbstractLayout target){
		this.state = state;
		this.target = target;
		mainContrainer = new HorizontalLayout();
		mainContrainer.setSizeUndefined();
		mainContrainer.setSpacing(true);
		setCompositionRoot(mainContrainer);
	}
	
	private void refresh(){
		mainContrainer.removeAllComponents();
		String currentGraph = state.getCurrentGraph();
	    if (currentGraph == null || currentGraph.isEmpty()){
	    	VerticalLayout l = new VerticalLayout();
	    	l.setSizeFull();
	    	mainContrainer.addComponent(l);
	        Label message=new Label("No graph is currently selected. You can select one below:");
	        l.addComponent(message);
	        l.setExpandRatio(message, 0.0f);
	        ConfigurationTab config=new ConfigurationTab(this.state);
	        l.addComponent(config);
	        l.setExpandRatio(config, 2.0f);
	        l.setComponentAlignment(message,Alignment.TOP_LEFT);
	        l.setComponentAlignment(config,Alignment.TOP_LEFT);
	
	        return;
	    }
	    
	    createTestQueries();
	    createGUI();
	}
	
	private void createGUI(){
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
		final Object itemMeasuresInDSDs = criteriaList.addItem();
		criteriaList.setItemCaption(itemMeasuresInDSDs, "Measures in DSDs");
		final Object itemDimensionsHaveRange = criteriaList.addItem();
		criteriaList.setItemCaption(itemDimensionsHaveRange, "Dimensions have range");
		final Object itemDimDefined = criteriaList.addItem();
		criteriaList.setItemCaption(itemDimDefined, "Dimensions - codes from code lists");
		final Object itemSliceKeysDeclared = criteriaList.addItem();
		criteriaList.setItemCaption(itemSliceKeysDeclared, "Slice keys declared");
		final Object itemSliceKeysConsistent = criteriaList.addItem();
		criteriaList.setItemCaption(itemSliceKeysConsistent, "Slice keys consistent");
		final Object itemSliceStructureUnique = criteriaList.addItem();
		criteriaList.setItemCaption(itemSliceStructureUnique, "Slice structure unique");
		final Object itemSliceDimensionsComplete = criteriaList.addItem();
		criteriaList.setItemCaption(itemSliceDimensionsComplete, "Slice dimensions complete");
		final Object itemDimReq = criteriaList.addItem();
		criteriaList.setItemCaption(itemDimReq, "All dimensions required");
		final Object itemObsUnique = criteriaList.addItem();
		criteriaList.setItemCaption(itemObsUnique, "No duplicate observations");
		
		validationTab = new VerticalLayout();
		validationTab.setMargin(false);
		validationTab.setSpacing(true);
		validationPanel = new Panel(validationTab);
		validationPanel.setSizeFull();
		validationPanel.setScrollable(true);
		
		mainContrainer.addComponent(criteriaList);
		mainContrainer.setExpandRatio(criteriaList, 0.0f);
		mainContrainer.addComponent(validationPanel);
		mainContrainer.setExpandRatio(validationPanel, 2.0f);
		
		criteriaList.setImmediate(true);
		criteriaList.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object selectedItem = event.getProperty().getValue();
				if (selectedItem == itemSummary)
					summary();
				else if (selectedItem == itemProvenance)
					provenance();
				else if (selectedItem == itemObsLinks)
					observationLinks();
				else if (selectedItem == itemDataSetLinks)
					dataSetLinks();
				else if (selectedItem == itemMeasuresInDSDs)
					measuresInDSD();
				else if (selectedItem == itemDimensionsHaveRange)
					dimensionsHaveRange();
				else if (selectedItem == itemSliceKeysDeclared)
					sliceKeysDeclared();
				else if (selectedItem == itemSliceKeysConsistent)
					sliceKeysConsistentWithDSD();
				else if (selectedItem == itemSliceStructureUnique)
					sliceStructureUnique();
				else if (selectedItem == itemSliceDimensionsComplete)
					sliceDimensionsComplete();
				else if (selectedItem == itemDimDefined)
					dimensionDefinitions();
				else if (selectedItem == itemDimReq)
					dimensionsRequired();
				else if (selectedItem == itemObsUnique)
					noDuplicateObs();
				else {
					summary();
				}
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
			graphs = ConfigurationTab.request_graphs(state);
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
	
	private void slice(){
		validationTab.removeAllComponents();
		CreateSlices cs = new CreateSlices(state);
		validationTab.addComponent(cs);
		cs.render();
		validationTab.setExpandRatio(cs, 2.0f);
		showContent();
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
		final String [] metaProps = new String [] {
				"rdfs:label",
				"rdfs:comment",
				"dct:title",
				"dct:description",
				"dct:issued",
				"dct:modified",
				"dct:subject",
				"dct:publisher",
				"dct:licence"
		};
		
		final Label label = new Label("It is recommended to mark datasets with metadata tu support discovery, presentation and processing. Choose a dataset below and check the values for recommended core set of metadata",Label.CONTENT_TEXT);
		validationTab.addComponent(label);
		
		TupleQueryResult res = executeTupleQuery(testProvenance);
		if (res == null) {
			label.setValue("ERROR");
			showContent();
			return;
		}
		final HashMap<String, ArrayList<Value>> map = new HashMap<String, ArrayList<Value>>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				String ds = set.getValue("ds").stringValue();
				ArrayList<Value> values = new ArrayList<Value>(9);
				values.add(set.getValue("label"));
				values.add(set.getValue("comment"));
				values.add(set.getValue("title"));
				values.add(set.getValue("description"));
				values.add(set.getValue("issued"));
				values.add(set.getValue("modified"));
				values.add(set.getValue("subject"));
				values.add(set.getValue("publisher"));
				values.add(set.getValue("licence"));
				map.put(ds, values);
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		final ComboBox combo = new ComboBox("Choose dataset", map.keySet());
		combo.setWidth("100%");
		combo.setNullSelectionAllowed(false);
		combo.setImmediate(true);
		validationTab.addComponent(combo);
		
		final Table table = new Table("Metadata of the chosen dataset");
		table.setWidth("100%");
		table.addContainerProperty("Property", String.class, null);
		table.addContainerProperty("Value", Value.class, null);
		validationTab.addComponent(table);
		
		Button editInOW = new Button("Edit in OntoWiki");
		validationTab.addComponent(editInOW);
		validationTab.setExpandRatio(editInOW, 2.0f);
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)combo.getValue());
			}
		});
		
		combo.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				ArrayList<Value> list = map.get((String)event.getProperty().getValue());
				table.removeAllItems();
				for (int i=0; i<metaProps.length; i++)
					table.addItem(new Object [] { metaProps[i], list.get(i) }, i);
			}
		});
		
		showContent();
	}
	
	private void observationLinks(){
		validationTab.removeAllComponents();
		TupleQueryResult res = executeTupleQuery(testLinkToDataSet);
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, String> map = new HashMap<String, String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				map.put(set.getValue("obs").stringValue(), set.getValue("dsNum").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		if (map.size() == 0){
			Label label = new Label();
			label.setValue("All observations have links to data sets");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Below is the list of observations that are not linked to exactly one data set. Click on any of them to get more information and either edit the resource in OntoWiki or choose a quick solution");
		validationTab.addComponent(label);
		
		final ListSelect listObs = new ListSelect("Observations", map.keySet());
		listObs.setNullSelectionAllowed(false);
		validationTab.addComponent(listObs);
		listObs.setImmediate(true);
		listObs.setWidth("100%");
		
		final Table detailsTable = new Table("Details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		final Label lblProblem = new Label("<b>Problem description: </b>", Label.CONTENT_XHTML);
		validationTab.addComponent(lblProblem);
		
		Button editInOW = new Button("Edit in OntoWiki");
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listObs.getValue());
			}
		});
		
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		panelLayout.addComponent(new Label("After the fix the selected observation will belong only to the data set selected below or you can choose to edit the selected observation manually in OntoWiki"));
		final ComboBox comboDataSets = new ComboBox(null, getDataSets());
		comboDataSets.setNullSelectionAllowed(false);
		comboDataSets.setWidth("100%");
		panelLayout.addComponent(comboDataSets);
		final Button fix = new Button("Quick Fix");
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		buttonsLayout.addComponent(fix);
		buttonsLayout.addComponent(editInOW);
		panelLayout.addComponent(buttonsLayout);
		panelLayout.setExpandRatio(buttonsLayout, 2.0f);
		
		listObs.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
				String chosenObs = (String)event.getProperty().getValue();
				lblProblem.setValue("<b>Problem description: </b>The selected observation belongs to " + map.get(chosenObs) +
						" data sets. It should belong to exactly one.");
			}
		});
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String chosenDataSet = (String)comboDataSets.getValue();
				String observation = (String)listObs.getValue();
				
				if (chosenDataSet == null) {
					getWindow().showNotification("DataSet was not selected", Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				if (observation == null) {
					getWindow().showNotification("Observation was not selected", Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				
				String dataSetProp = "http://purl.org/linked-data/cube#dataSet";
				List<String> forRemoval = getObsDataSets(observation);
				if (forRemoval.size()>0){
					ArrayList<Statement> stmts = new ArrayList<Statement>();
					for (String ds: forRemoval)
						stmts.add(getStatementFromUris(observation, dataSetProp, ds));
					removeStatements(stmts);
				}
				ArrayList<Statement> addStmts = new ArrayList<Statement>();
				addStmts.add(getStatementFromUris(observation, dataSetProp, chosenDataSet));
				uploadStatements(addStmts);
				observationLinks();
			}
		});
		
		showContent();
	}
	
	private void dataSetLinks(){
		validationTab.removeAllComponents();
		TupleQueryResult res = executeTupleQuery(testLinkToDSD);
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, String> map = new HashMap<String, String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				map.put(set.getValue("dataSet").stringValue(), set.getValue("dsdNum").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		if (map.size() == 0){
			Label label = new Label();
			label.setValue("All data sets have exactly one link to the DSD");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Below is the list of data sets that are not linked to exactly one DSD. Click on any of them to get more information and either edit the data set in OntoWiki or choose a quick solution");
		validationTab.addComponent(label);
		
		final ListSelect listDataSets= new ListSelect("Data Sets", map.keySet());
		listDataSets.setNullSelectionAllowed(false);
		validationTab.addComponent(listDataSets);
		listDataSets.setImmediate(true);
		listDataSets.setWidth("100%");
		
		final Table detailsTable = new Table("Details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		final Label lblProblem = new Label("<b>Problem description: </b>", Label.CONTENT_XHTML);
		validationTab.addComponent(lblProblem);
		
		Button editInOW = new Button("Edit in OntoWiki");
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listDataSets.getValue());
			}
		});
		
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		panelLayout.addComponent(new Label("After the fix the selected data sets will link only to the DSD selected below or you can choose to edit the selected data set manually in OntoWiki"));
		final ComboBox comboDSDs = new ComboBox(null, getDataStructureDefinitions());
		comboDSDs.setNullSelectionAllowed(false);
		comboDSDs.setWidth("100%");
		panelLayout.addComponent(comboDSDs);
		final Button fix = new Button("Quick Fix");
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		buttonsLayout.addComponent(fix);
		buttonsLayout.addComponent(editInOW);
		panelLayout.addComponent(buttonsLayout);
		panelLayout.setExpandRatio(buttonsLayout, 2.0f);
		
		listDataSets.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
				String chosenDataSet = (String)event.getProperty().getValue();
				lblProblem.setValue("<b>Problem description: </b>The selected data set belongs to " + map.get(chosenDataSet) +
						" DSDs. It should belong to exactly one.");
			}
		});
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String chosenDSD = (String)comboDSDs.getValue();
				String dataSet = (String)listDataSets.getValue();
				
				if (chosenDSD == null) {
					getWindow().showNotification("DSD was not selected", Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				if (dataSet == null) {
					getWindow().showNotification("Data set was not selected", Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				
				String structProp = "http://purl.org/linked-data/cube#structure";
				List<String> forRemoval = getDataSetDSDs(dataSet);
				if (forRemoval.size()>0){
					ArrayList<Statement> stmts = new ArrayList<Statement>();
					for (String dsd: forRemoval)
						stmts.add(getStatementFromUris(dataSet, structProp, dsd));
					removeStatements(stmts);
				}
				ArrayList<Statement> addStmts = new ArrayList<Statement>();
				addStmts.add(getStatementFromUris(dataSet, structProp, chosenDSD));
				uploadStatements(addStmts);
				dataSetLinks();
			}
		});
		
		showContent();
	}
	
	private void measuresInDSD(){
		validationTab.removeAllComponents();
		TupleQueryResult res = executeTupleQuery(testMeasuresInDSD);
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final List<String> dsdList = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				dsdList.add(set.getValue("dsd").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		if (dsdList.size() == 0){
			Label label = new Label();
			label.setValue("All DSDs contain at least one measure");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following DSDs do not have at least one measure defined");
		validationTab.addComponent(lbl);
		
		final ListSelect listDSDs = new ListSelect("DSDs", dsdList);
		listDSDs.setNullSelectionAllowed(false);
		validationTab.addComponent(listDSDs);
		
		Button editInOW = new Button("Edit in OntoWiki");
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listDSDs.getValue());
			}
		});
		
		Button fix = new Button("Edit in OntoWiki");
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		buttonsLayout.addComponent(fix);
		buttonsLayout.addComponent(editInOW);
		validationTab.addComponent(buttonsLayout);
		validationTab.setExpandRatio(buttonsLayout, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listDSDs.getValue());
			}
		});
		
		showContent();
	}
	
	private void dimensionsHaveRange(){
		validationTab.removeAllComponents();
		TupleQueryResult res = executeTupleQuery(testDimensionsHaveRange);
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final List<String> dimList = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				dimList.add(set.getValue("dim").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		if (dimList.size() == 0){
			Label label = new Label();
			label.setValue("All dimensions have a defined range");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following dimensions do not have a defined range");
		validationTab.addComponent(lbl);
		
		final ListSelect listDimensions = new ListSelect("Dimensions", dimList);
		listDimensions.setNullSelectionAllowed(false);
		validationTab.addComponent(listDimensions);
		
		Button fix = new Button("Edit in OntoWiki");
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listDimensions.getValue());
			}
		});
		
		showContent();
	}
	
	private void sliceKeysDeclared(){
		validationTab.removeAllComponents();
		
		final TupleQueryResult res = executeTupleQuery(testSliceKeysDeclared);
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - " + errorMsg);
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final ArrayList<String> listSliceKeys = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				listSliceKeys.add(set.getValue("sliceKey").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		if (listSliceKeys.size() == 0){
			Label label = new Label();
			label.setValue("Every slice key is associated with a DSD");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Following slice keys should be associated with a DSD");
		validationTab.addComponent(label);
		final ListSelect lsSliceKeys = new ListSelect("Slice keys", listSliceKeys);
		lsSliceKeys.setImmediate(true);
		lsSliceKeys.setNullSelectionAllowed(false);
		validationTab.addComponent(lsSliceKeys);
		
		final Table detailsTable = new Table("Slice key details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		Button editInOW = new Button("Edit in OntoWiki");
		validationTab.addComponent(editInOW);
		
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)lsSliceKeys.getValue());
			}
		});
		lsSliceKeys.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		
		showContent();
	}
	
	private void sliceKeysConsistentWithDSD(){
		validationTab.removeAllComponents();
		
		final TupleQueryResult res = executeTupleQuery(testSliceKeysConsistentWithDSD);
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - " + errorMsg);
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final ArrayList<String> listSliceKeys = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				listSliceKeys.add(set.getValue("sliceKey").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		if (listSliceKeys.size() == 0){
			Label label = new Label();
			label.setValue("All slice keys are consistent with associated DSD, i.e. for every slice key holds: " +
					"every component property of the slice key is also declared as a component of the associated DSD");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("All slice keys should be consistent with thier associated DSDs, i.e. for every slice key following should hold: " +
				"every component property of the slice key is also declared as a component of the associated DSD.");
		validationTab.addComponent(label);
		Label label2 = new Label();
		label2.setValue("Following slice keys should be modified in order to be consistent with the associated DSD");
		validationTab.addComponent(label2);
		final ListSelect lsSliceKeys = new ListSelect("Slice keys", listSliceKeys);
		lsSliceKeys.setImmediate(true);
		lsSliceKeys.setNullSelectionAllowed(false);
		validationTab.addComponent(lsSliceKeys);
		
		final Table detailsTable = new Table("Slice key details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		Button editInOW = new Button("Edit in OntoWiki");
		validationTab.addComponent(editInOW);
		
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)lsSliceKeys.getValue());
			}
		});
		lsSliceKeys.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		
		showContent();
	}
	
	private void sliceStructureUnique(){
		validationTab.removeAllComponents();
		
		final TupleQueryResult res = executeTupleQuery(testSliceStructureUnique);
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - " + errorMsg);
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final ArrayList<String> listSlices = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				listSlices.add(set.getValue("slice").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		if (listSlices.size() == 0){
			Label label = new Label();
			label.setValue("Every slice has a unique structure, i.e. exactly one associated slice key (via property qb:sliceStructure)");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Following slices have 0 or more than 1 associated slice keys (via property qb:sliceStructure)");
		validationTab.addComponent(label);
		final ListSelect lsSlices = new ListSelect("Slices", listSlices);
		lsSlices.setImmediate(true);
		lsSlices.setNullSelectionAllowed(false);
		validationTab.addComponent(lsSlices);
		
		final Table detailsTable = new Table("Slice details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		Button editInOW = new Button("Edit in OntoWiki");
		validationTab.addComponent(editInOW);
		
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)lsSlices.getValue());
			}
		});
		lsSlices.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		
		showContent();
	}
	
	private void sliceDimensionsComplete(){
		validationTab.removeAllComponents();
		
		final TupleQueryResult res = executeTupleQuery(testSliceDimensionsComplete);
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - " + errorMsg);
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		String lastSlice = null;
		ArrayList<String> lastDimensions = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				String s = set.getValue("slice").stringValue();
				if (lastSlice == null) lastSlice = s;
				String d = set.getValue("dim").stringValue();
				if (!s.equals(lastSlice)) {
					map.put(lastSlice, lastDimensions);
					lastSlice = s;
					lastDimensions = new ArrayList<String>();
				}
				lastDimensions.add(d);
			}
			if (lastSlice != null) map.put(lastSlice, lastDimensions);
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		if (map.size() == 0){
			Label label = new Label();
			label.setValue("Every slice has a value for every dimension declared in its associated slice key (via property qb:sliceStructure)");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Following slices do not have a value for every dimension declared in its associated slice key (via property qb:sliceStructure)");
		validationTab.addComponent(label);
		final ListSelect lsSlices = new ListSelect("Slices", map.keySet());
		lsSlices.setImmediate(true);
		lsSlices.setNullSelectionAllowed(false);
		validationTab.addComponent(lsSlices);
		
		final Table detailsTable = new Table("Slice details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		final Label lblProblem = new Label("<b>Problem description: </b>", Label.CONTENT_XHTML);
		validationTab.addComponent(lblProblem);
		
		Button editInOW = new Button("Edit in OntoWiki");
		validationTab.addComponent(editInOW);
		
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)lsSlices.getValue());
			}
		});
		lsSlices.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String slice = (String)event.getProperty().getValue();
				TupleQueryResult res = getResourceProperties(slice);
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
				StringBuilder sb = new StringBuilder();
				sb.append("<b>Problem description: </b>Selected slice is missing a value for the following dimensions:");
				for (String dim: map.get(slice))
					sb.append(" ").append(dim).append(",");
				sb.deleteCharAt(sb.length()-1);
				lblProblem.setValue(sb.toString());
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
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				map.put(set.getValue("val").stringValue(), set.getValue("list").stringValue());
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
		
		Button editInOW = new Button("Edit in OntoWiki");
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listValues.getValue());
			}
		});
		
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		
		Button fix = new Button("Quick Fix");
		validationTab.addComponent(buttonsLayout);
		validationTab.setExpandRatio(buttonsLayout, 2.0f);
		buttonsLayout.addComponent(fix);
		buttonsLayout.addComponent(editInOW);
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String resource = (String)listValues.getValue();
				String codeList = map.get(resource);
				getWindow().addWindow(new QuickFixCodesFromCodeLists(resource, codeList));
			}
		});
		
		showContent();
	}
	
	private void dimensionsRequired(){
		validationTab.removeAllComponents();
		validationTab.addComponent(new Label("Following observation don't have a value for each dimension: "));
		TupleQueryResult res = executeTupleQuery(testDimensionsRequired);
		ArrayList<String> listObs = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				listObs.add(set.getValue("obs").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		ListSelect ls = new ListSelect("Observations", listObs);
		ls.setNullSelectionAllowed(false);
		ls.setWidth("100%");
		validationTab.addComponent(ls);
		Button fix = new Button("Quick Fix");
		fix.setEnabled(false);
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		showContent();
	}
	
	private void noDuplicateObs(){
		validationTab.removeAllComponents();
		validationTab.addComponent(new Label("Following observations belong to the same data set and have the same value for all dimensions."));
		
		final ListSelect ls1 = new ListSelect("Observations");
		ls1.setNullSelectionAllowed(false);
		ls1.setImmediate(true);
		ls1.setWidth("100%");
		validationTab.addComponent(ls1);
		
		TupleQueryResult res = executeTupleQuery(testNoDuplicateObservations);
		final HashMap<String, List<String>> mapDuplicates = new HashMap<String, List<String>>();
		String lastObs = "";
		List<String> lastDuplicates = null;
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				String obs1 = set.getValue("obs1").stringValue();
				if (!obs1.equals(lastObs)){
					lastObs = obs1;
					lastDuplicates = new ArrayList<String>();
					mapDuplicates.put(lastObs, lastDuplicates);
					ls1.addItem(lastObs);
				}
				lastDuplicates.add(set.getValue("obs2").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		final ListSelect ls2 = new ListSelect("Duplicates");
		ls2.setNullSelectionAllowed(false);
		ls2.setImmediate(true);
		ls2.setWidth("100%");
		validationTab.addComponent(ls2);
		
		ls1.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				ls2.removeAllItems();
				for (String duplicate: mapDuplicates.get(event.getProperty().getValue()))
					ls2.addItem(duplicate);
			}
		});
		
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		panelLayout.addComponent(new Label("After the fix duplicates of the selected observation will be removed from the graph"));
		
		Button fix = new Button("Quick Fix");
		fix.setEnabled(false);
		panelLayout.addComponent(fix);
		panelLayout.setExpandRatio(fix, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String obsString = (String)ls1.getValue();
				if (obsString == null || obsString.isEmpty())
					getWindow().showNotification("Select observation first", Notification.TYPE_ERROR_MESSAGE);
				ValueFactory factory = state.getRdfStore().getValueFactory();
				URI obsURI = factory.createURI(obsString);
				TupleQueryResult res = getResourceProperties(obsString);
				ArrayList<Statement> stmts = new ArrayList<Statement>();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						URI propURI = factory.createURI(set.getValue("p").stringValue());
						Value objValue = set.getValue("o");
						stmts.add(factory.createStatement(obsURI, propURI, objValue));
					}
					res = getResourceLinks(obsString);
					while (res.hasNext()){
						BindingSet set = res.next();
						URI propURI = factory.createURI(set.getValue("p").stringValue());
						URI subURI = factory.createURI(set.getValue("s").stringValue());
						stmts.add(factory.createStatement(subURI, propURI, obsURI));
					}
					removeStatements(stmts);
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		
		showContent();
	}
	
	private void showContent(){
		mainContrainer.setExpandRatio(criteriaList, 0.0f);
		mainContrainer.setExpandRatio(validationPanel, 2.0f);
		mainContrainer.setSizeFull();
	}
	
	private void showInOntowiki(String resource){
		if (resource == null || resource.isEmpty())
			return;
		target.removeAllComponents();
		String path = "/resource/properties?m="+state.getCurrentGraph()+"&r="+resource;
		OntoWikiPathExtended component = new OntoWikiPathExtended(state, path, false);
		component.setSizeFull();
		target.addComponent(component);
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
	
	private TupleQueryResult getResourceLinks(String resource){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			StringBuilder q = new StringBuilder();
			q.append("select ?s ?p from <").append(state.getCurrentGraph()).append("> where { ?s ?p <");
			q.append(resource).append("> . }");
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
	
	private List<String> getObsDataSets(String obs){
		StringBuilder q = new StringBuilder();
		q.append("select ?ds from <").append(state.getCurrentGraph());
		q.append("> where { <").append(obs).append("> <http://purl.org/linked-data/cube#dataSet> ?ds . ");
		q.append("?ds a <http://purl.org/linked-data/cube#DataSet> . }");
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
	
	private List<String> getDataSetDSDs(String dataSet){
		StringBuilder q = new StringBuilder();
		q.append("select ?dsd from <").append(state.getCurrentGraph());
		q.append("> where { <").append(dataSet).append("> <http://purl.org/linked-data/cube#structure> ?dsd . ");
		q.append("?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> . }");
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
	
	private void uploadStatements(Iterable<? extends Statement> statements){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			URI graph = state.getRdfStore().getValueFactory().createURI(state.getCurrentGraph());
			con.add(statements, graph);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private void removeStatements(Iterable<? extends Statement> statements){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			URI graph = state.getRdfStore().getValueFactory().createURI(state.getCurrentGraph());
			con.remove(statements, graph);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private Statement getStatementFromUris(String s, String p, String o){
		ValueFactory factory = state.getRdfStore().getValueFactory();
		URI sub = factory.createURI(s);
		URI pre = factory.createURI(p);
		URI obj = factory.createURI(o);
		return factory.createStatement(sub, pre, obj);
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
						" via skos:inScheme property"));
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
					if (resource == null || resource.isEmpty())
						return;
					
					ArrayList<Statement> statements = new ArrayList<Statement>();
					String concept = "http://www.w3.org/2004/02/skos/core#Concept";
					String inScheme = "http://www.w3.org/2004/02/skos/core#inScheme";
					String type = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
					statements.add(getStatementFromUris(resource, type, concept));
					statements.add(getStatementFromUris(resource, inScheme, codeList));
					uploadStatements(statements);
					Validation.this.getWindow().removeWindow(QuickFixCodesFromCodeLists.this);
					dimensionDefinitions();
				}
			});
			setContent(content);
			QuickFixCodesFromCodeLists.this.center();
		}
	}
	
	public void notifyCurrentGraphChange(String graph) {
        refresh();
    }

	@Override
	public void attach() {
		state.addCurrentGraphListener(this);
		refresh();
	}

	@Override
	public void detach() {
		state.removeCurrentGraphListener(this);
	}

}
