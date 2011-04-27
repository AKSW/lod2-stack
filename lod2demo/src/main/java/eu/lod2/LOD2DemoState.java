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
import virtuoso.sesame2.driver.VirtuosoRepository;

// import java.lang.RuntimeException;

public class LOD2DemoState
{
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
