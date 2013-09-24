package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import java.util.List;

import eu.lod2.lod2testsuite.configuration.TestCase;
import eu.lod2.lod2testsuite.pages.OntoWikiPage;
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
     * TC 002.
     *
     */
    @Test
    @Parameters({ "geoGraph", "query", "geoGraphResult" })
    public void ontoWikiSparql(String graph, String query, String expectedResult)   {
        /*
         boolean useDefaultQuery = false;
         if(graph == null)  {
         graph = "";
         }
         if(query == null)  {
         useDefaultQuery = true;
         }
         */
        bf.checkAndChooseDefaultGraph(graph);
        navigator.navigateTo(new String[]{
                    "Querying & Exploration",
                    "SPARQL querying",
                    "OntoWiki SPARQL endpoint"});

        By frameIdent = By.xpath("//iframe[contains(@src,'ontowiki')]");
        // Check if isparql page is visible
        bf.checkIFrame(frameIdent, By.name("sparqlquery"));
        OntoWikiPage ontoWiki = new OntoWikiPage(frameIdent);
        ontoWiki.submitSparqlQuery("", query, expectedResult);
    }
    
    /**
     * TC 003.
     */
    @Test
    @Parameters({ "geoGraph","query", "geoGraphResult" })
    public void virtuosoSparql(String graph, String query, String expectedResult)  {
     
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "SPARQL querying", 
            "Virtuoso SPARQL endpoint"});
        
         // Check if isparql page is visible
         bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'sparql')]"), 
                By.id("query"));
                  
         // Set graph
         bf.waitUntilElementIsVisible("Could not find input field for graph.", 
                 By.id("default-graph-uri")).sendKeys(graph);
         
         // Type query
         bf.getVisibleElement(By.id("query")).clear();
         bf.getVisibleElement(By.id("query")).sendKeys(query);
         
         // Hit play button with predefined query
         bf.getVisibleElement(
                 By.xpath("//div[@id='main']//input[@type='submit']")).click();
         
         // Get results of query request
         List<WebElement> results = bf.waitUntilElementsAreVisible(
                 "Can not find results for query.",
                 By.xpath("//table[@class='sparql']//tr/td"));
         
         // Check if there are more then two results.
         assertTrue("Query does not return any data. (Not more then 2).", results.size() > 2);
         
         assertTrue("Result after submit was not found: "+expectedResult,
                 bf.isElementVisible(By.xpath("//table[@class='sparql']//tr/td[contains(.,'" 
                 +expectedResult+ "')]")));         
    }
    
    
    /**
     * TC 004.
     */
    @Test
    @Parameters({ "geoGraph","query", "geoGraphResult" })
    public void virtuosoInteractiveSparql(String graph, String query, String expectedResult) {
 
         navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "SPARQL querying", 
            "Virtuoso interactive SPARQL endpoint"});     
         
         // Check if isparql page is visible
         bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'isparql')]"), 
                By.id("page_query"));
         
         // Set graph
         bf.waitUntilElementIsVisible("Could not find input field for graph.", 
                 By.id("default-graph-uri")).sendKeys(graph);
         
         // Type query
         bf.getVisibleElement(By.id("query")).clear();
         bf.getVisibleElement(By.id("query")).sendKeys(query);
         
         // Hit play button with predefined query
         bf.getVisibleElement(By.xpath("//div[@id='toolbar']"
                 + "//img[contains(@src,'player_play')]")).click();
        
         
         // Get results of query request
         List<WebElement> results = driver.findElements(
                 By.xpath("//div[@id='res_tab_ctr']//tr/td"));
         
         // Check if there are more then two results.
         assertTrue("Query does not return any data. (Not more then 2).", results.size() > 2);
                  // Check for result
         assertTrue("Result after submit was not found: "+expectedResult,
                 bf.isElementVisible(By.xpath("//div[@id='res_tab_ctr']//tr/td[contains(.,'" 
                 +expectedResult+ "')]")));
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
     */
    @Test
    @Parameters({ "geoGraph" })
    public void geoSpatialExploration(@Optional String geoGraph)  {
        
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "Geo-spatial exploration"});
        
        // TODO: Further testing
    }
    
    /**
     * TC 007.
     */
    @Test
    @Parameters({"geoGraph", "specialResult" })
    public void assistedQueryingWithCurrentGraph(String graph, String expectedResult) {
        bf.checkAndChooseDefaultGraph(graph);
        navigator.navigateTo(new String[]{
                    "Querying & Exploration",
                    "SPARQL querying",
                    "SparQLed - Assisted Querying",
                    "Use currently selected graph"});
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'sparqled')]"), 
                By.xpath("//iframe[@src='sindice-editor']"));
        
        WebElement iframe = bf.waitUntilElementIsVisible("Could not find iframe.",By.xpath("//iframe[@src='sindice-editor']"));        
        driver.switchTo().frame(iframe);
        logger.info("Switched to sindice editor frame.");

        bf.waitUntilElementIsVisible(
                "Iframe content was not correctly displayed.",
                By.id("flint-test"), By.xpath("//iframe[@src='sindice-editor']"), BasicFunctions.MAX_PATIENCE_SECONDS);
        /*
        // Type query
        WebElement inputField = bf.waitUntilElementIsVisible("Could not find inputField for query.",
                By.xpath("//textarea"));
        inputField.clear();
        inputField.sendKeys(query);
        */
        
        // Click submit
        bf.getVisibleElement("Could not find submit button.", 
                By.id("flint-endpoint-submit")).click();
        // Check results
        bf.waitUntilElementIsVisible("Query result: " + expectedResult
                + " was not found after running query.", 
                By.xpath("//div[@id='formatted-results']//font[contains(.,'" +expectedResult+ "')]"));
        
    }

    /**
     * TC 008.
     * @TODO create TC
     */
    @Test
     @Parameters({"bookGraph","geoGraph","query", "specialResult" })
    public void assistedQueryingAndSummeryGraph(String inputGraph, String outputGraph, String query, String expectedResult) {
        navigator.navigateTo(new String[]{
                    "Querying & Exploration",
                    "SPARQL querying",
                    "SparQLed - Assisted Querying",
                    "Use manager to calculate summary graph"});
        By frameIdentifier = By.xpath("//iframe[contains(@src,'sparqled')]");
        
        bf.checkIFrame(frameIdentifier, By.id("form"));
        
        bf.waitUntilElementIsVisible("Could not find input field for input graph.", 
                By.id("input-graph")).sendKeys(inputGraph);
        bf.getVisibleElement("Could not find input field for output graph.", 
                By.id("output-graph")).sendKeys(outputGraph);
        // Click create button
        bf.getVisibleElement(By.id("create")).click();
        
        // Click refresh button
        bf.getVisibleElement(By.id("list")).click();
        
        bf.waitUntilElementIsVisible("Summary is not listed in available summaries.",
                By.xpath("//div[@id='list-result'][contains(.,'"+outputGraph+"')]"));
        
        bf.getVisibleElement("Could not find input field for summary graph.", 
                By.id("dg")).sendKeys(outputGraph);
        
        // Click select button
        bf.getVisibleElement(By.id("select")).click();
        
        WebElement iframe = bf.waitUntilElementIsVisible("Could not find iframe.",
                By.xpath("//iframe[@src='/sparqled/sindice-editor/']"));        
        driver.switchTo().frame(iframe);
        logger.info("Switched to sindice editor frame.");

        bf.waitUntilElementIsVisible(
                "Iframe content was not correctly displayed.",
                By.id("flint-test"), By.xpath("//iframe[@src='sindice-editor']"), BasicFunctions.MAX_PATIENCE_SECONDS);
        /*
        // Type query
        WebElement inputField = bf.waitUntilElementIsVisible("Could not find inputField for query.",
                By.xpath("//textarea"));
        inputField.clear();
        inputField.sendKeys(query);
        */
        
        // Click submit
        //bf.getVisibleElement("Could not find submit button.", 
        //        By.id("flint-endpoint-submit")).click();
        // Check results
        //bf.waitUntilElementIsVisible("Query result: " + expectedResult
        //        + " was not found after running query.", 
        //        By.xpath("//div[@id='formatted-results']//font[contains(.,'" +expectedResult+ "')]"));
        
        
        // TODO: submit query and check result
        
    }

}