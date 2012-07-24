package eu.lod2.lod2testsuite.configuration;

import eu.lod2.lod2testsuite.testcases.TestCase;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

/**
 *
 * @author Stefan Schurischuster
 */
public class ScreenShotListener extends TestListenerAdapter {
    private static final Logger logger = Logger.getLogger(ScreenShotListener.class); 
    
    @Override
    public void onTestFailure(ITestResult tr) {    
        super.onTestFailure(tr);
        //logger.error(takeScreenshot());
        Reporter.log(takeScreenshot());
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        Reporter.log(takeScreenshot());
        //logger.error(takeScreenshot());
    }
    
    /**
     * Takes a screenshot and puts it into the desired directory.
     * 
     *          An html img tag pointing to the image.
     */
    private String takeScreenshot()  {
        String sep = System.getProperty("file.seperator");
        sep  = File.separator;
        // Surfire reports directory
        String dir = System.getProperty("user.dir") + sep
                + "target" + sep + "surefire-reports" + sep;

        File file = ((TakesScreenshot) TestCase.driver).getScreenshotAs(OutputType.FILE);
        String filename = "";
        if(filename.isEmpty()) {
            filename = file.getName();
        }
        try  {
            File screen =  new File(dir+filename);
            logger.debug("Trying to move to: "+screen.getAbsolutePath() + screen.getName());
            FileUtils.moveFile(file,screen);
        }catch(IOException e)  {
            Assert.fail("could not copy screenshot");
        }
        
        String full = dir + filename;
        
        return "<a href=\" "+full+" \">"
                + "<img width=50% height=50% src=\"file:///" + full + "\" alt=\"\"/></a><br />";
    }
    
}
