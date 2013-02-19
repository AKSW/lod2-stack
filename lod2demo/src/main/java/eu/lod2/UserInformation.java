package eu.lod2;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;

import java.util.Arrays;
import java.util.List;

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
        LOD2DemoState.User user= this.state.getUser();
        if(user == null){
            this.showMessage("warning","user was null");
            // no user present, show message and return
            window.showNotification(
                    "No user logged in!",
                    "Sorry, there is currently no valid user that is logged in. Please log in and try again.",
                    com.vaadin.ui.Window.Notification.TYPE_ERROR_MESSAGE);

            return;
        }

        final Form form= new UserForm(user,this.state);

        List<String> properties= Arrays.asList(new String[] {"username", "firstName", "lastName", "email","organization"});
        form.setVisibleItemProperties(properties);

        form.setFormFieldFactory(new DefaultFieldFactory() {
            public Field createField(Item item, Object propertyId, Component uiContext) {
                TextField tf=(TextField) super.createField(item, propertyId,uiContext);
                tf.setWidth("400px");
                tf.setRequired(true);
                tf.setRequiredError("This is a required field");
                return tf;
            }
        });

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

    public class UserForm extends Form {
        private LOD2DemoState.User user;
        private LOD2DemoState state;

        public UserForm(LOD2DemoState.User user, LOD2DemoState state){
            super();
            this.user=user;
            this.state=state;

            this.setCaption("User details");
            this.setWriteThrough(false);
            this.setInvalidCommitted(false);

            BeanItem<LOD2DemoState.User> userItem= new BeanItem<LOD2DemoState.User>(user);
            this.setItemDataSource(userItem);
        }

        @Override
        public void commit(){
            super.commit();
            LOD2DemoState.User newUser=this.user;
            state.updateUser(newUser);
        }
    }

}
