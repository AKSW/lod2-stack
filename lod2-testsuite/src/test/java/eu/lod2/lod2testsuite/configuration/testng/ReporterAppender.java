package eu.lod2.lod2testsuite.configuration.testng;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.testng.Reporter;

/**
 * A log4j appender to log selenium commands into testng reports.
 * 
 * @author Stefan Schurischuster
 */
public class ReporterAppender extends AppenderSkeleton {
    private String logging = "";
    
    @Override
    protected void append(LoggingEvent event) {
        String m = event.getRenderedMessage();
        String currName = "";
         
        if(m.startsWith("BEGIN TEST"))  {
            logging = beginMarginDiv();
            //logging += "<br><b>" + m + "</b><br>";
            // Get name of test by parsing logging message. 
            currName = event.getRenderedMessage().split(":")[1].trim();
        } else if(m.contains("TEST: "+currName))  {
            Reporter.log("<b>"+m+"</b>", false);
            Reporter.log("<br>");
        } else if(m.contains("@ Throwable:"))  {
            logging += this.getLayout().format(event) + "<br>";
            logging += endDiv();
            Reporter.log(logging);           
        } else  {
            logging += this.getLayout().format(event) + "<br>";
            //Reporter.log(this.getLayout().format(event), false);
            //Reporter.log("<br>");
        }
        
        //Reporter.log( this.getLayout().format(event), false );
    }

    @Override
    public void close() {
        //There are no resources to release.
    }

    @Override
    public boolean requiresLayout() {
        return true; // Use layout information from properties file.
    }
    
     private String beginMarginDiv() {
        return "<div style = \"overflow:scroll;\">"
                + "<div style= \"float:left; margin-right:-9999px; position:relative;\">";
    }
     
    private String beginDiv(int width, int height) {
        
        return "<div style = \"width:100%; height:" + height + "px; "
                + "overflow:scroll; font-family: courier_new; font-size:small\">"
                + "<div style= \"float:left; margin-right:-9999px; position:relative;\">";
    }

    private String endDiv() {
        return "</div></div>";
    }
}
