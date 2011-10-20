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
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

  @Autowired(required = true)
  private Namespace namespace;


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
    wkdTransformer.transform(davReader.getInputStream(resource), outputStream, inputName);
    log.info(inputName+ ": Transformation finished");
    return baos;

    /*writeToWebdav(outputFile, outputName);
    log.info("Written to webdav, writing to Virtuoso..");
    writeToVirtuoso(outputFile, outputName);*/
  }

  private ByteArrayOutputStream transform(String filename) {
    if (filename.length() <= 0) return null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    File inputFile = new File(filename);
    //String outputName = inputFile.getName().replaceAll("(?i).xml", ".rdf");

    log.info(new StringBuilder().append("processing: ").append(inputFile.getName()).toString());

    //File outputFile = new File(rdfFolder + outputName);
    try {
      InputStream inputStream = new FileInputStream(inputFile);
      StreamResult outputStream = new StreamResult(baos);
      log.info(filename + ": Transformation started");
      wkdTransformer.transform(inputStream, outputStream, filename);
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
  private void writeToFile(ByteArrayOutputStream baos, FileOutputStream fos){
	try{
	baos.writeTo(fos);
	} catch(Exception e){
		log.error(e.getMessage(),e);
	}
  }
  private void transformToFile(String file){
	if(file.length() == 0){
     		while (davReader.hasNext()) {
        		DavResource resource = davReader.getNextMatch();
			//log.info("Hier geraak ik");
			//if (resource == null){log.info("Resource is null");}
			if (resource != null && !(new File(rdfFolder + resource.getName().replaceAll("(?i).xml",".rdf"))).exists()) {
				String fileName = resource.getName().replaceAll("(?i).xml", ".rdf");
				try{
				String graphName = namespace.getBaseURI() + fileName;
				File graphFile = new File(rdfFolder + fileName.replaceAll("(?i).rdf",".graph"));
      				FileWriter fw = new FileWriter(graphFile,true);
      				fw.write(graphName);
      				fw.close();
				FileOutputStream outputStream = new FileOutputStream(new File(rdfFolder + fileName));
				log.info(fileName + ": Writing to file started");
				writeToFile(transform(resource),outputStream);
				log.info(fileName + ": Writing of file finished");
				} catch (Exception e){}
			}
      		}
	}
	else{	
		if(file.endsWith(".xml")){
			transformToFileFromFile(file, "");
		}
		else{
			File dir = new File (file);
			//FilenameFilter select = new FileListFilter("xml");
			File[] files = dir.listFiles();
			loadDir(files, "");		
		}
	}
  }
  private void loadDir(File [] files, String outputPath){
	for(int i = 0; i<files.length;i++){
		if(files[i].getName().endsWith(".xml")){		
			transformToFileFromFile(files[i].getPath(),outputPath);
		}
		else if(files[i].isDirectory()){
			(new File(rdfFolder + outputPath + files[i].getName())).mkdir();
			loadDir(files[i].listFiles(), outputPath + files[i].getName() + "/");
		}
	}	
  }
  private void transformToVirtuoso(String file){
	if(file.length() == 0){
	     	while (davReader.hasNext()) {
        		DavResource resource = davReader.getNextMatch();
			if (resource != null && !(new File(rdfFolder + resource.getName().replaceAll("(?i).xml",".rdf"))).exists()) {
				String fileName = resource.getName().replaceAll("(?i).xml", ".rdf");
				try{
				new FileOutputStream(new File(rdfFolder + fileName));
				log.info(fileName + ": Writing to virtuoso");
				writeToVirtuoso(transform(resource),fileName);
				log.info(fileName + ": Writing in virtuoso finished");
				} catch (FileNotFoundException e){}
			}
      		}

	}
	else{
		if(file.endsWith(".xml")){
			transformToVirtuosoFromFile(file);
		}
		else{
			File dir = new File (file);
			FilenameFilter select = new FileListFilter("xml");
			File[] files = dir.listFiles(select);
			for(int i = 0; i<files.length;i++){
				transformToVirtuosoFromFile(files[i].getPath());
			}		
		}
	}
  }
  private void transformToWebDav(String file){
	if(file.length() == 0){
     		while (davReader.hasNext()) {
        		DavResource resource = davReader.getNextMatch();
			if (resource != null && !(new File(rdfFolder + resource.getName().replaceAll("(?i).xml",".rdf"))).exists()) {
				String fileName = resource.getName().replaceAll("(?i).xml", ".rdf");
				try{
				new FileOutputStream(new File(rdfFolder + fileName));
				} catch (FileNotFoundException e){}
				log.info(fileName + ": Writing to webdav");
				writeToWebdav(transform(resource),fileName);
				log.info(fileName + ": Writing in webdav finished");
			}
      		}
	}
	else{
	
		if(file.endsWith(".xml")){
			transformToWebdavFromFile(file);
		}
		else{
			File dir = new File (file);
			FilenameFilter select = new FileListFilter("xml");
			File[] files = dir.listFiles(select);
			for(int i = 0; i<files.length;i++){
				transformToWebdavFromFile(files[i].getPath());
			}		
		}
	}
  }
  private void transformToFileAndVirtuoso(String file){
	if(file.length() == 0){
     		while (davReader.hasNext()) {
        		DavResource resource = davReader.getNextMatch();
			if (resource != null && !(new File(rdfFolder + resource.getName().replaceAll("(?i).xml",".rdf"))).exists()) {
				String fileName = resource.getName().replaceAll("(?i).xml",".rdf");
				try{
				String graphName = namespace.getBaseURI() + fileName;
				File graphFile = new File(rdfFolder + fileName.replaceAll("(?i).rdf",".graph"));
      				FileWriter fw = new FileWriter(graphFile,true);
      				fw.write(graphName);
      				fw.close();
				FileOutputStream outputStream = new FileOutputStream(new File(rdfFolder + fileName));
				ByteArrayOutputStream baos = transform(resource);
				log.info(fileName + ": Writing to file started");
				writeToFile(baos, outputStream);
				log.info(fileName + ": Writing of file finished");
				log.info(fileName + ": Writing to virtuoso");
				writeToVirtuoso(baos,fileName);
				log.info(fileName + ": Writing in virtuoso finised");
				} catch (Exception e){}

			}
      		}
	}
	else{	
		if(file.endsWith(".xml")){
			transformToFileFromFile(file, "");
			transformToVirtuosoFromFile(file);
		}
		else{
			File dir = new File (file);
			File[] files = dir.listFiles();
			loadDir(files, "");
			for(int i = 0; i<files.length;i++){
			//	transformToFileFromFile(files[i].getPath(), null);
				transformToVirtuosoFromFile(files[i].getPath());
			}		
		}
	}
  }
  private void transformToWebDavAndVirtuoso(String file){
	if(file.length() == 0){
     		while (davReader.hasNext()) {
        		DavResource resource = davReader.getNextMatch();
			if (resource != null && !(new File(rdfFolder + resource.getName().replaceAll("(?i).xml",".rdf"))).exists()) {
				String fileName = resource.getName().replaceAll("(?i).xml",".rdf");
				try{
				new FileOutputStream(new File(rdfFolder + fileName));
				} catch (FileNotFoundException e){}
				ByteArrayOutputStream baos = transform(resource);
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
		if(file.endsWith(".xml")){
			transformToWebdavFromFile(file);
			transformToVirtuosoFromFile(file);
		}
		else{
			File dir = new File (file);
			FilenameFilter select = new FileListFilter("xml");
			File[] files = dir.listFiles(select);
			for(int i = 0; i<files.length;i++){
				transformToWebdavFromFile(files[i].getPath());
				transformToVirtuosoFromFile(files[i].getPath());
			}		
		}
	}
  }
  private void transformToFileFromFile(String file,String outputPath){
	String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
	if(!(new File(rdfFolder + outputPath + fileName).exists())){
	try{	
	FileOutputStream outputStream = new FileOutputStream(new File(rdfFolder + outputPath + fileName));
	String graphName = namespace.getBaseURI() + fileName;
	log.info(fileName +": Writing to file started");	
	writeToFile(transform(file),outputStream);
	log.info(fileName + ": Writing of file finished");
	File graphFile = new File(rdfFolder + outputPath + fileName.replaceAll("(?i).rdf",".graph"));
      	FileWriter fw = new FileWriter(graphFile,true);
      	fw.write(graphName);
      	fw.close();
	} catch (Exception e){}
	}
  }
  private void transformToVirtuosoFromFile(String file){
	String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
	if(!(new File(rdfFolder + fileName).exists())){
	try{	
	FileOutputStream outputStream = new FileOutputStream(new File(rdfFolder + fileName));
	log.info(fileName + ": Writing to virtuoso");
	writeToVirtuoso(transform(file),fileName);
	log.info(fileName + ": Writing in virtuoso finished");
	} catch (Exception e){}
	}

  }
  private void transformToWebdavFromFile(String file){
	String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
	if(!(new File(rdfFolder + fileName).exists())){
	try{	
	FileOutputStream outputStream = new FileOutputStream(new File(rdfFolder + fileName));
	log.info(fileName + ": Writing to webdav");
	writeToWebdav(transform(file),file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf"));
	log.info(fileName + ": Writing in webdav finished");
	} catch (Exception e){}
	}
  }
}
