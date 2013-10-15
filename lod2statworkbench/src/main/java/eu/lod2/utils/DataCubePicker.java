package eu.lod2.utils;

import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.HashMap;
import java.util.Map;

/**
 * Selects a datacube from the current graph or the entire database if none is present
 */
public class DataCubePicker extends SparqlPicker {

    public DataCubePicker(String title, RepositoryConnection connection, String currentGraph) {
        super(title, connection, currentGraph);
    }

    public DataCubePicker(String title, RepositoryConnection connection, String currentGraph, String defaultValue) {
        super(title, connection, currentGraph,defaultValue);
    }

    //* fetches the relevant datacubes from the server
    protected void fetchOptions() {
        this.removeAllItems();
        try{
            Map<String,String> dsds=getDatastructureDefinitions();

            for(String graph : dsds.keySet()){
                this.addItem(new SparqlPickerOption(graph,dsds.get(graph)));
            }
        }catch (Exception e){
            throw new IllegalStateException("Could not fetch the available graphs from the server. " +
                    "An error occured: "+e.getClass().getSimpleName()+", "+e.getMessage());
        }
    }

    /**
     * Looks for the datastructure definitions in the current graph (or the entire dataset if no graph is selected)
     * Returns a map from uri to label for all dsd's that have been discovered
     */
    private Map<String,String> getDatastructureDefinitions()
            throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        if(currentGraph== null || currentGraph.isEmpty()){
            currentGraph=null;
        }

        StringBuilder builder=new StringBuilder();
        builder.append("SELECT ?s ?l (count(distinct ?dim) as ?dimcount) ");
        builder.append("(count(distinct ?measure) as ?mcount) ");
        builder.append("(count(distinct ?attribute) as ?acount)\n");
        builder.append((currentGraph == null ? "" : "FROM <" + currentGraph + "> "));
        builder.append("WHERE {\n");
        builder.append("?s a <http://purl.org/linked-data/cube#DataSet>.\n");
        builder.append("?s <http://purl.org/linked-data/cube#structure> ?dsd.\n");
        builder.append("?dsd <http://purl.org/linked-data/cube#component> ?compcs.\n");
        builder.append("?dsd <http://purl.org/linked-data/cube#component> ?compcs2.\n");
        builder.append("?dsd <http://purl.org/linked-data/cube#component> ?compcs3.\n");
        builder.append("?compcs <http://purl.org/linked-data/cube#dimension> ?dim.\n");
        builder.append("?compcs2 <http://purl.org/linked-data/cube#measure> ?measure. \n");
        builder.append("OPTIONAL {?compcs3 <http://purl.org/linked-data/cube#attribute> ?attribute.}");
        builder.append("OPTIONAL {{ ?s rdfs:comment ?l } UNION { ?s rdfs:label ?l } } } GROUP BY ?s ?l");

        TupleQueryResult result = connection.prepareTupleQuery(QueryLanguage.SPARQL, builder.toString()).evaluate();

        Map<String,String> results=new HashMap<String, String>();
        while(result.hasNext()){
            BindingSet set=result.next();
            String uri=set.getBinding("s").getValue().stringValue();
            String label= uri;
            if(set.getBinding("l")!=null && !showURIs){
               label=set.getBinding("l").getValue().stringValue();
            }
            String dimCount=set.getBinding("dimcount").getValue().stringValue();
            String mCount=set.getBinding("mcount").getValue().stringValue();
            String aCount=set.getBinding("acount").getValue().stringValue();

            results.put(uri,label+ " (D: "+dimCount+", M: "+mCount+", A: "+(Integer.parseInt(aCount)-1)+")");
        }

        return results;
    }
}