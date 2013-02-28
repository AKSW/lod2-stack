package eu.lod2.stat;

import java.net.*;
import java.net.URI;
import java.io.*;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Alignment.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.*;

import org.openrdf.model.*;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.model.impl.*;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;

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
			String query = "select ?u ?p ?s from <" + state.getConfigurationRDFgraph() + "> where {<" + state.getConfigurationRDFgraph() + "> <http://lod2.eu/lod2demo/configures> <http://localhost/ontowiki>. <http://localhost/ontowiki> <http://lod2.eu/lod2demo/password> ?p. <http://localhost/ontowiki> <http://lod2.eu/lod2demo/username> ?u. <http://localhost/ontowiki> <http://lod2.eu/lod2demo/service> ?s.} LIMIT 100";
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
					service += pathExtension;
					
					String curGraph = state.getCurrentGraph();
					if (selectCurrentGraph) 
						if (curGraph==null) service += "/?m=currentGraphIsNull";
						else if (curGraph.equals("")) service += "/?m=currentGraphIsEmpty";
						else
						try {
							String encodedUrl = URLEncoder.encode(curGraph, "UTF-8");
							service += "/?m=" + encodedUrl;
						} catch (UnsupportedEncodingException e) {
							service += "/?m=unsupported-encoding";
							e.printStackTrace();
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

	};


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

