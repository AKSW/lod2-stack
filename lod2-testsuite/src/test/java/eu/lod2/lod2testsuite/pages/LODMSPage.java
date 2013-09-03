
package eu.lod2.lod2testsuite.pages;

import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.Navigator;
import eu.lod2.lod2testsuite.configuration.TestCase;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 *
 * @author Stefan Schurischuster
 */
public class LODMSPage extends Page {
    private WebDriver driver;
    private BasicFunctions bf;
    private Navigator navigator;
    private Actions driverActions;   
    
        /**
     * 
     * @param frameIdentifier 
     *          Identifier for the frame that layers LODMS.
     */
    public LODMSPage(By frameIdentifier)  {
        super(frameIdentifier);
        this.driver = TestCase.driver;
        this.bf = TestCase.bf;
        this.navigator = TestCase.navigator;
        this.driverActions = TestCase.driverActions;
    }
    
    /**
     * 
     * @param directoryPath 
     */
    public void setDataDirectory(String directoryPath)  {
       WebElement input = bf.waitUntilElementIsVisibleFast("Could not find data directory input field.", 
               By.cssSelector("div.popupContent input"), frameIdentifier);
       input.sendKeys(directoryPath);
       driver.findElement(By.cssSelector("div.popupContent div.v-button")).click();
    }
}
