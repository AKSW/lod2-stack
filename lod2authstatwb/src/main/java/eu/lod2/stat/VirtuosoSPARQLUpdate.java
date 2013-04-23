package eu.lod2.stat;

import java.net.MalformedURLException;
import java.net.URL;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;

import eu.lod2.LOD2DemoState;

public class VirtuosoSPARQLUpdate extends CustomComponent
{

	// reference to the global internal state
	private LOD2DemoState state;

	public VirtuosoSPARQLUpdate(LOD2DemoState st) {

		// The internal state and 
		state = st;

        Embedded browser = new Embedded();
	try { 
		String appendage = "/virtuoso/sparql-auth";
		String curGraph = state.getCurrentGraph();
		if (curGraph != null && !curGraph.equals(""))
			appendage += "?default-graph-uri";
		URL url;
		if (state.getHostName().equals("http://localhost:8080")) {
			url = new URL("http://localhost:8890/sparql-auth");
		} else {
	  		url = new URL(state.getHostName() + "/virtuoso/sparql-auth");
		};
		browser = new Embedded("", new ExternalResource(url));
		browser.setType(Embedded.TYPE_BROWSER);
		browser.setSizeFull();
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
