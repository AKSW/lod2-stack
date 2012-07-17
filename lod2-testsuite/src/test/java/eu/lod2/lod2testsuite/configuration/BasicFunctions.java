package eu.lod2.lod2testsuite.configuration;

import java.util.List;
import java.util.logging.Logger;
import java.io.File;
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
    private static final Logger logger = Logger.getLogger(BasicFunctions.class.getName());
    
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
     * Sets the local Thread to sleep for a predefined period.
     */
    public void bePatient()  {
        bePatient(PATIENCE_MILLI_SECONDS);
    }
    
    /**
     * Waits and performs a isVisible check on a specific iframe and checks weather 
     * a frame specific element is visible.
     * 
     * @param frameIdentifier
     *          The identifier refering to the iframe element.
     * @param contentIdentifier
     *          The identifier refering to an element inside the frame.
     * 
     * @TODO Maybe add collecton for multiple content identifiers.
     */
    public void checkIFrame(By frameIdentifier, By contentIdentifier)  {
        WebElement iframe = waitUntilElementIsVisible(frameIdentifier);
        
        TestCase.driver.switchTo().frame(iframe);
        System.out.println("Switched to iframe");
        
        WebElement contentElement = waitUntilElementIsVisible(
                "Iframe content was not correctly displayed.",
                contentIdentifier);
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
     * Handles an vaadin file upload and checks wheather the describing text
     * contains the uploaded file after clicking the button.
     * 
     * @param locator
     *              A By object referencing to the <form> tag.
     * @param pathToFile 
     *              Path of the file to upload.
     */
    public void handleFileUpload(By locator, String pathToFile)  {
        WebElement field = TestCase.bf.waitUntilElementIsVisible(locator);
        
        WebElement input = null;
        WebElement button = null;
        
        try { // This should search relativly to selector.
            field.findElement(By.className("gwt-FileUpload"));
            field.findElement(By.className("v-button"));
        } catch (NoSuchElementException e) {
            Assert.fail(e.getMessage());
        }
        input.sendKeys(pathToFile);
        File file = new File(pathToFile);
        
        button.click();
        
        WebElement uploaded = TestCase.bf.waitUntilElementIsVisible(
                "File was not successfully uploaded.", By.xpath(
                "//div[@class='v-captiontext'][contains(.,'" +file.getName()+ "')]"));
    }
    
    /**
     * Handles vaadin filterselects, chooses and writes values.
     * @TODO Is there a way to get the value of the selector?
     * 
     * @param locator
     *          The locator of the div - element representing the filterselector.
     * @param value
     *          The value to be choosen or written into the selector.
     * @param typeValue 
     *          true types value. false chooses value from popup.
     */
    public void handleSelector(By locator, String value, boolean typeValue)  {
         WebElement selector = TestCase.bf.waitUntilElementIsVisible(locator);
         WebElement input = null;
         WebElement button = null;
         
         try { // This should search relativly to selector.
             input = selector.findElement(By.className("v-filterselect-input"));
             button = selector.findElement(By.className("v-filterselect-button"));
         } catch(NoSuchElementException e)  {
             Assert.fail(e.getMessage());
         }
         
         if(typeValue)  {
            input.sendKeys(value);
         } else  {
             button.click();
             WebElement popUpElement = waitUntilElementIsVisible(
                     "Selector popup menu did not open", 
                     By.xpath("//div[contains(@class,'popupContent')]//"
                     + "td[contains(@class,'gwt-MenuItem')]"
                     + "/span[text() = '" +value+ "']"));
             
             popUpElement.click();
             bePatient();
         }
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
    
    /**
     * Sets the current browser session to sleep until an element is present.
     * This element must be identified over its xpath.
     * 
     * @param locator
     *          The identifying By object.
     * @return 
     *          If the element was found before patience has ran out it is 
     *          returned.
     */
    public WebElement waitUntilElementIsVisible(By locator)  {
        return waitUntilElementIsVisible("", locator);
    }
    
    /**
     * Sets the current browser session to sleep until an element is present.
     * This element must be identified over its xpath.
     * 
     * @param failureMessage 
     *          The failure message to be displayed when an error appears.
     * @param locator
     *          The identifying By object.
     * @return 
     *          If the element was found before patience has ran out it is 
     *          returned.
     */
    public WebElement waitUntilElementIsVisible(String failureMessage, By locator)  {
        WebDriverWait pageWait = new WebDriverWait(TestCase.driver, MAX_PATIENCE_SECONDS);
        if(!failureMessage.isEmpty())
            pageWait.withMessage("Time expired: " +failureMessage);
        WebElement element = null;
        try  {
            element = pageWait.until(
                ExpectedConditions.visibilityOfElementLocated(locator));
        } catch(NoSuchElementException e)  {
            if(!failureMessage.isEmpty())
                Assert.fail("Element not found: " +failureMessage 
                    + " Stack trace: " +e.getMessage());
            else
                Assert.fail(e.getMessage());
        }
        return element;
    }
    
     /**
     * Sets the current browser session to sleep until an element is present.
     * This element must be identified over its xpath.
     * 
     * @param failureMessage 
     *          The failure message to be displayed when an error appears.
     * @param locator
     *          The identifying By object.
     * @return 
     *          All elements of the locator
     */
    public List<WebElement> waitUntilElementsAreVisible(String failureMessage, By locator)  {
        WebDriverWait pageWait = new WebDriverWait(TestCase.driver, MAX_PATIENCE_SECONDS);
        if(!failureMessage.isEmpty())
            pageWait.withMessage("Time expired: " +failureMessage);
        List<WebElement> elements = null;
        
        WebElement firstElement = null;
        try  {
            firstElement = pageWait.until(
                ExpectedConditions.visibilityOfElementLocated(locator));
            
            // Get multiple WebElements
            elements = TestCase.driver.findElements(locator);

        } catch(NoSuchElementException e)  {
            if(!failureMessage.isEmpty())
                Assert.fail("Element not found: " +failureMessage 
                    + " Stack trace: " +e.getMessage());
            else
                Assert.fail(e.getMessage());
        }
        return elements;
    }  
    
    
    /**
     * @param failureMessage
     * @param locator 
     *          The locator of the element to disappear.
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
}