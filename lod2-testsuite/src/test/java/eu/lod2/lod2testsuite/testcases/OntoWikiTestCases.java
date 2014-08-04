package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.TestCase;
import eu.lod2.lod2testsuite.pages.OntoWikiPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class contains functional tests concerning authoring of
 * the lod2 - stack.
 * All TestCases are documented here:
 * https://grips.semantic-web.at/display/LOD2/LOD2Stack+Test+Cases+-+Authoring
 * 
 * @author Stefan Schurischuster
 * @email s.schurischuster@semantic-web.at
 */
public class OntoWikiTestCases extends TestCase {
    
    @BeforeMethod(alwaysRun=true)
    @Override
    public void prepareTestCase()  {
        logger.info("Preparing for onto wiki test...");
        logger.info("Current url: " +driver.getCurrentUrl());
        
        if(!driver.getCurrentUrl().equals(ontowikiUrl.toString()) &&
                !driver.getCurrentUrl().equals(ontowikiUrl.toString()+"/"))  {
            driver.get(ontowikiUrl.toString());
            logger.info("Navigating to: " +ontowikiUrl.toString());
        }
        // Reposition the browser view to be at the top.
        bf.scrollIntoView(bf.waitUntilElementIsVisible(By.id("application")));
    }

    @BeforeMethod(alwaysRun=true)
    @Override
    public void afterTestCase() {}


    /**
     * TC 001.
     * pre: ontoWiki is accessible.
     * post: user is logged into ontoWiki
     */
    @Test(groups = { "ontowiki" })
    @Parameters({"ontowiki.user","ontowiki.pw"})
    public void logIntoOntoWiki(String user, String pw)  {
        bf.waitUntilElementIsVisible("Ontowiki could not be loaded in time.",
                By.id("application"));
        OntoWikiPage ontoWiki = new OntoWikiPage();
        ontoWiki.logIntoOntoWiki(user, pw);
    }
    
    /**
     * TC 002.
     * pre: Knowledge Base with the same URI does not exist.
     * post: New knowledge base exists.
     */
    @Test(groups = { "ontowiki" })//, dependsOnMethods= {"logIntoOntoWiki"})
    @Parameters({"ontowiki.user","ontowiki.pw","knowledgeBaseTitle","knowledgeBaseUri"})
    public void createNewKnowledgeBase(String user, String pw,String knowledgeBaseTitle, String knowledgeBaseUri)  {        
        bf.waitUntilElementIsVisible("Ontowiki could not be loaded in time.",
                By.id("application"));
        OntoWikiPage ontoWiki = new OntoWikiPage();
        ontoWiki.logIntoOntoWiki(user, pw);
        ontoWiki.createNewKnowledgeBase(knowledgeBaseTitle, knowledgeBaseUri, "");
    }
    
    /**
     * TC 003.
     * pre: Knowledge base is accessible; Web resource is available.
     * post: Knowledge base has data added.
     */
    @Test(groups = { "ontowiki" })//, dependsOnMethods= {"logIntoOntoWiki","createNewKnowledgeBase"})
    @Parameters({"ontowiki.user","ontowiki.pw","knowledgeBaseUri","importUri"})
    public void addDataToKnowledgeBaseViaRDFFromWeb(String user, String pw, String knowledgeBaseUri, String importUri)  {
        bf.waitUntilElementIsVisible("Ontowiki could not be loaded in time.",
                By.id("application"));
        OntoWikiPage ontoWiki = new OntoWikiPage();
        ontoWiki.logIntoOntoWiki(user, pw);
        ontoWiki.addDataToKnowledgeBaseViaRDFFromWeb(knowledgeBaseUri, importUri);
    }
    
    /**
     * TC 004.
     * pre: Knowledge base is accessible; Resource with same title does not exist already.
     * post: New Resource exists in knowledge base.
     */
    @Test(groups = { "ontowiki" })//, dependsOnMethods= {"logIntoOntoWiki","createNewKnowledgeBase"})
    @Parameters({"ontowiki.user","ontowiki.pw","knowledgeBaseUri","resourceTitle"})
    public void addResource(String user, String pw, String knowledgeBaseUri, String resourceTitle)  {        
        bf.waitUntilElementIsVisible("Ontowiki could not be loaded in time.",
                By.id("application"));
        OntoWikiPage ontoWiki = new OntoWikiPage();
        ontoWiki.logIntoOntoWiki(user, pw);
        ontoWiki.addResource(knowledgeBaseUri, resourceTitle);
    }
   
    /**
     * TC 005.
     * pre: Resource is accessible; Instance with same title does not exist already.
     * post: New Instance is added to existing Resource.
     */
    @Test(groups = { "ontowiki" })//, dependsOnMethods= {"logIntoOntoWiki","createNewKnowledgeBase","addResource"})
    @Parameters({"ontowiki.user","ontowiki.pw","knowledgeBaseUri","resourceTitle","instanceTitle"})
    public void addInstanceToResource(String user, String pw, String knowledgeBaseUri, String resourceTitle, String instanceTitle)  {
        bf.waitUntilElementIsVisible("Ontowiki could not be loaded in time.",
                By.id("application"));
        OntoWikiPage ontoWiki = new OntoWikiPage();
        ontoWiki.logIntoOntoWiki(user, pw);
        ontoWiki.addInstanceToResource(knowledgeBaseUri, resourceTitle, instanceTitle);
    }
    
    
    /**
     * TC 006.
     * pre: Knowledge base to delete exists
     * post: Knowledge base is deleted.
     */
    @Test(groups = { "ontowiki" })
    @Parameters({ "ontowiki.user","ontowiki.pw","knowledgeBaseUri" })
    public void deleteKnowledgeBase(String username, String pw, String knowledgeBaseUri)  {
        bf.waitUntilElementIsVisible("Ontowiki could not be loaded in time.",
                By.id("application"));
        OntoWikiPage ontoWiki = new OntoWikiPage();   
        // Perform login if necessary
        ontoWiki.logIntoOntoWiki(username, pw);
        // Delete KB
        ontoWiki.deleteKnowledgeBase(knowledgeBaseUri);        
    }
}