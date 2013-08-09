
package eu.lod2.lod2testsuite.configuration.testng;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;


/**
 * Adds a retry analyser to all @Test annotated Methods.
 * 
 * @author Stefan Schurischuster
 */
public class RetryAnalyzerAppender implements IAnnotationTransformer {
    private static final Logger logger = Logger.getLogger(RetryAnalyzerAppender.class);
    
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        IRetryAnalyzer retry = annotation.getRetryAnalyzer();
        if (retry==null){
            logger.debug("Transforming annotation! -> Adding RetryAnalyzer.class");            
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
