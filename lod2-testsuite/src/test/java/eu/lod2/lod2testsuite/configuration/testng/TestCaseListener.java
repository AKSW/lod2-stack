package eu.lod2.lod2testsuite.configuration.testng;

import org.apache.log4j.Logger;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * A Listener that listens to test success and prints 
 * log information.
 * 
 * @author Stefan Schurischuster
 */
public class TestCaseListener extends TestListenerAdapter {
    
    private int skipCount;
    private static final Logger logger = Logger.getLogger(TestCaseListener.class);    
    
    public TestCaseListener()  {
        this.skipCount = 0;
    }
    
    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        logger.info("SUCCEDED TEST: "+ tr.getName());
    }    
    
    @Override
    public void onTestFailure(ITestResult tr) {    
        super.onTestFailure(tr);
        String str = "";
        
        if (tr.getAttributeNames().contains("retry")) {
            if ((Boolean) tr.getAttribute("retry")) {
                logger.info("Different onTestFailure in " + this.getClass().getSimpleName()
                        + " because of retry.");
                // Following properties must exist when retry exists.
                int retryCount = (Integer) tr.getAttribute("retry.count");
                int retryMaxCount = (Integer) tr.getAttribute("retry.maxCount");
                
                str = "RETRY " + retryCount + " OF " + retryMaxCount + " TIMES for TEST: " 
                        + tr.getName();
            } else  {
                str = "FAILED RETRIED TEST: "+ tr.getName(); 
            }
        } else  {
            str = "FAILED TEST: "+ tr.getName(); 
        }
        
        logger.error("@ Throwable: "+ tr.getThrowable());
        logger.error(str);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        String str = "";
        super.onTestSkipped(tr);
        if (tr.getAttributeNames().contains("retry")) {
            if ((Boolean) tr.getAttribute("retry")) {
                logger.info("Different onTestSkipped in " + this.getClass().getSimpleName()
                        + " because of retry.");
                // Following properties must exist when retry exists.
                int retryCount = (Integer) tr.getAttribute("retry.count");
                int retryMaxCount = (Integer) tr.getAttribute("retry.maxCount");
                
                str = "RETRY " + retryCount + " OF " + retryMaxCount + " TIMES for TEST: " 
                        + tr.getName();
            } else  {
                 str = "SKIP RETRIED TEST: "+ tr.getName(); 
            }
        } else  {
            str = "SKIP TEST: "+ tr.getName();   
        }
        if (skipCount == 0) {
            logger.error("@ Throwable: "+ tr.getThrowable());
            logger.error(str);
        }
        skipCount++;
    }

    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);      
        logger.info("BEGIN TEST: "+ tr.getName());
    }
}