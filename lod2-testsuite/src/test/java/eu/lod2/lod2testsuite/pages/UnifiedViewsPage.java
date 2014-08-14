
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
     * Copies an existing pipeline.
     * 
     * pre: Logged into unifiedviews; Pipeline to copy exists
     * post: Pipeline is doubled 
     * 
     * @param name 
     *          The name of the pipeline to copy.
     */
    public void copyPipeline(String name)  {
        navigateToTopMenu("Pipelines");
        
        bf.waitUntilElementIsVisible("Could not find Pipeline to copy: "+name, 
                By.xpath(getPipelineIdentifier(name)), frameIdentifier);
        bf.bePatient(1000);
        bf.waitUntilElementIsVisible(By.xpath(getPipelineButtonIdentifier(name,"copy"))).click();
        logger.info("Clicked copy");
        bf.waitUntilElementIsVisible("Confirm copy dialog did not pop up.", 
               bf.getInfoPopupLocator()).click();
        bf.waitUntilElementIsVisible(By.xpath(getPipelineButtonIdentifier(name,"copy"))).click();
        bf.waitUntilElementDisappears("Pipeline was not copied. Copy is not visible.", 
                By.xpath("//table[@class='v-table-table']//td[@class='v-table-cell-content']"
                        + "[contains(.,'" +name+ "')][contains(.,'Copy')]"));
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
        if (bf.isElementVisibleAfterWait(By.xpath(getPipelineIdentifier(name)))) {
            logger.info("Pipline with same title already exists:" + name);
            logger.info("Deleting exisitng Pipeline: " + name);
            deletePipeline(name);
        }
        
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
        bf.bePatient(1000);
        bf.waitUntilElementIsVisible(By.xpath(getPipelineButtonIdentifier(name,"trash"))).click();
        logger.info("Clicked delete");
        bf.waitUntilElementIsVisible("Confirm delete dialog did not pop up.", 
                By.xpath("//*[@class='popupContent']"
                        +getButtonIdentifier("Delete pipeline"))).click();
        bf.waitUntilElementDisappears("Pipeline was not deleted. It is still visible.", 
                By.xpath(getPipelineIdentifier(name)));
    }
    
    /**
     * Deletes a scheduler rule.
     *
     * pre: schedule rule exists.
     * post: schedule rule is deleted; all schedules of this schedule rule are removed too.
     * 
     * @param pipeline 
     *          The name of the pipeline
     * @param schedulerRule 
     *          The name of the scheduler rule or parts of it.
     */
    public void deleteScheduleRule(String pipeline, String schedulerRule)  {
        navigateToTopMenu("Scheduler");
        
        bf.waitUntilElementIsVisible("Could not find scheduled pipeline",
                By.xpath(getRuleButtonIdentifier(pipeline,schedulerRule,"trash"))).click();
        logger.info("Clicked delete");
        bf.bePatient(1000);
        bf.waitUntilElementIsVisible("Confirm delete dialog did not pop up.", 
                By.xpath("//*[@class='popupContent']"
                        +getButtonIdentifier("Delete"))).click();
        bf.waitUntilElementDisappears("Scheduler Rule was not deleted. It is still visible.", 
                By.xpath(getSchedulerRuleIdentifier(pipeline,new String[]{schedulerRule}))); 
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
     * Creates a schedule rule for a pipeline, to run after another pipeline.
     * pre: At least two pipelines exist
     * post: Pipeline is scheduled to run after another pipeline
     * 
     * @param pipelineToSchedule
     *          The name of the pipeline to be scheduled.
     * @param pipelineToRunBefore
     *          The name of the pipeline that must run before the pipeline to be scheduled.
     */
     public void schedulePipelineAfterAnotherPipeline(String pipelineToSchedule, String pipelineToRunBefore) {
        navigateToTopMenu("Pipelines");
        bf.waitUntilElementIsVisible("Could not find Pipeline to schedule: " + pipelineToSchedule,
                By.xpath(getPipelineIdentifier(pipelineToSchedule)), frameIdentifier);
        bf.bePatient(1000);
        bf.waitUntilElementIsVisible(By.xpath(getPipelineButtonIdentifier(pipelineToSchedule, "schedule"))).click();
        logger.info("Clicked schedule button");bf.waitUntilElementIsVisible("Could not choose to run pipline after another.",
                By.xpath("//*[@class='popupContent']//span[contains(@class,'v-select')]"
                        + "[contains(.,'after selected pipeline')]")).click();
        
        bf.waitUntilElementIsVisible("Could not choose to run pipline after another.",
                By.cssSelector("div.popupContent input.v-textfield")).sendKeys(pipelineToRunBefore);
        bf.bePatient(1000);
        bf.waitUntilElementIsVisible("Could not choose to run pipline after another.",
                By.cssSelector("div.popupContent  select.v-select-twincol-options :first-child")).click();
        driver.findElement(By.xpath("//*[@class='popupContent']" + getButtonIdentifier(">>"))).click();                       
        
        bf.waitUntilElementIsVisible("Could not schedule pipeline after: "+ pipelineToRunBefore,
                By.xpath("//*[@class='popupContent']//select[@class='v-select-twincol-selections']"
                        + "//option[.='" +pipelineToRunBefore+ "']"));
        
        driver.findElement(By.xpath("//*[@class='popupContent']" + getButtonIdentifier("Save"))).click();
        navigateToTopMenu("Scheduler");
        
        bf.waitUntilElementIsVisible("Could not find scheduled pipeline",
                By.xpath(getSchedulerRuleIdentifier(pipelineToSchedule, 
                        new String[]{"Run after", pipelineToRunBefore})));
     }
     
     
    

    /**
     * Helper method. Navigates to a top menu entry.
     *
     * @param menuTitle The exact title of the top menu.
     */
    private void navigateToTopMenu(String menuTitle) {
        bf.waitUntilElementIsVisibleFast("Could not find main menu entry: " + menuTitle,
                By.xpath("//span[contains(@class,'v-menubar-menuitem')][.='" + menuTitle + "']"),
                frameIdentifier).click();
        
    }
    
    private String getButtonIdentifier(String caption)  {
        return "//span[@class='v-button-wrap'][.='" +caption +"']";
    }
    
    private String getPipelineIdentifier(String name)  {
        return "//table[@class='v-table-table']//td[@class='v-table-cell-content']"
                        + "[.='" +name+ "']/..";
    }
    
    private String getSchedulerRuleIdentifier(String pipeline, String[] ruleParts)  {
        String start = "//table[@class='v-table-table']//td[@class='v-table-cell-content'][.='" 
                +pipeline+ "']/..//td[@class='v-table-cell-content']";
        for (String part : ruleParts)  {
            start += "[contains(.,'" +part+ "')]";
        }                                
        return start+"/..";
    }
    
    private String getPipelineButtonIdentifier(String pipeline, String pictureTitle)  {
        return getPipelineIdentifier(pipeline)+"//span[@class='v-button-wrap']/img[contains(@src,'"
                + pictureTitle +"')]";
    }
    
    private String getRuleButtonIdentifier(String pipeline, String rule, String pictureTitle)  {
        return getSchedulerRuleIdentifier(pipeline, new String[]{rule})+"//span[@class='v-button-wrap']/img[contains(@src,'"
                + pictureTitle +"')]";
    }
}