/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.lod2.lod2testsuite.pages;

import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.Navigator;
import eu.lod2.lod2testsuite.configuration.TestCase;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 *
 * @author Stefan Schurischuster
 */
public class DataPage extends Page {
    private static Logger logger = Logger.getLogger(DataPage.class);
    
    private WebDriver driver;
    private BasicFunctions bf;
    private Navigator navigator;
    
    /**
     * 
     * @param frameIdentifier 
     *          Identifier for the frame that layers Virtuoso.
     */
    public DataPage(By frameIdentifier)  {
        super(frameIdentifier);
        this.driver = TestCase.driver;
        this.bf = TestCase.bf;
        this.navigator = TestCase.navigator;
    }
    
    
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
    public String getRDFdataFromPublicDataEu(String searchPhrase, String resourceNmbr)  {
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Load RDF data from publicdata.eu"});

        bf.checkIFrame(frameIdentifier, By.cssSelector("div.module-content")); 
        
        return loadData(searchPhrase, Integer.parseInt(resourceNmbr));
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
    public String getRDFdataFromDataHub(String searchPhrase, String resourceNmbr)  {
        navigator.navigateTo(new String[] {
            "Extraction & Loading", 
            "Load LOD cloud RDF data from the Data Hub"});
       
        bf.checkIFrame(frameIdentifier, By.cssSelector("div.module-content")); 
        
        return loadData(searchPhrase, Integer.parseInt(resourceNmbr));
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
    public String loadData(String searchPhrase, int resourceNmbr)   {
        bf.checkIFrame(frameIdentifier, By.cssSelector("div.module-content"));
        // Search
        By searchField = By.xpath("//*[@class='module-content']//input[@class='search']");
        // Type
        bf.waitUntilElementIsVisible("Could not find search field.", 
                searchField).clear();
        bf.waitUntilElementIsVisible("Could not find search field.", 
                searchField).sendKeys(searchPhrase);
        bf.getVisibleElement("Could not find search field.", 
                searchField).sendKeys(Keys.ENTER);
        
        // Click first result 
        bf.waitUntilElementIsVisible("No result was displayed for search phrase: " + searchPhrase, 
                By.xpath("//li[@class='dataset-item']/descendant::a[1]"
                + "[contains(@href,'dataset')]")).click();
        
        //bf.waitUntilElementIsPresent("",By.id("dataset-resources"));
        bf.checkIFrame(frameIdentifier, By.id("dataset-resources"));
        // Click rdf
        try  {
            bf.getExisitingElement("Could not find rdf link to selected source", 
                By.xpath("//li[" +resourceNmbr+ "]//a[@class='heading']")).click();
        } catch(Exception e)  {
            
            //Try again with css selector
            logger.warn("Try again with css selector");
            bf.waitUntilElementIsPresent("Could not find rdf link to selected source", 
                By.cssSelector("li:nth-child(" +resourceNmbr+ ") a.heading")).click();
        }
        
        // Get Link
        String link = bf.waitUntilElementIsPresent("Could not find rdf url.", 
                By.cssSelector("p.muted a")).getAttribute("href");
        //By.xpath("//p[contains(@class,'muted')]//a[contains(@href,'rdf')]")
        
        logger.info("This is the link to rdf data: "+link);
        return link;
    }
}
