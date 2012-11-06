package eu.lod2.lod2testsuite.configuration;

import eu.lod2.lod2testsuite.testcases.TestCase;
import java.util.List;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import static org.testng.AssertJUnit.*;

/**
 * This class handles navigation through the main link menu 
 * of the lod2-stack.
 * 
 * @author Stefan Schurischuster
 */
public class Navigator {
    private static final Logger logger = Logger.getLogger(Navigator.class);
    private WebDriver driver;
    private Actions driverActions;
    
    public Navigator(WebDriver driver)  {
        this.driver = driver;
        this.driverActions = new Actions(driver);
    }
    
    /**
     * This methods navigates through the main link menu of lod2-stack.
     * 
     * @param path 
     *          An array containing the names of the links that are to be clicked.
     *          No ' (single quotes) are allowed. So in case a link has a single
     *          quote inside just try parts of the link name. For example:
     *          "Europe's Public Data" try it with "s Public Data". 
     */
    public void navigateTo(String[] path)  {
        int index = 0, x = 0;
        WebElement link = null;
        String pp = "";
        
        for(String p : path)  {
            if(x==0) {  
                pp = p;
            }
            else {
                pp += " -> " +p;
            }
            x++;
        }
        logger.info("Navigating to: " + pp);
        
        while (index < path.length)  {
            /*
            // Prepare link if it contains " ' " a quote. For example: Europe's Public Data
            if(path[index].contains("'"))  {
                logger.info("------------contains! "+ path[index].split("'")[0] + " " + path[index].split("'")[1]);
            }
            */
            String identifier = "//span[contains(.,'" +path[index]+ "')]"
                    + "[not(contains(@class,'caption'))]";
            if(index > 0) {
                identifier = "//div[@class = 'v-menubar-popup'][last()]" +identifier;
            }
            try  {
                link = driver.findElement(
                                By.xpath(identifier));
            } catch(NoSuchElementException e)  {
                Assert.fail("Element not found: "+e.getMessage());
            }
            
            assertTrue("Could not find link: "+ link,link.isDisplayed());
            
            // This should pop up dropdowns.
            if(index > 1)  {
                // First move to the first avialiable popup item.
                driverActions.moveToElement(
                        driver.findElement(
                        By.xpath("//div[@class = 'v-menubar-popup'][last()]"
                        + "//span[contains(@class,'v-menubar-menuitem')][1]"
                        + "[not(contains(@class,'caption'))]")));
                 
            }
            
            // And then move to the desired link
            driverActions.moveToElement(link).build().perform();
            
            // If it does not clicking does the job.
            link.click();
            index ++;
            TestCase.bf.bePatient();  
        } 
        logger.info("Finished navigating to: " + pp);
    }
    
    /**
     * This methods navigates through the main link menu of lod2-stack.
     * 
     * @param path 
     *          A list containing the names of the links that are to be clicked.
     *          No ' (single quotes) are allowed. So in case a link has a single
     *          quote inside just try parts of the link name. For example:
     *          "Europe's Public Data" try it with "s Public Data". 
     */
    public void navigateTo(List<String> path)  {
        navigateTo((String[]) path.toArray());
    }
}
