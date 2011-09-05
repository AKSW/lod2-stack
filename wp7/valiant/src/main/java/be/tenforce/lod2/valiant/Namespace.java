package be.tenforce.lod2.valiant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Namespace {

  @Value("#{properties.baseURI}")
  private String baseURI;

  @PostConstruct
  private void initialize() {
    if (null == baseURI || baseURI.length() == 0) throw new RuntimeException("BaseURI is not set");
  }

  public String getBaseURI() {
    return baseURI;
  }
}
