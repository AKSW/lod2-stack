package eu.lod2.lod2testsuite.testcases;

import com.thoughtworks.selenium.Selenium;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.configuration.Navigator;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

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
    
    
    /**
     * Initiallizes the browser and opens the website.
     * 
     * @param context 
     *          Contains the necessary metainformation from the testng.xml
     */
    @BeforeSuite(alwaysRun=true)
    public void setUp(ITestContext context)  {
        System.out.println("STARTING");

        // Get parameters from testng.xml
        url = context.getCurrentXmlTest().getParameter("selenium.url");
        ffProfile = context.getCurrentXmlTest().getParameter("firefox.profile");        
        
        FirefoxProfile firefoxProfile = new FirefoxProfile(new File(ffProfile));
        
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.DRIVER, Level.ALL);
        
        
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        
        //TODO  add firefox prfile to capabilities.
        //capabilities.setCapability(CapabilityType.firefoxProfile);
        
        // Choose the right driver
        //driver = new FirefoxDriver(firefoxProfile);
        driver = new FirefoxDriver(capabilities);
        
        ((RemoteWebDriver) driver).setLogLevel(Level.ALL); 
        selenium = new WebDriverBackedSelenium(driver, url);
        driverActions = new Actions(driver);
        navigator = new Navigator();
        bf = new BasicFunctions();

        // Open Website
        //driver.get(url);
        selenium.open(url);
        WebElement elem = bf.waitUntilElementIsPresent(
                By.xpath("//img[contains(@src,'lifecycle')]"));
    }
    
    /**
     * Stops browser.
     */
    @AfterSuite(alwaysRun=true)
    public void tearDown()  {
        System.out.println("STOPPING"); 
        //Insteat of driver.quit();
        //driver.quit();
        selenium.stop();
    }   
}
