package eu.lod2;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Iframe integrator for the sindice web linkage validator
 * Deprecated temporarily
 */
public class WebLinkageValidator extends CustomComponent
{

    // reference to the global internal state
    private LOD2DemoState state;

    public WebLinkageValidator(LOD2DemoState st) {

        // The internal state
        state = st;

        Embedded browser = new Embedded();
        try {
            URL url = new URL("http://demo.sindice.net/dataset/?raw=1");

            browser = new Embedded("", new ExternalResource(url));
            browser.setType(Embedded.TYPE_BROWSER);
            browser.setSizeFull();
            //panel.addComponent(browser);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        };

        // The composition root MUST be set
        setCompositionRoot(browser);
    }

    // propagate the information of one tab to another.
    public void setDefaults() {
    };

};
