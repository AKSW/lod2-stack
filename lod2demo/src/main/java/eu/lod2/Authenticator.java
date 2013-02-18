package eu.lod2;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.VerticalLayout;

/**
 * This is the class that takes care of verifying that the user credentials are fine
 * before the user gains access to the protected component of the vertical layout.
 */
public class Authenticator extends VerticalLayout implements LOD2DemoState.LoginListener {

    //* the component that is protected by this authenticator.
    protected final AbstractComponent protectedComponent;
    //* the application state
    protected final LOD2DemoState state;

    /**
     * Creates a new authenticator component. Requires a protected component to
     * be passed in. The protected component can only be accessed if the user is
     * authenticated in a correct way.
     */
    public Authenticator(AbstractComponent protectedComponent,LOD2DemoState state){
        this.state=state;
        this.state.addLoginListener(this);
        this.protectedComponent=protectedComponent;
        // check whether the user is logged in and handle the result
        this.checkLoggedIn();
    }

    /**
     * when the component gets detached from the application, it should remove
     * itself from the list of listeners in the state.
     * Previous behaviour for this method is retained.
     */
    public void detach(){
        super.detach();
        this.state.removeLoginListener(this);
    }

    /**
     * This function checks whether there is currently a logged in user. If so,
     * it opens this authenticator's protected component immediately. If not, the
     * user is first prompted for his/her password.
     */
    protected void checkLoggedIn(){
        LOD2DemoState.User currentUser=state.getUser();
        // only show the protected component if a user is logged in correctly
        if(currentUser!=null){
            this.buildProtectedComponent();
        }else{
            this.showLoginScreen();
        }
    }

    //* shows the login screen to the user.
    protected void showLoginScreen(){
        // safety: remove all components anyway
        this.removeAllComponents();

        // then create the new component
        LoginForm login= new LoginForm();
        login.setWidth("100%");
        login.setHeight("300px");
        login.addListener(new LoginForm.LoginListener() {
            public void onLogin(LoginForm.LoginEvent event) {
                // let the state pick the new user from the provided information
                LOD2DemoState.User user = state.logIn(event.getLoginParameter("username"),
                        event.getLoginParameter("password"));
                // check if a user was found, if not, show a message to the user
                if(user==null){
                    getWindow().showNotification("Login Failed", "Sorry, the given combination of username " +
                            "and password was not found.");

                }
            }
        });
        this.addComponent(login);
    }

    /**
     *  add the protected component of the authenticator as a child.
     */
    protected void buildProtectedComponent(){
        // safety: remove all components anyway
        this.removeAllComponents();

        this.addComponent(this.protectedComponent);
    }

    public void notifyLogin(LOD2DemoState.User user){
        // during the checkLoggedIn process, the current user is checked in either case,
        // no need to pass it in again.
        this.checkLoggedIn();
    }
}
