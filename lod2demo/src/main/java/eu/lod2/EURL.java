
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
 * Extract and upload the RDF content of a URL in a graph.
 */
//@SuppressWarnings("serial")
public class EURL extends CustomComponent
{

        // reference to the global internal state
        private LOD2DemoState state;

        private ExportSelector exportGraph;
        private TextField uriSelector;
    	private Label resultUser;

        public EURL (LOD2DemoState st) {

                // The internal state 
                state = st;

                VerticalLayout panel = new VerticalLayout();
                Label description = new Label(
                                "Import the RDF available at a URL"
                                );
                panel.addComponent(description);

                exportGraph = new ExportSelector(state);
                panel.addComponent(exportGraph);

	        uriSelector = new TextField("Enter URL to extract from: ");
                uriSelector.setDescription("From this URL the RDF will be extracted and imported in the above selected graph.");
                panel.addComponent(uriSelector);

		Button importURL = new Button("Extract Links", new ClickListener() {
			        public void buttonClick(ClickEvent event) {
                        doImportURL();
                    };
                });
                importURL.setEnabled(true);
                importURL.setDescription("The operation is active only if the exported graph has been selected.");
                panel.addComponent(importURL);

                resultUser =  new Label("");
                panel.addComponent(resultUser);

/*
                // if an uri is given 
                if (!uriSelector.getValue().equals("")) {
                    // activate button
                    importURL.setEnabled(true);
                };
*/
                

                // The composition root MUST be set
                setCompositionRoot(panel);
        }

        // propagate the information of one tab to another.
        public void setDefaults() {
        };

        // execute the linking
        public void doImportURL() {
           try {
                        String encoded = URLEncoder.encode((String) uriSelector.getValue(), "UTF-8");
                        java.net.URL sameAsData = new java.net.URL(encoded);
                        String baseURI = "http://localhost:8080/base#";

                        RepositoryConnection con = state.getRdfStore().getConnection();
                        Resource contextURI = con.getValueFactory().createURI(exportGraph.getExportGraph());
                        Resource[] contexts = new Resource[] {contextURI};
                        Long BeforeSize = con.size(contextURI);
                        con.add(sameAsData, baseURI, RDFFormat.RDFXML, contexts);

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




};

