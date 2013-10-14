package eu.lod2.stat;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import eu.lod2.LOD2DemoState;
import eu.lod2.utils.DataCubePicker;
import eu.lod2.utils.GraphPicker;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.*;

/**
 * This component allows the user to select and merge two datacubes that are available in the sparql endpoint as graphs.
 * Merging datacubes in this case means that the two cubes have the same structure (in terms of dimensions, attributes
 * and units), but that they have observations for different values in the dimensions (e.g. dataset 1 has observations
 * for years 1-3 and dataset 2 has observations for years 4-5. The resulting datset in that case will have all
 * observations for years 1-5.
 * Note: this component assumes that even if a URI is used in different graphs, the URI still denotes the same object,
 * this includes observations.
 * Note: this component assumes that both datacubes that are selected for merging are well formed datacubes according
 * to the specs: http://www.w3.org/TR/vocab-data-cube/#wf-rules.
 *
 * TODO measuretype, code lists, observation groups, slices
 */
public class MergeDatasets extends VerticalLayout {
    private LOD2DemoState state;

    private Set<DataCubePicker> selectors=new LinkedHashSet<DataCubePicker>();
    private DataCubePicker referenceSelector=null;
    //* whether or not to look in the current graph only for datacubes
    private boolean lookInCurrentGraph=true;
    //* whether or not to show the datacube labels in stead of the uris
    private boolean showLabels=true;
    //* the connection to use for querying the triple store
    private RepositoryConnection connection;
    //* the memory store that holds the information about the new datacube
    private SailRepository localStore;

    public MergeDatasets(LOD2DemoState state) {
        this.state=state;
        this.render();
    }

    public void attach(){
        super.attach();
        this.localStore=new SailRepository(new MemoryStore());
        try{
            this.localStore.initialize();
        }catch (Exception e){
            throw new IllegalStateException("Could not access the memory store that should create the new cube..." +
                    "Contact your software supplier.");
        }
    }

    public void detach(){
        super.detach();
        try{
            this.localStore.shutDown();
        }catch (Exception e){
            //well at least we tried...
        }
    }

    public void render(){
        this.removeAllComponents();

        Panel panel=new Panel("Merge datasets");

        //* settings for selection
        HorizontalLayout controlsBox=new HorizontalLayout();
        controlsBox.setSpacing(true);
        CheckBox lookInCurrentGraph=new CheckBox("Data cubes from current graph",this.lookInCurrentGraph);
        CheckBox showCubeLabels=new CheckBox("Show data cube labels", this.showLabels);
        lookInCurrentGraph.setImmediate(true);
        showCubeLabels.setImmediate(true);
        controlsBox.addComponent(lookInCurrentGraph);
        controlsBox.addComponent(showCubeLabels);

        lookInCurrentGraph.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                String valueString=valueChangeEvent.getProperty().getValue().toString();
                if(valueString.equals("true")){
                    setLookInCurrentGraph(true);
                    updateDatasetPickers();
                }else{
                    setLookInCurrentGraph(false);
                    updateDatasetPickers();
                }
            }
        });
        showCubeLabels.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                String valueString = valueChangeEvent.getProperty().getValue().toString();
                if (valueString.equals("true")) {
                    setShowLabels(true);
                    updateDatasetPickers();
                } else {
                    setShowLabels(false);
                    updateDatasetPickers();
                }
            }
        });
        panel.addComponent(controlsBox);

        //* datasets to merge
        final VerticalLayout datasetbox=new VerticalLayout();
        try{
            this.connection=state.getRdfStore().getConnection();
        }catch (Exception e){
            throw new RuntimeException("Could not get a connection to the RDF store, please check the configuration.");
        }

        this.addDataset(datasetbox,true);
        this.addDataset(datasetbox);
        panel.addComponent(datasetbox);

        Button addButton=new Button("Add dataset");
        addButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                addDataset(datasetbox);
            }
        });
        panel.addComponent(addButton);

        //* target
        VerticalLayout targetProperties=new VerticalLayout();
        final GraphPicker targetGraphPicker=new GraphPicker("Select the graph to enter the new datacube in",state);
        final TextField datasetURI=new TextField("URI for the new datacube");
        targetProperties.addComponent(targetGraphPicker);
        targetProperties.addComponent(datasetURI);

        Button mergeButton=new Button("Merge datasets");
        mergeButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                Object dsURI=datasetURI.getValue();
                String targetGraph=(String)targetGraphPicker.getValue();
                if(dsURI==null || dsURI.toString().isEmpty()){
                    getWindow().showNotification("No name for the new data set",
                            "We could not find a name for the new data set. " +
                                    "Please provide one in the available field.",
                            Window.Notification.TYPE_WARNING_MESSAGE);
                    return;
                }
                if(targetGraph==null || targetGraph.isEmpty()){
                    getWindow().showNotification("No target graph selected",
                            "We could not find a target graph to put the new data set in. " +
                                    "Please provide one in the available select box.",
                            Window.Notification.TYPE_WARNING_MESSAGE);
                    return;
                }

                Set<String> datasets=getDatasetsToMerge();
                if(datasets.size()<2){
                    getWindow().showNotification("Not enough datasets",
                            "The component needs to have at least two datasets to perform a merge. " +
                                    "Please select more data cubes.",
                            Window.Notification.TYPE_WARNING_MESSAGE);
                    return;
                }

                mergeDatasets(dsURI.toString(),targetGraph,datasets);
            }
        });
        panel.addComponent(targetProperties);
        panel.addComponent(mergeButton);


        ((VerticalLayout)panel.getContent()).setSpacing(true);

        updateDatasetPickers();

        this.addComponent(panel);

        //* add a legend
        Panel legend = new Panel("Legend");
        legend.addComponent(new Label("- D: number of dimensions, M: number of measures, A: number of attributes"));
        legend.addComponent(new Label("- Reference dataset: the properties of the reference dataset will be " +
                "transferred to the new cube that gets created. This only holds for properties that are directly " +
                "connected via a single predicate to the elements in the Data Cube Vocabulary."));
        this.addComponent(legend);

    }

    public void setLookInCurrentGraph(boolean look) {
        this.lookInCurrentGraph=look;
    }

    public void setShowLabels(boolean labels) {
        this.showLabels=labels;
    }

    /**
     * returns whether or not it is possible to merge the given set of dataset uris according to the
     * current connection
     *
     * returns a reason why the components cannot be merged if they cannot be merged and returns null if they
     * can be merged.
     */
    public String canMergeDatasets(Set<String> datasets){
        String previousDSD=null;
        for(String currentDSD : datasets){
            if(previousDSD==null){
                previousDSD=currentDSD;
                continue;
            }
            String incompatibilityReason=compatibleDSDComponents(previousDSD, currentDSD, connection);
            if(incompatibilityReason!=null){
                return incompatibilityReason;
            }
            previousDSD=currentDSD;
        }

        return null;
    }

    /**
     * Checks whether the given dsds are considered to have the same components. Two dsds are considered to have the
     * components if the set of their components are the same (same uri)
     */
    public boolean equalDSDComponents(String dsdURI1, String dsdURI2, RepositoryConnection connection){
        StringBuilder builder=new StringBuilder();
        builder.append("ASK { ");
        builder.append("{ <").append(dsdURI1).append("> qb:component ?componentSpec. ");
        builder.append("FILTER NOT EXISTS { <").append(dsdURI2).append("> qb:component ?componentSpec } }");
        builder.append("UNION { <").append(dsdURI2).append("> qb:component ?componentSpec. ");
        builder.append("FILTER NOT EXISTS { <").append(dsdURI1).append("> qb:component ?componentSpec } } }");

        try{
            return !connection.prepareBooleanQuery(QueryLanguage.SPARQL,builder.toString()).evaluate();
        }catch (Exception e){
            throw new IllegalStateException("There was an implementation problem with the query that checks whether " +
                    "the two datasets have the same DataStructureDefinition. Please contact the development team...");
        }
    }

    /**
     * Checks whether the given dsds are compatible, meaning that they should have the same number of components per
     * type of component
     * Returns a reason as a string if the components are not compatible, returns null if they are compatible
     */
    public String compatibleDSDComponents(String dsdURI1, String dsdURI2, RepositoryConnection connection){
        Map<String,Integer> counts1=getComponentCounts(dsdURI1,connection);
        Map<String,Integer> counts2=getComponentCounts(dsdURI2,connection);

        for(String s : counts1.keySet()){
            // both counts are guaranteed to have the exact same (equal) keys
            if(!counts1.get(s).equals(counts2.get(s))){
                return "DSD <"+dsdURI1+"> and <"+dsdURI2+"> are incompatible because " +
                        "they have different counts for the components of type "+s;
            }
        }
        return null;
    }

    public static final String DIMENSION_HUMAN_NAME = "dimension";
    public static final String MEASURE_HUMAN_NAME = "measure";
    public static final String ATTRIBUTE_HUMAN_NAME = "attribute";

    /**
     * returns the component counts for each of the datacube components mapped from component type to a count
     * of components for that type
     */
    public Map<String,Integer> getComponentCounts(String dsdURI, RepositoryConnection connection){
        StringBuilder builder=new StringBuilder();
        builder.append("SELECT (count(distinct ?dim) as ?dimcount) ");
        builder.append("(count(distinct ?measure) as ?mcount) ");
        builder.append("(count(distinct ?attribute) as ?acount)\n");
        builder.append("WHERE {\n");
        builder.append("<").append(dsdURI).append(">  <http://purl.org/linked-data/cube#structure> ?dsd.\n");
        builder.append("?dsd <http://purl.org/linked-data/cube#component> ?compcs.\n");
        builder.append("{?compcs <http://purl.org/linked-data/cube#dimension> ?dim.}\n");
        builder.append("UNION {?compcs <http://purl.org/linked-data/cube#measure> ?measure.} \n");
        builder.append("UNION {?compcs <http://purl.org/linked-data/cube#attribute> ?attribute.}}");

        try{
            TupleQueryResult result=connection.prepareTupleQuery(QueryLanguage.SPARQL,builder.toString()).evaluate();
            if(result.hasNext()){
                BindingSet bindings=result.next();
                Map<String,Integer> results=new HashMap<String, Integer>();
                results.put(DIMENSION_HUMAN_NAME,Integer.parseInt(bindings.getValue("dimcount").stringValue()));
                results.put(MEASURE_HUMAN_NAME,Integer.parseInt(bindings.getValue("mcount").stringValue()));
                results.put(ATTRIBUTE_HUMAN_NAME,Integer.parseInt(bindings.getValue("acount").stringValue()));

                return results;
            }else{
                Map<String,Integer> results=new HashMap<String, Integer>();
                results.put(DIMENSION_HUMAN_NAME,0);
                results.put(MEASURE_HUMAN_NAME,0);
                results.put(ATTRIBUTE_HUMAN_NAME,0);

                return results;
            }
        }catch (Exception e){
            throw new IllegalStateException("Sorry, we could not execute the query to retrieve the component counts " +
                    "from the server. Contact the development team.");
        }
    }

    //* returns the graph that have currently been selected by the user for merging as a set of strings
    public Set<String> getDatasetsToMerge(){
        HashSet<String> toMerge=new LinkedHashSet<String>();
        for(DataCubePicker picker : this.selectors){
            String value = picker.getSelection();
            if(value != null && !value.isEmpty()){
                toMerge.add(value);
            }
        }

        return toMerge;
    }

    //* updates the dataset pickers according to this component's options
    private void updateDatasetPickers(){
        for(DataCubePicker picker : this.selectors){
            picker.setShowURIs(!this.showLabels);
            picker.setCurrentGraph(this.lookInCurrentGraph?this.state.getCurrentGraph():null);
        }
    }

    /**
     * This function merges the datasets that have been selected by the user
     * @param datacubeURI the new uri for the datacube (may already exist, if so, tough luck)
     * @param targetGraph the graph to inser the datacube into (is guaranteed to exist)
     * @param datasets the uris for the datasets, should be larger than one and filled with uris
     */
    private void mergeDatasets(String datacubeURI, String targetGraph, Set<String> datasets){
        String incompatibilityReason=canMergeDatasets(datasets);
        if(incompatibilityReason==null){
            String referenceCubeUri=this.referenceSelector.getSelection();
            datasets.remove(referenceCubeUri);
            DataCubeMapper mapper=new DataCubeMapper("Select data cube component mappings",datasets,
                    referenceCubeUri,datacubeURI,targetGraph);
            mapper.setModal(true);
            mapper.setWidth((getWindow().getWidth()/2)+"px");
            mapper.center();

            this.getWindow().addWindow(mapper);
        }else{
            Window window=new Window("Cannot map datasets");
            window.setModal(true);
            window.setWidth("50%");
            window.addComponent(new Label("Sorry, we cannot map the datasets, their structure is too different. One reason why the datasets are incompatible is: "));
            window.addComponent(new Label(incompatibilityReason));
            window.center();

            this.getWindow().addWindow(window);
        }
    }

    /**
     * Adds a new Graphpicker to the list of datasets
     * @param datasetHolder the panel to add the dataset picker to
     * @param reference whether or not the dataset is the reference cube
     */
    public void addDataset(final VerticalLayout datasetHolder, boolean reference){

        final HorizontalLayout box=new HorizontalLayout();
        final DataCubePicker picker=new DataCubePicker("Select a "+(reference?"(reference) ":"")+"dataset to merge",
                connection, this.lookInCurrentGraph?this.state.getCurrentGraph():null);
        final Button remover = new Button("Remove");
        remover.setEnabled(!reference);
        remover.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                datasetHolder.removeComponent(box);
                selectors.remove(picker);
            }
        });


        box.addComponent(picker);
        box.addComponent(remover);

        box.setSpacing(true);

        box.setComponentAlignment(picker,Alignment.MIDDLE_LEFT);
        box.setComponentAlignment(remover,Alignment.BOTTOM_LEFT);

        datasetHolder.addComponent(box);

        selectors.add(picker);
        if(reference){
            this.referenceSelector=picker;
        }
    }

    //* adds a non-reference dataset to the layout
    public void addDataset(VerticalLayout datasetHolder){
        this.addDataset(datasetHolder,false);
    }

    /**
     * Maps the properties of similar datacubes to the properties of other cubes that are available
     */
    private class DataCubeMapper extends Window{
        //* the uri of the reference datacube (other cube's properties sould be linked to this cube
        private String referenceCube;
        //* the other cube uris, reference cube not in this list
        private List<String> otherCubes;
        //* the new uri for the datacube
        private String datacubeURI;
        //* the target graph to insert the new cube into
        private String targetGraph;
        //* suffix to use for creating the dsd uri
        private final static String dsdSuffix="/dsd";
        //* suffix to use for creating component uris
        private final static String componentSuffix="/comp";
        //* suffix to use for creating component specification uris
        private final static String componentSpecSuffix="/compspec";
        //* component types for reference components
        private Map<String,String> referenceTypes=null;
        //* dimensions in this set will be fused
        private Set<String> fuseDimensions=new HashSet<String>();

        //* whether or not to use uris to talk about components (otherwise labels will be used)
        private boolean displayURIs=false;

        //* mapping from component uri to the selected combobox holding the users mapping choice
        private Map<String,Map<String,ComboBox>> userMapping=null;

        public DataCubeMapper(String title, Set<String> datacubes, String referenceCubeUri, String datacubeURI, String targetGraph){
            super(title);
            if(datacubes.size()<1){
                throw new IllegalArgumentException("Sorry, you cannot map fewer than two data cubes...");
            }
            this.datacubeURI=datacubeURI;
            this.targetGraph=targetGraph;
            otherCubes=new Vector<String>(datacubes);
            referenceCube=referenceCubeUri;
            this.render();
        }

        public void setDisplayURIs(boolean value){
            this.displayURIs=value;
        }

        public void setFuseDimension(String component,boolean fuse){
            if(fuse){
                this.fuseDimensions.add(component);
            }else{
                this.fuseDimensions.remove(component);
            }
        }

        //* re-renders the window based on the current settings
        public void render(){
            this.removeAllComponents();
            ((VerticalLayout)this.getContent()).setSpacing(true);

            HorizontalLayout options=new HorizontalLayout();
            options.setSpacing(true);

            final CheckBox uriLabels=new CheckBox("Show component URIs", this.displayURIs);
            uriLabels.setImmediate(true);
            options.addComponent(uriLabels);

            uriLabels.addListener(new Property.ValueChangeListener() {
                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                    String valueString=valueChangeEvent.getProperty().getValue().toString();
                    if(valueString.equals("true")){
                        setDisplayURIs(true);
                        render();
                    }else{
                        setDisplayURIs(false);
                        render();
                    }
                }
            });

            this.addComponent(options);

            // for each component, allow selection boxes to do mappings for every cube
            Map<String,String> referenceLabels=new HashMap<String, String>();
            this.referenceTypes=new HashMap<String, String>();
            fetchReferenceComponents(this.referenceCube, referenceLabels, referenceTypes);
            final Map<String,Map<String,String>> labels=new HashMap<String, Map<String, String>>();
            final Map<String,Map<String,String>> types=new HashMap<String, Map<String, String>>();

            for(String otherCube: this.otherCubes){
                Map<String, String> otherLabels=new HashMap<String, String>();
                Map<String, String> otherTypes=new HashMap<String, String>();
                fetchReferenceComponents(otherCube,otherLabels,otherTypes);
                labels.put(otherCube,otherLabels);
                types.put(otherCube,otherTypes);
            }

            this.userMapping= new HashMap<String, Map<String, ComboBox>>();
            for(String component:referenceLabels.keySet()){
                Map<String,ComboBox> result=
                        buildComponentMappingPanel(component,
                                referenceLabels.get(component),referenceTypes.get(component),
                                labels,types);
                userMapping.put(component,result);
            }

            Button confirm=new Button("Perform merge");
            confirm.addListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent clickEvent) {
                    if(!readyToMerge(labels)){
                        getWindow().showNotification("Not ready to merge", "Sorry, we are not ready to merge, please " +
                                "ensure that all components are matched and no duplicates exist.",
                                Notification.TYPE_WARNING_MESSAGE);
                    }else{
                        performMerge();
                    }
                }
            });
            this.addComponent(confirm);

        }

        /**
         * Ready to merge if all components are matched and no duplicates exist
         */
        public boolean readyToMerge(Map<String, Map<String, String>> labels){
            // set containing all uris for components in the graphs, should be empty if all components have been
            // divided and no duplicates exist
            HashSet<String> uris=new HashSet<String>();
            for(String otherDs: labels.keySet()){
                for(String component: labels.get(otherDs).keySet()){
                    uris.add(component);
                }
            }

            for(String component:userMapping.keySet()){
                Map<String,ComboBox> componentMatches=userMapping.get(component);
                for(String otherDS: componentMatches.keySet()) {
                    ComboBox options=componentMatches.get(otherDS);
                    LabeledOption option=(LabeledOption) options.getValue();
                    if(option==null){
                        return false;
                    }else{
                        uris.remove(option.value);
                    }
                }
            }
            return uris.isEmpty();
        }

        /**
         * Merges the datasets according to the user's settings
         */
        public void performMerge(){
            this.buildNewDSD();
            this.buildNewDataset();
            HashMap<String,String> componentMapping=this.buildNewComponents();
            this.buildNewCodeLists();
            this.buildNewObservations(componentMapping);
            getWindow().getParent().showNotification("Merge complete",
                    "The datacubes have been merged into the cube <"+datacubeURI+"> in <"+targetGraph+">.",
                    Notification.TYPE_HUMANIZED_MESSAGE);
            this.close();
        }

        private void buildNewObservations(HashMap<String,String> componentTranslation) {
            HashMap<String, HashMap<String,String>> componentMappingPerCube=
                    new HashMap<String, HashMap<String, String>>();
            // translate out the comboboxes
            for(String component:this.userMapping.keySet()){
                HashMap<String,String> cubeComponentMapping=new HashMap<String, String>();
                componentMappingPerCube.put(component,cubeComponentMapping);
                for(String otherCube:this.userMapping.get(component).keySet()){
                    String targetComponent=((LabeledOption)this.userMapping.get(component).
                            get(otherCube).getValue()).value;
                    cubeComponentMapping.put(otherCube,targetComponent);
                }
                cubeComponentMapping.put(referenceCube,component);
            }

            this.buildObservationsWithFusion(componentMappingPerCube,componentTranslation);

        }

        /**
         * This function applies a basic fusion strategy to the values of the dimensions. It uses the labels of the
         * dimension values to decide whether the uri's in the values point to the same concept. Currently uri's are
         * regarded as the same if the labels of the values are exactly equal.
         */
        private void buildObservationsWithFusion(HashMap<String, HashMap<String, String>> componentMappingPerCube,
                                                 HashMap<String, String> componentTranslation){
            // use dimension values as a fixed point
            this.buildDimensionValues(componentMappingPerCube,componentTranslation);
            // then perform the actual fusion
            for(String component:componentMappingPerCube.keySet()){
                String targetComponent=componentTranslation.get(component);
                String type=referenceTypes.get(component);
                for(String otherCube:componentMappingPerCube.get(component).keySet()){
                    String originalComponent=componentMappingPerCube.get(component).get(otherCube);

                    if(type.equals("dimension") && this.fuseDimensions.contains(component)){
                        // keep uri's of original observations but change their value to the correct dimension value
                        fuseNewObservations(targetComponent,originalComponent,otherCube);
                    }else{
                        generateNewObservations(targetComponent,originalComponent,otherCube);
                    }
                }
            }

        }

        /**
         * Creates new observations values for the observations found in the original cube, without regard for fusing the
         * dimension values. This means that unless the exact same code lists have been used for dimension values in all cubes, dimension
         * values with the 'same' values will *not* be seen as equal.
         */
        private void generateNewObservations(String tComponent, String originalComponent, String originalCube){

            // TODO doing two queries per dimension per datacube... -> network. Make more efficient?
            // keep the uri's of the original observations as they are not being merged here!
            StringBuilder builder=new StringBuilder();
            builder.append("INSERT INTO GRAPH <").append(targetGraph).append(">\n");
            builder.append("{ ?s ?p ?o. \n");
            builder.append("?s <").append(tComponent).append("> ?value. \n");
            builder.append("?s a <http://purl.org/linked-data/cube#Observation>. \n");
            builder.append("?s <http://purl.org/linked-data/cube#dataSet> <").
                    append(datacubeURI).append("> }\n");
            builder.append("WHERE { { SELECT ?p ?o ?value ").
                    append("(IRI(xsd:string(concat(\"").append(datacubeURI).append("/obs/\",ENCODE_FOR_URI(STR(?s))))) AS ?s)").
                    append("WHERE {").
                    append("?s <http://purl.org/linked-data/cube#dataSet> <").append(originalCube).append(">. \n");
            builder.append("?s a <http://purl.org/linked-data/cube#Observation>. \n");
            builder.append("?s <").append(originalComponent).append("> ?value. \n");
            builder.append("?s ?p ?o. \n");
            builder.append("FILTER fn:not(regex(?p, \"http://purl.org/linked-data/cube#\")).\n").
                    append( "FILTER NOT EXISTS {?p a <http://purl.org/linked-data/cube#MeasureProperty>.}. \n").
                    append( "FILTER NOT EXISTS {?p a <http://purl.org/linked-data/cube#DimensionProperty>.}. \n").
                    append( "FILTER NOT EXISTS {?p a <http://purl.org/linked-data/cube#AttributeProperty>.} }} }");

            try{
                connection.prepareGraphQuery(QueryLanguage.SPARQL, builder.toString()).evaluate();
            }catch (Exception e){
                throw new IllegalStateException("Sorry, we could not update the target graph with the new observations. The " +
                        "error message was: "+e.getMessage());
            }

            // keep the uri's of the original dimension/measure values as they are not being merged here!
            StringBuilder builder2=new StringBuilder();
            builder2.append("INSERT INTO GRAPH <").append(targetGraph).append(">\n");
            builder2.append("{ ?s ?p ?o. \n");
            builder2.append("?s a <").append(tComponent).append(">. } \n");
            builder2.append("WHERE { ").
                    append("?s a <").append(originalComponent).append(">. \n");
            builder2.append("?obs <").append(originalComponent).append("> ?s. \n");
            builder2.append("?obs <http://purl.org/linked-data/cube#dataSet> <").append(originalCube).append(">. \n");
            builder2.append("?s ?p ?o. \n");
            builder2.append("FILTER fn:not(regex(?p, \"http://purl.org/linked-data/cube#\")) } ");

            try{
                connection.prepareGraphQuery(QueryLanguage.SPARQL, builder2.toString()).evaluate();
            }catch (Exception e){
                throw new IllegalStateException("Sorry, we could not update the target graph with the new observations. The " +
                        "error message was: "+e.getMessage());
            }
        }

        /**
         * Creates new dimension values by fusing the original component values based on rdfs:label or skos:prefLabel.
         * It uses the labels of the dimension values to decide whether the uri's in the values point to the same concept.
         */
        private void fuseNewObservations(String targetComponent, String originalComponent, String otherCube){
            StringBuilder builder=new StringBuilder();

            builder.append("INSERT INTO GRAPH <").append(targetGraph).append(">\n");
            builder.append("{ ?s ?p ?o. \n");
            builder.append("?s <").append(targetComponent).append("> ?value. \n");
            builder.append("?s a <http://purl.org/linked-data/cube#Observation>. \n");
            builder.append("?s <http://purl.org/linked-data/cube#dataSet> <").
                    append(datacubeURI).append("> }\n");
            builder.append("WHERE { { SELECT ?p ?o ?value ").
                    append("(IRI(xsd:string(concat(\"").append(datacubeURI).append("/obs/\",ENCODE_FOR_URI(STR(?s))))) AS ?s)").
                    append("WHERE {").
                    append("?s <http://purl.org/linked-data/cube#dataSet> <").append(otherCube).append(">. \n");
            builder.append("?s a <http://purl.org/linked-data/cube#Observation>. \n");
            builder.append("?s <").append(originalComponent).append("> ?originalvalue. \n");
            builder.append("{?originalvalue rdfs:label ?label. } UNION {?originalvalue skos:prefLabel ?label}. \n");
            builder.append("GRAPH <").append(targetGraph).append("> {\n").
                    append("?value a <").append(targetComponent).append(">.\n").
                    append("{?value rdfs:label ?label. } UNION {?value skos:prefLabel ?label} }.");
            builder.append("?s ?p ?o. \n");
            builder.append("FILTER fn:not(regex(?p, \"http://purl.org/linked-data/cube#\")).\n").
                    append( "FILTER NOT EXISTS {?p a <http://purl.org/linked-data/cube#MeasureProperty>.}. \n").
                    append( "FILTER NOT EXISTS {?p a <http://purl.org/linked-data/cube#DimensionProperty>.}. \n").
                    append( "FILTER NOT EXISTS {?p a <http://purl.org/linked-data/cube#AttributeProperty>.} }} } ");

            try{
                connection.prepareGraphQuery(QueryLanguage.SPARQL, builder.toString()).evaluate();
            }catch (Exception e){
                throw new IllegalStateException("Sorry, we could not update the target graph with the new observations. The " +
                        "error message was: "+e.getMessage());
            }

        }

        /**
         * Creates new dimension values (this happens in case the dimensions should be fused).
         */
        private void buildDimensionValues(HashMap<String, HashMap<String, String>> componentMappingPerCube, HashMap<String, String> componentTranslation){
            for(String component:componentMappingPerCube.keySet()){
                String referenceComponent=componentTranslation.get(component);
                String type=referenceTypes.get(component);
                if(!type.equals("dimension")){
                    continue;
                }
                for(String otherCube:componentMappingPerCube.get(component).keySet()){
                    String targetComponent=componentMappingPerCube.get(component).get(otherCube);
                    StringBuilder builder=new StringBuilder();
                    builder.append("INSERT INTO GRAPH <").append(targetGraph).append(">\n");
                    builder.append("{ ?value a <").append(referenceComponent).append(">. \n");
                    builder.append("?value ?p ?o. }\n");
                    builder.append("WHERE { ").
                            append("?s <http://purl.org/linked-data/cube#dataSet> <").append(otherCube).append(">. \n");
                    builder.append("?s a <http://purl.org/linked-data/cube#Observation>. \n");
                    builder.append("?s <").append(targetComponent).append("> ?value. \n");
                    builder.append("?value ?p ?o. \n");
                    builder.append("FILTER fn:not(regex(?p, \"http://purl.org/linked-data/cube#\")).\n").
                            append("FILTER fn:not(?o IN (<").append(targetComponent).append(">)).\n").
                            append("FILTER NOT EXISTS { ?value rdfs:label ?l.\n").
                            append("GRAPH <").append(targetGraph).append("> {\n").
                            append("?other a <").append(referenceComponent).append(">. \n").
                            append("?other rdfs:label ?l.}\n");
                    builder.append("} }");

                    try{
                        connection.prepareGraphQuery(QueryLanguage.SPARQL, builder.toString()).evaluate();
                    }catch (Exception e){
                        throw new IllegalStateException("Sorry, we could not update the target graph with the new dimension values. The " +
                                "error message was: "+e.getMessage());
                    }
                }
            }
        }

        private void buildNewCodeLists() {
            //TODO
        }

        //* removes the leading namespace part from the given uri if possible, returns uri otherwise
        private String removeNamespace(String uri){
            if(uri.endsWith("/")){
                uri=uri.substring(0,uri.length()-1);
            }
            try{
                return uri.substring(uri.lastIndexOf("/")+1);
            }catch (Exception e){
                return uri;
            }
        }

        //* generates a name from the given component uri that has not been taken in the provided set of names
        private String generateNameFromComponentUri(String componentUri,Set namesTaken){
            String name = removeNamespace(componentUri);
            int i = 1;
            while(namesTaken.contains(name)){
                name = name + i;
                i++;
            }
            namesTaken.add(name);
            return name;
        }

        /**
         * Builds the new component specifications, taking the original dataset's components for a base
         * links the new dsd to the component specifications
         *
         * returns a mapping from reference component uri to new component uri
         */
        private HashMap<String,String> buildNewComponents() {
            // for each component
            int count=1;
            // holds all new component names in this namespace
            HashSet<String> namesTaken=new HashSet<String>();
            HashMap<String, String> componentMapping=new HashMap<String, String>();
            for(String component:this.userMapping.keySet()){
                StringBuilder builder=new StringBuilder();
                String name = generateNameFromComponentUri(component,namesTaken);
                String newComponentName=datacubeURI+componentSuffix+"/"+name;
                componentMapping.put(component,newComponentName);
                String newComponentSpecName=datacubeURI+componentSpecSuffix+"/"+name;
                String type="http://purl.org/linked-data/cube#"+referenceTypes.get(component);

                //create component specifications
                builder.append("INSERT INTO GRAPH <").append(targetGraph).append(">\n");
                builder.append("{ <").append(newComponentSpecName).append("> ?p ?o. \n");
                builder.append("<").append(datacubeURI).append(dsdSuffix).
                        append("> <http://purl.org/linked-data/cube#component> <").
                        append(newComponentSpecName).append("> }\n");
                builder.append("WHERE { ?cs <").append(type).append("> <").append(component).append(">.\n");
                builder.append("?cs ?p ?o.\n");
                builder.append("FILTER fn:not(regex(?p, \"http://purl.org/linked-data/cube#\")) }");

                //create component
                StringBuilder builder2=new StringBuilder();
                builder2.append("INSERT INTO GRAPH <").append(targetGraph).append(">\n");
                builder2.append("{ <").append(newComponentName).append("> ?p ?o. \n");
                builder2.append("<").append(newComponentSpecName).append("> <").append(type).
                        append("> <").append(newComponentName).append(">. }");
                builder2.append("WHERE { <").append(component).append("> ?p ?o.\n");
                builder2.append("FILTER fn:not(regex(?p, \"http://purl.org/linked-data/cube#\")) }\n");
                try{
                    connection.prepareGraphQuery(QueryLanguage.SPARQL, builder.toString()).evaluate();
                    connection.prepareGraphQuery(QueryLanguage.SPARQL, builder2.toString()).evaluate();
                }catch (Exception e){
                    throw new IllegalStateException("Sorry, we could not update the target graph with the new components. The " +
                            "error message was: "+e.getMessage());
                }
                count++;
            }
            return componentMapping;
        }

        //* builds the new dataset, taking the reference dataset for a base and linking it to the dsd
        private void buildNewDataset() {
            StringBuilder builder=new StringBuilder();
            builder.append("INSERT INTO GRAPH <").append(targetGraph).append(">\n");
            builder.append("{ <").append(datacubeURI).append("> ?p ?o. \n");
            builder.append("<").append(datacubeURI).append("> <http://purl.org/linked-data/cube#structure> ").
                    append("<").append(datacubeURI).append(dsdSuffix).append("> }\n");
            builder.append("WHERE { <").append(referenceCube).
                    append("> ?p ?o.\n");
            builder.append("FILTER fn:not(regex(?p, \"http://purl.org/linked-data/cube#\")) }");
            try{
                connection.prepareGraphQuery(QueryLanguage.SPARQL, builder.toString()).evaluate();
            }catch (Exception e){
                throw new IllegalStateException("Sorry, we could not update the target graph with the new cube. The " +
                        "error message was: "+e.getMessage());
            }
        }

        //* this function creates a new dsd in the target graph based on the uri of the dataset that was provided
        private void buildNewDSD(){
            StringBuilder builder=new StringBuilder();
            builder.append("INSERT INTO GRAPH <").append(targetGraph).append(">\n");
            builder.append("{ <").append(datacubeURI).append(dsdSuffix).append("> ?p ?o }\n");
            builder.append("WHERE { <").append(referenceCube).
                    append("> <http://purl.org/linked-data/cube#structure> ?dsd.\n");
            builder.append("?dsd ?p ?o.\n");
            builder.append("FILTER fn:not(regex(?p, \"http://purl.org/linked-data/cube#\")) }");
            try{
                connection.prepareGraphQuery(QueryLanguage.SPARQL, builder.toString()).evaluate();
            }catch (Exception e){
                throw new IllegalStateException("Sorry, we could not update the target graph with the new dsd. The " +
                        "error message was: "+e.getMessage());
            }
        }

        /**
         * creates a panel that allows the user to map the component uri to each of the components in the other cubes
         * Adds the panel to the window as a next component. Returns a reference map from othercube uri to
         * the comboboxwith the selected component uri
         */
        private Map<String,ComboBox> buildComponentMappingPanel(final String componentUri, String label, String type,
                                                 Map<String,Map<String,String>> labels,
                                                 Map<String,Map<String,String>> types){
            Panel panel=new Panel(this.displayURIs?componentUri:label + " ("+type+")");

            if(type.equals("dimension")){
                final CheckBox fuseDimensions=new CheckBox("Consider component values with same label to be equal",
                        this.fuseDimensions.contains(componentUri));
                fuseDimensions.setImmediate(true);
                panel.addComponent(fuseDimensions);

                fuseDimensions.addListener(new Property.ValueChangeListener() {
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        String valueString=valueChangeEvent.getProperty().getValue().toString();
                        if(valueString.equals("true")){
                            setFuseDimension(componentUri,true);
                        }else{
                            setFuseDimension(componentUri,false);
                        }
                    }
                });
            }

            HashMap<String,ComboBox> comboBoxes=new HashMap<String, ComboBox>();
            for(String otherCube : labels.keySet()){
                ComboBox options=new ComboBox(otherCube);
                Map<String,String> currentLabels= labels.get(otherCube);
                Map<String,String> currentTypes= types.get(otherCube);
                for(String component: currentLabels.keySet()){
                    String currentType= currentTypes.get(component);
                    if(type.equals(currentType)){
                        //only consider compatible types
                        options.addItem(new LabeledOption(component,
                                this.displayURIs?component : currentLabels.get(component)));
                    }
                }
                comboBoxes.put(otherCube,options);
                panel.addComponent(options);
            }

            this.addComponent(panel);
            return comboBoxes;
        }

        private class LabeledOption{
            public String label;
            public String value;

            public LabeledOption(String value,String label){
                this.label=label;
                this.value=value;
            }

            public String toString(){
                return this.label;
            }
        }

        //* fetches the labels and types for the components in the reference cube
        private void fetchReferenceComponents(String target,Map<String,String> labels, Map<String,String> types){
            StringBuilder queryBuilder=new StringBuilder();
            queryBuilder.append("SELECT ?c ?l ?type \nWHERE { ");
            // only fetch info on relevant cubes in case there are very very many in the endpoint
            queryBuilder.append("<").append(target).append("> ");
               queryBuilder.append(" <http://purl.org/linked-data/cube#structure> ?dsd.\n");
            queryBuilder.append("?dsd <http://purl.org/linked-data/cube#component> ?compcs. \n");
            queryBuilder.append("{ {?compcs <http://purl.org/linked-data/cube#measure> ?c. } \n");
            queryBuilder.append("UNION {?compcs <http://purl.org/linked-data/cube#dimension> ?c }\n");
            queryBuilder.append("UNION {?compcs <http://purl.org/linked-data/cube#attribute> ?c } }\n");
            queryBuilder.append("?compcs ?type ?c.\n");
            queryBuilder.append("OPTIONAL {?c <http://www.w3.org/2000/01/rdf-schema#label> ?l } }");

            try{
                TupleQueryResult result=connection.prepareTupleQuery(
                        QueryLanguage.SPARQL,queryBuilder.toString()).evaluate();

                while (result.hasNext()){
                    BindingSet bindings=result.next();
                    String uri=bindings.getValue("c").stringValue();
                    String type=bindings.getValue("type").stringValue();
                    type=type.substring(type.lastIndexOf("#")+1);
                    String label=uri;
                    try{
                        label=bindings.getValue("l").stringValue();
                    }catch (Exception e){
                        //missing value, keep it ot uri
                    }
                    labels.put(uri,label);
                    types.put(uri,type);
                }
            }catch (Exception e){
                throw new IllegalStateException("There was a problem fetching the translation from reference cube " +
                        "component uri's to labels and types. " +
                        "Please check your database setup or contact the development team.");
            }
        }
    }
}
