package eu.lod2;

import com.google.gwt.json.client.JSONParser;
import com.vaadin.ui.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
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
public class CKANPublisherPanel extends Panel {
    protected LOD2DemoState state;
    private Map<String, String> CKANInfo;
    private Map<String, String> PackageInfo;

    private Publisher publisher=null;
    //* the full name and id of the dataset that was retrieved by the user.
    private Map<String,String> fullDatasetIdentifiers=null;

    public CKANPublisherPanel(LOD2DemoState state){
        super();
        this.setSizeFull();
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
        }else if(this.CKANInfo==null || this.PackageInfo==null || this.fullDatasetIdentifiers==null){
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
            Button back = new Button("Back", new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {
                    fullDatasetIdentifiers=null;
                    render();
                }
            });
            HorizontalLayout buttonsHolder=new HorizontalLayout();
            this.addComponent(buttonsHolder);
            buttonsHolder.addComponent(back);
            buttonsHolder.addComponent(saver);
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

        this.publisher=new Publisher();
        this.publisher.CKANapi=this.CKANInfo.get("CKAN api key");
        this.publisher.CKANrepos=this.CKANInfo.get("CKAN repository");

        if(this.publisher.CKANrepos.endsWith("/")){
            // remove trailing slash
            this.publisher.CKANrepos=this.publisher.CKANrepos.substring(0,this.publisher.CKANrepos.length()-1);
        }

        String name= this.PackageInfo.get("name");
        try{
            Map<String,String> datasetIdentifiers=this.publisher.lookForDatasetByName(name);
            this.fullDatasetIdentifiers=datasetIdentifiers;
            if(datasetIdentifiers==null){
                this.requestCreatePermission(this.PackageInfo,this.publisher);
                return;
            }
        }catch (Exception e){
            getWindow().showNotification("Could not retrieve package",
                    "An error occured while searching for a package with the name "+name+".\n" +
                            "The error message was: "+e.getMessage(),
                    Window.Notification.TYPE_ERROR_MESSAGE);
        }
        if(name==null){
            getWindow().showNotification("Could not retrieve package",
                    "We could not retrieve a package with the name "+name+" in the given ckan store...",
                    Window.Notification.TYPE_HUMANIZED_MESSAGE);
            this.PackageInfo=null;
        }

        this.render();
    }

    /**
     * Asks the user whether he wants to create a new package if the package he wants does not exist yet
     */
    private void requestCreatePermission(final Map<String, String> packageProperties, final Publisher publisher) {

        final Window notifier= new Window("Create new package");
        notifier.setModal(true);

        VerticalLayout layout = (VerticalLayout) notifier.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

        Label message = new Label("No package was found with this name on the CKAN server. Do you wish to create it?");
        notifier.addComponent(message);
        final CKANPublisherPanel panel=this;

        Button yes = new Button("Yes", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                try {
                    panel.fullDatasetIdentifiers=publisher.createDataset(packageProperties);
                } catch (Exception e) {
                    getWindow().showNotification("Package creation failed", "Could not create the package. " +
                            "Received error with message: "+e.getMessage());
                }
                notifier.getParent().removeWindow(notifier);
                panel.render();
            }
        });
        Button no = new Button("No", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                notifier.getParent().removeWindow(notifier);
                panel.render();
            }
        });

        HorizontalLayout buttons=new HorizontalLayout();
        buttons.addComponent(yes);
        buttons.addComponent(no);
        notifier.addComponent(buttons);

        getWindow().addWindow(notifier);
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
            result.put(prop,((String)field.getValue()).trim());
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
            if(this.CKANInfo!=null){
                field.setValue(this.CKANInfo.get(property));
            }
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
        layout.addComponent(nameField);

        String descriptionTag="notes";
        TextArea description=new TextArea(descriptionTag);
        fields.put(descriptionTag,description);
        layout.addComponent(description);

        String[] properties= new String[] {"title", "url", "version"};
        // TODO in a perfect world, tags would be added as well
        for(String property : properties){
            TextField field=new TextField(property);
            layout.addComponent(field);
            fields.put(property, field);
        }
        this.addComponent(panel);

        // add previously entered values if any
        if(this.PackageInfo!=null){
            for(String property : fields.keySet()){
                fields.get(property).setValue(this.PackageInfo.get(property));
            }
        }
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
        public String CKANapiHeader="X-CKAN-API-Key";


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
                getRequest.setHeader(CKANapiHeader, this.CKANapi);

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
         * Looks for a dataset with the given name in the repository that has currently been selected by the user
         * @return a map holding name and id as keys or null if no object was found
         */
        public Map<String,String> lookForDatasetByName(String datasetName) throws IOException {
            String jsonCall="{\"q\":\"name:"+datasetName+"\", \"fl\":\"name,id\"}";

            Map<String,Object> resultMapping=this.postToCKANApi(jsonCall,"/api/search/dataset");
            List<Map<String,Object>> results=(List<Map<String,Object>>) resultMapping.get("results");
            Map<String,String> bestMatch=new HashMap<String, String>();

            int resultCount=results.size();
            if(resultCount<1){
                return null;
            }else{
                String shortest=null;
                String id=null;


                for(Map<String,Object> result : results){
                    String name=(String)result.get("name");
                    if(name !=null && (shortest==null || shortest.length() > name.length())){
                        shortest=name;
                        id=(String)result.get("id");
                    }
                }
                // more than one result found. Take shortest (most complete match) but report to the user
                if(resultCount>1){
                    getWindow().showNotification("More than one match for the dataset name",
                            "There were "+resultCount+" results in this CKAN repository for the name that you entered." +
                                    "We picked the shortest one for you, as it is the closest match to your query. The " +
                                    "full name of the repository that was retrieved was: <b>"+shortest+"</b>.<br>If you " +
                                    "wanted to get another dataset, please add a more complete name to the dataset.",
                            Window.Notification.TYPE_HUMANIZED_MESSAGE,
                            true);
                }
                bestMatch.clear();
                bestMatch.put("name",shortest);
                bestMatch.put("id",id);

                return bestMatch;
            }
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
                postRequest.setHeader(CKANapiHeader, this.CKANapi);
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

        /**
         * Posts the given json to the action api.
         * @param json : the parameters in json format
         * @param method : the method of the api to call
         * @return the returned json object from the api
         */
        private Map<String, Object> postToCKANApi(String json, String method) throws UnsupportedEncodingException {
            if (json.isEmpty() || method.isEmpty())
                return null;
            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(this.CKANrepos+method);
            httpPost.addHeader(CKANapiHeader, this.CKANapi);

            //TODO ignoring authentication for now
            //httpPost.addHeader(BasicScheme.authenticate(credentials, ENCODING, false));

            StringEntity dataentity = new StringEntity(json);
            //NOTE: this contenttype is very important. If it is set to anything else (for example, true content
            //types), the request payload is ignored.
            dataentity.setContentType("application/x-www-form-urlencoded");
            //dataentity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));


            httpPost.setEntity(dataentity);

            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    long contentLength = entity.getContentLength();
                    Header contentEncoding = entity.getContentEncoding();
                    Header contentType = entity.getContentType();
                    String body = EntityUtils.toString(entity);
                    try {
                        getWindow().showNotification("method "+method,
                                "params:\n"+json+
                                "\n\nreceived:\n"+body, Window.Notification.TYPE_ERROR_MESSAGE);
                        return new ObjectMapper().readValue(body, HashMap.class);
                    } catch (Exception e) {
                        throw new IllegalStateException("An invalid response object was returned by the CKAN while " +
                                "performing the '"+method+" operation.'\n" +
                                "The response was: \n"+body);
                    }
                }
                EntityUtils.consume(entity);
            } catch (IOException e) {
                throw new RuntimeException("A problem occured while communitacting with the CKAN server. " +
                        "The '"+method+" operation raised an exception with the following message:'\n" +
                        e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }

        /**
         * Creates a new package on the ckan with the given info
         * @param packageInfo : the information on the package as entered by the user
         * @return a map holding identification information on the created package (holding keys id and name)
         */
        public Map<String,String> createDataset(Map<String, String> packageInfo) throws UnsupportedEncodingException {
            String jsonCall="{";
            int count=0;
            for(String key : packageInfo.keySet()){
                jsonCall+=((count>0?",":"")+"\""+key+"\":\""+packageInfo.get(key)+"\"");
                count++;
            }
            // these are apparently required (not in api)
            //jsonCall+=",\"published_by\":\"test\",\"description\":\"test\",\"url\":\"http://test.test\",\"status\":\"published\"";

            jsonCall+="}";

            Map<String,Object> resultMapping=this.postToCKANApi(jsonCall,"/api/action/package_create");
            boolean success=(Boolean) resultMapping.get("success");
            if(!success){
                throw new IllegalStateException("Could not create a new package, the server responded with "+
                        ((Map<String,Object>) resultMapping.get("error")).get("message"));
            }
            Map<String,Object> result=(Map<String,Object>)resultMapping.get("result");
            Map<String,String> identification=new HashMap<String, String>();
            identification.put("id", (String) result.get("id"));
            identification.put("name", (String) result.get("name"));

            return identification;
        }
    }
}
