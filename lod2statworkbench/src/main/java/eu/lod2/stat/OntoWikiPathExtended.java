package eu.lod2.stat;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import eu.lod2.ConfigurationTab;
import eu.lod2.LOD2DemoInitApp;
import eu.lod2.LOD2DemoState;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.RepositoryConnection;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Embedded OntoWiki tool
 */
//@SuppressWarnings("serial")
public class OntoWikiPathExtended extends CustomComponent implements LOD2DemoState.CurrentGraphListener {

  // reference to the global internal state
  private LOD2DemoState state;
  private String username;
  private String password;
  private String service;
  private String pathExtension;
  private boolean selectCurrentGraph;
  private String sessionId;

  private VerticalLayout componentLayout = null;

  public OntoWikiPathExtended(LOD2DemoState st, String pathExtension, boolean selectCurrentGraph) {

    // The internal state
    this.pathExtension = pathExtension;
    this.selectCurrentGraph = selectCurrentGraph;
    this.state = st;

    VerticalLayout layout = new VerticalLayout();
    this.setCompositionRoot(layout);
    this.componentLayout = layout;
  }

  public void attach() {
    // will ensure immediate notify
    this.state.addCurrentGraphListener(this);
  }

  public void detach() {
    this.state.removeCurrentGraphListener(this);
  }

  /**
   * Refreshes the current visualization of the component to reflect the current state.
   */
  public void refresh() {
    this.componentLayout.removeAllComponents();

    String currentGraph = this.state.getCurrentGraph();
    if (this.selectCurrentGraph && (currentGraph == null || currentGraph.isEmpty())) {
      Label message = new Label("No graph is currently selected. You can select one below:");
      this.componentLayout.addComponent(message);
      ConfigurationTab config = new ConfigurationTab(this.state);
      this.componentLayout.addComponent(config);
      this.componentLayout.setComponentAlignment(message, Alignment.TOP_LEFT);
      this.componentLayout.setComponentAlignment(config, Alignment.TOP_LEFT);

      return;
    }
    this.componentLayout.setSizeFull();

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
    }

    this.componentLayout.addComponent(browser);
  }

  // propagate the information of one tab to another.
  public void setDefaults() {
  }

  private void initLogin() {
    LOD2DemoInitApp urlOntoWiki = new LOD2DemoInitApp(state, "http://localhost/ontowiki");
    service = urlOntoWiki.service;
    service += pathExtension;
    String curGraph = state.getCurrentGraph();
    if (selectCurrentGraph){
      if (curGraph != null && !curGraph.equals("")){
        try {
          String encodedUrl = URLEncoder.encode(curGraph, "UTF-8");
          service += "/?m=" + encodedUrl;
        } catch (UnsupportedEncodingException e) {
          service += "/?m=unsupported-encoding";
          e.printStackTrace();
        }
      }
    }

  }

  private void activateCurrentGraph() {

    if (this.selectCurrentGraph && !(state.getCurrentGraph() == null || state.getCurrentGraph().isEmpty())) {

      try {
        RepositoryConnection con = state.getRdfStore().getConnection();

        String query = "create silent graph <" + state.getCurrentGraph() + ">";
        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
        tupleQuery.evaluate();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void notifyCurrentGraphChange(String graph) {
    this.refresh();
  }
}

