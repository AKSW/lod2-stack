package be.tenforce.lod2.valiant;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class DavWriter {
  private static final Logger log = Logger.getLogger(DavWriter.class);

  @Autowired(required = true)
  DavConnector davConnector;

  private static final String MIMETYPE = "application/rdf+xml";

  public void putStream(String name, InputStream input) {
    davConnector.putStream(input, name, MIMETYPE);
  }
}
