package eu.lod2.utils;

import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.HashMap;
import java.util.Map;

/**
 * ComboBox to pick dimensions in the current graph or outside
 */
public class CodeListPicker extends SparqlPicker {

    public CodeListPicker(String title, RepositoryConnection connection, String currentGraph) {
        super(title, connection, currentGraph);
    }

    public CodeListPicker(String title, RepositoryConnection connection, String currentGraph,String defaultValue) {
        super(title, connection, currentGraph,defaultValue);
    }

    //* fetches the relevant datacubes from the server
    protected void fetchOptions() {
        this.removeAllItems();
        try{
            Map<String,String> dsds=getCodeLists();

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
    private Map<String,String> getCodeLists()
            throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        if(currentGraph== null || currentGraph.isEmpty()){
            currentGraph=null;
        }

        StringBuilder builder=new StringBuilder();
        builder.append("SELECT ?list ?listLabel ");
        builder.append((currentGraph == null ? "" : "FROM <" + currentGraph + "> "));
        builder.append("WHERE {\n");
        builder.append("?list a <http://www.w3.org/2004/02/skos/core#ConceptScheme>.\n");
        builder.append("OPTIONAL { {?list rdfs:comment ?listLabel} UNION {?list rdfs:label ?listLabel} } }");

        TupleQueryResult result = connection.prepareTupleQuery(QueryLanguage.SPARQL, builder.toString()).evaluate();

        Map<String,String> results=new HashMap<String, String>();
        while(result.hasNext()){
            BindingSet set=result.next();
            String uri=set.getBinding("list").getValue().stringValue();
            String label= uri;
            if(set.getBinding("listLabel")!=null && !showURIs){
                label=set.getBinding("listLabel").getValue().stringValue();
            }

            results.put(uri,label);
        }

        return results;
    }
}
