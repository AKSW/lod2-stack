package eu.lod2.utils;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import eu.lod2.ConfigurationTab;
import eu.lod2.LOD2DemoState;

/**
 * Decorator that enforces a current graph has been selected
 */
public class CurrentGraphEnforcer extends VerticalLayout implements LOD2DemoState.CurrentGraphListener, VisualComponent {

    private LOD2DemoState state;
    AbstractComponent comp = null;
    public CurrentGraphEnforcer(AbstractComponent component, LOD2DemoState state) {
        this.state = state;
        comp= component;
        this.render();
    }

    public void render() {
        this.removeAllComponents();
        if(state.getCurrentGraph() == null){
            Panel panel = new Panel();
            this.addComponent(panel);
            panel.addComponent(new Label("Please select a graph first using the " +
                    "interface below:"));
            panel.addComponent(new ConfigurationTab(this.state));
        }else{
            this.addComponent(comp);
            comp.setSizeFull();
            try{
                ((VisualComponent) comp).render();
            }catch (ClassCastException e){
                // ok so not a visual component
            }
        }
    }

    @Override
    public void attach(){
        super.attach();
        this.state.addCurrentGraphListener(this);
    }

    @Override
    public void detach(){
        super.detach();
        this.state.removeCurrentGraphListener(this);
    }

    public void notifyCurrentGraphChange(String graph) {
        this.render();
    }
}
