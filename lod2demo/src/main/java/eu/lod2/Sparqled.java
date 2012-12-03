package eu.lod2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

public class Sparqled extends CustomComponent {
	
	private LOD2DemoState state;
	private VerticalLayout sparqledTab;
	
	public Sparqled(LOD2DemoState state){
		this.state = state;
		
		sparqledTab = new VerticalLayout();
		
		if (state.getCurrentGraph() != null && !state.getCurrentGraph().equals(""))
			calculateSummaryGraph();
		
		Embedded browser = new Embedded();
		try {
			URL url = new URL(state.getHostName() + "/sparqled");
			browser = new Embedded("", new ExternalResource(url));
			browser.setType(Embedded.TYPE_BROWSER);
			browser.setSizeFull();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		sparqledTab.addComponent(browser);
		
		// The composition root MUST be set
		sparqledTab.setSizeFull();
		setCompositionRoot(sparqledTab);
	}
	
	private void processResponse(String method, HttpResponse response){
		if (!(response.getStatusLine().getStatusCode() == 200)){
			StringBuilder builder = new StringBuilder("Sparqled ");
			builder.append(method);
			builder.append(" was not successful.\n");
			builder.append("Code: ").append(response.getStatusLine().getStatusCode()).append("\n");
			builder.append("Reason: ").append(response.getStatusLine().getReasonPhrase());
			System.err.println(builder.toString());
			sparqledTab.getWindow().showNotification(builder.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void calculateSummaryGraph(){
		String curGraph = state.getCurrentGraph();
		String summaryGraph = "http://sindice.com/analytics/lod2-summary";
		
		String deleteUrl = state.getHostName() + "/sparqled/rest/summaries/delete";
		String createUrl = state.getHostName() + "/sparqled/rest/summaries/create";
		String setUrl = state.getHostName() + "/sparqled/AssistedSparqlEditorServlet";
		
		final String errorMessage = "There was a problem while calculating and setting the summary graph";
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpDelete delete = new HttpDelete(deleteUrl + "?graph=" + summaryGraph);
		HttpPost post = new HttpPost(createUrl);
		HttpPut put = new HttpPut(setUrl + "?dg=" + summaryGraph);
		try {
			// delete old data in the summary graph
			HttpResponse response = client.execute(delete);
			processResponse("DELETE", response);
			
			// create summary graph
			client = new DefaultHttpClient();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("input-graph", curGraph));
			nameValuePairs.add(new BasicNameValuePair("output-graph", summaryGraph));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = client.execute(post);
			processResponse("POST", response);
			
			// set summary graph
			client = new DefaultHttpClient();
			response = client.execute(put);
			processResponse("PUT", response);
		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
			sparqledTab.getWindow().showNotification(errorMessage + "\n\n" + e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			sparqledTab.getWindow().showNotification(errorMessage + "\n\n" + e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

}
