
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

/**
 * Extract RDF data from a text document using spotlight. 
 */
//@SuppressWarnings("serial")
public class ESpotlight extends CustomComponent
    implements TextChangeListener 
{

    // reference to the global internal state
    private LOD2DemoState state;

    // 
    private Button annotateButton;
    private Label annotatedTextField;

    private String textToAnnotate;
    private String annotatedText;

    public ESpotlight(LOD2DemoState st) {

        // The internal state and 
        state = st;

        VerticalLayout panel = new VerticalLayout();


        // Spotlight form start
        // annotate a plain text 
        // TODO: and add the result as RDF to the default graph
        Form t2f = new Form();
        t2f.setDebugId(this.getClass().getSimpleName()+"_t2f");
        t2f.setCaption("Annotate plain text");

        TextArea textToAnnotateField = new TextArea("text:");
        textToAnnotateField.setDebugId(this.getClass().getSimpleName()+"_textToAnnotateField");
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

        annotateButton = new Button("Annotate with Spotlight", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                annotateText(event);
            }
        });
        annotateButton.setDebugId(this.getClass().getSimpleName()+"_annotateButton");
        annotateButton.setDescription("Annotate the text with Spotlight");
        annotateButton.setEnabled(false);

        t2f.getFooter().addComponent(annotateButton);

        panel.addComponent(t2f);

        // Spotlight form end



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

};

