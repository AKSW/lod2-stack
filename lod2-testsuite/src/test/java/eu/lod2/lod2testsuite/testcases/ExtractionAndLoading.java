package eu.lod2.lod2testsuite.testcases;

import java.util.List;
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
     */
    @Test
    public void openVirtuoso()  {
        
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Upload RDF file or RDF from URL"});

        // Check if Iframe is visible and shows Virtuoso.
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'conductor')]"), 
                By.id("MTB"));
        
    }
    
    /**
     * Uploads a graph to the lod2stack
     */
    @Parameters({ "graphName", "graphFilePath"})
    @Test(dependsOnMethods={"openVirtuoso"})
    public void uploadGraphInVirtuoso(String graphName, String graphFilePath)  {
        // Vitruoso is already opened.
        // But frames have been switched. so check 
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'conductor')]"),
                By.id("MTB"));
        // If not logged in: log into Virtuoso
        if (bf.isElementVisible(By.id("t_login_usr"))) {
            WebElement user = bf.getVisibleElement(
                    "Could not find user input",
                    By.id("t_login_usr"));
            WebElement pw = bf.getVisibleElement(
                    "Could not find password input",
                    By.id("t_login_pwd"));

            user.sendKeys("dba");
            pw.sendKeys("dba");

            // Click login button
            bf.getVisibleElement(
                    "Could not find login button",
                    By.id("login_btn")).click();
        }
        // Click on Linked Data tab
        bf.waitUntilElementIsVisible("Could not find LinkedData tab",
                By.linkText("Linked Data")).click();
        // Wait and click on Quad Store Upload
        bf.waitUntilElementIsVisible("Could not find sub tab Quad Store Upload.",
                By.linkText("Quad Store Upload")).click();
        WebElement filePath = bf.waitUntilElementIsVisible("File upload not found",
                By.xpath("//tr[@id='rd1']//input[@type='file']"));
        WebElement name = bf.waitUntilElementIsVisible("File upload not found",
                By.xpath("//tr[@id='rd2']//input[@type='text']"));
        // Type
        name.clear();
        name.sendKeys(graphName);
        filePath.sendKeys(graphFilePath);
        // Click upload
        bf.getVisibleElement("Could not find submit button.", By.name("bt1")).click();
        // Wait for upload to finish
        bf.waitUntilElementIsVisible("Upload did not finish",
                By.xpath("//div[@class='message'][contains(.,'Upload finished')]"), 30);
    }
    
    /**
     * TC 002
     */
    @Test
    public void loadRDFdataFromCKAN()  {
        
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Load RDF data from CKAN"});
        
        WebElement link = bf.waitUntilElementIsVisible(
                By.xpath("//a[starts-with(@href, 'apt:')]"));
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
     * TC 003-x
     * @TODO 003-1-2-4
     * @TODO Need testdata for basic extraction
     * Tests transformation and uploading.
     */
    @Test
    @Parameters({ "xmlTextFile", "xsltTextFile", "exportGraph" })
    public void basicExtraction(String xmlFile, String xsltFile, String exportGraph) {
        
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Extract RDF from XML", 
            "Basic extraction"});
        
        ArrayList<String> xmlText = bf.readFile(xmlFile, false);
        ArrayList<String> xsltText = bf.readFile(xsltFile, false);
        
        // Wait for first element
        WebElement xmlField = bf.waitUntilElementIsVisible(
                By.id("EXML_xmlText"));
        
        WebElement xsltField = bf.getVisibleElement(
                By.id("EXML_xsltText"));
        
        WebElement transformButton = bf.getVisibleElement(
                By.id("EXML_transformButton"));
        
        WebElement uploadButton = bf.getVisibleElement(
                By.id("EXML_uploadButton"));
        
        // Choose export graph if one is provided.
        if(!exportGraph.isEmpty())          
            bf.handleSelector(By.id("ExportSelector_graphSelector"), exportGraph, false);
        
        // Type into input fields
        for(String chars : xmlText)  {
            xmlField.sendKeys(chars);
        }
        
        // No result should be displayed.
        assertFalse("Result is displayed, although required information is missing.",
                 bf.isElementVisible(By.id("EXML_rdfResultField")));
        
        for(String chars : xsltText)  {
            xsltField.sendKeys(chars);
        }
        
        transformButton.click();
        
        // Do not select any input graph and try to upload
        //uploadButton.click();
        
        // Wait for result to appear.
        WebElement resultField = bf.waitUntilElementIsVisible(
                "Transformation did not succeed. Result was not displayed.", 
                By.id("EXML_rdfResultField"));
        
        // Verify result containing an rdf - tag
        resultField.getText().contains("<rdf:");
        
        uploadButton.click();
        
        // Wait for result to appear.
        List<WebElement> uploadResults = bf.waitUntilElementsAreVisible(
                "Transformation did not succeed. Result was not displayed.", 
                By.id("EXML_rdfResultField"));
        
        // Confirm that last element contains "succeeded".
        assertTrue("Upload did not work properly.",
                uploadResults.get(uploadResults.size()-1).getText().contains("suceeded"));
    }
    
    /**
     * TC 004-1-4
     * @TODO click buttons and whatch for result.
     * @TODO Need testdata for extendedExtraction
     */
    @Test
    @Parameters({ 
        "xmlFile", 
        "xsltFile",
        "cataologFile", 
        "exportGraph", 
        "downloadFileName", 
        "downloadFilePath" })
    public void extendedExtraction(String xmlFile, String xsltFile, String catalogFile, 
                                   String exportGraph, String downloadFileName, String downloadFilePath) {
        
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Extract RDF from XML", 
            "Extended extraction"});
                
        WebElement uploadButton = bf.getVisibleElement(
                By.id("EXMLExtended_uploadButton"));
        WebElement transformButton = bf.getVisibleElement(
                By.id("EXMLExtended_transformButton"));
        
        // Select export graph
        bf.handleSelector(By.id("ExportSelector_graphSelector"), exportGraph, false);
        
        // Handle uploads
        bf.handleFileUpload(By.id("EXMLExtended_uploadXMLFile"), xmlFile);
        
        // Check if upload is performed although neccessery fields have not been filled out.
        uploadButton.click();
        WebElement notice = bf.waitUntilElementIsVisible(
                "No error message appeared after clicking upload with a field missing.", 
                By.xpath("//div[@class='gwt-HTML']"));
        // Click notice
        notice.click();
        bf.waitUntilElementDisappears(By.xpath("//div[@class='gwt-HTML']"));
        
        bf.handleFileUpload(By.id("EXMLExtended_uploadXSLTFile"), xsltFile);
        
        // Check if upload is performed although neccessery fields have not been filled out.
        uploadButton.click();
        notice = bf.waitUntilElementIsVisible(
                "No error message appeared after clicking upload with a field missing.", 
                By.xpath("//div[@class='gwt-HTML']"));
        // Click notice
        notice.click();
        bf.waitUntilElementDisappears(By.xpath("//div[@class='gwt-HTML']"));
        
        bf.handleFileUpload(By.id("EXMLExtended_uploadCatalogFile"), catalogFile);
        
        // Begin transformation
        transformButton.click();
        
        WebElement resultField = bf.waitUntilElementIsVisible(
                "No result is able after transformation.", 
                By.id("EXMLExtended_textToAnnotateField"));
        
        //@TODO: Maybe add a check for the contents of the textfield.
        
        
        // Download file
        assertTrue("Download dialogue is missing.", bf.isElementVisible(By.id("EXMLExtended_dlFileName")));
        WebElement filename = bf.getVisibleElement(By.id("EXMLExtended_dlFileName"));
        WebElement filepath = bf.getVisibleElement(By.id("EXMLExtended_dlPath"));
        filename.sendKeys(downloadFileName);
        filepath.sendKeys(downloadFilePath);
        
        // Click download
        bf.getVisibleElement(By.id("EXMLExtended_downloadButton")).click();
        assertFalse("Error message appeared.",
                bf.getExistingElement(By.xpath("//div[@class='gwt-HTML']")).isDisplayed());
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
        WebElement textField = bf.getVisibleElement(
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
     * @TODO create TC
     */
    @Test
    @Parameters({ "exportGraph","poolPartyProjectId","language","textFile" })
    public void poolPartyExtractor(String exportGraph, String poolPartyProjectId, String language, String textFile)  {
        
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Extract RDF from text w.r.t. a controlled vocabulary"});
    }
}