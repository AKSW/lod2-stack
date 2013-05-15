package eu.lod2.lod2testsuite.configuration;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

/**
 * Listens for test failures and prints a screen shot.
 * 
 * @author Stefan Schurischuster
 */
public class ScreenShotListener extends TestListenerAdapter {
    private static final Logger logger = Logger.getLogger(ScreenShotListener.class); 
    private int skipCount;
    
    public ScreenShotListener()  {
        this.skipCount = 0;
    }
    
    @Override
    public void onStart(ITestContext testContext) {
        this.skipCount = 0;
    }
    
    @Override
    public void onTestFailure(ITestResult tr) {    
        super.onTestFailure(tr);
        Reporter.log(takeScreenShot(tr));
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        if (skipCount == 0) {
            Reporter.log(takeScreenShot(tr));
        }
        skipCount++;
    }
    
    /**
     * Takes a screen shot and puts it into the desired directory.
     * 
     *          An html img tag pointing to the image.
     */
    private String takeScreenShot(ITestResult tr)  {
        String filename = "";
        try  {
            TestCase tc = (TestCase)tr.getInstance();
            WebDriver driver = tc.getDriver();
            if(driver == null)  {
                return "Could not take screenshot, WebDriver was not initiallized "
                        + "because beforeSuite() was not run.\n";
            }
            String sep  = File.separator;
            // Surfire reports directory
            String dir = System.getProperty("user.dir") + sep
                    + "target" + sep + "surefire-reports" + sep;
            File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            // Use testname specific title for screenshot. 
            // Replace special characters
            filename = tr.getName().replace(' ', '_').replace("#", "");
            if(filename.isEmpty()) {
                filename = file.getName();
            }
            try  {
                File screen =  new File(dir+filename);
                //logger.info("Trying to move to: "+screen.getAbsolutePath() + screen.getName());
                FileUtils.moveFile(file,screen);
            } catch(IOException e)  {
                throw new Exception("could not copy screenshot: "+e.getMessage());
            }
        } catch(Exception e)  {
            logger.error("Could not take screenshot: "+e.getMessage());
        }
        return "<a href=\"../"+filename+" \">"
                    + "<img width=50% height=50% src=\"../" +filename+ "\" />"
                    + "</a><br />";
    }
}
