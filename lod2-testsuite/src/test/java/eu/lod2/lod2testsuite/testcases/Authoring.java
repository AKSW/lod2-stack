package eu.lod2.lod2testsuite.testcases;

import org.openqa.selenium.By;
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
     * TC 001
     */
    @Test
    public void ontoWiki()  {
        navigator.navigateTo(new String[] {
            "Authoring", 
            "OntoWiki"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ontowiki')]"), 
                By.id("Local"));
    }
}
