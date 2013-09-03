package eu.lod2;


import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.*;
import org.apache.http.client.methods.*;

/*
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
*/
import org.apache.commons.lang3.StringUtils;
import org.apache.http.params.BasicHttpParams;

import java.util.Map;
import java.util.HashMap;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;

import java.net.URLEncoder;

/*
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Parser;
import org.xml.sax.ParserFactory;
import org.xml.sax.SAXException;
*/

import java.io.IOException;

public class PrefixCC {

  // here should come a proper media type for json
  public PrefixCC() {
  }

  public static Map<String,String> request_abbrev(String[] uris) throws Exception {

	
	Map<String,String> result = new HashMap();

        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.setRedirectStrategy(new DefaultRedirectStrategy() {                
		public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)  {
		    boolean isRedirect=false;
		    try {
			isRedirect = super.isRedirected(request, response, context);
		    } catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    if (!isRedirect) {
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 301 || responseCode == 302) {
			    return true;
			}
		    }
		    return isRedirect;
		}
		public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) {
			HttpGet get = null;
			try {

			    String newLocation = "" + response.getFirstHeader("Location");
			    newLocation = newLocation.substring(newLocation.indexOf("Location:")+10);
			    get = new HttpGet(newLocation + ".file.json");
		    	    get.addHeader("accept", "application/json");


			} catch (Exception ex) {
			    System.out.println(ex.getLocalizedMessage());
			}
			return get;
		    }
        });
		
		

        try {
	    

	    ResponseHandler<String> responseHandler = new BasicResponseHandler();

	    for (String uri: uris) {

		   if (uri != null) {
		   System.out.println(uri);

            	    String prefixurl = "http://prefix.cc/reverse?";
		    prefixurl = prefixurl + "uri=" +  URLEncoder.encode(uri, "UTF-8");

		    HttpGet httpget = new HttpGet(prefixurl);

		    httpget.addHeader("accept", "application/json");
	
		    String responseBody;

        	    try {
		    	responseBody = httpclient.execute(httpget, responseHandler);

//	            	System.out.println("respons: " + responseBody);
		    	Map<String,String> resultelement = parse_result(responseBody);
		    	result.putAll(resultelement);

        	    } catch (HttpResponseException e) {
	//		    System.out.println("Exception : " + e.getMessage() + e.getStatusCode());
			    int responseCode = e.getStatusCode();
			    if (responseCode != 404) {
				throw e;
			   };
		    }; 
		};

            };
	   
			
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

	return result;
    }

	    	
	// get the uri's for a list of abbreviations
  public static Map<String,String> request_uri(String[] abbrev) throws Exception {

	Map<String,String> result = new HashMap();

        HttpClient httpclient = new DefaultHttpClient();
        try {
	    
            String prefixurl = "http://prefix.cc/";

	    prefixurl = prefixurl + StringUtils.join(abbrev, ",");	
	    prefixurl = prefixurl + ".file.json";

            HttpGet httpget = new HttpGet(prefixurl);

	    httpget.addHeader("accept", "application/json");
 

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
    
	    result = parse_result(responseBody);
	    

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

	return result;
  } 

  private static Map<String,String> parse_result(String result) throws Exception {


    ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    Map<String,String> userData = mapper.readValue(result, Map.class);

    return userData;
   
  }}
