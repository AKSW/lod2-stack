package eu.lod2;

import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * iframe integration of dbpedia-spotlight-ui
 */
public class DBpediaSpotlightUI extends IframedUrl {
    public DBpediaSpotlightUI(LOD2DemoState st) {
        super(st, "http://localhost/dbpedia-spotlight-ui");
        this.initService();
    }

    private void initService() {
        try {
            RepositoryConnection con = state.getRdfStore().getConnection();

            // initialize the hostname and portnumber
            String query = "select ?s from <" + state.getConfigurationRDFgraph() + "> where {<" +
                    state.getConfigurationRDFgraph() + "> <http://lod2.eu/lod2demo/configures> " +
                    "<http://localhost/dbpedia-spotlight-ui>. " +
                    "<http://localhost/dbpedia-spotlight-ui> <http://lod2.eu/lod2demo/service> ?s.} LIMIT 1";
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
            TupleQueryResult result = tupleQuery.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Value valueOfS = bindingSet.getValue("s");
                if (valueOfS instanceof LiteralImpl) {
                    LiteralImpl literalS = (LiteralImpl) valueOfS;
                    String service0 = literalS.getLabel();
                    String service;
                    if (service0 == null | service0.equals("")) {
                        service = "http://localhost/lodrefine/";
                    } else {
                        service = service0;
                    }

                    this.updateUrl(service);
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
