package be.tenforce.lod2.valiant;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class DavConnector {
  private static final Logger log = Logger.getLogger(DavConnector.class);

  @Value("#{properties.host}")
  private String host;

  @Value("#{properties.user}")
  private String user;

  @Value("#{properties.password}")
  private String password;

  @Value("#{properties.webdavUrl}")
  private String url;

  @Value("#{properties.webdavOutputFolder}")
  private String outputFolder;

  private Sardine sardine;
  private List<DavResource> resources;

  @PostConstruct
  private void initialize() {
    validate();
    setup();
  }

  public InputStream getInputStream(DavResource resource) {
    if (null == resource) return null;
    try {
      return sardine.get(host + resource.getPath());
    }
    catch (Exception e) {
      log.error("Failed to retrieve resource: " + e.getMessage(), e);
    }
    return null;
  }

  public void putStream(InputStream input, String name, String mimetype) {
    if (null == input || null == name || null == mimetype) return;
    try {
      sardine.put(host + outputFolder + name, input, mimetype);
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public List<DavResource> getResources() {
    return resources;
  }

  private void validate() {
    if (null == host || host.length() == 0) throw new RuntimeException("Host url is not set.");
    if (null == url || url.length() == 0) throw new RuntimeException("Webdav url is not set.");
    logFields();
  }

  private void setup() {
    this.sardine = SardineFactory.begin(user, password);
    try {
      this.resources = sardine.list(host + url);
    }
    catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private void logFields() {
    log.info("host: " + host);
    log.info("user: " + user);
    log.info("password: " + password);
    log.info("webdavInputFolder: " + host + url);
    log.info("webdavOutputFolder: " + host + outputFolder);
  }
}
