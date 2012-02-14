package be.tenforce.lod2.valiant;

import org.apache.log4j.Logger;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

public class ValiantXMLErrorHandler 

extends DefaultHandler
{

    private static final Logger log = Logger.getLogger(ValiantXMLErrorHandler.class);

    public ValiantXMLErrorHandler() {
	super();
	};

    public void error(SAXParseException e) throws SAXException {
            log.error("XML parser error: " + e.getMessage(), e);
	    super.error(e); 
	};
   
};
