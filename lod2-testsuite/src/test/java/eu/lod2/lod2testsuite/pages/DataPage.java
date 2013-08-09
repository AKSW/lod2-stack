/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.lod2.lod2testsuite.pages;

import eu.lod2.lod2testsuite.configuration.TestCase;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


/**
 *
 * @author Stefan Schurischuster
 */
public class DataPage {
    private static Logger logger = Logger.getLogger(DataPage.class);
    
    /**
     * Navigates to Public Data and searches for a data source.
     * 
     * @param searchPhrase
     *          The search phrase to search for.
     * @param resourceNmbr
     *          After clicking a search hit, there are a number of resources
     *          that can be used. This number refers to the number of the resource
     *          in this list. e.g. 2 clicks the second resource.
     * @return 
     *          The url to a rdf source
     */
    public static String getRDFdataFromPublicDataEu(String searchPhrase, String resourceNmbr)  {
        TestCase.navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Load RDF data from publicdata.eu"});

        By frameIdentifier = By.xpath("//iframe[contains(@src,'publicdata.eu')]");
        TestCase.bf.checkIFrame(
                frameIdentifier, 
                By.id("dataset-search"));
        
       return loadData(searchPhrase, Integer.parseInt(resourceNmbr), frameIdentifier);
    }
    
    /**
     * Navigates to Data Hub and searches for a data source.
     * 
     * @param searchPhrase
     *          The search phrase to search for.
     * @param resourceNmbr
     *          After clicking a search hit, there are a number of resources
     *          that can be used. This number refers to the number of the resource
     *          in this list. e.g. 2 clicks the second resource.
     * @return 
     *          The url to a rdf source
     */
    public static String getRDFdataFromDataHub(String searchPhrase, String resourceNmbr)  {
        TestCase.navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Load LOD cloud RDF data from the Data Hub"});
        By frameIdent = By.xpath("//iframe[contains(@src,'datahub.io')]");
        TestCase.bf.checkIFrame(
                frameIdent, 
                By.id("dataset-search")); 
       return loadData(searchPhrase, Integer.parseInt(resourceNmbr), frameIdent);
    }
    
    /**
     * Returns a url to a rdf resource.
     * pre: data loading page e.g. publicdata.eu is opened.
     * 
     * @param searchPhrase
     *          The search phrase to search for.
     * @param resourceNmbr
     *          After clicking a search hit, there are a number of resources
     *          that can be used. This number refers to the number of the resource
     *          in this list. e.g. 2 clicks the second resource.
     * @return 
     *          The url to a rdf source
     */
    public static String loadData(String searchPhrase, int resourceNmbr, By frameIdentifier)   {
        // Search
        By searchField = By.xpath("//form[@id='dataset-search']//input");
        // Type
        TestCase.bf.waitUntilElementIsVisible("Could not find search field.", 
                searchField).clear();
        TestCase.bf.waitUntilElementIsVisible("Could not find search field.", 
                searchField).sendKeys(searchPhrase);
        TestCase.bf.getVisibleElement("Could not find search field.", 
                searchField).sendKeys(Keys.ENTER);
        
        // Click first result 
        TestCase.bf.waitUntilElementIsVisible("No result was displayed for search phrase: " + searchPhrase, 
                By.xpath("//li[@class='dataset-item']/descendant::a[1]"
                + "[contains(@href,'dataset')]")).click();
        
        //TestCase.bf.waitUntilElementIsPresent("",By.id("dataset-resources"));
        TestCase.bf.checkIFrame(
                frameIdentifier, 
                By.id("dataset-resources"));
        // Click rdf
        try  {
            TestCase.bf.getExisitingElement("Could not find rdf link to selected source", 
                By.xpath("//li[" +resourceNmbr+ "]//a[@class='heading']")).click();
        } catch(Exception e)  {
            
            //Try again with css selector
            logger.warn("Try again with css selector");
            TestCase.bf.waitUntilElementIsPresent("Could not find rdf link to selected source", 
                By.cssSelector("li:nth-child(" +resourceNmbr+ ") a.heading")).click();
        }
        
        // Get Link
        String link = TestCase.bf.waitUntilElementIsPresent("Could not find rdf url.", 
                By.cssSelector("p.muted a")).getAttribute("href");
        //By.xpath("//p[contains(@class,'muted')]//a[contains(@href,'rdf')]")
        
        logger.info("This is the link to rdf data: "+link);
        return link;
    }
}
