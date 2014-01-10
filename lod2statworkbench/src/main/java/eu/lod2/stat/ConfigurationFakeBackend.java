package eu.lod2.stat;

import eu.lod2.LOD2DemoState;

public class ConfigurationFakeBackend extends ConfigurationBackend {
	
	public ConfigurationFakeBackend (LOD2DemoState state){
		super (state,
				"http://pupin.rs/vukm/test/statwb-conf/",
				"http://lod2.eu/lod2statworkbench/",
				"http://localhost/lod2statworkbenchconfiguration");
		
		executeGraphQuery("drop silent graph <http://pupin.rs/vukm/test/statwb-conf/>");
		executeGraphQuery("create silent graph <http://pupin.rs/vukm/test/statwb-conf/>");
		StringBuilder query = new StringBuilder();
		query.append("insert into graph <http://pupin.rs/vukm/test/statwb-conf/> { \n");
		query.append("  ?s ?p ?o . \n");
		query.append("} \n");
		query.append("WHERE { \n");
		query.append("  graph <http://localhost/lod2statworkbenchconfiguration> { ?s ?p ?o . } \n");
		query.append("}");
		executeGraphQuery(query.toString());
	}

}
