
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
import org.openrdf.model.impl.*;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;


import org.restlet.resource.ClientResource;
import org.restlet.data.MediaType;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;

/**
 * The ELoadRDFFile allows to upload a file into virtuoso via the conductor
 */
//@SuppressWarnings("serial")
public class ELoadRDFFile extends CustomComponent
{

    // reference to the global internal state
    private LOD2DemoState state;

    private String virtuoso_username = "";
    private String virtuoso_password = "";
    private String virtuoso_service  = "";

    public ELoadRDFFile(LOD2DemoState st) {

        // The internal state and 
        state = st;

	initVirtuoso();

	Embedded browser = new Embedded();
	try { 
		URL url;
		if (virtuoso_username.equals("") || virtuoso_password.equals("")) {
			url = new URL("http://localhost:8890/conductor/rdf_import.vspx?username=dba&t_login_pwd=dba&password=dba");
		} else if (virtuoso_service.equals("")) {
			url = new URL(state.getHostName() + "/conductor/rdf_import.vspx?username=" + virtuoso_username+ "&t_login_pwd=" + virtuoso_password + "&password=" + virtuoso_password);
		} else {
			url = new URL(virtuoso_service + "/rdf_import.vspx?username=" + virtuoso_username+ "&t_login_pwd=" + virtuoso_password + "&password=" + virtuoso_password);
		};
		browser = new Embedded("", new ExternalResource(url));
		browser.setType(Embedded.TYPE_BROWSER);
		browser.setSizeFull();
		//panel.addComponent(browser);
	} catch (MalformedURLException e) {
                e.printStackTrace();
	};

        browser.setSizeUndefined();
        setSizeUndefined();

        // The composition root MUST be set
        setCompositionRoot(browser);
    }

	// propagate the information of one tab to another.
	public void setDefaults() {
	};

	public void initVirtuoso() {
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();

			// initialize the hostname and portnumber
			String query = "select ?u ?p ?s from <" + state.getConfigurationRDFgraph() + "> where {<" + state.getConfigurationRDFgraph() + "> <http://lod2.eu/lod2statworkbench/configures> <http://localhost/virtuoso>. <http://localhost/virtuoso> <http://lod2.eu/lod2statworkbench/password> ?p. <http://localhost/virtuoso> <http://lod2.eu/lod2statworkbench/username> ?u. OPTIONAL { <http://localhost/virtuoso> <http://lod2.eu/lod2statworkbench/service> ?s.}} LIMIT 100";
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
			TupleQueryResult result = tupleQuery.evaluate();
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value valueOfH = bindingSet.getValue("u");
				if (valueOfH instanceof LiteralImpl) {
					LiteralImpl literalH = (LiteralImpl) valueOfH;
					virtuoso_username = literalH.getLabel();
				};	
				Value valueOfP = bindingSet.getValue("p");
				if (valueOfP instanceof LiteralImpl) {
					LiteralImpl literalP = (LiteralImpl) valueOfP;
					virtuoso_password = literalP.getLabel();
				};	
				Value valueOfS = bindingSet.getValue("s");
				if (valueOfS != null && valueOfS instanceof LiteralImpl) {
					LiteralImpl literalS = (LiteralImpl) valueOfS;
					String service0 = literalS.getLabel();
					if (service0 == null | service0.equals("")) {
						virtuoso_service = "http://localhost:8890/conductor";
					} else {
						virtuoso_service = service0;
					};
				} else {
					virtuoso_service = "http://localhost:8890/conductor";
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

