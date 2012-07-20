package eu.lod2.lod2testsuite.configuration;

import org.openqa.selenium.JavascriptExecutor;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import java.util.List;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    public static int MAX_PATIENCE_SECONDS = 15;
    private static final Logger logger = Logger.getLogger(BasicFunctions.class);
    private WebDriver driver;
    
    
    public BasicFunctions(WebDriver driver) {
        this.driver = driver;
    }
    
    /**
     * Sets the local Thread to sleep.
     * 
     * @param sleeptime
     *          Sleeping time in milli seconds.
     */
    public void bePatient(int sleeptime)  {
        try  {
            //driverWait.wait(sleeptime);
            synchronized(driver)  {
                driver.wait(sleeptime);
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
     * @Notice Maybe add collecton for multiple content identifiers.
     */
    public void checkIFrame(By frameIdentifier, By contentIdentifier)  {
        WebElement iframe = waitUntilElementIsVisible(frameIdentifier);
        driver.switchTo().frame(iframe);
        
        WebElement contentElement = waitUntilElementIsVisible(
                "Iframe content was not correctly displayed.",
                contentIdentifier);
    }
  
    /**
     * Tries to create a WebElement using the passed locator.
     * 
     * @param locator 
     *          The locator of the element.
     * @return 
     *      If an exception is thrown it returns false, true otherwise.
     */
    public boolean isElementPresent(By locator)  {
        try  {
            WebElement element = driver.findElement(locator);
        } catch(NoSuchElementException e)  {
            return false;
        } catch(Exception e)  {
            Assert.fail(e.getMessage());
        }
        return true;
    }
    
    /**
     * Tries to create a WebElement using the passed locator and checks 
     * wheather it is visible and displayed on the webpage.
     * 
     * @param locator 
     *          The locator of the element.
     * @return 
     *      If an exception is thrown or element is hidden it returns false,
     *      true otherwise.
     */
    public boolean isElementVisible(By locator)  {
        WebElement element = null;
        try  {
           element = driver.findElement(locator);
        } catch(NoSuchElementException e)  {
            return false;
        } catch(Exception e)  {
            Assert.fail(e.getMessage());
        }
        return element.isDisplayed();
    }
    
    /**
     * @return 
     *       Returns the locator of a vaadin error message.
     */
    public By getErrorPopupLocator()  {
        return By.xpath("//div[@class='gwt-HTML']/../..[contains(@class,'error')]");
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
    public WebElement getVisibleElement(By locator)  {
        WebElement element = null;
        try  {
            element = driver.findElement(locator);
        } catch(NoSuchElementException e)  {
            Assert.fail(e.getMessage());
        }
        assertTrue("Element is not visible: " +element, element.isDisplayed());
        return element;
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
    public WebElement getVisibleElement(String failureMessage, By locator)  {
        WebElement element = null;
        try  {
            element = driver.findElement(locator);
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
            element = driver.findElement(locator);
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
        WebElement field = waitUntilElementIsVisible(locator);
        
        WebElement input = null;
        WebElement button = null;
        
        try { // This should search relativly to selector.
            input = field.findElement(By.className("gwt-FileUpload"));
            button = field.findElement(By.className("v-button"));
        } catch (NoSuchElementException e) {
            Assert.fail(e.getMessage());
        }
        input.sendKeys(pathToFile);
        File file = new File(pathToFile);
        
        button.click();
        
        WebElement uploaded = waitUntilElementIsVisible(
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
         WebElement selector = waitUntilElementIsVisible(locator);
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
                     "Slector Element not found.", 
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
     * Sets the value of a field using javascript. This may not trigger the same
     * events as sendKeys does!
     * 
     * @param element
     *          The WebElement to be set.
     * @param value 
     *          The value that is used.
     */
    public void setValue(WebElement element, String value) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].value = arguments[1]", element, value);
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
        return waitUntilElementIsVisible(failureMessage, locator, MAX_PATIENCE_SECONDS);
    }
    
    /**
     * Sets the current browser session to sleep until an element is present.
     * This element must be identified over its xpath.
     * 
     * @param failureMessage 
     *          The failure message to be displayed when an error appears.
     * @param locator
     *          The identifying By object.
     * @param maxPatienceSeconds 
     *          Maximum time to wait before throwing an error if the element is
     *          not visible.
     * @return 
     *          If the element was found before patience has ran out it is 
     *          returned.
     */
    public WebElement waitUntilElementIsVisible(String failureMessage, By locator, int maxPatienceSeconds)  {
        WebDriverWait pageWait = new WebDriverWait(driver, maxPatienceSeconds);
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
        WebDriverWait pageWait = new WebDriverWait(driver, MAX_PATIENCE_SECONDS);
        if(!failureMessage.isEmpty())
            pageWait.withMessage("Time expired: " +failureMessage);
        List<WebElement> elements = null;
        
        WebElement firstElement = null;
        try  {
            firstElement = pageWait.until(
                ExpectedConditions.visibilityOfElementLocated(locator));
            
            // Get multiple WebElements
            elements = driver.findElements(locator);

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
        WebDriverWait pageWait = new WebDriverWait(driver, MAX_PATIENCE_SECONDS);
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
     * @param failureMessage
     *          The message to be displayed if an error occures.
     * @param maxPatienceSeconds   
     *          The max time to wait before throwing an error.
     */
    public void waitUntilElementDisappears(String failureMessage, By locator, int maxPatienceSeconds)  {
        WebDriverWait pageWait = new WebDriverWait(driver, maxPatienceSeconds);
        try  {
            pageWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch(NoSuchElementException e)  {
            Assert.fail(e.getMessage());
        } catch(Exception e)  {
            Assert.fail(failureMessage);
        }
    }
    
    /**
     * Waits until a certain element disappears.
     * Maximum patience is predifined in BasicFunctions.class.
     * 
     * @param locator 
     *          The locator of the element to disappear.
     * @param failureMessage
     *          The message to be displayed if an error occures.
     */
    public void waitUntilElementDisappears(String failureMessage, By locator)  {
        waitUntilElementDisappears(failureMessage, locator, MAX_PATIENCE_SECONDS);
    }
    
    /**
     * Waits until a certain element disappears.
     * Maximum patience is predifined in BasicFunctions.class.
     * No specific failure message will be displayed, only the message of the
     * exception.
     * 
     * @param locator 
     *          The locator of the element to disappear.
     */
    public void waitUntilElementDisappears(By locator)  {
        waitUntilElementDisappears("", locator, MAX_PATIENCE_SECONDS);
    }
}