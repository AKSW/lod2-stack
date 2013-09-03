
package eu.lod2.lod2testsuite.pages;

import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.Navigator;
import eu.lod2.lod2testsuite.configuration.TestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

/**
 *
 * @author Stefan Schurischuster
 */
public class SpotlightPage extends Page {
    private WebDriver driver;
    private BasicFunctions bf;
    private Navigator navigator;
    private Actions driverActions;   
    
    /**
     * 
     * @param frameIdentifier 
     *          Identifier for the frame that layers LODMS.
     */
    public SpotlightPage(By frameIdentifier)  {
        super(frameIdentifier);
        this.driver = TestCase.driver;
        this.bf = TestCase.bf;
        this.navigator = TestCase.navigator;
        this.driverActions = TestCase.driverActions;
    }
    

    /**
     * Analyses a given text for annotations.
     * 
     * @param text
     *          The text to analyse. If left empty the default text will be used.
     * @param language
     *          A two letter abbreviation for the analysing language e.g en or de.
     *          If left empty the default language (en) is used.
     * @param confienceValue
     *          Value between 0 and 1. If 0 then the default value is used.
     * @param annotationScreValue
     *          Value between 0 and 1. If 0 then the default value is used.
     * @param nBestCandidates 
     *          If true the n best candidates are used. False otherwise.
     */
    public void analyseText(String text, String language, double confienceValue, double annotationScoreValue, boolean nBestCandidates)  {
        // Select lang
        if(!language.isEmpty())  {
            Select langSelect = new Select(driver.findElement(By.id("annotation_language")));
            langSelect.selectByValue(language);
        }
        
        if(!text.isEmpty())  {
            // TODO: set text
        }
        
        if(confienceValue > 0 && confienceValue <= 1)  {
            // TODO: set confience value
        }
        
        if(annotationScoreValue > 0 && annotationScoreValue <= 1)  {
            // TODO: set annotation score
        }
        
        if(nBestCandidates)  {
            // TODO: check n best cnadidates
        }
        
        driver.findElement(By.id("uniform-annotate")).click();
        bf.waitUntilElementIsVisible("Could not find annnotated text.", 
                By.xpath("//div[@id='text_annotated']//a"));
        
    }
}
