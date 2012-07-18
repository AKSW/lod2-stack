package eu.lod2.lod2testsuite.configuration;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

/**
 *
 * @author Stefan Schurischuster
 */
public class TestNGListener extends TestListenerAdapter {
 
    private static final Logger logger = Logger.getLogger(TestNGListener.class);    
    
    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        logger.info("SUCCEDED TEST: "+ tr.getName());
    }    
    
    @Override
    public void onTestFailure(ITestResult tr) {    
        super.onTestFailure(tr);
        logger.info("FAILED TEST: "+ tr.getName());
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        logger.info("SKIP TEST: "+ tr.getName());
    }

    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);      
        logger.info("BEGIN TEST: "+ tr.getName());
    }
}