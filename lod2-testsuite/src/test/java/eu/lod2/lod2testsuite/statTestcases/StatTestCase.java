package eu.lod2.lod2testsuite.statTestcases;

import eu.lod2.lod2testsuite.configuration.*;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Represents a base class for all test cases.
 *
 * @author Stefan Schurischuster
 */
public abstract class StatTestCase extends TestCase {

    public static String testGraph="";
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
        
        logger.info("Projects root directory is "+System.getProperty("user.dir"));
        
        String filesDir = System.getProperty("user.dir") + File.separator + "files";
        String firebugPath = filesDir + File.separator + "firefox"
                + File.separator + "firebug-1.9.2.xpi";
        String firefinderPath = filesDir + File.separator + "firefox"
                + File.separator + "firefinder_for_firebug-1.2.2.xpi";
        // Create new FirefoxProfile:
        FirefoxProfileConfig config = new FirefoxProfileConfig(filesDir);
        try {
            // Add firebug extension
            config.addFireBugExtension(firebugPath);
            // Add firefinder extension
            config.addExtension(firefinderPath);
        } catch (FileNotFoundException ex) {
            Assert.fail("Could not find firefox-plugin: " + ex.getMessage());
        } catch (IOException ex) {
            Assert.fail("Something went wrong trying to register "
                    + "plugins at firefox profile.: " + ex.getMessage());
        }
        // use the custom firefox binary (version 1.8 to be compatible with selenium
        //FirefoxBinary binary=new FirefoxBinary(new File(filesDir+File.separator+"firefox-18"));
        FirefoxBinary binary=new FirefoxBinary(new File(filesDir+File.separator+
                "firefox-18"+File.separator+"firefox-bin"));
        // Create WebDriver instance.
        eventListener = new MyWebDriverEventListener();
        driver = new EventFiringWebDriver(
                new FirefoxDriver(binary,config.getConfiguredProfile()))
                .register(eventListener);
        // Set implicit waitingtime when a field is not available
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        // Create Selenium instance (to be able to use selenium 1 api).
        selenium = new WebDriverBackedSelenium(driver, url);
        driverActions = new Actions(driver);
        navigator = new Navigator(driver);
        bf = new BasicFunctions(driver);
        //TODO hack
        eu.lod2.lod2testsuite.configuration.TestCase.bf=bf;
        // Open Website
        selenium.open(url);
        // Wait for page to be completely displayed.
        WebElement elem = bf.waitUntilElementIsVisible(
                By.xpath("//img[contains(@src,'lifecycle')]"));

        testGraph=context.getCurrentXmlTest().getParameter("graphName");
        clearOntowiki(testGraph);
    }

    //* logs in to ontowiki and clears the testgraph if it exists
    public void clearOntowiki(String testgraph){
        navigator.navigateTo(new String[] {
                "Manage Graph",
                "Create Graph"});
        // Check if Iframe is visible and shows ontowiki
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ontowiki')]"),
                By.id("login"));

        // enter default login details
        bf.setValue(driver.findElement(By.xpath("//input[@id='username']")),"Admin");
        driver.findElement(By.xpath("//a[@id='locallogin']")).click();
        bf.waitUntilElementIsVisible("Could not find the create model button after logging in to ontowiki...",
                By.xpath("//a[@id='createmodel']"));

        // look for the correct graph
        String graphLocator="//a[@about='"+testgraph+"']";
        WebElement graphElement=null;
        try{
            graphElement=driver.findElement(By.xpath(graphLocator));
        }catch (Exception e){
            // no such element (really? why not return null, you guys??)
        }
        if(graphElement != null){
            // click the graphbutton for options

            bf.hoverOverElement(graphElement);
            By buttonLocator=By.xpath(graphLocator+"/span[@class='button']");
            bf.waitUntilElementIsVisible("could not find options button for graph...",buttonLocator);
            driver.findElement(buttonLocator).click();
            // and destroy the element
            By destroyLocator = By.xpath("//a[text()='Delete Knowledge Base']");
            bf.waitUntilElementIsVisible("could not find destroy button for graph...",destroyLocator);
            driver.findElement(destroyLocator).click();
        }else{
            logger.info("testgraph was not present yet. Moving on...");
        }

    }
}
