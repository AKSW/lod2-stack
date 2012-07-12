package eu.lod2.lod2testsuite.factories;

import eu.lod2.lod2testsuite.testcases.FirstTestCase;
import java.util.ArrayList;
import java.util.List;
import eu.lod2.lod2testsuite.configuration.BasicFunctions;
import eu.lod2.lod2testsuite.dataclasses.FirstTestData;
import eu.lod2.lod2testsuite.configuration.MetaParams;
import org.testng.annotations.Factory;

/**
 *
 * @author Stefan Schurischuster
 */
public class FirstTestFactory extends TestFactory {
    private List<FirstTestData> testData;
    private MetaParams mp;
    private ArrayList<String> fileLines;
    private BasicFunctions bf;
    
    public FirstTestFactory()  {
       csvFile = "FirstTest.csv";
       //Retrieve information
       testData = loadConfig(csvFile, FirstTestData.class);
       fileLines = bf.readFile(csvFile,true);
       //System.out.println(csvFile + "   " + testData.toString() + fileLines.toString());
    }
        
    @Factory
    public Object[] produce() {
       List<FirstTestData> testData = new ArrayList<FirstTestData>();
        
        FirstTestData td1 = new FirstTestData();
        td1.setAction("1");
        FirstTestData td2 = new FirstTestData();
        td2.setAction("2");
        FirstTestData td3 = new FirstTestData();
        td3.setAction("3");
        FirstTestData td4 = new FirstTestData();
        td4.setAction("4");
        FirstTestData td5 = new FirstTestData();
        td5.setAction("5");
        FirstTestData td6 = new FirstTestData();
        td6.setAction("6");
        
        testData.add(td1);
        testData.add(td2);
        testData.add(td3);
        testData.add(td4);
        testData.add(td5);
        testData.add(td6);
    
        
        //Create testcases by creating an object array that holds the 
        //instances of the specific test cases.
        Object[] testCases = new Object[testData.size()];
        int index = 0;
        
        for(FirstTestData pd : testData)  {
            //mp = new MetaParams(fileLines.get(index+1), index+2, csvFile);        
            //testCases[index] = new FirstTestCase(pd, mp);
            testCases[index] = new FirstTestCase(pd);
            index++;
        }
        return testCases;
    }
    

}
