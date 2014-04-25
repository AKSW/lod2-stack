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
package eu.lod2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import com.vaadin.Application;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.terminal.*;
import com.vaadin.terminal.gwt.server.UploadException;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.MenuBar.*;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
import org.vaadin.googleanalytics.tracking.*;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class LOD2Demo extends Application implements LOD2DemoState.CurrentGraphListener
{

    private LOD2DemoState state;


    private Window mainWindow;
    private VerticalLayout mainContainer;
    private VerticalLayout workspace;

    private Label     currentgraphlabel;
    private VerticalLayout welcome;

    //    private static final Logger logger = Logger.getLogger(LOD2Demo.class.getName());

    @Override
        public void init() {
            state = new LOD2DemoState();


            mainWindow = new Window("LOD2 Prototype");
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

            //menubarContainer.addComponent(lod2logo);
            welcomeContainer.addComponent(welcomeSlagzin);
            welcomeContainer.setComponentAlignment(welcomeSlagzin, Alignment.TOP_LEFT);
            welcomeContainer.addComponent(homeb);
            welcomeContainer.setComponentAlignment(homeb, Alignment.TOP_RIGHT);
            welcomeContainer.addComponent(currentgraphlabel);
            welcomeContainer.setComponentAlignment(currentgraphlabel, Alignment.TOP_RIGHT);

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

            MenuBar.Command me1c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    ELoadRDFFile content = new ELoadRDFFile(state);
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

            MenuBar.Command me3c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    EXML me3c_content = new EXML(state);
                    workspace.addComponent(me3c_content);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                    me3c_content.setSizeFull();
                }  
            };
            MenuBar.Command me3cbis = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    EXMLExtended content = new EXMLExtended(state);
                    workspace.addComponent(content);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                    content.setSizeFull();
                }  
            };


            MenuBar.Command me4c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    ESpotlight content = new ESpotlight(state);
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

            MenuBar.Command me5c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    EPoolPartyExtractor me5c_content = new EPoolPartyExtractor(state);
                    workspace.addComponent(me5c_content);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                    me5c_content.setHeight("90%");
                }  
            };

            MenuBar.Command me6c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    D2RCordis content = new D2RCordis(state);
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
            
            MenuBar.Command me7c_1 = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                	workspace.removeAllComponents();
                    IframedUrl content = new IframedUrl(state, "http://publicdata.eu/dataset?res_format=RDF&q=rdf");
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
            
            MenuBar.Command me7c_2 = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                	workspace.removeAllComponents();
                    IframedUrl content = new IframedUrl(state, "http://datahub.io/dataset?groups=lodcloud");
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

            MenuBar.Command me8c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    EURL content = new EURL(state);
                    workspace.addComponent(content);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                    content.setHeight("90%");
                }  
            };

            MenuBar.Command me9c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    EPoolPartyLabel content = new EPoolPartyLabel(state);
                    workspace.addComponent(content);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                    content.setHeight("90%");
                }  
            };

            MenuBar.Command silk = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    LinkingTab lsilk = new LinkingTab(state);
                    workspace.addComponent(lsilk);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                    lsilk.setSizeFull();
                    workspace.setSizeFull();
                    workspace.setExpandRatio(lsilk, 1.0f);
                    mainContainer.setExpandRatio(workspace, 2.0f);
                    mainWindow.getContent().setSizeFull();
                }
            };

            MenuBar.Command limes = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    Limes limes = new Limes(state);
                    workspace.addComponent(limes);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                    limes.setSizeFull();
                    workspace.setSizeFull();
                    workspace.setExpandRatio(limes, 1.0f);
                    mainContainer.setExpandRatio(workspace, 2.0f);
                    mainWindow.getContent().setSizeFull();
                }
            };

            MenuBar.Command sameaslinking = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    SameAsLinking content = new SameAsLinking(state);
                    workspace.addComponent(content);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                    content.setSizeFull();
                }  
            };

            MenuBar.Command ore = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    ORE content = new ORE(state);
                    workspace.addComponent(content);
                    welcome.setHeight("110px");
                    content.setSizeFull();
                    workspace.setSizeFull();
                    workspace.setExpandRatio(content, 1.0f);
                    mainContainer.setExpandRatio(workspace, 2.0f);
                    mainWindow.getContent().setSizeFull();
                }
            };


            MenuBar.Command lodrefine = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    Lodrefine content = new Lodrefine(state);
                    workspace.addComponent(content);
                    welcome.setHeight("110px");
                    content.setSizeFull();
                    workspace.setSizeFull();
                    workspace.setExpandRatio(content, 1.0f);
                    mainContainer.setExpandRatio(workspace, 2.0f);
                    mainWindow.getContent().setSizeFull();
                }
            };

            MenuBar.Command mconfiguration = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    ConfigurationTab content = new ConfigurationTab(state);
                    workspace.addComponent(content);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                    content.setHeight("500px");
                }  
            };

            MenuBar.Command mabout = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    About content = new About(state);
                    workspace.addComponent(content);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                }  
            };

            MenuBar.Command mau = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    OntoWiki content = new OntoWiki(state);
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

            MenuBar.Command mq1c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    SesameSPARQL content = new SesameSPARQL(state);
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

            MenuBar.Command mq2c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    OntoWikiQuery content = new OntoWikiQuery(state);
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

            MenuBar.Command mq3c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    VirtuosoSPARQL content = new VirtuosoSPARQL(state);
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

            MenuBar.Command mq4c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    VirtuosoISPARQL content = new VirtuosoISPARQL(state);
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

            MenuBar.Command mq5c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    GeoSpatial content = new GeoSpatial(state);
                    workspace.addComponent(content);
	    	        resetSizeFull(workspace);
                    welcome.setHeight("110px");
		            workspace.setSizeFull();
		            workspace.setHeight("500px");
		            workspace.setExpandRatio(content,1.0f);
                    mainContainer.setExpandRatio(workspace, 2.0f);
                }
            };
            
            MenuBar.Command mq_s_6c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                	workspace.removeAllComponents();
                    Sparqled content = new Sparqled(state);
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
            
            MenuBar.Command mq_s_7c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                	workspace.removeAllComponents();
                    SparqledManager content = new SparqledManager(state);
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


            MenuBar.Command mo1c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    SameAs content = new SameAs(state);
                    workspace.addComponent(content);
                    // stretch the content to the full workspace area
                    welcome.setHeight("110px");
                    content.setHeight("500px");
                }  
            };

            MenuBar.Command mo2c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    Sigma content = new Sigma(state);
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

            MenuBar.Command mo3c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    LODCloud content = new LODCloud(state);
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

            MenuBar.Command mo4c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    DBpedia content = new DBpedia(state);
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

            MenuBar.Command mo5c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    SPARQLPoolParty content = new SPARQLPoolParty(state);
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


            MenuBar.Command mo6c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    OnlinePoolParty content = new OnlinePoolParty(state);
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

            MenuBar.Command mo7c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    MondecaSPARQLList content = new MondecaSPARQLList(state);
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

            MenuBar.Command mo8c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    CKAN content = new CKAN(state);
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

            MenuBar.Command mo9c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    IframedUrl content = new IframedUrl(state, "http://publicdata.eu");
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

            MenuBar.Command mo10c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    IframedUrl content = new IframedUrl(state, "http://sig.ma");
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

            MenuBar.Command mo11c = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    workspace.removeAllComponents();
                    IframedUrl content = new IframedUrl(state, "http://sindice.com");
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

            MenuBar.Command userinfoCommand = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    showInWorkspace(/*new Authenticator(*/new UserInformation(state)/*, state)*/);
                }
            };

            MenuBar.Command publishCommand = new Command() {
                public void menuSelected(MenuItem selectedItem){
                    // publishing should be protected with an authenticator, otherwise a store could be published
                    // without provenance information!
                    showInWorkspace(/*new Authenticator(*/new CKANPublisherPanel(state)/*, state)*/);
                }
            };

            MenuBar.Command mDeleteGraphs = new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    showInWorkspace(/*new Authenticator(*/new DeleteGraphs(state)/*, state)*/);
                }
            };


        // Secondly define menu layout
            // root menu's
            MenuBar.MenuItem extraction    = menubar.addItem("Extraction & Loading", null, null);
            MenuBar.MenuItem querying      = menubar.addItem("Querying & Exploration", null, null);
            MenuBar.MenuItem authoring     = menubar.addItem("Authoring", null, null);
            MenuBar.MenuItem linking       = menubar.addItem("Linking", null, null);
            MenuBar.MenuItem enrichment    = menubar.addItem("Enrichment & Data Cleaning", null, null);
            MenuBar.MenuItem onlinetools   = menubar.addItem("Online Tools & Services", null, null);
            MenuBar.MenuItem configuration = menubar.addItem("Configuration", null, null);

            // sub menu's 
            MenuBar.MenuItem me1  = extraction.addItem("Upload RDF file or RDF from URL", null, me1c);
            //	    MenuBar.MenuItem me1b = extraction.addItem("Import RDF data from URL", null, me8c);
            MenuBar.MenuItem me2_1  = extraction.addItem("Load RDF data from publicdata.eu", null, me7c_1);
            MenuBar.MenuItem me2_2  = extraction.addItem("Load LOD cloud RDF data from the Data Hub", null, me7c_2);
            MenuBar.MenuItem me3  = extraction.addItem("Extract RDF from XML", null, null);
            MenuBar.MenuItem me6  = extraction.addItem("Extract RDF from SQL", null, me6c);
            MenuBar.MenuItem me4  = extraction.addItem("Extract RDF from text w.r.t. DBpedia",null, me4c);
            MenuBar.MenuItem me5  = extraction.addItem("Extract RDF from text w.r.t. a controlled vocabulary", null, me5c);
            //	    MenuBar.MenuItem me9  = extraction.addItem("Complete RDF w.r.t. a controlled vocabulary", null, me9c);

            MenuBar.MenuItem exml     = me3.addItem("Basic extraction", null, me3c);
            MenuBar.MenuItem extended = me3.addItem("Extended extraction", null, me3cbis);


            MenuBar.MenuItem mq1 = querying.addItem("SPARQL querying", null, null);
            MenuBar.MenuItem mq2 = querying.addItem("Sig.ma EE", null, mo2c);
            MenuBar.MenuItem mq3 = querying.addItem("Geo-spatial exploration", null, mq5c);
            // TODO: replace this with a menu with two entries, editor and manager, after stephane fixes the manager
            MenuBar.MenuItem mqs5 = mq1.addItem("SparQLed - Assisted Querying", null, mq_s_6c);
            MenuBar.MenuItem mqsparqled1 = mqs5.addItem("Use currently selected graph", null, mq_s_6c);
            MenuBar.MenuItem mqsparqled2 = mqs5.addItem("Use manager to calculate summary graph", null, mq_s_7c);
            //MenuBar.MenuItem mqs1 = mq1.addItem("Direct via Sesame API", null, mq1c);
            MenuBar.MenuItem mqs2 = mq1.addItem("OntoWiki SPARQL endpoint", null, mq2c);
            MenuBar.MenuItem mqs3 = mq1.addItem("Virtuoso SPARQL endpoint", null, mq3c);
            MenuBar.MenuItem mqs4 = mq1.addItem("Virtuoso interactive SPARQL endpoint", null, mq4c);

            MenuBar.MenuItem ma = authoring.addItem("OntoWiki", null, mau);
            MenuBar.MenuItem publishing = authoring.addItem("Publish to CKAN", null, publishCommand);

            MenuBar.MenuItem linking1 = linking.addItem("Silk", null, silk);
            MenuBar.MenuItem linking2 = linking.addItem("Limes", null, limes);
            MenuBar.MenuItem linking3 = linking.addItem("SameAs Linking", null, sameaslinking);

            MenuBar.MenuItem enrichment1 = enrichment.addItem("ORE", null, ore);
            MenuBar.MenuItem enrichment2 = enrichment.addItem("LOD enabled Refine", null, lodrefine);

            MenuBar.MenuItem sameAs       = onlinetools.addItem("SameAs", null, mo1c);
            MenuBar.MenuItem sindice      = onlinetools.addItem("Sindice", null, mo11c);
            MenuBar.MenuItem sigmaOnline  = onlinetools.addItem("Sigma", null, mo10c);
            MenuBar.MenuItem ckan      = onlinetools.addItem("CKAN", null, mo8c);
            MenuBar.MenuItem publicdata = onlinetools.addItem("Europe's Public Data", null, mo9c);
            MenuBar.MenuItem poolparty = onlinetools.addItem("PoolParty", null, mo6c);
            MenuBar.MenuItem sparqlonline = onlinetools.addItem("Online SPARQL endpoints", null, null);
            MenuBar.MenuItem lodcloud        = sparqlonline.addItem("LOD cloud", null, mo3c);
            MenuBar.MenuItem dbpedia         = sparqlonline.addItem("DBpedia", null, mo4c);
            MenuBar.MenuItem sparqlpoolparty = sparqlonline.addItem("PoolParty SPARQL endpoint", null, mo5c);
            MenuBar.MenuItem mondecalist     = sparqlonline.addItem("Mondeca SPARQL endpoint Collection", null, mo7c);

            MenuBar.MenuItem conf  = configuration.addItem("Demonstrator configuration", null, mconfiguration);
            MenuBar.MenuItem userconf = configuration.addItem("UserConfiguration", null, userinfoCommand);
            MenuBar.MenuItem about = configuration.addItem("About", null, mabout);
            MenuBar.MenuItem delgraphs = configuration.addItem("Delete Graphs", null, mDeleteGraphs);


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
            LoginStatus login = new LoginStatus(state,this.workspace);
            welcomeContainer.addComponent(login);
            welcomeContainer.setComponentAlignment(login, Alignment.TOP_RIGHT);
            welcomeContainer.setWidth("100%");

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

    public void notifyCurrentGraphChange(String graph) {
        this.currentgraphlabel.setValue(graph);
    }
}


