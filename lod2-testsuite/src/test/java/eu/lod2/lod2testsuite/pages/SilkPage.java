/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.lod2.lod2testsuite.pages;

import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.Navigator;
import eu.lod2.lod2testsuite.configuration.TestCase;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Stefan Schurischuster
 */
public class SilkPage extends Page {
    private static Logger logger = Logger.getLogger(SilkPage.class);
    
    private WebDriver driver;
    private BasicFunctions bf;
    private Navigator navigator;
    
    /**
     * 
     * @param frameIdentifier 
     *          Identifier for the frame that layers Silk.
     */
    public SilkPage(By frameIdentifier)  {
        super(frameIdentifier);
        this.driver = TestCase.driver;
        this.bf = TestCase.bf;
        this.navigator = TestCase.navigator;
    }
    
    /**
     * pre: silk is opened.
     * @param projectTitle 
     */
    public void createProject(String projectTitle)  {
               
        bf.waitUntilElementIsVisibleFast("Could not find project add button.", 
                By.id("newproject"), frameIdentifier).click();
        // Type title
        bf.waitUntilElementIsVisibleFast("Could not find project title input field.", 
                By.id("projectName"), frameIdentifier).sendKeys(projectTitle);
        bf.waitUntilElementIsVisibleFast("Could not find project title input field.", 
                By.id("projectName"), frameIdentifier).sendKeys(Keys.ENTER);
        // Check for project
        bf.waitUntilElementIsVisibleFast("Could not find project after create.", 
                By.id("project_"+projectTitle), frameIdentifier);
    }
    
    /**
     * pre: silk is opened; project exists.
     *
     * @param projectTitle
     */
    public void deleteProject(String projectTitle) {
        By projectIdentifier = By.id("project_" + projectTitle);
        // Check if already exists and delete if necessary.
        bf.waitUntilElementIsVisibleFast("Could not find project after create.",
                projectIdentifier, frameIdentifier);
        // Click delete
        bf.waitUntilElementIsVisibleFast("Could not find delete button for " + projectTitle,
                By.xpath("//*[@id='project_" + projectTitle
                + "']//*[@title='Remove project']"), frameIdentifier).click();
        bf.waitUntilElementIsVisibleFast("Delete dialog did not pop up. ",
                By.xpath("//*[@class='ui-dialog-buttonset']/button[1]"), frameIdentifier).click();

        bf.waitUntilElementDisappears("Could not find project after create.",
                projectIdentifier);
    }
}
