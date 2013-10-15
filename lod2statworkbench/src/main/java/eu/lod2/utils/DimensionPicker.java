package eu.lod2.utils;

import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.HashMap;
import java.util.Map;

/**
 * ComboBox to pick dimensions in the current graph or outside
 */
public class DimensionPicker extends SparqlPicker {
    protected String targetCube = null;

    public DimensionPicker(String title, RepositoryConnection connection, String currentGraph) {
        super(title, connection, currentGraph);
    }

    public DimensionPicker(String title, RepositoryConnection connection, String currentGraph,String defaultValue) {
        super(title, connection, currentGraph,defaultValue);
    }

    public DimensionPicker(String title, RepositoryConnection connection, String currentGraph,String defaultValue, String targetCube) {
        super(title, connection, currentGraph,defaultValue);
    }

    //* fetches the relevant datacubes from the server
    protected void fetchOptions() {
        this.removeAllItems();
        try{
            Map<String,String> dsds=getDimensions();

            for(String graph : dsds.keySet()){
                this.addItem(new SparqlPickerOption(graph,dsds.get(graph)));
            }
        }catch (Exception e){
            throw new IllegalStateException("Could not fetch the available elements from the server. " +
                    "An error occured: "+e.getClass().getSimpleName()+", "+e.getMessage());
        }
    }

    /**
     * Looks for the datastructure definitions in the current graph (or the entire dataset if no graph is selected)
     * Returns a map from uri to label for all dsd's that have been discovered
     */
    private Map<String,String> getDimensions()
            throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        if(currentGraph== null || currentGraph.isEmpty()){
            currentGraph=null;
        }

        StringBuilder builder=new StringBuilder();
        builder.append("SELECT ?dim ?dimLabel ");
        builder.append((currentGraph == null ? "" : "FROM <" + currentGraph + "> "));
        builder.append("WHERE {\n");
        if(targetCube==null || targetCube.isEmpty()){
            builder.append("?s a <http://purl.org/linked-data/cube#DataSet>.\n");
            builder.append("?s <http://purl.org/linked-data/cube#structure> ?dsd.\n");
        }else{
            builder.append("<"+targetCube+"> a <http://purl.org/linked-data/cube#DataSet>.\n");
            builder.append("<"+targetCube+"> <http://purl.org/linked-data/cube#structure> ?dsd.\n");
        }
        builder.append("?dsd <http://purl.org/linked-data/cube#component> ?compcs.\n");
        builder.append("?compcs <http://purl.org/linked-data/cube#dimension> ?dim.\n");
        builder.append("OPTIONAL { {?dim rdfs:comment ?dimLabel} UNION {?dim rdfs:label ?dimLabel} } }");

        TupleQueryResult result = connection.prepareTupleQuery(QueryLanguage.SPARQL, builder.toString()).evaluate();

        Map<String,String> results=new HashMap<String, String>();
        while(result.hasNext()){
            BindingSet set=result.next();
            String uri=set.getBinding("dim").getValue().stringValue();
            String label= uri;
            if(set.getBinding("dimLabel")!=null && !showURIs){
                label=set.getBinding("dimLabel").getValue().stringValue();
            }

            results.put(uri,label);
        }

        return results;
    }

    public void setTargetCube(String cube){
        this.targetCube = cube;
        this.refresh();
    }
}
