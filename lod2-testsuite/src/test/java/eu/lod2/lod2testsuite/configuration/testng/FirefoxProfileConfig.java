package eu.lod2.lod2testsuite.configuration.testng;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.Assert;

/**
 *
 * @author Stefan Schurischuster
 */
public class FirefoxProfileConfig {
    private FirefoxProfile profile;
    private String downloadDir;
    private static final Logger logger = Logger.getLogger(FirefoxProfileConfig.class);    
    private boolean socksProxy;
    
    public FirefoxProfileConfig(String downloadDir, boolean socksProxy) {
        this.profile = new FirefoxProfile();
        this.downloadDir = downloadDir;
        this.socksProxy = socksProxy;
        setPreferences();
        addExtensions();
        // Sets native events to true: Can cause problems under Linux
        // @TODO: Monitor and keep in mind.
        boolean nativeEvents = true;
        profile.setEnableNativeEvents(nativeEvents);
        logger.info("Using native events: " +nativeEvents);
    }
    
    private void setPreferences()  {
        if(socksProxy)  {
            profile.setPreference("network.proxy.socks", "localhost");
            profile.setPreference("network.proxy.socks_port", 4567);
            profile.setPreference("network.proxy.type", 1); 
        }
        // Set download folder.
        logger.info("Download directory is: " + downloadDir);
        // Use the download folder to dowload files.
        profile.setPreference("browser.download.folderList", 2);
        // Set download folder
        profile.setPreference("browser.download.dir", downloadDir);
        profile.setPreference("browser.download.lastDir", downloadDir);
        // Set open new window preferences to allways open tabs when
        // triggered by javascript.
        profile.setPreference("browser.link.open_newwindow.restriction", 0);
        // Not allowed to override
        //profile.setPreference("browser.link.open_newwindow", 3);
        //profile.setPreference("signon.autologin.proxy", true);
        
        // Dont ask for the following mime types.
        String mimeTypes = "application/rdf+xml,"
                        + "text/rdf+n3,"
                        + "text/turtle,"        
                        + "text/plain,"
                        + "application/zip,"
                        + "application/x-trig,"
                        + "application/trix,"
                        + "application/x-binary-rdf,"
                        + "application/xml";
       
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",mimeTypes);
        // Do not ask where to save.
        profile.setPreference("browser.download.useDownloadDir", true);
        // Don't show download window.
        profile.setPreference("browser.download.manager.showWhenStarting", false);
    }
    
    private void addExtensions()  {
        // Add firebug to config
        String sep = File.separator;
        
        String firebugPath = System.getProperty("user.dir") + sep + "files" 
               + sep + "firefox" + sep + "firebug-1.12.7.xpi";
        
        String firefinderPath = System.getProperty("user.dir") + sep + "files" 
               + sep + "firefox" + sep + "firefinder_for_firebug-1.2.2.xpi";
        
        String firepathPath = System.getProperty("user.dir") + sep + "files" 
                + sep + "firefox" + sep + "firepath-0.9.7-fx.xpi";
        
        File firebug = new File(firebugPath);
        File firefinder = new File(firefinderPath);
        File firePath = new File(firepathPath);
        
        String firebugName = firebug.getName();
        String firefinderName = firefinder.getName();
        
        String firebugVersion = firebugName.substring(firebugName.lastIndexOf("-")+1, 
                firebugName.lastIndexOf("."));
        String firefinderVersion = firefinderName.substring(firefinderName.lastIndexOf("-")+1, 
                firefinderName.lastIndexOf("."));
        try {
            profile.addExtension(firebug);
            profile.addExtension(firefinder);
            profile.addExtension(firePath);
        } catch (IOException ex) {
            Assert.fail("Could not find Extension: " +ex.getMessage());
        }
        // Set current version to avoid poping the startup screen.
        profile.setPreference("extensions.firebug.currentVersion", firebugVersion);
        
        logger.info("Using firebug-plugin with version: " +firebugVersion);
        logger.info("Using firefinder-plugin with version: " +firefinderVersion);
    }
    
    /**
     * @return 
     *      Returns a pre configured Firefox profile to fit the needs of 
     *      PPT testing.
     */
    public FirefoxProfile getConfiguredProfile()  {
        return this.profile;
    }
}
