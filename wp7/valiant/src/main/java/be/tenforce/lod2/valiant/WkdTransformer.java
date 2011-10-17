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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import net.sf.saxon.Controller;
import net.sf.saxon.event.MessageEmitter;

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

  public void transform(InputStream input, StreamResult output, String fileName) {
    if (null == input || null == output || null == transformer) return;

    FileWriter fw;
    SAXSource inputSource = new SAXSource(xmlReader, new InputSource(input));
    try {
      	    fw = new FileWriter(new File("/home/jand/valiant/log/valiant.log"),true);
	MessageEmitter emitter = new MessageEmitter();
	((Controller)transformer).setMessageEmitter(emitter);
	((MessageEmitter)((Controller)transformer).getMessageEmitter()).setWriter(fw);	
        if (emitter==null){log.info("Emitter is null");}
            transformer.transform(inputSource, output);
	    fw.close();
    }
    catch (TransformerException e) {
	//log.error(fileName.substring(fileName.lastIndexOf('/') + 1));  
        log.error(fileName);
	log.info("Transform failed: " + e.getMessage(), e);
    }
    catch (IOException e){
	log.error("Logfile not found.");
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
