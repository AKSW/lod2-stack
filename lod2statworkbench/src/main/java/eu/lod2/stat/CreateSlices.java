package eu.lod2.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

import eu.lod2.ConfigurationTab;
import eu.lod2.LOD2DemoState;
import eu.lod2.LOD2DemoState.CurrentGraphListener;

public class CreateSlices extends VerticalLayout implements CurrentGraphListener{
	
	private LOD2DemoState state;
	private String selectedDataSet;
	private ArrayList<String> availableDimensions;
	private VerticalLayout layoutDimensions;
	
	private ArrayList<ComboBox> listComboDim;
	private ArrayList<ComboBox> listComboVal;
	
	private List<SliceKey> availableKeys;
	private List<Slice> availableSlices;
	private TextField txtSliceKeyURI;
	private TextField txtSliceKeyLabel;
	private TextField txtSliceURI;
	private TextField txtSliceLabel;
	private Button btnCreateSlice;
	
	public CreateSlices(LOD2DemoState state){
		this.state = state;
		this.selectedDataSet = null;
		this.availableKeys = new LinkedList<CreateSlices.SliceKey>();
		this.availableSlices = new LinkedList<CreateSlices.Slice>();
		this.availableDimensions = new ArrayList<String>();
		this.listComboDim = new ArrayList<ComboBox>();
		this.listComboVal = new ArrayList<ComboBox>();
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
	
	private TupleQueryResult getDataSets(){
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		queryBuilder.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		queryBuilder.append("select ?ds ?dsd \n");
		queryBuilder.append("from <").append(state.getCurrentGraph()).append("> \n");
		queryBuilder.append("where { \n");
		queryBuilder.append("  ?ds a qb:DataSet . \n");
		queryBuilder.append("  ?ds qb:structure ?dsd . \n");
		queryBuilder.append("} \n");
		return executeTupleQuery(queryBuilder.toString());
	}
	
	private TupleQueryResult getDimensions(String dataSet){
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		queryBuilder.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		queryBuilder.append("select distinct ?dim \n");
		queryBuilder.append("from <").append(state.getCurrentGraph()).append("> \n");
		queryBuilder.append("where { \n");
		queryBuilder.append("  <").append(dataSet).append("> qb:structure ?dsd . \n");
		queryBuilder.append("  ?dsd qb:component ?cs .  \n");
		queryBuilder.append("  ?cs qb:dimension ?dim .  \n");
		queryBuilder.append("} \n");
		return executeTupleQuery(queryBuilder.toString());
	}
	
	private TupleQueryResult getValues(String dataSet, String dimension){
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		queryBuilder.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		queryBuilder.append("select distinct ?val \n");
		queryBuilder.append("from <").append(state.getCurrentGraph()).append("> \n");
		queryBuilder.append("where { \n");
		queryBuilder.append("  ?obs a qb:Observation . \n");
		queryBuilder.append("  ?obs qb:dataSet <").append(dataSet).append("> . \n");
		queryBuilder.append("  ?obs <").append(dimension).append("> ?val . \n");
		queryBuilder.append("} \n");
		return executeTupleQuery(queryBuilder.toString());
	}
	
	public static class SliceDimension{
		public Value dim=null, val=null;
	}
	
	public static class Slice {
		public Value slice=null;
		public String label = null;
		public List<SliceDimension> dimensions = null;
	}
	
	private List<Slice> getSlices(){
		LinkedList<Slice> slices = new LinkedList<CreateSlices.Slice>();
		StringBuilder q = new StringBuilder();
		q.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		q.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		q.append("prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n");
		q.append("select ?slice ?label ?dim ?val \n");
		q.append("from <").append(state.getCurrentGraph()).append("> \n");
		q.append("where { \n");
		q.append("  <").append(selectedDataSet).append("> qb:slice ?slice . \n");
		q.append("  ?slice a qb:Slice . \n");
		q.append("  ?slice qb:sliceStructure ?key . \n");
		q.append("  ?key qb:componentProperty ?dim . \n");
		q.append("  ?slice ?dim ?val . \n");
		q.append("  OPTIONAL { ?slice rdfs:label ?label } \n");
		q.append("} order by ?slice");
		
		try {
			RepositoryConnection conn = state.getRdfStore().getConnection();
			TupleQueryResult res = conn.prepareTupleQuery(QueryLanguage.SPARQL, q.toString()).evaluate();
			String lastSlice = null;
			List<SliceDimension> lastDimensions = null;
			while (res.hasNext()){
				BindingSet set = res.next();
				Value sliceValue = set.getValue("slice");
				String sliceString = sliceValue.stringValue();
				if (!sliceString.equals(lastSlice)) {
					lastSlice = sliceString;
					lastDimensions = new LinkedList<CreateSlices.SliceDimension>();
					Slice s = new Slice();
					s.slice = sliceValue;
					s.dimensions = lastDimensions;
					Value labelValue = set.getValue("label");
					s.label = (labelValue != null)?labelValue.stringValue():null;
					slices.add(s);
				}
				SliceDimension sd = new SliceDimension();
				sd.dim = set.getValue("dim");
				sd.val = set.getValue("val");
				lastDimensions.add(sd);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		return slices;
	}
	
	public static class SliceKey {
		public Value key = null;
		public String label = null;
		public List<Value> dimensions = null;
	}
	
	private List<SliceKey> getSliceKeys(){
		LinkedList<SliceKey> keys = new LinkedList<SliceKey>();
		StringBuilder q = new StringBuilder();
		q.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		q.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		q.append("prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n");
		q.append("select ?key ?label ?dim \n");
		q.append("from <").append(state.getCurrentGraph()).append("> \n");
		q.append("where { \n");
		q.append("  <").append(selectedDataSet).append("> qb:structure ?dsd . \n");
		q.append("  ?dsd qb:sliceKey ?key . \n");
		q.append("  ?key a qb:SliceKey . \n");
		q.append("  ?key qb:componentProperty ?dim . \n");
		q.append("  OPTIONAL { ?key rdfs:label ?label } \n");
		q.append("} order by ?key");
		
		try {
			RepositoryConnection conn = state.getRdfStore().getConnection();
			TupleQueryResult res = conn.prepareTupleQuery(QueryLanguage.SPARQL, q.toString()).evaluate();
			String lastKey = null;
			LinkedList<Value> lastDimensions = null;
			while (res.hasNext()){
				BindingSet set = res.next();
				Value key = set.getValue("key");
				String keyString = key.stringValue();
				if (!keyString.equals(lastKey)){
					lastKey = keyString;
					lastDimensions = new LinkedList<Value>();
					SliceKey sk = new SliceKey();
					sk.key = key;
					sk.dimensions = lastDimensions;
					Value labelValue = set.getValue("label");
					sk.label = (labelValue != null)?labelValue.stringValue():null;
					keys.add(sk);
				}
				lastDimensions.add(set.getValue("dim"));
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		return keys;
	}
	
	private void addDimension(){
		final HorizontalLayout dimensionLayout = new HorizontalLayout();
		dimensionLayout.setWidth("100%");
		layoutDimensions.addComponent(dimensionLayout);
		dimensionLayout.setSpacing(true);
		final ComboBox comboDimensions = new ComboBox("Choose dimension", availableDimensions);
		comboDimensions.setNullSelectionAllowed(true);
		comboDimensions.setImmediate(true);
		comboDimensions.setWidth("100%");
		dimensionLayout.addComponent(comboDimensions);
		dimensionLayout.setExpandRatio(comboDimensions, 2.0f);
		final ComboBox comboValues = new ComboBox("Choose value");
		comboValues.setNullSelectionAllowed(false);
		comboValues.setImmediate(true);
		comboValues.setWidth("100%");
		dimensionLayout.addComponent(comboValues);
		dimensionLayout.setExpandRatio(comboValues, 2.0f);
		Button btnRemove = new Button("Remove");
		dimensionLayout.addComponent(btnRemove);
		dimensionLayout.setComponentAlignment(btnRemove, Alignment.BOTTOM_LEFT);
		
		listComboDim.add(comboDimensions);
		listComboVal.add(comboValues);
		
		comboDimensions.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				comboValues.removeAllItems();
				String dim = (String)event.getProperty().getValue();
				if (dim==null) {
					comboValues.requestRepaint();
					updateKeyInfo();
					return;
				}
				for (ComboBox c: listComboDim){
					if (c == comboDimensions || c.getValue() == null) continue;
					if (dim.equals((String)c.getValue())){
						comboDimensions.setValue(comboDimensions.getNullSelectionItemId());
						getWindow().showNotification("Dimension already selected");
						return;
					}
				}
				String ds = selectedDataSet;
				TupleQueryResult res = getValues(ds, dim);
				try {
					while (res.hasNext()){
						BindingSet bs = res.next();
						comboValues.addItem(bs.getValue("val"));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
				comboValues.requestRepaint();
				updateKeyInfo();
			}
		});
		comboValues.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				updateSliceInfo();
			}
		});
		btnRemove.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				listComboDim.remove(comboDimensions);
				if (comboDimensions.getValue() != null) 
					updateKeyInfo();
				listComboVal.remove(comboValues);
				if (comboValues.getValue() != null)
					updateSliceInfo();
				layoutDimensions.removeComponent(dimensionLayout);
			}
		});
	}
	
	private void updateKeyInfo(){
		// check if there already is an appropriate key
		SliceKey key = null;
		for (SliceKey k:availableKeys){
			int n = 0;
			boolean containsAll = true;
			for (ComboBox combo: listComboDim){
				if (combo.getValue() == null) continue;
				n++;
				boolean containsThis = false;
				for (Value v: k.dimensions) {
					if (v.stringValue().equals((String)combo.getValue())) {
						containsThis = true;
						break;
					}
				}
				if (!containsThis){
					containsAll = false;
					break;
				}
			}
			if (containsAll && n == k.dimensions.size()){
				key = k;
				break;
			}
		}
		
		if (key != null){
			txtSliceKeyURI.setValue(key.key.stringValue());
			txtSliceKeyURI.setEnabled(false);
			txtSliceKeyLabel.setValue(key.label);
			txtSliceKeyLabel.setEnabled(false);
		} else {
			txtSliceKeyURI.setValue("");
			txtSliceKeyURI.setEnabled(true);
			txtSliceKeyLabel.setValue("");
			txtSliceKeyLabel.setEnabled(true);
		}
	}
	
	private void updateSliceInfo(){
		Slice slice = null;
		for (Slice sl: availableSlices){
			int n = 0;
			boolean containsAll = true;
			for (int i=0; i<listComboDim.size(); i++){
				String dimString = (String)listComboDim.get(i).getValue();
				Value valValue = (Value)listComboVal.get(i).getValue();
				if (dimString == null || valValue == null) continue;
				n++;
				boolean containsThis = false;
				for (SliceDimension slDim: sl.dimensions){
					if (slDim.dim.stringValue().equals(dimString) && slDim.val.stringValue().equals(valValue.stringValue())){
						containsThis = true;
						break;
					}
				}
				if (!containsThis) {
					containsAll = false;
					break;
				}
			}
			if (containsAll && n == sl.dimensions.size()){
				slice = sl;
				break;
			}
		}
		
		if (slice != null){
			txtSliceURI.setValue(slice.slice.stringValue());
			txtSliceURI.setEnabled(false);
			txtSliceLabel.setValue(slice.label);
			txtSliceLabel.setEnabled(false);
			btnCreateSlice.setEnabled(false);
		} else {
			txtSliceURI.setValue("");
			txtSliceURI.setEnabled(true);
			txtSliceLabel.setValue("");
			txtSliceLabel.setEnabled(true);
			btnCreateSlice.setEnabled(true);
		}
	}
	
	private void refresh(){
		this.removeAllComponents();
		this.setSizeUndefined();
		this.setWidth("100%");
		
		String currentGraph = state.getCurrentGraph();
	    if (currentGraph == null || currentGraph.isEmpty()){
	    	VerticalLayout l = new VerticalLayout();
	    	l.setSizeFull();
	    	this.addComponent(l);
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
		
		this.selectedDataSet = null;
		this.availableKeys.clear();
		this.availableSlices.clear();
		this.availableDimensions.clear();
		this.listComboDim.clear();
		this.listComboVal.clear();
		render();
	}
	
	public void render(){
		setSpacing(true);
		TupleQueryResult res = getDataSets();
		final ArrayList<String> listDataSets = new ArrayList<String>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				listDataSets.add(set.getValue("ds").stringValue());
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		final ComboBox comboDataSets = new ComboBox("Choose dataset", listDataSets);
		comboDataSets.setImmediate(true);
		comboDataSets.setNullSelectionAllowed(false);
		comboDataSets.setWidth("100%");
		this.addComponent(comboDataSets);
		
		layoutDimensions = new VerticalLayout();
		layoutDimensions.setWidth("100%");
		this.addComponent(layoutDimensions);
		
		Button btnAddDimension = new Button("Add dimension");
		btnCreateSlice = new Button("Create slice");
		this.addComponent(btnAddDimension);
		
		txtSliceKeyURI = new TextField("SliceKey URI");
		txtSliceKeyURI.setWidth("100%");
		this.addComponent(txtSliceKeyURI);
		
		txtSliceKeyLabel = new TextField("SliceKey Label");
		txtSliceKeyLabel.setWidth("100%");
		this.addComponent(txtSliceKeyLabel);
		
		txtSliceURI = new TextField("Slice URI");
		txtSliceURI.setWidth("100%");
		this.addComponent(txtSliceURI);
		
		txtSliceLabel = new TextField("Slice Label");
		txtSliceLabel.setWidth("100%");
		this.addComponent(txtSliceLabel);
		
		this.addComponent(btnCreateSlice);
		
		comboDataSets.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				selectedDataSet = (String)event.getProperty().getValue();
				availableDimensions.clear();
				availableKeys.clear();
				availableKeys.addAll(getSliceKeys());
				availableSlices.clear();
				availableSlices.addAll(getSlices());
				TupleQueryResult res = getDimensions(selectedDataSet);
				try {
					while (res.hasNext()){
						BindingSet bs = res.next();
						availableDimensions.add(bs.getValue("dim").stringValue());
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
				layoutDimensions.removeAllComponents();
				addDimension();
			}
		});
		btnAddDimension.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				addDimension();
			}
		});
		btnCreateSlice.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				HashMap<URI, Value> mapDimensionValues = new HashMap<URI, Value>();
				ValueFactory factory = state.getRdfStore().getValueFactory();
				StringBuilder strSliceURI = new StringBuilder(selectedDataSet).append("_slice");
				StringBuilder strSliceKeyURI = new StringBuilder(selectedDataSet).append("_sliceKey");
				StringBuilder strSliceLabel = new StringBuilder("Slice -");
				StringBuilder strSliceKeyLabel = new StringBuilder("Slice Key -");
				for (int i=0; i<listComboDim.size(); i++){
					String dim = (String)listComboDim.get(i).getValue();
					Value val = (Value)listComboVal.get(i).getValue();
					if (dim == null || val == null || mapDimensionValues.containsKey(dim)) continue;
					mapDimensionValues.put(factory.createURI(dim), val);
					String dim1 = null, val1 = null, valS = val.stringValue();
					if (dim.contains("#"))
						dim1 = dim.substring(dim.lastIndexOf("#")+1);
					else 
						dim1 = dim.substring(dim.lastIndexOf("/")+1);
					if (valS.contains("#"))
						val1 = valS.substring(valS.lastIndexOf("#")+1);
					else 
						val1 = valS.substring(valS.lastIndexOf("/")+1);
					strSliceURI.append("_").append(dim1).append("-").append(val1);
					strSliceKeyURI.append("_").append(dim1);
					strSliceLabel.append(" ").append(dim1).append("=").append(val1);
					strSliceKeyLabel.append(" ").append(dim1);
				}
				if (mapDimensionValues.size() == 0) return;
				String qb = "http://purl.org/linked-data/cube#";
				String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
				String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
				
				String suri = (String)txtSliceURI.getValue();
				String slab = (String)txtSliceLabel.getValue();
				String kuri = (String)txtSliceKeyURI.getValue();
				String klab = (String)txtSliceKeyLabel.getValue();
				if (suri == null || suri.trim().equals("")){
					getWindow().showNotification("Slice URI not defined", Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				if (kuri == null || kuri.trim().equals("")){
					getWindow().showNotification("SliceKey URI not defined", Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				
				URI sliceURI = factory.createURI(suri.trim());
				URI sliceKeyURI = factory.createURI(kuri.trim());
				URI dsURI = factory.createURI(selectedDataSet);
				
				ArrayList<Statement> stmts = new ArrayList<Statement>();
				stmts.add(factory.createStatement(dsURI, 
						factory.createURI(qb, "slice"), 
						sliceURI));
				stmts.add(factory.createStatement(sliceURI, 
						factory.createURI(rdf, "type"), 
						factory.createURI(qb,"Slice")));
				if (slab != null && !slab.trim().equals(""))
					stmts.add(factory.createStatement(sliceURI, 
							factory.createURI(rdfs, "label"), 
							factory.createLiteral(strSliceLabel.toString())));
				stmts.add(factory.createStatement(sliceURI, 
						factory.createURI(qb, "sliceStructure"), 
						sliceKeyURI));
				stmts.add(factory.createStatement(sliceKeyURI, 
						factory.createURI(rdf, "type"), 
						factory.createURI(qb, "SliceKey")));
				if (klab != null && !klab.trim().equals(""))
					stmts.add(factory.createStatement(sliceKeyURI, 
							factory.createURI(rdfs, "label"), 
							factory.createLiteral(strSliceKeyLabel.toString())));
				for (URI dimURI: mapDimensionValues.keySet()){
					stmts.add(factory.createStatement(sliceKeyURI, 
							factory.createURI(qb, "componentProperty"), 
							dimURI));
					stmts.add(factory.createStatement(sliceURI, 
							dimURI, 
							mapDimensionValues.get(dimURI)));
				}
				
				StringBuilder linkDSD = new StringBuilder();
				linkDSD.append("INSERT INTO GRAPH <").append(state.getCurrentGraph()).append("> { \n");
				linkDSD.append("  ?dsd qb:sliceKey <").append(strSliceKeyURI.toString()).append("> . \n");
				linkDSD.append("} \n");
				linkDSD.append("WHERE { GRAPH <").append(state.getCurrentGraph()).append("> { \n");
				linkDSD.append("  ?ds qb:slice <").append(strSliceURI.toString()).append("> . \n");
				linkDSD.append("  ?ds qb:structure ?dsd . \n");
				linkDSD.append("} } ");
				
				StringBuilder linkObs = new StringBuilder();
				linkObs.append("INSERT INTO GRAPH <").append(state.getCurrentGraph()).append("> { \n");
				linkObs.append("  <").append(strSliceURI.toString()).append("> qb:observation ?obs . \n");
				linkObs.append("} \n");
				linkObs.append("WHERE { GRAPH <").append(state.getCurrentGraph()).append("> { \n");
				linkObs.append("  ?obs a qb:Observation . \n");
				linkObs.append("  ?obs qb:dataSet ?ds . \n");
				linkObs.append("  ?ds qb:slice <").append(strSliceURI.toString()).append("> . \n");
				linkObs.append("  FILTER NOT EXISTS { \n");
				linkObs.append("    <").append(strSliceKeyURI.toString()).append("> qb:componentProperty ?dim . \n");
				linkObs.append("    <").append(strSliceURI.toString()).append("> ?dim ?val1 . \n");
				linkObs.append("    ?obs ?dim ?val2 . \n");
				linkObs.append("    FILTER (?val2 != ?val1) \n");
				linkObs.append("  } \n");
				linkObs.append("} } ");
				
				try {
					RepositoryConnection connection = state.getRdfStore().getConnection();
					connection.add(stmts, factory.createURI(state.getCurrentGraph()));
					connection.prepareGraphQuery(QueryLanguage.SPARQL, linkDSD.toString()).evaluate();
					connection.prepareGraphQuery(QueryLanguage.SPARQL, linkObs.toString()).evaluate();
				} catch (RepositoryException e) {
					e.printStackTrace();
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				} catch (MalformedQueryException e) {
					e.printStackTrace();
				}
				
				getWindow().showNotification("All done!");
				availableSlices.clear();
				availableKeys.clear();
				availableSlices.addAll(getSlices());
				availableKeys.addAll(getSliceKeys());
				updateSliceInfo();
				updateKeyInfo();
			}
		});
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
