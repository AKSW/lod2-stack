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
     * TC 001
     * @TODO check if logged in?
     */
    @Test
    public void openVirtuoso()  {
        bf.waitUntilElementDisappears(By.xpath("//div[@class='gwt-HTML']"));
        // Navigate to page
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Upload RDF file or RDF from URL"});

        // Check if Iframe is visible and shows Virtuoso.
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'conductor')]"), 
                By.id("MTB"));
    }
    
    /**
     * TC 002
     */
    @Test
    public void loadRDFdataFromCKAN()  {
        bf.waitUntilElementDisappears(By.xpath("//div[@class='gwt-HTML']"));
        // Navigate to page
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Load RDF data from CKAN"});
        
        WebElement link = bf.waitUntilElementIsVisible(
                By.xpath("//a[starts-with(@href, 'apt:')]"));
        
        link.click();
        
        // Check for link count.
        try  {
            assertTrue("Not all CKAN links are displayed.",
                    9 == driver.findElements(
                    By.xpath("//a[starts-with(@href, 'apt:')]")).size());
        } catch(NoSuchElementException e)  {
            Assert.fail(e.getMessage());
        }
    }
    
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
        
        // Wait for first element
        WebElement xmlField = bf.waitUntilElementIsVisible(
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
     * TC 004
     * @TODO click buttons and whatch for result.
     */
    @Test
    @Parameters({ "xmlFile", "xsltFile","cataologFile", "exportGraph" })
    public void extendedExtraction(String xmlFile, String xsltFile, String catalogFile, String exportGraph) {
        
        bf.waitUntilElementDisappears(By.xpath("//div[@class='gwt-HTML']"));
        
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Extract RDF from XML", 
            "Extended extraction"});
        
        // Handle uploads
        bf.handleFileUpload(By.id("EXMLExtended_uploadXMLFile"), xmlFile);
        bf.handleFileUpload(By.id("EXMLExtended_uploadXSLTFile"), xsltFile);
        bf.handleFileUpload(By.id("EXMLExtended_uploadCatalogFile"), catalogFile);
         
        bf.handleSelector(By.id("ExportSelector_graphSelector"), exportGraph, false);
    }
    
    /**
     * TC 006
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
    
    /**
     * TC 007
     */
    @Test
    @Parameters({ "exportGraph","poolPartyProjectId","language","textFile" })
    public void poolPartyExtractor(String exportGraph, String poolPartyProjectId, String language, String textFile)  {
        
        bf.waitUntilElementDisappears(By.xpath("//div[@class='gwt-HTML']"));
        
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Extract RDF from text w.r.t. a controlled vocabulary"});
    }
}