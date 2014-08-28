package eu.lod2.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import eu.lod2.IConfiguration;
import eu.lod2.LOD2DemoState;

public class ConfigurationBackend implements IConfiguration {
	
	private LOD2DemoState state;
	private String confGraph;
	private String namespace;
	private String confURI;
	
	public ConfigurationBackend(LOD2DemoState state, String confGraph, String namespace, String confURI){
		this.state = state;
		this.confGraph = confGraph;
		this.namespace = namespace;
		this.confURI = confURI;
	}
        
        public ConfigurationBackend(LOD2DemoState state){
                this.state = state;
		this.confGraph = "http://localhost/lod2statworkbenchconfiguration";
		this.namespace = "http://lod2.eu/lod2statworkbench/";
		this.confURI = "http://localhost/lod2statworkbenchconfiguration";
        }
	
	protected TupleQueryResult executeTupleQuery(String query){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
			TupleQueryResult result = tupleQuery.evaluate();
			return result;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected GraphQueryResult executeGraphQuery(String query){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			GraphQuery graphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, query);
			GraphQueryResult result = graphQuery.evaluate();
			return result;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getComponents() {
		StringBuilder query = new StringBuilder();
		query.append("PREFIX swb: <").append(namespace).append("> \n");
		query.append("SELECT ?component \n");
		query.append("FROM <").append(confGraph).append("> \n");
		query.append("WHERE { \n");
		query.append("  <").append(confURI).append("> swb:configures ?component . \n");
		query.append("} \n");
		
		TupleQueryResult tuples = executeTupleQuery(query.toString());
		List<String> res = new ArrayList<String>();
		try {
			while (tuples.hasNext()){
				try {
					BindingSet set = tuples.next();
					res.add(set.getBinding("component").getValue().stringValue());
				} catch (QueryEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}

	public Map<String, String> getComponentURLs() {
		StringBuilder query = new StringBuilder();
		query.append("PREFIX swb: <").append(namespace).append("> \n");
		query.append("SELECT ?component ?url \n");
		query.append("FROM <").append(confGraph).append("> \n");
		query.append("WHERE { \n");
		query.append("  <").append(confURI).append("> swb:configures ?component . \n");
		query.append("  ?component swb:service ?url . \n");
		query.append("} \n");
		
		TupleQueryResult tuples = executeTupleQuery(query.toString());
		Map<String, String> res = new HashMap<String, String>();
		try {
			while (tuples.hasNext()){
				try {
					BindingSet set = tuples.next();
					res.put(set.getBinding("component").getValue().stringValue(), 
							set.getBinding("url").getValue().stringValue());
				} catch (QueryEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}

	public List<PropertyValue> getComponentProperties(String component) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT ?prop ?val \n");
		query.append("FROM <").append(confGraph).append("> \n");
		query.append("WHERE { \n");
		query.append("  <").append(component).append("> ?prop ?val . \n");
		query.append("} \n");
		
		TupleQueryResult tuples = executeTupleQuery(query.toString());
		List<PropertyValue> res = new ArrayList<PropertyValue>();
		try {
			while (tuples.hasNext()){
				try {
					BindingSet set = tuples.next();
					PropertyValue e = new PropertyValue();
					e.prop = set.getBinding("prop").getValue().stringValue();
					e.val = set.getBinding("val").getValue().stringValue();
					res.add(e);
				} catch (QueryEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}

	public void setServiceURL(String component, String url) {
		setProperty(component, "http://lod2.eu/lod2statworkbench/service", url);
	}

	public void setProperty(String component, String property, String value) {
		StringBuilder query = new StringBuilder();
		query.append("PREFIX swb: <").append(namespace).append("> \n");
		query.append("MODIFY GRAPH <").append(confGraph).append("> \n");
		query.append("DELETE { \n");
		query.append("  <").append(component).append("> <").append(property).append("> ?v . \n");
		query.append("} \n");
		query.append("INSERT { \n");
		query.append("  <").append(component).append("> <").append(property).append("> \"").append(value).append("\"^^<http://www.w3.org/2001/XMLSchema#string> . \n");
		query.append("} \n");
		query.append("WHERE { \n");
		query.append("  <").append(component).append("> <").append(property).append("> ?v . \n");
		query.append("} ");
		executeGraphQuery(query.toString());
	}

	public String getHostname() {
		StringBuilder query = new StringBuilder();
		query.append("PREFIX swb: <").append(namespace).append("> \n");
		query.append("SELECT ?hostname \n");
		query.append("FROM <").append(confGraph).append("> \n");
		query.append("WHERE { \n");
		query.append("  <").append(confURI).append("> swb:hostname ?hostname . \n");
		query.append("}");
		
		TupleQueryResult res = executeTupleQuery(query.toString());
		try {
			if (res.hasNext()) return res.next().getValue("hostname").stringValue();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setHostname(String hostname) {
		setProperty(confURI, namespace + "hostname", hostname);
	}

}
