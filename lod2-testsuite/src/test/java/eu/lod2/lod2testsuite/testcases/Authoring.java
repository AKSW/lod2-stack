package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.TestCase;
import eu.lod2.lod2testsuite.pages.OntoWikiPage;
import org.openqa.selenium.By;
import org.testng.Assert;

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
    
    /**
     * TC 001 - 006.
     */
    @Test
    @Parameters({ "username", "knowledgeBaseTitle", "knowledgeBaseUri", "importUri", "resourceTitle", "instanceTitle"})
    public void addAndEditKnowledgeBase(String username, String knowledgeBaseTitle, String knowledgeBaseUri, String importUri, String resourceTitle, String instanceTitle)  {
        
        navigator.navigateTo(new String[] {
            "Authoring", 
            "OntoWiki"});  
        By frameIdentifier = By.xpath("//iframe[contains(@src,'ontowiki')]");
        
        bf.checkIFrame(
                frameIdentifier, 
                By.id("application"));
        
        OntoWikiPage ontoWiki = new OntoWikiPage(frameIdentifier);   
        
        // Login into onto wiki
        ontoWiki.logIntoOntoWiki(username,"");
        
        // Create new Knowledge base
        ontoWiki.createNewKnowledgeBase(knowledgeBaseTitle,knowledgeBaseUri,"");
        
        // Add data
        ontoWiki.addDataToKnowledgeBaseViaRDFFromWeb(knowledgeBaseUri, importUri);
        
        //TODO: drop off sparql query?
        // Add resource
        ontoWiki.addResource(knowledgeBaseUri, resourceTitle);
        
        //Add instance
        ontoWiki.addInstanceToResource(knowledgeBaseUri, resourceTitle, instanceTitle);
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
        
        OntoWikiPage ontoWiki = new OntoWikiPage(frameIdentifier);   
        // Perform login if necessary
        ontoWiki.logIntoOntoWiki(username, "");
        // Delete KB
        ontoWiki.navigateToContextMenuEntry(knowledgeBaseUri,"Delete Knowledge");
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
        bf.checkAndChooseDefaultGraph();
        navigator.navigateTo(new String[] {
            "Authoring", 
            "Publish to CKAN"});  

        bf.waitUntilElementIsVisible("Could not find CKAN input fields.", 
                By.cssSelector("input.v-textfield"));
        //TODO: further testing
    }
}