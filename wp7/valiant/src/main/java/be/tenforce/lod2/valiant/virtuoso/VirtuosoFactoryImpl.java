package be.tenforce.lod2.valiant.virtuoso;

import be.tenforce.lod2.valiant.Namespace;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;

@Service
public class VirtuosoFactoryImpl implements VirtuosoFactory {

  private static final Logger log = Logger.getLogger(VirtuosoFactoryImpl.class);

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

  private String jdbcUrl;

  @PostConstruct
  private void initialize() {
    validate();
  }

  public void add(File inputFile, String fileName) {
    String graphName = namespace.getBaseURI() + fileName;
    VirtGraph graph = new VirtGraph(graphName, jdbcUrl, username, password);
    Model model = new VirtModel(graph);
    try {
      model.read(new FileInputStream(inputFile), namespace.getBaseURI());
      log.info("graph '<" + graphName + ">' loaded in virtuoso");
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    finally {
      model.close();
    }
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
