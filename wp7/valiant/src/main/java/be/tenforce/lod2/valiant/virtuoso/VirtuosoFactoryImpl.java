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
  //private FileWriter fw;

  @PostConstruct
  private void initialize() {
    validate();
    setup();
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

  private void validate() {
    if (null == host || host.length() == 0) throw new RuntimeException("Virtuoso host is not set");
    if (null == port || port.length() == 0) throw new RuntimeException("Virtuoso port is not set");
    if (null == username || username.length() == 0) throw new RuntimeException("Virtuoso username is not set");
    if (null == password || password.length() == 0) throw new RuntimeException("Virtuoso password is not set");
    jdbcUrl = new StringBuilder("jdbc:virtuoso://").append(host).append(":").append(port).toString();
    logFields();
  }

  private void logFields() {
    log.info("Virtuoso host: " + host);
    log.info("Virtuoso port: " + port);
    log.info("Virtuoso username: " + username);
    log.info("Virtuoso password: " + password);
    log.info("Virtuoso jdbc url: " + jdbcUrl);
  }
}
