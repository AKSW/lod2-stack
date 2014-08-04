
package eu.lod2.lod2testsuite.pages;

import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.Navigator;
import eu.lod2.lod2testsuite.configuration.TestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import static org.testng.AssertJUnit.*;

/**
 *
 * @author Stefan Schurischuster
 */
public class UnifiedViewsPage extends Page {
    private WebDriver driver;
    private BasicFunctions bf;
    private Navigator navigator;
    private Actions driverActions;   
    

    public UnifiedViewsPage()  {
        this.driver = TestCase.driver;
        this.bf = TestCase.bf;
    } 
    
    /**
     * 
     * @param frameIdentifier 
     *          Identifier for the frame that layers LODMS.
     */
    public UnifiedViewsPage(By frameIdentifier)  {
        super(frameIdentifier);
        this.driver = TestCase.driver;
        this.bf = TestCase.bf;
        this.navigator = TestCase.navigator;
        this.driverActions = TestCase.driverActions;
    }
    
    /**
     * Creates a new pipeline.
     * 
     * @param name
     *          The name of the pipeline.
     * @param description
     *          The description of the pipeline.
     * @param visibility 
     *          Can be one of the following values: Private, Public, Public (ReadOnly).
     */
    public void createPipeline(String name, String description, String visibility)  {
        navigateToTopMenu("Pipelines");
        bf.bePatient(1200);
        
    }
    
    /**
     * Logs into unified views.
     * 
     * @param user
     * @param pw 
     */
    public void login(String user, String pw)  {
        // Check if already logged in
        if(!bf.isElementVisibleAfterWait(By.xpath("//h1[.='Login']"), 2))  {
            logger.info("Already logged into unifiedviews. Skipping login.");
            return;
        }
        
        bf.waitUntilElementIsVisible("Could not find username input field for logging into unified views.",
                By.xpath("//input[@type='text']"),frameIdentifier).sendKeys(user);
        bf.waitUntilElementIsVisible("Could not find username input field for logging into unified views.",
                By.xpath("//input[@type='password']"),frameIdentifier).sendKeys(pw);
        
        bf.getVisibleElement("Could not find Login button.", 
                By.xpath("//div/span[contains(@class,'button')][.='Login']")).click();
        assertFalse(bf.isElementVisible(By.xpath("//div[contains(@class,'loginError')]")));
        bf.waitUntilElementIsVisible("Could not find unified views home screen.", 
                By.xpath("//div[contains(@class,'v-slot v-slot-viewLayout')]"
                + "/descendant::a[contains(@href,'Introduction')]"),frameIdentifier);
        logger.info("Successfuly logged in.");
    }
    
    /**
     * Helper method. 
     * Navigates to a top menu entry.
     * 
     * @param menuTitle 
     *      The exact title of the top menu.
     */
    private void navigateToTopMenu(String menuTitle)  {
        bf.waitUntilElementIsVisibleFast("Could not find main menu entry: "+menuTitle, 
                By.xpath("//span[contains(@class,'v-menubar-menuitem')][.='Pipelines']"), 
                frameIdentifier).click();
        
    }
}
