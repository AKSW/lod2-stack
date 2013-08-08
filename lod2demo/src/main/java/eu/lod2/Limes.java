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

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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
    private LOD2DemoInitApp initColanut;

    public Limes(LOD2DemoState st) {

        // The internal state and 
        state = st;

        VerticalLayout colanutspatiallayout = new VerticalLayout();

        initColanut = new LOD2DemoInitApp(st, "colanut");


        // Configuration form start
        // Set all properties at once for the moment.
        Form t2f = new Form();
        t2f.setDebugId(this.getClass().getSimpleName()+"_t2f");
        t2f.setCaption("Configuration");


        sourceGraph = new ExportSelector3(state, false, "Select graph with Source Limes data:");
        sourceGraph.setDebugId(this.getClass().getSimpleName()+"_sourceGraph");
        sourceGraph.graphSelector.setDebugId(this.getClass().getSimpleName()+"_graphSelector"+"_sourceGraph");
        t2f.getLayout().addComponent(sourceGraph);

        targetGraph = new ExportSelector3(state, false, "Select graph with Target Limes data:");
        targetGraph.setDebugId(this.getClass().getSimpleName()+"_targetGraph");
        targetGraph.graphSelector.setDebugId(this.getClass().getSimpleName()+"_graphSelector"+"_targetGraph");
        t2f.getLayout().addComponent(targetGraph);
        // initialize the footer area of the form
        HorizontalLayout t2ffooterlayout = new HorizontalLayout();
        t2f.setFooter(t2ffooterlayout);

        Button commitButton = new Button("Set configuration", new ClickListener() {
                public void buttonClick(ClickEvent event) {
                storeConfiguration(event);
                }
                });
        commitButton.setDebugId(this.getClass().getSimpleName()+"_commitButton");
        commitButton.setDescription("Commit the new configuration settings.");
        t2f.getFooter().addComponent(commitButton);

        colanutspatiallayout.addComponent(t2f);
        try { 

            URL url = new URL(initColanut.service + "?se="+
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
            URL url = new URL(initColanut.service + "?se="+
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

