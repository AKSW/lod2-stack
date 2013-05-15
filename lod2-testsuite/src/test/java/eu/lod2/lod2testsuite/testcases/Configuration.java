package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.TestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/**
 * This class contains functional tests concerning configuration of
 * the lod2 - stack.
 * All TestCases are documented here:
 * https://grips.semantic-web.at/display/LOD2/LOD2Stack+Test+Cases+-+Configuration
 * 
 * @author Stefan Schurischuster
 * @email s.schurischuster@semantic-web.at
 */
public class Configuration extends TestCase {
    /**
     * TC 001.
     * @TODO create TC
     */
    @Test
    public void demonstratorConfig()  {
        navigator.navigateTo(new String[] {
            "Configuration", 
            "Demonstrator configuration"});  
        
    }
    
    /**
     * TC 002.
     */
    @Test
    public void about()  {
        navigator.navigateTo(new String[] {
            "Configuration", 
            "About"});  
        
        // Wait for table with participants information is visible
        WebElement table = bf.waitUntilElementIsVisible(
                "Menu Bar of ORE did not load within time.", 
                By.id("About_table"));
    }       
}
