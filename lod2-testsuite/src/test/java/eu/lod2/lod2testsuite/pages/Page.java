/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.lod2.lod2testsuite.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;

/**
 *
 * @author Stefan Schurischuster
 */
public abstract class Page {
    protected static Logger logger = Logger.getLogger(Page.class);
    protected By frameIdentifier;
    
    /**
     * 
     * @param frameIdentifier 
     *              Identifies the iframe of this page.
     */
    public Page(By frameIdentifier)  {
        this.frameIdentifier = frameIdentifier;
    }
    
    public By getFrameIdentifier() {
        return frameIdentifier;
    }

    public void setFrameIdentifier(By frameIdentifier) {
        this.frameIdentifier = frameIdentifier;
    }
}
