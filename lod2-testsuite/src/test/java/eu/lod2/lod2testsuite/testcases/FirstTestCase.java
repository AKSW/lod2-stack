package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.Convert;
import eu.lod2.lod2testsuite.configuration.MetaParams;
import eu.lod2.lod2testsuite.configuration.Navigator;
import eu.lod2.lod2testsuite.dataclasses.FirstTestData;
import org.testng.annotations.Test;

/**
 *
 * @author Stefan Schurischuster
 */
public class FirstTestCase extends ParameterizedTestCase {
    
    private FirstTestData prop;
    /*
    public FirstTestCase(FirstTestData prop, MetaParams metaParams)  {
        this.prop = prop;
        this.csvMetaParams = metaParams;
    }
     * 
     */
    public FirstTestCase(FirstTestData prop)  {
        this.prop = prop;
    }
    
    @Test
    public void beforeTest() {
       System.out.println(prop.getAction() + " TEST PREPARED");
    }
    
    
    @Test(dependsOnMethods={"beforeTest"})
    public void runTest() {
        //Navigator nav = new Navigator();
        //nav.navigateTo(Convert.getStringArrayFromString(prop.getPath(), ";"));
        
        System.out.println(prop.getAction() + " TEST RUN");
    }   
}
