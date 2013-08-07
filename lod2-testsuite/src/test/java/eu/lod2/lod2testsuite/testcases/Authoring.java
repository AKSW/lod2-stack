package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.TestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

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
    /**
     * TC 001 - 006.
     */
    @Test
    @Parameters({ "username"})
    public void ontoWiki(String username)  {
        String testTitle = "Test Knowledge Base";
        String testUri = "http://example.com/empty/";
        String dataUri  = "http://sebastian.tramp.name";
        String resourceTitle  = "TestResource";
        
        navigator.navigateTo(new String[] {
            "Authoring", 
            "OntoWiki"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ontowiki')]"), 
                By.id("username"));
        
        // Login into onto wiki
        bf.waitUntilElementIsVisible("Could not find username input field for logging into onto wiki.",
                By.id("username")).sendKeys(username);
        // Skipping password input
        bf.getVisibleElement("Could not find Login button.", By.id("locallogin")).click();
        logger.info("Successfuly logged in.");
        
        // Create new Knowledge base
        createNewKnowledgeBase(testTitle,testUri,"");
        
        
        // Add data
        addDataToKnowledgeBaseViaRDFFromWeb(testUri, dataUri);
        
        
        //TODO: drop off sparql query?
        
        addResource(resourceTitle);
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
    private void addDataToKnowledgeBaseViaRDFFromWeb(String knowledgeBaseUri, String dataUri)  {
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
                By.id("location-input")).sendKeys(dataUri);
        
        // Submit again
        bf.getVisibleElement("Can not find submit button.", 
                By.id("importdata")).click();
        bf.waitUntilElementIsVisible("Data form "+ dataUri+" was not correctly added."
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
     * @param kbUri
     *          The uri of the Knowledge Base.
     * @param entry 
     *          Complete link text or unique parts of it of the context 
     *          menu entry.
     */
    /**
     * 
     * @param ident
     * @param entry 
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
}