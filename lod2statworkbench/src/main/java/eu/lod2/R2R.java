package eu.lod2;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class R2R extends VerticalLayout {
    public R2R(){
        this.render();
    }

    public void render(){
        this.removeAllComponents();
        //TODO
        this.addComponent(new Label("Question: do we integrate a package that does not have its own " +
                "user interface/debian package, " +
                "because this simply means we build there interface and package ourselves"));
    }
}
