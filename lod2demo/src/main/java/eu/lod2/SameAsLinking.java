
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

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Alignment.*;
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
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;


import org.restlet.resource.ClientResource;
import org.restlet.data.MediaType;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;
import eu.lod2.ExportSelector;

/**
 * SameAsLinking will guide the user to link an instance (uri) from the current graph to 
 * other uris using the online service SameAs
 */
//@SuppressWarnings("serial")
public class SameAsLinking extends CustomComponent
{

        // reference to the global internal state
        private LOD2DemoState state;

        private ExportSelector exportGraph;
    	private ComboBox uriSelector;
    	private Label resultUser;

        public SameAsLinking(LOD2DemoState st) {

                // The internal state 
                state = st;

                VerticalLayout panel = new VerticalLayout();
                Label description = new Label(
                                "Import public links between individuals (URI's) from the current graph to external resources using " +
                                "the online service SameAs.org."
                                );
                panel.addComponent(description);

                exportGraph = new ExportSelector(state);
                panel.addComponent(exportGraph);

	            uriSelector = new ComboBox("Select URI to link: ");
                uriSelector.setDescription("The selector contains uri's only if a current graph has a value.");
                panel.addComponent(uriSelector);

		        Button sameAsLinking = new Button("Extract Links", new ClickListener() {
			        public void buttonClick(ClickEvent event) {
                        doSameAsLinking();
                    };
                });
                sameAsLinking.setEnabled(false);
                sameAsLinking.setDescription("The operation is active only if a current graph has a value.");
                panel.addComponent(sameAsLinking);

                resultUser =  new Label("");
                panel.addComponent(resultUser);

                // if the current graph is selected then
                if (state.getCurrentGraph() != null && !state.getCurrentGraph().equals("")) {
                    // activate linking button
                    sameAsLinking.setEnabled(true);
                    // retrieve list of candidate uri's
	                addCandidateURIs(uriSelector);
                };
                

                // The composition root MUST be set
                setCompositionRoot(panel);
        }

        // propagate the information of one tab to another.
        public void setDefaults() {
        };

        // execute the linking
        public void doSameAsLinking() {
           try {
                        String encoded = URLEncoder.encode((String) uriSelector.getValue(), "UTF-8");
                        java.net.URL sameAsData = new java.net.URL("http://sameAs.org/rdf?uri=" + encoded);
                        String baseURI = "http://sameAs.org#";

                        RepositoryConnection con = state.getRdfStore().getConnection();
                        Resource contextURI = con.getValueFactory().createURI(exportGraph.getExportGraph());
                        Resource[] contexts = new Resource[] {contextURI};
                        Long BeforeSize = con.size(contextURI);
                        con.add(sameAsData, baseURI, RDFFormat.RDFXML, contexts);

                        cleanSameAsResult(con);

                        Long AddTriples = con.size(contextURI) - BeforeSize;

                        if (AddTriples == 0) {
                            resultUser.setValue("No triples added to target store.");
                        } else {
                            resultUser.setValue("Added " + AddTriples.toString() + " triples to target store.");
                        };

                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                } catch (RepositoryException e) {
                        e.printStackTrace();
                } catch (MalformedURLException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                } catch (RDFParseException e) {
                        e.printStackTrace();
                }; 
        };

        public void addCandidateURIs(AbstractSelect selection) {
                try {
                        RepositoryConnection con = state.getRdfStore().getConnection();

                        // initialize the hostname and portnumber
                        String query = "SELECT  DISTINCT ?s from <" + state.getCurrentGraph() +"> where { {?s  ?p1  ?o1. filter isURI(?s).} union {?s2 ?p2 ?s. filter isURI(?s).  }} limit 100";
                        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
                        TupleQueryResult result = tupleQuery.evaluate();


                        while (result.hasNext()) {
                                BindingSet bindingSet = result.next();
                                Value valueOfG = bindingSet.getValue("s");
                                // exclude some value to be candidates
                                if (valueOfG.stringValue() != "null") {
                                        selection.addItem(valueOfG.stringValue());
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


        public void cleanSameAsResult(RepositoryConnection con) {
                try {

                        String query1 = "delete from <" + exportGraph.getExportGraph() + "> {?s <http://www.w3.org/2002/07/owl#sameAs> ?s } where { graph <" +
                        exportGraph.getExportGraph() + "> {?s ?p ?o. filter isURI(?s)}.}";

                        String query2 = "delete from <" + exportGraph.getExportGraph() + "> {?s ?p ?o } where { ?s <http://purl.org/dc/elements/1.1/creator> \"sameAs.org\".  ?s ?p ?o.}";

/*
                        String encoded1 = URLEncoder.encode(query1, "UTF-8");
                        String encoded2 = URLEncoder.encode(query2, "UTF-8");

                        String delete1 = "http://localhost:8890/sparql?query="+encoded1;
                        String delete2 = "http://localhost:8890/sparql?query="+encoded2;
                        
                        ClientResource restcall1 = new ClientResource(delete1);
                        String result1 = restcall1.get(MediaType.APPLICATION_RDF_XML).getText();  
                
                        ClientResource restcall2 = new ClientResource(delete2);
                        String result2 = restcall2.get(MediaType.APPLICATION_RDF_XML).getText();  

                        resultUser.setValue(result1 + result2); 
*/
                        TupleQuery tupleQuerydelete1 = con.prepareTupleQuery(QueryLanguage.SPARQL, query1);
                        TupleQueryResult resultd1 = tupleQuerydelete1.evaluate();
                        TupleQuery tupleQuerydelete2 = con.prepareTupleQuery(QueryLanguage.SPARQL, query2);
                        TupleQueryResult resultd2 = tupleQuerydelete2.evaluate();
                    
//                } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                } catch (UnsupportedEncodingException e) { 
//                        e.printStackTrace();
//                } catch (IOException e) { 
//                        e.printStackTrace();
                } catch (RepositoryException e) {
                        e.printStackTrace();
                } catch (MalformedQueryException e) {
                        e.printStackTrace();
                } catch (QueryEvaluationException e) {
                        e.printStackTrace();
                };
        };

};

