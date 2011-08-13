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

	public OntoWikiQuery(LOD2DemoState st) {

		// The internal state and 
		state = st;

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


		Embedded browser = new Embedded();
		try { 
			URL url = new URL(state.getHostName() + "/ontowiki/queries/editor/?query="+ encodedQuery + "&m=" + encodedGraphName);
			browser = new Embedded("", new ExternalResource(url));
			browser.setType(Embedded.TYPE_BROWSER);
			browser.setSizeFull();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		};

		// The composition root MUST be set
		setCompositionRoot(browser);
	}

};

