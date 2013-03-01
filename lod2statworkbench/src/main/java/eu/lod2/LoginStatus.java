package eu.lod2;

import com.vaadin.ui.*;

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
        this.setMargin(true);
        this.targetComponent=target;
        this.state.addLoginListener(this);
    }

    //* re-renders this component based on the user that is currently logged in
    public void render(){
        LOD2DemoState.User user = this.state.getUser();

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
    protected void createUserInfo(LOD2DemoState.User user){
        Label name=new Label(user.getUsername());
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
    public void notifyLogin(LOD2DemoState.User user) {
        // user can be obtained using state,
        this.render();
    }
}
