
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;


import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.UserError;
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
public class EXMLExtended extends CustomComponent
implements Button.ClickListener
{

    private VerticalLayout panel;

    File  xmlFile;     // The original file
    File  rdfFile;     // The RDF file containing the triples derived via the XSLT
    File  xsltFile;	   // The XSLT transformation file
    File  catalogFile; // The XML Catalog file that connects multiple xslt & xml files

    //
    //private Button annotateButton;
    private Label annotatedTextField;
    private Label errorMsg;

    private LOD2DemoState state;

    private TextArea textToAnnotateField;
    private TextField dlPath;
    private TextField dlFileName;

    private Button transformButton;
    private Button downloadButton;
    private Button uploadButton;

    private Form downloadForm, t2f;

    private ExportSelector exportGraph;

    private ByteArrayOutputStream oStream;


    FileUpload uploadXMLFile = null;
    FileUpload uploadXSLTFile = null;
    FileUpload uploadCatalogFile = null;
    //    final Upload uploadDTDFile = new Upload("Upload the DTD file here", this);

    public EXMLExtended(){

    }
    public EXMLExtended(LOD2DemoState state) {
        this();
        this.state = state;


        // The internal state and

        panel = new VerticalLayout();
        panel.setSpacing(true);

        Label desc = new Label(
                "This page aids the extraction of RDF out of an XML document.<br/>" + 
                "This is done by defining an XSLT transformation which transforms the XML document into a set of RDF triples.<br/>" +
                "The resulting triples are uploaded in the share RDF store.",
                Label.CONTENT_XHTML
                );

        uploadXMLFile = new FileUpload("Upload the XML file here", "The XML file ", xmlFile);
        uploadXSLTFile = new FileUpload("Upload the XLST file here", "The XSLT file ", xsltFile);
        uploadCatalogFile = new FileUpload("Upload the XML Catalog file here", "The XML Catalog file ", catalogFile);
        uploadCatalogFile.setDescription("This file must be named catalog.xml. " +
                "The references can be to files on the local system or web reachable resources.");


        errorMsg = new Label("");

        exportGraph = new ExportSelector(state, true);
        uploadButton = new Button("Upload result to RDF Store", (Button.ClickListener) this);


        transformButton = new Button("transform XML to RDF", (Button.ClickListener) this);

        panel.addComponent(desc);
        panel.addComponent(uploadXMLFile);
        panel.addComponent(uploadXSLTFile);
        panel.addComponent(uploadCatalogFile);
        panel.addComponent(exportGraph);
        panel.addComponent(uploadButton);
        panel.addComponent(transformButton);
        panel.addComponent(errorMsg);

        errorMsg.setVisible(false);


        t2f = new Form();
        t2f.setCaption("");

        annotatedTextField = new Label("Extracted RDF", Label.CONTENT_XHTML);
        t2f.getLayout().addComponent(annotatedTextField);

        textToAnnotateField = new TextArea();
        textToAnnotateField.setImmediate(false);
        textToAnnotateField.setColumns(100);
        textToAnnotateField.setRows(25);
        t2f.getLayout().addComponent(textToAnnotateField);

        panel.addComponent(t2f);
        t2f.setVisible(false);

        downloadForm = new Form();
        downloadForm.setCaption("Download file.");
        dlFileName = new TextField();
        dlFileName.setRequired(true);
        //dlFileName.setCaption("Give a filename. (Required)");
        downloadForm.getLayout().addComponent(new Label("Give a filename."));
        downloadForm.getLayout().addComponent(dlFileName);
        dlPath = new TextField();
        dlPath.setRequired(true);
        //dlPath.setCaption("Specify a path. (Required for downloading)");
        downloadForm.getLayout().addComponent(new Label("Specify a path."));
        downloadForm.getLayout().addComponent(dlPath);
        downloadButton = new Button("Download file", (Button.ClickListener) this);
        downloadForm.getLayout().addComponent(downloadButton);

        panel.addComponent(downloadForm);
        downloadForm.setVisible(false);

        // The composition root MUST be set
        setCompositionRoot(panel);

    }

    // propagate the information of one tab to another.
    public void setDefaults() {
    };

    public void buttonClick(ClickEvent event) {
        try {
        if(event.getComponent()==transformButton){
            this.transform();
        }
        else if(event.getComponent()==downloadButton){
            download();
        }
        else if(event.getComponent()==uploadButton){
            uploadToVirtuoso();
        }
        } catch (LOD2Exception e) {
             showExceptionMessage(e);
        };
    }

    // check the inputs if everything is available
    private boolean validTransformInput() {
        boolean valid = true;
        if(xmlFile == null) {
            uploadXMLFile.setComponentError(new UserError("Upload a xml file."));
            valid = false;
        }
        if(xsltFile == null) {
            uploadXSLTFile.setComponentError(new UserError("Upload a xlst file."));
            valid = false;
        }
        if (catalogFile == null) {
            uploadCatalogFile.setComponentError(new UserError("Upload a catalog file."));
            valid = false;
        };

        return valid;

    }

    private void transform() throws LOD2Exception {

        errorMsg.setVisible(false);
        InputStream xmlStream, xsltStream;
        oStream = new ByteArrayOutputStream();
        StreamResult sResult = new StreamResult(oStream);

        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        //System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory" , "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");

        xmlFile = uploadXMLFile.file;
        xsltFile = uploadXSLTFile.file;
        catalogFile = uploadCatalogFile.file;

        if (!validTransformInput()) {
            // break here
            return;
        }

        try {
            xmlStream = new FileInputStream(xmlFile);
            xsltStream = new FileInputStream(xsltFile);
            XsltTransformer xsltTransformer = new XsltTransformer(new StreamSource(xsltStream));
            xsltTransformer.transform(xmlStream, sResult);
            if(oStream.toString().isEmpty()){
                textToAnnotateField.setValue("Transformation resulted in no triples; please check if you entered a valid xml and xslt code.");
            } else {
                textToAnnotateField.setValue(oStream.toString());
                t2f.setVisible(true);
                downloadForm.setVisible(true);
            };
            xmlStream.close();
            xmlStream.close();
            xsltTransformer.close();
        } catch (Exception e){
            e.printStackTrace();
            throw new LOD2Exception("Transformation failed:" , e);
        }
    };

    private void download(){
        String path = dlPath.getValue().toString();
        if(path.charAt(path.length() -1) != '/'){
            path += "/";
        }
        try{
            File dlFile = new File(path + dlFileName.getValue().toString() + ".rdf");
            FileOutputStream fos = new FileOutputStream(dlFile);
            oStream.writeTo(fos);
        } catch (Exception e){
            e.printStackTrace();
            panel.addComponent(new Label("Download failed"));
            return;
        }
        panel.addComponent(new Label("Download succeeded!"));
    }

    private void uploadToVirtuoso() throws LOD2Exception {
        if(exportGraph.getExportGraph() == null) {
            uploadButton.setComponentError(new UserError("No graph selected"));
            return;
        } else if(oStream == null || oStream.toString().isEmpty()){
            uploadButton.setComponentError(null);
            this.transform();
            if(oStream.toString().isEmpty()){

                this.getWindow().showNotification(
                        "The transformation results in an empty dataset. This can be correct, or indicate an issue.",
                        "",
                        Notification.TYPE_WARNING_MESSAGE);
                return;
            }
        }
        try{
            rdfFile = new File (state.getUploadDir() + "file.rdf");
            FileOutputStream fos = new FileOutputStream(rdfFile);
            oStream.writeTo(fos);
            String baseURI = state.getCurrentGraph() + "#";

            RepositoryConnection con = state.getRdfStore().getConnection();
            Resource contextURI = con.getValueFactory().createURI(exportGraph.getExportGraph());
            Resource[] contexts = new Resource[] {contextURI};
            con.add(rdfFile, baseURI, RDFFormat.RDFXML, contexts);
        } catch (Exception e){
            e.printStackTrace();
            throw new LOD2Exception("Upload failed:", e);
        }
        this.getWindow().showNotification(
                "The processing has succeeded.",
                "",
                Notification.TYPE_HUMANIZED_MESSAGE);
    }

    private void showExceptionMessage(Exception e) {

            this.getWindow().showNotification(
                    "The operation failed due some errors. See for detailed information to the catalina log. ",
                    e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE);
    };




    /**
     * Silently closes the given {@link Closeable} implementation, ignoring any errors that come out of the {@link Closeable#close()} method.
     *
     * @param closable the closeable to close, can be <code>null</code>.
     */
    private void silentlyClose(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            }
            catch (IOException e) {
                // Best effort; nothing we can (or want) do about this...
            }
        }
    }

    public class FileUpload extends Upload
            implements	Upload.SucceededListener,
                        Upload.FailedListener,
                        Upload.Receiver
    {

        // associate the reference for this upload
        public File file;

        // the caption for the error & success messages;
        private String capt;

        public FileUpload(String initCapt, String c, File f) {
            super();
            file = f;
            capt = c;
            this.setCaption(initCapt);
            this.setReceiver(this);
            this.addListener((Upload.SucceededListener) this);
            this.addListener((Upload.FailedListener) this);

        }

        public OutputStream receiveUpload(String filename, String MIMEType) {
            FileOutputStream fos = null; // Output stream to write to
            file = new File(state.getUploadDir() + filename);
            try {
                // Open the file for writing.
                fos = new FileOutputStream(file);
            } catch (java.io.FileNotFoundException e) {
                System.err.println(e.getMessage());

                this.getWindow().showNotification(
                        "Access to the file failed.",
                        e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE);
            }

            return fos; // Return the output stream to write to
        }

        // This is called if the upload fails.
        public void uploadFailed(Upload.FailedEvent event) {

            this.getWindow().showNotification(
                    "The upload failed due some errors. See for detailed information to the catalina log. ",
                    "Uploading " + event.getFilename() + " failed.",
                    Notification.TYPE_ERROR_MESSAGE);
        }

        // This is called if the upload is finished.
        public void uploadSucceeded(Upload.SucceededEvent event) {
            this.setCaption(capt + file.getName() + " upload succeeded.");
            this.setComponentError(null);
        }


    }
};

