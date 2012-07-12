package eu.lod2.lod2testsuite.configuration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import eu.lod2.lod2testsuite.testcases.TestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import static org.testng.AssertJUnit.*;

/**
 *
 * @author Stefan Schurischuster
 */
public class BasicFunctions {
    public static int PATIENCE_MILLI_SECONDS = 900;
    public static int MAX_PATIENCE_SECONDS = 30;
    
    /**
     * Sets the local Thread to sleep.
     * 
     * @param sleeptime
     *          Sleeping time in milli seconds.
     */
    public void bePatient(int sleeptime)  {
        try  {
            //driverWait.wait(sleeptime);
            synchronized(TestCase.driver)  {
                TestCase.driver.wait(sleeptime);
            }
        } catch(InterruptedException e)  {
            Assert.fail("Could not interrupt Thread: "+e.getMessage());
        } 
    }
    
    /**
     * Returns an existing and visible WebElement from the webpage.
     * Throws an assert.fail if the element is not present or not visible.
     * 
     * @param locator
     *          A By object locator.
     * @return
     *          The existing and visible WebElement.
     */      
    public WebElement getExistingAndVisibleElement(By locator)  {
        WebElement element = null;
        try  {
            element = TestCase.driver.findElement(locator);
        } catch(NoSuchElementException e)  {
            Assert.fail(e.getMessage());
        }
        assertTrue("Element is not visible: " +element, element.isDisplayed());
        return element;
    }
    
    /**
     * Returns an existing WebElement from the webpage.
     * Throws an assert.fail if the element is not present.
     * 
     * @param locator
     *          A By object locator.
     * @return 
     *          The existing WebElement
     */
    public WebElement getExistingElement(By locator)  {
        WebElement element = null;
        try  {
            element = TestCase.driver.findElement(locator);
        } catch(NoSuchElementException e)  {
            Assert.fail(e.getMessage());
        }
        return element;
    }
    
    /**
     * Sets the current browser session to sleep until an element is present.
     * This element must be identified over its xpath.
     * 
     * @param xpath
     *          The idetifying xpath as a string.
     * @return 
     *          If the element was found before patience has ran out it is 
     *          returned.
     */
    public WebElement waitUntilElementIsPresent(By locator)  {
        WebDriverWait pageWait = new WebDriverWait(TestCase.driver, MAX_PATIENCE_SECONDS);
        
        WebElement element = null;
        try  {
            element = pageWait.until(
                ExpectedConditions.visibilityOfElementLocated(locator));
        } catch(NoSuchElementException e)  {
            Assert.fail(e.getMessage());
        }
        return element;
    }
    
    /**
     * @TODO Beautify!
     * 
     * @param failureMessage
     * @param locator
     * @return 
     */
    public WebElement waitUntilElementIsPresent(String failureMessage, By locator)  {
        WebDriverWait pageWait = new WebDriverWait(TestCase.driver, MAX_PATIENCE_SECONDS);
        pageWait.withMessage("Time expired: " +failureMessage);
        
        WebElement element = null;
        try  {
            element = pageWait.until(
                ExpectedConditions.visibilityOfElementLocated(locator));
        } catch(NoSuchElementException e)  {
            Assert.fail("Element not found: " +failureMessage 
                    + " Stack trace: " +e.getMessage());
        }
        return element;
    }
    
    /**
     * Waits until a certain element disappears.
     * 
     * @param locator 
     *          The locator of the element to disappear.
     */
    public void waitUntilElementDisappears(By locator)  {
        WebDriverWait pageWait = new WebDriverWait(TestCase.driver, MAX_PATIENCE_SECONDS);
        try  {
            pageWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch(NoSuchElementException e)  {
            Assert.fail(e.getMessage());
        }
    }
    
    /**
     * Sets the local Thread to sleep for a predefined period.
     */
    public void bePatient()  {
        bePatient(PATIENCE_MILLI_SECONDS);
    }
    
    /**
     * Reads a file from resources.
     * 
     * @param filename
     *          The name of the file to read.
     * @return 
     *          An ArrayList containing the lines of the file.
     */
    public ArrayList<String> readFile(String filename, boolean fromResource) {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            InputStreamReader isr = null;
            if(fromResource)
                isr = new InputStreamReader(getClass().getResourceAsStream(filename));
            else
                isr = new InputStreamReader(new FileInputStream(filename));
            
            BufferedReader br = new BufferedReader(isr);
            String x = "";
            while(( x = br.readLine()) != null)  { //get rid of first line? or later?
                lines.add(x);
            }
        } catch (Exception ex) {
            //Logger.getLogger(TestEditValuesFactory.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail("An error occured trying to read the input csv-file.");
        }
         return lines;   
    } 
    
    
    
}
