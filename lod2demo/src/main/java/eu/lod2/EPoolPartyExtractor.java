
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
import eu.lod2.ExportSelector3;

/**
 * The extract from a natural language text (english) the relevant
 * concepts w.r.t. a controlled vocabulary in PoolParty.
 */
//@SuppressWarnings("serial")
public class EPoolPartyExtractor extends CustomComponent
implements TextChangeListener 
{

    // reference to the global internal state
    private LOD2DemoState state;

    // 
    private Button annotateButton;
    private Label annotatedTextField;

    private String textToAnnotate;
    private String annotatedText;

    private ExportSelector3 exportGraph;
    private TextField ppProjectId;
    private ComboBox textLanguage;

    public EPoolPartyExtractor(LOD2DemoState st) {

        // The internal state 
        state = st;


        // second component
        VerticalLayout panel = new VerticalLayout();

        Label description = new Label(
                "This service will identify text elements (tags) which correspond to concepts in a given controlled vocabulary using the PoolParty Extractor (PPX).\n"+
                "At the moment we have fixed the controlled vocabulary to be the Social Semantic Web thesaurus also available at CKAN.\n" + 
                "The identified concepts will be inserted as triples in the current graph.\n"
                );
        panel.addComponent(description);


        Form t2f = new Form();
        t2f.setDebugId(this.getClass().getSimpleName()+"_t2f");
        exportGraph = new ExportSelector3(state, true);
        exportGraph.setDebugId(this.getClass().getSimpleName()+"_exportGraph");
        t2f.getLayout().addComponent(exportGraph);

        ppProjectId = new TextField("PoolParty project Id:");
        ppProjectId.setDebugId(this.getClass().getSimpleName()+"_ppProjectId");
        ppProjectId.setDescription("The unique identifier of the PoolParty project to use for the extraction (usually a UUID like d06bd0f8-03e4-45e0-8683-fed428fca242) ");
        t2f.getLayout().addComponent(ppProjectId);

        textLanguage = new ComboBox("Language of the text:");
        textLanguage.setDebugId(this.getClass().getSimpleName()+"_textLanguage");
        textLanguage.setDescription("This is the language of the text. Language can be en (english) or de (german).");
        textLanguage.addItem("en");
        textLanguage.addItem("de");
        t2f.getLayout().addComponent(textLanguage);

        TextArea textToAnnotateField = new TextArea("text:");
        textToAnnotateField.setDebugId(this.getClass().getSimpleName()+"_textToAnnotateField");
        textToAnnotateField.setImmediate(false);
        textToAnnotateField.addListener(this);
        textToAnnotateField.setWidth("100%");
        textToAnnotateField.setRows(10);
        t2f.getLayout().addComponent(textToAnnotateField);

        // initialize the footer area of the form
        HorizontalLayout t2ffooterlayout = new HorizontalLayout();
        t2f.setFooter(t2ffooterlayout);

        annotateButton = new Button("Extract concepts", new ClickListener() {
                public void buttonClick(ClickEvent event) {
                annotateText(event);
                }
                });
        annotateButton.setDebugId(this.getClass().getSimpleName()+"_annotateButton");
        annotateButton.setDescription("Extract the relevant concepts w.r.t. the controlled vocabulary in PoolParty");
        annotateButton.setEnabled(false);

        t2f.getFooter().addComponent(annotateButton);

        panel.addComponent(t2f);


        // The composition root MUST be set
        setCompositionRoot(panel);
    }

    // make the button activate when suffient conditions are met to execute the action.
    // NOTE: the order could be a problem. This activation is only triggered by updating the text area.
    // not the other fields.
    public void textChange(TextChangeEvent event) {

        textToAnnotate = event.getText();
        if (exportGraph.getExportGraph() == null || exportGraph.getExportGraph().equals("")) {
            annotateButton.setEnabled(false);
        } else if (textToAnnotate == null || textToAnnotate.equals("")) {
            annotateButton.setEnabled(false);
        } else if (textLanguage.getValue() == null || textLanguage.getValue().equals("")) {
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

            String ppProjectIdVal = (String) ppProjectId.getValue();
            String textLanguageVal = (String) textLanguage.getValue();

            String restCallString = 
                "http://lod2.poolparty.biz/extractor/api/extract?text=" + encoded + 
                "&project=" + ppProjectIdVal +
                "&locale=" + textLanguageVal +
                "&format=rdfxml"+
                "&countConcepts=25"+
                "&countTerms=0";

            /* A call with the restlet package

               ClientResource restcall = new ClientResource(RestCallString);
               String result = restcall.get(MediaType.APPLICATION_RDF_XML).getText();  
             */


            java.net.URL data = new java.net.URL(restCallString);
            String baseURI = "http://poolparty.biz/defaultns#";

            RepositoryConnection con = state.getRdfStore().getConnection();
            Resource contextURI = con.getValueFactory().createURI(exportGraph.getExportGraph());
            Resource[] contexts = new Resource[] {contextURI};
            con.add(data, baseURI, RDFFormat.RDFXML, contexts);

        } catch (RepositoryException e) {
            annotateButton.setEnabled(false);
            e.printStackTrace();
        } catch (MalformedURLException e) {
            annotateButton.setEnabled(false);
            e.printStackTrace();
        } catch (RDFParseException e) {
            annotateButton.setEnabled(false);
            e.printStackTrace();
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

