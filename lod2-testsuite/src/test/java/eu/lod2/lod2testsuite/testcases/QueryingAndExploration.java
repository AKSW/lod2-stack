package eu.lod2.lod2testsuite.testcases;

import java.util.List;

import eu.lod2.lod2testsuite.configuration.TestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class contains functional tests concerning querying and exploration of
 * the lod2 - stack.
 * All test cases are documented here:
 * https://grips.semantic-web.at/pages/viewpage.action?pageId=37193602
 * 
 * @author Stefan Schurischuster
 * @email s.schurischuster@semantic-web.at
 */
public class QueryingAndExploration extends TestCase {
    
    /**
     * TC 001.
     * 
     * @Note: Will change soon.
     */
    @Test
    @Parameters({ "query" })
    public void sparqlViaSesameApi(String query)  {
        
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "SPARQL querying", 
            "Direct via Sesame API"});

        WebElement textfield = bf.waitUntilElementIsVisible(By.id("SesameSPARQL_query"));
        
        // Check for predefined value?
        textfield.sendKeys(query);
        
        // Click "evaluate"
        bf.getVisibleElement(By.id("SesameSPARQL_okbutton")).click();
        
        bf.waitUntilElementIsVisible(By.xpath("//div[@id='SesameSPARQL_sparqlResult']"
                + "/div[@class='v-panel-content'][not(text()='')]"));
        //"//div[@id='SesameSPARQL_sparqlResult']//textarea"
        
    }
    
    /**
     * TC 002.
     * @Note: Needs a default - graph selected. Otherwise it states a message.
     * Test this?
     * @TODO choose graph??
     * 
     */
    @Test
    public void ontoWikiSparql()   {
        
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "SPARQL querying", 
            "OntoWiki SPARQL endpoint"});
        
         // Check if isparql page is visible
         bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ontowiki')]"),
                By.name("sparqlquery"));
         
         // Choose Graph??
         
         // Wait for text input field.
         bf.waitUntilElementIsVisible(
                 "Textfield that displays query is not visible.", 
                 By.className("innercontent"));
         
         // Click submit
         WebElement submitButton = bf.getVisibleElement("Could not find the submit button.",By.xpath(
                 "//div[@class='messagebox']/div[@class='toolbar']/a[@class='button submit']"));
         submitButton.click();
         
         WebElement firstSubject = bf.waitUntilElementIsVisible("No result from query found.", 
                 By.id("r0-c0"));
         
         /*
         // If not logged in already log in --> NOT NECESSARY for submitting a query.
         if(bf.isElementVisible(By.xpath("//div[@class='login.window']")))  {
             WebElement user = bf.getVisibleElement(
                     "Could not find user input", 
                     By.xpath("//input[@class='username.text']"));
             WebElement pw = bf.getVisibleElement(
                     "Could not find password input", 
                     By.xpath("//input[@class='username.text']"));
             
             user.sendKeys("dba");
             pw.sendKeys("dba");
             
             // Click login button
             bf.getVisibleElement(
                     "Could not find login button",
                     By.xpath("//a[@class='locallogin.button']")).click();
         }
         */
    }
    
    /**
     * TC 003.
     */
    @Test
    public void virtuosoSparql()  {
     
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "SPARQL querying", 
            "Virtuoso SPARQL endpoint"});
        
         // Check if isparql page is visible
         bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'sparql')]"), 
                By.id("query"));
         
         // Hit play button with predefined query
         WebElement runButton = bf.getVisibleElement(
                 By.xpath("//div[@id='main']//input[@type='submit']"));
         
         runButton.click();
         
         // Get results of query request
         List<WebElement> results = bf.waitUntilElementsAreVisible(
                 "Can not find results for query.",
                 By.xpath("//table[@class='sparql']//tr/td"));
         
         // Check if there are more then two results.
         assertTrue("Query does not return any data. (Not more then 2).", results.size() > 2);
    }
    
    
    /**
     * TC 004.
     */
    @Test
    public void virtuosoInteractiveSparql() {
 
         navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "SPARQL querying", 
            "Virtuoso interactive SPARQL endpoint"});     
         
         // Check if isparql page is visible
         bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'isparql')]"), 
                By.id("page_query"));
         
         // Hit play button with predefined query
         WebElement playButton = bf.getVisibleElement(
                 By.xpath("//div[@id='toolbar']//img[contains(@src,'player_play')]"));
         
         playButton.click();
         
         // Get results of query request
         List<WebElement> results = driver.findElements(
                 By.xpath("//div[@id='res_tab_ctr']//tr/td"));
         
         // Check if there are more then two results.
         assertTrue("Query does not return any data. (Not more then 2).", results.size() > 2);
         
    }
    
    /**
     * TC 005.
     */
    @Test
    public void sigmaEe()  {
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "Sig.ma EE"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'sigmaee')]"), 
                By.id("header"));
    }
    
    /**
     * TC 006.
     * @TODO create TC
     */
    @Test
    @Parameters({ "geoGraph" })
    public void geoSpatialExploration(@Optional String geoGraph)  {
        
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "Geo-spatial exploration"});
    }
}
