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
import java.net.URLEncoder;
import java.io.*;
import java.io.UnsupportedEncodingException;
import java.lang.*;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Alignment.*;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Link;
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
 * The colanut interface for limes
 */
//@SuppressWarnings("serial")
public class Limes extends CustomComponent
{

    // reference to the global internal state
    private LOD2DemoState state;

    // fields
    private ExportSelector3 sourceGraph;
    private ExportSelector3 targetGraph;
    private Link colanutLink;
    private Embedded colanutbrowser;

    public Limes(LOD2DemoState st) {

        // The internal state and 
        state = st;

        VerticalLayout colanutspatiallayout = new VerticalLayout();


        // Configuration form start
        // Set all properties at once for the moment.
        Form t2f = new Form();
        t2f.setCaption("Configuration");


        sourceGraph = new ExportSelector3(state, false, "Select graph with Source Limes data:");
        t2f.getLayout().addComponent(sourceGraph);

        targetGraph = new ExportSelector3(state, false, "Select graph with Target Limes data:");
        t2f.getLayout().addComponent(targetGraph);
        // initialize the footer area of the form
        HorizontalLayout t2ffooterlayout = new HorizontalLayout();
        t2f.setFooter(t2ffooterlayout);

        Button commitButton = new Button("Set configuration", new ClickListener() {
                public void buttonClick(ClickEvent event) {
                storeConfiguration(event);
                }
                });

        commitButton.setDescription("Commit the new configuration settings.");
        t2f.getFooter().addComponent(commitButton);

        colanutspatiallayout.addComponent(t2f);
        try { 

            URL url = new URL(state.getHostName() + "/colanut?se="+
                    URLEncoder.encode("http://localhost:8890/sparql", "UTF-8") + 
                    "&te=" + URLEncoder.encode("http://localhost:8890/sparql", "UTF-8") +
                    "&sgp=source&tgp=target");
            colanutLink = new Link("Open ColaNut - the web interface for Limes", new ExternalResource(url));
            colanutLink.setTargetName("second");
            colanutLink.setTargetHeight(500);
            colanutLink.setTargetWidth(1000);
            colanutLink.setTargetBorder(Link.TARGET_BORDER_DEFAULT);
            colanutLink.setVisible(false);
            colanutspatiallayout.addComponent(colanutLink);

        } catch (UnsupportedEncodingException e) { 
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        };
        

        // Configuration form end
/* do not use it as the visualisation is not so nice
        colanutbrowser = new Embedded();
        try { 

            URL url = new URL(state.getHostName() + "/colanut?se="+
                              URLEncoder.encode("http://localhost:8890/sparql", "UTF-8") + 
                              "&te=" + URLEncoder.encode("http://localhost:8890/sparql", "UTF-8") +
                              "&sgp=source&tgp=target");
            colanutbrowser = new Embedded("", new ExternalResource(url));
            colanutbrowser.setType(Embedded.TYPE_BROWSER);
            colanutspatiallayout.addComponent(colanutbrowser);
            colanutbrowser.setHeight(1000, Sizeable.UNITS_PIXELS);
            colanutbrowser.setWidth(1000, Sizeable.UNITS_PIXELS);
        } catch (UnsupportedEncodingException e) { 
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        };
*/


        // The composition root MUST be set
        setCompositionRoot(colanutspatiallayout);
    }

    private void storeConfiguration(ClickEvent event) {
        String encodedsource = "";
        if (sourceGraph.getExportGraph() == null || sourceGraph.getExportGraph().equals("")) {
        } else {
            try {
                encodedsource = URLEncoder.encode(sourceGraph.getExportGraph(), "UTF-8");
            } catch (UnsupportedEncodingException e) { 
                e.printStackTrace();
            };
        };

        String encodedtarget = "";
        if (targetGraph.getExportGraph() == null || targetGraph.getExportGraph().equals("")) {
        } else {
            try {
                encodedtarget = URLEncoder.encode(targetGraph.getExportGraph(), "UTF-8");
            } catch (UnsupportedEncodingException e) { 
                e.printStackTrace();
            };
        };

        try {
            URL url = new URL(state.getHostName() + "/colanut?se="+
                              URLEncoder.encode("http://localhost:8890/sparql", "UTF-8") + 
                              "&te=" + URLEncoder.encode("http://localhost:8890/sparql", "UTF-8") +
                              "&sgp=source&tgp=target" +
                              "&sgn=" + encodedsource + "&tgn=" + encodedtarget);
                ExternalResource res = new ExternalResource(url);
                colanutLink.setResource(res);
                colanutLink.setVisible(true);
//                colanutbrowser.setSource(res);
                
            } catch (UnsupportedEncodingException e) { 
                e.printStackTrace();
            } catch (MalformedURLException e) { 
                e.printStackTrace();
            };

    };

    // propagate the information of one tab to another.
    public void setDefaults() {
    };

};

