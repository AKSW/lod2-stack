package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.TestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/**
 * This class contains functional tests concerning EnrichmentAndDataCleaning of
 * the lod2 - stack.
 * All TestCases are documented here:
 * https://grips.semantic-web.at/display/LOD2/LOD2Stack+Test+Cases+-+EnrichmentAndDataCleaning
 * 
 * @author Stefan Schurischuster
 * @email s.schurischuster@semantic-web.at
 */
public class EnrichmentAndDataCleaning extends TestCase {
    /**
     * TC 001.
     */
    @Test
    public void ore()  {
        navigator.navigateTo(new String[] {
            "Enrichment", 
            "ORE"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ore')]"), 
                By.xpath("//a[@href='http://ore-tool.net/']"));
        
        // Wait for the interface of ORE to appear.
        WebElement menuBar = bf.waitUntilElementIsVisible(
                "Menu Bar of ORE did not load within time.", 
                By.cssSelector("span.v-menubar-menuitem"));
    }    
    
    /**
     * TC 002.
     */
    @Test
    public void lodRefine()  {
        navigator.navigateTo(new String[] {
            "Enrichment", 
            "LOD enabled Refine"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'lodrefine')]"), 
                By.id("action-area-tabs"));
        
        // TODO further testing
    }
    
    /**
     * TC 003.
     */
    @Test
    public void dbpediaSpotlight()  {
         navigator.navigateTo(new String[] {
            "Enrichment", 
            "DBpedia Spotlight"});  
         
         bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'dbpedia-spotlight-ui')]"), 
                By.id("action_container"));
         
         // TODO further testing
    }
}
