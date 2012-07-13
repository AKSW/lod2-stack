package eu.lod2.lod2testsuite.configuration;

import org.openqa.selenium.NoSuchElementException;
import java.util.List;
import junit.framework.Assert;
import eu.lod2.lod2testsuite.testcases.TestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import static org.testng.AssertJUnit.*;

/**
 * 
 * @author Stefan Schurischuster
 */
public class Navigator {
    
    /**
     * This methods navigates through the main link menu of lod2-stack.
     * 
     * @param path 
     *          The path of the links that are to be clicked.
     */
    public void navigateTo(String[] path)  {
        int index = 0;
        WebElement link = null;
       
        while (index < path.length)  {
            String identifier = "//span[contains(.,'" +path[index]+ "')]"
                    + "[not(contains(@class,'caption'))]";
            if(index > 0)
                identifier = "//div[@class = 'v-menubar-popup'][last()]" +identifier;
            
            try  {
                link = TestCase.driver.findElement(
                                By.xpath(identifier));
            } catch(NoSuchElementException e)  {
                Assert.fail("Element not found: "+e.getMessage());
            }
            
            assertTrue("Could not find link: "+ link,link.isDisplayed());
            
            // This should pop up dropdowns.
            if(index > 1)  {
                // First move to the first avialiable popup item.
                TestCase.driverActions.moveToElement(
                        TestCase.driver.findElement(
                        By.xpath("//div[@class = 'v-menubar-popup'][last()]"
                        + "//span[contains(@class,'v-menubar-menuitem')][1]"
                        + "[not(contains(@class,'caption'))]")));
                 
                TestCase.bf.bePatient();  
            }
            
            // And then move to the desired link
            TestCase.driverActions.moveToElement(link).build().perform();
            
            // If it does not clicking does the job.
            link.click();
            index ++;
            TestCase.bf.bePatient();  
        } 
    }
    
    /**
     * @TODO
     * @param path 
     */
    public void navigateTo(List<String> path)  {
        navigateTo(path.toArray(new String[0]));
    }
}
