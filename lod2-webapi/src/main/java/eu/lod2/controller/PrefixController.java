package eu.lod2.controller;

import eu.lod2.Graphs;
import eu.lod2.convertor.GraphsConverter;
import eu.lod2.PrefixManager;
import eu.lod2.WebApiList;
import eu.lod2.convertor.WebApiListConverter;
import eu.lod2.PrefixCC;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
public class PrefixController {
    @Autowired(required = true)
  private AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter;

  private PrefixManager prefixManager = new PrefixManager();

  @PostConstruct
  private void init() {
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(
        Arrays.asList(annotationMethodHandlerAdapter.getMessageConverters()));
    messageConverters.add(0, new GraphsConverter());
    messageConverters.add(1, new WebApiListConverter());

    annotationMethodHandlerAdapter.setMessageConverters(messageConverters
                                                            .toArray(new HttpMessageConverter<?>[messageConverters.size()]));

  }

  public void setAnnotationMethodHandlerAdapter(AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter) {
    this.annotationMethodHandlerAdapter = annotationMethodHandlerAdapter;
  }

//	 SELECT * FROM DB.DBA.SYS_XML_PERSISTENT_NS_DECL;
// NS_PREFIX
//VARCHAR 	NS_URL
//  DB.DBA.XML_SET_NS_DECL ('txn', 'http://lod.taxonconcept.org/ontology/txn.owl#', 2);
  @RequestMapping(value = "/prefix", method = RequestMethod.GET)
  public ResponseEntity<Graphs> getGraphs(HttpServletResponse response, 
		@RequestParam(value="abbrev", required=false) String abbrevS, 
		@RequestParam(value="uri",    required=false) String namespaces,
		@RequestParam(value="from",   required=false) Integer start
		)
	 throws Exception {
    
    WebApiList graphs;
 
    if (namespaces == null) {
    if (abbrevS == null) {
	    if (start == null) {
		graphs = prefixManager.getPrefixes();
	    } else {
		graphs = prefixManager.getPrefixes(start);
	    };
    } else {
	    if (start == null) {
		String[] abbrev = StringUtils.split(abbrevS, ",");
		graphs = prefixManager.getPrefixesLimitAbbrev(0, abbrev);
	    } else {
		String[] abbrev = StringUtils.split(abbrevS, ",");
		graphs = prefixManager.getPrefixesLimitAbbrev(start, abbrev);
	    };
   };
   } else {
   if (abbrevS == null) {
	    if (start == null) {
		String[] uris = StringUtils.split(namespaces, ",");
		
	    for (String u: uris) {
		System.out.println(u);
	    };
		graphs = prefixManager.getPrefixesLimitURI(0,uris);
	    } else {
		String[] uris = StringUtils.split(namespaces, ",");
		graphs = prefixManager.getPrefixesLimitURI(start, uris);
	    };
	
   } else {
	// error case
        throw new RuntimeException("The service does not support requests where both parameters uri and abbrev have a value.");
   };
 
	
   };
		

    Graphs something = new Graphs();
    something.setKind("graphs");
    something.setGraphs(graphs);
    return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
  };

	// add an abbreviation for a namespace.
        // indicate if it is a preferred combination
  @RequestMapping(value = "/add_prefix", method = RequestMethod.POST)
  public ResponseEntity<Graphs> getGraphs(HttpServletResponse response, 
		@RequestParam(value="abbrev",    required=true) String abbrev, 
		@RequestParam(value="uri",       required=true) String namespace,
		@RequestParam(value="preferred", required=false) Boolean preferred 
		)
	 throws Exception {

    WebApiList graphs;

    prefixManager.insertPrefix(abbrev, namespace, preferred);
    graphs = prefixManager.getPrefixes();
    Graphs something = new Graphs();
    something.setKind("graphs");
    something.setGraphs(graphs);
    return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
  };

/*
  // follow virtuoso like syntax
  // http://docs.openlinksw.com/virtuoso/SELECTSTMT.html  
  @RequestMapping(value = "/prefixregex", method = RequestMethod.GET)
  public ResponseEntity<Graphs> getGraphRegex(HttpServletResponse response, 
		@RequestParam(value="system", required=false) Boolean systemincluded, 
		@RequestParam("regex") String regex, 
		@RequestParam(value="from",   required=false) Integer start
		)
	 throws IOException {
   
    WebApiList graphs;
    if (systemincluded ==null || !systemincluded) {
	if (start == null) {
    		graphs = prefixManager.getNonSystemGraphsRegex(regex);
	} else {
    		graphs = prefixManager.getNonSystemGraphsRegex(regex, start);
	};
    } else {
	if (start == null) {
    		graphs = prefixManager.getAllGraphsRegex(regex);
	} else {
    		graphs = prefixManager.getAllGraphsRegex(regex,start);
	};
    };
    Graphs something = new Graphs();
    something.setKind("graphsregex");
    something.setGraphs(graphs);
    return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
  };

*/

  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
    return headers;
  }




  @RequestMapping(value = "/getError2", method = RequestMethod.GET)
  public void getError(HttpServletResponse response, @RequestParam("message") String message) throws Exception {
    throw new RuntimeException(message);
  }


  @RequestMapping(value = "/getMessage2", method = RequestMethod.GET)
  public void getMessage(HttpServletResponse response, @RequestParam("message") String message) throws Exception {
    ServletOutputStream outputStream = response.getOutputStream();
    try {
      outputStream.write(message.getBytes(Charset.forName("UTF-8")));
    }
    finally {
      IOUtils.closeQuietly(outputStream);
    }

  }


  @ExceptionHandler(Exception.class)
  public void handleException(Exception e, HttpServletResponse response) throws Exception {
    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error by PrefixController errorhandling: " + e.getMessage());
  }


}
