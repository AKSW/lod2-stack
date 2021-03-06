
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

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Alignment.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.*;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.model.*;
import org.openrdf.model.impl.*;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;

/**
 * The authoring tab which collects information about 
 * ways and components to extract information.
 */
//@SuppressWarnings("serial")
public class AuthoringTab extends CustomComponent
	implements TextChangeListener 
{

    // reference to the global internal state
    private LOD2DemoState state;

    private TextField activategraph;
    private String resourceToEdit;
    private Link   ontowikil;

    public AuthoringTab(LOD2DemoState st) {

        // The internal state and 
        state = st;

        VerticalLayout authoringTab = new VerticalLayout();

	// Activate a graph in Virtuoso be editable in OntoWiki.
	// Remark: the accessrightsnull in Virtuoso have be set correct [check this]
        Form activateform = new Form();
        activateform.setDebugId(this.getClass().getSimpleName()+"_activateform");
        activateform.setCaption("Activate graph in OntoWiki");

	// the localhost ip-address
        activategraph = new TextField("graphname:", state.getCurrentGraph());
        activategraph.setColumns(50);
        activateform.getLayout().addComponent(activategraph);

        Button activateButton = new Button("Activate graph", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                activateGraph(event);
            }
        });
        activateButton.setDebugId(this.getClass().getSimpleName()+"_activateButton");
        activateButton.setDescription("Activate the graph in Virtuoso to become editable in OntoWiki.");
        activateform.getFooter().addComponent(activateButton);
        


        authoringTab.addComponent(activateform);



        // add a form widget to edit with OntoWiki (or other editor) a specific resource
        Form t2f = new Form();
        t2f.setCaption("Edit resource content");

        TextField resToEdit = new TextField("Resource:");
        resToEdit.setDebugId(this.getClass().getSimpleName()+"_resToEdit");
        resToEdit.setImmediate(false);
        resToEdit.addListener(this);
        resToEdit.setColumns(50);
		t2f.getLayout().addComponent(resToEdit);

        // initialize the footer area of the form
        HorizontalLayout t2ffooterlayout = new HorizontalLayout();
        t2f.setFooter(t2ffooterlayout);

        ontowikil = new Link("Edit with Ontowiki",
                new ExternalResource(state.getHostName() + "/ontowiki/view/?r=&m=http://mytest.com"));
        ontowikil.setTargetName("_blank");
        ontowikil.setTargetBorder(Link.TARGET_BORDER_NONE);
        ThemeResource ontoWikiIconl = new ThemeResource("app_images/OntoWiki.logo.png");
        ontowikil.setIcon(ontoWikiIconl);
        ontowikil.setEnabled(false);


        t2f.getFooter().addComponent(ontowikil);
        t2ffooterlayout.setComponentAlignment(ontowikil, Alignment.TOP_RIGHT);

        authoringTab.addComponent(t2f);

        final Panel panel = new Panel("LOD2 components interfaces");

        VerticalLayout panelContent = new VerticalLayout();

        Link l = new Link("Ontowiki",
                new ExternalResource(state.getHostName() + "/ontowiki/view/?r=&m=http://mytest.com"));
        l.setTargetName("_blank");
        l.setTargetBorder(Link.TARGET_BORDER_NONE);
        ThemeResource ontoWikiIcon = new ThemeResource("app_images/OntoWiki.logo.png");
        l.setIcon(ontoWikiIcon);
        panelContent.addComponent(l);


        panel.setContent(panelContent);
        authoringTab.addComponent(panel);


        // The composition root MUST be set
        setCompositionRoot(authoringTab);
    }

	public void textChange(TextChangeEvent event) {
		
		resourceToEdit = event.getText();
        if (resourceToEdit == null || resourceToEdit.equals("")) {
            ontowikil.setEnabled(false);
        } else {    
        if (state.getCurrentGraph() == null || state.getCurrentGraph().equals("")) {
            ontowikil.setEnabled(false);
        } else {    
            String Encoded = "";
            try {
                Encoded = URLEncoder.encode(resourceToEdit, "UTF-8");
                String encodedGraph = URLEncoder.encode(state.getCurrentGraph(), "UTF-8");
                ExternalResource o = new ExternalResource(
                    state.getHostName() + "/ontowiki/view/?r=" + Encoded + "&m=" + encodedGraph);
                ontowikil.setResource(o);
                ontowikil.setEnabled(true);
            } catch (UnsupportedEncodingException e) { 
                ontowikil.setEnabled(false);
                e.printStackTrace();
            };
        };
        };
		
	}

    private void activateGraph(ClickEvent event) {

	try {
		RepositoryConnection con = state.getRdfStore().getConnection();

		// initialize the hostname and portnumber
		String query = "create silent graph <" + activategraph.getValue() + ">"; 
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

	
    }
    //
	// propagate the information of one tab to another.
	public void setDefaults() {
		if (activategraph.getValue() == null || activategraph.getValue().equals("")) {    
			// on empty set the default value
			activategraph.setValue(state.getCurrentGraph());
		};
	};

}
