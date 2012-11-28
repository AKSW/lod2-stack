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
import org.openrdf.rio.*;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import virtuoso.sesame2.driver.VirtuosoRepository;

import java.util.Properties;
import java.io.*;

import com.vaadin.ui.Label;
// import java.lang.RuntimeException;

public class LOD2DemoState
{
    // configuration store: this is an RDF graph with all configuration data installed.
    // We assume this graph is accessible via the Virtuoso connection.
    private String configurationRDFgraph = "http://localhost/lod2democonfiguration";

    // the hostname and portnumber where the tools are installed.
    private String hostname = "http://localhost:8080";

    // the default graph on which the queries and actions will be performed
    private String currentGraph = "";
    public  Label cGraph = new Label("no current graph selected");

    // The virtuoso repository
    private String jDBCconnectionstring = "jdbc:virtuoso://localhost:1111";
    private String jDBCuser = "dba";
    private String jDBCpassword = "pwd";

    // the upload Directory for the application
    private String uploadDir = "/tmp/uploads";

    // for googleAnalytics
    public String googleAnalyticsID = "";
    public String googleAnalyticsDomain = "";

    public Repository rdfStore;

    public Boolean InitStatus = false;
    public String ErrorMessage = "true";

    // initialize the state with an default configuration
    // After succesfull initialisation the rdfStore connection is an active connection 
    public LOD2DemoState() {

        readConfiguration();
        rdfStore = new VirtuosoRepository(jDBCconnectionstring, jDBCuser, jDBCpassword);
        try {
            rdfStore.initialize();
        } catch (RepositoryException e) {
            ErrorMessage = "Initialization connection to Virtuoso failed";
            e.printStackTrace();
        };

        String Filename = "/etc/lod2demo/configuration.rdf";

        try {
            RepositoryConnection con = rdfStore.getConnection();


            File configurationFile = new File(Filename);
            Resource contextURI = con.getValueFactory().createURI(configurationRDFgraph);
            Resource[] contexts = new Resource[]{contextURI};

                // first empty the graph as the repository(Virtuoso) appends triples to a graph with the add.
            con.clear(contexts);
            con.add(configurationFile, "http://lod2.eu/", RDFFormat.RDFXML, contexts);

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

        } catch (IOException e) {
            ErrorMessage = "the configuration file is not readable or present:" + Filename;
            e.printStackTrace();
        } catch (RepositoryException e) {
            ErrorMessage = "Query execution failed due to problems with the repository.";
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            ErrorMessage = "Query execution failed due to malformed query.";
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            ErrorMessage = "Query execution failed due to query evaluation problem.";
            e.printStackTrace();
        } catch (RDFParseException e) {
            ErrorMessage = "the configuration graph url is incorrect.";
            e.printStackTrace();
        }

        InitStatus = new Boolean(ErrorMessage);

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
        cGraph.setValue(graphname);
    };


    public Repository getRdfStore() {
        return rdfStore;
    };

    public String getUploadDir(){
        return uploadDir;
    }

    public void setUploadDir(String ud) {
        uploadDir = ud;
    }



    public String getConfigurationRDFgraph() {
        return configurationRDFgraph;
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

    // read the local configuration file /etc/lod2demo/lod2demo.conf
    private void readConfiguration() {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("/etc/lod2demo/lod2demo.conf"));
            jDBCconnectionstring = properties.getProperty("JDBCconnection");
            jDBCuser             = properties.getProperty("JDBCuser");
            jDBCpassword         = properties.getProperty("JDBCpassword");
            uploadDir            = properties.getProperty("uploadDirectory");
            googleAnalyticsID    = properties.getProperty("googleAnalyticsID", "");
            googleAnalyticsDomain = properties.getProperty("googleAnalyticsDomain", "");


            //		System.print("$"+jDBCuser+"$");
            //		System.print("$"+jDBCpassword+"$");

        } catch (IOException e) {
            // the file is absent or has faults in configuration settings
            ErrorMessage = "Reading configuration file in /etc/lod2demo/lod2demo.conf failed.";
            // print more detail to catalina logs
            e.printStackTrace();
        };
    }

}
