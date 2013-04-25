package eu.lod2;

import com.turnguard.webid.tomcat.security.WebIDUser;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

/**
 * This class can be used to register actions on a given component into the provenance graph
 */
public class ActionRecorder extends VerticalLayout implements DecoratorComponent {
    //* the target component to register actions on
    private AbstractComponent target;
    //* the toolURI to register an action for
    private String toolURI;
    //* the state of the application
    private LOD2DemoState state;

    final private static String actionType="http://lod2.eu/ref#action";

    //* immediately attaches the action recorder to the given target
    public ActionRecorder(AbstractComponent target, String toolURI, LOD2DemoState state){
        this.state=state;
        this.attach(target, toolURI);
        this.addComponent(target);
        target.setSizeFull();
    }

    /**
     * Attaches the action recorder to the given target with the given toolURI. If the recorder was already
     * attached to some other target, it is detached first
     * @param target the target to listen on
     * @param toolURI the URI to register an action for
     */
    public void attach(AbstractComponent target, String toolURI){
        if(this.target==null){
            this.detach();
        }

        this.target=target;
        this.toolURI=toolURI;

        this.addListener(new LayoutEvents.LayoutClickListener() {
            public void layoutClick(LayoutEvents.LayoutClickEvent layoutClickEvent) {
                logAction(layoutClickEvent);
            }
        });
    }

    private void logAction(Component.Event event){

        WebIDUser user=state.getUser();
        String userURI;
        if(user!=null){
            userURI = user.getURI().toString();
        }else{
            userURI = "http://lod2.eu/provenance/anonymous";
        }
        try {

            RepositoryConnection connection = state.rdfStore.getConnection();
                ValueFactory valueFact= connection.getValueFactory();

            URI action=this.generateActionURI();
            HashSet<Statement> statements=new HashSet<Statement>();
            statements.add(valueFact.createStatement(action, new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), new URIImpl(actionType)));
            GregorianCalendar cal=new GregorianCalendar();
            cal.setTime(new Date());
            statements.add(valueFact.createStatement(action,new URIImpl("http://lod2.eu/provenance/ref#executionTime"),
                    valueFact.createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal))));
            statements.add(valueFact.createStatement(action, new URIImpl("http://lod2.eu/provenance/ref#usesTool"), new URIImpl(this.toolURI)));
            statements.add(valueFact.createStatement(action, new URIImpl("http://lod2.eu/provenance/ref#actor"), new URIImpl(userURI)));
            String target=state.getCurrentGraph();
            if(target!=null){
                //TODO note: we cannot be entirely sure about the target of the action. User might cheat and use the tool on some other graph
                statements.add(valueFact.createStatement(action, new URIImpl("http://lod2.eu/provenance/ref#target"), new URIImpl(target)));
            }
            for(Statement statement: statements){
                connection.add(statement,new URIImpl(state.getProvenanceGraph()));
            }
        } catch (DatatypeConfigurationException e) {
                // cannot happen
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException("Sorry, we could not add the provenance information for your action to the store, please contact the development team: "+e.getMessage());
        }

    }

    /**
     * Generates a URI for an action that is probably unique
     */
    public URI generateActionURI(){
        String uriBase = "http://lod2.eu/id/action/";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        String uniquenessBestEffort= format.format(new Date())+"-"+Math.random();
        try {
            // assuming no collisions on uri. If collision -> tough luck.
            return new URIImpl(uriBase+new BigInteger(1,MessageDigest.getInstance("MD5").digest(uniquenessBestEffort.getBytes("UTF-8"))).toString(16));
        } catch (Exception e){
            // cannot happen
            throw new RuntimeException(e);
        }
    }

    public AbstractComponent getLeafComponent() {
        if(target instanceof DecoratorComponent){
            return ((DecoratorComponent)target).getLeafComponent();
        }else{
            return target;
        }
    }
}
