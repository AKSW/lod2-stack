package eu.lod2.lod2testsuite.statTestcases;

import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;

/**
 * This class contains functional tests concerning extraction and loading of
 * the lod2 - stack.
 * All TestCases are documented here:
 * https://grips.semantic-web.at/pages/viewpage.action?pageId=36409526
 * 
 * @author karel.kremer@tenforce.com
 */
public class StatisticalDemoScenario extends StatTestCase {

    private String csvUriBase =null;

    /**
     * TC 001.
     * post: fail or ontowiki is open on the create graph panel and a user with admin rights is logged in
     */
    @Test
    public void openOntowiki()  {
        navigator.navigateTo(new String[] {
                "Manage Graph",
                "Create Graph"});
        // Check if Iframe is visible and shows ontowiki
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ontowiki')]"),
                By.id("createmodel"));
    }
    
    /**
     * Creates a new knowledgebase
     * pre: the openOntowiki testcase has run immediately prior to this testcase
     * post: the test has failed or the graph has been created
     */
    @Test
    public void newKnowledgeBase()  {
        // Ontowiki
        // But frames have been switched. so check and by implication, switch
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ontowiki')]"),
                By.id("createmodel"));


        // enter upload information
        bf.getVisibleElement(By.xpath("//input[@value='empty']")).click();

        bf.getVisibleElement(By.xpath("//input[@id='model-input']")).sendKeys(testGraph);

        // Click create
        bf.getVisibleElement("Could not find submit button.", By.xpath("//a[@id='createmodel']")).click();
        // Wait for upload to finish
        bf.waitUntilElementIsVisible("Creation did not finish correctly",
                By.xpath("//div[@id='modellist']//a[@about='"+testGraph+"']"));
    }

    /**
     * Sets the current graph to the test graph
     * pre: the test graph has been freshly created
     * post: the current graph is set to the test graph
     */
    @Test
    public void setCurrentGraph(){
            navigator.navigateTo(new String[]{"Manage Graph", "Select Default Graph"});
        driver.findElement(By.xpath("//div[@class='v-filterselect-button']")).click();
        WebElement graph=
                bf.waitUntilElementIsVisible("Could not find the test graph in the list of possible current graphs",
                        By.xpath("//div[@class='popupContent']//span[text()='"+testGraph+"']"));
        graph.click();
        driver.findElement(By.xpath("//div[@class='v-button']//span[text()='Set configuration']")).click();
        driver.findElement(By.xpath("//div[contains(@class,currentgraphlabel)][text()='"+testGraph+"']"));
    }

    /**
     * Imports a csv file using the csv importer application
     * pre: a graph with the test graph uri exists, the csv file is in the files directory. This graph has been
     * selected as the current graph
     * post: the graph should be imported using the csv importer
     */
    @Test
    @Parameters({"csvFilePath"})
    public void doCsvImport(String csvFilePath){
        // Get absolute paths.
        csvFilePath = getAbsolutePath(csvFilePath);
        // Check if required files exist
        assertTrue("Could not find graph file on local drive: " + csvFilePath,
                bf.isLocalFileAvailable(csvFilePath));

        navigator.navigateTo(new String[]{"Manage Graph", "Import", "Import from CSV"});
        // are we in ontowiki?
        By importButtonLocator=By.xpath("//a[@id='import']");
        bf.checkIFrame(By.xpath("//iframe[contains(@src,'ontowiki')]"), importButtonLocator);
        // do we have the csv importer?
        bf.waitUntilElementIsVisible("Could not find the import button for the csv importer",
                By.xpath("//legend[contains(text(),'Import CSV Data')]"));
        driver.findElement(By.xpath("//input[@value='scovo']")).click();
        driver.findElement(By.xpath("//input[@id='file-input']")).sendKeys(csvFilePath);
        driver.findElement(importButtonLocator).click();

        // wait until the extraction interface shows up
        bf.waitUntilElementIsVisible("Could not find the extraction button for the csv import application...",
                By.xpath("//a[@id='extract']"));

        //remember the base uri for the csv data
        this.csvUriBase = driver.findElement(By.xpath("//input[@id='uribase']")).getAttribute("value");

        //enter some values for the datacube dimensions
        driver.findElement(By.xpath("//input[@id='datastructure']")).sendKeys("dsd");
        driver.findElement(By.xpath("//input[@id='dataset']")).sendKeys("ds");
        driver.findElement(By.xpath("//input[@id='measure']")).sendKeys("measure");


        // add some dimensions
        int contentStartLeft=1; int contentEndLeft=4;
        int contentStartTop=6; int contentEndTop=10;
        addCSVDimension("location",contentStartLeft,1,contentEndLeft,1);
        addCSVDimension("disease",0,contentStartTop,0,contentEndTop);

        // add the content for these dimensions
        driver.findElement(By.xpath("//a[@id='btn-datarange']")).click();
        try{
            driver.switchTo().alert().accept();
        }catch (Exception e){
            // void, in case they remove the alert
        }
        clickCSVCells(contentStartLeft,contentStartTop,contentStartLeft,contentStartTop);
        clickCSVCells(contentEndLeft,contentEndTop,contentEndLeft,contentEndTop);
        // ... and submit!
        driver.findElement(By.xpath("//a[@id='extract']")).click();

        bf.waitUntilElementIsVisible("Could not find extraction confirm button",
                By.xpath("//input[@id='extract_triples_btn']")).click();

        bf.waitUntilElementIsVisible("Extraction did not complete as expected",
                By.xpath("//div[@id='import-options'][text()[contains(.,'Done saving data!')]]"));

        // now check if the dimensions are added to the dataset
        driver.findElement(By.xpath("//div[@id='modellist']//a[@about='"+testGraph+"']")).click();
        bf.waitUntilElementIsVisible("Could not find the inserted dimensions",
                By.xpath("//div[@id='navigation']//a[text()[contains(.,'location')]]"));
    }
    //* helper function for csvimport testcase that creates csvimport dimensions
    private void addCSVDimension(String name, int startLeft, int startTop, int endLeft, int endTop) {
        // horrible, horrible hack to avoid the prompt issue which selenium can't handle
        ((JavascriptExecutor)driver).executeScript("window.prompt=function(message,defaultvalue){ " +
                "return '"+name+"'; }");
        // argh! the HORROR! glad it's over now...
        driver.findElement(By.xpath("//a[@id='btn-add-dimension']")).click();
        bf.bePatient(100);

        clickCSVCells(startLeft,startTop,endLeft,endTop);
    }

    //* helper function for csvimport testcase that selects the given range of cells
    private void clickCSVCells(int startLeft, int startTop, int endLeft, int endTop){
        for(int left=startLeft; left<=endLeft; left++){
            for(int top=startTop; top<=endTop; top++){
                driver.findElement(By.xpath("//td[@id='r"+top+"-c"+left+"']")).click();
            }
        }
    }

    @Test
    @Parameters({"pptFilePath"})
    /**
     * Uploads the locations that were generated from poolparty so they can be linked later on
     * @pre the current graph has been selected to the testGraph and that graph has been selected in ontowiki
     * @post the poolparty locations are added to the testGraph
     */
    public void uploadPoolpartyLocations(String pptFilePath){
        // Get absolute paths.
        pptFilePath = getAbsolutePath(pptFilePath);
        // Check if required files exist
        assertTrue("Could not find graph file on local drive: " + pptFilePath,
                bf.isLocalFileAvailable(pptFilePath));

        navigator.navigateTo(new String[]{"Manage Graph", "Import", "Import triples from file"});

        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ontowiki')]"),
                By.id("modellist"));


        // do the upload
        driver.findElement(By.xpath("//input[@value='upload']")).click();
        bf.waitUntilElementIsVisible("Could not find the file upload button",
                By.xpath("//input[@id='file-input']")).sendKeys(pptFilePath);

        driver.findElement(By.xpath("//a[@id='addmodel']")).click();

        // check if the code list has been added
        bf.waitUntilElementIsVisible("Could not find the inserted dimensions",
                By.xpath("//div[@id='navigation']//a[text()[contains(.,'skos:Concept')]]"));
    }

    @Test
    /**
     * validates the datacube
     * pre: the datacube has been inserted and the current graph has been set correctly
     */
    public void validateCube(){
        navigator.navigateTo(new String[]{"Manage Graph", "Validate"});

        driver.findElement(By.xpath("//option[text()[contains(.,'Summary')]]")).click();
        bf.waitUntilElementIsVisible("Could not get summary information",
                By.xpath("//h2[text()[contains(.,'Summary')]]"));

        driver.findElement(By.xpath("//li[text()[contains(.,'There are 20 observations')]]"));
        driver.findElement(By.xpath("//li[text()[contains(.,'There are 1 data sets')]]"));
        driver.findElement(By.xpath("//li[text()[contains(.,'There are 1 data structure definitions')]]"));
    }

    @Test
    @Parameters({"silkProjectName", "virtUser", "virtPwd", "silkLinkSpec"})
    /**
     * Links the locations from poolparty to the locations from the csv
     * pre: both the poolparty and the csv file have been imported
     * post: there is a same as predicate between the csv locations and the poolparty locations
     *
     * NOTE: silk broke their namespaces. have to do that manually for xpath
     */
    public void linkPoolPartyLocations(String silkProjectName, String virtUser, String virtPwd, String silkLinkSpec){
        // Get absolute paths.
        silkLinkSpec= getAbsolutePath(silkLinkSpec);
        // Check if required files exist
        assertTrue("Could not find graph file on local drive: " + silkLinkSpec,
                bf.isLocalFileAvailable(silkLinkSpec));

        navigator.navigateTo(new String[]{"Enrich Data", "Interlinking dimensions (Silk)"});

        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'silk')]"),
                By.id("title"));

        bf.waitUntilElementIsVisible("Could not find the silk title banner",
                By.xpath("//*[local-name() = 'div'][contains(text(),'Silk Workbench')]"));
        // check if a previous project is available and remove if so
        try{
            getSilkActionButton("Remove",silkProjectName).click();
            driver.findElement(By.xpath("//*[local-name() = 'span'][text()[contains(.,'Yes, delete it')]]")).click();
        }catch (Exception e){
            // ok no previous project
        }

        driver.findElement(By.xpath("//*[local-name() = 'div']" +
                "[@id='newproject']//*[local-name() = 'span'][text()='Project']")).click();
        driver.findElement(By.xpath("//*[local-name() = 'input']" +
                "[@id='projectName']")).sendKeys(silkProjectName);
        driver.findElement(By.xpath("//*[local-name() = 'div']" +
                "[@id='createProjectDialog']//*[local-name() = 'input'][@type='submit']")).click();


        // add required prefixes
        getSilkActionButton("Prefixes", silkProjectName).click();

        addSilkPrefix("skos: http://www.w3.org/2004/02/skos/core#");
        addSilkPrefix("csv: "+csvUriBase);

        driver.findElement(By.xpath("//*[local-name() = 'div'][@id='editPrefixesDialog']//" +
                "*[local-name() = 'span'][@class='ui-button-text'][text()='Save']")).click();

        bf.bePatient(100); // dom change

        //add sources
        addSilkSPARQLSource("codelists",silkProjectName);
        addSilkSPARQLSource("csvfile",silkProjectName);

        addSilkSparqlOutput("output",silkProjectName, virtUser, virtPwd);

        // add link spec
        getSilkActionButton("Link Spec", silkProjectName).click();
        String sourceDivId=driver.findElement(By.xpath("//*[local-name() ='div']" +
                "[.//*[local-name()='span'][@class = 'ui-dialog-title'][text()='Add Link Specification']]/" +
                "*[local-name()='div'][@id]")).getAttribute("id");
        driver.findElement(By.xpath("//*[@id='"+sourceDivId+"']//" +
                "*[@type='file'][local-name()='input']")).sendKeys(silkLinkSpec);
        driver.findElement(By.xpath("//*[local-name()='input'][@value='Add']")).click();

        // open the link spec
        bf.waitUntilElementIsVisible("Could not find the link that should have been created",
                By.xpath("//*[local-name()='span'][@class='link']//*" +
                        "[local-name()='button']//*[local-name()='span'][text()='Open']")).click();
        // got to generate links tab
        bf.waitUntilElementIsVisible(By.xpath("//*[local-name()='div'][@class='tab']/*" +
                "[local-name()='a'][text()='Generate Links']")).click();
        // start generation
        bf.waitUntilElementIsVisible(By.xpath("//*[local-name()='button']/*" +
                "[local-name()='span'][text()='Start']")).click();
        // select the output
        driver.findElement(By.xpath("//*[local-name()='select'][./*" +
                "[local-name()='option'][text()='Display only']]")).sendKeys("output");
        driver.findElement(By.xpath("//*[local-name()='input'][@value='OK'][@type='submit']")).click();

        //* wait for output to finish
        try{
            bf.waitUntilElementIsVisible("Could not get feedback on task start",
                    By.xpath("//*[local-name()='button']/*" +
                            "[local-name()='span'][text()='Stop']"));
        }catch (Exception e){
            // we were just too late. assuming the data is ready
        }
        bf.waitUntilElementIsVisible("Could not get feedback on task start",
                By.xpath("//*[local-name()='button']/*" +
                        "[local-name()='span'][text()='Start']"));

        // check if the link was made in ontowiki
        driver.switchTo().defaultContent();
        navigator.navigateTo(new String[]{"Edit & Transform", "Edit Graph (OntoWiki)"});
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ontowiki')]"),
                By.id("navigation"));
        driver.findElement(By.xpath("//a[text()[normalize-space(.)='skos:Concept']]")).click();
        bf.waitUntilElementIsVisible("Silk did not make the correct sameAs links",
                By.xpath("//div[@id='showproperties']//a[text()[contains(.,'owl:sameAs')]]"));
    }

    //* helper function that adds a prefix in the silk test case
    private void addSilkPrefix(String prefix){
        bf.waitUntilElementIsVisible("Could not find prefix dialog",
                By.xpath("//*[local-name() = 'div'][@id='editPrefixesDialog']//*[local-name() = 'span'][@class='ui-button-text'][text()='add']")).click();
        driver.findElement(
                By.xpath("//*[local-name() = 'div'][@id='editPrefixesDialog']//*[local-name() = 'tr'][last()-1]//*[local-name() = 'input'][@type='text'][@value='']")).sendKeys(prefix);
    }

    //* helper function that adds a silk sparql source to the given project
    private void addSilkSPARQLSource(String name, String project){
        getSilkActionButton("Source",project).click();

        getSilkDialogInput("Source","name").sendKeys(name);
        getSilkDialogInput("Source","endpointURI").sendKeys("http://localhost:8890/sparql");
        getSilkDialogInput("Source","graph").sendKeys(testGraph);

        driver.findElement(By.xpath("//*[local-name() = 'input'][@value='Save']")).click();

        bf.waitUntilElementIsVisible("Could not find the source "+name+ " which should have been created",
                By.xpath("//*[local-name()='span'][@class='source']/*[local-name()='span'][text()='"+name+"']"));
    }

    //* helper function for the silk testcase that returns the action button with the given name for the given project
    private WebElement getSilkActionButton(String name, String project){
        return driver.findElement(By.xpath("//*[local-name() = 'span'][contains(@class, 'folder')]/" +
                "*[local-name() = 'div'][@class='actions'][preceding-sibling::" +
                "*[local-name() = 'span'][contains(@class,'label')][text()[contains(.,'"+project+"')]]]//" +
                "*[local-name() = 'span'][text()='"+name+"']"));
    }

    //* helper function that returns the input with the given name for the silk source input form
    private WebElement getSilkDialogInput(String dialogName, String name){
        String sourceDivId=driver.findElement(By.xpath("//*[local-name() ='div']" +
                "[.//*[local-name()='span'][@class = 'ui-dialog-title'][text()='"+dialogName+"']]/" +
                "*[local-name()='div'][@id]")).getAttribute("id");
        return driver.findElement(By.xpath("//*[@id='"+sourceDivId+"']//" +
                "*[local-name() = 'td'][preceding-sibling::" +
                "*[local-name() = 'td'][text()[contains(.,'"+name+"')]]]//*[local-name()='input']"));
    }

    //* helper function for the silk component that creates a new sparql output with the given name
    private void addSilkSparqlOutput(String name, String project, String virtUser, String virtPwd){
        getSilkActionButton("Output",project).click();

        // select sparql output option
        WebElement select =driver.findElement(By.xpath("//*[local-name()='select'][./*[local-name()='option'][text()='SPARQL/Update']]"));
        select.sendKeys("SPARQL");
        bf.bePatient(200);

        // NOTE: the following code is a hack that works around this issue
        // find the div above the select that has an id for access
        //String id=driver.findElement(By.xpath("//*[./*[local-name()='select']" +
        //"[./*[local-name()='option'][text()='SPARQL/Update']]]")).getAttribute("id");



        getSilkDialogInput("Output","name").sendKeys(name);
        getSilkDialogInput("Output","uri").sendKeys("http://localhost:8890/sparql");
        getSilkDialogInput("Output","login").sendKeys(virtUser);
        getSilkDialogInput("Output","password").sendKeys(virtPwd);
        getSilkDialogInput("Output","graphUri").sendKeys(testGraph);

        driver.findElement(By.xpath("//*[local-name()='div'][preceding-sibling::" +
                "*[./*[local-name()='span'][@class='ui-dialog-title'][text()='Output']]]//" +
                "*[local-name() = 'input'][@value='Save']")).click();

        bf.waitUntilElementIsVisible("Could not find the source "+name+ " which should have been created",
                By.xpath("//*[local-name()='span'][@class='output']/*[local-name()='span'][text()='"+name+"']"));
    }

    @Test
    /**
     * Tests the cubeviz visualization functionality. No checks are made to the internal workings of cubeviz as that is
     * still likely to change.
     * pre: the current graph is set to the test graph
     * post: the screen shows the cubeviz interface
     */
    public void visualizeGraph(){
        navigator.navigateTo(new String[]{"Present & Publish", "Visualization with CubeViz"});

        bf.checkIFrame(By.xpath("//iframe[contains(@src, 'cubeviz')]"),
                By.id("modellist"));

        driver.findElement(By.xpath("//h1[text()[contains(.,'"+testGraph+"')]]"));
        driver.findElement(By.xpath("//*[local-name()='svg']"));
    }

    @Test
    /**
     * Tests whether we can remove the graph we just created from the database using our own graph removal service
     * pre: the testgraph has been added to the database
     * post: the testgraph is no longer in the database
     */
    public void removeGraph(){
        // clear the current graph panel first
        navigator.navigateTo(new String[]{"Manage Graph", "Select Default Graph"});
        driver.findElement(By.xpath("//div[@class='v-filterselect-button']")).click();
        WebElement graph=
                bf.waitUntilElementIsVisible("Could not find the empty graph in the list of possible current graphs",
                        By.xpath("//div[@class='popupContent']//tbody//span[not(text()[contains(.,'http')])]"));
        graph.click();
        driver.findElement(By.xpath("//div[@class='v-button']//span[text()='Set configuration']")).click();

        navigator.navigateTo(new String[]{"Manage Graph", "Remove Graphs"});
        driver.findElement(By.xpath("//div[preceding-sibling::div//div[text()[contains(.,'Filter:')]]]//" +
                "input[@type='text']")).sendKeys(testGraph);
        bf.bePatient(500); // updating filter
        driver.findElement(By.xpath("//div[@class='v-button']" +
                "[.//span[text()[contains(.,'Mark selected graphs')]]]")).click();
        driver.findElement(By.xpath("//div[@class='v-button']" +
                "[.//span[text()[contains(.,'Delete marked graphs')]]]")).click();

        bf.waitUntilElementIsVisible("Could not find confirmation dialog",
                By.xpath("//div[preceding-sibling::div//div[text()[contains(.,'Are you sure?')]]]//" +
                        "div[@class='v-button'][.//span[text()='Yes']]")).click();

        bf.waitUntilElementDisappears(By.xpath("//div[@class='v-table-cell-wrapper'][text()='" + testGraph + "']"));
        navigator.navigateTo(new String[]{"Manage Graph", "Select Default Graph"});
        driver.findElement(By.xpath("//div[@class='v-filterselect-button']")).click();
        bf.bePatient(500);

        try{
            driver.findElement(By.xpath("//div[@class='popupContent']//span[text()='"+testGraph+"']"));
            Assert.fail("The test graph was not correctly removed");
        }catch (NoSuchElementException e){
            //ok could not find the element
        }
    }
}