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
    
}
