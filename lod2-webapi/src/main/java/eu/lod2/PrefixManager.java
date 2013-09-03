package eu.lod2;

import java.util.Properties;
import java.io.*;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Array;
import org.apache.commons.lang3.StringUtils;

import virtuoso.jdbc3.*;

import eu.lod2.WebApiList;
import eu.lod2.PrefixCC;

public class PrefixManager {

  // The virtuoso repository
  private String jDBCconnectionstring = "jdbc:virtuoso://localhost:1111";
  private String jDBCuser = "dba";
  private String jDBCpassword = "dba";
  private Integer offset = 100;
  private String systemGraph = "<http://localhost/lod2democonfiguration>";
  private String localSparqlEndpoint = "<http://localhost::8890/sparql>";

  private Connection connection;
  // ErrorMessage
  private String ErrorMessage = "";

  public PrefixManager() {

    // initialize the graphmanager
    readConfiguration();

    try {
      Class.forName("virtuoso.jdbc3.Driver");

      connection = DriverManager.getConnection(jDBCconnectionstring,jDBCuser,jDBCpassword);

	try{
  		Statement st = connection.createStatement();
  		ResultSet rs = st.executeQuery("SELECT * FROM DB.DBA.SYS_XML_PERSISTENT_NS_DECL");
  		ResultSetMetaData md = rs.getMetaData();
  		int col = md.getColumnCount();
		Boolean existsPreferred = false;
		for (int i = 1; i <= col; i++){
		  String col_name = md.getColumnName(i);
		  if (col_name.equals("Preferred")) {
			existsPreferred = true;
		  }
		};
		if (!existsPreferred) {
  			Statement st_alter = connection.createStatement();
  			int rs_alter = st_alter.executeUpdate("alter table DB.DBA.SYS_XML_PERSISTENT_NS_DECL add Preferred integer");
			st_alter.close();
		};
		st.close();
	  }
	  catch (SQLException s){
	  	System.out.println("testing if Preferred Column exists failed");
	  	System.out.println(s.getMessage());
	  }
    } catch (Exception e) {
      System.err.print(e);
      e.printStackTrace();
    }

  };


  // read the local configuration file /etc/lod2demo/lod2demo.conf
  private void readConfiguration() {

    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream("/etc/lod2webapi/lod2webapi.conf"));
      jDBCconnectionstring = properties.getProperty("JDBCconnection");
      jDBCuser             = properties.getProperty("JDBCuser");
      jDBCpassword         = properties.getProperty("JDBCpassword");
      offset               = Integer.parseInt(properties.getProperty("Offset"));
      systemGraph          = properties.getProperty("SystemGraph");
      localSparqlEndpoint  = properties.getProperty("LocalSparqlEndpoint");
     
      // alter statement alter table DB.DBA.SYS_XML_PERSISTENT_NS_DECL add Preferred integer

    } catch (IOException e) {
      // the file is absent or has faults in configuration settings
      ErrorMessage = "Reading configuration file in /etc/lod2webapi/lod2webapi.conf failed.";
      System.err.println(ErrorMessage);
    };
  };

  /*
    Query syntax agreements:
    The query is written without the select keyword, as it will be subject to change to retrieve additional information
    by issueing variants of the query
  */

  public WebApiList getPrefixes() {

      String query= "FROM DB.DBA.SYS_XML_PERSISTENT_NS_DECL";
	return getPrefixQuery(query);
	};

  public WebApiList getPrefixes(Integer start) {

        String query= "FROM DB.DBA.SYS_XML_PERSISTENT_NS_DECL";
	String variable = "NS_Prefix";
	return getPrefixQuery2(variable, query, start);
	};


       // should not be called with second argument having null
       // return all URI's for a list of abbreviations
  public WebApiList getPrefixesLimitAbbrev(Integer start, String[] abbrev) {

     WebApiList result;
     if (abbrev == null) {
	result = getPrefixes(start);
     } else {

	     ArrayList<String> escapedAbbrev = new ArrayList();

   	     for (String ab0 : abbrev ) {
 			String ab = "\'" + ab0 + "\'";
			escapedAbbrev.add(ab);
		};
		
	     String inabbrev = StringUtils.join(escapedAbbrev, ",");	
	     String query= "FROM DB.DBA.SYS_XML_PERSISTENT_NS_DECL where NS_Prefix in ("+inabbrev+")";
	     String variable = "NS_URL";
	     result = getPrefixQuery3(variable, query, start, abbrev, true);
     };
     return result;
  };

       // should not be called with second argument having null
       // return all prefixes for a list of URI's
  public WebApiList getPrefixesLimitURI(Integer start, String[] uris) {

     WebApiList result;
     if (uris == null) {
	result = getPrefixes(start);
     } else {

	     ArrayList<String> escaped = new ArrayList();

   	     for (String ab0 : uris ) {
 			String ab = "\'" + ab0 + "\'";
			escaped.add(ab);
		};
		
	     String inuris = StringUtils.join(escaped, ",");	
	     String query= "FROM DB.DBA.SYS_XML_PERSISTENT_NS_DECL where NS_URL in ("+inuris+")";
	     String variable = "NS_Prefix";
	     result = getPrefixQuery3(variable, query, start, uris, false);
     };
     return result;
  };


  /* generic querying */

  private WebApiList getPrefixQuery(String query) {
 
	String variable = "NS_Prefix";
	return getPrefixQuery2(variable, query, 0);
  };

  private WebApiList getPrefixQuery(String selectedColumn, String query, Integer start) {

    WebApiList webResult = new WebApiList();
    ArrayList<String> graphs =new ArrayList<String>();
    try {

      Statement allgraphs_st = connection.createStatement();
      String countQuery = "SELECT count(*) as C " + query;
//      System.out.print(countQuery);
      ResultSet mycount = allgraphs_st.executeQuery(countQuery);
      Integer total = 0 ; 
      while (mycount.next()) {
      	total = (Integer) mycount.getInt("C");
      };
	
      String selectQuery = "SELECT TOP "+ start + ","+ offset + " " + selectedColumn + " AS Prefix " + query;
//      System.out.print(selectQuery);
      ResultSet rs = allgraphs_st.executeQuery(selectQuery);
      while (rs.next())
      {
	String s = rs.getString("Prefix");
	graphs.add(s); 
      };
      if (graphs.size() == offset && start+offset < total) {
	webResult.setMore(true);
	} else {
	webResult.setMore(false);
	};
      webResult.setStart(start);
      webResult.setListSize(graphs.size());
      webResult.setResultList(graphs);
      webResult.setTotalAmount(total);
	// free up memory
      allgraphs_st.close();
      mycount.close();
      rs.close();

    } catch (SQLException ex) {
      System.out.print(ex.getMessage());
    };
    return webResult;
  };

  private WebApiList getPrefixQuery2(String selectedColumn, String query, Integer start) {

	return getPrefixQuery3(selectedColumn, query, start, null, true);

  };

        // if kind = true then ab = abbreviations
        // if kind = false then ab = uri's
  private WebApiList getPrefixQuery3(String selectedColumn, String query, Integer start, String[] ab, Boolean kind) {

    WebApiList webResult = new WebApiList();
    ArrayList<String> graphs =new ArrayList<String>();
    HashMap<String, String> abbrev = new HashMap();
    try {

      Statement allgraphs_st = connection.createStatement();
      String countQuery = "SELECT count( distinct NS_URL ) as C " + query;
//      System.out.println(countQuery);
      ResultSet mycount = allgraphs_st.executeQuery(countQuery);
      Integer total = 0 ; 
      while (mycount.next()) {
      	total = (Integer) mycount.getInt("C");
      };
	
      String selectQuery = "SELECT TOP "+ start + ","+ offset + " * "   + query;
//      System.out.println(selectQuery);
      ResultSet rs = allgraphs_st.executeQuery(selectQuery);
      while (rs.next())
      {
	String prefix = rs.getString("NS_Prefix");
	String url    = rs.getString("NS_URL");
        Integer pref  = rs.getInt("Preferred");
	
	if (abbrev.containsKey(url)) {
		if (pref == 1) {
			abbrev.put(url, prefix);
		};
        } else {
		abbrev.put(url, prefix);
	};
      };

      if (ab != null) {
	
      HashMap<String, Boolean> checka = new HashMap();
      // initialize the to be retrieved strings
      for (String a : ab) {
		checka.put(a, false);
	};
	

      for (Map.Entry<String,String> entry : abbrev.entrySet()) {
	 if (kind) { 
	 	checka.put(entry.getValue(), true);
         } else {	
	 	checka.put(entry.getKey(), true);
	 };
	};

	// XXX this is a HARD LIMIT => TO BE REMOVED
      String[] abcc = new String[100];
      int i = 0;
      for (Map.Entry<String, Boolean> centry: checka.entrySet()) {
	if (centry.getValue() == false) {
		abcc[i] = centry.getKey();
		i = i + 1;
	};
	};

      Map<String, String> pccresult = new HashMap();
      try {
        if (kind) {
        	pccresult = PrefixCC.request_uri(abcc);
	} else {
		pccresult = PrefixCC.request_abbrev(abcc);
	};
	} catch (Exception e) {
		pccresult = new HashMap();
	}
      abbrev.putAll(pccresult);

      };

      for (Map.Entry<String,String> entry : abbrev.entrySet()) {
	    String key = entry.getKey();
	    String value = entry.getValue();
	    graphs.add(key + " - " +value);
	};
     

      if (graphs.size() == offset && start+offset < total) {
	webResult.setMore(true);
	} else {
	webResult.setMore(false);
	};
      webResult.setStart(start);
      webResult.setListSize(graphs.size());
      webResult.setResultList(graphs);
      webResult.setTotalAmount(total);
		// free up memory
      allgraphs_st.close();
      mycount.close();
      rs.close();

    } catch (SQLException ex) {
      System.out.print(ex.getMessage());
    };
    return webResult;
  };


  private WebApiList getSPARQLquery(String query, Integer start) {

    WebApiList webResult = new WebApiList();
    ArrayList<String> graphs =new ArrayList<String>();
    try {

      Statement allgraphs_st = connection.createStatement();
      String countQuery = "sparql SELECT count(*) as ?C from " + systemGraph +  query;
      System.out.print(countQuery);
      ResultSet mycount = allgraphs_st.executeQuery(countQuery);
      Integer total = 0 ; 
      while (mycount.next()) {
      	total = (Integer) mycount.getInt("C");
      };
	
      String selectQuery = "sparql select ?g as ?GRAPH from " + systemGraph + query + " limit " + offset + " offset " + start;
      System.out.print(selectQuery);
      ResultSet rs = allgraphs_st.executeQuery(selectQuery);
      while (rs.next())
      {
	String s = rs.getString("GRAPH");
	graphs.add(":"+ s + ":"); // should be of the form < ... >. For that first the mediaType must be real application/json
      };
      if (graphs.size() == offset && start+offset < total) {
	webResult.setMore(true);
	} else {
	webResult.setMore(false);
	};
      webResult.setStart(start);
      webResult.setListSize(graphs.size());
      webResult.setResultList(graphs);
      webResult.setTotalAmount(total);
	// free up memory
      allgraphs_st.close();
      mycount.close();
      rs.close();

    } catch (SQLException ex) {
      System.out.print(ex.getMessage());
    };
    return webResult;
  };


  /* Updating the store */

	// register abbreviation for a uri and indicate if it is preferred;
        // in case of setting the preference to true, then all the preferences for this uri are removed.
  public void insertPrefix(String abbrev, String uri, Boolean preferred) {

    try {
      Statement allgraphs_st = connection.createStatement();

      int p;
      String insert;
      if (preferred ==null || preferred == false) {
		p = 0;      
      	        insert = "insert replacing DB.DBA.SYS_XML_PERSISTENT_NS_DECL(NS_Prefix, NS_URL, Preferred) values(\'" + 
		      abbrev + "\', \'" + uri + "\' , " + p + " )";
      } else {
		String clear_all_preferences = "update DB.DBA.SYS_XML_PERSISTENT_NS_DECL set Preferred = 0 where NS_URL= \'" + uri + "\'";
      		allgraphs_st.execute(clear_all_preferences);
		p = 1;
      	        insert = "insert replacing DB.DBA.SYS_XML_PERSISTENT_NS_DECL(NS_Prefix, NS_URL, Preferred) values(\'" + 
		      abbrev + "\', \'" + uri + "\' , " + p + " )";
      };


      allgraphs_st.execute(insert);
      allgraphs_st.close();

    } catch (SQLException ex) {
      System.out.print(ex.getMessage());
    };
  };

}
