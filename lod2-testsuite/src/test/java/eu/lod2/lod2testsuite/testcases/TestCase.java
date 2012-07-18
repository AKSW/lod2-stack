package eu.lod2.lod2testsuite.testcases;

import com.thoughtworks.selenium.Selenium;
import java.io.File;
import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.MyWebDriverEventListener;
import eu.lod2.lod2testsuite.configuration.Navigator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;


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
        navigator = new Navigator();
        bf = new BasicFunctions();

        // Open Website
        selenium.open(url);
        
        WebElement elem = bf.waitUntilElementIsVisible(
                By.xpath("//img[contains(@src,'lifecycle')]"));
    }
    
    
    /**
     * This method is run after every testmethod and returns the focus
     * back to the main window. This is necessery when switching iframes.
     */
    @BeforeMethod
    public void prepareTestCase()  {
        driver.switchTo().defaultContent();
        logger.debug("Switching to default frame.");
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
