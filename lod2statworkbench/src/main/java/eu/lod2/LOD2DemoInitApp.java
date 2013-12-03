package eu.lod2;

import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

public class LOD2DemoInitApp {

  public String username;
  public String password;
  public String service;

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
          "> <http://lod2.eu/lod2statworkbench/configures> <" +
          applicationname +
          ">. OPTIONAL { <" +
          applicationname +
          "> <http://lod2.eu/lod2statworkbench/password> ?p. } OPTIONAL { <" +
          applicationname +
          "> <http://lod2.eu/lod2statworkbench/username> ?u. } <" +
          applicationname +
          "> <http://lod2.eu/lod2statworkbench/service> ?s.} LIMIT 100";
      TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
      TupleQueryResult result = tupleQuery.evaluate();
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        Value valueOfH = bindingSet.getValue("u");
        if (valueOfH != null && valueOfH instanceof LiteralImpl) {
          LiteralImpl literalH = (LiteralImpl) valueOfH;
          username = literalH.getLabel();
        }

        Value valueOfP = bindingSet.getValue("p");
        if (valueOfP != null && valueOfP instanceof LiteralImpl) {
          LiteralImpl literalP = (LiteralImpl) valueOfP;
          password = literalP.getLabel();
        }

        Value valueOfS = bindingSet.getValue("s");
        if (valueOfS instanceof LiteralImpl) {
          LiteralImpl literalS = (LiteralImpl) valueOfS;
          String service0 = literalS.getLabel();
          if (service0 == null | service0.equals("")) {
            service = applicationname;
          } else {
            service = service0;
          }

        }

      }

      // if hostname is set, DO NOT USE LOCALHOST (sorry for shouting, but it is somewhat important and seems to be
      // forgotten quite often)
      service = service.replaceFirst("http://localhost",state.getHostNameWithoutPort());

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
