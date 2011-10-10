package be.tenforce.lod2.valiant;

import be.tenforce.lod2.valiant.virtuoso.VirtuosoFactory;
import be.tenforce.lod2.valiant.webdav.DavReader;
import be.tenforce.lod2.valiant.webdav.DavWriter;
import com.googlecode.sardine.DavResource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Component("valiant")
public class Valiant {

  private static final Logger log = Logger.getLogger(Valiant.class);

  @Value("#{properties.rdfFolder}")
  private String rdfFolder;

  @Value("#{properties['virtuoso.load']}")
  private boolean loadInVirtuoso;

  @Value("#{properties.transformMode}")
  private String mode;

  @Value("#{properties.inputfile}")
  private String inputfile;

  @Value("#{properties.implementationMode}")
  private String implementationMode;

  @Autowired(required = true)
  private DavReader davReader;

  @Autowired(required = true)
  private DavWriter davWriter;

  @Autowired(required = true)
  private WkdTransformer wkdTransformer;

  @Autowired(required = true)
  private VirtuosoFactory virtuosoFactory;

  public void execute() {
    if(implementationMode.equals("saxon")){System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");}
    else if(implementationMode.equals("xalan")){System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");}
    if (null == mode) {
      log.error("Mode needs to be selected");
      return;
    }
    /*else {
      while (davReader.hasNext()) {
        DavResource resource = davReader.getNextMatch();
	if (resource != null) {
		transform(resource);
	}
      }
    }*/
    if(mode.equals("f")){
	transformToFile(inputfile);
    }
    else if(mode.equals("v")){
	transformToVirtuoso(inputfile);
    }
    else if(mode.equals("w")){
	transformToWebDav(inputfile);
    }
    else if(mode.equals("fv")){
	transformToFileAndVirtuoso(inputfile);
    }
    else if(mode.equals("wv")){
	transformToWebDavAndVirtuoso(inputfile);
    }
  }

  private ByteArrayOutputStream transform(DavResource resource) {
    String inputName = resource.getName();
   // String outputName = inputName.replaceAll("(?i).xml", ".rdf"); //(?i) will ignore the case

    log.info(new StringBuilder().append("processing: ").append(inputName).append(" (").append(davReader.pos()).append("/").append(davReader.size()).append(")").toString());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    StreamResult outputStream = new StreamResult(baos);
    /*File outputFile = new File(rdfFolder + outputName);
    StreamResult outputStream = new StreamResult(outputFile);*/
    log.info(inputName+ ": Transformation started");
    wkdTransformer.transform(davReader.getInputStream(resource), outputStream);
    log.info(inputName+ ": Transformation finished");
    return baos;

    /*writeToWebdav(outputFile, outputName);
    log.info("Written to webdav, writing to Virtuoso..");
    writeToVirtuoso(outputFile, outputName);*/
  }

  private ByteArrayOutputStream transform(String filename) {
    if (filename.length() <= 0) return null;
    log.info(filename);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    File inputFile = new File(filename);
    //String outputName = inputFile.getName().replaceAll("(?i).xml", ".rdf");

    log.info(new StringBuilder().append("processing: ").append(inputFile.getName()).toString());

    //File outputFile = new File(rdfFolder + outputName);
    try {
      InputStream inputStream = new FileInputStream(inputFile);
      StreamResult outputStream = new StreamResult(baos);
      log.info(filename + ": Transformation started");
      wkdTransformer.transform(inputStream, outputStream);
      log.info(filename + ": Transformation finished");
      //writeToVirtuoso(outputFile, outputName);
      return baos;
    }
    catch (FileNotFoundException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private void writeToWebdav(OutputStream outputStream, String outputName) {
   // try {
      InputStream rdf = new ByteArrayInputStream(((ByteArrayOutputStream)outputStream).toByteArray());
      davWriter.putStream(outputName, rdf);
    /*}
    catch (FileNotFoundException e) {
      log.error("Failed to write output: '" + e.getMessage(), e);
    }*/
  }

  private void writeToVirtuoso(ByteArrayOutputStream outputStream, String outputName) {
    if (loadInVirtuoso) virtuosoFactory.add(outputStream, outputName);
  }
  private void writeToFile(ByteArrayOutputStream baos, String fileName){
	try{
	File outputFile = new File(rdfFolder + fileName);
    	FileOutputStream outputStream = new FileOutputStream(outputFile);
	baos.writeTo(outputStream);
	} catch(Exception e){
		log.error(e.getMessage(),e);
	}
  }
  private void transformToFile(String file){
	if(file.length() == 0){
     		while (davReader.hasNext()) {
        		DavResource resource = davReader.getNextMatch();
			if (resource != null) {
				String fileName = resource.getName().replaceAll("(?i).xml", ".rdf");
				log.info(fileName + ": Writing to file started");
				writeToFile(transform(resource),fileName);
				log.info(fileName + ": Writing of file finished");
			}
      		}
	}
	else{	
		String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
		log.info(fileName +": Writing to file started");	
		writeToFile(transform(file),fileName);
		log.info(fileName + ": Writing of file finished");
	}
  }
  private void transformToVirtuoso(String file){
	if(file.length() == 0){
	     	while (davReader.hasNext()) {
        		DavResource resource = davReader.getNextMatch();
			if (resource != null) {
				String fileName = resource.getName().replaceAll("(?i).xml", ".rdf");
				log.info(fileName + ": Writing to virtuoso");
				writeToVirtuoso(transform(resource),fileName);
				log.info(fileName + ": Writing in virtuoso finished");
			}
      		}

	}
	else{
		String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
		log.info(fileName + ": Writing to virtuoso");
		writeToVirtuoso(transform(file),fileName);
		log.info(fileName + ": Writing in virtuoso finished");
	}
  }
  private void transformToWebDav(String file){
	if(file.length() == 0){
     		while (davReader.hasNext()) {
        		DavResource resource = davReader.getNextMatch();
			if (resource != null) {
				String fileName = resource.getName().replaceAll("(?i).xml", ".rdf");
				log.info(fileName + ": Writing to webdav");
				writeToWebdav(transform(resource),fileName);
				log.info(fileName + ": Writing in webdav finished");
			}
      		}
	}
	else{
		String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
		log.info(fileName + ": Writing to webdav");
		writeToWebdav(transform(file),file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf"));
		log.info(fileName + ": Writing in webdav finished");
	}
  }
  private void transformToFileAndVirtuoso(String file){
	if(file.length() == 0){
     		while (davReader.hasNext()) {
        		DavResource resource = davReader.getNextMatch();
			if (resource != null) {
				String fileName = resource.getName().replaceAll("(?i).xml",".rdf");
				ByteArrayOutputStream baos = transform(resource);
				log.info(fileName + ": Writing to file started");
				writeToFile(baos,fileName);
				log.info(fileName + ": Writing of file finished");
				log.info(fileName + ": Writing to virtuoso");
				writeToVirtuoso(baos,fileName);
				log.info(fileName + ": Writing in virtuoso finised");
			}
      		}
	}
	else{	
		String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
		ByteArrayOutputStream baos = transform(file);
		log.info(fileName + ": Writing to file started");
		writeToFile(baos,fileName);
		log.info(fileName + ": Writing of file finished");
		log.info(fileName + ": Writing to virtuoso:");
		writeToVirtuoso(baos,fileName);
		log.info(fileName + ": Writing in virtuoso finished");
	}
  }
  private void transformToWebDavAndVirtuoso(String file){
	if(file.length() == 0){
     		while (davReader.hasNext()) {
        		DavResource resource = davReader.getNextMatch();
			if (resource != null) {
				ByteArrayOutputStream baos = transform(resource);
				String fileName = resource.getName().replaceAll("(?i).xml",".rdf");
				log.info(fileName + ": Writing to webdav");
				writeToWebdav(baos,fileName);
				log.info(fileName + ": Writing in webdav finished");
				log.info(fileName + ": Writing to virtuoso");
				writeToVirtuoso(baos,fileName);
				log.info(fileName + ": Writing in virtuoso finished");
			}
      		}
	}
	else{
		String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
		ByteArrayOutputStream baos = transform(file);
		log.info(fileName + ": Writing to webdav:");
		writeToWebdav(baos,fileName);
		log.info(fileName + ": writing in webdav finished");
		log.info(fileName + ": Writing to virtuoso");
		writeToVirtuoso(baos,fileName);
		log.info(fileName + ": Writing in virtuoso finished");

	}
  }
}
