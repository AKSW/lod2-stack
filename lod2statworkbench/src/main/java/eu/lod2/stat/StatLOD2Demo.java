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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import com.vaadin.Application;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.terminal.*;
import com.vaadin.terminal.gwt.server.UploadException;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.MenuBar.*;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
import org.vaadin.googleanalytics.tracking.*;
import eu.lod2.*;
import eu.lod2.stat.CustomComponentFactory.CompType;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class StatLOD2Demo extends Application
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
    	
        state = new LOD2DemoState();
        customComponentFactory = new CustomComponentFactory(state);

        mainWindow = new Window("LOD2 Prototype");
        setTheme("lod2");
        mainContainer =  new VerticalLayout();
	    mainWindow.addComponent(mainContainer);
	    mainContainer.setSizeFull();
	
	    //TODO: current graph??

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

        //menubarContainer.addComponent(lod2logo);
        welcomeContainer.addComponent(welcomeSlagzin);
        welcomeContainer.setComponentAlignment(welcomeSlagzin, Alignment.TOP_LEFT);
        welcomeContainer.addComponent(homeb);
        welcomeContainer.setComponentAlignment(homeb, Alignment.TOP_RIGHT);
        welcomeContainer.addComponent(currentgraphlabel);
        welcomeContainer.setComponentAlignment(currentgraphlabel, Alignment.TOP_RIGHT);

        //create login/logout component that shows currently logged in user
        LoginStatus login = new LoginStatus(state,workspace);
        welcomeContainer.addComponent(login);
        welcomeContainer.setComponentAlignment(login, Alignment.TOP_RIGHT);
        welcomeContainer.setWidth("100%");

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
        MenuBar.Command cmdValidation = getCustomComponentCommand(CompType.Validation);
        MenuBar.Command cmdUploadRDF = getCustomComponentCommand(CompType.UploadRDF);
        MenuBar.Command cmdExtractXML = getCustomComponentCommand(CompType.ExtractFromXML, false);
        MenuBar.Command cmdExtractXMLE = getCustomComponentCommand(CompType.ExtractFromXMLExtended, false);
        MenuBar.Command cmdLoadFromPublicData = getFramedUrlCommand("http://publicdata.eu/dataset?res_format=RDF&q=rdf");
        MenuBar.Command cmdLoadFromDataHub = getFramedUrlCommand("http://datahub.io/dataset?groups=lodcloud");
        MenuBar.Command cmdD2R = getCustomComponentCommand(CompType.D2R);
        MenuBar.Command cmdSparqled = getCustomComponentCommand(CompType.Sparqled);
        MenuBar.Command cmdSparqledManager = getCustomComponentCommand(CompType.SparqledManager);
        MenuBar.Command cmdSparqlOntowiki = getCustomComponentCommand(CompType.SparqlOW);
        MenuBar.Command cmdSparqlVirtuoso = getCustomComponentCommand(CompType.SparqlVirtuoso);
        MenuBar.Command cmdSparqlVirtuosoI = getCustomComponentCommand(CompType.SparqlIVirtuoso);
        MenuBar.Command cmdOntoWikiEdit = getCustomComponentCommand(CompType.EditWithOW);
        MenuBar.Command cmdSparqlUpdateVirtuoso = getFramedUrlCommand(sparqlAuthURL);
        MenuBar.Command cmdPoolPartyEdit = getCustomComponentCommand(CompType.OnlinePoolParty);
        MenuBar.Command cmdCkan = getCustomComponentCommand(CompType.CKAN);
        MenuBar.Command cmdGeoSpatial = getCustomComponentCommand(CompType.GeoSpatial);
        MenuBar.Command cmdSilk = getCustomComponentCommand(CompType.Silk);
        MenuBar.Command cmdLodRefine = getCustomComponentCommand(CompType.LodRefine);
        MenuBar.Command cmdLimes = getCustomComponentCommand(CompType.Limes);
        MenuBar.Command cmdSameAs = getCustomComponentCommand(CompType.SameAs);
        MenuBar.Command cmdPublicData = getFramedUrlCommand("http://publicdata.eu");
        MenuBar.Command cmdSigMa = getFramedUrlCommand("http://sig.ma");
        MenuBar.Command cmdSindice = getFramedUrlCommand("http://sindice.com");
        MenuBar.Command cmdLODCloud = getCustomComponentCommand(CompType.LODCloud);
        MenuBar.Command cmdDBPedia = getCustomComponentCommand(CompType.DBPedia);
        MenuBar.Command cmdSPARQLPoolParty = getCustomComponentCommand(CompType.SPARQLPoolParty);
        MenuBar.Command cmdMondecaSPARQLList = getCustomComponentCommand(CompType.MondecaSPARQLList);
        MenuBar.Command cmdDemoConfig = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                workspace.removeAllComponents();
                ConfigurationTab content = new ConfigurationTab(state, currentgraphlabel);
                workspace.addComponent(content);
                // stretch the content to the full workspace area
                welcome.setHeight("110px");
                content.setHeight("500px");
            }  
        };
        MenuBar.Command userinfoCommand = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                showInWorkspace(new Authenticator(new UserInformation(state), state));
            }
        };

        MenuBar.Command publishCommand = new Command() {
            public void menuSelected(MenuItem selectedItem){
                // publishing should be protected with an authenticator, otherwise a store could be published
                // without provenance information!
                showInWorkspace(new Authenticator(new CKANPublisherPanel(state), state));
            }
        };

        // root menus
        MenuBar.MenuItem menuGraph    	= menubar.addItem("Graph", null, null);
        MenuBar.MenuItem menuExtraction = menubar.addItem("Extraction & Loading", null, null);
        MenuBar.MenuItem menuEdit     	= menubar.addItem("Edit & Transform", null, null);
        MenuBar.MenuItem menuQuery      = menubar.addItem("Querying & Exploration", null, null);
        MenuBar.MenuItem menuEnrich    	= menubar.addItem("Enrichment", null, null);
        MenuBar.MenuItem menuOnline   	= menubar.addItem("Online Tools & Services", null, null);
        MenuBar.MenuItem menuHelp 		= menubar.addItem("Help", null, null);
        
        //graph menu
        menuGraph.addItem("Create Knowledge Base", null, cmdOntoWikiCreateKB);
        menuGraph.addItem("Import", null, cmdOntoWikiImport);
        menuGraph.addItem("Validate", null, cmdValidation);
        menuGraph.addItem("Publish to CKAN", null, publishCommand);
        
        // edit menu
        menuEdit.addItem("Edit Graph (OntoWiki)", null, cmdOntoWikiEdit);
        menuEdit.addItem("Edit Code Lists (PoolParty)", null, cmdPoolPartyEdit);
        menuEdit.addItem("Transform and Update Graph (SPARQL Update Endpoint)", null, cmdSparqlUpdateVirtuoso);
        
        // extraction menus
        menuExtraction.addItem("Upload RDF File or RDF from URL", null, cmdUploadRDF);
        MenuBar.MenuItem itemExtractFromXML = menuExtraction.addItem("Extract RDF from XML", null, null);
        itemExtractFromXML.addItem("Basic extraction", null, cmdExtractXML);
        itemExtractFromXML.addItem("Extended extraction", null, cmdExtractXMLE);
        menuExtraction.addItem("Load RDF data from publicdata.eu", null, cmdLoadFromPublicData);
        menuExtraction.addItem("Load RDF data from Data Hub", null, cmdLoadFromDataHub);
        menuExtraction.addItem("Extract RDF from SQL", null, cmdD2R);
        
        // querying menu
        MenuBar.MenuItem itemSparqlQuerying = menuQuery.addItem("SPARQL querying", null, null);
        MenuBar.MenuItem itemSparqled = itemSparqlQuerying.addItem("SparQLed - Assisted Querying", null, cmdSparqled);
        itemSparqled.addItem("Use currently selected graph", null, cmdSparqled);
        itemSparqled.addItem("Use manager to calculate summary graph", null, cmdSparqledManager);
        itemSparqlQuerying.addItem("OntoWiki SPARQL endpoint", null, cmdSparqlOntowiki);
        itemSparqlQuerying.addItem("Virtuoso SPARQL endpoint", null, cmdSparqlVirtuoso);
        itemSparqlQuerying.addItem("Virtuoso interactive SPARQL endpoint", null, cmdSparqlVirtuosoI);
//        menuQuery.addItem("Find RDF Data Cubes", null, null);
//        menuQuery.addItem("RDF Data Cube Matching Analysis", null, null);
        menuQuery.addItem("*Visualization with CubeViz", null, null);
        menuQuery.addItem("CKAN", null, cmdCkan);
        menuQuery.addItem("Geo-Spatial exploration", null, cmdGeoSpatial);
        
        // enrichment menu
        menuEnrich.addItem("Interlinking dimensions (Silk)", null, cmdSilk);
        menuEnrich.addItem("Data enrichment and reconciliation (LODRefine)", null, cmdLodRefine);
        menuEnrich.addItem("Interlinking with Limes", null, cmdLimes);
        menuEnrich.addItem("Interlinking with SameAs", null, cmdSameAs);
        
        // online menu
        menuOnline.addItem("Sindice", null, cmdSindice);
        menuOnline.addItem("Sig.ma", null, cmdSigMa);
        menuOnline.addItem("Europe's Public Data", null, cmdPublicData);
        MenuBar.MenuItem itemOnlineSparql = menuOnline.addItem("Online SPARQL Endpoints", null, null);
        itemOnlineSparql.addItem("LOD cloud", null, cmdLODCloud);
        itemOnlineSparql.addItem("DBPedia", null, cmdDBPedia);
        itemOnlineSparql.addItem("PoolParty SPARQL endpoint", null, cmdSPARQLPoolParty);
        itemOnlineSparql.addItem("Mondeca SPARQL endpoint Collection", null, cmdMondecaSPARQLList);
        
        // help menu
        menuHelp.addItem("Demonstrator Configuration", null, cmdDemoConfig);
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
        tracker.trackPageview("/lod2demo");
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
        workspace.addComponent(component);
        // stretch the content to the full workspace area
        welcome.setHeight("110px");
        component.setSizeFull();
        workspace.setSizeFull();
        workspace.setExpandRatio(component, 1.0f);
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
                workspace.addComponent(content);
                // stretch the content to the full workspace area
                welcome.setHeight("110px");
                content.setSizeFull();
                if (expand) {
                	workspace.setSizeFull();
	                workspace.setExpandRatio(content, 1.0f);
	                mainContainer.setExpandRatio(workspace, 2.0f);
	                mainWindow.getContent().setSizeFull();
                }
            }
		};
    }
}


