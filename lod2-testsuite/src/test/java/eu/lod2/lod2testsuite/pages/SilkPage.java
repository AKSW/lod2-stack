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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

/**
 *
 * @author Stefan Schurischuster
 */
public class SilkPage extends Page {
    private static Logger logger = Logger.getLogger(SilkPage.class);
    
    private WebDriver driver;
    private BasicFunctions bf;
    private Navigator navigator;
    private Actions driverActions;
    
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
        this.driverActions = TestCase.driverActions;
    }
    
    /**
     * pre: silk is opened.
     * @param projectTitle 
     */
    public void createProject(String projectTitle)  {        
        bf.waitUntilElementIsVisibleFast("Could not find project add button.", 
                By.cssSelector("#newproject .ui-button"), frameIdentifier).click();
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
     * Deletes an existing silk project.
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
                By.xpath("//*[@id='dialog']/../descendant::*[@class='ui-button-text'][1]"), 
                frameIdentifier).click();

        bf.waitUntilElementDisappears("Could not find project after create.",
                projectIdentifier);
    }

    //* helper function that adds a prefix in the silk test case
    public void addSilkPrefix(String prefix){
        bf.waitUntilElementIsVisible("Could not find prefix dialog",
                By.xpath("//*[local-name() = 'div'][@id='editPrefixesDialog']//*[local-name() = 'span'][@class='ui-button-text'][text()='add']")).click();
        driver.findElement(
                By.xpath("//*[local-name() = 'div'][@id='editPrefixesDialog']//*[local-name() = 'tr'][last()-1]//*[local-name() = 'input'][@type='text'][@value='']")).sendKeys(prefix);
    }

    //* helper function that adds a silk sparql source to the given project
    public void addSilkSPARQLSource(String graph, String projectTitle, String endpointName, String endpointURI){
        getSilkActionButton("Source",projectTitle).click();

        getSilkDialogInput("Source","name").sendKeys(endpointName);
        getSilkDialogInput("Source","endpointURI").sendKeys(endpointURI);
        getSilkDialogInput("Source","graph").sendKeys(graph);

        driver.findElement(By.xpath("//*[local-name() = 'input'][@value='Save']")).click();

        bf.waitUntilElementIsVisible("Could not find the source "+endpointName+ " which should have been created",
                By.xpath("//*[local-name()='span'][@class='source']/*[local-name()='span'][text()='"+endpointName+"']"));
    }

    /**
     * 
     * @param project
     * @param name
     * @param source
     * @param sourceRestrictions
     * @param target
     * @param targetRestriction
     * @param linkType 
     */
    public void addLinkingTask(String project, String name, String source, String sourceRestrictions, String target, String targetRestriction, String linkType)  {
        getSilkDialogInput("Linking Task", "Name").sendKeys(name);
        Select sourceSelect = new Select(driver.findElement(By.xpath("//*[@title='Source dataset']")));
        sourceSelect.selectByValue(source);
        getSilkDialogInput("Linking Task", "Source restrictions").sendKeys(sourceRestrictions);
    }
    
    /**
     * 
     * @param projectTitle 
     */
    public void openCollapsedProject(String projectTitle)  {
        if(bf.isElementVisible(By.xpath(
                "//*[@id='project_"+projectTitle+"'][contains(@class,'expandable')]")))  {
           logger.info("Silk project is collapsed and will be expanded."); 
           
           driver.findElement(By.xpath("//*[@id='project_"+projectTitle+"'][contains(@class,'expandable')]"
                   + "/descendant::*[contains(@class,'hitarea')][1]")).click();
           bf.bePatient();
        } else  {
           logger.info("Silk project is already expanded. No click necessary."); 
        }
    }
    
    /**
     * 
     * @param project
     * @param task 
     */
    public void createLink(String project, String task)  {
        openCollapsedProject(project);
        bf.bePatient(2000);
        getProjectEntryActionButton(project, task, "Open").click();
        bf.waitUntilElementIsVisible("Could not find silk",By.id("droppable"), frameIdentifier);
       /*
        driverActions.clickAndHold(driver.findElement(By.xpath("//*[@id='source0']//*[contains(.,'custom')]")));
        driverActions.moveByOffset(100, 100);
        driverActions.release();
        //driverActions.moveToElement(driver.findElement(By.id("droppable")));
        driverActions.build().perform();
        bf.bePatient(150000);
        
        */
        bf.bePatient();
        
        driverActions.clickAndHold(driver.findElement(By.xpath("//*[starts-with(@id,'comparator')]//*[.='Equality']")));
        driverActions.moveByOffset(300, 0);
        driverActions.release();
        driverActions.build().perform();
        
        bf.bePatient(10000);
        
    }
    
    
    /**
     * Helper function for the silk testcase that returns the action button with the 
     * given name for the given project.
     * 
     * @param name
     * @param project
     * @return 
     */
    //* 
    private WebElement getSilkActionButton(String name, String project){
        return bf.waitUntilElementIsVisibleFast("Could not find silk action button with name: "+name,
                By.xpath("//*[local-name() = 'span'][contains(@class, 'folder')]/" +
                "*[local-name() = 'div'][@class='actions'][preceding-sibling::" +
                "*[local-name() = 'span'][contains(@class,'label')][text()[contains(.,'"+project+"')]]]//" +
                "*[local-name() = 'span'][text()='"+name+"']"),frameIdentifier);
    }
    
    /**
     * 
     * @param project
     * @param entry
     * @param buttonTitle
     * @return 
     */
    private WebElement getProjectEntryActionButton(String project, String entry, String buttonTitle) {  
        return bf.waitUntilElementIsVisibleFast("Could not find silk entry action button with name: "+buttonTitle, 
                By.xpath("//*[contains(@id,'" +project+ "_" +entry+ "')]//*[@class='actions']"
                + "//*[local-name() = 'span'][contains(.,'" +buttonTitle+ "')]"), frameIdentifier);
    }

    //* helper function that returns the input with the given name for the silk source input form
    private WebElement getSilkDialogInput(String dialogName, String name){
        String sourceDivId=driver.findElement(By.xpath("//*[local-name() ='div']" +
                "[.//*[local-name()='span'][@class = 'ui-dialog-title'][text()='"+dialogName+"']]/" +
                "*[local-name()='div'][@id]")).getAttribute("id");
        return driver.findElement(By.xpath("//*[@id='"+sourceDivId+"']//" +
                "*[local-name() = 'td'][preceding-sibling::" +
                "*[local-name() = 'td'][text()[contains(.,'"+name+"')]]]//*[local-name()='input']"));
    }
    

    //* helper function for the silk component that creates a new sparql output with the given name
    private void addSilkSparqlOutput(String name, String project, String graph, String virtUser, String virtPwd){
        getSilkActionButton("Output",project).click();

        // select sparql output option
        WebElement select =driver.findElement(By.xpath("//*[local-name()='select'][./*[local-name()='option'][text()='SPARQL/Update']]"));
        select.sendKeys("SPARQL");
        bf.bePatient(200);

        // NOTE: the following code is a hack that works around this issue
        // find the div above the select that has an id for access
        //String id=driver.findElement(By.xpath("//*[./*[local-name()='select']" +
        //"[./*[local-name()='option'][text()='SPARQL/Update']]]")).getAttribute("id");

        getSilkDialogInput("Output","name").sendKeys(name);
        getSilkDialogInput("Output","uri").sendKeys("http://localhost:8890/sparql");
        getSilkDialogInput("Output","login").sendKeys(virtUser);
        getSilkDialogInput("Output","password").sendKeys(virtPwd);
        getSilkDialogInput("Output","graphUri").sendKeys(graph);

        driver.findElement(By.xpath("//*[local-name()='div'][preceding-sibling::" +
                "*[./*[local-name()='span'][@class='ui-dialog-title'][text()='Output']]]//" +
                "*[local-name() = 'input'][@value='Save']")).click();

        bf.waitUntilElementIsVisible("Could not find the source "+name+ " which should have been created",
                By.xpath("//*[local-name()='span'][@class='output']/*[local-name()='span'][text()='"+name+"']"));
    }

}
