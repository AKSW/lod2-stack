package eu.lod2;

import com.turnguard.webid.tomcat.security.WebIDUser;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.openrdf.model.impl.URIImpl;

/**
 * The LoginStatus class is a component that shows the username of the currently
 * logged in user if such a user exists. In that case, the component also offers
 * the user a logout button. If no user is currently logged in, the component
 * shows a login button.
 */
public class LoginStatus extends HorizontalLayout implements LOD2DemoState.LoginListener {
    //* the state of the application
    protected LOD2DemoState state;
    //* the component that should show the login screen when the user clicks the login button
    protected AbstractLayout targetComponent;

    /**
     * creates a new LoginStatus component that is supplied with the given
     * application state and the component to show the login screen in.
     */
    public LoginStatus(LOD2DemoState state, AbstractLayout target){
        this.state=state;
        this.targetComponent=target;
        this.state.addLoginListener(this);
        this.setSpacing(true);
    }

    //* re-renders this component based on the user that is currently logged in
    public void render(){
        WebIDUser user = this.state.getUser();

        this.removeAllComponents();

        if(user!=null){
            this.createUserInfo(user);
        }else{
            this.createLoginButton();
        }
    }

    /**
     * shows the user that is currently logged in and allows him to log out
     * pre: user is not null
     */
    protected void createUserInfo(WebIDUser user){
        String username=null;
        try{
            username=user.get(new URIImpl("http://xmlns.com/foaf/0.1/name")).iterator().next().stringValue();
        }catch (Exception e){
            //assume username is empty
        }
        if(username==null || username.length()==0){
            username=user.getURI().toString();
        }
        Label name=new Label("Logged in as: "+username);
        name.setContentMode(Label.CONTENT_TEXT);
        Button logout= new Button("Log out");

        logout.setStyleName("currentgraphlabel");
        logout.setDescription("Click here to log out");
        logout.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                // log out user
                state.setUser(null);
            }
        });
        this.addComponent(name);
        this.addComponent(logout);
    }

    /**
     * creates a button that allows loggin in a new user. This button redirects the
     * main page to the user information page, which is preceded by an authenticator
     */
    protected void createLoginButton(){
        Button login= new Button("Log in");
        login.setStyleName("currentgraphlabel");
        login.setDescription("Click here to log in");
        login.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                // move application to the user information page
                targetComponent.removeAllComponents();
                targetComponent.addComponent(new Authenticator(new UserInformation(state), state));
            }
        });

        this.addComponent(login);
    }

    //* remove the listener on detach
    public void detach(){
        this.state.removeLoginListener(this);
        super.detach();
    }

    //* when the logged in user changes, the component should re-render
    public void notifyLogin(WebIDUser user) {
        // user can be obtained using state,
        this.render();
    }
}
