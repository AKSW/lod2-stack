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

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;

/**
 * The extraction tab which collects information about 
 * ways and components to extract information.
 */
//@SuppressWarnings("serial")
public class QueryingTab extends CustomComponent
	implements TextChangeListener 
{

	private Panel sparqlResult = new Panel("Query Results");

	// reference to the global internal state
	private LOD2DemoState state;

	public QueryingTab(LOD2DemoState st) {

		// The internal state and 
		state = st;

		VerticalLayout queryingTab = new VerticalLayout();

		Form t2f = new Form();
		t2f.setCaption("Information source Querying");

		TextField graphname = new TextField("repository graph name:");
		// configure & add to layout
		graphname.setImmediate(true);
		graphname.addListener(this);
		graphname.setColumns(30);
		graphname.setRequired(true);
		graphname.setRequiredError("Name of the graph is missing. No query will be issued.");
		t2f.getLayout().addComponent(graphname);

		// initialize the footer area of the form
		HorizontalLayout t2ffooterlayout = new HorizontalLayout();
		t2f.setFooter(t2ffooterlayout);

		Button okbutton = new Button("List graph content", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				extractionQuery(event);
			}
		});
		okbutton.setDescription("View the result from the SPARQL query: 'select * from <graphname> where {?s ?p ?o.} LIMIT 100'");
		//								okbutton.addListener(this); // react to tclicks
		t2f.getFooter().addComponent(okbutton);
		t2ffooterlayout.setComponentAlignment(okbutton, Alignment.TOP_RIGHT);

		queryingTab.addComponent(t2f);

		final Panel t2components = new Panel("External components interfaces");

		VerticalLayout t2ComponentsContent = new VerticalLayout();

		Link t2l = new Link("Query via Ontowiki",
				new ExternalResource("http://localhost/ontowiki/"));
		t2l.setTargetName("_blank");
		t2l.setTargetBorder(Link.TARGET_BORDER_NONE);
		t2ComponentsContent.addComponent(t2l);

		t2components.setContent(t2ComponentsContent);
		queryingTab.addComponent(t2components);
		// put result panel at the bottom
		queryingTab.addComponent(sparqlResult);

		// The composition root MUST be set
		setCompositionRoot(queryingTab);
	}

	private void extractionQuery(ClickEvent event) {

		try {
			RepositoryConnection con = state.getRdfStore().getConnection();

			if (state.getCurrentGraph() == null | state.getCurrentGraph().equals("")) {

				sparqlResult.removeAllComponents();
				getWindow().showNotification("No query issued.");

			} else {
				//Initialize the result page
				sparqlResult.removeAllComponents();

				String query = "select * from <" + state.getCurrentGraph() + "> where {?s ?p ?o} LIMIT 100";
				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
				TupleQueryResult result = tupleQuery.evaluate();

				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					Value valueOfS = bindingSet.getValue("s");
					Value valueOfP = bindingSet.getValue("p");
					Value valueOfO = bindingSet.getValue("o");

					String triple = "<" + valueOfS.stringValue() + ">  <" + valueOfP.stringValue() + "> '" + valueOfO.stringValue() + "'"; 

					sparqlResult.addComponent(new Label(triple));

					// do something interesting with the values here...
				}
			}

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

	public void textChange(TextChangeEvent event) {
		
		state.setCurrentGraph(event.getText());

		
	}
};

