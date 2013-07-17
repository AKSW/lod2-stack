
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
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Embedded OntoWiki tool
 */
//@SuppressWarnings("serial")
public class OntoWiki extends CustomComponent
{

  // reference to the global internal state
  private LOD2DemoState state;
	private String username;
	private String password;
	private String service;

  public OntoWiki(LOD2DemoState st) {

    // The internal state 
    state = st;
    initLogin();
    activateCurrentGraph();

    Embedded browser = new Embedded();
    try { 
			URL url = new URL(service + "/");
      browser = new Embedded("", new ExternalResource(url));
      browser.setType(Embedded.TYPE_BROWSER);
      browser.setSizeFull();
      //panel.addComponent(browser);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    };

    // The composition root MUST be set
    setCompositionRoot(browser);
  }

  // propagate the information of one tab to another.
  public void setDefaults() {
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
					String service0 = literalS.getLabel();
                    this.service=state.processService(service0,"ontowiki");
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


    private void activateCurrentGraph() {

	if ( ! state.getCurrentGraph().equals("")) {

	try {
		RepositoryConnection con = state.getRdfStore().getConnection();

		// initialize the hostname and portnumber
		String query = "create silent graph <" + state.getCurrentGraph() + ">"; 
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
    }
};

