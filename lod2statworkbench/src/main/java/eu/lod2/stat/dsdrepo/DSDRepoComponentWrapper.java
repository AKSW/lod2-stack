/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.lod2.stat.dsdrepo;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import eu.lod2.ConfigurationTab;
import eu.lod2.LOD2DemoState;

/**
 *
 * @author vukm
 */
public class DSDRepoComponentWrapper extends CustomComponent implements LOD2DemoState.CurrentGraphListener {
    
    private LOD2DemoState state;
    private AbstractLayout target;
    private final HorizontalLayout mainContainer;
    
    public DSDRepoComponentWrapper(LOD2DemoState state, AbstractLayout target){
        this.state = state;
	this.target = target;
        mainContainer = new HorizontalLayout();
        mainContainer.setSizeFull();
	mainContainer.setSpacing(true);
	setCompositionRoot(mainContainer);
    }
    
    private void refresh(){
        mainContainer.removeAllComponents();
        String currentGraph = state.getCurrentGraph();
        if (currentGraph == null || currentGraph.isEmpty()){
            VerticalLayout l = new VerticalLayout();
            l.setSizeFull();
            mainContainer.addComponent(l);
            Label message=new Label("No graph is currently selected. You can select one below:");
            l.addComponent(message);
            l.setExpandRatio(message, 0.0f);
            ConfigurationTab config=new ConfigurationTab(this.state);
            l.addComponent(config);
            l.setExpandRatio(config, 2.0f);
            l.setComponentAlignment(message,Alignment.TOP_LEFT);
            l.setComponentAlignment(config,Alignment.TOP_LEFT);

            return;
        }

        mainContainer.addComponent(new DSDRepoComponent(state.getRdfStore(), currentGraph));
    }
    
    public void notifyCurrentGraphChange(String graph) {
        refresh();
    }

    @Override
    public void attach() {
            state.addCurrentGraphListener(this);
            refresh();
    }

    @Override
    public void detach() {
            state.removeCurrentGraphListener(this);
    }
    
}
