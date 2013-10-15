package eu.lod2.utils;

import com.vaadin.ui.ComboBox;
import org.openrdf.repository.RepositoryConnection;

/**
 * Allows selection among a number of items returned by a sparql query
 */
public abstract class SparqlPicker extends ComboBox {
    protected String currentGraph=null;
    //* connection to the triple store
    protected RepositoryConnection connection=null;
    //* if true forces the option to display the uri, not the label.
    protected boolean showURIs=false;
    //* default value that is selected
    protected String defaultValue;

    public SparqlPicker(String title, RepositoryConnection connection, String currentGraph){
        super(title);
        this.currentGraph=currentGraph;
        this.connection=connection;
        this.refresh();
    }

    public SparqlPicker(String title, RepositoryConnection connection, String currentGraph,String defaultValue){
        super(title);
        this.currentGraph=currentGraph;
        this.connection=connection;
        this.defaultValue=defaultValue;
        this.refresh();
    }

    public void refresh(){
        this.fetchOptions();
        this.setSelected(defaultValue);
        this.requestRepaint();
    }

    //* selects the value that is given if it is available
    public void setSelected(String defaultValue){
        for(Object option : this.getItemIds()){
            if(((SparqlPickerOption) option).uri.equals(defaultValue)){
                this.setValue(option);
            }
        }
    }

    //* updates the uris to show
    public void setShowURIs(boolean show){
        if(this.showURIs!=show){
            this.showURIs=show;
            this.refresh();
        }
    }

    //* sets the current graph to the given graph and updates the options if changed
    public void setCurrentGraph(String graph){
        if((this.currentGraph != null && !this.currentGraph.equals(graph)) ||
                (this.currentGraph==null && graph!=null)){
            this.currentGraph=graph;
            this.refresh();
        }
    }

    //* fetches the relevant options from the server
    protected abstract void fetchOptions();

    //* returns the selected uri
    public String getSelection(){
        SparqlPickerOption selected=(SparqlPickerOption) this.getValue();
        return selected==null?null:selected.uri;
    }

    //* an option in the select box (strings will not suffice)
    protected class SparqlPickerOption {
        public String name=null;
        public String uri=null;

        public SparqlPickerOption (String uri, String name){
            this.uri=uri;
            this.name=name;
        }
        @Override
        public String toString(){
            return name==null || name.isEmpty()?uri:name;
        }
    }
}
