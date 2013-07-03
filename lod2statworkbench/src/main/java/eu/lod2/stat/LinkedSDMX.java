package eu.lod2.stat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;

import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import eu.lod2.ExportSelector;
import eu.lod2.LOD2DemoState;
import eu.lod2.LOD2Exception;
import eu.lod2.slimvaliant.XsltTransformer;

public class LinkedSDMX extends CustomComponent implements Button.ClickListener {
	
	private VerticalLayout panel;
	
	private File  xmlFile;     // The SDMX-ML file to be transformed
    private File  rdfFile;     // The RDF file containing the triples derived via the XSLT
    private File  configFile; // The RDF configuration file 

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


    private FileUpload uploadXMLFile = null;
    private FileUpload uploadConfigFile = null;
    
    public LinkedSDMX() {}
    
    public LinkedSDMX(LOD2DemoState state){
    	this();
        this.state = state;

        panel = new VerticalLayout();
        panel.setSpacing(true);

        Label desc = new Label(
                "This page aids the creation of RDF Data Cubes out of SDMX-ML.<br/>" + 
                "This is done by using XSLT transformation which transforms the XML document into a set of RDF triples conforming to RDF Data Cube vocabulary.<br/>" +
                "This page is entirely based on the effort done in <a href=\"http://csarven.ca/linked-sdmx-data\">http://csarven.ca/linked-sdmx-data</a>. Therefore, additional information and instructions on using and configuring the tool can be found at <a href=\"https://github.com/csarven/linked-sdmx\"/>https://github.com/csarven/linked-sdmx</a>. <br/>" +		
                "The resulting triples are uploaded in the share RDF store.",
                Label.CONTENT_XHTML
                );
        
        uploadXMLFile = new FileUpload("Upload the XML file here", "The XML file ", xmlFile);
        uploadXMLFile.setDebugId(this.getClass().getSimpleName()+"_uploadXMLFile");
        uploadConfigFile = new FileUpload("Upload the RDF/XML Config file here", "The RDF/XML Config file ", configFile);
        uploadConfigFile.setDebugId(this.getClass().getSimpleName()+"_uploadCatalogFile");

        errorMsg = new Label("");

        exportGraph = new ExportSelector(state, true);
        exportGraph.setDebugId(this.getClass().getSimpleName()+"_exportGraph");
        uploadButton = new Button("Upload result to RDF Store", (Button.ClickListener) this);
        uploadButton.setDebugId(this.getClass().getSimpleName()+"_uploadButton");

        transformButton = new Button("transform SDMX-ML to RDF Data Cube", (Button.ClickListener) this);
        transformButton.setDebugId(this.getClass().getSimpleName()+"_transformButton");
        
        panel.addComponent(desc);
        panel.addComponent(uploadXMLFile);
        panel.addComponent(uploadConfigFile);
        panel.addComponent(exportGraph);
        panel.addComponent(uploadButton);
        panel.addComponent(transformButton);
        panel.addComponent(errorMsg);

        errorMsg.setVisible(false);
        
        t2f = new Form();
        t2f.setDebugId(this.getClass().getSimpleName()+"_t2f");
        t2f.setCaption("");

        annotatedTextField = new Label("Extracted RDF", Label.CONTENT_XHTML);
        t2f.getLayout().addComponent(annotatedTextField);

        textToAnnotateField = new TextArea();
        textToAnnotateField.setDebugId(this.getClass().getSimpleName()+"_textToAnnotateField");
        textToAnnotateField.setImmediate(false);
        textToAnnotateField.setColumns(100);
        textToAnnotateField.setRows(25);
        
        t2f.getLayout().addComponent(textToAnnotateField);

        panel.addComponent(t2f);
        t2f.setVisible(false);
        
        downloadForm = new Form();
        downloadForm.setDebugId(this.getClass().getSimpleName()+"_downloadForm");
        downloadForm.setCaption("Download file.");
        dlFileName = new TextField();
        dlFileName.setDebugId(this.getClass().getSimpleName()+"_dlFileName");
        dlFileName.setRequired(true);
        //dlFileName.setCaption("Give a filename. (Required)");
        downloadForm.getLayout().addComponent(new Label("Give a filename."));
        downloadForm.getLayout().addComponent(dlFileName);
        dlPath = new TextField();
        dlPath.setDebugId(this.getClass().getSimpleName()+"_dlPath");
        dlPath.setRequired(true);
        //dlPath.setCaption("Specify a path. (Required for downloading)");
        downloadForm.getLayout().addComponent(new Label("Specify a path."));
        downloadForm.getLayout().addComponent(dlPath);
        downloadButton = new Button("Download file", (Button.ClickListener) this);
        downloadButton.setDebugId(this.getClass().getSimpleName()+"_downloadButton");
        downloadForm.getLayout().addComponent(downloadButton);

        panel.addComponent(downloadForm);
        downloadForm.setVisible(false);

        // The composition root MUST be set
        setCompositionRoot(panel);
    }
    
    // propagate the information of one tab to another.
    public void setDefaults() {};

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
        if (configFile == null) {
            uploadConfigFile.setComponentError(new UserError("Upload a config file."));
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
        configFile = uploadConfigFile.file;

        if (!validTransformInput()) {
            // break here
            return;
        }

        try {
            xmlStream = new FileInputStream(xmlFile); 
            // TODO create xslt stream
            xsltStream = new FileInputStream("TODO");
            XsltTransformer xsltTransformer = new XsltTransformer(new StreamSource(xsltStream));
            xsltTransformer.transform(xmlStream, sResult);
            if(oStream.toString().isEmpty()){
                textToAnnotateField.setValue("Transformation resulted in no triples; please check if you entered a valid xml and rdf configuration.");
            } else {
                textToAnnotateField.setValue(oStream.toString());
                t2f.setVisible(true);
                downloadForm.setVisible(true);
            };
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
	
	private class FileUpload extends Upload
    implements	Upload.SucceededListener,
                Upload.FailedListener,
                Upload.Receiver {
		
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
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
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
	
}
