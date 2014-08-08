
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
        bf.waitUntilElementIsVisible("Create pipeline button not found.", 
                By.xpath(getButtonIdentifier("Create pipeline")), 
                frameIdentifier).click();
        bf.waitUntilElementIsVisible("Pipeline details view did not show up.", 
                By.cssSelector("div.pipelineSettingsLayout"), 
                frameIdentifier);
        // Type name and description
        driver.findElement(By.cssSelector(
                "div.pipelineSettingsLayout input.v-textfield")).sendKeys(name);
        driver.findElement(By.cssSelector(
                "div.pipelineSettingsLayout textarea.v-textarea")).sendKeys(description);
        // Choose visibility
        driver.findElement(By.xpath(
                "//div[contains(@class,'pipelineSettingsLayout')]//label[.='"
                +visibility+ "']/../input")).click();
        // Save and close
        driver.findElement(By.xpath("//span[@class='v-button-wrap'][.='Save & Close']")).click();
        bf.waitUntilElementIsVisible("Could not find Pipeline after create: "+name, 
                By.xpath("//table[@class='v-table-table']//td[@class='v-table-cell-content']"
                        + "[.='" +name+ "']"), frameIdentifier);
    }
    
    /**
     * Deletes a new pipeline.
     *
     * pre: pipeline exists.
     * post: pipeline is deleted; all schedules of this pipeline are removed too.
     * 
     * @param name
     *          The name of the pipeline.
     */
    public void deletePipeline(String name)  {
        navigateToTopMenu("Pipelines");
        bf.waitUntilElementIsVisible("Could not find Pipeline to delete: "+name, 
                By.xpath(getPipelineIdentifier(name)), frameIdentifier);
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
    
    
    private String getButtonIdentifier(String caption)  {
        return "//span[@class='v-button-wrap'][.='" +caption +"']";
    }
    
    private String getPipelineIdentifier(String name)  {
        return "//table[@class='v-table-table']//td[@class='v-table-cell-content']"
                        + "[.='" +name+ "']/..";
    }
    
    private String getPipelineButtonIdentifier(String pipeline, String buttonAltText)  {
        return getPipelineIdentifier(pipeline)+"//span[@class='v-button-wrap']";
    }
}
////table[@class='v-table-table']//td[@class='v-table-cell-content'][.='Testpipe']/..//span[@class='v-button-wrap']