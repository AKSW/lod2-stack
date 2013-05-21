package eu.lod2.stat;

import com.vaadin.ui.*;
import eu.lod2.ConfigurationTab;
import eu.lod2.LOD2DemoState;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.*;

/**
 * This component allows the user to select and merge two datacubes that are available in the sparql endpoint as graphs
 */
public class MergeDatasets extends VerticalLayout {
    private LOD2DemoState state;
    private Panel layout;
    private Set<GraphPicker> selectors=new LinkedHashSet<GraphPicker>();

    public MergeDatasets(LOD2DemoState state) {
        this.state=state;
        this.render();
    }

    public void render(){
        this.removeAllComponents();
        Panel panel=new Panel("Merge datasets");

        final VerticalLayout datasetbox=new VerticalLayout();
        this.addDataset(datasetbox);
        this.addDataset(datasetbox);
        panel.addComponent(datasetbox);

        HorizontalLayout buttonbox=new HorizontalLayout();
        buttonbox.setSpacing(true);
        panel.addComponent(buttonbox);

        Button addButton=new Button("Add dataset");
        addButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                addDataset(datasetbox);
            }
        });
        buttonbox.addComponent(addButton);

        Button mergeButton=new Button("Merge datasets");
        mergeButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                mergeDatasets();
            }
        });
        buttonbox.addComponent(mergeButton);

        ((VerticalLayout)panel.getContent()).setSpacing(true);

        this.addComponent(panel);
    }

    //* returns the graph that have currently been selected by the user for merging as a set of strings
    public Set<String> getGraphsToMerge(){
        HashSet<String> toMerge=new LinkedHashSet<String>();
        for(GraphPicker picker : this.selectors){
            String value=(String) picker.getValue();
            if(value != null && !value.isEmpty()){
                toMerge.add(value);
            }
        }

        return toMerge;
    }

    /**
     * This function merges the datasets that have been selected by the user
     */
    private void mergeDatasets(){
        Window window=new Window("Merging datasets");
        window.setModal(true);
        window.setWidth((getWindow().getWidth()/2)+"px");
        window.center();

        for(String graph : this.getGraphsToMerge()){
            window.addComponent(new Label(graph));
        }

        getWindow().addWindow(window);
    }

    /**
     * Adds a new Graphpicker to the list of datasets
     * @param datasetHolder the panel to add the dataset picker to
     */
    public void addDataset(final VerticalLayout datasetHolder){
        final HorizontalLayout box=new HorizontalLayout();
        final GraphPicker picker=new GraphPicker("Select a dataset to merge");
        final Button remover = new Button("Remove");
        remover.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                datasetHolder.removeComponent(box);
                selectors.remove(picker);
            }
        });



        box.addComponent(picker);
        box.addComponent(remover);

        box.setSpacing(true);

        box.setComponentAlignment(picker,Alignment.MIDDLE_LEFT);
        box.setComponentAlignment(remover,Alignment.BOTTOM_LEFT);

        datasetHolder.addComponent(box);

        selectors.add(picker);
    }

    /**
     * A combobox that fetches the available graphs from the database
     */
    public static class GraphPicker extends ComboBox {
        public GraphPicker(String title){
            super(title);
            this.fetchGraphs();
        }

        private void fetchGraphs() {
            try{
                List<String> graphs= ConfigurationTab.request_graphs();
                for(String graph : graphs){
                    this.addItem(graph);
                }
            }catch (Exception e){
                throw new IllegalStateException("Could not fetch the available graphs from the server. " +
                        "An error occured: "+e.getClass().getSimpleName()+", "+e.getMessage());
            }
        }

    }

    /**
     * Selects a datacube from the current graph
     */
    public static class DataCubePicker extends ComboBox {
        private LOD2DemoState state;

        public DataCubePicker(String title, LOD2DemoState state){
            super(title);
            this.state=state;
            this.fetchCubes();
        }

        private void fetchCubes() {
            try{
                Map<String,String> dsds=getDatastructureDefinitions();

                for(String graph : dsds.keySet()){
                    this.addItem(graph);
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
            RepositoryConnection connection=state.getRdfStore().getConnection();
            String currentGraph=state.getCurrentGraph();
            if(currentGraph== null || currentGraph.isEmpty()){
                currentGraph=null;
            }
            TupleQueryResult result=connection.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?s ?l " +
                    (currentGraph == null ? "" : "FROM <" + currentGraph + "> ") +
                    "WHERE {?s a <http://purl.org/linked-data/cube#DataStructureDefinition>." +
                    " OPTIONAL {?s rdfs:comment ?l} }>").evaluate();
            Map<String,String> results=new HashMap<String, String>();
            while(result.hasNext()){
                BindingSet set=result.next();
                Binding label=set.getBinding("l");
                String uri=set.getBinding("s").getValue().stringValue();
                results.put(uri,(label==null?uri:label.getValue().stringValue()));
            }

            return results;
        }
    }
}