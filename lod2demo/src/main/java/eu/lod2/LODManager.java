package eu.lod2;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;

import java.net.MalformedURLException;
import java.net.URL;

public class LODManager extends CustomComponent
{

    // reference to the global internal state
    private LOD2DemoState state;

    public LODManager(LOD2DemoState st) {

        // The internal state 
        state = st;

	Embedded browser = new Embedded();
	try { 
	  	URL url = new URL(state.getHostName(false) + ":8080/unifiedviews/");
		browser = new Embedded("", new ExternalResource(url));
		browser.setType(Embedded.TYPE_BROWSER);
		browser.setSizeFull();
		//panel.addComponent(browser);
	} catch (MalformedURLException e) {
                e.printStackTrace();
	};

        // The composition root MUST be set
        setCompositionRoot(browser);
    }

	// propagate the information of one tab to another.
	public void setDefaults() {
	};

};

/**
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * iframe integration of lodms
  
public class LODManager extends IframedUrl {
    public LODManager(LOD2DemoState st) {
        super(st, "http://localhost/lodms");
        this.initService();
    }

    private void initService() {
        try {
            RepositoryConnection con = state.getRdfStore().getConnection();

            // initialize the hostname and portnumber
            String query = "select ?s from <" + state.getConfigurationRDFgraph() + "> where {<" +
                    state.getConfigurationRDFgraph() + "> <http://lod2.eu/lod2demo/configures> " +
                    "<http://localhost/lodms>. " +
                    "<http://localhost/lodms> <http://lod2.eu/lod2demo/service> ?s.} LIMIT 1";
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
            TupleQueryResult result = tupleQuery.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Value valueOfS = bindingSet.getValue("s");
                if (valueOfS instanceof LiteralImpl) {
                    LiteralImpl literalS = (LiteralImpl) valueOfS;
                    String service0 = literalS.getLabel();

                    this.updateUrl(state.processService(service0,"lodms"));
                }
            }
            //TODO auto generated catch blocks
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }

    };
}
*/

