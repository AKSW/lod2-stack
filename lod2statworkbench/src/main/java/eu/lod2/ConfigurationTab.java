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

import com.vaadin.ui.*;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import eu.lod2.utils.GraphPicker;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * The configuration tab which collects information about 
 * the default settings van the LOD2 demonstrator.
 */
//@SuppressWarnings("serial")
public class ConfigurationTab extends CustomComponent
{

		// reference to the global internal state
		private LOD2DemoState state;

		// fields
		private ComboBox currentGraphPicker;

		public ConfigurationTab(LOD2DemoState st) {

				// The internal state and 
				state = st;

				VerticalLayout configurationTab = new VerticalLayout();

				// Configuration form start
				// Set all properties at once for the moment.
				Form t2f = new Form();
                //t2f.setDebugId(this.getClass().getSimpleName()+"_t2f");
				t2f.setCaption("Configuration");

				// the graph selector
				// it displays all acceptable graphs in Virtuoso
                currentGraphPicker= new GraphPicker("Select default graphs: ",state);
				//currentGraphPicker.setDebugId(this.getClass().getSimpleName()+"_graphSelector2");
				addCandidateGraphs(currentGraphPicker);

				currentGraphPicker.setValue(this.state.getCurrentGraph());

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
            Window window=this.getWindow();
            if(window!=null){
                // window can be null if the graph change reaction destroys this component immediately
                this.getWindow().showNotification("Current graph changed", "The current graph has been changed to: "+this.state.getCurrentGraph(), Window.Notification.TYPE_HUMANIZED_MESSAGE);
            }
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

						String query = "SELECT  DISTINCT ?g { GRAPH ?g { ?s  ?p  ?o }. OPTIONAL {?g <http://lod2.eu/lod2statworkbench/SystemGraphFor> ?sys.}. FILTER (!bound(?sys))} limit 100";
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
						graphs = request_graphs(state);
				} catch (Exception e) {
						System.err.println(e.getMessage());
				};
				Iterator<String> giterator = graphs.iterator();
				while (giterator.hasNext()) {
						selection.addItem(giterator.next());
				};

		};

		// get the uri's for a list of abbreviations
		public static List<String> request_graphs(LOD2DemoState state) throws Exception {

				List<String> result = null;

				HttpClient httpclient = new DefaultHttpClient();
				try {

						String prefixurl = state.getLod2WebApiService();

						HttpGet httpget = new HttpGet(prefixurl+"/graphs");
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

