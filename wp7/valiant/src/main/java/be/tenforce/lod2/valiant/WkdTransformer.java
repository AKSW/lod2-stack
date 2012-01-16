package be.tenforce.lod2.valiant;

import org.apache.xerces.util.XMLCatalogResolver;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.resolver.CatalogManager;
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
import javax.xml.transform.URIResolver;

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

    @Value("#{properties.catalogManager}")
        private String catalogManager;

    @Value("#{properties.logFolder}")
        private String logFolder;

    private TransformerFactory transformerFactory;
    private Transformer transformer;
    private XMLCatalogResolver resolver;
    private XMLReader xmlReader;

    @PostConstruct
        private void initialize() {
            try {
                validate();
                setup();
            } catch (Exception e) {
                System.err.println("Initalisation error has occurred.");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

    public void transform(InputStream input, StreamResult output, String fileName) {
        if (null == input || null == output || null == transformer) return;

        FileWriter fw;
        SAXSource inputSource = new SAXSource(xmlReader, new InputSource(input));
        try {
            transformer.setParameter("{}file-uri", fileName);
            fw = new FileWriter(new File(logFolder + "valiant.log"),true);
            ((MessageEmitter)((Controller)transformer).getMessageEmitter()).setWriter(fw);	
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
        CatalogManager manager = new CatalogManager(catalogManager);
        CatalogResolver uriResolver  = new CatalogResolver(manager);
        transformerFactory.setURIResolver(uriResolver);
        setResolverForXmlreader(uriResolver);
        loadTransformer();
    }

    private void setResolverForXmlreader(CatalogResolver resolver) {
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
            MessageEmitter emitter = new MessageEmitter();
            ((Controller)transformer).setMessageEmitter(emitter);

        }
        catch (Exception e) {
            log.error("Failed to compile stylesheet: " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    private void validate() {
        if (xsltUrl == null || xsltUrl.length() == 0) throw new RuntimeException("XslPath is not set");
        if (catalogUrl == null || catalogUrl.length() == 0) throw new RuntimeException("catalogUrl is not set");
        if (catalogManager == null || catalogManager.length() == 0) throw new RuntimeException("catalogManager is not set");
        logFields();
    }

    private void logFields() {
        log.info("xslPath: " + xsltUrl);
        log.info("catalogUrl: " + catalogUrl);
        log.info("catalogManager: " + catalogManager);
    }

    private void informObsoleteFields() {
        log.warn("The field catalogUrl is obsolete, replace it with a CatalogManager configuration.");
        log.warn("catalogUrl: " + catalogUrl);
        System.err.println("Warning: The field catalogUrl is obsolete, replace it with a CatalogManager configuration.");
    }

}
