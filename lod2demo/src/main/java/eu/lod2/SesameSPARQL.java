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
 * Query the current graph via the Sesame interface.
 */
//@SuppressWarnings("serial")
public class SesameSPARQL extends CustomComponent
	implements TextChangeListener 
{

	private Panel sparqlResult = new Panel("Query Results");

	// reference to the global internal state
	private LOD2DemoState state;

	// queryform
	private TextArea query;

	public SesameSPARQL(LOD2DemoState st) {

		// The internal state and 
		state = st;

		VerticalLayout queryingTab = new VerticalLayout();

		query = new TextArea("SPARQL Query");

		// configure & add to layout
		query.setImmediate(false);
		query.addListener(this);
		query.setColumns(30);
        	query.setRows(10);
		query.setRequired(true);
		query.setRequiredError("The query is missing. No call will be issued.");

		Button okbutton = new Button("List graph content", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				extractionQuery(event);
			}
		});
		okbutton.setDescription("View the result from the SPARQL query");
		//								okbutton.addListener(this); // react to tclicks
		
		queryingTab.addComponent(query);
		queryingTab.addComponent(okbutton);
		queryingTab.addComponent(sparqlResult);


		// The composition root MUST be set
		setCompositionRoot(queryingTab);
	}

	private void extractionQuery(ClickEvent event) {

		try {
			RepositoryConnection con = state.getRdfStore().getConnection();

			String queryValue = (String) query.getValue();

			if (queryValue.equals("")) {

				sparqlResult.removeAllComponents();
				getWindow().showNotification("No query issued.");

			} else {
				//Initialize the result page
				sparqlResult.removeAllComponents();

				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryValue);
				TupleQueryResult result = tupleQuery.evaluate();
			
				String statements = "";
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					Value valueOfS = bindingSet.getValue("s");
					Value valueOfP = bindingSet.getValue("p");
					Value valueOfO = bindingSet.getValue("o");

					String objectType = "";
					String objectString = "";
				        if (valueOfO instanceof LiteralImpl) {
						objectType = "literal";
						LiteralImpl literalO = (LiteralImpl) valueOfO;
						objectString = "\"" + literalO.getLabel() + "\" ^^ <" + literalO.getDatatype() + ">";

					};	
				        if (valueOfO instanceof URIImpl) {
						objectType = "resource";
						objectString = "<" + valueOfO.stringValue() + ">";
					};	

					String triple = "<" + valueOfS.stringValue() + ">  <" + valueOfP.stringValue() + "> " + 
							objectString; 

					statements = statements + "\n" + triple;


					// do something interesting with the values here...
				}
				TextArea resultArea = new TextArea("", statements);
				resultArea.setReadOnly(true);
				resultArea.setColumns(0);
				resultArea.setRows(30);
				sparqlResult.addComponent(resultArea);
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
		
	//	activateQuery();
	}

	// propagate the information of one tab to another.
	public void setDefaults() {
	};
/*
	private void activateQuery() {
		if (query.equals("")) {
		    ontowikiquerylink.setEnabled(false);
		} else {    
		    final String query = "SELECT * where {?s ?p ?o.} LIMIT 20";
		    String encoded = "";
		    try {
			encoded = URLEncoder.encode(query, "UTF-8");
			String encodedGraph = URLEncoder.encode(querygraph, "UTF-8");
			ExternalResource o = new ExternalResource(
			    state.getHostName() + "/ontowiki/queries/editor/?query=" + encoded + "&m=" + encodedGraph);
			ontowikiquerylink.setResource(o);
			ontowikiquerylink.setEnabled(true);
		    } catch (UnsupportedEncodingException e) { 
			ontowikiquerylink.setEnabled(false);
			e.printStackTrace();
		    };
		};
	};
	*/
};

