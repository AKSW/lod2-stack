package eu.lod2;

import com.vaadin.ui.*;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

import java.util.HashMap;
import java.util.Map;

/**
 * This class extends the publisher panel to allow upload directly to a preconfigured cka ninstance, without going through
 * all the hassle of doing a separate login and ckan selection. The lod2test account is used by default to post to the ckan
 */
public class DataHubPublisher extends CKANPublisherPanel {
    public DataHubPublisher(LOD2DemoState state) {
        super(state);
    }

    @Override
    //* The ckan info is fetched directly from the configuration, a map with fields is created to conform to the original api, but the fields are not shown
    protected HashMap<String,AbstractTextField> createCKANInfo() {

        // not using form here, will not couple domain object to the properties entered by the user
        Panel panel= new Panel("CKAN configuration");
        VerticalLayout layout = (VerticalLayout) panel.getContent();

        try
        {
            Map<String,String> ckanConfig= readCKANConfiguration();

            String url= ckanConfig.get("service");
            String pwd= ckanConfig.get("pwd");

            layout.addComponent(new Label("This component allows you to upload the currently active graph to "+url+
                    ". The graph will be created by the user with the given api key in this CKAN instance.\nNOTE: file upload currently not supported here!"));
            this.addComponent(panel);

            HashMap<String, AbstractTextField> fields = new HashMap<String, AbstractTextField>();
            fields.put(CKAN_REPOS_LABEL,new TextField(CKAN_REPOS_LABEL,url));
            fields.put(API_KEY_LABEL,new TextField(API_KEY_LABEL,pwd));


            return fields;

        }catch (Exception e){
            getWindow().showNotification("Error fetching ckan configuration",
                    "Sorry, we could not fetch the CKAN configuration from the store, please check your store configuration.",
                    Window.Notification.TYPE_ERROR_MESSAGE);
            return new HashMap<String, AbstractTextField>();
        }
    }

    /**
     * reads the ckan configuration from the configuration graph
     * @return a map holding all ckan properties as defined by the graph
     */
    protected Map<String,String> readCKANConfiguration() throws Exception{
        HashMap<String,String> config= new HashMap<String, String>();
        RepositoryConnection con = state.getRdfStore().getConnection();

        // initialize the hostname and portnumber
        String query = "select ?username ?pwd ?service from <" + state.getConfigurationRDFgraph() + "> where {" +
                "<" + state.getConfigurationRDFgraph() + "> <http://lod2.eu/lod2statworkbench/configures> <http://localhost/ckan>. " +
                "<http://localhost/ckan> <http://lod2.eu/lod2statworkbench/password> ?pwd. " +
                "<http://localhost/ckan> <http://lod2.eu/lod2statworkbench/username> ?username. " +
                "<http://localhost/ckan> <http://lod2.eu/lod2statworkbench/service> ?service.} LIMIT 1";
        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
        TupleQueryResult result = tupleQuery.evaluate();
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            for(String name : bindingSet.getBindingNames()){
                config.put(name,bindingSet.getValue(name).stringValue());
            }
        }

        return config;
    }
}
