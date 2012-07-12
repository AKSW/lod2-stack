package eu.lod2.lod2testsuite.configuration;

import eu.lod2.lod2testsuite.testcases.ParameterizedTestCase;
import eu.lod2.lod2testsuite.testcases.TestCase;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

/**
 *
 * @author Stefan Schurischuster
 */
public class TestNGListener extends TestListenerAdapter {
    
    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        Reporter.log("STARTING TESTSESSION: " +testContext.getName(), true);
    }
    
    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        Reporter.log("SUCCEDED TEST: "+ tr.getName() ,true);
    }    
    
    @Override
    public void onTestFailure(ITestResult tr) {    
        super.onTestFailure(tr);
        Reporter.log("FAILED TEST: "+ tr.getName(), true);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        Reporter.log("SKIP TEST: "+ tr.getTestName(), true);
    }

    @Override
    public void onTestStart(ITestResult result) {
        super.onTestStart(result);      
        Reporter.log("BEGIN TEST: "+ result.getTestName(), true);
    }
}