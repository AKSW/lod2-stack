package eu.lod2;

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

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openrdf.model.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.*;

//import org.restlet.engine.util.ListUtils;

/**
 * An about page on the LOD2 stack
 */
//@SuppressWarnings("serial")
public class DeleteGraphs extends CustomComponent
{

    // reference to the global internal state
    private LOD2DemoState state;
    private Table table;
    private VerticalLayout panel;
    // whether or not we arer loading data
    private ProgressIndicator indicator;

    public DeleteGraphs(LOD2DemoState st) {

        // The internal state 
        state = st;
        panel = new VerticalLayout();


        Label intro = new Label(
                "A tabular view to ease the removal of graphs. Note: data is fetched in parts to allow dealing with " +
                        "endpoints that have very many graphs. If the component is fetching data, a spinner is " +
                        "shown below.",
                Label.CONTENT_XHTML);

        panel.addComponent(intro);

        indicator=new ProgressIndicator();
        indicator.setIndeterminate(true);
        indicator.setPollingInterval(200);
        indicator.setEnabled(false);
        panel.addComponent(indicator);

        Button hideButton = new Button("Hide selected graphs", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                hidegraphs(event);
            }
        });

        Button markButton = new Button("Mark selected graphs", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                markgraphs(event);
            }
        });

        Button deleteButton = new Button("Delete marked graphs", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                deletegraphs(event);
            }
        });

        Button invertSelection = new Button("Invert current selection", new ClickListener(){
            public void buttonClick(ClickEvent clickEvent) {
                invertSelection();
            }
        });

        Button resetButton = new Button("Restore original view", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                resetTable();
            }
        });

        HorizontalLayout buttons=new HorizontalLayout();
        buttons.addComponent(markButton);
        buttons.addComponent(deleteButton);
        buttons.addComponent(hideButton);
        buttons.addComponent(invertSelection);
        buttons.addComponent(resetButton);
        buttons.setSpacing(true);
        buttons.setMargin(true);
        panel.addComponent(buttons);

        HorizontalLayout searcher=new HorizontalLayout();
        searcher.addComponent(new Label("Filter: "));
        final TextField filter=new TextField();
        filter.setWidth("400px");
        filter.setInputPrompt("Enter a filter");

        searcher.addComponent(filter);

        filter.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.LAZY);
        filter.setTextChangeTimeout(200);
        filter.setImmediate(true);
        filter.addListener(new FieldEvents.TextChangeListener() {
            public void textChange(FieldEvents.TextChangeEvent event) {
                String text=event.getText();
                filter.setValue(text);
            }
        });

        Button filterAction=new Button("Apply filter to table");
        Button filterFetchAction=new Button("Fetch matching graphs");

        filterAction.addListener(new ClickListener() {
            public void buttonClick(ClickEvent clickEvent) {
                selectByFilter((String)filter.getValue(),false);
            }
        });

        filterFetchAction.addListener(new ClickListener() {
            public void buttonClick(ClickEvent clickEvent) {
                fetchByFilter((String)filter.getValue());
            }
        });

        searcher.addComponent(filterFetchAction);
        searcher.addComponent(filterAction);

        searcher.setSpacing(true);
        searcher.setMargin(true);
        panel.addComponent(searcher);

        table = new Table("");
        table.setDebugId(this.getClass().getSimpleName()+"_table");
        table.setWidth("100%");
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true);
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);

        this.resetTable();

        panel.addComponent(table);


        // The composition root MUST be set
        setCompositionRoot(panel);
    }


    /**
     * Returns the datasource of the table with a correct type
     * @return the datasource of the table
     */
    private BeanContainer<String,DeleteGraphsTable> getTableDatasource(){
        return (BeanContainer<String, DeleteGraphsTable>) this.table.getContainerDataSource();
    }

    /**
     * Selects the rows in the current table that match the given filter. Note that this means that the hidden graphs
     * are *not* selected, even if they match the filter regex.
     * @param filter : a regular expression as a string
     * @param useRegex : whether or not to mach by regex
     */
    private synchronized void selectByFilter(String filter, boolean useRegex){
        this.clearSelection();
        BeanContainer<String,DeleteGraphsTable> all=this.getTableDatasource();

        for(String id:all.getItemIds()){
            BeanItem<DeleteGraphsTable> item=all.getItem(id);
            String name=item.getBean().getGraph();
            if(useRegex && name.matches(filter)){
                this.table.select(id);
            }else if(!useRegex && name.contains(filter)){
                this.table.select(id);
            }
        }
    }

    /**
     * Clears the current selection from the table.
     */
    private void clearSelection(){
        this.table.setValue(new HashSet<String>());
    }

    /**
     * Resets the table to its original state, without any hidden graphs.
     */
    private void resetTable(){
        BeanContainer<String,DeleteGraphsTable> container=this.createDeleteGraphsTable();
        this.table.setContainerDataSource(container);

        table.setVisibleColumns(new String[] {"mark","graph"});
        table.setColumnWidth("mark",55);
        table.setColumnWidth("hide", 55);
        table.setColumnExpandRatio("graph", 0.1f);

        table.setColumnAlignments(new String[] { Table.ALIGN_CENTER, Table.ALIGN_LEFT});
    }

    /**
     * Returns the BeanItems of the currently selected graphs. Note that this implies that the hidden graphs are not
     * returned.
     * @return the BeanItems of the currently selected graphs
     */
    private synchronized Map<String,BeanItem<DeleteGraphsTable>> getSelectedGraphs(){
        Set<String> ids=(Set<String>)this.table.getValue();
        Map<String,BeanItem<DeleteGraphsTable>> items=new LinkedHashMap<String, BeanItem<DeleteGraphsTable>>();
        BeanContainer<String,DeleteGraphsTable> container=this.getTableDatasource();
        for(String id:ids){
            items.put(id, container.getItem(id));
        }
        return items;
    }

    /**
     * Returns the ids of all the graphs that have been marked in the current table. This implies that the hidden graphs are ignored
     * @return the ids of the graphs that have currently been marked
     */
    public synchronized Set<String> getMarkedGraphs(){
        HashSet<String> marked=new HashSet<String>();

        BeanContainer<String,DeleteGraphsTable> all=this.getTableDatasource();
        for(String id:all.getItemIds()){
            BeanItem<DeleteGraphsTable> item=all.getItem(id);
            if((Boolean)item.getBean().getMark().getValue()){
                marked.add(id);
            }
        }

        return marked;
    }

    // the table data structure as a bean.
    public class DeleteGraphsTable implements Serializable {
        CheckBox mark;
        String graph;

        public String getGraph() {
            return graph;
        }

        public void setGraph(String graph) {
            this.graph = graph;
        }

        public CheckBox getMark() {
            return mark;
        }

        public void setMark(CheckBox mark) {
            this.mark = mark;
        }


        public DeleteGraphsTable(String g) {
            mark = new CheckBox();
            graph = g;
        };
    };

    //* creates an empty beancontainer that can hold the information on the graphs
    public BeanContainer<String, DeleteGraphsTable> createDeleteGraphsTable() {

        BeanContainer<String, DeleteGraphsTable> graphs = new BeanContainer<String, DeleteGraphsTable>(DeleteGraphsTable.class);
        graphs.setBeanIdProperty("graph");

        return graphs;
    };

    //* updates the table's datasource with the list of new graphs, synchronized to allow multiple worker threads
    private synchronized void addIntermediateResultToTable(List<String> newGraphs){
        BeanContainer<String, DeleteGraphsTable> graphs = (BeanContainer<String, DeleteGraphsTable>) table.getContainerDataSource();
        for(String graph : newGraphs){
            graphs.addBean(new DeleteGraphsTable(graph));
        }
    }

    public String getStringValue(Value v) {

        if (v == null) {
            return "";
        } else {
            return v.stringValue();
        }
    };

    public class WebAPIResult {

        public List<String> the_graphs;
        public Boolean      nextquery = false;
        public String       nextquery_params = "";

        public WebAPIResult(List<String> g) {

        the_graphs = g;
    }

    }

//    private static List<String> parse_graph_api_result(String result) throws Exception {
    private WebAPIResult parse_graph_api_result(String result) throws Exception {

        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<
                HashMap<String,Object>
                >() {};
        HashMap<String,Object> userData = mapper.readValue(result, typeRef);

        WebAPIResult graphs = null;
//        List<String> graphs = null;
        if (userData.containsKey("graphs")) {
            Object ographs = userData.get("graphs");
            try {
                HashMap<String, Object> oographs = (HashMap<String, Object>) ographs;
                if (oographs.containsKey("resultList")) {
                    Object graphsList = oographs.get("resultList");
                    graphs = new WebAPIResult((List<String>) graphsList);
                    Object more = oographs.get("more");
                    graphs.nextquery = (Boolean) more;
                    Object start = oographs.get("start");
                    Object lsize = oographs.get("listSize");
                    Integer istart = (Integer) start;
                    Integer ilsize = (Integer) lsize;
                    int from = istart.intValue() + ilsize.intValue();
                    graphs.nextquery_params = "from=" + from;


                };
            } catch (Exception e) {
                System.err.println(e.getMessage());
            };
        };

        return graphs;

    }

    //* the worker thread that fetches the results from the server
    private ResultFetcher resultFetcher=null;

    /**
     * Fetches the data from the server by applying a filter
     */
    private void fetchData(String filter){
        if(this.resultFetcher!=null){
            this.resultFetcher.nevermind=true;
        }
        this.resultFetcher= new ResultFetcher(filter);
        // should be here, while client is still interacting or it will be too late to start client side polling
        indicator.setEnabled(true);
        resultFetcher.start();
    }

    /**
     * Fetches the data from the server without applying a filter
     */
    private void fetchData(){
        this.fetchData("");
    }

    /**
     * Updates the table by fetching only the graphs that match the given filter
     */
    private void fetchByFilter(final String filter){
        fetchData(filter);
    }

    /**
     * Takes the currently marked graphs in the table and destroys the graphs. Asks for confirmation first.
     * Resets the entire table after selection.
     * @param event : the clickevent that fired the call
     */
    private void deletegraphs(ClickEvent event) {
        Button yesOption=showConfirmDialog("Warning! This function will result in the complete and irretrievable removal of the selected graphs.\n\n " +
                "Do you wish to continue?");
        final DeleteGraphs panel=this;

        yesOption.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                try {
                    panel.doDeleteGraphs();
                } catch (Exception e) {
                    getWindow().showNotification("Graph removal failed", "Remove the graphs. " +
                            "Received " + e.getClass().getSimpleName() + " error with message: " +
                            e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
    }

    private Button showConfirmDialog(String message){
        final Window notifier= new Window("Are you sure?");
        notifier.setWidth("400px");
        notifier.setModal(true);

        VerticalLayout layout = (VerticalLayout) notifier.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

        Label messageLabel = new Label(message);
        notifier.addComponent(messageLabel);

        Button yes = new Button("Yes",new ClickListener(){
            public void buttonClick(ClickEvent event){
                notifier.getParent().removeWindow(notifier);
            }
        });
        Button no = new Button("No", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                notifier.getParent().removeWindow(notifier);
            }
        });

        HorizontalLayout buttons=new HorizontalLayout();
        buttons.addComponent(yes);
        buttons.addComponent(no);
        notifier.addComponent(buttons);

        getWindow().addWindow(notifier);

        return yes;
    }

    /**
     * Performs the actual deletion of the selected graphs as specified in method eu.lod2.DeleteGraphs#deletegraphs(com.vaadin.ui.Button.ClickEvent)
     */
    private synchronized void doDeleteGraphs() throws IOException {
        String params="";
        Set<String>selection=this.getMarkedGraphs();
        BeanContainer<String,DeleteGraphsTable> datasource=this.getTableDatasource();
        for(String id: selection){
            String name=datasource.getItem(id).getBean().getGraph();
            if(params.length()>0){
                params+=",";
            }
            params+="<"+name+">";
        }
        HttpClient httpclient = new DefaultHttpClient();
        try {

            String prefixurl = state.getLod2WebApiService()+"/remove_graphs";

            HttpPost post = new HttpPost(prefixurl);

            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("graphs", params));
            UrlEncodedFormEntity data=new UrlEncodedFormEntity(postParameters);
            post.setEntity(data);

            HttpResponse response = httpclient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode!=200){
                String body="";
                BufferedReader br = new BufferedReader(
                        new InputStreamReader((response.getEntity().getContent())));

                String line;
                while ((line = br.readLine()) != null) {
                    body += line;
                }
                throw new IllegalStateException("The server responded with an invalid status code: "+statusCode+". The response was: "+body);
            }
        }finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        this.resetTable();
    }

    /**
     * Takes the current selection in the table and hides it. Clears the selection afterwards.
     * @param event : the button event that fires this call
     */
    private synchronized void hidegraphs(ClickEvent event) {
        Map<String,BeanItem<DeleteGraphsTable>>selection=this.getSelectedGraphs();
        BeanContainer<String,DeleteGraphsTable> datasource=this.getTableDatasource();
        for(String id: selection.keySet()){
            datasource.removeItem(id);
        }
        this.clearSelection();
    }

    /**
     * Takes the current seleciton in the table and inverts it.
     */
    private synchronized void invertSelection(){

        Set<String> ids=(Set<String>)this.table.getValue();
        Set<String> inverted= new HashSet<String>();

        for(String id:(Collection<String>)table.getItemIds()){
            if(!ids.contains(id)){
                inverted.add(id);
            }
        }

        this.table.setValue(inverted);
    }

    /**
     * Takes the current selection in the table and marks it. Clears the selection afterwards.
     * @param event : the button event that fires this call
     */
    private void markgraphs(ClickEvent event) {
        Map<String,BeanItem<DeleteGraphsTable>>selection=this.getSelectedGraphs();
        for(String id: selection.keySet()){
            selection.get(id).getBean().getMark().setValue(true);
        }
        this.clearSelection();
    }

    /**
     * Thread to fetch the results from the server based on the filter that is provided. Max batch size is
     * determined by the server instance.
     */
    public class ResultFetcher extends Thread {
        private String filter=null;
        //* fetching interval in milliseconds
        public int interval=500;
        //* whether or not the result is stil necessary
        public boolean nevermind=false;

        //* creates a new fetcher with the given filter
        public ResultFetcher(String filter){
            this.filter=filter;//((filter==null || filter.isEmpty())?".*":filter);

        }
        @Override
        public void run(){
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 400000);

            HttpClient httpclient = new DefaultHttpClient(httpParams);
            resetTable();

            try {

                String prefixurl = state.getLod2WebApiService()+ "/graphsregex";
                //String prefixurl = "http://lod2.wolterskluwer.de/lod2webapi/graphsregex";

                WebAPIResult r;

                //params containing the filter
                String baseParams="regex="+this.filter+"&all=true";
                //params added by result
                String extraParams="";
                String fullParams=baseParams;

                do {
                    HttpGet httpget = new HttpGet(prefixurl+"?"+fullParams);
                    httpget.addHeader("accept", "application/json");

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = httpclient.execute(httpget, responseHandler);

                    r= parse_graph_api_result(responseBody);
                    List<String> result = r.the_graphs;

                    if (r.nextquery!=null && r.nextquery) {
                        fullParams=baseParams;
                        extraParams=r.nextquery_params;
                        if(extraParams!=null && !extraParams.isEmpty()){
                            fullParams+="&"+extraParams;
                        }
                    }
                    // get the result and add it to the table
                    result = r.the_graphs;
                    addIntermediateResultToTable(result);
                    indicator.setEnabled(true);

                    sleep(interval);
                }while (r.nextquery!=null && r.nextquery && !nevermind);

            } catch (Exception e){
                getWindow().showNotification("Error fetching data", "Could not fetch the data from the server " +
                        "for the current query. The exception that occurred was: "+e.getClass().getSimpleName()+
                        " with the message: "+e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            } finally {
                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources
                httpclient.getConnectionManager().shutdown();
                indicator.setEnabled(false);
            }
        }
    }
};

