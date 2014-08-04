/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.lod2.lod2testsuite.pages;

import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.TestCase;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import static org.testng.AssertJUnit.*;
/**
 *
 * @author Stefan Schurischuster
 */
public class OntoWikiPage extends Page {
    private static Logger logger = Logger.getLogger(OntoWikiPage.class);
    
    private static int createCount = 0;
    private WebDriver driver;
    private BasicFunctions bf;
    
    
    public OntoWikiPage()  {
        this.driver = TestCase.driver;
        this.bf = TestCase.bf;
    } 
    /**
     * 
     * @param frameIdentifier 
     */
    public OntoWikiPage(By frameIdentifier)  {
        super(frameIdentifier);
        //this.driver = driver;
        this.driver = TestCase.driver;
        this.bf = TestCase.bf;
    }

    /**
     * Adds data to a existing knowledge base
     * @param knowledgeBaseUri 
     *          identifies the knowledge base to use.
     */
    public void addDataToKnowledgeBaseViaRDFFromWeb(String knowledgeBaseUri, String importUri)  {
        bf.bePatient();
        navigateToContextMenuEntry(knowledgeBaseUri, "Add");
        bf.waitUntilElementIsVisible("Could not find import form"
                + " when trying to add data to a knowledge base.",
                By.id("addmodel"));
        // Choose correct import type
        bf.waitUntilElementIsVisible("Could not find import rdf from web option", 
                By.id("import-basicimporter-rdfweb")).click();
        
        // Submit
        bf.getVisibleElement("Could not find data import submit button.", 
                By.id("addmodel")).click();
        
        bf.waitUntilElementIsVisible("Could not find location input for import from web.", 
                By.id("location-input")).sendKeys(importUri);
        
        // Submit again
        bf.getVisibleElement("Can not find submit button.", 
                By.id("importdata")).click();
        bf.checkIFrame(frameIdentifier, By.cssSelector("div.content"));
        bf.waitUntilElementIsVisible("Data form "+ importUri+" was not correctly added."
                + "Success message was not displayed." , By.xpath("//p[contains(@class,'success')]"));
        logger.info("Successfully added data to knowledge base.");
    }
    
    /**
     * Adds a resource to an existing knowledge base.
     * pre: logged into ontoWiki; Knowledge Base exists and is visible.
     *
     * @param knowledgeBaseUri
     * @param resourceLabel 
     *              The label of the resource to add.
     */
    public void addResource(String knowledgeBaseUri, String resourceLabel)  {
        By kbLocator = By.xpath("//div[@class='section-sidewindows']//a[" 
                +bf.xpathEndsWith("@about", knowledgeBaseUri) +"]");
        bf.waitUntilElementIsVisible("Could not find knowledge base to add resource.",
                kbLocator, frameIdentifier).click();
        bf.waitUntilElementIsVisible("Could not find Edit Menu item.", By.xpath(
                "//div[@id='navigation']//ul[@class='menu clickMenu']/li[contains(.,'Edit')]")).click();
        bf.waitUntilElementIsVisibleFast("Could not find menu Create Knowledge Base menu entry.", 
                By.xpath("//ul[@class='innerBox']//a[contains(.,'Add')]")).click();
        bf.waitUntilElementIsVisibleFast("Add-dialog did not pop up.", 
                By.id("rdfauthor-view"));
        bf.getVisibleElement(By.xpath("//*[starts-with(@id,'literal-value-')]")).sendKeys(resourceLabel);
        bf.bePatient(700);
        // Click submit
        bf.getVisibleElement("Could not find create resource button.", 
                By.id("rdfauthor-button-submit")).click();
        if(bf.isElementVisible(By.id("rdfauthor-button-submit")))  {
            bf.getVisibleElement("Could not find create resource button.", 
                By.id("rdfauthor-button-submit")).click();
        }

        bf.waitUntilElementIsVisible("Data"+ resourceLabel+" was not correctly added.", 
                By.xpath("//div[@id='navigation-content']//a[" 
                +bf.xpathEndsWith("@about", resourceLabel) +"]"),
                frameIdentifier);
        logger.info("Successfully added resource with label:" +resourceLabel);
    }
    
    /**
     * Adds a instance to a existing resource.
     *
     * @param knowledgeBaseUri 
     * @param resource
     * @param label 
     */
    public void addInstanceToResource(String knowledgeBaseUri, String resource, String label)  {
        By kbLocator = By.xpath("//div[@class='section-sidewindows']//a[" 
                +bf.xpathEndsWith("@about", knowledgeBaseUri) +"]");
        By rscsLocation = By.xpath("//div[@id='navigation-content']//a[" 
                +bf.xpathEndsWith("@about", resource) +"]");

        bf.waitUntilElementIsVisibleFast("Could not find knowledge base to add resource.",
                kbLocator, frameIdentifier).click();
        bf.waitUntilElementIsVisibleFast("Could not find resource with title: "+resource, 
                rscsLocation, frameIdentifier).click();
        
        navigateToContextMenuEntry(resource, "Create");
        
        bf.waitUntilElementIsVisibleFast("Add-dialog did not pop up.", 
                By.id("rdfauthor-view"));
        bf.getVisibleElement(By.xpath("//*[starts-with(@id,'literal-value-')]")).sendKeys(label);
        bf.bePatient(1000);
        // Click submit
        bf.getVisibleElement("Could not find create resource button.", 
                By.id("rdfauthor-button-submit")).click();
        if(bf.isElementVisible(By.id("rdfauthor-button-submit")))  {
            bf.getVisibleElement("Could not find create resource button.", 
                By.id("rdfauthor-button-submit")).click();
        }     
        // Otherwise a modal dialog pops up when the click comes to fast.
        bf.bePatient(2000);
        //bf.waitUntilElementIsVisible("Could not find resource with title: "+resource, 
        //        rscsLocation, frameIdentifier).click();
        
        bf.waitUntilElementIsVisible("Could not find resource with title: "+resource, 
                rscsLocation, frameIdentifier).click();
        
        bf.checkIFrame(frameIdentifier, By.xpath("//div[@class='innercontent']"));
        
        bf.waitUntilElementIsVisible("Adding of instance: "+label+" was not successful."
                + "It is not listed when clicking the resource:" +resource, 
                By.xpath("//div[@class='innercontent']//a[starts-with(normalize-space(.),'" +label+ "')]"));       
        //bf.waitUntilElementIsVisibleFast("Adding of instance: "+label+" was not successful."
        //        + "It is not listed when clicking the resource:" +resource, 
        //        By.xpath("//div[@class='innercontent']//a[" +bf.xpathEndsWith("@about", label) +"]"));       
    }

    /**
     * login is required.
     * 
     *@param title 
     *          The title of the knowledge base to create.
     * @param uri
     *          A uri for the knowledge base.
     * @param optionID 
     *          This String contains the element id of the radio button that is
     *          used to determine data import during knowledge base creation.
     *          If this field is left empty the default configuration is used.
     * 
     */
    public void createNewKnowledgeBase(String title, String uri, String optionID)  {
        createCount++;
        assertTrue("Could not create Knowledge Base. Delete and Create did not work.",
                createCount < 3);
        
        bf.waitUntilElementIsVisible("Could not find Edit Menu item.", By.xpath(
                "//div[@id='modellist']//ul[@class='menu clickMenu']/li[contains(.,'Edit')]"),
                frameIdentifier).click();
        bf.waitUntilElementIsVisibleFast("Could not find menu Create Knowledge Base menu entry.", 
                By.xpath("//ul[@class='innerBox']//a[.='Create Knowledge Base']")).click();
        
        // Type title and uri
        bf.waitUntilElementIsVisible(By.xpath("//input[@name='title']")).sendKeys(title);
        bf.getVisibleElement(By.xpath("//input[@name='modeluri']")).sendKeys(uri);
        
        // Leave radio buttons untouched.
        if(!optionID.isEmpty())  {
            bf.getVisibleElement("Could not find radio button with id:" +optionID, 
                    By.id(optionID)).click();
        }
        
        // Submit
        bf.getVisibleElement("Could not find 'Create Knowledge Base' submit button.", 
                By.id("createmodel")).click();
        
        if(bf.isElementVisibleAfterWait(By.xpath("//p[contains(@class,'error')]"),4)) {
            // Perform delete
            navigateToContextMenuEntry(uri,"Delete Knowledge");
        
            By element = By.xpath("//div[@class='section-sidewindows']//a[" 
                    +bf.xpathEndsWith("@about", uri) +"]");
            bf.waitUntilElementDisappears("Knowledgebase was not correctly deleted. It is still"
                + "visible after delete.", element);        
            // Restart test case:
            createNewKnowledgeBase(title,uri,optionID);
        }
        bf.checkIFrame(frameIdentifier, By.cssSelector("div.innercontent"));
        bf.waitUntilElementIsVisible("Knwoledge Base "+ title+" was not correctly created. "
                + "Success message was not displayed." , By.xpath("//p[contains(@class,'success')]"));
        assertTrue("New Knowledge Base with title: "+title +" was not added to existing Knowledge Bases.",
                bf.isElementVisible(By.xpath("//a[@about='" +uri+ "']")));
        logger.info("Successfully created a new knowledge base.");
    }    
    
    /**    
     * Deletes an existing Knowledge Base, or skips method if the
     * resource to delete does not exist.
     * 
     * @param knowledgeBaseUri 
     *          The uri of the Knowledge Base to delete.
     * 
     * post: Knowledge base is not visible.
     */
    public void deleteKnowledgeBase(String knowledgeBaseUri)  {
        By kbIdentifier = By.xpath("//div[@class='section-sidewindows']//a[" 
                +bf.xpathEndsWith("@about", knowledgeBaseUri) +"]");
        
        if(!bf.isElementVisibleAfterWait(kbIdentifier))  {
            logger.info("Could not find knowledge base to delete: "+knowledgeBaseUri);
            logger.info("Skipping delete.");
            return;
        }
        
        bf.checkIFrame(frameIdentifier, By.id("application"));
        navigateToContextMenuEntry(knowledgeBaseUri,"Delete Knowledge");
        // Check if delted
        bf.waitUntilElementDisappears("Knowledgebase was not correctly deleted. It is still"
                + "visible after delete.", kbIdentifier);        
    }
    
    
    
    /**
     * Navigates to a knowledge base context menu entry.
     * 
     * @param ident
     *          Identifying text from the @about field of the html element.
     * @param entry 
     *          The entry to click from the context menu.
     */
    public void navigateToContextMenuEntry(String ident, String entry)  {
        By element = By.xpath("//div[@class='section-sidewindows']//a[" +bf.xpathEndsWith("@about", ident) +"]");
        //By element = By.xpath("//div[@class='section-sidewindows']//a[starts-with(normalize-space(.),'" +ident+ "')]");
        bf.checkIFrame(frameIdentifier, element);
        
        bf.waitUntilElementIsVisible("Could not find knowledge base with uri: "+ ident, 
                element).click();
        bf.bePatient(2000);
        TestCase.driverActions.moveToElement(bf.waitUntilElementIsVisibleFast(
                "Could not find knowledge base with uri: "+ ident, 
                element)).build().perform();
        
        bf.waitUntilElementIsVisibleFast("Context menu button did not pop up.", 
                By.xpath("//a[" +bf.xpathEndsWith("@about", ident) +"]//span[@class='button']")).click();
        
        bf.waitUntilElementIsVisibleFast("Context menu did not pop up.", 
                By.className("contextmenu-enhanced"));
        // Click entry
        bf.getVisibleElement("Could not find context entry: "+entry, 
                By.xpath("//div[@class='contextmenu-enhanced']//a[contains(.,'"+entry+"')]")).click();
        bf.bePatient(1000);
     }
    
    /**
     * Logs into ontoWiki. 
     * pre: ontoWiki frontend must be visible.
     * post: logged in with username.
     * 
     * @param username
     * @param password 
     */
    public void logIntoOntoWiki(String username, String password)  {
        // Check if already logged in
        if(!bf.isElementVisibleAfterWait(By.id("login"),2))  {
            logger.info("Already logged into ontoWiki. Skipping login.");
            return;
        }
        
        bf.waitUntilElementIsVisible("Could not find username input field for logging into onto wiki.",
                By.id("username")).sendKeys(username);
        
        bf.waitUntilElementIsVisible("Could not find password input field for logging into onto wiki.",
                By.id("password")).sendKeys(password);
        
        bf.getVisibleElement("Could not find Login button.", By.id("locallogin")).click();
        logger.info("Successfuly logged in.");
    }
    
    
   /**
    * Submits a sparql query and checks the result.
    * 
    * pre: ontoWiki sparql editor is opened.
    * @param graph
    *           The graph to use. If empty the predefined graph will be used.
    * @param query 
    *           The query to use.
    */
    public void submitSparqlQuery(String graph, String query, String expectedResult)  {
        if(graph.isEmpty())  {
            //TODO: Choose different graph
        }
        /*
        //TODO: Can not clear input field --> until then leave query untouched
        
        bf.bePatient(3000);
        bf.checkIFrame(frameIdentifier, By.xpath("//form"));
        // Clear input field.
        bf.getExisitingElement("Could not find input field.", By.id("inputField")).clear();
       
        // Type query
        WebElement inputField = bf.waitUntilElementIsVisibleFast(
                "Textfield that displays query is not visible.",
                By.cssSelector("div.CodeMirror div textarea"), frameIdentifier);
        
        bf.setValueViaJavaScript(inputField, "");
        inputField.sendKeys(query);
        */
        // Click submit
        bf.getVisibleElement("Could not find the submit button.", By.xpath("//div[@class='messagebox']"
               + "/div[@class='toolbar']/a[@class='button submit']")).click();

        bf.waitUntilElementIsVisibleFast("Could not find expected result: "+expectedResult, 
                By.xpath("//td[normalize-space(.)='" +expectedResult+ "']"), frameIdentifier);
    }
}
