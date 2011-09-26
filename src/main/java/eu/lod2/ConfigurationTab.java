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
    	private ComboBox graphSelector;

	public ConfigurationTab(LOD2DemoState st, Label cg) {

		// The internal state and 
		state = st;
		currentgraph = cg;

		VerticalLayout configurationTab = new VerticalLayout();

	    // Configuration form start
        // Set all properties at once for the moment.
        Form t2f = new Form();
        t2f.setCaption("Configuration");

	// the localhost ip-address
        TextField hostname = new TextField("Hostname:", state.getHostName());
        hostname.setColumns(50);
        hostname.setReadOnly(true);
        t2f.getLayout().addComponent(hostname);


	// the graph selector
	// it displays all acceptable graphs in Virtuoso 
	// XXX TODO show only those which are editable in OntoWiki
	graphSelector = new ComboBox("Select default graph: ");
	addCandidateGraphs(graphSelector);
	if (cg.getValue() != null 
			&& cg.getValue() != "no current  graph selected"
			&& cg.getValue() != "null"
			) {
		graphSelector.setValue(cg.getValue());
		graphSelector.setColumns(cg.toString().length());
	};
	t2f.getLayout().addComponent(graphSelector);

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

        configurationTab.addComponent(t2f);

        // Configuration form end


		// The composition root MUST be set
		setCompositionRoot(configurationTab);
	}

    private void storeConfiguration(ClickEvent event) {
       state.setCurrentGraph((String) graphSelector.getValue());
       currentgraph.setValue((String) graphSelector.getValue());

    };

	// propagate the information of one tab to another.
	public void setDefaults() {
		graphSelector.setValue(state.getCurrentGraph());
	};

	public void addCandidateGraphs(AbstractSelect selection) {
	// SELECT ID_TO_IRI(REC_GRAPH_IID) AS GRAPH FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH

	try {
		RepositoryConnection con = state.getRdfStore().getConnection();

		// initialize the hostname and portnumber
		String query = "SELECT  DISTINCT ?g { GRAPH  ?g   { ?s  ?p  ?o } } limit 100";
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult result = tupleQuery.evaluate();


		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			Value valueOfG = bindingSet.getValue("g");
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
};

