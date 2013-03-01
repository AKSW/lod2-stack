package eu.lod2;

import com.vaadin.ui.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openrdf.model.Resource;
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
    public final static String storageLocation="/storage/f/";

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
        this.publisher.CKANuser=this.CKANInfo.get("CKAN username");
        this.publisher.CKANpwd=this.CKANInfo.get("password");

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
            }else if(this.packageDetails){
                try{
                    this.publisher.updateDataset(datasetIdentifiers.get("id"),this.PackageInfo);
                }catch (Exception e){
                    getWindow().showNotification("Could not update package",
                            "An error occurred while updating the package with name: "+name+".\n" +
                                    "The error message was: "+e.getMessage(),
                            Window.Notification.TYPE_ERROR_MESSAGE,true);
                }
            }
        }catch (Exception e){
            getWindow().showNotification("Could not retrieve package",
                    "An error occurred while searching for a package with the name "+name+".\n" +
                            "The error message was: "+e.getMessage(),
                    Window.Notification.TYPE_ERROR_MESSAGE,true);
        }
        if(name==null){
            getWindow().showNotification("Could not retrieve package",
                    "We could not retrieve a package with the name "+name+" in the given ckan store...",
                    Window.Notification.TYPE_HUMANIZED_MESSAGE,true);
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

        Label message = new Label("No package with this name was found on the CKAN server. Do you wish to create it?");
        notifier.addComponent(message);
        final CKANPublisherPanel panel=this;

        Button yes = new Button("Yes", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                try {
                    panel.fullDatasetIdentifiers=publisher.createDataset(packageProperties);
                } catch (Exception e) {
                    getWindow().showNotification("Package creation failed", "Could not create the package. " +
                            "Received error with message: "+e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE,true);
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

            Map<String, String> values=this.readFieldsMap(resourceFields);
            String location=publishDataset(this.publisher,values.get("filename"));
            values.remove("filename");
            createResourceLink(location, this.fullDatasetIdentifiers.get("id"), values);


            this.createSuccessMessage("Congratulations, your dataset is available under "+
                    this.publisher.CKANrepos+storageLocation + location + ".");
        } catch (Exception e) {
            getWindow().showNotification("Could not create resource","An unexpected error occurred while creating the resource. " +
                    "<br>The message was: <br><br>"+e.getMessage(),
                    Window.Notification.TYPE_ERROR_MESSAGE,true);
        }
    }

    /**
     * Shows the given message in a panel with title "upload successful".
     * Also gives the user the option of uploading a new file
     * @param message : the success messsage to show
     */
    public void createSuccessMessage(String message){
        this.removeAllComponents();

        Panel panel=new Panel("Upload successful");
        VerticalLayout layout=(VerticalLayout) panel.getContent();

        layout.addComponent(new Label(message));

        Button button=new Button("Upload another file");
        final CKANPublisherPanel publisherPanel=this;
        button.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                publisherPanel.fullDatasetIdentifiers=null;
                publisherPanel.render();
            }
        });
        layout.addComponent(button);

        this.addComponent(panel);
    }

    /**
     * Creates a resource that is linked to the given pacakgeId
     * @param location : the url of the resource to link
     * @param packageId : the id of the package to link the resource to
     * @param resourceProperties : additional properties of the resource
     */
    private void createResourceLink(String location, String packageId, Map<String, String> resourceProperties) throws IOException {
        this.publisher.linkResourceBackup(this.publisher.CKANrepos + storageLocation + location, packageId, resourceProperties);
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
            String value=(String)field.getValue();
            if(value != null && !value.trim().isEmpty()){
                result.put(prop, value.trim());
            }
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
                "CKAN repository", "CKAN api key", "CKAN username", "Password"};
        LinkedHashMap<String, AbstractTextField> fields=new LinkedHashMap<String, AbstractTextField>();
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

    private boolean packageDetails =false;
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

        final VerticalLayout details= new VerticalLayout();
        String descriptionTag="notes";
        TextArea description=new TextArea(descriptionTag);
        fields.put(descriptionTag,description);
        details.addComponent(description);

        String[] properties= new String[] {"title", "url", "version"};
        // TODO in a perfect world, tags would be added as well
        for(String property : properties){
            TextField field=new TextField(property);
            details.addComponent(field);
            fields.put(property, field);
        }

        CheckBox cb = new CheckBox("Extra details?");
        cb.setDescription("Check to add extra details to the package.\n Empty fields will be ignored.");
        cb.setImmediate(true);
        cb.setValue(packageDetails);
        cb.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                packageDetails =!packageDetails;
                details.setVisible(packageDetails);
            }
        });
        layout.addComponent(cb);

        details.setVisible(packageDetails);
        layout.addComponent(details);

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
     * Creates the text fields that are required for the resource configuration
     */
    protected Map<String, AbstractTextField> createResourceFields(){
        Panel panel= new Panel("CKAN resource information");
        VerticalLayout layout = (VerticalLayout) panel.getContent();

        final Map<String,AbstractTextField> fields=new LinkedHashMap<String, AbstractTextField>();
        String[] properties= new String[] {"name", "filename"};
        for(String property : properties){
            TextField field=new TextField(property);
            field.setRequired(true);
            layout.addComponent(field);
            fields.put(property, field);
        }

        TextArea descriptionArea = new TextArea("description");
        layout.addComponent(descriptionArea);
        fields.put("description", descriptionArea);

        this.addComponent(panel);
        return fields;
    }

    /**
     * Attempts to publish the current dataset in the application state to the given ckan repository. Throws an
     * exception with a useful error message on fail.
     * @param publisher : the publisher to use when publising the resource
     * @param datasetName : the name of the dataset to publish
     *
     * @return : the filename that was created for the new file
     */
    protected String publishDataset(Publisher publisher, String datasetName)
            throws RepositoryException, RDFHandlerException, IOException {
        String fullFilename= null;

        String currentGraphName=this.state.getCurrentGraph();

        RepositoryConnection con = this.state.getRdfStore().getConnection();
        try{

            Resource currentGraph=con.getValueFactory().createURI(currentGraphName);
            File temp=File.createTempFile(datasetName,".rdf", new File("/tmp"));
            temp.deleteOnExit();

            FileWriter fw=new FileWriter(temp);
            RDFWriter w = Rio.createWriter(RDFFormat.RDFXML, fw);

            con.export(w,currentGraph);
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
        public String CKANuser="";
        public String CKANpwd="";
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
         * Updates the dataset with the given id with the given properties
         * @param id : the id of the dataset to update
         * @param packageInfo : the package information to add to the dataset
         */
        public void updateDataset(String id, Map<String, String> packageInfo) throws IOException {
            // current values must be requested and re-applied or they will be overwritten!!!
            Map<String,Object> currentInfo=this.getPackageInfo(id);

            for(String key : packageInfo.keySet()){
                String newValue=packageInfo.get(key);
                if(key!=null && !key.isEmpty()){
                    currentInfo.put(key,newValue);
                }
            }

            String jsonCall=new ObjectMapper().writeValueAsString(currentInfo);

            Map<String,Object> resultMapping=this.postToCKANApi(jsonCall,"/api/action/package_update");
            boolean success=(Boolean) resultMapping.get("success");
            if(!success){
                throw new IllegalStateException("<div>Could not create a new package, the server responded with </div>"+
                        this.printCKANErrorMessage(resultMapping));
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

            HttpClient httpclient = new DefaultHttpClient();

            try {

                FileBody bin = new FileBody(file,"application/rdf+xml");

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("file", bin);

                reqEntity.addPart("key", new StringBody(filename));


                HttpPost postRequest = new HttpPost(this.CKANrepos+"/storage/upload_handle");
                postRequest.setEntity(reqEntity);
                postRequest.setHeader(CKANapiHeader, this.CKANapi);

                Credentials credentials= new UsernamePasswordCredentials(this.CKANuser, this.CKANpwd);
                postRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

                HttpResponse response = httpclient.execute(postRequest);
                int statusCode = response.getStatusLine().getStatusCode();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader((response.getEntity().getContent())));

                String line;
                while ((line = br.readLine()) != null) {
                    body += line;
                }
                if(statusCode!=200){
                    getWindow().showNotification("Upload failed: "+statusCode,
                            "Could not upload the file to the server. The server responded with:<br>"+
                                    body+"<br>If you believe this message is not correct, or you do not how to respond " +
                                    "to it, please contact support.",
                            Window.Notification.TYPE_ERROR_MESSAGE,true);

                }else if(!body.toLowerCase().contains("<h1>Upload - Successful</h1>".toLowerCase())){
                    // TODO why don't they just send a json object??????
                    throw new IllegalStateException("The server sent an unexpected response. The server response was: "+
                            body);
                }
            }finally {
                httpclient.getConnectionManager().shutdown();
            }
        }

        /**
         * Posts the given json to the action api.
         *
         * @param json : the parameters in json format
         * @param method : the method of the api to call
         * @return the returned json object from the api
         */
        private HashMap<String,Object> postToCKANApi(String json, String method) throws UnsupportedEncodingException {
            if (json.isEmpty() || method.isEmpty())
                return null;
            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(this.CKANrepos+method);
            httpPost.addHeader(CKANapiHeader, this.CKANapi);

            Credentials credentials= new UsernamePasswordCredentials(this.CKANuser, this.CKANpwd);
            httpPost.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

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
                    String body = EntityUtils.toString(entity);
                    try {
//                        some debug information
//                        getWindow().showNotification("method "+method,
//                                "params:\n"+json+
//                                "\n\nreceived:\n"+body, Window.Notification.TYPE_ERROR_MESSAGE);
                        return (HashMap<String, Object>)new ObjectMapper().readValue(body, HashMap.class);
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
        public Map<String,String> createDataset(Map<String, String> packageInfo) throws IOException, IllegalStateException {
            String jsonCall="{";

            jsonCall+=this.buildJSONParams(packageInfo);
            // TODO implementation dependent! make the calls conform to the datasets that we support
            // these are apparently required (not in api, the store that we are using for testing is using a custom schema!!)
            jsonCall+=",\"published_by\":\"acp\",\"description\":\"test\",\"url\":\"http://test.test\",\"status\":\"http://ec.europa.eu/open-data/kos/dataset-status/Completed\"";

            jsonCall+="}";

            Map<String,Object> resultMapping=this.postToCKANApi(jsonCall,"/api/action/package_create");
            boolean success=(Boolean) resultMapping.get("success");
            if(!success){
                throw new IllegalStateException("<div>Could not create a new package, the server responded with </div>"+
                        this.printCKANErrorMessage(resultMapping));
            }
            Map<String,Object> result=(Map<String,Object>)resultMapping.get("result");
            Map<String,String> identification=new HashMap<String, String>();
            identification.put("id", (String) result.get("id"));
            identification.put("name", (String) result.get("name"));

            return identification;
        }

        /**
         * Creates a json string representation of the properties in the given map
         * @param parameters : the map to convert to json
         * @return a list of json properties, without the {} around them
         */
        private String buildJSONParams(Map<String, String> parameters) {
            String json="";
            int count=0;
            for(String key : parameters.keySet()){
                String value=parameters.get(key);
                if(value!=null && !value.isEmpty()){
                    json+=((count>0?",":"")+"\""+key+"\":\""+value+"\"");
                    count++;
                }
            }
            return json;
        }

        /**
         * Links the given url as a resource to the given packageId. The values in the map of properties are also set.
         * @param resourceUrl : the url of the resource to link
         * @param packageId : the package to link the resource to
         * @param properties : the properties to add to the resource
         * TODO note: the resource_create function has been not been <s>fully</s> implemented in the ckan repository that we are using
         *            (see the create.py file in ckan/logic/action and
         *                   http://lists.okfn.org/pipermail/ckan-dev/2012-April/002013.html)
         * @deprecated use eu.lod2.CKANPublisherPanel.Publisher#linkResourceBackup(java.lang.String, java.lang.String, java.util.Map<java.lang.String,java.lang.String>)
         */
        @Deprecated
        public void linkResource(String resourceUrl, String packageId, Map<String,String> properties) throws IOException {
            String jsonCall="{\"package_id\":\""+packageId+"\"," +
                    "\"url\":\""+resourceUrl+"\",";

            jsonCall+=this.buildJSONParams(properties);

            jsonCall+="}";

            Map<String,Object> resultMapping=this.postToCKANApi(jsonCall,"/api/action/resource_create");
            boolean success=(Boolean) resultMapping.get("success");
            if(!success){
                throw new IllegalStateException("<div>Could not create a new package, the server responded with </div>"+
                        this.printCKANErrorMessage(resultMapping));
            }
        }

        /**
         * publishes the given resource url to the given package using a workaround. The workaround is to first get all
         * information on the package. We then isolate the resources information and update the package resources with
         * that list plus a newly created resource.
         * @param resourceUrl : the url of the resource to publish
         * @param packageId : the id of the package to publish to
         * @param properties : additional properties of the resource
         *
         * TODO note: this method is a workaround. It is necessary because the ODP store does not support resource_create
         */
        public void linkResourceBackup(String resourceUrl, String packageId, Map<String,String> properties) throws IOException {
            // first get the current information on the package (accessing json in java is a bit convoluted
            Map<String,Object> currentInfo=this.getPackageInfo(packageId);
            List<Map<String,Object>> resources= (List<Map<String, Object>>) currentInfo.get("resources");

            if(resources==null){
                resources=new ArrayList<Map<String, Object>>();
                currentInfo.put("resources", resources);
            }
            // build a new resource json object
            String newResourceJson="{\"url\":\""+resourceUrl+"\","+this.buildJSONParams(properties)+"}";
            Map<String,Object> newResource=new ObjectMapper().readValue(newResourceJson,HashMap.class);
            resources.add(newResource);

            //upload the new statistics on the dataset
            // TODO does the server handle multiple writes correctly? Otherwise silent information loss is possible.
            String updateCall=new ObjectMapper().writeValueAsString(currentInfo);
            Map<String, Object> result=this.postToCKANApi(updateCall,"/api/action/package_update");
            if(!(Boolean)result.get("success")){
                throw new IllegalStateException("<div>Could not create a new resource, the server responded with </div>"+
                        this.printCKANErrorMessage(result));
            }
        }

        /**
         * Looks up the current information on the given package. Throws an exception with a meaningful message if the
         * lookup process fails.
         * @param packageId ; the id of the package to search for
         * @return : a json object map with the current information on the package
         */
        private Map<String, Object> getPackageInfo(String packageId) throws IOException {
            String jsonCall="{\"q\":\"id:"+packageId+"\"}";

            Map<String,Object> response=(Map<String,Object>)this.postToCKANApi(jsonCall,"/api/action/package_search");
            Map<String,Object> currentInfo = null;
            try{
                currentInfo=((List<Map<String,Object>>)
                        ((Map<String,Object>) response.get("result"))
                                .get("results")).get(0);
            }catch(NullPointerException e){
                //just for clarity
                throw new IllegalStateException("The server responded with an object of an unexpected format. " +
                        "The object was: \n"+new ObjectMapper().writeValueAsString(response));
            }

            // all keys must be retained or their information is lost.
            // some values must be transformed, as requested by the ODP CKAN repository's implementation
            // TODO note: CKAN implementation specific!! have to remove redundant " at start and end of string
            // requesting all language uris from the server's developers and reversing the mapping)
            String languageTag=(String)currentInfo.get("metadata_language");
            if(languageTag != null && languageTag.startsWith("\"") && languageTag.endsWith("\"")){
                languageTag=languageTag.substring(1,languageTag.length()-1);
                currentInfo.put("metadata_language",languageTag);
            }
            return currentInfo;
        }

        /**
         * Prints the error message in the response in a form that is readable by non-technical humans
         * @param response : the response object sent by the ckan server. If this response is not an error message,
         *                 the result will be a simple string visualization of the json object
         * @throws IOException : writing the response's json to a string did not succeed.
         * @return a non-technical version of the error message sent by the server in html format.
         */
        private String printCKANErrorMessage(Map<String,Object> response) throws IOException {
            // cloning so we do not destroy the original result
            Map<String,Object> error=new HashMap<String, Object>((Map<String,Object>)response.get("error"));
            ObjectMapper mapper=new ObjectMapper();
            if(error!=null){
                String message=(String) error.get("message");
                String type=(String) error.get("__type");
                error.remove("message");
                error.remove("__type");

                String details=null;

                if(type.equalsIgnoreCase("validation error")){
                    if(message==null){
                        message="The CKAN server could not understand the format of the request. This typically means " +
                                "that the CKAN server that you are trying to contact is custom made and that it is " +
                                "currently not supported by the LOD2 stack. If you believe that this is the case and " +
                                "you feel the CKAN server deserves to be supported, please contact LOD2 support.";
                    }

                    details= this.jsonToHtml(error);

                }

                String result="<h1>Received an error message of type: "+type+"</h1>";
                if(message==null){
                    message="No message was attached to the error";
                }
                result+="<p>"+message+"</p>";

                if(details!=null && !details.isEmpty()){
                    result+="<div>The server provided further details to the message:</div>"+details;
                }
                return result;
            }else{
                return mapper.writeValueAsString(response);
            }
        }

        /**
         * @see #jsonToHtml(java.util.Map)
         */
        private String jsonToHtml(Object base){
            if(base==null){
                return "";
            }
            try{
                // base case
                return new ObjectMapper().writeValueAsString(base);
            }catch (IOException e){
                return "unreadable object!";
            }
        }

        /**
         * @see #jsonToHtml(java.util.Map)
         */
        private String jsonToHtml(List<Object> list){
            String result="";
            for(Object value: list){
                if(value == null){
                    result+=jsonToHtml(value);
                }else{
                    if(value instanceof Map){
                        result+=jsonToHtml((Map<String,Object>)value);
                    }else if(value instanceof List){
                        result+=jsonToHtml((List<Object>)value);
                    }else{
                        result+=jsonToHtml(value);
                    }
                }
            }
            return result;
        }

        /**
         * Prints the contents of the given json object as html
         * Note: method overloading is done on the static (compile-time) type of the arguments to the method, hence the
         * ugly instanceof and cast.
         * @param map : the json object to print
         * @return a html representation of the json map
         */
        private String jsonToHtml(Map<String,Object> map){
            String result="<table>";
            for(String property : map.keySet()){
                result+="<tr><td>"+property+"</td>";
                Object value=map.get(property);
                if(value == null){
                    result+="<td>"+jsonToHtml(value)+"</td></tr>";
                }else{
                    if(value instanceof Map){
                        result+="<td>"+jsonToHtml((Map<String,Object>)value)+"</td></tr>";
                    }else if(value instanceof List){
                        result+="<td>"+jsonToHtml((List<Object>)value)+"</td></tr>";
                    }else{
                        result+="<td>"+jsonToHtml(value)+"</td></tr>";
                    }
                }

            }
            return result+"</table>";
        }
    }
}
