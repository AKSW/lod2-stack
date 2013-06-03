package eu.lod2.utils;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Window;
import eu.lod2.ConfigurationTab;
import eu.lod2.LOD2DemoState;
import org.openrdf.query.QueryLanguage;

import java.util.List;

/**
 * ComboBox for selecting available graphs in endpoint
 */
public class GraphPicker extends ComboBox {
    private LOD2DemoState state;
    public GraphPicker(String label,final LOD2DemoState state){
        super(label);
        this.state=state;
        this.fetchPossibleGraphs();
        this.setNewItemsAllowed(true);
        this.setImmediate(true);
        this.setNewItemHandler(new NewItemHandler() {
            public void addNewItem(String s) {
                try{
                    if(!containsId(s)){
                        state.rdfStore.getConnection().prepareGraphQuery(QueryLanguage.SPARQL,
                                "CREATE SILENT GRAPH <"+s+">").evaluate();
                        getWindow().showNotification("Graph created", "The new graph <"+s+"> has been created.",
                                Window.Notification.TYPE_HUMANIZED_MESSAGE);
                        fetchPossibleGraphs();
                        setValue(s);
                    }
                }catch (Exception e){
                    getWindow().showNotification("Could not create graph",
                            "Sorry, we could not create the new graph, " +
                            "please make sure that the connection to your store has been properly configured.",
                            Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
    }
    public void fetchPossibleGraphs(){
        this.removeAllItems();
        try {
            List<String> graphs= ConfigurationTab.request_graphs(state);
            for(String graph : graphs){
                this.addItem(graph);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not find the available graphs in the server. Check your " +
                    "configuration for the database connection.");
        }
    }
}
