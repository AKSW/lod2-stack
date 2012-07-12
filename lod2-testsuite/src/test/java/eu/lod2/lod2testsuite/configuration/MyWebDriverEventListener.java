package eu.lod2.lod2testsuite.configuration;
import java.util.NoSuchElementException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
 
public class MyWebDriverEventListener implements WebDriverEventListener {
    private Log log = LogFactory.getLog(this.getClass());
    private By lastFindBy;
    private String originalValue;
 
    public void beforeNavigateTo(String url, WebDriver selenium){
        log.info("WebDriver navigating to:'"+url+"'");
    }
 
    public void beforeChangeValueOf(WebElement element, WebDriver selenium){
        originalValue = element.getText();
    }
 
    public void afterChangeValueOf(WebElement element, WebDriver selenium){
        log.debug("WebDriver changing value in element found "+lastFindBy+" from '"+originalValue+"' to '"+element.getText()+"'");
    }
 
    public void beforeFindBy(By by, WebElement element, WebDriver selenium){
        lastFindBy = by;
    }
 
    public void onException(Throwable error, WebDriver selenium){
        if (error.getClass().equals(NoSuchElementException.class)){
            log.error("WebDriver error: Element not found "+lastFindBy);
        } else {
            log.error("WebDriver error:", error);
        }
    }
 
    public void beforeNavigateBack(WebDriver selenium){}
    public void beforeNavigateForward(WebDriver selenium){}
    public void beforeClickOn(WebElement element, WebDriver selenium){}
    public void beforeScript(String script, WebDriver selenium){}
    public void afterClickOn(WebElement element, WebDriver selenium){}
    public void afterFindBy(By by, WebElement element, WebDriver selenium){}
    public void afterNavigateBack(WebDriver selenium){}
    public void afterNavigateForward(WebDriver selenium){}
    public void afterNavigateTo(String url, WebDriver selenium){}
    public void afterScript(String script, WebDriver selenium){}
 
}