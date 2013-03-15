package eu.lod2.stat;

import com.google.gwt.user.client.Cookies;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;
import eu.lod2.LOD2DemoState;
import org.apache.http.cookie.Cookie;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Embedded OntoWiki tool
 */
//@SuppressWarnings("serial")
public class OntoWikiPathExtended extends CustomComponent
{

  // reference to the global internal state
  private LOD2DemoState state;
	private String username;
	private String password;
	private String service;
	private String pathExtension;
	private boolean selectCurrentGraph;
    private String sessionId;

  public OntoWikiPathExtended(LOD2DemoState st, String pathExtension, boolean selectCurrentGraph) {

    // The internal state 
	this.pathExtension = pathExtension;
	this.selectCurrentGraph = selectCurrentGraph;
    this.state = st;
    initLogin();
    activateCurrentGraph();

    Embedded browser = new Embedded();
    try { 
			URL url = new URL(service);
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

	private void initLogin() {
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();

			// initialize the hostname and portnumber
			String query = "select ?u ?p ?s from <" + state.getConfigurationRDFgraph() + "> where {<" + state.getConfigurationRDFgraph() + "> <http://lod2.eu/lod2statworkbench/configures> <http://localhost/ontowiki>. <http://localhost/ontowiki> <http://lod2.eu/lod2statworkbench/password> ?p. <http://localhost/ontowiki> <http://lod2.eu/lod2statworkbench/username> ?u. <http://localhost/ontowiki> <http://lod2.eu/lod2statworkbench/service> ?s.} LIMIT 100";
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
					if (service0 == null | service0.equals("")) {
						service = "http://localhost/ontowiki";
					} else {
						service = service0;
					};

                    //this.authenticate(username, password, service);

					service += pathExtension;
					
					String curGraph = state.getCurrentGraph();
					if (selectCurrentGraph)
//						if (curGraph==null) service += "/?m=currentGraphIsNull";
//						else if (curGraph.equals("")) service += "/?m=currentGraphIsEmpty";
//						else
						if (curGraph != null && !curGraph.equals(""))
						try {
							String encodedUrl = URLEncoder.encode(curGraph, "UTF-8");
							service += "/?m=" + encodedUrl;
						} catch (UnsupportedEncodingException e) {
							service += "/?m=unsupported-encoding";
							e.printStackTrace();
						}
                    if(this.sessionId!=null){
                        service+="&C="+this.sessionId;
                    }
//					else service += "/";
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

    }

    /**
     * Authenticates the given user with the given password
     * @param user the username
     * @param password the password that should match the given username
     * @param service the location of the ontowiki service
     */
    private void authenticate(String user, String password, String service) throws IOException, IllegalAccessException {

        /*
        // the authentication post request should originate from the client.
        // TODO: use the correct cookie from the client side
        String script="(function(){ var xmlhttp = null;\n" +
                "if (window.XMLHttpRequest)\n" +
                "{// code for IE7+, Firefox, Chrome, Opera, Safari\n" +
                "    xmlhttp = new XMLHttpRequest();\n" +
                "}\n" +
                "else\n" +
                "{// code for IE6, IE5\n" +
                "    xmlhttp = new ActiveXObject(\"Microsoft.XMLHTTP\");\n" +
                "}\n" +
                "\n" +
                "var url = \""+service+"/service/auth\";\n" +
                "xmlhttp.open(\"POST\", url, true);\n" +
                "xmlhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n" +
                "var params = \"u=\"+encodeURIComponent(\""+user+"\")+\"&p=\"+encodeURIComponent(\""+password+"\");\n" +
                "xmlhttp.send(params);})();";
        window.executeJavaScript(script);
        */

    }


    private void activateCurrentGraph() {

	if ( ! state.getCurrentGraph().equals("")) {

	try {
		RepositoryConnection con = state.getRdfStore().getConnection();

		// initialize the hostname and portnumber
		String query = "create silent graph <" + state.getCurrentGraph() + ">"; 
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult result = tupleQuery.evaluate();

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

	}
    }
};

