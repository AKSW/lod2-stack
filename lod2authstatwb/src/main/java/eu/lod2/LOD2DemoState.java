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

import com.turnguard.webid.tomcat.security.WebIDUser;
import com.vaadin.ui.Label;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
// import java.lang.RuntimeException;

public class LOD2DemoState
{
    // configuration store: this is an RDF graph with all configuration data installed.
    // We assume this graph is accessible via the Virtuoso connection.
    private String configurationRDFgraph = "http://localhost/lod2statworkbenchconfiguration";

    // The lod2 runtime configuration file. This holds information on the users in the
    // system and can be extended with further information that is important to the
    // running system.
    private String runtimeRDFgraph ="http://localhost/lod2runtime";

    //* the graph containing all the information on user rights
    private String userGraph= "http://webidrealm.localhost/rolegraph";

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

    // for ckan
    public String CKANApiKey = "";
    public String CKANUser = "";
    public String CKANPwd = "";


    public Repository rdfStore;

    public Boolean InitStatus = false;
    public String ErrorMessage = "true";

    private HttpServletRequest lastRequest;
    private String provenanceGraph = "http://lod2.eu/provenance";

    public String adminRole = "http://demo.lod2.eu/Role/Administrator";

    // initialize the state with an default configuration
    // After succesfull initialisation the rdfStore connection is an active connection
    public LOD2DemoState() {

        readConfiguration();

        try{
            StoreFactory storeFactory=new StoreFactory();
            Repository configurator=storeFactory.fetchConfiguration("/etc/lod2statworkbench/storeconfig.rdf");
            Map<String,Repository> stores=storeFactory.buildRepositories(configurator);
            rdfStore=stores.get("http://localhost/mainrepos");
            configurator.shutDown();
        }catch(Exception e){
            ErrorMessage = "Could not fetch the store configuration";
            e.printStackTrace();
        }

        try {
            rdfStore.initialize();
        } catch (RepositoryException e) {
            ErrorMessage = "Initialization connection to Virtuoso failed";
            e.printStackTrace();
        };

        String Filename = "/etc/lod2statworkbench/configuration.rdf";

        try {
            RepositoryConnection con = rdfStore.getConnection();

            File configurationFile = new File(Filename);
            Resource contextURI = con.getValueFactory().createURI(configurationRDFgraph);
            Resource[] contexts = new Resource[]{contextURI};

                // first empty the graph as the repository(Virtuoso) appends triples to a graph with the add.
            con.clear(contexts);
            con.add(configurationFile, "http://lod2.eu/", RDFFormat.RDFXML, contexts);

            // initialize the hostname and portnumber
            String query = "select ?h from <" + configurationRDFgraph + "> where {<" + configurationRDFgraph + "> <http://lod2.eu/lod2statworkbench/hostname> ?h} LIMIT 100";
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

        this.loadRuntimeConfig();

        InitStatus = new Boolean(ErrorMessage);

    }

    // initialize the state with a graphname
    public LOD2DemoState(String graphname) {
        this();
        currentGraph = graphname;
    };

    /**
     * Loads the runtime configuration for the demonstrator into the virtuoso store. Removes the previous configuration
     * if any.
     */
    private void loadRuntimeConfig(){
        String Filename = "/etc/lod2statworkbench/lod2runtime.rdf";

        try {
            RepositoryConnection con = rdfStore.getConnection();

            File configurationFile = new File(Filename);
            Resource contextURI = con.getValueFactory().createURI(runtimeRDFgraph);

            // first empty the graph to remove any previous configuration
            con.clear(contextURI);
            con.add(configurationFile, "http://lod2.eu/", RDFFormat.RDFXML, contextURI);
        } catch (IOException e) {
            ErrorMessage = "the configuration file is not readable or present:" + Filename;
            e.printStackTrace();
        } catch (RepositoryException e) {
            ErrorMessage = "Query execution failed due to problems with the repository.";
            e.printStackTrace();
        } catch (RDFParseException e) {
            ErrorMessage = "the configuration graph url is incorrect.";
            e.printStackTrace();
        }
    }

    // accessors
    public String getCurrentGraph() {
        return currentGraph;
    };

    // accessors
    public String getHostName() {
        return hostname;
    };

    public String getHostNameWithoutPort() {
        if(hostname.matches(".*:\\d+")){
            int portIdx=hostname.lastIndexOf(":");
            if(portIdx>=0){
                return hostname.substring(0,portIdx);
            }else{
                return hostname;
            }
        }else{
            return hostname;
        }

    }

    /**
     * updates the current graph to the given graphname. Also updates any currentgraph listeners
     * @param graphname the new current graph
     */
    public void setCurrentGraph(String graphname) {
        currentGraph = graphname;
        cGraph.setValue(graphname);
        this.informCurrentGraphListeners();
    }

    private Set<CurrentGraphListener> currentGraphListeners=new HashSet<CurrentGraphListener>();

    /**
     * Adds the given listener to the set of graph listeners. Immediately notifies the listener of the current graph.
     * @param listener the listener to add
     */
    public void addCurrentGraphListener(CurrentGraphListener listener){
        this.currentGraphListeners.add(listener);
        listener.notifyCurrentGraphChange(this.getCurrentGraph());
    }

    /**
     * Removes the given listener from the set of current graph listeners if it is present
     * @param listener the listener to remove
     */
    public void removeCurrentGraphListener(CurrentGraphListener listener){
        this.currentGraphListeners.remove(listener);
    }

    /**
     * Sends an update with the current graph to the current graph listeners
     */
    public void informCurrentGraphListeners(){
        Set<CurrentGraphListener> listeners=new HashSet<CurrentGraphListener>(this.currentGraphListeners);
        String currentGraph=this.getCurrentGraph();
        for(CurrentGraphListener listener: listeners){
            listener.notifyCurrentGraphChange(currentGraph);
        }
    }

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
    }

    public String getUserGraph() {
        return userGraph;
    }

    //* returns the runtime RDF graph
    public String getRuntimeRDFgraph(){
        return this.runtimeRDFgraph;
    }

    // a method to reconnect to the rdfStore.
    public void reconnectRdfStore() {
        try {
            rdfStore.initialize();
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
    }

    // read the local configuration file /etc/lod2statworkbench/lod2statworkbench.conf
    private void readConfiguration() {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("/etc/lod2statworkbench/lod2statworkbench.conf"));
            jDBCconnectionstring = properties.getProperty("JDBCconnection");
            jDBCuser             = properties.getProperty("JDBCuser");
            jDBCpassword         = properties.getProperty("JDBCpassword");
            uploadDir            = properties.getProperty("uploadDirectory");
            googleAnalyticsID    = properties.getProperty("googleAnalyticsID", "");
            googleAnalyticsDomain = properties.getProperty("googleAnalyticsDomain", "");

            CKANApiKey = properties.getProperty("CKANApiKey");
            CKANUser = properties.getProperty("CKANUser");
            CKANPwd = properties.getProperty("CKANPwd");

            userGraph = properties.getProperty("UserGraph");
            provenanceGraph = properties.getProperty("ProvenanceGraph");

            //		System.print("$"+jDBCuser+"$");
            //		System.print("$"+jDBCpassword+"$");

        } catch (IOException e) {
            // the file is absent or has faults in configuration settings
            ErrorMessage = "Reading configuration file in /etc/lod2statworkbench/lod2statworkbench.conf failed.";
            // print more detail to catalina logs
            e.printStackTrace();
        };
    }

    //* the currently logged in user
    private WebIDUser user;

    /**
     * Sets the currently logged in user to the given user. If the user has the same URI, no notification is sent, as the
     * information on the user is assumed to be kept the same. If the URI changes or the user changes from or to null, an
     * update is sent.
     * @param user the WebIDUser to change to
     */
    public void setUser(WebIDUser user){
        String previousUser=this.user == null? null : this.user.getURI().toString();
        String userUri= user==null?null:user.getURI().toString();
        this.user = user;

        if((previousUser== null && user==null) ||
            (previousUser!=null && previousUser.equals(userUri))){
            // no change of importance
            return;
        }

        if(this.user!=null){
            try{
                // a new user has arrived, lets see if we have a role for him. If we don't, lets make him a 'User'
                RepositoryConnection con=rdfStore.getConnection();
                String userGraph=getUserGraph();
                boolean hasRole= con.prepareBooleanQuery(QueryLanguage.SPARQL, "ASK FROM <"+userGraph+"> " +
                    "Where { <"+userUri+"> <http://schema.turnguard.com/webid/2.0/core#hasRole> ?value }").evaluate();

                if(!hasRole){
                    // add the new role if the user does not have one yet
                    ValueFactory thePlant=con.getValueFactory();
                    Statement product = thePlant.createStatement((Resource)
                            user.getURI(), new URIImpl("http://schema.turnguard.com/webid/2.0/core#hasRole"), new URIImpl("http://demo.lod2.eu/Role/User"));
                    con.add(product, new URIImpl(userGraph));
                }
            }catch(Exception e){
                throw new RuntimeException("Could not verify user privileges because of a server configuration issue, " +
                        "please notify the development team. The error message was: "+e.getMessage());
            }
        }
        // an important change happened, lets get everyone up to date!
        for(LoginListener listener : loginListeners){
            this.notifyListener(listener);
        }
    }

    //* returns the currently logged in user.
    public WebIDUser getUser(){
        return this.user;
    }

    //* the currently subscribed loginListeners
    protected HashSet<LoginListener> loginListeners=new HashSet<LoginListener>();

    /**
     * adds the given login listener as an actively subscribed listener. The listener
     * immediately receives a notification.
     */
    public void addLoginListener(LoginListener listener){
        this.loginListeners.add(listener);
        this.notifyListener(listener);
    }

    /**
     * removes the given listener from the set of listeners
     * @param listener :: the listener to remove
     */
    public void removeLoginListener(LoginListener listener){
        this.loginListeners.remove(listener);
    }

    //* notifies the given listener that of the current user.
    protected void notifyListener(LoginListener listener){
        listener.notifyLogin(this.getUser());
    }

    public String getProvenanceGraph() {
        return provenanceGraph;
    }

    public interface LoginListener {
        /**
         * the listener is informed that the user has changed. The logged in user is
         * provided if it exists.
         * @param user :: the user that is currently logged in or null if no such user exists.
         */
        public void notifyLogin(WebIDUser user);
    }

    public interface CurrentGraphListener {
        /**
         * the listener is informed that the current graph has changed. The current graph is provided.
         * @param graph tha current graph
         */
        public void notifyCurrentGraphChange(String graph);
    }
}


