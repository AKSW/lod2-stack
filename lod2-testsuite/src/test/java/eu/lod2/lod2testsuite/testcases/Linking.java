package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.TestCase;
import eu.lod2.lod2testsuite.pages.SilkPage;
import org.openqa.selenium.By;
import org.testng.annotations.Parameters;
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
        String projectTitle = "asd";
        
        navigator.navigateTo(new String[] {
            "Linking", 
            "Silk"});  
        By frameIdentifier = By.xpath("//iframe[contains(@src,'silk')]");
        bf.checkIFrame(frameIdentifier, 
                By.id("project_movies_example"));
        SilkPage silk = new SilkPage(frameIdentifier);
        silk.deleteProject(projectTitle);
    }
    
    /**
     * TC 002.
     */
    @Test
    @Parameters({"geoGraph", "exampleGraph"})
    public void limes(String inputGraph, String outputGraph )  {
        navigator.navigateTo(new String[] {
            "Linking", 
            "Limes"});  
        
        // Select input and output
        bf.handleSelector(By.id("Limes_graphSelector_sourceGraph"), inputGraph, false);
        bf.handleSelector(By.id("Limes_graphSelector_targetGraph"), outputGraph, false);
        
        // Submit
        bf.getVisibleElement("Could not find commit button for Limes linking.",
                By.id("Limes_commitButton")).click();
        
        bf.waitUntilElementIsVisibleFast("Could not find link to ColaNut after choosing graphs.", 
                By.xpath("//a[@target='second']"));
        
        //TODO click link and perform further testing        
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
