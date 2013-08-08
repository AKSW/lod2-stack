package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.TestCase;
import org.openqa.selenium.By;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class contains functional tests concerning authoring of
 * the lod2 - stack.
 * All TestCases are documented here:
 * https://grips.semantic-web.at/display/LOD2/LOD2Stack+Test+Cases+-+Authoring
 * 
 * @author Stefan Schurischuster
 * @email s.schurischuster@semantic-web.at
 */
public class Authoring extends TestCase {
    
    private static int createCount = 0;
    /**
     * TC 001 - 006.
     */
    @Test
    @Parameters({ "username", "knowledgeBaseTitle", "knowledgeBaseUri", "importUri", "resourceTitle", "inctanceTitle"})
    public void ontoWiki(String username, String knowledgeBaseTitle, String knowledgeBaseUri, String importUri, String resourceTitle, String instanceTitle)  {
        
        navigator.navigateTo(new String[] {
            "Authoring", 
            "OntoWiki"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ontowiki')]"), 
                By.id("application"));
        
        // Login into onto wiki
        logIntoOntoWiki(username,"");
        
        // Create new Knowledge base
        createNewKnowledgeBase(knowledgeBaseTitle,knowledgeBaseUri,"");
        
        // Add data
        addDataToKnowledgeBaseViaRDFFromWeb(knowledgeBaseUri, importUri);
        
        //TODO: drop off sparql query?
        
        // Add resource
        addResource(resourceTitle);
        
        //Add instance
        addInstanceToResource(resourceTitle, instanceTitle);
    }
    
    /**
     * TC 007.
     * pre: Knowledge base to delete exists
     * post: Knowledge base is deleted.
     */
    @Test
    @Parameters({ "username","knowledgeBaseUri" })
    public void deleteKnowledgeBase(String username, String knowledgeBaseUri)  {
        By frameIdentifier = By.xpath("//iframe[contains(@src,'ontowiki')]");
        if(bf.isElementVisible(frameIdentifier))  {
            logger.info("Already on correct page. Skipping navigation");
        } else {
            navigator.navigateTo(new String[] {
            "Authoring", 
            "OntoWiki"});  
        }
             
        bf.checkIFrame(frameIdentifier, By.id("application"));
        // Perform login if necessary
        logIntoOntoWiki(username, "");
        // Delete KB
        navigateToContextMenuEntry(knowledgeBaseUri,"Delete Knowledge");
        // Check for delted
        By element = By.xpath("//div[@class='section-sidewindows']//a[" +bf.xpathEndsWith("@about", knowledgeBaseUri) +"]");
        bf.waitUntilElementDisappears("Knowledgebase was not correctly deleted. It is still"
                + "visible after delete.", element);        
    }
    
    /**
     * TC 002.
     */
    @Test
    public void publishToCkan()  {
        navigator.navigateTo(new String[] {
            "Authoring", 
            "Publish to CKAN"});  

        bf.waitUntilElementIsVisible("Could not find CKAN input fields.", 
                By.cssSelector("input.v-textfield"));
        //TODO: further testing
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
    private void createNewKnowledgeBase(String title, String uri, String optionID)  {
        createCount++;
        assertTrue("Could not create Knowledge Base. Delete and Create did not work.",
                createCount < 3);
        
        bf.waitUntilElementIsVisible("Could not find Edit Menu item.", By.xpath(
                "//div[@id='modellist']//ul[@class='menu clickMenu']/li[contains(.,'Edit')]")).click();
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
        
        bf.waitUntilElementIsVisible("Knwoledge Base "+ title+" was not correctly created. "
                + "Success message was not displayed." , By.xpath("//p[contains(@class,'success')]"));
        assertTrue("New Knowledge Base with title: "+title +" was not added to existing Knowledge Bases.",
                bf.isElementVisible(By.xpath("//a[@about='" +uri+ "']")));
        logger.info("Successfully created a new knowledge base.");
    }
    
    /**
     * Adds data to a existing knowledge base
     * @param knowledgeBaseUri 
     *          identifies the knowledge base to use.
     */
    private void addDataToKnowledgeBaseViaRDFFromWeb(String knowledgeBaseUri, String importUri)  {
        //navigateToKBContextMenuEntry(knowledgeBaseUri, "Add");
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
        bf.waitUntilElementIsVisible("Data form "+ importUri+" was not correctly added."
                + "Success message was not displayed." , By.xpath("//p[contains(@class,'success')]"));
        logger.info("Successfully added data to knowledge base.");
    }
    
    /**
     * Adds a resource to an existing knowledge base.
     * 
     * @param resourceLabel 
     *              The label of the resource to add.
     */
    private void addResource(String resourceLabel)  {
        bf.waitUntilElementIsVisible("Could not find Edit Menu item.", By.xpath(
                "//div[@id='navigation']//ul[@class='menu clickMenu']/li[contains(.,'Edit')]")).click();
        bf.waitUntilElementIsVisibleFast("Could not find menu Create Knowledge Base menu entry.", 
                By.xpath("//ul[@class='innerBox']//a[contains(.,'Add')]")).click();
        bf.waitUntilElementIsVisibleFast("Add-dialog did not pop up.", 
                By.id("rdfauthor-view"));
        bf.getVisibleElement(By.xpath("//*[starts-with(@id,'literal-value-')]")).sendKeys(resourceLabel);
        // Click submit
        bf.getVisibleElement("Could not find create resource button.", 
                By.id("rdfauthor-button-submit")).click();
        bf.waitUntilElementIsVisible("Data"+ resourceLabel+" was not correctly added.", 
                By.xpath("//div[@id='navigation-content']//a[" +bf.xpathEndsWith("@about", resourceLabel) +"]"));
        logger.info("Successfully added resource with label:" +resourceLabel);
    }
    
    /**
     * Adds a instance to a existing resource.
     * 
     * @param resource
     * @param label 
     */
    private void addInstanceToResource(String resource, String label)  {
        By rscsLocation = By.xpath("//div[@id='navigation-content']//a[" +bf.xpathEndsWith("@about", resource) +"]");
        bf.waitUntilElementIsVisible("Could not find resource with title: "+resource, 
                rscsLocation);
        navigateToContextMenuEntry(resource, "Create");
        
        bf.waitUntilElementIsVisibleFast("Add-dialog did not pop up.", 
                By.id("rdfauthor-view"));
        bf.getVisibleElement(By.xpath("//*[starts-with(@id,'literal-value-')]")).sendKeys(resource);
        // Click submit
        bf.getVisibleElement("Could not find create resource button.", 
                By.id("rdfauthor-button-submit")).click();
        bf.bePatient(2000);
        bf.getVisibleElement("Could not find resource with title: "+resource, 
                rscsLocation).click();
        bf.waitUntilElementIsVisibleFast("Adding of instance: "+label+" was not successful."
                + "It is not listed when clicking the resource:" +resource, 
                By.xpath("//div[@class='innercontent']//a[" +bf.xpathEndsWith("@about", resource) +"]"));       
    }
    
    /**
     * Navigates to a knowledge base context menu entry.
     * 
     * @param ident
     *          Identifying text from the @about field of the html element.
     * @param entry 
     *          The entry to click from the context menu.
     */
    private void navigateToContextMenuEntry(String ident, String entry)  {
        By element = By.xpath("//div[@class='section-sidewindows']//a[" +bf.xpathEndsWith("@about", ident) +"]");
        bf.waitUntilElementIsVisible("Could not find knowledge base with uri: "+ ident, 
                element).click();
        bf.bePatient(2000);
        driverActions.moveToElement(bf.waitUntilElementIsVisibleFast(
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
    private void logIntoOntoWiki(String username, String password)  {
        // Check if already logged in
        if(!bf.isElementVisibleAfterWait(By.id("login"),2))  {
            logger.info("Already logged into ontoWiki. Skipping login.");
            return;
        }
        
        bf.waitUntilElementIsVisible("Could not find username input field for logging into onto wiki.",
                By.id("username")).sendKeys(username);
        
         // Skipping password input
        
        
        bf.getVisibleElement("Could not find Login button.", By.id("locallogin")).click();
        logger.info("Successfuly logged in.");
    }
}