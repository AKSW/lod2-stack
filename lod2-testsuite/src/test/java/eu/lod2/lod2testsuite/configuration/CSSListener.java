package eu.lod2.lod2testsuite.configuration;

import org.testng.ITestContext;
import org.testng.TestListenerAdapter;

/**
 *
 * @author Stefan Schurischuster
 */
public class CSSListener extends TestListenerAdapter {
    @Override
     public void onStart(ITestContext testContext) {
        String script = " <script type=\"text/javascript\"> "
                + "function unhide(divID){var item = document.getElementById(divID);"
                + "if (item) {item.className=(item.className=='hidden')"
                + "?'unhidden':'hidden';}}</script>";
     }
}
