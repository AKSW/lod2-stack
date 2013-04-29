/*
 * Copyright 2011 LOD2.eu consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package eu.lod2.stat;

import com.turnguard.webid.tomcat.security.WebIDUser;
import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.terminal.gwt.server.UploadException;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
import eu.lod2.*;
import eu.lod2.stat.CustomComponentFactory.CompType;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class StatLOD2Demo extends Application implements LOD2DemoState.CurrentGraphListener, HttpServletRequestListener
{
	
    private LOD2DemoState state;


    private Window mainWindow;
    private VerticalLayout mainContainer;
    private VerticalLayout workspace;

    private Label     currentgraphlabel;
    private VerticalLayout welcome;
    
    private CustomComponentFactory customComponentFactory;

    //    private static final Logger logger = Logger.getLogger(LOD2Demo.class.getName());


    @Override
    public void init() {
        ensureState();

        customComponentFactory = new CustomComponentFactory(state);

        mainWindow = new Window("LOD2 Statistical Workbench DEMO");
        setTheme("lod2");
        mainContainer =  new VerticalLayout();
	    mainWindow.addComponent(mainContainer);
	    mainContainer.setSizeFull();

        final AbsoluteLayout welcomeSlagzin = new AbsoluteLayout();
        welcomeSlagzin.setWidth("370px");
        welcomeSlagzin.setHeight("75px");
        final Link homepage = new Link();
        homepage.setResource(new ExternalResource("http://lod2.eu"));
        final ThemeResource logo = new ThemeResource("app_images/logo-lod2-small.png");
        homepage.setIcon(logo);
        welcomeSlagzin.addComponent(homepage, "top:0px; left:5px");
        homepage.setSizeFull();
        homepage.addStyleName("logo");


        // the current graph as label
/*
            currentgraphlabel = new Label("no current graph selected");
            currentgraphlabel.addStyleName("currentgraphlabel");
*/

        Button homeb = new Button("home");
        homeb.setDebugId(this.getClass().getSimpleName()+"_homeb");
        homeb.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                home();
            }
        });
        homeb.setStyleName(BaseTheme.BUTTON_LINK);
        homeb.addStyleName("currentgraphlabel");

        currentgraphlabel = state.cGraph;
        currentgraphlabel.addStyleName("currentgraphlabel");
        // Create an horizontal container
        HorizontalLayout welcomeContainer = new HorizontalLayout();
        HorizontalLayout stateContainer = new HorizontalLayout();
        VerticalLayout toolsContainer = new VerticalLayout();
        toolsContainer.setWidth("100%");
        welcomeContainer.setWidth("100%");


        //menubarContainer.addComponent(lod2logo);
        welcomeContainer.addComponent(welcomeSlagzin);
        welcomeContainer.addComponent(toolsContainer);
        toolsContainer.addComponent(stateContainer);
        welcomeContainer.setComponentAlignment(welcomeSlagzin, Alignment.TOP_LEFT);
        stateContainer.addComponent(homeb);
        welcomeContainer.setComponentAlignment(toolsContainer, Alignment.TOP_RIGHT);
        stateContainer.addComponent(currentgraphlabel);
        stateContainer.setComponentAlignment(homeb, Alignment.TOP_LEFT);
        stateContainer.setComponentAlignment(currentgraphlabel, Alignment.TOP_RIGHT);


        final VerticalLayout welcome = new VerticalLayout();
        welcome.addComponent(welcomeContainer);
        // unfortunately, we need to be able to build components from outside
        // this initialization function and the welcome component needs to be
        // resized properly afterward
        this.welcome=welcome;

        mainContainer.addComponent(welcome);


        //************************************************************************
        //  menu bar style
        //
        MenuBar menubar = new MenuBar();
        menubar.setDebugId(this.getClass().getSimpleName()+"_menubar");

        // First define all menu commands

        String sparqlAuthURL;
        if (state.getHostName().equals("http://localhost:8080")) {
			sparqlAuthURL = "http://localhost:8890/sparql-auth";
		} else {
			sparqlAuthURL = state.getHostName() + "/virtuoso/sparql-auth";
		};
		
		MenuBar.Command cmdOntoWikiCreateKB = getCustomComponentCommand(CompType.CreateKB);
        MenuBar.Command cmdOntoWikiImport = getCustomComponentCommand(CompType.ImportCSV);

        MenuBar.Command cmdValidation = new MenuBar.Command() {
			public void menuSelected(MenuItem selectedItem) {
				showInWorkspace(new Validation(state, workspace));
			}
		};
        //MenuBar.Command cmdUploadRDF = getCustomComponentCommand(CompType.UploadRDF);
        //MenuBar.Command cmdExtractXML = getCustomComponentCommand(CompType.ExtractFromXML, false);
        //MenuBar.Command cmdExtractXMLE = getCustomComponentCommand(CompType.ExtractFromXMLExtended, false);
        MenuBar.Command cmdLoadFromPublicData = getFramedUrlCommand("http://publicdata.eu/dataset?q=statistical&res_format=application%2Frdf%2Bxml&_res_format_limit=0&sort=relevance+asc");
        MenuBar.Command cmdLoadFromDataHub = getFramedUrlCommand("http://datahub.io/dataset?tags=statistics&q=&groups=lodcloud");
        //MenuBar.Command cmdD2R = getCustomComponentCommand(CompType.D2R);
        MenuBar.Command cmdSparqled = getCustomComponentCommand(CompType.Sparqled);
        MenuBar.Command cmdSparqledManager = getCustomComponentCommand(CompType.SparqledManager);
        MenuBar.Command cmdSparqlOntowiki = getCustomComponentCommand(CompType.SparqlOW);
        MenuBar.Command cmdSparqlVirtuoso = getCustomComponentCommand(CompType.SparqlVirtuoso);
        MenuBar.Command cmdSparqlVirtuosoI = getCustomComponentCommand(CompType.SparqlIVirtuoso);
        MenuBar.Command cmdOntoWikiEdit = getCustomComponentCommand(CompType.EditWithOW);
        MenuBar.Command cmdSparqlUpdateVirtuoso = getFramedUrlCommand(sparqlAuthURL);
        MenuBar.Command cmdPoolPartyEdit = getCustomComponentCommand(CompType.OnlinePoolParty);
        //MenuBar.Command cmdCkan = getCustomComponentCommand(CompType.CKAN);
        MenuBar.Command cmdGeoSpatial = getCustomComponentCommand(CompType.GeoSpatial);
        MenuBar.Command cmdSilk = getCustomComponentCommand(CompType.Silk);
        MenuBar.Command cmdLodRefine = getCustomComponentCommand(CompType.LodRefine);
        MenuBar.Command cmdLimes = getCustomComponentCommand(CompType.Limes);
        MenuBar.Command cmdSameAs = getCustomComponentCommand(CompType.SameAs);
        //MenuBar.Command cmdPublicData = getFramedUrlCommand("http://publicdata.eu");
        //MenuBar.Command cmdSigMa = getFramedUrlCommand("http://sig.ma");
        MenuBar.Command cmdSindice = getFramedUrlCommand("http://sindice.com/main/submit");
        //MenuBar.Command cmdLODCloud = getCustomComponentCommand(CompType.LODCloud);
        MenuBar.Command cmdDBPedia = getCustomComponentCommand(CompType.DBPedia);
        MenuBar.Command cmdSPARQLPoolParty = getCustomComponentCommand(CompType.SPARQLPoolParty);
        MenuBar.Command cmdMondecaSPARQLList = getCustomComponentCommand(CompType.MondecaSPARQLList);
        MenuBar.Command cmdEditDataset = this.getEditDatasetCommand(this.state);
        MenuBar.Command cmdEditStructureDef = this.getEditStructureDefinition(this.state);
        MenuBar.Command cmdEditComponentProp = this.getEditComponentPropertyCommand(this.state);
        MenuBar.Command cmdVisualizeCubeviz = getCustomComponentCommand(CompType.VisualizeCubeviz);

        MenuBar.Command cmdDemoConfig = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                workspace.removeAllComponents();
                ConfigurationTab content = new ConfigurationTab(state);
                workspace.addComponent(content);
                // stretch the content to the full workspace area
                welcome.setHeight("110px");
                content.setHeight("500px");
            }  
        };
        MenuBar.Command userinfoCommand = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                showInWorkspace(new Authenticator(new UserInformation(state), new HashSet<String>(Arrays.asList(state.userRole)), state));
            }
        };

        MenuBar.Command publishCommand = new Command() {
            public void menuSelected(MenuItem selectedItem){
                // publishing should be protected with an authenticator, otherwise a store could be published
                // without provenance information!
                showInWorkspace(new Authenticator(new CKANPublisherPanel(state), new HashSet<String>(Arrays.asList(state.adminRole)), state));
            }
        };

        MenuBar.Command mDeleteGraphs = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                showInWorkspace(new Authenticator(new DeleteGraphs(state), new HashSet<String>(Arrays.asList(state.adminRole)),state));
            }
        };

        MenuBar.Command extractXML = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                showInWorkspace(new EXML(state));
            }
        };
        MenuBar.Command extractXMLExtended = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                showInWorkspace(new EXMLExtended(state));
            }
        };

        MenuBar.Command importDirectly = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                showInWorkspace(new OntoWikiPathExtended(state,"/model/add",true));
            }
        };

        MenuBar.Command exportRDFXML = getOWExportCommand("rdfxml");
        MenuBar.Command exportTurtle = getOWExportCommand("turtle");
        MenuBar.Command exportRDFJson = getOWExportCommand("rdfjson");
        MenuBar.Command exportRDFN3 = getOWExportCommand("rdfn3");

        /*
         legend for menu item names:
         - *: stub
         - !: incomplete functionality
         */

        // root menus
        MenuBar.MenuItem menuGraph    	= menubar.addItem("Manage Graph", null, null);
        MenuBar.MenuItem menuExtraction = menubar.addItem("Find more Data Online", null, null);
        MenuBar.MenuItem menuEdit     	= menubar.addItem("Edit & Transform", null, null);
        //MenuBar.MenuItem menuQuery      = menubar.addItem("Querying & Exploration", null, null);
        MenuBar.MenuItem menuEnrich    	= menubar.addItem("Enrich Datacube", null, null);
        //MenuBar.MenuItem menuOnline   	= menubar.addItem("Online Tools & Services", null, null);
        MenuBar.MenuItem menuPresent    = menubar.addItem("Present & Publish", null, null);
        MenuBar.MenuItem menuHelp 		= menubar.addItem("Help", null, null);

        //graph menu
        menuGraph.addItem("Select Default Graph", null, cmdDemoConfig);
        menuGraph.addItem("Create Graph", null, cmdOntoWikiCreateKB);
        MenuBar.MenuItem menuImport=menuGraph.addItem("Import", null, null);
        menuImport.addItem("Import from CSV",null,cmdOntoWikiImport);
        MenuBar.MenuItem excelImport= menuImport.addItem("Import from XML", null, null);
        MenuBar.MenuItem directImport= menuImport.addItem("Import triples from file", null, importDirectly);
        excelImport.addItem("From Text", null, extractXML);
        excelImport.addItem("From File", null, extractXMLExtended);
        MenuBar.MenuItem menuExport= menuGraph.addItem("Export", null, null);
        menuExport.addItem("Export as RDF/XML", null, exportRDFXML);
        menuExport.addItem("Export as Turtle", null, exportTurtle);
        menuExport.addItem("Export as RDF/JSON", null, exportRDFJson);
        menuExport.addItem("Export as Notation 3", null, exportRDFN3);
        menuGraph.addItem("Validate", null, cmdValidation);
        menuGraph.addItem("Remove Graphs", null, mDeleteGraphs);

        // edit menu
        MenuItem editmenu=menuEdit.addItem("Edit Graph (OntoWiki)", null, cmdOntoWikiEdit);
        editmenu.addItem("Edit qb:Dataset",null, cmdEditDataset);
        editmenu.addItem("Edit qb:StructureDefinition",null, cmdEditStructureDef);
        editmenu.addItem("Edit qb:ComponentProperty",null,cmdEditComponentProp);
        menuEdit.addItem("Edit Code Lists (PoolParty)", null, cmdPoolPartyEdit);
        menuEdit.addItem("Transform and Update Graph (SPARQL Update Endpoint)", null, cmdSparqlUpdateVirtuoso);
        
        // extraction menus
        //menuExtraction.addItem("Upload RDF File or RDF from URL", null, cmdUploadRDF);
        //MenuBar.MenuItem itemExtractFromXML = menuExtraction.addItem("Extract RDF from XML", null, null);
        //itemExtractFromXML.addItem("Basic extraction", null, cmdExtractXML);
        //itemExtractFromXML.addItem("Extended extraction", null, cmdExtractXMLE);
        menuExtraction.addItem("Load RDF data from publicdata.eu", null, cmdLoadFromPublicData);
        menuExtraction.addItem("Load RDF data from Data Hub", null, cmdLoadFromDataHub);
        //menuExtraction.addItem("Extract RDF from SQL", null, cmdD2R);
        
        // querying menu
        // many sparql query frontends are attached to the same endpoint (virtuoso) Removing duplicates
        //MenuBar.MenuItem itemSparqlQuerying = menuEdit.addItem("SPARQL querying", null, null);
        MenuBar.MenuItem itemSparqled = menuEdit.addItem("SparQLed - Assisted Querying", null, cmdSparqled);
        itemSparqled.addItem("Use currently selected graph", null, cmdSparqled);
        itemSparqled.addItem("Use manager to calculate summary graph", null, cmdSparqledManager);
        //itemSparqlQuerying.addItem("OntoWiki SPARQL endpoint", null, cmdSparqlOntowiki);
        //itemSparqlQuerying.addItem("Virtuoso SPARQL endpoint", null, cmdSparqlVirtuoso);
        //itemSparqlQuerying.addItem("Virtuoso interactive SPARQL endpoint", null, cmdSparqlVirtuosoI);
//        menuQuery.addItem("Find RDF Data Cubes", null, null);
//        menuQuery.addItem("RDF Data Cube Matching Analysis", null, null);
        menuPresent.addItem("Visualization with CubeViz", null, cmdVisualizeCubeviz);
        // seems like duplicate of publicdata.eu
        //menuQuery.addItem("CKAN", null, cmdCkan);
        menuPresent.addItem("Geo-Spatial exploration", null, cmdGeoSpatial);
        menuPresent.addItem("Publish to CKAN", null, publishCommand);

        // enrichment menu
        menuEnrich.addItem("Interlinking dimensions (Silk)", null, cmdSilk);
        menuEnrich.addItem("Data enrichment and reconciliation (LODRefine)", null, cmdLodRefine);
        menuEnrich.addItem("Interlinking with Limes", null, cmdLimes);
        menuEnrich.addItem("Interlinking with SameAs", null, cmdSameAs);
        
        // online menu
        //moved to present and publish
        menuPresent.addItem("Publish to Sindice", null, cmdSindice);
        //menuOnline.addItem("Sig.ma", null, cmdSigMa); // not a fitting case for stat wb?
        // duplicate?
        //menuOnline.addItem("Europe's Public Data", null, cmdPublicData);
        //MenuBar.MenuItem itemOnlineSparql = menuOnline.addItem("Online SPARQL Endpoints", null, null);
        // no longer working
        //itemOnlineSparql.addItem("LOD cloud", null, cmdLODCloud);
        // moved to find more data
        menuExtraction.addItem("DBPedia", null, cmdDBPedia);
        // moved to sparql querying
        menuEdit.addItem("PoolParty Code Lists SPARQL endpoint", null, cmdSPARQLPoolParty);
        // moved to extract
        menuExtraction.addItem("Mondeca SPARQL endpoint Collection", null, cmdMondecaSPARQLList);
        
        // help menu
        menuHelp.addItem("User Configuration", null, userinfoCommand);
        menuHelp.addItem("*Documentation", null, null);
        menuHelp.addItem("*Examples", null, null);
        menuHelp.addItem("*About", null, null);


        HorizontalLayout menubarContainer = new HorizontalLayout();
        menubarContainer.addComponent(menubar);
        menubarContainer.addStyleName("menubarContainer");
        menubarContainer.setWidth("100%");
        welcome.addComponent(menubarContainer);
        welcome.setHeight("110px");


        //************************************************************************
        // add workspace
        workspace = new VerticalLayout();

        mainContainer.addComponent(workspace);


        //create login/logout component that shows currently logged in user
        LoginStatus login = new LoginStatus(state,workspace);
        toolsContainer.addComponent(login);
        //welcome.setComponentAlignment(login, Alignment.TOP_RIGHT);

    /*
        workspace.setHeight("80%");

        HorizontalLayout introH = new HorizontalLayout();
        Embedded lod2cycle = new Embedded("", new ThemeResource("app_images/lod-lifecycle-small.png"));
        lod2cycle.setMimeType("image/png");
        introH.addComponent(lod2cycle);
        introH.setComponentAlignment(lod2cycle, Alignment.MIDDLE_LEFT);

        VerticalLayout introV =  new VerticalLayout();
        introH.addComponent(introV);

        Label introtextl =  new Label(introtext, Label.CONTENT_XHTML);
        introV.addComponent(introtextl);
        introtextl.setWidth("400px");

        HorizontalLayout introVH =  new HorizontalLayout();
        introV.addComponent(introVH);

        Embedded euflag = new Embedded("", new ThemeResource("app_images/eu-flag.gif"));
        euflag.setMimeType("image/gif");
        introVH.addComponent(euflag);
        euflag.addStyleName("eugif");
        euflag.setHeight("50px");
        Embedded fp7 = new Embedded("", new ThemeResource("app_images/fp7-gen-rgb_small.gif"));
        fp7.setMimeType("image/gif");
        fp7.addStyleName("eugif");
        fp7.setHeight("50px");
        introVH.addComponent(fp7);

        workspace.addComponent(introH);
        */
        home();



        // Create a tracker for the demo.lod2.eu domain.
        if (!state.googleAnalyticsID.equals("")) {
//            GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker("UA-26375798-1", "demo.lod2.eu");
        GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(state.googleAnalyticsID, state.googleAnalyticsDomain);
        mainWindow.addComponent(tracker);
        tracker.trackPageview("/lod2statworkbench");
        };



        setMainWindow(mainWindow);
    
//	    mainWindow.setExpandRatio(workspace, 1.0f);

        if (!state.InitStatus) {
            mainWindow.showNotification(
                    "Initialization Demonstration Failed",
                    state.ErrorMessage,
                    Notification.TYPE_ERROR_MESSAGE);
        };



    }

    /**
     * This function ensures that a state is present. If a state object exists, then that object is reused, if it does not, it is created.
     * Also returns the state for convenience
     */
    private LOD2DemoState ensureState() {
        if(state==null){
            state = new LOD2DemoState();
        }
        return state;
    }

    public void home() {
        workspace.removeAllComponents();
        workspace.setHeight("80%");

        HorizontalLayout introH = new HorizontalLayout();
        Embedded lod2cycle = new Embedded("", new ThemeResource("app_images/lod-lifecycle-small.png"));
        lod2cycle.setMimeType("image/png");
        introH.addComponent(lod2cycle);
        introH.setComponentAlignment(lod2cycle, Alignment.MIDDLE_LEFT);

        VerticalLayout introV =  new VerticalLayout();
        introH.addComponent(introV);

        Label introtextl =  new Label(introtext, Label.CONTENT_XHTML);
        introV.addComponent(introtextl);
        introtextl.setWidth("400px");

        HorizontalLayout introVH =  new HorizontalLayout();
        introV.addComponent(introVH);

        Embedded euflag = new Embedded("", new ThemeResource("app_images/eu-flag.gif"));
        euflag.setMimeType("image/gif");
        introVH.addComponent(euflag);
        euflag.addStyleName("eugif");
        euflag.setHeight("50px");
        Embedded fp7 = new Embedded("", new ThemeResource("app_images/fp7-gen-rgb_small.gif"));
        fp7.setMimeType("image/gif");
        fp7.addStyleName("eugif");
        fp7.setHeight("50px");
        introVH.addComponent(fp7);

        workspace.addComponent(introH);

    }

    public void currentGraphChange(TextChangeEvent event) {

        if (event != null && event.getText() != null) {
            state.setCurrentGraph(event.getText());
        };
    };

    public void setDefaults() {
        currentgraphlabel.setValue(state.getCurrentGraph());
    };

    private String introtext = 
        "<p>This is Version 2.0 of the LOD2 Stack, which comprises a number of tools " +
        "for managing the life-cycle of Linked Data. The life-cycle comprises in " + 
        "particular the stages" + 
        "</p><p>" + 
        "<ul>" + 
        "<li>Extraction of RDF from text, XML and SQL</li>" + 
        "<li>Querying and Exploration using SPARQL</li>" + 
        "<li>Authoring of Linked Data using a Semantic Wiki</li>" + 
        "<li>Semi-automatic link discovery between Linked Data sources</li>" + 
        "<li>Knowledge-base Enrichment and Repair</li>" + 
        "</ul>" + 
        "</p><p>" + 
        "You can access tools for each of these stages using the menu on top." + 
        "</p><p>" + 
        "The LOD2 Stack is developed by the LOD2 project consortium comprising 15" + 
        "research groups and companies. The LOD2 project is co-funded by the" + 
        "European Commission within the 7th Framework Programme (GA no. 257934)." + 
        "</p><p>" + 
        "You can find further information about the LOD2 Stack at <a href=\"http://stack.lod2.eu\">http://stack.lod2.eu</a> " +
        "and the LOD2 project at <a href=\"http://lod2.eu\">http://lod2.eu</a>." +
        "<p>";

   
    private void resetSize(AbstractComponentContainer comp) {
	
	    System.err.println("reset sizing");

	    Iterator<Component> it = comp.getComponentIterator();
	    while (it.hasNext()) {
			Component c = it.next();
			if (c instanceof AbstractComponent) { 
				AbstractComponent ac = (AbstractComponent) c;
				ac.setSizeUndefined();
				System.err.println("Size:"+ac.getHeight());
				};
			if (c instanceof AbstractComponentContainer) { 
				AbstractComponentContainer acc = (AbstractComponentContainer) c;
				resetSize(acc) ; 
				};
		};
 
    };

    private void resetSizeFull(AbstractComponentContainer comp) {

	    Iterator<Component> it = comp.getComponentIterator();
	    while (it.hasNext()) {
			Component c = it.next();
			if (c instanceof AbstractComponent) { 
				AbstractComponent ac = (AbstractComponent) c;
				ac.setSizeFull();
				if (ac.getHeight() < 0) {
					ac.setHeight("100%");
				};
				};
			if (c instanceof AbstractComponentContainer) { 
				AbstractComponentContainer acc = (AbstractComponentContainer) c;
				resetSizeFull(acc) ; 
				};
		};
 
    };

    private void printSize(AbstractComponentContainer comp) {

	    System.err.println("PrintSizing");
	    System.err.println("Container Start");
		
	    Iterator<Component> it = comp.getComponentIterator();
	    while (it.hasNext()) {
			Component c = it.next();
			if (c instanceof AbstractComponent) { 
				AbstractComponent ac = (AbstractComponent) c;
				System.err.println("Size: Height: "+ac.getHeight() + " Width: " + ac.getWidth());
				};
			if (c instanceof AbstractComponentContainer) { 
				AbstractComponentContainer acc = (AbstractComponentContainer) c;
				printSize(acc) ; 
				};
		};
	     System.err.println("Container end");
 
    };

    @Override
    public void terminalError(Terminal.ErrorEvent event) {
        Window errorWindow = mainWindow;

        try {
            UploadException uploadException = (UploadException) event.getThrowable();
            System.err.println(event.getThrowable().getMessage());
        } catch (Exception e) {
           // not an UploadException
           // Shows an error notification
            if (errorWindow != null) {
                StringWriter sw=new StringWriter();
                PrintWriter writer=new PrintWriter(sw);
                event.getThrowable().printStackTrace(writer);
                String stack=sw.toString();
                errorWindow.showNotification(
                        "An internal error has occurred, please " +
                                "contact the administrator!",
                        "The error message was: \n"+
                                stack,
                        Notification.TYPE_ERROR_MESSAGE);
                System.err.println(event.getThrowable().getMessage());
                System.err.println(event.toString());
            }
        }

    }

    //* shows the given component in this application's workspace.
    public void showInWorkspace(AbstractComponent component) {
        workspace.removeAllComponents();
        // stretch the content to the full workspace area
        welcome.setHeight("110px");
        ActionRecorder recorder=new ActionRecorder(component,"http://lod2.eu/id/tool/"+
                (component instanceof DecoratorComponent ?
                        ((DecoratorComponent) component).getLeafComponent().getClass().getSimpleName():
                        component.getClass().getSimpleName()),state);
        workspace.addComponent(recorder);
        recorder.setSizeFull();
        workspace.setSizeFull();
        workspace.setExpandRatio(recorder, 1.0f);
        mainContainer.setExpandRatio(workspace, 2.0f);
        mainWindow.getContent().setSizeFull();

    }

    /** Just a method to get rid of the boilerplate code when Commands are created for the menu
     * @param url - url for the frame
     * @return
     */
    private MenuBar.Command getFramedUrlCommand(final String url){
    	return new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                workspace.removeAllComponents();
                IframedUrl content = new IframedUrl(state, url);
                workspace.addComponent(content);
                // stretch the content to the full workspace area
                welcome.setHeight("110px");
                content.setSizeFull();
                workspace.setSizeFull();
                workspace.setExpandRatio(content, 1.0f);
                mainContainer.setExpandRatio(workspace, 2.0f);
                mainWindow.getContent().setSizeFull();
            }
        };
    }

    /**
     * A function to get an ontowiki export command
     * @param format - the formating string defining the format to export in
     */
    private MenuBar.Command getOWExportCommand(final String format){
        return new MenuBar.Command() {
            public void menuSelected(final MenuItem selectedItem) {
                String currentGraph=state.getCurrentGraph();
                if(currentGraph==null || currentGraph.isEmpty()){
                    final Window window=new Window("No graph selected");
                    window.setWidth("550px");
                    LOD2DemoState.CurrentGraphListener listener=new LOD2DemoState.CurrentGraphListener() {
                        boolean ignoreFirst=true;
                        public void notifyCurrentGraphChange(String graph) {
                            if(ignoreFirst){
                                ignoreFirst=false;
                                return;
                            }
                            window.getParent().removeWindow(window);
                            menuSelected(selectedItem);
                            state.removeCurrentGraphListener(this);
                        }
                    };
                    VerticalLayout layout=(VerticalLayout) window.getContent();
                    layout.addComponent(new Label("You did not specify a graph to work with. Please do so below and try again: "));
                    ConfigurationTab configure=new ConfigurationTab(state);
                    layout.addComponent(configure);
                    layout.setComponentAlignment(configure, Alignment.BOTTOM_CENTER);
                    getMainWindow().addWindow(window);
                    window.center();
                    state.addCurrentGraphListener(listener);
                    return;
                }
                try {
                    getMainWindow().open(new ExternalResource(state.getHostNameWithoutPort()+"/ontowiki/model/export?m="+
                            URLEncoder.encode(currentGraph,"UTF-8")+"&f="+format));
                } catch (UnsupportedEncodingException e) {
                    // should never happen
                    throw new RuntimeException("The lod2 server encountered error when exporting the graph: "+e.getMessage()+" Please contact an administrator");
                }
            }
        };
    }


    /** Just a method to get rid of the boilerplate code when Commands are created for the menu
     * @param componentType - a factory that takes CompType as an argument is used because 
     * the component has to be created inside menuSelected() method. Otherwise the constructor
     * is executed before the item is clicked in the menu.
     * @return
     */
    private MenuBar.Command getCustomComponentCommand(final CompType componentType){
    	return getCustomComponentCommand(componentType, true);
    }
    
    /** Just a method to get rid of the boilerplate code when Commands are created for the menu
     * @param componentType - a factory that takes CompType as an argument is used because 
     * the component has to be created inside menuSelected() method. Otherwise the constructor
     * is executed before the item is clicked in the menu.
     * @param expand - some components need less 'initialization'
     * @return
     */
    private MenuBar.Command getCustomComponentCommand(final CompType componentType, final boolean expand){
        return new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                workspace.removeAllComponents();
                CustomComponent content = customComponentFactory.create(componentType);
                ActionRecorder recorder = new ActionRecorder(content, "http://lod2.eu/id/tool/"+componentType.name(), state);
                workspace.addComponent(recorder);
                // stretch the content to the full workspace area
                welcome.setHeight("110px");
                recorder.setSizeFull();
                if (expand) {
                	workspace.setSizeFull();
	                workspace.setExpandRatio(recorder, 1.0f);
	                mainContainer.setExpandRatio(workspace, 2.0f);
	                mainWindow.getContent().setSizeFull();
                }

            }
        };
    }

    private Command getEditDatasetCommand(final LOD2DemoState state){
        return new Command(){
            public void menuSelected(MenuItem selectedItem){
                String currentGraph=state.getCurrentGraph();
                final SparqlResultSelector choices=new SparqlResultSelector(
                        "select distinct ?s ?name " +
                                "from <"+currentGraph+"> "+
                                "where {" +
                                "?s a <http://purl.org/linked-data/cube#DataSet>." +
                                "optional { ?s rdfs:label ?name.}" +
                                "}", state);
                choices.setCaption("Select the dataset to edit");
                choices.setMessage("Please select the dataset to edit from the drop-down menu: ");
                choices.setWidth("550px");
                choices.setModal(true);
                choices.addListener(new Property.ValueChangeListener() {
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        String key=(String) valueChangeEvent.getProperty().getValue();
                        String dataset=choices.getResults().get(key);

                        showInWorkspace(new OntoWikiPathExtended(state,"/resource/properties?m="+state.getCurrentGraph()+"&r="+dataset,false));
                    }
                });

                getMainWindow().addWindow(choices);
            }
        };
    }

    private Command getEditStructureDefinition(final LOD2DemoState state){
        return new Command(){
            public void menuSelected(MenuItem selectedItem){
                String currentGraph=state.getCurrentGraph();
                final SparqlResultSelector choices=new SparqlResultSelector(
                        "select distinct ?s ?name " +
                                "from <"+currentGraph+"> "+
                                "where {" +
                                "?s a <http://purl.org/linked-data/cube#DataStructureDefinition>." +
                                "optional { ?s rdfs:label ?name.}" +
                                "}", state);
                choices.setCaption("Select the data structure definition to edit");
                choices.setMessage("Please select the data structure definition to edit from the drop-down menu: ");
                choices.setWidth("550px");
                choices.setModal(true);
                choices.addListener(new Property.ValueChangeListener() {
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        String key=(String) valueChangeEvent.getProperty().getValue();
                        String dataset=choices.getResults().get(key);

                        showInWorkspace(new OntoWikiPathExtended(state,"/resource/properties?m="+state.getCurrentGraph()+"&r="+dataset,false));
                    }
                });

                getMainWindow().addWindow(choices);
            }
        };
    }

    private Command getEditComponentPropertyCommand(final LOD2DemoState state){
        return new Command(){
            public void menuSelected(MenuItem selectedItem){
                String currentGraph=state.getCurrentGraph();
                final SparqlResultSelector choices=new SparqlResultSelector(
                        "select distinct ?s ?name " +
                                "from <"+currentGraph+"> "+
                                "where {" +
                                "{ ?s a  <http://purl.org/linked-data/cube#DimensionProperty> } union "+
                                "{ ?s a  <http://purl.org/linked-data/cube#ComponentProperty> } union "+
                                "{ ?s a  <http://purl.org/linked-data/cube#AttributeProperty> } union "+
                                "{ ?s a  <http://purl.org/linked-data/cube#MeasureProperty> } "+
                                "optional { ?s rdfs:label ?name.} " +
                                "}", state);
                choices.setCaption("Select the component property to edit");
                choices.setMessage("Please select the component property to edit from the drop-down menu: ");
                choices.setWidth("550px");
                choices.setModal(true);
                choices.addListener(new Property.ValueChangeListener() {
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        String key=(String) valueChangeEvent.getProperty().getValue();
                        String dataset=choices.getResults().get(key);

                        showInWorkspace(new OntoWikiPathExtended(state,"/resource/properties?m="+state.getCurrentGraph()+"&r="+dataset,false));
                    }
                });

                getMainWindow().addWindow(choices);
            }
        };
    }

    public void notifyCurrentGraphChange(String graph) {
        this.currentgraphlabel.setValue(graph);
    }

    /**
     * Method gets called when a client performs a request. This is the only way to access the http request object apparently
     */
    public void onRequestStart(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        LOD2DemoState state=ensureState();
        Principal user=httpServletRequest.getUserPrincipal();
        if(user instanceof WebIDUser){
            state.setUser((WebIDUser)user);
        }
    }

    /**
     * Method gets called when a client's request ends. This needs to be implemented as well when one wants to access the request information
     */
    public void onRequestEnd(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // thank you, come again!
    }

    private class SparqlResultSelector extends Window implements Property.ValueChangeListener, LOD2DemoState.CurrentGraphListener{
        private String query;
        private Set<Property.ValueChangeListener> listeners=new HashSet<Property.ValueChangeListener>();
        private LOD2DemoState state;

        //* the results that were returned by the query. Maps from name to uri. Name is the representation to be shown to the user
        private Map<String,String> results;

        public SparqlResultSelector(String query, LOD2DemoState state){
            super();
            this.query=query;
            this.state=state;
            this.state.addCurrentGraphListener(this);
        }

        private void refresh(){
            this.removeAllComponents();
            this.results=this.fetchResults();
            this.buildSelector();
        }

        @Override
        public void detach(){
            super.detach();
            this.state.removeCurrentGraphListener(this);
        }

        public void addListener(Property.ValueChangeListener listener){
            this.listeners.add(listener);
        }

        public void removeListener(Property.ValueChangeListener listener){
            this.listeners.remove(listener);
        }

        public Map<String,String> getResults(){
            return new HashMap<String, String>(this.results);
        }

        private Map<String,String> fetchResults(){
            try{
                RepositoryConnection connection=state.getRdfStore().getConnection();
                TupleQueryResult result=connection.prepareTupleQuery(QueryLanguage.SPARQL, this.query).evaluate();
                List<String> bindings=result.getBindingNames();
                String nameBindingName="name";
                if(bindings.size()!=1 && (bindings.size()!=2 || !bindings.contains(nameBindingName))){
                    throw new IllegalArgumentException("The sparql result selector window requires its query to have only a single result, " +
                            "with an optional unique 'name' added to the result.");
                }

                boolean useName=bindings.size()>1;
                String resultName=nameBindingName; // default to name, so single binding of 'name' is allowed
                for(String bind:bindings){
                    if(!bind.equals(nameBindingName)){
                        resultName=bind;
                    }
                }
                Map<String, String> mappings=new HashMap<String, String>();
                while(result.hasNext()){
                    BindingSet res=result.next();
                    String value=res.getValue(resultName).stringValue();
                    String name;
                    if(useName && res.getValue(nameBindingName)!=null){
                        // use the name and the value for better readability
                        name=res.getValue(nameBindingName).stringValue() +": "+value;
                    }else{
                        name=value;
                    }
                    mappings.put(name,value);
                }
                return mappings;
            }catch (QueryEvaluationException e){
                throw new IllegalArgumentException("The query result selector requires its query to be valid.");
            } catch (RepositoryException e) {
                throw new IllegalStateException("The LOD2 demonstrator has been incorrectly configured: the repository could not be used.");
            } catch (MalformedQueryException e) {
                throw new IllegalArgumentException("The query result selector requires its query to be valid.");
            }
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        private String message;


        /**
         * Builds a selector for the query results in the current map. If the map is empty, a message is shown in stead.
         */
        public void buildSelector(){
            VerticalLayout content=(VerticalLayout)this.getContent();
            content.setSpacing(true);
            final Window window=this;

            String currentGraph=state.getCurrentGraph();
            if(currentGraph==null || currentGraph.isEmpty()){
                content.addComponent(new Label("You did not specify a graph to work with. Please do so below and try again: "));
                ConfigurationTab configure=new ConfigurationTab(state);
                content.addComponent(configure);
                content.setComponentAlignment(configure, Alignment.BOTTOM_CENTER);
                return;
            }else if(this.results.isEmpty()){
                content.addComponent(new Label("Sorry, no results were found for your request. Please ensure that your " +
                        "data contains a valid datacube (see 'Validate' under 'Manage Graph'). \n You can also select another graph below:"));
                ConfigurationTab configure=new ConfigurationTab(state);
                content.addComponent(configure);
                content.setComponentAlignment(configure, Alignment.BOTTOM_CENTER);
                Button ok=new Button("I will check");
                ok.addListener(new ClickListener() {
                    public void buttonClick(ClickEvent clickEvent) {
                        window.getParent().removeWindow(window);
                    }
                });
                content.addComponent(ok);
                content.setComponentAlignment(ok,Alignment.BOTTOM_CENTER);
                return;
            }

            NativeSelect selector=new NativeSelect(this.getMessage());

            String select=null;
            for(String key:this.results.keySet()){
                selector.addItem(key);
                select=key;
            }
            selector.setImmediate(true);
            selector.addListener(this);
            selector.setValue(select);
            selector.setWidth("80%");

            selector.setNullSelectionAllowed(false);

            content.addComponent(selector);
            content.setComponentAlignment(selector,Alignment.MIDDLE_CENTER);

            Button button= new Button("Select");
            content.addComponent(button);

            button.addListener(new ClickListener() {
                public void buttonClick(ClickEvent clickEvent) {
                    for(Property.ValueChangeListener listener:listeners){
                        listener.valueChange(lastChange);
                    }
                    getParent().removeWindow(window);
                }
            });
            content.setComponentAlignment(button,Alignment.BOTTOM_CENTER);
        }

        private Property.ValueChangeEvent lastChange;
        public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
            this.lastChange=valueChangeEvent;
        }

        public void notifyCurrentGraphChange(String graph) {
            this.query=this.query.replaceFirst("from <.*?>","from <"+graph+">");
            this.refresh();
        }
    }
}

