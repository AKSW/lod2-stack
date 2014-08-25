/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.lod2.stat.dsdrepo;

/**
 *
 * @author vukm
 */
public class DsdRepoQueries {
    
    public static String qCompatibleCodeLists(String property, String sourceGraph, String targetGraph){
        String query = "SELECT DISTINCT ?cl \n"
                + "WHERE { \n"
                + "  graph <@gTarget> { \n"
                + "    ?cl a skos:ConceptScheme . \n"
                + "  } \n"
                + "  FILTER NOT EXISTS { \n"
                + "    graph <@gSource> { \n"
                + "      ?obs a qb:Observation . \n"
                + "      ?obs <@prop> ?item . \n"
                + "    } \n"
                + "    FILTER NOT EXISTS { \n"
                + "      graph <@gTarget> { \n"
                + "        ?item skos:inScheme ?cl . \n"
                + "      }"
                + "    } \n"
                + "  } \n"
                + "}";
        return query.replace("@prop", property).replace("@gSource", sourceGraph).replace("@gTarget", targetGraph);
    }
    
    public static String qPropertyTypes(String property, String sourceGraph){
        String query = "SELECT DISTINCT isiri(?val) datatype(?val) \n"
                + "FROM <@gSource> \n"
                + "WHERE { \n"
                + "  ?obs a qb:Observation . \n"
                + "  ?obs <@prop> ?val . \n"
                + "}";
        return query.replace("@prop", property).replace("@gSource", sourceGraph);
    }
    
    public static void main (String [] args){
        System.out.println(qPropertyTypes("http://elpo.stat.gov.rs/lod2/RS-DIC/rs/geo", "http://elpo.stat.gov.rs/test/cvmod/noDataSet/"));
    }
    
}
