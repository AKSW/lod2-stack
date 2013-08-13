/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.lod2.lod2testsuite.pages;

import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.Navigator;
import eu.lod2.lod2testsuite.configuration.TestCase;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Stefan Schurischuster
 */
public class VirtuosoPage {
    private static Logger logger = Logger.getLogger(VirtuosoPage.class);
    
    private WebDriver driver;
    private BasicFunctions bf;
    private Navigator navigator;
    private By frameIdentifier;
    
    /**
     * 
     * @param frameIdentifier 
     *          Identifier for the frame that layers Virtuoso.
     */
    public VirtuosoPage(By frameIdentifier)  {
        this.driver = TestCase.driver;
        this.bf = TestCase.bf;
        this.navigator = TestCase.navigator;
        
        this.frameIdentifier = frameIdentifier;
    }
    /**
     * Navigates to Virtuoso and switches frame.
     */
    public void navigateToVirtuoso()  {
         navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Upload RDF file or RDF from URL"});
        // Check if Iframe is visible and shows VirtuosoPage.
        bf.checkIFrame(
                frameIdentifier, 
                By.id("MTB"));
    }
    
    /**
     * Navigates to quadStoreUpload tab of Virtuoso.
     * pre: VirtuosoPage is opened.
     */
    public void navigateToQuadStoreUploadTab()  {
        // Click on Linked Data tab
        bf.waitUntilElementIsVisible("Could not find LinkedData tab",
                By.linkText("Linked Data")).click();
        // Wait and click on Quad Store Upload
        bf.waitUntilElementIsVisible("Could not find sub tab Quad Store Upload.",
                By.linkText("Quad Store Upload")).click();
    }
    
    /**
     * Logs into Virtuoso.
     * pre: VirtuosoPage is opened; Login field is visible.
     */
    public void loginVirtuoso()  {
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
    
    /**
     * Performs the steps to upload a source to a graph in VirtuosoPage.
     * pre: VirtuosoPage is opened; Quad Store Upload tab is opened.
     * 
     * @param graphName
     *          The title of the graph to upload.
     * @param dataSource 
     *          The source of the data to upload, can either be a url or
     *          a local path to a rdf file.
     */
    public void uploadDataToVirtuosoGraph(String graphName, String dataSource)  {
        boolean isUrl = true;
        try{
            // Check if dataSource is a url or a local path
            new URL(dataSource);
        } catch(MalformedURLException e)  {
            isUrl = false;
        }
        if(isUrl)  {
            // Click radio button for url upload
            bf.getVisibleElement("Could not find url upload radio button.",
                    By.xpath("//input[@value='ur']")).click();
            bf.waitUntilElementIsVisible("Could not find url upload field.", 
                    By.xpath("//input[@name='t_rdf_url']")).sendKeys(dataSource);
        } else {
            // Click radio button for file upload
            bf.getVisibleElement("Could not find file upload radio button.",
                    By.xpath("//input[@value='fs']")).click();
            bf.waitUntilElementIsVisible("Could not find file upload field.", 
                    By.xpath("//input[@name='t_rdf_file']")).sendKeys(dataSource);
        }
        // Fill in graphName
        WebElement graph = bf.waitUntilElementIsVisible("Graph input field not found.",
                By.xpath("//input[@name='rdf_graph_name']"));
        // Type
        graph.clear();
        graph.sendKeys(graphName);
         // Click upload
        bf.getVisibleElement("Could not find submit button.", By.name("bt1")).click();
        // Wait for upload to finish
        bf.waitUntilElementIsVisible("Upload did not finish",
                By.xpath("//div[@class='message'][contains(.,'Upload finished')]"), 30);
    }
    
}
