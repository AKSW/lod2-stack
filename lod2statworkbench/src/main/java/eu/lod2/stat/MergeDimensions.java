package eu.lod2.stat;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import eu.lod2.LOD2DemoState;
import eu.lod2.utils.CodeListPicker;
import eu.lod2.utils.DataCubePicker;
import eu.lod2.utils.DimensionPicker;
import eu.lod2.utils.VisualComponent;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This component allows the user to merge dimensions according to a code list or according to a graph
 */
public class MergeDimensions extends VerticalLayout implements VisualComponent{

    // note: these variables are initialized with their default values after creation of subclass, so after call of render
    private RepositoryConnection connection;
    private boolean showLabels = false;
    private String selectedCube = null;
    private String selectedDimension = null;
    private String selectedCodeList = null;
    private String currentQuery = "";
    protected LOD2DemoState state;

    public MergeDimensions(LOD2DemoState state) {
        this.state=state;
        this.setShowLabels(true);
    }

    public void render() {
        this.removeAllComponents();

        AbstractComponent dimselect= this.buildDimensionSelect();
        AbstractComponent listselect = this.buildCodeListSelect();
        AbstractComponent queryselect = this.buildCreateQuerySelect();
        AbstractComponent options= this.buildOptions();
        dimselect.setHeight("100%");
        listselect.setHeight("100%");
        queryselect.setHeight("100%");
        options.setHeight("100%");

        this.addComponent(dimselect);
        this.addComponent(listselect);
        this.addComponent(queryselect);
        this.addComponent(options);

        this.setExpandRatio(dimselect,0.20f);
        this.setExpandRatio(listselect,0.20f);
        this.setExpandRatio(queryselect,0.45f);
        this.setExpandRatio(options,0.15f);

        this.requestRepaintAll();
    }

    @Override
    public void detach(){
        try{
            this.connection.close();
            this.connection = null;
        }catch (Exception e){
            // at least we tried
        }
    }

    //* lazilly get connection
    protected RepositoryConnection ensureConnection() {
        if(this.connection==null){
            try{
                this.connection=this.state.getRdfStore().getConnection();
            }catch (Exception e){
                getWindow().showNotification("Could not get connection to backend triple store",
                        "Sorry, no connection to the backend triple store could be established. " +
                                "Please check your configuration. The full error message was: "+
                                e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            }
        }
        return connection;
    }

    protected AbstractComponent buildDimensionSelect(){
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(true);
        layout.setSizeFull();
        layout.setSpacing(true);
        Panel panel = new Panel("1) Select Dimension to Reconcile",layout);

        final DataCubePicker dcPicker = new DataCubePicker("DataCube", ensureConnection(), state.getCurrentGraph(), this.selectedCube);
        dcPicker.setWidth("100%");
        dcPicker.setImmediate(true);
        dcPicker.setShowURIs(!this.showLabels);

        final DimensionPicker picker = new DimensionPicker("Dimension", ensureConnection(), state.getCurrentGraph(),this.selectedDimension, this.selectedCube);
        picker.setImmediate(true);
        picker.setShowURIs(!this.showLabels);
        picker.setWidth("100%");

        dcPicker.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                setSelectedCube(dcPicker.getSelection());
            }
        });

        picker.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                setSelectedDimension(picker.getSelection());
            }
        });
        layout.addComponent(dcPicker);
        layout.addComponent(picker);

        layout.setExpandRatio(dcPicker,0.5f);
        layout.setExpandRatio(picker,0.5f);

        return panel;
    }

    protected AbstractComponent buildCodeListSelect(){
        Panel panel = new Panel("2) Select Code List");

        final CodeListPicker picker = new CodeListPicker("CodeList", ensureConnection(), null,this.selectedCodeList);
        picker.setImmediate(true);
        picker.setShowURIs(!this.showLabels);
        picker.setWidth("100%");

        picker.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                setSelectedCodeList(picker.getSelection());
            }
        });
        panel.addComponent(picker);
        ((VerticalLayout)panel.getContent()).setExpandRatio(picker, 1.0f);
        return panel;
    }

    //* creates the query panel
    protected AbstractComponent buildCreateQuerySelect(){
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        Panel panel = new Panel("3) Reconcilliation Querries",layout);
        layout.setSpacing(true);
        layout.setMargin(true);

        VerticalLayout left = new VerticalLayout();
        left.setWidth("100%");
        left.setHeight("100%");
        left.setSpacing(true);
        VerticalLayout right = new VerticalLayout();
        right.setWidth("100%");

        layout.addComponent(left);
        layout.addComponent(right);
        layout.setExpandRatio(left,0.75f);
        layout.setExpandRatio(right,0.25f);

        final TextArea area = new TextArea();
        area.setImmediate(true);
        area.addListener(new Property.ValueChangeListener(){
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                currentQuery=(String)area.getValue();
            }
        });
        area.setValue(this.currentQuery);
        area.setWidth("100%");
        area.setHeight("100%");
        left.addComponent(area);
        left.setExpandRatio(area,1.0f);

        Button executeQuery = new Button("Run");
        executeQuery.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                handleQueryExecute();
            }
        });
        left.addComponent(executeQuery);

        Collection<Button> queryButtons = this.createQueryButtons();

        for(Button button : queryButtons){
            right.addComponent(button);

            button.setWidth("100%");
        }

        return panel;
    }

    protected void handleQueryExecute(){
        RepositoryConnection connection = ensureConnection();
        try{

            GraphQueryResult result=connection.prepareGraphQuery(QueryLanguage.SPARQL, this.currentQuery).evaluate();
            getWindow().showNotification("Query Executed","The query has been executed, the result was: "+ result.toString() );
        }catch (Exception e){
            getWindow().showNotification("Query Execution Failed",
                    "Sorry, we could not run this SPARQL query. This may be because this component only handles SPARQL update queries. The error message was: "+e.getLocalizedMessage(),
                    Window.Notification.TYPE_WARNING_MESSAGE);
        }

    }

    //* creates a set of buttons used to set up a query
    protected Collection<Button> createQueryButtons(){
        ArrayList<Button> buttons = new ArrayList<Button>();

        buttons.add(createLinkerQueryButton());
        buttons.add(createCleanerQueryButton());

        return buttons;
    }

    protected Button createLinkerQueryButton(){
        Button button = new Button("Linking to CodeList");
        final String targetGraph = state.getCurrentGraph();

        button.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                setCurrentQuery("INSERT {\n" +
                        "    GRAPH <"+targetGraph+"> {\n" +
                        "      ?obs <"+selectedDimension+"> ?code. \n" +
                        "      ?code <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+selectedDimension+">.\n" +
                        "      ?code <http://www.w3.org/2000/01/rdf-schema#label> ?label.\n" +
                        "    }\n" +
                        "  }\n" +
                        "  WHERE {\n" +
                        "    GRAPH <"+targetGraph+"> {\n" +
                        "    ?obs a <http://purl.org/linked-data/cube#Observation>. \n" +
                        "    ?obs <"+selectedDimension+"> ?val.\n" +
                        "    ?val <http://www.w3.org/2002/07/owl#sameAs> ?code.\n" +
                        "    }\n" +
                        "    ?code <http://www.w3.org/2004/02/skos/core#inScheme> <"+selectedCodeList+"> .\n" +
                        "    ?code <http://www.w3.org/2000/01/rdf-schema#label> ?label.\n" +
                        "  }");
            }
        });
        return button;
    }

    protected Button createCleanerQueryButton(){
        Button button = new Button("Clean old Dimensions");
        final String targetGraph = state.getCurrentGraph();
        button.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                setCurrentQuery("DELETE {\n" +
                        "    GRAPH <" + targetGraph + "> {\n" +
                        "      ?obs <" + selectedDimension + "> ?val.\n" +
                        "      ?val <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + selectedDimension + ">.\n" +
                        "    }\n" +
                        "  } \n" +
                        "  WHERE {\n" +
                        "    GRAPH <" + targetGraph + "> {\n" +
                        "      ?obs a <http://purl.org/linked-data/cube#Observation>. \n" +
                        "      ?obs <" + selectedDimension + "> ?val.\n" +
                        "    }\n" +
                        "    OPTIONAL {\n" +
                        "      GRAPH ?o { ?val <http://www.w3.org/2004/02/skos/core#inScheme> <" + selectedCodeList + "> }\n" +
                        "    }\n" +
                        "    FILTER (!bound(?o))\n" +
                        "  }");
            }
        });
        return button;
    }

    protected AbstractComponent buildOptions(){
        Panel panel = new Panel("Configuration");

        CheckBox showCubeLabels=new CheckBox("Show labels", this.showLabels);
        panel.addComponent(showCubeLabels);

        showCubeLabels.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                String valueString=valueChangeEvent.getProperty().getValue().toString();
                if(valueString.equals("true")){
                    setShowLabels(true);
                }else{
                    setShowLabels(false);
                }
            }
        });

        return panel;
    }

    public void setShowLabels(boolean value){
        this.showLabels = value;
        this.render();
    }

    public void setSelectedCube(String cubeUri){
        this.selectedCube = cubeUri;
        this.render();
    }

    public void setSelectedDimension(String dimensionUri){
        this.selectedDimension = dimensionUri;
        this.render();
    }

    public void setSelectedCodeList(String listUri){
        this.selectedCodeList = listUri;
        this.render();
    }

    public void setCurrentQuery(String query){
        this.currentQuery = query;
        this.render();
    }
}
