package be.tenforce.lod2.valiant;

import com.googlecode.sardine.DavResource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Component("valiant")
public class Valiant {

  private static final Logger log = Logger.getLogger(Valiant.class);

  @Value("#{properties.rdfFolder}")
  private String rdfFolder;

  @Autowired(required = true)
  private DavReader davReader;

  @Autowired(required = true)
  private DavWriter davWriter;

  @Autowired(required = true)
  private WkdTransformer transformer;

  public void execute(String[] args) {
    if (null != args && args.length > 0) {
      transform(args[0]);
    } else {
      while (davReader.hasNext()) {
        DavResource resource = davReader.getNextMatch();
        if (resource != null) transform(resource);
      }
    }
  }

  private void transform(DavResource resource) {
    String inputName = resource.getName();
    String outputName = inputName.replaceAll("(?i).xml", ".rdf"); //(?i) will ignore the case

    log.info(new StringBuilder().append("processing: ").append(inputName).append(" (").append(davReader.pos()).append("/").append(davReader.size()).append(")").toString());

    File outputFile = new File(rdfFolder + outputName);
    StreamResult outputStream = new StreamResult(outputFile);
    transformer.transform(davReader.getInputStream(resource), outputStream);

    write(outputFile, outputName);
  }

  private void transform(String filename) {
    if (filename.length() <= 0) return;
    File inputFile = new File(filename);
    String outputName = inputFile.getName().replaceAll("(?i).xml", ".rdf");

    log.info(new StringBuilder().append("processing: ").append(inputFile.getName()).toString());

    File outputFile = new File(rdfFolder + outputName);
    try {
      InputStream inputStream = new FileInputStream(inputFile);
      StreamResult outputStream = new StreamResult(outputFile);
      transformer.transform(inputStream, outputStream);
    }
    catch (FileNotFoundException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private void write(File outputFile, String outputName) {
    try {
      InputStream rdf = new FileInputStream(outputFile);
      davWriter.putStream(outputName, rdf);
    }
    catch (FileNotFoundException e) {
      log.error("Failed to write output: '" + e.getMessage(), e);
    }
  }
}
