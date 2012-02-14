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
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

@Component("valiant")
public class Valiant {

    private static final Logger log = Logger.getLogger(Valiant.class);

    // root of the RDF files containing the result of the transformation
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

    @Value("#{properties.haltOnFileError}")
        private boolean haltOnFileError;

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

    public void execute(String[] args) throws Exception {
        String fileToTransForm = "";
        if (null != args && args.length > 0) {
            System.err.println("Use the commandline file as input");
            fileToTransForm = args[0];
        } else if (! inputfile.equals(""))  {
            System.err.println("Use the inputfile configuration parameter as input");
            fileToTransForm = inputfile;
        } else {
            System.err.println("Use webdav as input");
        };

        if(implementationMode.equals("saxon")){System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");}
        else if(implementationMode.equals("xalan")){System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");}
        if (mode==null) {
            log.error("Mode needs to be selected");
            throw new RuntimeException("No transformation engine selected");
        };
        if(mode.equals("f")){
            transformToFile(fileToTransForm);
        }
        else if(mode.equals("v")){
	    if (!virtuosoFactory.isInitialized) {throw new RuntimeException("Writing to virtuoso impossible due to incomplete configuration"); };
            transformToVirtuoso(fileToTransForm);
        }
        else if(mode.equals("w")){
	    if (!davWriter.isInitialized) {throw new RuntimeException("Writing to WebDav impossible due to incomplete configuration"); };
            transformToWebDav(fileToTransForm);
        }
        else if(mode.equals("fv")){
	    if (!virtuosoFactory.isInitialized) {throw new RuntimeException("Writing to virtuoso impossible due to incomplete configuration"); };
            transformToFileAndVirtuoso(fileToTransForm);
        }
        else if(mode.equals("wv")){
	    if (!virtuosoFactory.isInitialized) {throw new RuntimeException("Writing to virtuoso impossible due to incomplete configuration"); };
	    if (!davWriter.isInitialized) {throw new RuntimeException("Writing to WebDav impossible due to incomplete configuration"); };
            transformToWebDavAndVirtuoso(fileToTransForm);
        }
    }
    // String outputName = inputName.replaceAll("(?i).xml", ".rdf"); //(?i) will ignore the case

    // argument = input
    // output filename is calculated from the input
    private ByteArrayOutputStream transform(DavResource resource) {
        String inputName = resource.getName();
        log.info(new StringBuilder().append("processing: ").append(inputName).append(" (").append(davReader.pos()).append("/").append(davReader.size()).append(")").toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult outputStream = new StreamResult(baos);
        log.info(inputName+ ": Transformation started");
        wkdTransformer.transform(davReader.getInputStream(resource), outputStream, inputName);
        log.info(inputName+ ": Transformation finished");
        return baos;
    }

    // argument = input
    // output filename is calculated from the input
    private ByteArrayOutputStream transform(String filename) {
        if (filename.length() <= 0) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File inputFile = new File(filename);

        log.info(new StringBuilder().append("processing: ").append(inputFile.getName()).toString());

        try {
            InputStream inputStream = new FileInputStream(inputFile);
            StreamResult outputStream = new StreamResult(baos);
            log.info(filename + ": Transformation started");
            wkdTransformer.transform(inputStream, outputStream, filename);
            inputStream.close();
            log.info(filename + ": Transformation finished");
            return baos;
        }
        catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            if (haltOnFileError) { throw new RuntimeException(e.getMessage(), e); } ;
            return null;
        }
        catch (IOException ioe){
            log.error(ioe.getMessage(),ioe);
            if (haltOnFileError) { throw new RuntimeException(ioe.getMessage(),ioe); };
            return null;
        }
    }

    // argument1 = input
    // argument2 = outputwriter
    private void transformWriter(String filename, Writer w) {
        if (filename.length() > 0) {
        File inputFile = new File(filename);

        log.info(new StringBuilder().append("processing: ").append(inputFile.getName()).toString());

        try {
            InputStream inputStream = new FileInputStream(inputFile);
            StreamResult outputStream = new StreamResult(w);
            log.info(filename + ": Transformation started");
            wkdTransformer.transform(inputStream, outputStream, filename);
            inputStream.close();
            log.info(filename + ": Transformation finished");
        }
        catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            if (haltOnFileError) { throw new RuntimeException(e.getMessage(), e); } ;
        }
        catch (IOException ioe){
            log.error(ioe.getMessage(),ioe);
            if (haltOnFileError) { throw new RuntimeException(ioe.getMessage(),ioe); };
        }
	}
    }

    private void writeToWebdav(OutputStream outputStream, String outputName) {
        InputStream rdf = new ByteArrayInputStream(((ByteArrayOutputStream)outputStream).toByteArray());
        davWriter.putStream(outputName, rdf);
    }

    private void writeToVirtuoso(ByteArrayOutputStream outputStream, String outputName) {
        if (loadInVirtuoso) virtuosoFactory.add(outputStream, outputName);
    }

    private void writeToFile(ByteArrayOutputStream baos, FileOutputStream fos) throws Exception {
        try{
            baos.writeTo(fos);
        } catch(Exception e){
            log.error(e.getMessage(),e);
            if (haltOnFileError) {
                throw new RuntimeException("error while writing a file", e);
            };
        }
    }

    private FileOutputStream createTargetFile(String folder, String filename) throws Exception {
        try{
            FileOutputStream s = new FileOutputStream(new File(folder + filename));
            return s;
        } catch (FileNotFoundException e){
            log.error(e.getMessage());
            if (haltOnFileError) {
                throw new RuntimeException("error while opening file " + folder + filename, e);
            };
            return null;
        }

    }

    private Writer createTargetFileWriter(String folder, String filename) throws Exception {
        try{
	    Writer s = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(folder + filename)), "UTF8"));
            return s;
        } catch (FileNotFoundException e){
            log.error(e.getMessage());
            if (haltOnFileError) {
                throw new RuntimeException("error while opening file " + folder + filename, e);
            };
            return null;
        }

    }



    private void transformToFile(String file) throws Exception {
        if(file.length() == 0){
	    if (!davReader.isInitialized) {throw new RuntimeException("Reading from WebDav impossible due to incomplete configuration"); };
            while (davReader.hasNext()) {
                DavResource resource = davReader.getNextMatch();
                if (resource != null && !(new File(rdfFolder + resource.getName().replaceAll("(?i).xml",".rdf"))).exists()) {
                    String fileName = resource.getName().replaceAll("(?i).xml", ".rdf");

                    String graphName = namespace.getBaseURI() + fileName;
                    File graphFile = new File(rdfFolder + fileName + ".graph");
                    FileWriter fw = new FileWriter(graphFile,true);
                    fw.write(graphName);
                    fw.close();
                    FileOutputStream outputStream = createTargetFile(rdfFolder,fileName);
                    log.info(fileName + ": Writing to file started");
                    writeToFile(transform(resource),outputStream);
                    log.info(fileName + ": Writing of file finished");

                }
            }
        }
        else{	
            if(file.endsWith(".xml")){
                transformToFileFromFileWriter(file, "");
            }
            else {
                // if the file does not end with .xml consider it a directory
                File dir = new File (file);
                File[] files = dir.listFiles();
                loadDir(files, "");		
            }
        }
    }
    private void loadDir(File [] files, String outputPath) throws Exception {
        for(int i = 0; i<files.length;i++){
            if(files[i].getName().endsWith(".xml")){		
                transformToFileFromFileWriter(files[i].getPath(),outputPath);
            }
            else if(files[i].isDirectory()){
		System.out.println("create dir: " + rdfFolder + outputPath + files[i].getName());
                (new File(rdfFolder + outputPath + files[i].getName())).mkdir();
                loadDir(files[i].listFiles(), outputPath + files[i].getName() + "/");
            }
        }	
    }
    private void transformToVirtuoso(String file) throws Exception {
        if(file.length() == 0){
	    if (!davReader.isInitialized) {throw new RuntimeException("Reading from WebDav impossible due to incomplete configuration"); };
            while (davReader.hasNext()) {
                DavResource resource = davReader.getNextMatch();
                if (resource != null && !(new File(rdfFolder + resource.getName().replaceAll("(?i).xml",".rdf"))).exists()) {
                    String fileName = resource.getName().replaceAll("(?i).xml", ".rdf");
                    log.info(fileName + ": Writing to virtuoso");
                    writeToVirtuoso(transform(resource),fileName);
                    log.info(fileName + ": Writing in virtuoso finished");
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
    private void transformToWebDav(String file) throws Exception {
        if(file.length() == 0){
	    if (!davReader.isInitialized) {throw new RuntimeException("Reading from WebDav impossible due to incomplete configuration"); };
            while (davReader.hasNext()) {
                DavResource resource = davReader.getNextMatch();
                if (resource != null && !(new File(rdfFolder + resource.getName().replaceAll("(?i).xml",".rdf"))).exists()) {
                    String fileName = resource.getName().replaceAll("(?i).xml", ".rdf");
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
    private void transformToFileAndVirtuoso(String file) throws Exception {
        if(file.length() == 0){
	    if (!davReader.isInitialized) {throw new RuntimeException("Reading from WebDav impossible due to incomplete configuration"); };
            while (davReader.hasNext()) {
                DavResource resource = davReader.getNextMatch();
                if (resource != null && !(new File(rdfFolder + resource.getName().replaceAll("(?i).xml",".rdf"))).exists()) {
                    String fileName = resource.getName().replaceAll("(?i).xml",".rdf");
                    String graphName = namespace.getBaseURI() + fileName;
                    File graphFile = new File(rdfFolder + fileName + ".graph");
                    FileWriter fw = new FileWriter(graphFile,true);
                    fw.write(graphName);
                    fw.close();
                    FileOutputStream outputStream = createTargetFile(rdfFolder,fileName);
                    ByteArrayOutputStream baos = this.transform(resource);
                    log.info(fileName + ": Writing to file started");
                    writeToFile(baos, outputStream);
                    log.info(fileName + ": Writing of file finished");
                    log.info(fileName + ": Writing to virtuoso");
                    writeToVirtuoso(baos,fileName);
                    log.info(fileName + ": Writing in virtuoso finised");

                }
            }
        }
        else{	
            if(file.endsWith(".xml")){
                toVirtuosoFromTransform(file, transformToFileFromFile2(file, ""));
            } else {
                // in general is it better to make this a command line arg.
                // in unix world shell programming does this excellent
                File dir = new File (file);
                File[] files = dir.listFiles();
                transformToFileAndVirtuoso_list(files, "");
            }

        }
    }

    private void transformToFileAndVirtuoso_list(File [] files, String outputPath) throws Exception {
        for(int i = 0; i<files.length;i++){
            if(files[i].getName().endsWith(".xml")){		
                String f = files[i].getPath();
                String fileName = f.substring(f.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
                if(!(new File(rdfFolder + outputPath + fileName).exists())){
                    toVirtuosoFromTransform(f, transformToFileFromFile2(f, outputPath));
                }
            }
            else if(files[i].isDirectory()){
                (new File(rdfFolder + outputPath + files[i].getName())).mkdir();
                transformToFileAndVirtuoso_list(files[i].listFiles(), outputPath + files[i].getName() + "/");
            }
        }	
    }

    private void transformToWebDavAndVirtuoso(String file) throws Exception {
        if(file.length() == 0){
	    if (!davReader.isInitialized) {throw new RuntimeException("Reading from WebDav impossible due to incomplete configuration"); };
            while (davReader.hasNext()) {
                DavResource resource = davReader.getNextMatch();
                if (resource != null && !(new File(rdfFolder + resource.getName().replaceAll("(?i).xml",".rdf"))).exists()) {
                    String fileName = resource.getName().replaceAll("(?i).xml",".rdf");
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

    private void transformToFileFromFile(String file, String outputPath) throws Exception {
        String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
        if(!(new File(rdfFolder + outputPath + fileName).exists())){
            FileOutputStream outputStream = new FileOutputStream(new File(rdfFolder + outputPath + fileName));
            String graphName = namespace.getBaseURI() + fileName;
            log.info(fileName +": Writing to file started");
            writeToFile(transform(file),outputStream);
            outputStream.close();
            log.info(fileName + ": Writing of file finished");
            File graphFile = new File(rdfFolder + outputPath + fileName + ".graph");
            FileWriter fw = new FileWriter(graphFile,true);
            fw.write(graphName);
            fw.close();
        }
    }

    private void transformToFileFromFileWriter(String file, String outputPath) throws Exception {
        String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
        if(!(new File(rdfFolder + outputPath + fileName).exists())){
            Writer w = createTargetFileWriter(rdfFolder + outputPath, fileName);
            String graphName = namespace.getBaseURI() + fileName;
            log.info(fileName +": Writing to file started");
            transformWriter(file, w);
            w.close();
            log.info(fileName + ": Writing of file finished");
            File graphFile = new File(rdfFolder + outputPath + fileName + ".graph");
            FileWriter fw = new FileWriter(graphFile,true);
            fw.write(graphName);
            fw.close();
        }
    }


    private ByteArrayOutputStream transformToFileFromFile2(String file, String outputPath) throws Exception {
        String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
        if(!(new File(rdfFolder + outputPath + fileName).exists())){
            FileOutputStream outputStream = new FileOutputStream(new File(rdfFolder + outputPath + fileName));
            String graphName = namespace.getBaseURI() + fileName;
            log.info(fileName +": Writing to file started");
            ByteArrayOutputStream s = transform(file);
            writeToFile(s,outputStream);
            log.info(fileName + ": Writing of file finished");
            outputStream.close();
            File graphFile = new File(rdfFolder + outputPath + fileName + ".graph");
            FileWriter fw = new FileWriter(graphFile,true);
            fw.write(graphName);
            fw.close();
            return s;
        } else { return null; }
    }

    // transform the input file & write the result to virtuso
    // if the file already exists then the transformation has already happened.
    private void transformToVirtuosoFromFile(String file) throws Exception {
        String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
        if(!(new File(rdfFolder + fileName).exists())){
            log.info(fileName + ": Writing to virtuoso");
            writeToVirtuoso(transform(file),fileName);
            log.info(fileName + ": Writing in virtuoso finished");
        }

    }
    // accept the transformed result from input file & write the result to virtuoso
    // if the file already exists then the transformation has already happened.
    private void toVirtuosoFromTransform(String file, ByteArrayOutputStream fileTransformed) throws Exception {
        String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
//        if(!(new File(rdfFolder + fileName).exists())){
            log.info(fileName + ": Writing to virtuoso");
            writeToVirtuoso(fileTransformed,fileName);
            log.info(fileName + ": Writing in virtuoso finished");
//        }

    }

    // transform the input file & write the result to webdav 
    // if the file already exists then the transformation has already happened.
    private void transformToWebdavFromFile(String file) throws Exception {
        String fileName = file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf");
        if(!(new File(rdfFolder + fileName).exists())){
            log.info(fileName + ": Writing to webdav");
            writeToWebdav(transform(file),file.substring(file.lastIndexOf('/') + 1).replaceAll("(?i).xml",".rdf"));
            log.info(fileName + ": Writing in webdav finished");
        }
    }
}
