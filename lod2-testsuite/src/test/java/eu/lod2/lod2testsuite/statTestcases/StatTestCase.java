package eu.lod2.lod2testsuite.statTestcases;

import eu.lod2.lod2testsuite.configuration.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;


/**
 * Represents a base class for all test cases.
 *
 * @author Stefan Schurischuster
 */
public abstract class StatTestCase extends TestCase {

    public static String testGraph="";

    @BeforeSuite(alwaysRun=true)
    //* logs in to ontowiki and clears the testgraph if it exists
    public void clearOntowiki(ITestContext context){
        testGraph = context.getCurrentXmlTest().getParameter("graphName");

        navigator.navigateTo(new String[] {
                "Manage Graph",
                "Create Graph"});
        // Check if Iframe is visible and shows ontowiki
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ontowiki')]"),
                By.id("login"));

        // enter default login details
        bf.setValueViaJavaScript(driver.findElement(By.xpath("//input[@id='username']")),"Admin");
        driver.findElement(By.xpath("//a[@id='locallogin']")).click();
        bf.waitUntilElementIsVisible("Could not find the create model button after logging in to ontowiki...",
                By.xpath("//a[@id='createmodel']"));

        // look for the correct graph
        String graphLocator="//a[@about='"+testGraph+"']";
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
