package be.tenforce.lod2.valiant.virtuoso;

import be.tenforce.lod2.valiant.Namespace;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import virtuoso.jdbc3.VirtuosoDataSource;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class VirtuosoFactoryImpl implements VirtuosoFactory {

  private static final Logger log = Logger.getLogger(VirtuosoFactoryImpl.class);

  @Value("#{properties.rdfFolder}")
  private String rdfFolder;

  @Value("#{properties['virtuoso.host']}")
  private String host;

  @Value("#{properties['virtuoso.port']}")
  private String port;

  @Value("#{properties['virtuoso.username']}")
  private String username;

  @Value("#{properties['virtuoso.password']}")
  private String password;

  @Autowired(required = true)
  private Namespace namespace;

  private VirtuosoDataSource virtuosoDataSource;
  private String jdbcUrl;
  public Boolean isInitialized = false;

  @PostConstruct
  private void initialize() {
    Boolean canInit = validate();
    if (canInit) { 
    	jdbcUrl = new StringBuilder("jdbc:virtuoso://").append(host).append(":").append(port).toString();
	setup();
	isInitialized = true;
	};
  }

  @PreDestroy
  private void exit() {
    try {
      virtuosoDataSource.getConnection().commit();
      virtuosoDataSource.getConnection().close();
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void add(ByteArrayOutputStream outputStream, String fileName) {
    String graphName = namespace.getBaseURI() + fileName;
    VirtGraph graph = new VirtGraph(graphName, virtuosoDataSource);
    Model model = new VirtModel(graph);
    try {
      //File graphFile = new File(rdfFolder + fileName.replaceAll("(?i).rdf",".graph"));
      //fw = new FileWriter(graphFile,true);
      model.read(new ByteArrayInputStream(outputStream.toByteArray()), namespace.getBaseURI());
      log.info("graph '<" + graphName + ">' loaded in virtuoso");
      //fw.write(graphName);
      //fw.close();
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      model.close();
    }
  }

	// maybe one should issue create silent graph here
   public void addToGraph(ByteArrayOutputStream outputStream, String fileName, String graphName) {
    VirtGraph graph = new VirtGraph(graphName, virtuosoDataSource);
    Model model = new VirtModel(graph);
    try {
      //File graphFile = new File(rdfFolder + fileName.replaceAll("(?i).rdf",".graph"));
      //fw = new FileWriter(graphFile,true);
      model.read(new ByteArrayInputStream(outputStream.toByteArray()), namespace.getBaseURI());
      log.info("graph '<" + graph.getGraphName() + ">' loaded in virtuoso");
      //fw.write(namespace.getBaseURI() + graphName);
      //fw.close();
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      model.close();
    }
  }
 

  private void setup() {
    virtuosoDataSource = new VirtuosoDataSource();
    virtuosoDataSource.setServerName(host);
    virtuosoDataSource.setPortNumber(Integer.parseInt(port));
    virtuosoDataSource.setUser(username);
    virtuosoDataSource.setPassword(password);
    virtuosoDataSource.setCharset("UTF-8");
  }

  private Boolean validate() {
    logFields();
    Boolean canInit = true;
    if (null == host || host.length() == 0) { canInit = false; };
    if (null == port || port.length() == 0) { canInit = false; };
    if (null == username || username.length() == 0) { canInit = false; };
    if (null == password || password.length() == 0) { canInit = false; };
    return canInit;
  }

  private void logFields() {
    log.info("Virtuoso host: " + host);
    log.info("Virtuoso port: " + port);
    log.info("Virtuoso username: " + username);
    log.info("Virtuoso password: " + password);
    log.info("Virtuoso jdbc url: " + jdbcUrl);
  }
}
