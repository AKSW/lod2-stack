package eu.lod2.lod2testsuite.factories;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Factory;
/**
 *
 * @author Stefan Schurischuster
 */
public abstract class TestFactory {
    protected String csvFile;
    
    /**
     * Prepares an Object array holding beans of TestData objects that represent
     * test cases with different parameters.
     */
    @Factory
    public abstract Object[] produce() ;
    
   /**
     * Retrieve information from a csv-file and parse it into a java bean.
     * 
     * @param <A>
     *          Represents the datatype of the TestData that will be parsed.
     * @param configCsvFile
     *          The csv-file.
     * @return
     *          A list of TestCase instances that are used as input for 
     *          a factory.
     */
    protected <A> List<A> loadConfig(String configCsvFile, Class<A> type) {
        CSVReader reader = null;
        try  {
            //create CSVReader with ";" as seperating character.
            reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream(configCsvFile)),';','"','^');
        } catch(NullPointerException e)  {
            Assert.fail("Could not load configuration file: File not found" + e.getMessage());
        }
                
        //Create mapping strategy
        ColumnPositionMappingStrategy<A> strat = new ColumnPositionMappingStrategy<A>();
        try  {
            String[] columns = reader.readNext();
            strat.setColumnMapping(columns);
            strat.setType(type);
            
        } catch(Exception e)  {
            Assert.fail("Could not read first line of csv file. " + e.getMessage());
        }
        
        //Create beans
        CsvToBean toBean = new CsvToBean();
        return toBean.parse(strat, reader);
    }
}
