package eu.lod2.lod2testsuite.configuration;

import com.thoughtworks.selenium.Selenium;
import eu.lod2.lod2testsuite.configuration.testng.BrowserType;
import static eu.lod2.lod2testsuite.configuration.testng.BrowserType.*;
import eu.lod2.lod2testsuite.configuration.testng.FirefoxProfileConfig;
import eu.lod2.lod2testsuite.configuration.testng.MyWebDriverEventListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.Assert;
import static org.testng.AssertJUnit.*;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;


/**
 * Represents a base class for all test cases.
 *
 * @author Stefan Schurischuster
 */
public abstract class TestCase {
    protected String url;

    public static WebDriver driver; 
    public static Selenium selenium;
    public static Actions driverActions;
    public static Navigator navigator;
    public static BasicFunctions bf;
    public static WebDriverEventListener eventListener;
    public static BrowserType browserType;
    public static String systemArchitecture;
    public static String localFilesDirectory;
    
    protected static final Logger logger = Logger.getLogger(TestCase.class);
    
    /**
     * Initialises browser and opens the web page.
     * 
     * @param context 
     *          Contains the necessary meta information from the testng.xml
     */
    @BeforeSuite(alwaysRun=true)
    public void setUp(ITestContext context) {
        logger.info("STARTING");
        // Get parameters from testng.xml
        url = context.getCurrentXmlTest().getParameter("selenium.url");
        systemArchitecture = context.getCurrentXmlTest().getParameter("system.architecture");
        
        String posBrowser = context.getCurrentXmlTest().getParameter("browser.type");
        // Get correct browser object
        for (BrowserType b : BrowserType.values()) {
            if (b.toString().toLowerCase().equals(posBrowser)) {
                browserType = b;
            }
        }
        
        assertNotNull("Browser: '"+posBrowser+ "' could not be correctly identified.",
                browserType);
        
        localFilesDirectory = System.getProperty("user.dir") + File.separator + "files";
        logger.info("Projects root directory is "+System.getProperty("user.dir"));
        logger.info("Projects file directory is "+localFilesDirectory);
        
        eventListener = new MyWebDriverEventListener();
        
        if (browserType.equals(CHROME)) {
            System.setProperty("webdriver.chrome.driver", localFilesDirectory 
                    + File.separator + "chrome" + File.separator +"chromedriver"+systemArchitecture);
            DesiredCapabilities capabilities = DesiredCapabilities.chrome();            
            capabilities.setCapability("chrome.switches", Arrays.asList("--no-default-browser-check"));
            Map<String, Object> prefs = new HashMap<String, Object>();
            prefs.put("profile.password_manager_enabled", false);
            prefs.put("download.prompt_for_download", false); // Does not work
            prefs.put("chrome.verbose", true);           
            capabilities.setCapability("chrome.prefs", prefs);
            driver =  new EventFiringWebDriver(
                    new ChromeDriver(capabilities)).register(eventListener);
        } else {
                        FirefoxProfile config = new FirefoxProfileConfig(
                    "",false).getConfiguredProfile();
            driver = new EventFiringWebDriver(
                     new FirefoxDriver(config))
                    .register(eventListener);
        }
        
        // Set implicit waitingtime when a field is not available
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        logger.info("Using browser: "+browserType);
        
        // Create Selenium instance (to be able to use selenium 1 api).
        //selenium = new WebDriverBackedSelenium(driver, url);
        driverActions = new Actions(driver);
        navigator = new Navigator(driver);
        bf = new BasicFunctions(driver);
        // Set browserwindow to fullscreen.
        driver.manage().window().maximize();
        
        logger.info("Opening url: "+ url);
        // Open Website
        driver.get(url);
        
        //selenium.open(url);
        // Wait for page to be completely displayed.
        WebElement elem = bf.waitUntilElementIsVisible(
                By.xpath("//img[contains(@src,'lifecycle')]"));
    }
    
    /**
     * Returns an absolute path using this projects root folder
     * as source for the parameters relativePath.
     * 
     * @param relativePath
     *          A relative path starting from root directory of this project.
     * @return 
     *          An absolute path matching the relative path.
     */
    protected String getAbsolutePath(String relativePath)  {
        return System.getProperty("user.dir") + File.separator + relativePath;
    }
    
    /**
     * This method is run before every test method and puts the window focus
     * back to its original position. 
     * This is necessary when switching iframes.
     */
    @BeforeMethod(alwaysRun=true)
    public void prepareTestCase()  {
        driver.switchTo().defaultContent();
        
        logger.info("Switching to default frame.");
                
        WebElement toMove = bf.getVisibleElement(By.xpath(
                "//img[contains(@src,'logo-lod2-small.png')]"));
        //driverActions.moveToElement(toMove).build().perform();
        // Reposition the browser view to be at the top.
        bf.scrollIntoView(toMove);
        //bf.getVisibleElement("Could not find home button. ", 
        //        By.cssSelector("div#LOD2Demo_homeb")).click();
        bf.getVisibleElement("Could not find graph label", 
                By.cssSelector("div.v-label-currentgraphlabel")).click();
        
    }
    
    @BeforeMethod(alwaysRun=true)
    public void xclickAwayFailureOnStartUp()  {
        bf.bePatient(1000);
        By warning = By.xpath("//div[@class='gwt-HTML']");
        if (bf.isElementVisible(bf.getErrorPopupLocator())) {
            WebElement message = bf.getVisibleElement(bf.getErrorPopupLocator());
            logger.fatal("Error message is visible with text: " + message.getText());
            message.click();
            bf.waitUntilElementDisappears(warning);
        } else  {
            logger.info("No error to click away");
        }
    }
    
    /**
     * Closes opened error messages.
     * Error messages from earlier test cases can interfere with current 
     * test case. Therefore if an error message is present it has to be closed.
     */
    @AfterMethod(alwaysRun=true)
    public void afterTestCase()  {
        By warning = By.xpath("//div[@class='gwt-HTML']");
        
        driver.switchTo().defaultContent();
        // Error message is visible.
        if(bf.isElementVisible(bf.getErrorPopupLocator()))  {
            WebElement message =  bf.getVisibleElement(bf.getErrorPopupLocator());
            logger.fatal("Error message is visible with text: " + message.getText());
            message.click();
            bf.waitUntilElementDisappears(warning); 
        }
        
        if(bf.isElementVisible(warning)) {
            logger.info("Warning message was displayed.");
            bf.getVisibleElement(warning).click();
            bf.waitUntilElementDisappears(warning); 
        }

    }
    
    /**
     * Stops browser.
     */
    @AfterSuite(alwaysRun=true)
    public void tearDown()  {
        logger.info("STOPPING");
        driver.quit();
        //selenium.stop();
    }   
}
