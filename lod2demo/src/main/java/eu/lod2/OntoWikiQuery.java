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
 * OntoWiki SPARQL Querying editor
 * Important the current graph has to be activated for OntoWiki
 */
//@SuppressWarnings("serial")
public class OntoWikiQuery extends CustomComponent
{

	// reference to the global internal state
	private LOD2DemoState state;
	private String username;
	private String password;
	private String service;

	public OntoWikiQuery(LOD2DemoState st) {



		// The internal state and 
		state = st;
		initLogin();

		VerticalLayout queryingTab = new VerticalLayout();

		final String query = "SELECT * where {?s ?p ?o.} LIMIT 20";
		String encodedQuery = "";
		String encodedGraphName= "";
		try {
			encodedQuery = URLEncoder.encode(query, "UTF-8");
			encodedGraphName = URLEncoder.encode(state.getCurrentGraph(), "UTF-8");
		    } catch (UnsupportedEncodingException e) { 
			e.printStackTrace();
		    };

		try {
	    		java.net.URL data = new java.net.URL(service + "/application/login?logintype=locallogin&password=" + password + "&username=" + username);
		} catch (IOException e) {
			e.printStackTrace();
		};

	        Label l = new Label(service + "/queries/editor/?query="+ encodedQuery + "&m=" + encodedGraphName);
		queryingTab.addComponent(l);

		Embedded browser = new Embedded();
		try { 
			URL url;
			if (encodedGraphName.equals("")) {
				url = new URL(service + "/queries/editor/?query="+ encodedQuery);
			} else {
				url = new URL(service + "/queries/editor/?query="+ encodedQuery + "&m=" + encodedGraphName);
			};
			browser = new Embedded("", new ExternalResource(url));
			browser.setType(Embedded.TYPE_BROWSER);
			browser.setSizeFull();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		};

		// The composition root MUST be set
		setCompositionRoot(browser);
	};

	private void initLogin() {
	try {
		RepositoryConnection con = state.getRdfStore().getConnection();

		// initialize the hostname and portnumber
		String query = "select ?u ?p ?s from <" + state.getConfigurationRDFgraph() + "> where {<" + state.getConfigurationRDFgraph() + "> <http://lod2.eu/lod2demo/configures> <http://localhost/ontowiki>. <http://localhost/ontowiki> <http://lod2.eu/lod2demo/password> ?p. <http://localhost/ontowiki> <http://lod2.eu/lod2demo/username> ?u. <http://localhost/ontowiki> <http://lod2.eu/lod2demo/service> ?s.} LIMIT 100";
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult result = tupleQuery.evaluate();
		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			Value valueOfH = bindingSet.getValue("u");
			if (valueOfH instanceof LiteralImpl) {
				LiteralImpl literalH = (LiteralImpl) valueOfH;
				username = literalH.getLabel();
				};	
			Value valueOfP = bindingSet.getValue("p");
			if (valueOfP instanceof LiteralImpl) {
				LiteralImpl literalP = (LiteralImpl) valueOfP;
				password = literalP.getLabel();
				};	
			Value valueOfS = bindingSet.getValue("s");
			if (valueOfS instanceof LiteralImpl) {
				LiteralImpl literalS = (LiteralImpl) valueOfS;
				service = literalS.getLabel();
				};	
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


};

