package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.TestCase;
import eu.lod2.lod2testsuite.pages.DataPage;
import eu.lod2.lod2testsuite.pages.VirtuosoPage;
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

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
     * TC 001x Uploads a graph to the lod2stack.
     */
    @Test
    @Parameters({ "graphNameForLocalFile", "graphFilePath"})
    public void uploadGraphInVirtuosoFromFile(String graphName, String graphFilePath)  {
        VirtuosoPage.navigateToVirtuoso();
        // Get absolute paths.
        graphFilePath = getAbsolutePath(graphFilePath);
        // Check if required files exist
        assertTrue("Could not find graph file on local drive: " + graphFilePath, 
                bf.isLocalFileAvailable(graphFilePath));        

        // If not logged in: log into VirtuosoPage
        if (bf.isElementVisible(By.id("t_login_usr"))) {
           VirtuosoPage.loginVirtuoso();
        }
        
        VirtuosoPage.uploadDataToVirtuosoGraph(graphName, graphFilePath);
    }
    
    @Test
    @Parameters({"graphNameForPublicDataEuUpload", "searchPhraseEU", "resourceNmbrEU"})
    public void uploadGraphInVirtuosoFromPublicDataEu(String graphName, String searchPhrase, String resourceNmbr)  {
        String ur = DataPage.getRDFdataFromPublicDataEu(searchPhrase, resourceNmbr);
        VirtuosoPage.navigateToVirtuoso();
        VirtuosoPage.uploadDataToVirtuosoGraph(graphName, ur);
    }
    
    @Test
    @Parameters({"graphNameForDataHubUpload", "searchPhraseDH", "resourceNmbrDH"})
    public void uploadGraphInVirtuosoFromDataHub(String graphName, String searchPhrase, String resourceNmbr)  {
        String ur = DataPage.getRDFdataFromDataHub(searchPhrase, resourceNmbr);
        VirtuosoPage.navigateToVirtuoso();
        VirtuosoPage.uploadDataToVirtuosoGraph(graphName, ur);
    }
    
    /**
     * TC 003-x.
     * 
     * Tests transformation and uploading.
     */
    @Test
    @Parameters({ "xmlTextFile", "xsltTextFile", "exportGraphBasicExtraction" })
    public void basicExtraction(String xmlFile, String xsltFile, String exportGraph) {
        // Set absolute paths.
        xmlFile = getAbsolutePath(xmlFile);
        xsltFile = getAbsolutePath(xsltFile);
        // Check files availability 
        assertTrue("Could not find xmlFile on local drive: " + xmlFile, 
                bf.isLocalFileAvailable(xmlFile));
        assertTrue("Could not find xsltFile on local drive: " + xsltFile, 
                bf.isLocalFileAvailable(xsltFile));
        
        navigator.navigateTo(new String[] {
            "Extraction", 
            "Extract RDF from XML", 
            "Basic extraction"});
        
        ArrayList<String> xmlText = bf.readFile(xmlFile, false);
        ArrayList<String> xsltText = bf.readFile(xsltFile, false);
        
        // Wait for first element
        WebElement xmlField = bf.waitUntilElementIsVisible(
                By.id("EXML_xmlText"));
        WebElement xsltField = bf.waitUntilElementIsVisible(
                By.id("EXML_xsltText"));
        WebElement transformButton = bf.waitUntilElementIsVisible(
                By.id("EXML_transformButton"));
        WebElement uploadButton = bf.waitUntilElementIsVisible(
                By.id("EXML_uploadButton"));
        
        // Type into input fields
        String complete = "";
        for(String chars : xmlText)  {
            complete += chars;
            complete += "\n";
            //xmlField.sendKeys(chars);   // To slow.
        }
        bf.setValueViaJavaScript(xmlField, complete);
        xmlField.sendKeys(" ");
        
        transformButton.click();
        
        // No result should be displayed.
        assertFalse("Result is displayed, although required information is missing.",
                 bf.isElementVisible(By.id("EXML_rdfResultField")));
        complete = "";
        for(String chars : xsltText)  {
            complete += chars;
            complete += "\n";
            //xsltField.sendKeys(chars);
        }
        bf.setValueViaJavaScript(xsltField, complete);
        xsltField.sendKeys(" ");
        transformButton.click();
        // Wait for result to appear.
        WebElement resultField = bf.waitUntilElementIsVisible(
                "Transformation did not succeed. Result was not displayed.", 
                By.id("EXML_rdfResultField"));
        // Verify result containing an rdf - tag
        assertTrue("No rdf tags found.",resultField.getText().contains("<rdf:"));
        
        /*
        // Don't select a graph and try to upload
        uploadButton.click();
        bf.waitUntilElementIsVisible(
                "Upload was successful without any graph.", 
                By.xpath("//div[@class='v-label'][contains(.,'No graph selected')]"));
        */
        
        // Choose graph
        bf.handleSelector(By.id("ExportSelector_graphSelector"), exportGraph, false);
        uploadButton.click();
        // Wait for result to appear.
        WebElement uploadResult = bf.waitUntilElementIsVisible(
                "Upload was not successful because no message was visible.", 
                By.xpath("//div[@class='v-label'][contains(.,'Upload succeeded')]"));
    }

    
    /**
     * TC 004
     * 
     * Transforms and uploads.
     */
    @Test
    @Parameters({ "xmlFile", "xsltFile", "catalogFile", "exportGraphExtendedExtraction",})
    public void extendedExtraction(String xmlFile, String xsltFile, String catalogFile, String exportGraph) {
        // Set absolute paths.
        xmlFile = getAbsolutePath(xmlFile);
        xsltFile = getAbsolutePath(xsltFile);
        catalogFile = getAbsolutePath(catalogFile);
        // Check files available.
        assertTrue("Could not find xmlFile on local drive: " + xmlFile, 
                bf.isLocalFileAvailable(xmlFile));
        assertTrue("Could not find xsltFile on local drive: " + xsltFile, 
                bf.isLocalFileAvailable(xsltFile));
        assertTrue("Could not find catalogFile on local drive: " + catalogFile, 
                bf.isLocalFileAvailable(catalogFile));
        
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Extract RDF from XML", 
            "Extended extraction"});
        WebElement uploadButton = bf.getVisibleElement(
                By.id("EXMLExtended_uploadButton"));
        WebElement transformButton = bf.getVisibleElement(
                By.id("EXMLExtended_transformButton"));

        bf.handleFileUpload(By.id("EXMLExtended_uploadXMLFile"), xmlFile);
        // Check if upload is performed although neccessery fields have not been filled out.
        transformButton.click();
        assertFalse("XML was transformed although necessary information was missing.",
                bf.isElementVisible(By.id("EXMLExtended_textToAnnotateField")));
                
        bf.handleFileUpload(By.id("EXMLExtended_uploadXSLTFile"), xsltFile);
        // Check if upload is performed although neccessery fields have not been filled out.
        transformButton.click();
        assertFalse("XML was transformed although necessary information was missing.",
                bf.isElementVisible(By.id("EXMLExtended_textToAnnotateField")));
        
        bf.handleFileUpload(By.id("EXMLExtended_uploadCatalogFile"), catalogFile);
        transformButton.click();
        WebElement resultField = bf.waitUntilElementIsVisible(
                "No result is able after transformation.", 
                By.id("EXMLExtended_textToAnnotateField"));
        
        // Verify result containing an rdf - tag
        //assertTrue("Transformation result did not contain any rdf information.",
        //        resultField.getText().contains("<rdf:"));
        
        // Transformation completed start uploading.
        // Select export graph
        bf.handleSelector(By.id("ExportSelector_graphSelector"), exportGraph, false);
        uploadButton.click();
        // Wait for upload message.
        WebElement success = bf.waitUntilElementIsVisible(
                "Upload did not succeed. Message was not displayed.", 
                bf.getInfoPopupLocator());

        assertTrue("Upload did not succeed. Wrong message was displayed.",
                success.getText().contains("succeeded"));
    }
    
    /**
     * TC 005
     * 
     * Performs a download of rdf code after it is been transformed by 
     * Extended Extraction.
     * 
     */
    //@Test(dependsOnMethods={"extendedExtraction"})
    @Test
    @Parameters({"downloadFileName", "downloadFilePath" })
    public void downloadExtendedExtraction(String downloadFileName, String downloadFilePath)  {
        // Download file
        assertTrue("Download dialogue is missing.", bf.isElementVisible(By.id("EXMLExtended_dlFileName")));
        WebElement filename = bf.getVisibleElement(By.id("EXMLExtended_dlFileName"));
        WebElement filepath = bf.getVisibleElement(By.id("EXMLExtended_dlPath"));
        filename.sendKeys(downloadFileName);
        filepath.sendKeys(downloadFilePath);
        // Click download
        bf.getVisibleElement(By.id("EXMLExtended_downloadButton")).click();
        assertFalse("Error message appeared.",bf.isElementVisible(bf.getErrorPopupLocator()));
    }
    
    /**
     * TC 006.
     */
    @Test
    @Parameters({ "textFile" })
    public void annotatePlainText(String textFile)  {        
        // Set absolute paths.
        textFile = getAbsolutePath(textFile);

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
     * TC 007.
     */
    @Test
    @Parameters({ "exportGraphExtractor","poolPartyProjectId","language","textFile" })
    public void poolPartyExtractor(String exportGraph, String poolPartyProjectId, String language, String textFile)  {
        // Set absolute paths.
        textFile = getAbsolutePath(textFile);
        ArrayList<String> text = bf.readFile(textFile, false);
        
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Extract RDF from text w.r.t. a controlled vocabulary"});
        // TODO continue test case
        bf.getVisibleElement("Could not find poolparty project id input.", 
                By.id("EPoolPartyExtractor_ppProjectId")).sendKeys(poolPartyProjectId);
        bf.handleSelector(By.id("ExportSelector3_graphSelector"), exportGraph, false);
        bf.handleSelector(By.id("EPoolPartyExtractor_textLanguage"), language, false);
        
        // Type into input fields
        String complete = "";
        for(String chars : text)  {
            complete += chars;
            complete += "\n";
            //xmlField.sendKeys(chars);   // To slow.
        }
        
        WebElement textField =  bf.getVisibleElement(
                "Could not find text input field for poolparty concept extractor.",
                By.id("EPoolPartyExtractor_textToAnnotateField"));
        
        bf.setValueViaJavaScript(textField, complete);
        textField.sendKeys(" ");
        
        // Click extract
        bf.getVisibleElement("Could not find extract concepts button. ", 
                By.id("EPoolPartyExtractor_annotateButton")).click();
        
        bf.bePatient(1000);
        // TODO: What is the expected output.
    }

}