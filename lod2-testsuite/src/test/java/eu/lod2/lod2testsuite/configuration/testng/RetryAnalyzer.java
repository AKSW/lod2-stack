
package eu.lod2.lod2testsuite.configuration.testng;

import org.apache.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;


public class RetryAnalyzer implements IRetryAnalyzer  { 
    private static final Logger logger = Logger.getLogger(RetryAnalyzer.class);
    
    @Override
    public boolean retry(ITestResult tr) {
        logger.warn("Checking for retrying test case: " + tr.getName());
        // Get Attributes
        if (tr.getAttributeNames().contains("retry")) {
            if ((Boolean) tr.getAttribute("retry")) {
                // Following properties must exist when retry exists.
                int retryCount = (Integer) tr.getAttribute("retry.count");
                int retryMaxCount = (Integer) tr.getAttribute("retry.maxCount");

                logger.warn("Retrying " + tr.getName() + " with status "
                        + tr.getStatus() + " for the " + (retryCount+1) + " of "
                        + retryMaxCount + " times.");
                return true;
            }
        }
        logger.debug("Skipping retry!");
        return false;
    }
}