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

import com.vaadin.Application;
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
import com.vaadin.ui.Window.*;
import com.vaadin.ui.Layout.*;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.data.*;
import com.vaadin.data.Property;
import com.vaadin.data.Property.*;

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
import org.openrdf.model.impl.LiteralImpl;


import org.restlet.resource.ClientResource;
import org.restlet.data.MediaType;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;

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



/**
 * Export Selector.
 * A common way to select and set the output graph
 * The second version has as additional feature to show the amount of triples (at time of selection)
 * 
 */
//@SuppressWarnings("serial")
public class ExportSelector3 extends CustomComponent
implements AbstractSelect.NewItemHandler, Property.ValueChangeListener
{

    // reference to the global internal state
    private LOD2DemoState state;

    // fields
    private ComboBox graphSelector;
    private Label    status;
    private Link     exploreGraph;
    private Boolean updateCurrentGraph = false;

    public ExportSelector3(LOD2DemoState st) {
            this(st, false);
    };

    public ExportSelector3(LOD2DemoState st, Boolean update) {

	   this(st, update, "Select Export graph: ");

    }

    public ExportSelector3(LOD2DemoState st, Boolean update, String cap) {

        // The internal state 
        state = st;
        updateCurrentGraph = update;


        HorizontalLayout layout = new HorizontalLayout();
        status = new Label("");

        // the graph selector
        // it displays all acceptable graphs in Virtuoso 
        // XXX TODO show only those which are editable in OntoWiki
        graphSelector = new ComboBox(cap);
        graphSelector.setNewItemsAllowed(true);
        graphSelector.setImmediate(true);
        graphSelector.setNewItemHandler(this);
        graphSelector.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        graphSelector.addListener(this);
        addCandidateGraphs(graphSelector);
        layout.addComponent(graphSelector);

        layout.addComponent(status);

        /*  XXX TODO Exploring the content of the graph requires some more work.

            String windowurl = "http://localhost:8080/lod2demo/explore";

            layout.addComponent(new Link("Explore", new ExternalResource(windowurl), "explore-this", -1, -1, Window.BORDER_DEFAULT));
         */


        // The composition root MUST be set
        setCompositionRoot(layout);
    }

    // propagate the information of one tab to another.
    public void setDefaults() {
    };


    public void addCandidateGraphs(AbstractSelect selection) {
        // add current graph as default possibility
        // only if the current graph has been set

        if (state.getCurrentGraph() != null && ! state.getCurrentGraph().equals("")) {
            selection.addItem("current graph");
            selection.select("current graph");
        };

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
    




    public void addNewItem(String newItemCaption) {
        final String newItem = newItemCaption;

        // request the user whether to add it to the list or to reject his choice.
        final Window subwindow = new Window("Create new graph");
        subwindow.setModal(true);

        // Configure the windows layout; by default a VerticalLayout
        VerticalLayout swlayout = (VerticalLayout) subwindow.getContent();

        Label desc = new Label("The graphname " + newItemCaption + " is not a known graph. Shall we create the graph?");
        HorizontalLayout buttons = new HorizontalLayout();

        Button ok = new Button("Create graph",new ClickListener() {
                public void buttonClick(ClickEvent event) {
                createGraph(newItem);
                (subwindow.getParent()).removeWindow(subwindow);
                }
                });
        Button cancel = new Button("Cancel", new ClickListener() {
                public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                }
                });

        swlayout.addComponent(desc);
        swlayout.addComponent(buttons);
        buttons.addComponent(ok);
        buttons.addComponent(cancel);
        buttons.setComponentAlignment(ok, Alignment.BOTTOM_RIGHT);
        buttons.setComponentAlignment(cancel, Alignment.BOTTOM_RIGHT);
        getWindow().addWindow(subwindow);
        subwindow.setWidth("300px");

    }


    private void createGraph(String newGraph) {
        graphSelector.addItem(newGraph);
        graphSelector.select(newGraph);
        activateGraph(newGraph);

    };


    private void activateGraph(String newGraph) {

        try {
            RepositoryConnection con = state.getRdfStore().getConnection();

            // initialize the hostname and portnumber
            String query = "create silent graph <" + newGraph + ">"; 
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
            TupleQueryResult result = tupleQuery.evaluate();

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

    // return the graph name for the export
    public String getExportGraph() {

        if (graphSelector.getValue() != null) {
            String val = (String) graphSelector.getValue();
            String g;
            if (graphSelector.getValue().equals("current graph")) {
                g = state.getCurrentGraph();
            } else {
                g = val;
            };
            return g;
        } else {
            return null;
        }
    };

    // count the triples in the graph
    private int countGraph(String newGraph) {

        int count = 0;
        if (!newGraph.equals("")) {
            try {
                RepositoryConnection con = state.getRdfStore().getConnection();

                // initialize the hostname and portnumber
                String query = "select count(*) as ?c from <" + newGraph + "> where {?s ?p ?o}"; 
                TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
                TupleQueryResult result = tupleQuery.evaluate();

                while (result.hasNext()) {
                    BindingSet bindingSet = result.next();
                    Value valueOfC = bindingSet.getValue("c");
                    LiteralImpl valueOfCLit = (LiteralImpl) valueOfC;
                    count = valueOfCLit.intValue();
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

        return count;

    };

    public void valueChange(Property.ValueChangeEvent event) {
        // The event.getProperty() returns the Item ID (IID) 
        // of the currently selected item in the component.
        int count = countGraph(this.getExportGraph());
        status.setValue(" contains " + count + " triples");
        if (state.getCurrentGraph() != null && state.getCurrentGraph().equals("")) {
               state.setCurrentGraph(event.getProperty().toString());
        } else if (updateCurrentGraph) {
               state.setCurrentGraph(event.getProperty().toString());
        };
    };

};

