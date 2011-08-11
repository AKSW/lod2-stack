
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
import eu.lod2.ExtractionTab;

/**
 * The extract from a natural language text (english) the relevant
 * concepts w.r.t. a controlled vocabulary in PoolParty.
 */
//@SuppressWarnings("serial")
public class EPoolPartyExtractor extends CustomComponent
    implements TextChangeListener 
{

    // reference to the global internal state
    private ExtractionTab extractionTab;

    // 
    private Button annotateButton;
    private Label annotatedTextField;

    private String textToAnnotate;
    private String annotatedText;

    public EPoolPartyExtractor(ExtractionTab etab) {

        // The internal state 
	extractionTab = etab;


	// second component
        VerticalLayout panel = new VerticalLayout();

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
        HorizontalLayout t2ffooterlayout = new HorizontalLayout();
        t2f.setFooter(t2ffooterlayout);

        annotateButton = new Button("Extract concepts", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                annotateText(event);
            }
        });
        annotateButton.setDescription("Extract the relevant concepts w.r.t. the controlled vocabulary in PoolParty");
        annotateButton.setEnabled(false);

        t2f.getFooter().addComponent(annotateButton);

        panel.addComponent(t2f);


        // The composition root MUST be set
        setCompositionRoot(panel);
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
	            "http://pilot1.poolparty.biz/extractor/api/extract?text=" + encoded + 
			    "&project=2d5bb6fb-9aef-44f8-a587-15a1bd6332e1" +
			    "&locale=en" +
			    "&format=rdfxml"+
			    "&countConcepts=25"+
			    "&countTerms=25"
			    );

            //            String result = restcall.get().getText();  
	    //            TEXT_XML is usefull to have the resources already extracted, 
	    //            but it does not render directly on a label content.
            // String result = restcall.get(MediaType.TEXT_XML).getText();  
	    //     APPLICATION_XHTML will return an annotated text with rdfa.
            String result = restcall.get(MediaType.APPLICATION_RDF_XML).getText();  
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

};

