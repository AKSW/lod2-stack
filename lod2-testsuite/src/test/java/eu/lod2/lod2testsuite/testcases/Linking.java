package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.TestCase;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

/**
 * This class contains functional tests concerning linking of
 * the lod2 - stack.
 * All TestCases are documented here:
 * https://grips.semantic-web.at/display/LOD2/LOD2Stack+Test+Cases+-+Linking
 * 
 * @author Stefan Schurischuster
 * @email s.schurischuster@semantic-web.at
 */
public class Linking extends TestCase {
    
    /**
     * TC 001.
     */
    @Test
    public void silk()  {
        navigator.navigateTo(new String[] {
            "Linking", 
            "Silk"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'silk')]"), 
                By.id("project_movies_example"));
    }
    
    /**
     * TC 002.
     * @TODO create TC
     */
    @Test
    public void limes()  {
        navigator.navigateTo(new String[] {
            "Linking", 
            "Limes"});  
        /*
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'silk')]"), 
                By.id("project_movies_example"));
         * 
         */
    }
    
    /**
     * TC 003.
     * @TODO create TC
     * @TDOD get valid test data for sameAsLinking
     */
    @Test
    public void sameAsLinking()  {
        navigator.navigateTo(new String[] {
            "Linking", 
            "SameAs Linking"});  
    }
}
