package eu.lod2;

import com.turnguard.webid.tomcat.security.WebIDUser;
import com.vaadin.ui.*;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;
import java.util.Set;

/**
 * Shows the known information on the user in an editable fashion.
 */
public class UserInformation extends VerticalLayout {

    LOD2DemoState state;

    public UserInformation(LOD2DemoState state){
        this.state=state;
    }

    //* when the component is attached, update the component's user
    public void attach(){
        this.render();
    }

    //* renders this component again, taking the current user into account
    public void render(){
        Window window=getWindow();
        if(window==null){
            // no window means rendering is pretty useless... Wait for attach to call use again.
            this.showMessage("warning", "window was null");
            return;
        }

        this.removeAllComponents();

        // fetch the current user from the state
        WebIDUser user= this.state.getUser();
        if(user == null){
            this.showMessage("warning","user was null");
            // no user present, show message and return
            window.showNotification(
                    "No user logged in!",
                    "Sorry, there is currently no valid user that is logged in. Please log in and try again.",
                    com.vaadin.ui.Window.Notification.TYPE_ERROR_MESSAGE);

            return;
        }

        final UserForm form= new UserForm(user,this.state);

        this.addComponent(form);

        Button saver = new Button("Save", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                try {
                    form.commit();
                    getWindow().showNotification("Save Successful","Your new information has been saved successfully.",
                            Window.Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception e) {
                    getWindow().showNotification("Could not save information",e.getMessage(),
                            Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        HorizontalLayout buttons=new HorizontalLayout();
        buttons.addComponent(saver);
        buttons.setMargin(true);

        this.addComponent(saver);
    }

    //* simple notification function that allows showing a number of messages in a single panel
    public void showMessage(String title, String ... messages){
        String result="";
        for(String m:messages){
            result+=m+"<br><br>";
        }
        getWindow().showNotification(title,result, Window.Notification.TYPE_WARNING_MESSAGE,true);
    }

    public class UserForm extends VerticalLayout{
        private WebIDUser user;
        private LOD2DemoState state;
        private int fieldWidth=400;

        public UserForm(WebIDUser user, LOD2DemoState state){
            super();

            this.user=user;
            this.state=state;

            this.fillForm();
        }

        /**
         * Fills the userform with the information that is currently known about the user
         */
        public void fillForm(){
            String userURI= user.getURI().toString();

            Panel userInfo=new Panel("User information");
            this.addComponent(userInfo);

            TextField uri=new TextField("WebID URI", userURI);
            uri.setReadOnly(true);
            uri.setWidth(this.fieldWidth+"px");

            Field name=this.ensureSingleUserField("http://xmlns.com/foaf/0.1/name", "Name", true);

            userInfo.addComponent(uri);
            userInfo.addComponent(name);

            Panel rolesInfo=new Panel("User Roles");
            this.addComponent(rolesInfo);

            Set<Component> roles = this.tryForFields("SELECT ?value FROM <"+state.getUserGraph()+"> " +
                    "Where { <"+userURI+"> <http://schema.turnguard.com/webid/2.0/core#hasRole> ?value }",null, true);

            for(Component role : roles){
                rolesInfo.addComponent(role);
            }
        }

        /**
         * Creates a single field for the given value, even if there is more than one value for the field or if there is
         * no value for it. It picks some field in the first case and creates an empty one in the second.
         * Both cases will print a warning
         */
        private Field ensureSingleUserField(String predicate, String fieldName, boolean readonly){
            Set<Field> fields=tryForUserFields(predicate, fieldName, readonly);
            Field theField;
            if(fields.size()==0){
                System.out.println("The user profile for "+user.getURI()+ " held no value for the predicate "+predicate);
                theField=new TextField(fieldName);
            }else{
                theField=fields.iterator().next();
            }
            if(fields.size()>1){
                System.out.println("The user profile for "+user.getURI()+ " held "+ fields.size() +"values for the predicate "+predicate);
            }
            theField.setWidth(this.fieldWidth+"px");
            theField.setReadOnly(readonly);
            return theField;
        }

        /**
         * Attempts to get the values for the given predicate and turns these values into fields
         * @param predicate the string representation of the uri to create fields for
         * @param fieldName the name of the field(s) to create
         * @param readonly whether or not the user can change the fields
         * @return the set of fields that have been found for the predicate
         */
        private Set<Field> tryForUserFields(String predicate, String fieldName, boolean readonly){
            HashSet<Field> fields=new HashSet<Field>();
            try{
                HashSet<Value> values=user.get(new URIImpl(predicate));

                for(Value val : values){
                    TextField field = new TextField(fieldName,val.stringValue());
                    field.setWidth(this.fieldWidth+"px");
                    field.setReadOnly(readonly);
                    fields.add(field);
                }

            }catch (Exception e){
                // no fields found
            }
            return fields;
        }

        /**
         * Builds fields for all results of the query with as name fieldname
         * @param query the query to get the values for, the query should have only one result binding, called value
         * @param fieldName the label to use for the fields
         *                  if the fieldName is null, readonly is assumed and the a textbox is returned
         * @param readonly whether or not the field can be changed
         */
        private Set<Component> tryForFields(String query, String fieldName, boolean readonly){
            HashSet<Component> resultSet = new HashSet<Component>();
            String errorMessage= "We are sorry, we could not fetch the necessary information for the field "+fieldName+
                    ". The exception that was thrown was: ";
            try{
                RepositoryConnection con = state.rdfStore.getConnection();
                TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
                TupleQueryResult result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet bindingSet = result.next();
                    Value value = bindingSet.getValue("value");
                    String stringValue =value==null?"":value.stringValue();
                    Component field;
                    if(fieldName!=null){
                        field = new TextField(fieldName,stringValue);
                        field.setReadOnly(readonly);
                    } else {
                        field = new Label(stringValue);
                    }
                    field.setWidth(fieldWidth+"px");
                    resultSet.add(field);
                }
                return resultSet;
                // can't do anything about the following exceptions, show a fiendly message to the user to contact dev team
            } catch (RepositoryException e) {
                throw new IllegalStateException(errorMessage+e);
            } catch (QueryEvaluationException e) {
                throw new IllegalArgumentException(errorMessage+e);
            } catch (MalformedQueryException e) {
                throw new IllegalArgumentException(errorMessage+e);
            }
        }

        public void commit(){
            //TODO
        }
    }
}