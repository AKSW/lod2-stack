package eu.lod2;

import com.hp.hpl.jena.n3.turtle.TurtleParseException;
import com.vaadin.data.Property;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.*;
import de.fuberlin.wiwiss.r2r.*;
import eu.lod2.utils.GraphPicker;

import java.io.*;
import java.util.Arrays;

/**
 * Integrates the R2R api in a basic way. Allows the user to specify a mapping, either from a repos or from text. Allows
 * him to specify a vocabulary as well.
 * The result of the mapping is presented to the user as a downloadable file. This is because R2R uses Jena, a dependency we do not
 * wish to introduce at this point.
 */
public class R2R extends VerticalLayout {
    protected LOD2DemoState state;
    protected final static String MAP_TEXT="Text";
    protected final static String MAP_GRAPH="Graph";
    protected String mappingFashion = MAP_TEXT;

    protected String sourceGraph=null;
    protected String mappingGraph=null;
    protected String mappingRules=null;
    protected String vocabulary=null;

    public R2R(LOD2DemoState state){
        this.state=state;
        this.render();
    }

    public void render(){
        this.removeAllComponents();

        Panel panel=new Panel("Perform R2R mapping");
        this.addComponent(panel);
        ((VerticalLayout)panel.getContent()).setSpacing(true);

        final GraphPicker sourceGraphPicker = new GraphPicker("Select the source graph", this.state);

        setValueIfAvailable(sourceGraphPicker,sourceGraph);
        panel.addComponent(sourceGraphPicker);
        sourceGraphPicker.setImmediate(true);
        sourceGraphPicker.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                sourceGraph=(String) sourceGraphPicker.getValue();
            }
        });

        final ComboBox mappingFashionBox = new ComboBox("How would you like to enter the mapping rules?", Arrays.asList(MAP_TEXT,MAP_GRAPH));
        mappingFashionBox.setNullSelectionAllowed(false);
        mappingFashionBox.setTextInputAllowed(false);
        mappingFashionBox.setImmediate(true);

        setValueIfAvailable(mappingFashionBox,mappingFashion);
        mappingFashionBox.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                mappingFashion= (String) mappingFashionBox.getValue();
                render();
            }
        });
        panel.addComponent(mappingFashionBox);

        final AbstractField mappingInput;
        if(this.mappingFashion.equals(MAP_TEXT)){
            mappingInput=new TextArea("Mapping Rules");
            setValueIfAvailable(mappingInput,mappingRules);
            mappingInput.addListener(new Property.ValueChangeListener() {
                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                    mappingRules=(String) mappingInput.getValue();
                }
            });
            mappingInput.setWidth("100%");
        }else{
            mappingInput=new GraphPicker("Mapping rules graph", state);
            setValueIfAvailable(mappingInput,mappingGraph);
            mappingInput.addListener(new Property.ValueChangeListener() {
                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                    mappingGraph=(String) mappingInput.getValue();
                }
            });
        }
        panel.addComponent(mappingInput);

        final TextArea vocabularyInput = new TextArea("Specify the target vocabulary here");
        setValueIfAvailable(vocabularyInput,vocabulary);
        vocabularyInput.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                vocabulary=(String)vocabularyInput.getValue();
            }
        });
        vocabularyInput.setWidth("100%");
        panel.addComponent(vocabularyInput);


        Button action= new Button("Perform Mapping");
        panel.addComponent(action);

        action.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                performMapping(sourceGraphPicker,vocabularyInput,mappingInput);
            }
        });
    }

    //* puts the given value into the field if the value is available
    protected void setValueIfAvailable(AbstractField field, String value){
        if(value!=null && !value.isEmpty()){
            field.setValue(value);
        }
    }

    //* performs the mapping based on the values of the inputs as entered by the user.
    protected void performMapping(GraphPicker sourceGraphPicker,
                                  AbstractTextField vocabularyInput,
                                  AbstractField mappingInput){
        String sourceGraph=(String) sourceGraphPicker.getValue();
        String vocabulary=(String) vocabularyInput.getValue();
        if(sourceGraph==null || sourceGraph.isEmpty()){
            getWindow().showNotification("No source graph selected",
                    "Please select a source graph for us to map elements from",
                    Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
        }
        if(vocabulary==null || vocabulary.isEmpty()){
            getWindow().showNotification("No vocabulary",
                    "Please provide a vocabulary for the mapping",
                    Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
        }
        String mappingRules=(String) mappingInput.getValue();
        if(mappingRules==null || mappingRules.isEmpty()){
            getWindow().showNotification("No mapping rules",
                    "Please provide mapping rules",
                    Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
        }

        try{
            File result;
            if(mappingFashion.equals(MAP_TEXT)){
                result=performMappingFromText(sourceGraph,mappingRules,vocabulary);
            }else{
                result=performMappingFromSPARQL(sourceGraph,mappingRules,vocabulary);
            }

            presentResultForDownload(result);
        }catch (IOException e){
            getWindow().showNotification("Mapping failed", "Could not perform the mapping, the message was: "+
                    e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    //* opens a download to the temporary file
    protected void presentResultForDownload(File result){
        try {
            final DownloadStream stream = new DownloadStream(new DeletingFileInputStream(result),"mime","mapping-result.ttl");
            StreamResource download = new StreamResource(new StreamResource.StreamSource() {
                public InputStream getStream() {
                    return stream.getStream();
                }
            },"mapping-result.ttl", getApplication());
            stream.setCacheTime(1000);
            getWindow().open(download);
        } catch (FileNotFoundException e) {
            getWindow().showNotification("Error creating download",
                    "Sorry, we could not set up the download for the mapping results. The error message was: "+
                            e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    /**
     * Maps the items in the source graph to the given target vocabulary according to the provided mapping. Enters the
     * results in a temporary file that will be presented for download and removed upon success/fail.
     * @param sourceGraph a graph in the repository to fetch the result from
     * @param mappingText the mapping as a turtle string
     * @param targetVocabulary the vocabulary to transform to
     * @throws IOException a problem occured while contacting the repository or while creating the temporary file
     * @return a temporary file that holds the result of the mapping
     */
    public File performMappingFromText(String sourceGraph, String mappingText, String targetVocabulary) throws IOException {
        // R2r requires a file or a sparql endpoint.
        File tempMappingFile = File.createTempFile("R2RmappingFile", ".ttl");
        String path = tempMappingFile.getAbsolutePath();

        // add content to file
        FileWriter writer = new FileWriter(tempMappingFile);
        writer.write(mappingText);
        writer.close();

        File result;
        try {
            Repository mappingRepository = Repository.createFileOrUriRepository(path);
            result = performMapping(sourceGraph, mappingRepository, targetVocabulary);

            if(!tempMappingFile.delete()){
                throw new IOException("Could not delete the file holding the temporary mapping");
            }
        } catch (TurtleParseException e){
            throw new IOException("Could not parse mapping: "+e.getMessage());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

        return result;
    }

    /**
     * Maps the items in the source graph to the given target vocabulary according to the provided mapping graph.
     * @param sourceGraph a graph in the repository to fetch the result from
     * @param mappingGraph the location of the mapping as a graph in the local endpoint
     * @param targetVocabulary the vocabulary to transform to
     * @throws IOException
     * @return a temporary file that holds the result of the mapping
     */
    public File performMappingFromSPARQL(String sourceGraph, String mappingGraph, String targetVocabulary) throws IOException {
        Repository mappingRepository = Repository.createSparqlEndpointRepository(state.getHostNameWithoutPort()+"/sparql",mappingGraph);
        return performMapping(sourceGraph,mappingRepository,targetVocabulary);
    }

    /**
     * Performs the actual mapping with the mapping being present in the mapping repository, be it from a file or from a sparql endpoint
     */
    private File performMapping(String sourceGraph, Repository mappingRepos, String targetVocabulary) throws IOException{
        //TODO should endpoint be configured?
        Source in = new SparqlEndpointSource(state.getHostNameWithoutPort()+"/sparql", sourceGraph);

        File result=File.createTempFile("mapping-result",".ttl");
        Output out = new NTriplesOutput(new BufferedWriter(new FileWriter(result)));

        // Transform: The output data is written to LabelToName_Output.nt
        Mapper.transform(in, out, mappingRepos, targetVocabulary);

        // Close the Output object to flush and close stream/printer
        out.close();

        return result;
    }

    /**
     * Deletes the given file on close
     */
    private class DeletingFileInputStream extends FileInputStream {
        private File file;
        public DeletingFileInputStream(File file) throws FileNotFoundException {
            super(file);
            this.file=file;
        }

        @Override
        public void close() throws IOException {
            super.close();
            if(!file.delete()){
                throw new IOException("Could not delete the temporary file in the deleting file input stream");
            }
        }
    }
}
