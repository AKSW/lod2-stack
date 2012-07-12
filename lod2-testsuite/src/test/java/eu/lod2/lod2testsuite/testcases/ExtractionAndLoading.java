package eu.lod2.lod2testsuite.testcases;

import java.util.logging.Logger;
import java.util.logging.Level;
import org.openqa.selenium.NoSuchElementException;
import java.util.ArrayList;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 * This class contains functional tests concerning extraction and loading of
 * the lod2 - stack.
 * All TestCases are documented here:
 * https://grips.semantic-web.at/pages/viewpage.action?pageId=36409526
 * 
 * @author Stefan Schurischuster
 * @email s.schurischuster@semantic-web.at
 */
public class ExtractionAndLoading extends TestCase {
    
    /**
     * TC 003-2 
     * @TODO verify result of button click.
     */
    @Test
    @Parameters({ "xmlTextFile", "xsltTextFile", "exportGraph" })
    public void basicExtraction(String xmlFile, String xsltFile, String exportGraph) {
        bf.waitUntilElementDisappears(By.xpath("//div[@class='gwt-HTML']"));
        
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Extract RDF from XML", 
            "Basic extraction"});
        
        ArrayList<String> xmlText = bf.readFile(xmlFile, false);
        ArrayList<String> xsltText = bf.readFile(xsltFile, false);
        
        WebElement xmlField = bf.getExistingAndVisibleElement(
                By.id("EXML_xmlText"));
        WebElement xsltField = bf.getExistingAndVisibleElement(
                By.id("EXML_xsltText"));
        WebElement transformButton = bf.getExistingAndVisibleElement(
                By.id("EXML_transformButton"));
        
        for(String chars : xmlText)  {
            xmlField.sendKeys(chars);
        }
        
        for(String chars : xsltText)  {
            xsltField.sendKeys(chars);
        }
        
        transformButton.click();
        
        // TODO verification.
    }
    
    /**
     * 
     * @param xmlFile
     * @param xsltFile
     * @param catalogFile
     * @param exportGraph 
     */
    @Test
    @Parameters({ "xmlFile", "xsltFile","cataologFile", "exportGraph" })
    public void extendedExtraction(String xmlFile, String xsltFile, String catalogFile, String exportGraph) {
        bf.waitUntilElementDisappears(By.xpath("//div[@class='gwt-HTML']"));
        
        String identifier = "//span[contains(.,'Extraction & Loading')]"
                    + "[not(contains(@class,'caption'))]";
        WebElement link = null;
        try  {
            link = TestCase.driver.findElement(
                            By.xpath(identifier));
        } catch(NoSuchElementException e)  {
            Assert.fail("Element not found: "+e.getMessage());
        }
        
        TestCase.driverActions.moveToElement(link);
        link.click();
        TestCase.bf.bePatient(2000);
        
        identifier = "//div[@class = 'v-menubar-popup'][last()]"
                + "//span[contains(.,'Extract RDF from XML')]"
                + "[not(contains(@class,'caption'))]";
        
        
        try  {
            link = TestCase.driver.findElement(
                            By.xpath(identifier));
        } catch(NoSuchElementException e)  {
            Assert.fail("Element not found: "+e.getMessage());
        }
        
        TestCase.driverActions.moveToElement(link);
        link.click();
        TestCase.bf.bePatient(1000);
        
        identifier = "//div[@class = 'v-menubar-popup'][last()]"
                + "//span[contains(.,'Extended extraction')]"
                + "[not(contains(@class,'caption'))]";
        
        try  {
            link = TestCase.driver.findElement(
                            By.xpath(identifier));
        } catch(NoSuchElementException e)  {
            Assert.fail("Element not found: "+e.getMessage());
        }
        System.out.println(link.getText());
        System.out.println(link.getLocation());
        
        selenium.click(identifier);
        //link.click();
        
        TestCase.bf.bePatient(1000);
        
         /*   
        navigator.navigateTo(new String[] {
            "Extraction & Loading", "Extract RDF from XML", "Extended extraction"});
        
        
        navigator.navigateTo(new String[] {
            "Querying & Exploration", 
            "SPARQL querying", 
            "OntoWiki SPARQL endpoint"});
        
        navigator.navigateTo(new String[] {
            "Online Tools and Services", 
            "Online SPARQL endpoints", 
            "DBpedia"});
       
        
        
        // Type into first input field
        WebElement xmlField = bf.waitUntilElementIsPresent(By.id("EXMLExtended_uploadXMLFile"));
        WebElement xsltField = bf.getExistingAndVisibleElement(By.id("EXMLExtended_uploadXSLTFile"));
        WebElement catalogField = bf.getExistingAndVisibleElement(By.id("EXMLExtended_uploadXSLTFile"));
        
        xmlField.sendKeys(xmlFile);
        bf.bePatient(5000);
        */
    }
    
    /**
     * TC 006
     * @param textFile 
     */
    @Test
    @Parameters({ "textFile" })
    public void annotatePlainText(String textFile)  {        
        bf.waitUntilElementDisappears(By.xpath("//div[@class='gwt-HTML']"));
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Extract RDF from text w.r.t. DBpedia"});
        
        ArrayList<String> text = bf.readFile(textFile, false);
        WebElement textField = bf.getExistingAndVisibleElement(
                By.id("ESpotlight_textToAnnotateField"));
        
        for(String t : text)  {
            textField.sendKeys(t);
        }
        
        // Wait until button is clickable after typing the text.
        WebElement button = bf.waitUntilElementIsPresent(
                "Button did not turn clickable.",
                By.xpath("//div[@id = 'ESpotlight_annotateButton']"
                + "[@class='v-button']"));
        
        button.click();
        
        // Wait until the result field contains links.
        WebElement result = bf.waitUntilElementIsPresent(
                "Result field does not contain any links to dbpedia.",
                By.xpath("//div[@class='v-label']//a"));
    }
}
        /*
        WebElement xmlField = null;
        WebElement xsltField = null;
        WebElement transformButton = null;
        
        try  {
            xmlField = driver.findElement(By.id("EXML_xmlText"));
            xsltField = driver.findElement(By.id("EXML_xsltText"));
            transformButton = driver.findElement(By.id("EXML_transformButton"));
        } catch(NoSuchElementException e)  {
            Assert.fail("Element not found: " +e.getMessage());
        }
        
        assertTrue(xmlField + " is not displayed.",xmlField.isDisplayed());
        for(String x : xmlText)  {
            xmlField.sendKeys(x);
        }
          
        assertTrue(xsltField+ " is not displayed.",xsltField.isDisplayed());
        for(String x : xsltText)  {
            xsltField.sendKeys(x);
        }
         * 
         */