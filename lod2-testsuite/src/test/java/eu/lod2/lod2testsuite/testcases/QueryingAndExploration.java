package eu.lod2.lod2testsuite.testcases;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 * This class contains functional tests concerning querying and exploration of
 * the lod2 - stack.
 * All test cases are documented here:
 * https://grips.semantic-web.at/pages/viewpage.action?pageId=37193602
 * 
 * @author Stefan Schurischuster
 * @email s.schurischuster@semantic-web.at
 */
public class QueryingAndExploration extends TestCase{
    
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
     * TC 002
     * Note: Needs a default - graph selected. Otherwise it states a message.
     * Test this?
     */
    @Test
    public void ontoWikiSparql()   {
        
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "SPARQL querying", 
            "OntoWiki SPARQL endpoint"});
    }
    
    /**
     * TC 003
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
     * TC 004
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
         /*
         for(WebElement w : results)  {
             System.out.println(w.getText());
         }*/
         
         // Check if there are more then two results.
         assertTrue("Query does not return any data. (Not more then 2).", results.size() > 2);
         
    }
    
    /**
     * TC 005
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
     * TC 006
     * @TODO
     */
    @Test
    @Parameters({ "geoGraph" })
    public void geoSpatialExploration(String geoGraph)  {
        
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "Geo-spatial exploration"});
    }
}
