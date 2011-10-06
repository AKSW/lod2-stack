package be.tenforce.lod2.valiant;

import com.sun.org.apache.xerces.internal.util.XMLCatalogResolver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.annotation.PostConstruct;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class WkdTransformer {
  private static final Logger log = Logger.getLogger(WkdTransformer.class);

  @Value("#{properties.xslPath}")
  private String xsltUrl;

  @Value("#{properties.catalogUrl}")
  private String catalogUrl;

  private TransformerFactory transformerFactory;
  private Transformer transformer;
  private XMLCatalogResolver resolver;
  private XMLReader xmlReader;

  @PostConstruct
  private void initialize() {
    validate();
    setup();
  }

  public String getTest(){
	return "test";
  }

  public void transform(InputStream input, StreamResult output) {
    if (input == null){
    	log.info("InputStream is null");
    }
    if (output == null){
	log.info("StreamResult is null");
    }
    if (transformer == null){
	log.info("transformer is null");
    }
    if (null == input || null == output || null == transformer) return;
   	
    SAXSource inputSource = new SAXSource(xmlReader, new InputSource(input));
    try {
      transformer.transform(inputSource, output);
    }
    catch (TransformerException e) {
       log.error("Transform failed: " + e.getMessage(), e);
    }
    //log.info("Exiting transform method");
    finally {
      try {
        input.close();
      }
      catch (IOException ignored) {
      }

    }
  }

  private void setup() {
    transformerFactory = TransformerFactory.newInstance();
    loadTransformer();
    loadResolver();
  }

  private void loadResolver() {
    resolver = new XMLCatalogResolver(new String[]{catalogUrl});
    try {
      xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setEntityResolver(resolver);
    }
    catch (SAXException e) {
      log.error("Failed to create the xmlReader: " + e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private void loadTransformer() {
    try {
      transformer = transformerFactory.newTransformer(new StreamSource(new File(xsltUrl)));
    }
    catch (Exception e) {
      log.error("Failed to compile stylesheet: " + e.getMessage(), e);
    }
  }

  private void validate() {
    if (xsltUrl == null || xsltUrl.length() == 0) throw new RuntimeException("XslPath is not set");
    if (catalogUrl == null || catalogUrl.length() == 0) throw new RuntimeException("catalogUrl is not set");
    logFields();
  }

  private void logFields() {
    log.info("xslPath: " + xsltUrl);
    log.info("catalogUrl: " + catalogUrl);
  }
}
