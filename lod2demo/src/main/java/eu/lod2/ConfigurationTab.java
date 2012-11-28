/*
 * Copyright 2011 LOD2 consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package eu.lod2;

import java.net.*;
import java.net.URI;
import java.io.*;
import java.lang.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.*;
import org.apache.http.client.methods.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.type.TypeReference;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Alignment.*;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.*;

import org.openrdf.model.*;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.model.impl.*;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;

/**
 * The configuration tab which collects information about 
 * the default settings van the LOD2 demonstrator.
 */
//@SuppressWarnings("serial")
public class ConfigurationTab extends CustomComponent
{

		// reference to the global internal state
		private LOD2DemoState state;
		private Label currentgraph;

		// fields
		private ComboBox currentGraphPicker;

		public ConfigurationTab(LOD2DemoState st, Label cg) {

				// The internal state and 
				state = st;
				currentgraph = cg;

				VerticalLayout configurationTab = new VerticalLayout();

				// Configuration form start
				// Set all properties at once for the moment.
				Form t2f = new Form();
                //t2f.setDebugId(this.getClass().getSimpleName()+"_t2f");
				t2f.setCaption("Configuration");

				// the graph selector
				// it displays all acceptable graphs in Virtuoso 
				currentGraphPicker = new ComboBox("Select default graph: ");
                //currentGraphPicker.setDebugId(this.getClass().getSimpleName()+"_graphSelector2");
				addCandidateGraphs(currentGraphPicker);

				if (cg.getValue() != null
								&& cg.getValue() != "no current graph selected"
								&& cg.getValue() != "null"
				   ) {
						currentGraphPicker.setValue(cg.getValue());
						currentGraphPicker.setColumns(cg.toString().length());
				};
				currentGraphPicker.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
				t2f.getLayout().addComponent(currentGraphPicker);

				// initialize the footer area of the form
				HorizontalLayout t2ffooterlayout = new HorizontalLayout();
				t2f.setFooter(t2ffooterlayout);
				Button commitButton = new Button("Set configuration", new ClickListener() {
								public void buttonClick(ClickEvent event) {
								storeConfiguration(event);
								}
								});
                //commitButton.setDebugId(this.getClass().getSimpleName()+"_commitButton");
				commitButton.setDescription("Commit the new configuration settings.");
				t2f.getFooter().addComponent(commitButton);

				configurationTab.addComponent(t2f);

				// Configuration form end


				// The composition root MUST be set
				setCompositionRoot(configurationTab);
		}

		private void storeConfiguration(ClickEvent event) {
				state.setCurrentGraph((String) currentGraphPicker.getValue());
				currentgraph.setValue((String) currentGraphPicker.getValue());

		};

		// propagate the information of one tab to another.
		public void setDefaults() {
				currentGraphPicker.setValue(state.getCurrentGraph());
		};

		// obsolete implementation
		// this one is a pure SPARQL implementation
		// It has the following drawbacks: no detection of empty graphs, calculation in the size of the DB (performance decrease when more data is added.)
    /*
		public void addCandidateGraphs_old(AbstractSelect selection) {
				// SELECT ID_TO_IRI(REC_GRAPH_IID) AS GRAPH FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH

				try {
						RepositoryConnection con = state.getRdfStore().getConnection();

						String query = "SELECT  DISTINCT ?g { GRAPH ?g { ?s  ?p  ?o }. OPTIONAL {?g <http://lod2.eu/lod2demo/SystemGraphFor> ?sys.}. FILTER (!bound(?sys))} limit 100";
						TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
						TupleQueryResult result = tupleQuery.evaluate();


						while (result.hasNext()) {
								BindingSet bindingSet = result.next();
								Value valueOfG = bindingSet.getValue("g");
								// exclude some value to be candidates
								if (valueOfG.stringValue() != "null") {
										selection.addItem(valueOfG.stringValue());
										// shortcut
										String cgquery = "create silent GRAPH <" + valueOfG.stringValue() + ">";
										TupleQuery cgtupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, cgquery);
										TupleQueryResult cgresult = tupleQuery.evaluate();
								};
						};

				} catch (RepositoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				} catch (MalformedQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				} catch (QueryEvaluationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
		};

    */
	  
    /*
     * implementation of the candidate graphs using the LOD2 web api
     */
		public void addCandidateGraphs(AbstractSelect selection) {

				List<String> graphs = null;
				try {
						graphs = request_graphs();
				} catch (Exception e) {
						System.err.println(e.getMessage());
				};
				Iterator<String> giterator = graphs.iterator();
				while (giterator.hasNext()) {
						selection.addItem(giterator.next());
				};

		};

		// get the uri's for a list of abbreviations
		public static List<String> request_graphs() throws Exception {

				List<String> result = null;

				HttpClient httpclient = new DefaultHttpClient();
				try {

						String prefixurl = "http://localhost:8080/lod2webapi/graphs";

						HttpGet httpget = new HttpGet(prefixurl);
						httpget.addHeader("accept", "application/json");


						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						String responseBody = httpclient.execute(httpget, responseHandler);

						result = parse_graph_api_result(responseBody);


				} finally {
						// When HttpClient instance is no longer needed,
						// shut down the connection manager to ensure
						// immediate deallocation of all system resources
						httpclient.getConnectionManager().shutdown();
				}

				return result;
		} 

		private static List<String> parse_graph_api_result(String result) throws Exception {

				ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
				TypeReference<HashMap<String,Object>> typeRef
						= new TypeReference<
						HashMap<String,Object>
						>() {}; 
				HashMap<String,Object> userData = mapper.readValue(result, typeRef);

				List<String> graphs = null;
				if (userData.containsKey("graphs")) {
						Object ographs = userData.get("graphs");
						try {
								HashMap<String, Object> oographs = (HashMap<String, Object>) ographs;
								if (oographs.containsKey("resultList")) {
										Object graphsList = oographs.get("resultList");
										graphs = (List<String>) graphsList;
								};
						} catch (Exception e) {
								System.err.println(e.getMessage());
						};
				};

				return graphs;

		};



};

