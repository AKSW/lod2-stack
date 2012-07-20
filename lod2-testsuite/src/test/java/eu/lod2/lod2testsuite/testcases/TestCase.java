package eu.lod2.lod2testsuite.testcases;

import com.thoughtworks.selenium.Selenium;
import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.MyWebDriverEventListener;
import eu.lod2.lod2testsuite.configuration.Navigator;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;


/**
 * Represents a base class for all testcases.
 *
 * @author Stefan Schurischuster
 */
public abstract class TestCase {
    private String url, ffProfile;

    public static WebDriver driver; 
    public static Selenium selenium;
    public static Actions driverActions;
    public static Navigator navigator;
    public static BasicFunctions bf;
    public static WebDriverEventListener eventListener;
    private static final Logger logger = Logger.getLogger(TestCase.class);
    
    /**
     * Initiallizes the browser and opens the website.
     * 
     * @param context 
     *          Contains the necessary metainformation from the testng.xml
     */
    @BeforeSuite(alwaysRun=true)
    public void setUp(ITestContext context)  {
        logger.info("STARTING");
        // Get parameters from testng.xml
        url = context.getCurrentXmlTest().getParameter("selenium.url");
        ffProfile = context.getCurrentXmlTest().getParameter("firefox.profile");        
        
        eventListener = new MyWebDriverEventListener();
        
        /*
        FirefoxProfile firefoxProfile = new FirefoxProfile(new File(ffProfile));
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        LoggingPreferences logs = new LoggingPrferences();
        logs.enable(LogType.DRIVER, Level.OFF);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
        // @TODO  add firefox prfile to capabilities.
        //capabilities.setCapability(CapabilityType.firefoxProfile);
        //driver = new FirefoxDriver(firefoxProfile);
         */
        
        
        // Choose the right driver
        //driver = new FirefoxDriver(capabilities);
        //driver = new FirefoxDriver();
        
        driver = new EventFiringWebDriver(
                 new FirefoxDriver()).register(eventListener);
        // Set implicit waitingtime when a field is not available
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        
        selenium = new WebDriverBackedSelenium(driver, url);
        driverActions = new Actions(driver);
        navigator = new Navigator(driver);
        bf = new BasicFunctions(driver);

        // Open Website
        selenium.open(url);
        
        WebElement elem = bf.waitUntilElementIsVisible(
                By.xpath("//img[contains(@src,'lifecycle')]"));
    }
    
    
    /**
     * This method is run after every testmethod and returns the focus
     * back to the main window. This is necessery when switching iframes.
     */
    @BeforeMethod(alwaysRun=true)
    public void prepareTestCase()  {
        driver.switchTo().defaultContent();
        logger.debug("Switching to default frame.");
    }
    
    /**
     * Error messages from former testcases can interfear with current testcases.
     * Therefore if an error message is present after a testcase has run it 
     * has to be closed.
     */
    @AfterMethod(alwaysRun=true)
    public void afterTestCase()  {
        // Error message is visible.
        if(bf.isElementVisible(By.xpath("//div[@class='gwt-HTML']/../..[contains(@class,'error')]")))  {
            WebElement message =  bf.getVisibleElement(
                    By.xpath("//div[@class='gwt-HTML']/../..[contains(@class,'error')]"));
            
            logger.fatal("Error message is visible with text: " + message.getText());
            
            message.click();
            bf.waitUntilElementDisappears(By.xpath("//div[@class='gwt-HTML']"));   
            
            // Can not throw an exception because that would result in skipping of all following tests.
            //Assert.fail("Error message is visible after testcase");
        }
    }
    
    /**
     * Stops browser.
     */
    @AfterSuite(alwaysRun=true)
    public void tearDown()  {
        logger.info("STOPPING");
        //Insteat of driver.quit();
        //driver.quit();
        selenium.stop();
    }   
}
