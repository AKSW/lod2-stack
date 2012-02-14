package be.tenforce.lod2.valiant.webdav;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Service
public class DavWriter {
  private static final Logger log = Logger.getLogger(DavWriter.class);

  @Autowired(required = true)
  DavConnector davConnector;
  public Boolean isInitialized = false;

  private static final String MIMETYPE = "application/rdf+xml";

  @PostConstruct
  private void initialize() {
    isInitialized = davConnector.isInitialized;
  };

  public void putStream(String name, InputStream input) {
    davConnector.putStream(input, name, MIMETYPE);
  }
}
