package eu.lod2;

import com.vaadin.ui.AbstractComponent;

/**
 * For building a delegation chain of components, Should be used to extend components obviously, but cannot have
 * multiple inheritance
 */
public interface DecoratorComponent {
    //* returns the final component in hte chain of decorator components
    public AbstractComponent getLeafComponent();
}
