package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.TestCase;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

/**
 * This class contains functional tests concerning online tools and service of
 * the lod2 - stack.
 * All TestCases are documented here:
 * https://grips.semantic-web.at/display/LOD2/LOD2Stack+Test+Cases+-+Online+Tools+and+Service
 * 
 * @author Stefan Schurischuster
 * @email s.schurischuster@semantic-web.at
 */
public class OnlineToolsAndServices extends TestCase {
    /**
     * TC 001.
     */
    @Test
    public void sameAs()  {
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "SameAs"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'sameas.org')]"), 
                By.id("intro"));
    }
    
    /**
     * TC 002.
     */
    @Test
    public void sindice()  {
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "Sindice"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'sindice.com')]"), 
                By.id("search-w"));
    }
    
    /**
     * TC 003.
     */
    @Test
    public void sigma()  {
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "Sigma"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'sig.ma')]"), 
                By.id("search-w"));
    }   

            
    /**
     * TC 004.
     */
    @Test
    public void ckan()  {
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "CKAN"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'ckan.net')]"), 
                By.id("content"));
    }     
    
            
    /**
     * TC 005.
     */
    @Test
    public void europesPublicData()  {
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "s Public Data"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'publicdata.eu')]"), 
                By.id("map"));
    }       
            
    /**
     * TC 006.
     */
    @Test
    public void poolParty()  {
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "PoolParty"});  
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'poolparty')]"), 
                By.id("TabSet"));
    }            
            
    /**
     * TC 007.
     */
    @Test
    public void lodCloud()  {
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "Online SPARQL endpoints",
            "LOD cloud"});
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'openlinksw')]"), 
                By.id("new_search_txt"));
    }

    /**
     * TC 008.
     */
    @Test
    public void dbpedia()  {
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "Online SPARQL endpoints",
            "DBpedia"});
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'dbpedia.org')]"), 
                By.xpath("//a[@href='http://dbpedia.org']"));
    }    
    
    /**
     * TC 009.
     */
    @Test
    public void poolPartySparqlEndpoint()  {
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "Online SPARQL endpoints",
            "PoolParty SPARQL endpoint"});
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'poolparty')]"), 
                By.id("query"));
    }     
    
    /**
     * TC 010.
     */
    @Test
    public void mondecaSparqlEndpointCollection()  {
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "Online SPARQL endpoints",
            "Mondeca SPARQL endpoint Collection"});
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'mondeca.com')]"), 
                By.id("endpoint_stats"));
    }      
    
    /**
     * TC 011.
     */
    @Test
    public void sindiceWebLinkageValidator()  {
        
        navigator.navigateTo(new String[] {
            "Online Tools", 
            "Sindice Web Linkage Validator"});
        
        bf.checkIFrame(
                By.xpath("//iframe[contains(@src,'demo.sindice')]"), 
                By.id("wrapper"));
    }
}
