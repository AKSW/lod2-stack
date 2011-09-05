package be.tenforce.lod2.valiant;

import com.googlecode.sardine.DavResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.log4j.Logger;
import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.regex.Pattern;

@Service
public class DavReader {
  private static final Logger log = Logger.getLogger(DavReader.class);

  @Value("#{properties.regex}")
  private String regex;

  @Value("#{properties.max}")
  int max;

  @Autowired(required = true)
  private DavConnector davConnector;

  private Pattern pattern;
  private int next = 0;

  @PostConstruct
  private void initialize() {
    if (null != regex && regex.length() > 0) {
      pattern = Pattern.compile(regex);
      log.info("regex: " + regex);
    }
    if (max < 0) max = Integer.MAX_VALUE;
    log.info("# found resources: " + size());
  }

  public DavResource getNext() {
    DavResource resource = null;
    if (hasNext()) {
      resource = davConnector.getResources().get(next);
      next++;
    }
    return resource;
  }

  public boolean hasNext() {
    return next < davConnector.getResources().size();
  }

  public DavResource getNextMatch() {
    while (hasNext()) {
      DavResource resource = getNext();
      if (isMatch(resource.getName())) return resource;
    }
    return null;
  }

  public void reset() {
    next = 0;
  }

  public int size() {
    return davConnector.getResources().size();
  }

  public int pos() {
    return next;
  }

  public InputStream getInputStream(DavResource resource) {
    return davConnector.getInputStream(resource);
  }

  private boolean isMatch(String filename) {
    return pattern.matcher(filename).matches();
  }
}
