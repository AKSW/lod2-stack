package eu.lod2;

import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Created with IntelliJ IDEA.
 * User: bertv
 * Date: 11/30/12
 * Time: 5:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class LOD2DemoInitApp {

    String username;
    String password;
    String service;

    LOD2DemoState state;

    public LOD2DemoInitApp(LOD2DemoState st, String applicationname) {
        state = st;
        initAppLogin(applicationname);
    }

    private void initAppLogin(String applicationname) {
        try {
            RepositoryConnection con = state.getRdfStore().getConnection();

            // initialize the hostname and portnumber
            String query = "select ?u ?p ?s from <" + state.getConfigurationRDFgraph() +
                    "> where {<" + state.getConfigurationRDFgraph() +
                    "> <http://lod2.eu/lod2demo/configures> <http://localhost/" +
                    applicationname +
                    ">. <http://localhost/" +
                    applicationname +
                    "> <http://lod2.eu/lod2demo/password> ?p. <http://localhost/" +
                    applicationname +
                    "> <http://lod2.eu/lod2demo/username> ?u. <http://localhost/" +
                    applicationname +
                    "> <http://lod2.eu/lod2demo/service> ?s.} LIMIT 100";
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
            TupleQueryResult result = tupleQuery.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Value valueOfH = bindingSet.getValue("u");
                if (valueOfH instanceof LiteralImpl) {
                    LiteralImpl literalH = (LiteralImpl) valueOfH;
                    username = literalH.getLabel();
                };
                Value valueOfP = bindingSet.getValue("p");
                if (valueOfP instanceof LiteralImpl) {
                    LiteralImpl literalP = (LiteralImpl) valueOfP;
                    password = literalP.getLabel();
                };
                Value valueOfS = bindingSet.getValue("s");
                if (valueOfS instanceof LiteralImpl) {
                    LiteralImpl literalS = (LiteralImpl) valueOfS;
                    String service0 = literalS.getLabel();
                    service= state.processService(service0, "/"+applicationname);
                };
            }

        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    };
}
