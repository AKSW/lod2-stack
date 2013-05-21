package eu.lod2;

import com.turnguard.webid.tomcat.security.WebIDUser;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;
import java.util.Set;

/**
 * This is the class that takes care of verifying that the user credentials are fine
 * before the user gains access to the protected component of the vertical layout.
 *
 * With the use of the webid, the user is no longer presented with a login screen if his credentials do not match,
 * In stead, he receives a message that he does not have access to the protected module.
 *
 * Note that the authenticator is only to be used for items that are not solely protected by URL, as in such cases, the
 * normal webid authentication will suffice.
 *
 * The authenticator can be subclassed to show a custom screen when the authentication fails. Use the
 * eu.lod2.Authenticator#showAuthenticationFailed() method for this.
 */
public class Authenticator extends VerticalLayout implements LOD2DemoState.LoginListener, DecoratorComponent {

    //* the component that is protected by this authenticator.
    protected final AbstractComponent protectedComponent;

    /**
     * the component that is shown if the user does not have the required access rights.
     * By default, this component is null, meaning that a simple authentication refused message is shown explaining
     * to the user why he does not have access.
     */
    protected AbstractComponent accessDeniedComponent=null;

    //* the application state
    protected final LOD2DemoState state;
    //* the roles that are allowed to access the protected component
    private Set<String> acceptedRoles=null;

    /**
     * Creates a new authenticator component. Requires a protected component to
     * be passed in. The protected component can only be accessed if the user is
     * authenticated in a correct way.
     *
     * This constructor uses the default list of accepted users, which is currently null
     */
    public Authenticator(AbstractComponent protectedComponent,LOD2DemoState state){
        this(protectedComponent,new HashSet<String>(), state);
    }

    /**
     * Creates a new authenticator component. Requires a protected component to
     * be passed in. The protected component can only be accessed if the user is
     * authenticated in a correct way.
     *
     * Only users that have a role in the accepted roles list can access this component
     * Sets the backup component to the default component
     */
    public Authenticator(AbstractComponent protectedComponent, Set<String> acceptedRoles, LOD2DemoState state){
        this.acceptedRoles=acceptedRoles;
        this.state=state;
        this.protectedComponent=protectedComponent;
        this.state.addLoginListener(this);
        this.setAccessDeniedComponent(null);
        // when a login listener gets added, a notification is provided immediately. No need to check for login
        // explicitly
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
     * Returns the accepted roles of this component. The set of roles is cloned for convenience.
     */
    public Set<String> getAcceptedRoles(){
        return new HashSet<String>(this.acceptedRoles);
    }

    /**
     * This function checks whether there is currently a logged in user. If so,
     * it opens this authenticator's protected component immediately. If not, the
     * user is first prompted for his/her password.
     */
    protected void checkLoggedIn(){
        WebIDUser currentUser=state.getUser();
        // only show the protected component if a user is logged in correctly
        String reason=checkUser(currentUser);
        if(reason==null){
            this.buildProtectedComponent();
        }else{
            this.showAuthenticationFailed(reason);
        }
    }

    /**
     * Checks whether the given user is allowed to use the protected component. Returns a string holding the reason of
     * failure if denied. Returns null otherwise.
     * @param user the user to check
     */
    public String checkUser(WebIDUser user){
        if(user!=null){
            try{
                String userName=user.getURI().toString();
                RepositoryConnection connection=state.rdfStore.getConnection();
                TupleQueryResult results=
                        connection.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?role FROM <" +
                                state.getUserGraph() + "> " +
                        "WHERE { " +
                        "<" + userName + "> <http://schema.turnguard.com/webid/2.0/core#hasRole> ?role" +
                        "}").evaluate();
                while(results.hasNext()){
                    BindingSet bindings=results.next();
                    String role=bindings.getBinding("role").getValue().stringValue();
                    if(role!=null && this.getAcceptedRoles().contains(role)){
                        return null;
                    }
                }

                return "The user does not have the required privileges to access this component.";
            } catch (RepositoryException e) {
                throw new IllegalStateException("Because of a server configuration error, your user could not be " +
                        "authenticated. Please contact the administrator. The error message was: "+e.getMessage());
            } catch (MalformedQueryException e) {
                throw new IllegalArgumentException("There was an error in the way the server checks your " +
                        "authentication. Please contact the development team. The error message was: "+e.getMessage());
            } catch (NullPointerException e){
                return "There was an issue with your certificate. Please make sure you use a " +
                        "valid webID certificate.";
            } catch (QueryEvaluationException e) {
                throw new IllegalArgumentException("There was an error in the way the server checks your " +
                        "authentication. Please contact the development team. The error message was: "+e.getMessage());
            }

        }else{
            return "No user was found";
        }
    }

    //* shows a screen to the user that notifies him that authentication has failed. The reason for failure is provided
    protected void showAuthenticationFailed(String reason){
        // safety: remove all components anyway
        this.removeAllComponents();

        //TODO check logout functionality?
        AbstractComponent backup=this.getAccessDeniedComponent();
        if(backup==null){
            this.addComponent(new Label("We are sorry, you are not authorized to view this page. The reason that " +
                    "access was denied is: "+reason));
        }else{
            this.addComponent(backup);
            backup.setSizeFull();
        }
    }

    /**
     *  add the protected component of the authenticator as a child.
     */
    protected void buildProtectedComponent(){
        // safety: remove all components anyway
        this.removeAllComponents();

        this.addComponent(this.protectedComponent);
        this.protectedComponent.setSizeFull();
    }

    public void notifyLogin(WebIDUser user){
        // during the checkLoggedIn process, the current user is checked in either case,
        // no need to pass it in again.
        this.checkLoggedIn();
    }

    public AbstractComponent getLeafComponent() {
        if(protectedComponent instanceof DecoratorComponent){
            return ((DecoratorComponent)protectedComponent).getLeafComponent();
        }else{
            return protectedComponent;
        }
    }

    public AbstractComponent getAccessDeniedComponent() {
        return accessDeniedComponent;
    }

    public void setAccessDeniedComponent(AbstractComponent accessDeniedComponent) {
        this.accessDeniedComponent = accessDeniedComponent;
    }

}
