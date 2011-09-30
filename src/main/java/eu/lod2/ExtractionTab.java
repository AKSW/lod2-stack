
/*
 * Copyright 2011 LOD2 consortium
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

import java.net.*;
import java.net.URI;
import java.io.*;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Alignment.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.*;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.data.*;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.model.*;

import org.restlet.resource.ClientResource;
import org.restlet.data.MediaType;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;

/**
 * The extraction tab which collects information about 
 * ways and components to extract information.
 */
//@SuppressWarnings("serial")
public class ExtractionTab extends CustomComponent
    implements TextChangeListener 
{

    // reference to the global internal state
    private LOD2DemoState state;

    // 
    private VerticalLayout extractionTab;
    private Button annotateButton;
    private Label annotatedTextField;

    private String textToAnnotate;
    private String annotatedText;

    public ExtractionTab(LOD2DemoState st) {

        // The internal state and 
        state = st;

	// split the tab in two.
	HorizontalSplitPanel extractionTabRoot = new HorizontalSplitPanel();

	// first component
	Label rdfupload = new Label("upload RDF file");
	
	OptionGroup optiongroup = new OptionGroup("Select");
	optiongroup.addItem("Upload RDF file");
	optiongroup.addItem("Load RDF data from CKAN");
	optiongroup.addItem("Extract RDF from XML");
	optiongroup.addItem("Extract RDF from text w.r.t. DBpedia");
	optiongroup.addItem("Extract RDF from text w.r.t. a controlled vocabulary");
	optiongroup.setImmediate(true);
        optiongroup.addListener(new Property.ValueChangeListener() {
		//  Respond to change in the selection.
    		public void valueChange(Property.ValueChangeEvent event) {
        		// The event.getProperty() returns the Item ID (IID) 
        		// of the currently selected item in the component.
			showRightPanelContent(event);
    	}
	});



	optiongroup.setHeight("300px");
	extractionTabRoot.setFirstComponent(optiongroup);
        

	// second component
        extractionTab = new VerticalLayout();

	extractionTab.setHeight("300px");

/*
        Link rdfuploadlink = new Link("Upload RDF content to local storage",
                new ExternalResource(state.getHostName() + "/conductor/rdf_import.vspx?username=dba&t_login_pwd=dba&password=dba"));
        rdfuploadlink.setTargetName("_blank");
        rdfuploadlink.setTargetBorder(Link.TARGET_BORDER_NONE);
        extractionTab.addComponent(rdfuploadlink);
	*/
/*
	try { 
	  	URL url = new URL(state.getHostName() + "/conductor/rdf_import.vspx?username=dba&t_login_pwd=dba&password=dba");
		Embedded browser = new Embedded("", new ExternalResource(url));
		browser.setType(Embedded.TYPE_BROWSER);
		extractionTab.addComponent(browser);
	} catch (MalformedURLException e) {
                e.printStackTrace();
	};

        // Spotlight form start
        // annotate a plain text 
        // TODO: and add the result as RDF to the default graph
        Form t2f = new Form();
        t2f.setCaption("Annotate plain text");

        TextArea textToAnnotateField = new TextArea("text:");
        textToAnnotateField.setImmediate(false);
        textToAnnotateField.addListener(this);
        textToAnnotateField.setColumns(50);
        textToAnnotateField.setRows(10);
        t2f.getLayout().addComponent(textToAnnotateField);

        annotatedTextField = new Label("annotated text", Label.CONTENT_XHTML);
        t2f.getLayout().addComponent(annotatedTextField);

        // initialize the footer area of the form
        HorizontalLayout t2ffooterlayout = new HorizontalLayoutUpload();
        t2f.setFooter(t2ffooterlayout);

        annotateButton = new Button("Annotate with Spotlight", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                annotateText(event);
            }
        });
        annotateButton.setDescription("Annotate the text with Spotlight");
        annotateButton.setEnabled(false);

        t2f.getFooter().addComponent(annotateButton);

        extractionTab.addComponent(t2f);

        // Spotlight form end


        final Panel panel = new Panel("LOD2 components interfaces");

        VerticalLayout panelContent = new VerticalLayout();

        Link l = new Link("Virtuoso Web Interface",
                new ExternalResource(state.getHostName() + "/conductor/"));
        l.setTargetName("_blank");
        l.setTargetBorder(Link.TARGET_BORDER_NONE);
        panelContent.addComponent(l);


        Link t1l2 = new Link("OpenRDF Workbench",
                new ExternalResource(state.getHostName() + "/openrdf-workbench/"));
        t1l2.setTargetName("_blank");
        t1l2.setTargetBorder(Link.TARGET_BORDER_NONE);
        panelContent.addComponent(t1l2);

        Link t1l3 = new Link("Spotlight",
                new ExternalResource("http://dbpedia.org/spotlight"));
        t1l3.setTargetName("_blank");
        t1l3.setTargetBorder(Link.TARGET_BORDER_NONE);
        panelContent.addComponent(t1l3);

        Link t1l4 = new Link("D2R - Cordis",
                new ExternalResource(state.getHostName() + "/d2r-cordis"));
        t1l4.setTargetName("_blank");
        t1l4.setTargetBorder(Link.TARGET_BORDER_NONE);
        panelContent.addComponent(t1l4);

        panel.setContent(panelContent);
        extractionTab.addComponent(panel);
*/
	extractionTabRoot.setSecondComponent(extractionTab);

        // The composition root MUST be set
        setCompositionRoot(extractionTabRoot);
    }

    public void textChange(TextChangeEvent event) {

        textToAnnotate = event.getText();
        if (textToAnnotate == null || textToAnnotate.equals("")) {
            annotateButton.setEnabled(false);
        } else {    
            String encoded = "";
            try {
                encoded = URLEncoder.encode(textToAnnotate, "UTF-8");
                annotateButton.setEnabled(true);
            } catch (UnsupportedEncodingException e) { 
                annotateButton.setEnabled(false);
                e.printStackTrace();
            };
        };

    }

    private void annotateText(ClickEvent event) {
        try {
            String encoded = "";
            encoded = URLEncoder.encode(textToAnnotate, "UTF-8");
            ClientResource restcall = new ClientResource(
                    "http://spotlight.dbpedia.org/rest/annotate?text=" + encoded + "&confidence=0.4&support=20");

            //            String result = restcall.get().getText();  
	    //            TEXT_XML is usefull to have the resources already extracted, 
	    //            but it does not render directly on a label content.
            // String result = restcall.get(MediaType.TEXT_XML).getText();  
	    //     APPLICATION_XHTML will return an annotated text with rdfa.
            String result = restcall.get(MediaType.APPLICATION_XHTML).getText();  
            annotatedTextField.setValue(result);
        } catch (UnsupportedEncodingException e) { 
            annotateButton.setEnabled(false);
            e.printStackTrace();
        } catch (IOException e) { 
            annotateButton.setEnabled(false);
            e.printStackTrace();
        };

    };

	// propagate the information of one tab to another.
	public void setDefaults() {
	};

	public LOD2DemoState getState() {
		return state;
	};

    	public void showRightPanelContent(Property.ValueChangeEvent event) {
	        extractionTab.removeAllComponents();	
		if (event.getProperty().toString() == "Upload RDF file") {
			ELoadRDFFile second = new ELoadRDFFile(state);
			second.addStyleName("second");
			extractionTab.addComponent(second);
		} else if (event.getProperty().toString() == "Load RDF data from CKAN") {
			extractionTab.addComponent(new Label(event.getProperty().toString()));
		} else if (event.getProperty().toString() == "Extract RDF from XML") {
			extractionTab.addComponent(new EXMLExtended(this));
		} else if (event.getProperty().toString() == "Extract RDF from text w.r.t. DBpedia") {
			extractionTab.addComponent(new ESpotlight(this));
		} else if (event.getProperty().toString() == "Extract RDF from text w.r.t. a controlled vocabulary") {
			extractionTab.addComponent(new EPoolPartyExtractor(state));
		} else {
			extractionTab.addComponent(new Label(event.getProperty().toString()));
		};
	};


};

