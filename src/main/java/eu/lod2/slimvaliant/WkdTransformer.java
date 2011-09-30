package eu.lod2.slimvaliant;

import org.apache.xerces.util.XMLCatalogResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Johan.De-Smedt
 * Date: Aug 15, 2011
 * Time: 6:34:36 PM
  */

public class WkdTransformer {
    private TransformerFactory transformerFactory = null;
    private Transformer transformer = null;
    //private String xsltUrl;
    private InputStream toTransformStream = null;
    private String[] catalogUrl = new String[]{"//home/jand/Project/jan/src/example/catalog/catalog.xml"};
    private XMLCatalogResolver resolver = null;
    private XMLReader xmlReader = null;

    public WkdTransformer(StreamSource xslt) throws Exception {
        if (this.transformerFactory == null)
            this.transformerFactory = TransformerFactory.newInstance();
        //this.xsltUrl = xsltUrl;
        //File xslt = new File(xsltUrl);
        // boolean readable = xslt.canRead();
        // System.out.println("file is readable: " + readable);
        try {
            this.transformer = this.transformerFactory.newTransformer(xslt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to compile the stylesheet");
        }
        // this.catalogUrl;
        this.resolver = new XMLCatalogResolver(catalogUrl);
        try {
            this.xmlReader = XMLReaderFactory.createXMLReader();
            this.xmlReader.setEntityResolver(this.resolver);
        }
        catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
            throw new Exception("Failed to create the XmlReader");
        }
    }

    public void transform(InputStream input, StreamResult output) throws Exception {
        if (this.transformer == null) throw new Exception("Xslt transformer is not initialized.");
        SAXSource inputSource = null;
        inputSource = new SAXSource(this.xmlReader, new InputSource(input));
        try {
            this.transformer.transform(inputSource, output);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Transform Failed");
        } finally {
            input.close();
        }
    }
}
