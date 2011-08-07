/*
 * Copyright 2011 LOD2.eu consortium
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

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.model.*;
import org.openrdf.model.impl.*;
import virtuoso.sesame2.driver.VirtuosoRepository;

// import java.lang.RuntimeException;

public class LOD2DemoState
{
	// configuration store: this is an RDF graph with all configuration data installed.
	// We assume this graph is accessible via the Virtuoso connection.
	private String configurationRDFgraph = "http://localhost/lod2democonfiguration";

	// the hostname and portnumber where the tools are installed.
	private String hostname = "http://localhost:8080";

	// the default graph on which the queries and actions will be performed
	private String currentGraph;

	// The virtuoso repository
	public Repository rdfStore;

	// initialize the state with an default configuration
	// After succesfull initialisation the rdfStore connection is an active connection 
	public LOD2DemoState() {

    	rdfStore = new VirtuosoRepository("jdbc:virtuoso://localhost:1111", "dba", "dba");
	try {
		rdfStore.initialize();
	} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	};

	try {
		RepositoryConnection con = rdfStore.getConnection();

		// initialize the hostname and portnumber
		String query = "select ?h from <" + configurationRDFgraph + "> where {<" + configurationRDFgraph + "> <http://lod2.eu/lod2demo/hostname> ?h} LIMIT 100";
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult result = tupleQuery.evaluate();
		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			Value valueOfH = bindingSet.getValue("h");
			if (valueOfH instanceof LiteralImpl) {
				LiteralImpl literalH = (LiteralImpl) valueOfH;
				hostname = "http://" +literalH.getLabel();
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

	}
	
	// initialize the state with a graphname
	public LOD2DemoState(String graphname) {
		this();
		currentGraph = graphname;
	};


	// accessors
	public String getCurrentGraph() {
		return currentGraph;
	};
	
	// accessors
	public String getHostName() {
		return hostname;
	};

	public void setCurrentGraph(String graphname) {
		currentGraph = graphname;
	};


	public Repository getRdfStore() {
		return rdfStore;
	};

	// a method to reconnect to the rdfStore.
	public void reconnectRdfStore() {
		try {
			rdfStore.initialize();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	};
		
}
