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
import java.util.*;

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

		if (encodedGraphName.equals("")) {
			Label l = new Label("One must select a current graph to use this functionality.");
			queryingTab.addComponent(l);
		} else {
			String redirecturi = service + "/queries/editor/?query="+ encodedQuery + "&m=" + encodedGraphName;
			URL request = loginandqueryRequest(redirecturi);
			if (request == null) {
				Label l = new Label("The settings are incorrect to result in a valid URL which gives access to this functionality.");
				queryingTab.addComponent(l);
			} else {
				Embedded browser = new Embedded("", new ExternalResource(request));
				browser.setType(Embedded.TYPE_BROWSER);
				browser.setSizeFull();
				queryingTab.addComponent(browser);
			}
		}
		/*
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

		   queryingTab.addComponent(browser);
		 */
		// The composition root MUST be set
		queryingTab.setSizeFull();
		setCompositionRoot(queryingTab);
	};

	private void initLogin() {
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();

			// initialize the hostname and portnumber
			String query = "select ?u ?p ?s from <" + state.getConfigurationRDFgraph() + "> where {<" + state.getConfigurationRDFgraph() + "> <http://lod2.eu/lod2statworkbench/configures> <http://localhost/ontowiki>. <http://localhost/ontowiki> <http://lod2.eu/lod2statworkbench/password> ?p. <http://localhost/ontowiki> <http://lod2.eu/lod2statworkbench/username> ?u. <http://localhost/ontowiki> <http://lod2.eu/lod2statworkbench/service> ?s.} LIMIT 100";
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
					String service0 = literalS.getLabel();
					if (service0 == null | service0.equals("")) {
						service = "http://localhost/ontowiki";
					} else {
						service = service0;
					};
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


	private URL loginandqueryRequest(String redirecturi) {

		URL loginandqueryRequestURL = null ;
		String encodedRedirectUri = "";
		try {
			encodedRedirectUri= URLEncoder.encode(redirecturi, "UTF-8");
		} catch (UnsupportedEncodingException e) { 
			e.printStackTrace();
		};

		if (!(service.equals("") | encodedRedirectUri.equals(""))) {

			try {
				loginandqueryRequestURL = new URL(service + "/application/login" +
						"?logintype=locallogin&password=" + password + "&username=" + username +
						"&redirect-uri=" + encodedRedirectUri
						); 
				} catch (MalformedURLException ex) {
					System.err.println(ex);
				}
			}

			return loginandqueryRequestURL;
    };

		private void loginRequest() {
			try {
				java.net.URL loginrequest = new java.net.URL(service + "/application/login"); 
				URLConnection connection = loginrequest.openConnection();
				//			connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)");
				connection.setDoOutput(true);
				OutputStreamWriter post = new OutputStreamWriter(connection.getOutputStream());
				//      			post.write("logintype=locallogin&password=" + password + "&username=" + username);
				post.write("logintype=locallogin&password=dba&username=dba");
				post.flush();
				/*
				   Map headers = connection.getHeaderFields();
				   Iterator it = headers.keySet().iterator();
				   while (it.hasNext()) {
				   String key = (String)it.next();
				//l.addComponent(new Label(key+": "+headers.get(key)));
				}
				System.out.println();
				 */
				BufferedReader in = null;
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line=null;
				//			while ((line=in.readLine()) != null)
				//				l.addComponent(new Label(line));

				post.close();
				in.close();
			}
			catch (MalformedURLException ex) {
				System.err.println(ex);
			}
			catch (FileNotFoundException ex) {
				System.err.println("Failed to open stream to URL: "+ex);
			}
			catch (IOException ex) {
				System.err.println("Error reading URL content: "+ex);
			}

		};

	};

