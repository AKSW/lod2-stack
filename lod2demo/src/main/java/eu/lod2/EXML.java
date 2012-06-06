
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

import java.io.*;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;

import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.model.*;

import eu.lod2.slimvaliant.*;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * extract RDF data from an XML file using an XSLT transformation
 */
public class EXML extends CustomComponent
implements 	Button.ClickListener
{

    // reference to the global internal state
    private LOD2DemoState state;

    private VerticalLayout panel;
    private ByteArrayOutputStream oStream;

    //
    //private Button annotateButton; 
    private Button transformButton;
    private Button uploadButton;
    private Label annotatedTextField;
    private Label errorMsg;

    private String textToAnnotate;
    private String annotatedText;

    private TextArea xmlText;
    private TextArea xsltText;
    private TextArea rdfResultField;

    private ExportSelector exportGraph;

    private Form t2f;

    public EXML(){ 
    };

    public EXML(LOD2DemoState st) {
        this();
        state = st;

        // The internal state and

        panel = new VerticalLayout();

        Label desc = new Label(
                "This page aids the extraction of RDF out of an XML document.<br/>" + 
                "This is done by defining an XSLT transformation which transforms the XML document into a set of RDF triples.<br/>" +
                "The resulting triples are uploaded in the share RDF store.<br/>" +
                "This pages is the simplified flow where you paste your document and XSLT transformation and see the result immediately."
                , Label.CONTENT_XHTML);

        xmlText = new TextArea("Enter your xml code:");
        xmlText.setImmediate(false);
        xmlText.setColumns(100);
        xmlText.setRows(25);
        xmlText.setRequired(true);
        xsltText = new TextArea("Enter your xslt code:");
        xsltText.setImmediate(false);
        xsltText.setColumns(100);
        xsltText.setRows(25);
        xsltText.setRequired(true);

        exportGraph = new ExportSelector(st);

        uploadButton = new Button("Upload result to RDF Store", (Button.ClickListener) this);

        transformButton = new Button("transform XML to RDF", (Button.ClickListener) this);
        errorMsg = new Label("");

        panel.addComponent(desc);
        panel.addComponent(xmlText);
        panel.addComponent(xsltText);
        panel.addComponent(transformButton);
        panel.addComponent(exportGraph);
        panel.addComponent(uploadButton);
        panel.addComponent(errorMsg);

        errorMsg.setVisible(false);

        t2f = new Form();
        t2f.setCaption("");

        annotatedTextField = new Label("Extracted RDF", Label.CONTENT_XHTML);
        t2f.getLayout().addComponent(annotatedTextField);

        rdfResultField = new TextArea();
        rdfResultField.setImmediate(false);
        rdfResultField.setColumns(100);
        rdfResultField.setRows(25);
        t2f.getLayout().addComponent(rdfResultField);

        panel.addComponent(t2f);
        t2f.setVisible(false);


        // The composition root MUST be set
        setCompositionRoot(panel);

    }

    public void buttonClick(ClickEvent event) {
        try{
        if(event.getComponent()==transformButton){
            transform();
        }
        else if(event.getComponent()==uploadButton){
            uploadToVirtuoso();
        }
        } catch (LOD2Exception e) {
          showExceptionMessage(e);
        };
    }

    // propagate the information of one tab to another.
    public void setDefaults() {
    };

    private void transform() throws LOD2Exception {
        errorMsg.setVisible(false);
        InputStream xmlStream, xsltStream;
        oStream = new ByteArrayOutputStream();
        StreamResult sResult = new StreamResult(oStream);

        System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory" , "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");

        if(xmlText.getValue().toString().isEmpty()){
            errorMsg.setVisible(true);
            errorMsg.setValue("Enter a xml text first.");
            return;
        }
        else if(xsltText.getValue().toString().isEmpty()){
            errorMsg.setVisible(true);
            errorMsg.setValue("Enter a xslt code.");
            return;
        }
        try{
            xmlStream = new ByteArrayInputStream(xmlText.getValue().toString().getBytes("UTF-8"));
            xsltStream = new ByteArrayInputStream(xsltText.getValue().toString().getBytes("UTF-8"));	

            XsltTransformer xsltTransformer = new XsltTransformer(new StreamSource(xsltStream));
            xsltTransformer.transform(xmlStream, sResult);
        } catch (Exception e){
            e.printStackTrace();
            throw new LOD2Exception("Transformation failed", e);
        }
        if(oStream.toString().isEmpty()){
            rdfResultField.setValue("Transformation results in no triples; please check if you entered a valid xml and xslt code.");
        } else {
            rdfResultField.setValue(oStream.toString());
            t2f.setVisible(true);
        };
    }

    private void uploadToVirtuoso() throws LOD2Exception {
        if(exportGraph.getExportGraph() == null){
            panel.addComponent(new Label("No graph selected"));
            return;
        }
        else if(oStream == null || oStream.toString().isEmpty()){
            transform();
            if(oStream.toString().isEmpty()){return;}
        }
        try{
            File rdfFile = new File ("/tmp/uploads/file.rdf");
            FileOutputStream fos = new FileOutputStream(rdfFile);
            oStream.writeTo(fos);
            String baseURI = state.getCurrentGraph() + "#";

            RepositoryConnection con = state.getRdfStore().getConnection();
            Resource contextURI = con.getValueFactory().createURI(exportGraph.getExportGraph());
            Resource[] contexts = new Resource[] {contextURI};
            con.add(rdfFile, baseURI, RDFFormat.RDFXML, contexts);
        } catch (Exception e){
            e.printStackTrace();
            throw new LOD2Exception("Upload Failed: ", e);
        }
        panel.addComponent(new Label("Upload succeeded!"));
    }

    private void showExceptionMessage(Exception e) {

            this.getWindow().showNotification(
                    "The operation failed due some errors. See for detailed information to the catalina log. ",
                    e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE);
    };
};

