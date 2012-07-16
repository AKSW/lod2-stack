package eu.lod2.lod2testsuite.configuration;
import java.util.NoSuchElementException;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
 
public class MyWebDriverEventListener implements WebDriverEventListener {
    private static final Logger logger = Logger.getLogger(MyWebDriverEventListener.class);   
    private By lastFindBy;
    private String originalValue, elementAsString;
 
    public void beforeNavigateTo(String url, WebDriver selenium){
        logger.debug("Navigating to:'"+url+"'");
    }
 
    public void beforeChangeValueOf(WebElement element, WebDriver selenium){
        originalValue = element.getText();
        elementAsString = element.toString();
    }
 
    public void afterChangeValueOf(WebElement element, WebDriver selenium){
        logger.debug("WebDriver changing value in element found " +lastFindBy
                +" from '"+originalValue+"' to '"+element.getText()+"'");
    }
 
    public void beforeFindBy(By by, WebElement element, WebDriver selenium){
        lastFindBy = by;
        logger.debug("Trying to find: '" +lastFindBy+ "'.");
    }
    
    public void afterFindBy(By by, WebElement element, WebDriver selenium){
        logger.debug("Found: '" +lastFindBy+ "'.");
    }
 
    public void onException(Throwable error, WebDriver selenium){
        if (error.getClass().equals(NoSuchElementException.class)){
            logger.error("WebDriver error: Element not found " +lastFindBy);
        } else {
            logger.error("WebDriver error:", error);
        }
    }
    
    public void beforeClickOn(WebElement element, WebDriver selenium){
        logger.debug("Trying to click: '" +element+ "'");
    }
    
    public void afterClickOn(WebElement element, WebDriver selenium){
        logger.debug("Clicked: '" +element+ "'");
    }
 
    public void beforeNavigateBack(WebDriver selenium){}
    public void beforeNavigateForward(WebDriver selenium){}
    public void beforeScript(String script, WebDriver selenium){}
    public void afterNavigateBack(WebDriver selenium){}
    public void afterNavigateForward(WebDriver selenium){}
    public void afterNavigateTo(String url, WebDriver selenium){}
    public void afterScript(String script, WebDriver selenium){}
}