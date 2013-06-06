package eu.lod2.stat;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import eu.lod2.LOD2DemoState;

public class CreateSlices extends VerticalLayout {
	
	private LOD2DemoState state;
	private String selectedDataSet;
	private ArrayList<String> availableDimensions;
	private VerticalLayout layoutDimensions;
	
	private ArrayList<ComboBox> listComboDim;
	private ArrayList<ComboBox> listComboVal;
	
	public CreateSlices(LOD2DemoState state){
		this.state = state;
		this.selectedDataSet = null;
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
	
	private void addDimension(){
		final HorizontalLayout dimensionLayout = new HorizontalLayout();
		dimensionLayout.setWidth("100%");
		layoutDimensions.addComponent(dimensionLayout);
		dimensionLayout.setSpacing(true);
		final ComboBox comboDimensions = new ComboBox("Choose dimension", availableDimensions);
		comboDimensions.setNullSelectionAllowed(false);
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
			}
		});
		btnRemove.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				listComboDim.remove(comboDimensions);
				listComboVal.remove(comboValues);
				layoutDimensions.removeComponent(dimensionLayout);
			}
		});
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
		Button btnCreateSlice = new Button("Create slice");
		HorizontalLayout layoutCommands = new HorizontalLayout();
		layoutCommands.setSpacing(true);
		this.addComponent(layoutCommands);
		layoutCommands.addComponent(btnAddDimension);
		layoutCommands.addComponent(btnCreateSlice);
		
		comboDataSets.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				selectedDataSet = (String)event.getProperty().getValue();
				availableDimensions.clear();
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
				URI sliceURI = factory.createURI(strSliceURI.toString());
				URI sliceKeyURI = factory.createURI(strSliceKeyURI.toString());
				URI dsURI = factory.createURI(selectedDataSet);
				
				ArrayList<Statement> stmts = new ArrayList<Statement>();
				stmts.add(factory.createStatement(dsURI, 
						factory.createURI(qb, "slice"), 
						sliceURI));
				stmts.add(factory.createStatement(sliceURI, 
						factory.createURI(rdf, "type"), 
						factory.createURI(qb,"Slice")));
				stmts.add(factory.createStatement(sliceURI, 
						factory.createURI(rdfs, "label"), 
						factory.createLiteral(strSliceLabel.toString())));
				stmts.add(factory.createStatement(sliceURI, 
						factory.createURI(qb, "sliceStructure"), 
						sliceKeyURI));
				stmts.add(factory.createStatement(sliceKeyURI, 
						factory.createURI(rdf, "type"), 
						factory.createURI(qb, "SliceKey")));
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
				
			}
		});
		
	}

}
