package eu.lod2;

import com.vaadin.ui.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The CKANPublisherPanel class allows the user to publish their rdf store on a CKAN database.
 */
public class CKANPublisherPanel extends VerticalLayout {
    protected LOD2DemoState state;
    private Map<String, String> CKANInfo;
    private Map<String, String> PackageInfo;

    public CKANPublisherPanel(LOD2DemoState state){
        super();
        this.state=state;
    }

    @Override
    public void attach(){
        super.attach();
        this.render();
    }

    /**
     * (Re-)renders this component, first removing all components, then adding the components that are required given
     * the current state.
     */
    public void render(){
        this.removeAllComponents();

        String currentGraph=this.state.getCurrentGraph();
        if(currentGraph==null || currentGraph.isEmpty()){
            this.addComponent(new Label("Please select a graph first using the " +
                    "\"demonstrator configuration\" interface"));
        }else if(this.CKANInfo==null && this.PackageInfo==null){
            final Map<String, AbstractTextField> ckanFields=this.createCKANInfo();
            final Map<String, AbstractTextField> packageFields= this.createPackageFields();

            // allow the user to publish his dataset
            Button saver = new Button("Select Package", new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {
                    handlePublishFields(ckanFields,packageFields);
                }
            });
            this.addComponent(saver);
        }else{
            final Map<String, AbstractTextField> resourceFields=this.createResourceFields();

            // allow the user to publish his dataset
            Button saver = new Button("Publish", new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {
                    handleResourceFields(resourceFields);
                }
            });
            this.addComponent(saver);
        }
    }

    /**
     * Handles the fact that the user selects a ckan package
     * @param ckanFields : the general ckan information entered by the user
     * @param packageFields : the information on the ckan package entered by the user
     */
    protected void handlePublishFields(Map<String, AbstractTextField> ckanFields, Map<String, AbstractTextField> packageFields){
        this.CKANInfo = this.readFieldsMap(ckanFields);
        this.PackageInfo=this.readFieldsMap(packageFields);

        this.render();
    }

    protected void handleResourceFields(Map<String,AbstractTextField> resourceFields){
        try{
            //            TODO use correct information
            //            String location=publishDataset(values.get(properties.get(0)),values.get(properties.get(1)),
            //                    values.get(properties.get(2)));

            getWindow().showNotification("Upload Successful", "Your dataset is available under " + "some location here" + ".",
                    Window.Notification.TYPE_HUMANIZED_MESSAGE);
        } catch (Exception e) {
            getWindow().showNotification("Could not save information",e.getMessage(),
                    Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    /**
     * Reads the provided mapping from properties to AbstractTextFields and transforms it into a string to string map
     * This function will throw a meaningful Validator.InvalidValueException when a validator of one of the fields is violated.
     * @return a map holding the values for all propeties
     */
    private Map<String, String> readFieldsMap(Map<String, AbstractTextField> fieldMap){
        Map<String,String> result=new LinkedHashMap<String, String>();
        for(String prop : fieldMap.keySet()){
            AbstractTextField field=fieldMap.get(prop);
            field.validate();
            result.put(prop,(String)field.getValue());
        }
        return result;
    }

    /**
     * Creates the ckan information panels
     */
    protected Map<String, AbstractTextField> createCKANInfo(){
        // not using form here, will not couple domain object to the properties entered by the user
        Panel panel= new Panel("CKAN repository information");
        VerticalLayout layout = (VerticalLayout) panel.getContent();

        // create required field for each property
        String[] properties= new String[]{
                "CKAN repository", "CKAN api key"};
        Map fields=new LinkedHashMap<String, TextField>();
        for(String property : properties){
            TextField field=new TextField(property);
            field.setRequired(true);
            layout.addComponent(field);
            fields.put(property, field);
        }
        this.addComponent(panel);
        return fields;
    }

    /**
     * Creates the text fields that are required for the package configuration
     */
    protected Map<String, AbstractTextField> createPackageFields(){
        // not using form here, will not couple domain object to the properties entered by the user
        Panel panel= new Panel("CKAN package information");
        VerticalLayout layout = (VerticalLayout) panel.getContent();


        final Map<String,AbstractTextField> fields=new LinkedHashMap<String, AbstractTextField>();
        String nameTag="name";
        TextField nameField=new TextField(nameTag);
        nameField.setRequired(true);
        fields.put(nameTag, nameField);

        String descriptionTag="notes";
        TextArea description=new TextArea(descriptionTag);
        fields.put(descriptionTag,description);

        String[] properties= new String[] {"name", "url", "version"};
        // TODO in a perfect world, tags would be added as well
        for(String property : properties){
            TextField field=new TextField(property);
            layout.addComponent(field);
            fields.put(property, field);
        }
        this.addComponent(panel);
        return fields;
    }

    /**
     * Creates the text fielsd that are required for the resource configuration
     */
    protected Map<String, AbstractTextField> createResourceFields(){
        Panel panel= new Panel("CKAN resource information");
        VerticalLayout layout = (VerticalLayout) panel.getContent();

        final Map<String,AbstractTextField> fields=new LinkedHashMap<String, AbstractTextField>();
        String[] properties= new String[] {"url", "name", "filename"};
        for(String property : properties){
            TextField field=new TextField(property);
            field.setRequired(true);
            layout.addComponent(field);
            fields.put(property, field);
        }
        this.addComponent(panel);
        return fields;
    }

    /**
     * Attempts to publish the current dataset in the application state to the given ckan repository. Throws an
     * exception with a useful error message on fail.
     * @param ckan : the url of the ckan repository to use for publishing
     * @param ckanApi : the api key to use when publishing to this repository
     * @param datasetName : the name of the dataset to publish
     *
     * @return : the filename that was created for the new file
     */
    protected String publishDataset(String ckan, String ckanApi, String datasetName)
            throws RepositoryException, RDFHandlerException, IOException {
        String fullFilename= null;

        Publisher publisher=new Publisher();
        publisher.CKANrepos=ckan;
        publisher.CKANapi=ckanApi;
        this.state.getCurrentGraph();

        RepositoryConnection con = this.state.getRdfStore().getConnection();
        try{

            File temp=File.createTempFile(datasetName,".rdf", new File("/tmp"));
            temp.deleteOnExit();

            FileWriter fw=new FileWriter(temp);
            RDFWriter w = Rio.createWriter(RDFFormat.RDFXML, fw);
            con.export(w);
            fullFilename= publisher.requestFileLocationInCKAN(datasetName+".rdf");
            publisher.uploadFileToCkan(temp,fullFilename);
        }finally {
            con.close();
        }
        return fullFilename;
    }

    /**
     * This class is responsible for the actual publishing of an rdf store to the CKAN repository
     *
     * Much of the code has been based on the code that was written for the ODP project (RDF2CKAN)
     */
    public class Publisher {
        // the information necessary to execute operations on the CKAN store
        public String CKANrepos="";
        public String CKANapi="";


        /**
         * Uses the ckan filestore api to reserve a spot for the file on the server.
         * @param filename : name of file to upload (will not be full filename)
         * @return full filename on the server, has the form timestam/\<original filename\>
         *
         *     TODO this call has been reverse engineered. The api talks about (well mentions really) authentication
         *     this is ignored as it is not returned by the server or expected by the ckan that we have here
         *     maybe we should contact the ckan team and ask for some extra information
         */
        private String requestFileLocationInCKAN(String filename) throws IOException {
            String body = "";
            String generatedFilename=null;

            HttpClient httpclient = new DefaultHttpClient();

            try {

                // need to avoid caching issues by including date *as specified by ckan api*
                SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMMddHHmmss");
                dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
                String date=dateFormatGmt.format(new Date());
                generatedFilename=date +"/"+filename;

                HttpGet getRequest = new HttpGet(this.CKANrepos+ "/api/storage/auth/form/"+generatedFilename);
                getRequest.setHeader("X-CKAN-API-Key", this.CKANapi);

                HttpResponse response = httpclient.execute(getRequest);
                int statusCode = response.getStatusLine().getStatusCode();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader((response.getEntity().getContent())));

                String line;
                while ((line = br.readLine()) != null) {
                    body += line;
                }
                if(statusCode!=200){
                    throw new IllegalStateException("File reservation failed, server responded with code: "+statusCode+
                            "\n\nThe message was: "+body);

                }
            }finally {
                httpclient.getConnectionManager().shutdown();
            }
            return generatedFilename;
        }

        /**
         * Uploads the given file to ckan.
         * @param file : the file to upload
         * @param filename : the filename to use when uploading. Note that this is the full filename as returned by the
         *                 reservation call that was made to the api before
         */
        private void uploadFileToCkan(File file, String filename) throws IOException {
            String body = "";
            String generatedFilename=null;

            HttpClient httpclient = new DefaultHttpClient();

            try {

                FileBody bin = new FileBody(file,"application/rdf+xml");

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("file", bin);

                reqEntity.addPart("key", new StringBody(filename));


                HttpPost postRequest = new HttpPost(this.CKANrepos+"/storage/upload_handle");
                postRequest.setEntity(reqEntity);
                postRequest.setHeader("X-CKAN-API-Key", this.CKANapi);
                HttpResponse response = httpclient.execute(postRequest);
                int statusCode = response.getStatusLine().getStatusCode();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader((response.getEntity().getContent())));

                String line;
                while ((line = br.readLine()) != null) {
                    body += line;
                }
                if(statusCode!=200){
                    getWindow().showNotification("Upload Statuscode: "+statusCode,
                            body,
                            Window.Notification.TYPE_ERROR_MESSAGE);

                }
            }finally {
                httpclient.getConnectionManager().shutdown();
            }
        }

        public void createNewPackage(String name ){

        }
    }
}
