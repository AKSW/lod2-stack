package eu.lod2;

import org.openrdf.model.Resource;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import virtuoso.sesame2.driver.VirtuosoRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a factory for creating sparql stores. For the moment only a small set of repositories is supported.
 * The stores to be loaded are configured using an rdf-xml file with the properties as specified by the
 * properties below.
 *
 * To build the stores specified in the configuration file, call eu.lod2.StoreFactory#buildRepositories(org.openrdf.repository.Repository).
 * TODO allows other types of stores as well.
 */
public class StoreFactory {

    public static enum StoreFactoryArgument{
        //* the delay before the persistent memory store saves to disk (optional)
        syncDelay {
            @Override
            public String getURI() {
                return "http://lod2.eu/config/syncdelay";
            }

            @Override
            public String getArgumentString() {
                return "syncdelay";
            }
        },
        //* the jdbc connection string to use when using a virtuoso repository
        JDBCConnection {
            @Override
            public String getURI(){
                return "http://lod2.eu/config/jdbcConnect";
            }

            @Override
            public String getArgumentString(){
                return "jdbcConnect";
            }
        },

        //* the user to use when connecting` to the jdbc db when using a virtuoso repository
        JDBCuser {
            @Override
            public String getURI(){
                return "http://lod2.eu/config/jdbcUser";
            }
            @Override
            public String getArgumentString(){
                return "jdbcUser";
            }
        },

        //* the password to use when connecting to the jdbc db when using a virtuoso repository
        JDBCpwd {
            @Override
            public String getURI(){
                return "http://lod2.eu/config/jdbcPWD";
            }
            @Override
            public String getArgumentString(){
                return "jdbcPWD";
            }
        },


        //* the predicate that is used when defining which files to load to a store (optional)
        loadPredicate {
                @Override
                public String getURI(){
                    return "http://lod2.eu/config/load";
                }
                @Override
                public String getArgumentString(){
                    return "load";
                }
            },


        //* the uri that can be used to represent the repository class
        repos {
            @Override
            public String getURI(){
                return "http://lod2.eu/config/repository";
            }
            @Override
            public String getArgumentString(){
                return "repository";
            }
        },


        //* the predicate to use when selecting a repository type
        storeType {
            @Override
            public String getURI(){
                return "http://lod2.eu/config/storeType";
            }
            @Override
            public String getArgumentString(){
                return "storeType";
            }
        },


        //* the predicate that is used to specify the indexes for a native repository (optional)
        indexes {
            @Override
            public String getURI(){
                return "http://lod2.eu/config/indexes";
            }
            @Override
            public String getArgumentString(){
                return "indexes";
            }
        },

        /**
        * the predicate that is used to specify the file (directory!) to use for a
        * persistent memory store or a native store. The contents of this directory may be
        * overwritten to house the contents of the store.
        */
        filename{
            @Override
            public String getURI(){
                return "http://lod2.eu/config/fileName";
            }
            @Override
            public String getArgumentString(){
                return "fileName";
            }
        };

        public abstract String getURI();
        public abstract String getArgumentString();
    }



    /**
     * Turns the provided hashmap into a memory store. The hashmap is like a json object having props and either strings or lists (can be extended).
     * When using a hashmap, only a single repos can be created atm.
     */
    public static Repository mapToMemStore(HashMap<String,Object> config) throws Exception{

        Repository configurator= new SailRepository(new MemoryStore());
        RepositoryConnection connection = configurator.getConnection();

        URIImpl storeURI= new URIImpl("http://localhost/configurationStore");
        connection.add(new StatementImpl(storeURI,new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                new URIImpl(StoreFactoryArgument.repos.getURI())));

        for(String prop : config.keySet()){
            StoreFactoryArgument argument = null;

            for(StoreFactoryArgument check : StoreFactoryArgument.values()){
                if(check.getArgumentString().toLowerCase().equals(prop.toLowerCase())){
                    argument=check;
                }
            }
            if(argument==null){
                // unknown property
                System.out.println("Found unknown store configuration property, called "+prop);
            }else{
                Object value = config.get(prop);
                if(value instanceof String){
                    connection.add(new StatementImpl(storeURI, new URIImpl(argument.getURI()), new LiteralImpl((String)value)));
                }else if(value instanceof List){
                    for(Object o : (List)value){
                        connection.add(new StatementImpl(storeURI, new URIImpl(argument.getURI()), new LiteralImpl((String)o)));
                    }
                }else{
                    System.out.println("Cannot handle value " + value.toString());
                }
            }
        }

        connection.close();
        return configurator;
    }
    /**
     * Creates all repositories that have been specified in the configuration file
     * @param config : the configuration file that determines which repositories to create
     * @return a map from name to repository.
     */
    public Map<String,Repository> buildRepositories(Repository config)
            throws MalformedQueryException, RepositoryException, QueryEvaluationException,
            IllegalAccessException, IOException, RDFParseException {
        RepositoryConnection con = config.getConnection();
        // find the repositories to be configured
        String lookup =
                "SELECT " +
                        "?repos "+
                "WHERE {"+
                        "?repos a <" + StoreFactoryArgument.repos.getURI()+
                ">}";
        TupleQueryResult result=con.prepareTupleQuery(QueryLanguage.SPARQL, lookup).evaluate();

        Map<String,Repository> reposmap=new HashMap<String, Repository>();
        while(result.hasNext()){
            BindingSet bindings=result.next();
            Resource repos=(Resource)bindings.getValue("repos");
            String name=repos.stringValue();
            reposmap.put(name,this.setupStore(repos, config));
        }

        con.close();

        return reposmap;
    }

    /**
     * Parses the given configuration string and returns a repository with the matching configuration
     * @param storeUri : the uri of the store as used in the config repository
     * @param config : a configuration Repository that holds information on how to create each repository
     * @return a repository that matches the string's configuration
     */
    public Repository setupStore(Resource storeUri, Repository config)
            throws RepositoryException, QueryEvaluationException, IllegalAccessException, MalformedQueryException,
            IOException, RDFParseException {
        RepositoryConnection connection=config.getConnection();

        StoreType type=this.findStoreType(storeUri, connection);
        Repository result;

        if(type==null){
            type=StoreType.MEMSTORE;
        }

        result=type.buildRepos(storeUri,connection,this);
        result.initialize();

        this.loadDefaultContents(storeUri, connection, result);

        connection.close();
        return result;
    }

    /**
     * Examines the given configuration connection for values of eu.lod2.StoreFactory#loadPredicateURI. All files found in this way
     * will be loaded into the repository. The simple filename of the file (the last part in the path) will be used as
     * the name of the graph that gets inserted. Note that this graph will *not* be emptied beforehand. If other values
     * were present in this graph, the contents of this file will be added to the existing values.
     *
     * The files added in this way should be in the rdf-xml format
     * @param storeUri the uri of the repository to be configured
     * @param connection the connection to the repository holding the configuration
     * @param target the repository to add the data to
     */
    private void loadDefaultContents(Resource storeUri, RepositoryConnection connection,Repository target)
            throws MalformedQueryException, RepositoryException, QueryEvaluationException,
            IllegalAccessException, IOException, RDFParseException {
        List<String> filenames=this.getPropertyValue(storeUri, connection, StoreFactoryArgument.loadPredicate.getURI());
        if(filenames==null){
            // no files should be loaded
            return;
        }

        RepositoryConnection targetConnection= target.getConnection();
        for(String filename: filenames){
            File file = new File(filename);
            String simpleName=file.getName();
            if(!file.exists()){
                System.err.print("Could not find the file to load, skipping: " + filename);
            }else if(file.isDirectory()){
                System.err.print("The file specified for loading is a directory, skipping: " + filename);
            }else if(!file.canRead()){
                System.err.print("The file specified for loading could not be read, skipping: " + filename);
            }else{
                Resource contextURI = targetConnection.getValueFactory().createURI("http://localhost/"+
                        simpleName.substring(0,simpleName.lastIndexOf(".")));

                targetConnection.add(file, "http://lod2.eu/", RDFFormat.RDFXML, contextURI);
            }
        }

        targetConnection.close();
    }

    /**
     * Reads the storetype from the configuration connection
     * @param storeUri : the uri of the store
     * @param configConnection : the connection to the configuration repository
     * @return the type of the store as a #StoreType or null if no type was found
     */
    private StoreType findStoreType(Resource storeUri, RepositoryConnection configConnection)
            throws RepositoryException, QueryEvaluationException, MalformedQueryException, IllegalAccessException {
        String storeType=this.requirePropertyValue(storeUri, configConnection, StoreFactoryArgument.storeType.getURI());
        return StoreType.fromName(storeType);
    }

    /**
     * Creates a temporary memory repository with the contents of the configuration file.
     * @param configFile : the path to the file with the configuration data
     * @return a temporary memory repository that can be queried to get a configuration file that creates repositories.
     */
    public Repository fetchConfiguration(String configFile) throws RepositoryException, IOException, RDFParseException {
        Repository configRepos=this.buildMemStore();
        configRepos.initialize();

        RepositoryConnection con = configRepos.getConnection();
        File configurationFile = new File(configFile);
        con.add(configurationFile, "http://lod2.eu/", RDFFormat.RDFXML);

        con.close();
        return configRepos;
    }

    /**
     * Creates a temporary memory repository with the contents of the configuration string.
     * @param configuration : the configuration to enter into the tmeporary memoery store
     * @return a temporary memory repository that can be queried to get a configuration file that creates repositories.
     */
    public Repository fetchConfigurationFromString(String configuration) throws RepositoryException, IOException, RDFParseException {
        Repository configRepos=this.buildMemStore();
        configRepos.initialize();

        RepositoryConnection con = configRepos.getConnection();

        con.add(new StringReader(configuration), "http://lod2.eu/", RDFFormat.RDFXML);

        con.close();
        return configRepos;
    }

    /**
     * Returns a basic memory store
     * @return a basic memory store without configuration
     */
    private Repository buildMemStore(){
        return new SailRepository(new MemoryStore());
    }

    /**
     * Returns the property value(s) that the given store has for the given values
     * @param store : the uri that represents the store in the configuration file
     * @param configConnection : the connection to the repository that holds the configuration
     * @param property : the property that should be looked up
     * @return the value(s) of the given property for the given store.
     */
    public List<String> getPropertyValue(Resource store, RepositoryConnection configConnection, String property)
            throws QueryEvaluationException, IllegalAccessException, MalformedQueryException, RepositoryException {
        TupleQueryResult result=configConnection.prepareTupleQuery(QueryLanguage.SPARQL,
                "SELECT ?v WHERE {<"+
                store.stringValue()+"> <"+configConnection.getValueFactory().createURI(property)+"> ?v }"
        ).evaluate();

        List<String> propertyValues=new ArrayList<String>();
        while(result.hasNext()){
            String value=result.next().getValue("v").stringValue();
            propertyValues.add(value);
        }

        if(propertyValues.size()==0){
            return null;
        }else{
            return propertyValues;
        }
    }

    /**
     * Finds and returns a single value for the given property. If no value was found, the provided backup
     * value is used.
     * @param store the uri of the store to get the configuration from
     * @param con the connection to the repository that holds the configuration
     * @param property the uri of the property (in string format) to lookup
     * @return the value for the property. If multiple values were found, the first one is selected and a warning message is logged.
     * @throws IllegalStateException no value was found for the required property
     */
    public String requirePropertyValue(Resource store, RepositoryConnection con, String property)
            throws RepositoryException, QueryEvaluationException, MalformedQueryException, IllegalAccessException {
        List<String> results=this.getPropertyValue(store, con, property);
        if(results==null){
            throw new IllegalStateException("Could not find a value for the required property " + property);
        }
        String selected=results.get(0);
        if(results.size()>1){
            System.out.print("Found multiple values for the required property " + property +
                    ", selected " + selected);
        }
        return selected;
    }

    /**
     * Attempts to return the first found value for the given property. If no value was found, null is returned.
     * @param store the uri for the store configuration to access
     * @param con the connection to the configuration repository
     * @param property the property to get the value for
     * @return the first found value for the given property. If no value was found, null is returned.
     */
    public String requestPropertyValue(Resource store, RepositoryConnection con, String property){
        try{
            return this.requirePropertyValue(store,con,property);
        }catch (Exception e){
            return null;
        }
    }

    public static enum StoreType {
        MEMSTORE("memorystore") {
            @Override
            public Repository buildRepos(Resource storeUri, RepositoryConnection connection, StoreFactory factory) {
                return new SailRepository(new MemoryStore());
            }
        },
        PERSISTENTMEMSTORE("filestore") {
            @Override
            public Repository buildRepos(Resource storeUri, RepositoryConnection connection, StoreFactory factory)
                    throws RepositoryException, QueryEvaluationException, MalformedQueryException, IllegalAccessException {
                String filename=factory.requirePropertyValue(storeUri,connection, StoreFactoryArgument.filename.getURI());
                MemoryStore memstore=new MemoryStore(new File(filename));

                String sync=factory.requestPropertyValue(storeUri,connection, StoreFactoryArgument.syncDelay.getURI());
                if(sync!=null){
                    memstore.setSyncDelay(Long.parseLong(sync));
                }
                return new SailRepository(memstore);
            }

        },
        NATIVESTORE("disk") {
            @Override
            public Repository buildRepos(Resource storeUri, RepositoryConnection connection, StoreFactory factory)
                    throws RepositoryException, QueryEvaluationException, MalformedQueryException, IllegalAccessException {
                String filename=factory.requirePropertyValue(storeUri, connection, StoreFactoryArgument.filename.getURI());
                List<String> indexes=factory.getPropertyValue(storeUri, connection, StoreFactoryArgument.indexes.getURI());
                if(indexes==null || indexes.isEmpty()){
                    return new SailRepository(new NativeStore(new File(filename)));
                }else{
                    String index=indexes.get(0);
                    if(indexes.size()>1){
                        System.out.print("Warning: multiple values found for "+ StoreFactoryArgument.indexes.getURI()+", selecting "+index);
                    }
                    return new SailRepository(new NativeStore(new File(filename)));
                }
            }
        },
        VIRTSTORE("virtuoso") {
            @Override
            public Repository buildRepos(Resource storeUri, RepositoryConnection connection, StoreFactory factory)
                    throws RepositoryException, QueryEvaluationException, MalformedQueryException, IllegalAccessException {
                String jdbcConnection=factory.requirePropertyValue(storeUri, connection, StoreFactoryArgument.JDBCConnection.getURI());
                String jdbcUser=factory.requirePropertyValue(storeUri, connection, StoreFactoryArgument.JDBCuser.getURI());
                String jdbcPwd=factory.requirePropertyValue(storeUri,connection, StoreFactoryArgument.JDBCpwd.getURI());
                return new VirtuosoRepository(jdbcConnection,jdbcUser,jdbcPwd);
            }
        };

        private String fullName;

        StoreType(String fullName) {
            this.fullName = fullName;
        }

        public String getFullName(){
            return this.fullName;
        }

        public static StoreType fromName(String fullName) {
            if (fullName != null) {
                for (StoreType type : StoreType.values()) {
                    if (fullName.equalsIgnoreCase(type.getFullName())) {
                        return type;
                    }
                }
            }
            return null;
        }

        /**
         * Creates a repository based on this type and the provided configuration
         * @param storeUri the uri of the repository to initialize
         * @param connection the connection to the configuration repository
         * @param factory the factory to use for configuring the store
         * @return a new but not yet initialized repository with the correct configuration
         */
        public abstract Repository buildRepos(Resource storeUri, RepositoryConnection connection, StoreFactory factory)
                throws RepositoryException, QueryEvaluationException, MalformedQueryException, IllegalAccessException;

    }
}