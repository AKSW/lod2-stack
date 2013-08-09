package eu.lod2.lod2testsuite.configuration.testng;

import eu.lod2.lod2testsuite.configuration.TestCase;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * Listens for failed tests that need to be rerun.
 */
public class RetryTestListener extends TestListenerAdapter  {
    private static final  Logger logger = Logger.getLogger(RetryTestListener.class);
    private static int count = 1; 
    private static final int maxCount = 2;

    @Override
    public void onTestFailure(ITestResult tr) {   
        // Set attributes:
        tr.setAttribute("retry.count", count);
        tr.setAttribute("retry.maxCount", maxCount);
        //tr.setAttribute("retry", false);
        boolean cond = false;
        if(count < maxCount) {
            count++;
            try  {
                if(TestCase.driver == null)  {
                    logger.error("COULD NOT RETRY TESTCASE: driver is null.");
                    return;
                }
               
                /*
                // Progress bar was visible
                if(tr.getThrowable() instanceof NoSuchElementException)  {
                    logger.warn("Progress-Bar caused a problem. Retrying testcase: "+tr.getName());
                    cond = true;
                }
                
                // StaleElementReferenceException
                if(tr.getThrowable() instanceof StaleElementReferenceException)  {
                    logger.warn("StaleElementReferenceException occured. Retrying testcase: "+tr.getName());
                    cond = true;
                }
                */
                cond = true;
                if(cond)  {
                    tr.setAttribute("retry", true);
                }
            } catch(Exception e)  {
                logger.error("COULD NOT RETRY TESTCASE: "+e.getMessage());
            } 
            //logger.info("Setting result to SKIPPED: "+ITestResult.SKIP);
            //tr.setStatus(ITestResult.SKIP);
        } else  {
            logger.error("Number of retries expired.");
            tr.setStatus(ITestResult.FAILURE);
            // reset count
            count = 1; 
        }
        super.onTestFailure(tr);
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        count = 1; 
    }
    

    @Override
    public void onFinish(ITestContext context) {
        for (int i = 0; i < context.getAllTestMethods().length; i++) {
            if (context.getAllTestMethods()[i].getCurrentInvocationCount() == 2) {
                if (context.getFailedTests().getResults(context.getAllTestMethods()[i]).size() == 2 
                        || context.getPassedTests().getResults(context.getAllTestMethods()[i]).size() == 1) {
                    context.getFailedTests().removeResult(context.getAllTestMethods()[i]);
                }
            }
        }
        
    }
    
    private Set<ITestNGMethod> findDuplicates(Set<ITestResult> listContainingDuplicates) {
        Set<ITestNGMethod> toRemove = new HashSet<ITestNGMethod>();
        Set<ITestNGMethod> testSet = new HashSet<ITestNGMethod>();
        
        for(ITestResult test : listContainingDuplicates)  {
            if (!testSet.add(test.getMethod())) {
                toRemove.add(test.getMethod());
            }    
        }
        return toRemove;
        
    }
}