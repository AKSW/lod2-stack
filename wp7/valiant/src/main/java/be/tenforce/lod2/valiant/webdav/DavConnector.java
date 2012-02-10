package be.tenforce.lod2.valiant.webdav;

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

  public Boolean isInitialized = false;


  @PostConstruct
  private void initialize() {
    Boolean canInit = validate();
    if (canInit) { 
	setup();
	isInitialized = true;
	};
  }

  public InputStream getInputStream(DavResource resource) {
    if (null == resource) return null;
    try {
      return sardine.get(host + resource.getPath());
    }
    catch (Exception e) {
      log.error("Failed to retrieve resource: " + resource.getName());
      log.error(e.getMessage(), e);
    }
    return null;
  }

  public void putStream(InputStream input, String name, String mimetype) {
    if (null == input || null == name || null == mimetype) return;
    try {
      sardine.put(host + outputFolder + name, input, mimetype);
    }
    catch (Exception e) {
      log.error("Failed to write to webdav: " + name);
      log.error(e.getMessage(), e);
    }
  }

  public List<DavResource> getResources() {
    return resources;
  }

  // validate if the setup can happen;
  private Boolean validate() {
//    if (null == host || host.length() == 0) throw new RuntimeException("Host url is not set.");
//    if (null == url || url.length() == 0) throw new RuntimeException("Webdav url is not set.");
    logFields();
    Boolean canInit = true;
    if (null == host || host.length() == 0) {
	canInit = false; };
    if (null == url  || url.length() == 0) {canInit = false;};
    if (null == password || password.length() == 0) {canInit = false;};
    if (null == user || user.length() == 0) {canInit = false;};
    return canInit;
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
