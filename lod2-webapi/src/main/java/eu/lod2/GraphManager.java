package eu.lod2;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

public class GraphManager {

  // The virtuoso repository
  private String jDBCconnectionstring = "jdbc:virtuoso://localhost:1111";
  private String jDBCuser = "dba";
  private String jDBCpassword = "dba";
  private Integer offset = 100;
  private String systemGraph = "<http://localhost/lod2democonfiguration>";
  private String localSparqlEndpoint = "<http://localhost:8890/sparql>";
  private String odbcfile = "";

  private Connection connection;
  // ErrorMessage
  private String ErrorMessage = "";



  public GraphManager() {

    // initialize the graphmanager
    readConfiguration();

    connection = null;
    try {
          Class.forName("sun.jdbc.odbc.JdbcOdbcDriver") ;

          connection = DriverManager.getConnection("jdbc:odbc:"+odbcfile);


      } catch (Exception e) {
          System.err.println("Exception: "+e.getMessage());

        // using jbdc connection string
        try {
            Class.forName("virtuoso.jdbc3.Driver");

            connection = DriverManager.getConnection(jDBCconnectionstring,jDBCuser,jDBCpassword);

        } catch (Exception ee) {
            System.out.print(ee);
            ee.printStackTrace();
        }
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
      odbcfile             = properties.getProperty("LocalSparqlEndpoint", "");

    } catch (IOException e) {
      // the file is absent or has faults in configuration settings
      ErrorMessage = "Reading configuration file in /etc/lod2webapi/lod2webapi.conf failed.";
      // print more detail to catalina logs
      e.printStackTrace();
    };
  };

  //* returns a query statement with the default config
  private Statement getConnectionStatement() throws SQLException{
      Statement statement=connection.createStatement();
      statement.setQueryTimeout(400);
      return statement;
  }

  /* every graph must be registered as a real graph in the database even if it is not a local graph
     XXX this assumption might have to be lifted.
   */

  /* Virtuoso syntax: select top from, length ...
     where from is the row counting from 0
   */

  /*
    Query syntax agreements:
    The query is written without the select keyword, as it will be subject to change to retrieve additional information
    by issueing variants of the query
  */

  public WebApiList getAllGraphs() {

      String query= "FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH";
	return getGraphsQuery(query);
	};

  public WebApiList getAllGraphs(Integer start) {

      String query= "FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH";
	return getGraphsQuery(query, start);
	};


  public WebApiList getNonSystemGraphs() {

	String query = "FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH as all_graphs where ID_TO_IRI(REC_GRAPH_IID) not in (sparql select ?g where {?g <http://lod2.eu/lod2demo/SystemGraphFor> ?o.})";
	return getGraphsQuery(query);
	};

  public WebApiList getNonSystemGraphs(Integer start) {

	String query = "FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH as all_graphs where ID_TO_IRI(REC_GRAPH_IID) not in (sparql select ?g where {?g <http://lod2.eu/lod2demo/SystemGraphFor> ?o.})";
	return getGraphsQuery(query, start);
	};

  public WebApiList getAllSystemGraphs() {

        String query = "FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH as all_graphs where ID_TO_IRI(REC_GRAPH_IID) in (sparql select ?g where {?g <http://lod2.eu/lod2demo/SystemGraphFor> ?o.})";
	return getGraphsQuery(query);
  };

  public WebApiList getAllSystemGraphs(Integer start) {

        String query = "FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH as all_graphs where ID_TO_IRI(REC_GRAPH_IID) in (sparql select ?g where {?g <http://lod2.eu/lod2demo/SystemGraphFor> ?o.})";
	return getGraphsQuery(query, start);
  };

  public WebApiList getAllGraphsRegex(String regex) {

        String query = "FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH where ID_TO_IRI(REC_GRAPH_IID) like '"+regex +"'";
	return getGraphsQuery(query);
  };

  public WebApiList getAllGraphsRegex(String regex, Integer start) {

        String query = "FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH where ID_TO_IRI(REC_GRAPH_IID) like '"+regex +"'";
	return getGraphsQuery(query, start);
  };

  public WebApiList getNonSystemGraphsRegex(String regex) {

	String query = "FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH as all_graphs where ID_TO_IRI(REC_GRAPH_IID) not in (sparql select ?g where {?g <http://lod2.eu/lod2demo/SystemGraphFor> ?o.}) and ID_TO_IRI(REC_GRAPH_IID) like '"+regex +"'";
	return getGraphsQuery(query);
  };

  public WebApiList getNonSystemGraphsRegex(String regex, Integer start) {

	String query = "FROM DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH as all_graphs where ID_TO_IRI(REC_GRAPH_IID) not in (sparql select ?g where {?g <http://lod2.eu/lod2demo/SystemGraphFor> ?o.}) and ID_TO_IRI(REC_GRAPH_IID) like '"+regex +"'";
	return getGraphsQuery(query, start);
  };

    /**
     * Retrieves the complete set of graphs that match the given regex, even the graphs that are not in the DB.DBA.RDF_EXPLICITLY_CREATED_GRAPH table
     * @param regex the regex to match
     * @param start the offset to start from
     */
  public WebApiList getReallyAllGraphs(String regex, int start
  ) throws SQLException {

      String countQuery = "sparql SELECT COUNT( DISTINCT ?g) AS ?c WHERE {\n" +
              "             GRAPH ?g {\n" +
              "             ?s ?p ?o\n" +
              "            } FILTER regex(?g,\""+regex+"\")\n" +
              "}";
      Statement counter= getConnectionStatement();

      ResultSet mycount = counter.executeQuery(countQuery);

      Integer total = 0 ;
      while (mycount.next()) {
          total = (Integer) mycount.getInt("c");
      }

      // then do actual query
      WebApiList graphs=new WebApiList();

      String query = "sparql SELECT DISTINCT ?g WHERE {\n" +
              "  GRAPH ?g {\n" +
              "    ?s ?p ?o\n" +
              "  } FILTER regex(?g,\""+regex+"\")\n" +
              "}"+(offset>0?" ORDER BY ?g LIMIT "+offset+" OFFSET "+start:"");

      Statement retriever=getConnectionStatement();

      ResultSet results=retriever.executeQuery(query);
      ArrayList<String> graphList=new ArrayList<String>();

      while (results.next()){
          graphList.add(results.getString("g"));
      }

      if (graphList.size() == offset && start+offset < total) {
          graphs.setMore(true);
      } else {
          graphs.setMore(false);
      }

      graphs.setStart(start);
      graphs.setListSize(graphList.size());
      graphs.setResultList(graphList);
      graphs.setTotalAmount(total);

      return graphs;
  }

  public WebApiList getAllEndPoints() {

        //String query= "sparql select ?graph where {?graph a ?c.} limit 10 offset 0";
        String query= "where {?g a <http://lod2.eu/lod2demo/SparqlEndpoint>.}";
	return getSPARQLquery(query, 0);
	};

  public WebApiList getAllEndPoints(Integer start) {

        String query= "where {?g a <http://lod2.eu/lod2demo/SparqlEndpoint>.}";
	return getSPARQLquery(query, start);
	};


  /* generic querying */

  private WebApiList getGraphsQuery(String query) {

	return getGraphsQuery(query, 0);
  };

  private WebApiList getGraphsQuery(String query, Integer start) {

    WebApiList webResult = new WebApiList();
    ArrayList<String> graphs =new ArrayList<String>();
    try {
      String selectedColumn = "ID_TO_IRI(REC_GRAPH_IID)"; 

      Statement allgraphs_st = getConnectionStatement();
      String countQuery = "SELECT count(*) as C " + query;
      System.out.print(countQuery);
      ResultSet mycount = allgraphs_st.executeQuery(countQuery);
      Integer total = 0 ; 
      while (mycount.next()) {
      	total = (Integer) mycount.getInt("C");
      };
	
      String selectQuery = "SELECT TOP "+ start + ","+ offset + " " + selectedColumn + " AS GRAPH " + query;
      System.out.print(selectQuery);
      ResultSet rs = allgraphs_st.executeQuery(selectQuery);
      while (rs.next())
      {
	String s = rs.getString("GRAPH");
	graphs.add(s); // should be of the form < ... >. For that first the mediaType must be real application/json
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

      Statement allgraphs_st = getConnectionStatement();
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
	graphs.add(s); // should be of the form < ... >. For that first the mediaType must be real application/json
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
      mycount.close();
      rs.close();

    } catch (SQLException ex) {
      System.out.print(ex.getMessage());
    };
    return webResult;
  };


  /* Updating the store */


	// register <graph> >
  public void registerGraph(String graph) {

    try {
      Statement allgraphs_st = getConnectionStatement();
      allgraphs_st.execute("sparql create silent graph <"+graph+">");
    } catch (SQLException ex) {
      System.out.print(ex.getMessage());
    };
  };

 /**
  * Removes the graph with the given name from the store
  * @param graph : the name of the graph to remove
  */
  public void removeGraph(String graph) {
      try{
          Statement remover=getConnectionStatement();
          remover.execute("sparql drop silent graph <"+graph+">");
      }catch (SQLException e){
          System.out.print(e.getMessage());
      }
  }

	// register <graph> as a system graph for <tool>
  public void insertSystemGraph(String graph, String tool) {

    try {
      Statement allgraphs_st = getConnectionStatement();
      allgraphs_st.execute("sparql insert data into " + systemGraph + " { <" + graph +
		"> <http://lod2.eu/lod2demo/SystemGraphFor> <"+tool+">.}");
      allgraphs_st.execute("sparql create silent graph <"+graph+">");
    } catch (SQLException ex) {
      System.out.print(ex.getMessage());
    };
  };

	// register <graph> as a graph in <endpoint>
  public void registerGraphforSparqlEndpoint(String graph, String endpoint) {

    try {
      Statement allgraphs_st = getConnectionStatement();
      allgraphs_st.execute("sparql insert data into " + systemGraph + " { <" + graph +
		"> <http://lod2.eu/lod2demo/GraphIn> <"+ endpoint +">.}");
    } catch (SQLException ex) {
      System.out.print(ex.getMessage());
    };
  };

	// register <endpoint> as external endpoint
  public void registerSparqlEndpoint(String endpoint) {

    try {
      Statement allgraphs_st = getConnectionStatement();
      allgraphs_st.execute("sparql insert data into " + systemGraph + " { <" + endpoint +
		"> a <http://lod2.eu/lod2demo/SparqlEndpoint>.}");
    } catch (SQLException ex) {
      System.out.print(ex.getMessage());
    };
  };

    /**
     * Builds a graph group called group that holds the given graphs as children.
     * Note: for the graph to be visible as well, please call #registerGraph as well.
     * @param group the URI of the new graph group
     * @param graphs the set of graphs to add as children
     */
  public void buildGraphGroup(String group, List<String> graphs) throws Exception{
     Statement builder = getConnectionStatement();
      String buildQuery="DB.DBA.RDF_GRAPH_GROUP_CREATE ('"+group +"', 0 )";
      builder.execute(buildQuery);
      for(String graph : new HashSet<String>(graphs)){
          Statement adder= getConnectionStatement();
          String addQuery="DB.DBA.RDF_GRAPH_GROUP_INS ('"+group+"', '"+graph+"')";
          adder.execute(addQuery);
      }
  }

    /**
     * Removes the given graph group from the triple store
     * @param group URI of graph group to remove
     */
  public void dropGraphGroup(String group) throws Exception{
      try {
          Statement remover = getConnectionStatement();
          String query= "DB.DBA.RDF_GRAPH_GROUP_DROP('" +group +"', 0 )";
          remover.execute(query);

      } catch (SQLException ex) {
          System.out.print(ex.getMessage());
      }

  }


  void prnRs(ResultSet rs)
 {
   try {
     ResultSetMetaData rsmd;

     System.out.println(">>>>>>>>");
     rsmd = rs.getMetaData();
     int cnt = rsmd.getColumnCount();

       while(rs.next()) {
         Object o;

         System.out.print("Thread:"+Thread.currentThread().getId()+"  ");
         for (int i = 1; i <= cnt; i++) {
           o = rs.getObject(i);
           if (rs.wasNull())
             System.out.print("<NULL> ");
           else
             System.out.print("["+ o + "] ");
         }
         System.out.println();
       }

   } catch (Exception e) {
     System.out.println(e);
     e.printStackTrace();
   }
   System.out.println(">>>>>>>>");
 }
}
