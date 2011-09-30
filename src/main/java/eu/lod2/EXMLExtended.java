
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


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
import com.vaadin.terminal.FileResource;

/*import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.annotation.PostConstruct;

import com.sun.org.apache.xerces.internal.util.XMLCatalogResolver;*/
//import org.apache.log4j.Logger;

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

import eu.lod2.slimvaliant.*;

/*import be.tenforce.lod2.valiant.WkdTransformer;
import be.tenforce.lod2.valiant.Valiant;     */
/*import be.tenforce.lod2.valiant.virtuoso.VirtuosoFactory;
import be.tenforce.lod2.valiant.webdav.DavReader;
import be.tenforce.lod2.valiant.webdav.DavWriter;

import com.googlecode.sardine.DavResource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;*/

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * extract RDF data from an XML file using an XSLT transformation
 */
//@SuppressWarnings("serial")
//@Configurable(preConstruction = true)
//@Component
public class EXMLExtended extends CustomComponent
    implements	Upload.SucceededListener,
                Upload.FailedListener,
                Upload.Receiver,
		Button.ClickListener
{
 //private static final Logger log = Logger.getLogger(EXML.class);

    // reference to the global internal state
    private ExtractionTab extractionTab;

    private VerticalLayout panel;
    File  file;
    File  xmlFile;         // The original file
    File  rdfFile;         // The RDF file containing the triples derived via the XSLT
    File  xsltFile;	   // The XSLT transformation file

    //
    //private Button annotateButton;
    private Label annotatedTextField;
    private Label errorMsg;

    private LOD2DemoState state;

    private String textToAnnotate;
    private String annotatedText;

    private TextArea textToAnnotateField;
    private TextField dlPath;
    private TextField dlFileName;

    private Button transformButton;
    private Button downloadButton;
    private Button uploadButton;

    private Form downloadForm, t2f;

    private ExportSelector exportGraph;

    private ByteArrayOutputStream oStream;

    final Upload uploadXMLFile =
                new Upload("Or upload the XML file here", this);

    final Upload uploadXSLTFile =
                new Upload("Or upload the XLST file here", this);
    public EXMLExtended(){


}
    public EXMLExtended(ExtractionTab etab, LOD2DemoState state) {
	this();
	this.state = state;
	
        // The internal state and
        extractionTab = etab;

        panel = new VerticalLayout();

	transformButton = new Button("transform XML to RDF", (Button.ClickListener) this);



        // Create the Upload component for the XML file.
        
	uploadXMLFile.setButtonCaption("Upload Now");
        uploadXMLFile.addListener((Upload.SucceededListener) this);
        uploadXMLFile.addListener((Upload.FailedListener) this);

        panel.addComponent(uploadXMLFile);

	// Create the Upload component for the XSLT file.
        

        uploadXSLTFile.setButtonCaption("Upload Now");
        uploadXSLTFile.addListener((Upload.SucceededListener) this);
        uploadXSLTFile.addListener((Upload.FailedListener) this);

	errorMsg = new Label("");

        panel.addComponent(uploadXSLTFile);
       	panel.addComponent(transformButton);
	panel.addComponent(errorMsg);
	
	errorMsg.setVisible(false);


        t2f = new Form();
        t2f.setCaption("");

        annotatedTextField = new Label("Extracted RDF", Label.CONTENT_XHTML);
	t2f.getLayout().addComponent(annotatedTextField);

	textToAnnotateField = new TextArea();
        textToAnnotateField.setImmediate(false);
        //textToAnnotateField.addListener(this);
        textToAnnotateField.setColumns(100);
        textToAnnotateField.setRows(25);
        t2f.getLayout().addComponent(textToAnnotateField);

        panel.addComponent(t2f);
	t2f.setVisible(false);

	downloadForm = new Form();
	downloadForm.setCaption("Download file, or upload it to Virtuoso.");
	dlFileName = new TextField();
	//dlFileName.setCaption("Give a filename. (Required)");
	downloadForm.getLayout().addComponent(new Label("Give a filename. (Required)"));
	downloadForm.getLayout().addComponent(dlFileName);
	dlPath = new TextField();
	//dlPath.setCaption("Specify a path. (Required for downloading)");
	downloadForm.getLayout().addComponent(new Label("Specify a path. (Only required for downloading)"));
	downloadForm.getLayout().addComponent(dlPath);
	downloadButton = new Button("Download file", (Button.ClickListener) this);
	downloadForm.getLayout().addComponent(downloadButton);
	uploadButton = new Button("Upload to Virtuoso", (Button.ClickListener) this);
	downloadForm.getLayout().addComponent(uploadButton);
	exportGraph = new ExportSelector(state);
	downloadForm.getLayout().addComponent(exportGraph);
	
	panel.addComponent(downloadForm);
	downloadForm.setVisible(false);

        // The composition root MUST be set
        setCompositionRoot(panel);

}

	// propagate the information of one tab to another.
	public void setDefaults() {
	};

	// Callback method to begin receiving the upload.
    public OutputStream receiveUpload(String filename,
                                      String MIMEType) {
        FileOutputStream fos = null; // Output stream to write to
        file = new File("/tmp/uploads/" + filename);
        try {
            // Open the file for writing.
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            // Error while opening the file. Not reported here.
            e.printStackTrace();
            return null;
        }
	
        return fos; // Return the output stream to write to
    }

    // This is called if the upload is finished.
    public void uploadSucceeded(Upload.SucceededEvent event) {
        // Log the upload on screen.
       /* panel.addComponent(new Label("File " + event.getFilename()
                + " of type '" + event.getMIMEType()
                + "' uploaded."));*/
		if(event.getComponent()== uploadXMLFile){
			xmlFile = file;
			//xmlText.setVisible(false);
			uploadXMLFile.setCaption("xml file upload succeeded.");
		}
		else if(event.getComponent()== uploadXSLTFile){
			xsltFile = file;
			//xsltText.setVisible(false);
			uploadXSLTFile.setCaption("xslt file upload succeeded.");
		}
    }

    // This is called if the upload fails.
    public void uploadFailed(Upload.FailedEvent event) {
        // Log the failure on screen.
        panel.addComponent(new Label("Uploading "
                + event.getFilename() + " of type '"
                + event.getMIMEType() + "' failed."));
    }
    public void buttonClick(ClickEvent event) {
	if(event.getComponent()==transformButton){
		transform();
	}
	else if(event.getComponent()==downloadButton){
		download();
	}
	else if(event.getComponent()==uploadButton){
		uploadToVirtuoso();
	}
    }


    private void transform(){
		errorMsg.setVisible(false);
		InputStream xmlStream, xsltStream;
		oStream = new ByteArrayOutputStream();
		StreamResult sResult = new StreamResult(oStream);

		System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
            	System.setProperty("javax.xml.parsers.DocumentBuilderFactory" , "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
            	System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");

		if(xmlFile == null){
			errorMsg.setVisible(true);
			errorMsg.setValue("Upload a xml file first.");
			return;
		}
		else if(xsltFile == null){
			errorMsg.setVisible(true);
			errorMsg.setValue("Upload a xslt file.");
			return;
		}
		try{
			xmlStream = new FileInputStream(xmlFile);
			xsltStream = new FileInputStream(xsltFile);
            		WkdTransformer wkdTransformer = new WkdTransformer(new StreamSource(xsltStream)); 
	    		wkdTransformer.transform(xmlStream, sResult);
        	} catch (Exception e){
            		e.printStackTrace();
		}
		if(oStream.toString().isEmpty()){
			textToAnnotateField.setValue("Transformation failed, please check if you entered a valid xml and xslt code.");
			return;
		}
        	textToAnnotateField.setValue(oStream.toString());
		t2f.setVisible(true);
		downloadForm.setVisible(true);
	}

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
    private void uploadToVirtuoso(){
	try{
	rdfFile = new File ("/tmp/uploads/file.rdf");
	FileOutputStream fos = new FileOutputStream(rdfFile);
	oStream.writeTo(fos);
	String baseURI = "http://poolparty.biz/defaultns#";

	RepositoryConnection con = state.getRdfStore().getConnection();
	Resource contextURI = con.getValueFactory().createURI(exportGraph.getExportGraph());
        Resource[] contexts = new Resource[] {contextURI};
	con.add(rdfFile, baseURI, RDFFormat.RDFXML, contexts);
	} catch (Exception e){
		e.printStackTrace();
		panel.addComponent(new Label("Upload failed"));
		return;
	}
	panel.addComponent(new Label("Upload succeeded!"));
    }
};


