package eu.lod2.lod2testsuite.testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/**
 * This class contains functional tests concerning Enrichment of
 * the lod2 - stack.
 * All TestCases are documented here:
 * https://grips.semantic-web.at/display/LOD2/LOD2Stack+Test+Cases+-+Enrichment
 * 
 * @author Stefan Schurischuster
 * @email s.schurischuster@semantic-web.at
 */
public class Enrichment extends TestCase {
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
                By.id("loading"));
        
        // Wait for the interface of ORE to appear.
        WebElement menuBar = bf.waitUntilElementIsVisible(
                "Menu Bar of ORE did not load within time.", 
                By.id("x-auto-7"));
    }    
}
