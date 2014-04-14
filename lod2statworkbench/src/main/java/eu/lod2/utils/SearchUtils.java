package eu.lod2.utils;

public class SearchUtils {
	
	private static StringBuilder createBuilder(){
		StringBuilder builder = new StringBuilder();
		builder.append("PREFIX qb: <http://purl.org/linked-data/cube#> \n");
		builder.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n");
		builder.append("PREFIX dct: <http://purl.org/dc/terms/> \n");
		builder.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		return builder;
	}
	
	public static String getMatchingDataSets(String regex){
		StringBuilder query = createBuilder();
		query.append("SELECT DISTINCT * \n");
		query.append("WHERE { \n");
		query.append("  GRAPH ?g { \n");
		query.append("    ?ds qb:structure [] . \n");
		query.append("    OPTIONAL { \n");
		query.append("      ?ds rdfs:label ?label . \n");
		query.append("      FILTER (REGEX(LCASE(?label),LCASE('").append(regex).append("'))) \n");
		query.append("    } \n");
		query.append("    OPTIONAL { \n");
		query.append("      ?ds rdfs:comment ?comment . \n");
		query.append("      FILTER (REGEX(LCASE(?comment),LCASE('").append(regex).append("'))) \n");
		query.append("    } \n");
		query.append("    OPTIONAL { \n");
		query.append("      ?ds dct:title ?title . \n");
		query.append("      FILTER (REGEX(LCASE(?title),LCASE('").append(regex).append("'))) \n");
		query.append("    } \n");
		query.append("    OPTIONAL { \n");
		query.append("      ?ds dct:description ?description . \n");
		query.append("      FILTER (REGEX(LCASE(?description),LCASE('").append(regex).append("'))) \n");
		query.append("    } \n");
		query.append("    FILTER(BOUND(?label) OR BOUND(?comment) OR BOUND(?title) OR BOUND(?description)) \n");
		query.append("  } \n");
		query.append("} ORDER BY ?g ?ds ");
		return query.toString();
	}

}
