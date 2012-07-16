package eu.lod2.lod2testsuite.testcases;

import org.openqa.selenium.NoSuchElementException;
import java.util.ArrayList;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 * This class contains functional tests concerning querying and exploration of
 * the lod2 - stack.
 * All TestCases are documented here:
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
        bf.getExistingAndVisibleElement(By.id("SesameSPARQL_okbutton")).click();
        
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
    
    @Test
    public void virtuosoSparql()  {
     
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "SPARQL querying", 
            "Virtuoso SPARQL endpoint"});
    }
    
    @Test
    public void virtuosoInteractiveSparql() {
 
         navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "SPARQL querying", 
            "Virtuoso interactive SPARQL endpoint"});     
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
    
    @Test
    @Parameters({ "geoGraph" })
    public void geoSpatialExploration(String geoGraph)  {
        
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "Geo-spatial exploration"});
    }
}
