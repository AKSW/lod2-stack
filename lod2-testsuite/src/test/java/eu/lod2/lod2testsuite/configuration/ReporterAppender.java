package eu.lod2.lod2testsuite.configuration;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.testng.Reporter;

/**
 * A log4j appender to log selenium commands into testng reports.
 * 
 * @author Stefan Schurischuster
 */
public class ReporterAppender extends AppenderSkeleton {

    @Override
    protected void append(LoggingEvent event) {
        String m = event.getRenderedMessage();
        String currName = "";
        
        if(m.startsWith("BEGIN TEST"))  {
            Reporter.log("--------------------------------------------", false);
            Reporter.log("<b>" + m + "</b>", false);
            // Get name of test by parsing logging message. 
            currName = event.getRenderedMessage().split(":")[1].trim();
        } else if(m.contains("TEST: "+currName))  {
            Reporter.log("<b>"+m+"</b>", false);
        } else  {
            Reporter.log(  this.getLayout().format(event), false );
        }
        
        //Reporter.log( this.getLayout().format(event), false );
    }

    public void close() {
        //There are no resources to release.
    }

    public boolean requiresLayout() {
        return true; // Use layout information from properties file.
    }
}
