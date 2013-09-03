package eu.lod2.controller;

import eu.lod2.GraphManager;
import eu.lod2.Graphs;
import eu.lod2.WebApiList;
import eu.lod2.convertor.GraphsConverter;
import eu.lod2.convertor.WebApiListConverter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
public class GraphController {
    @Autowired(required = true)
    private AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter;

    private GraphManager graphManager = new GraphManager();

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

    // system should be made optional, but it is not yet the case.
    @RequestMapping(value = "/graphs", method = RequestMethod.GET)
    public ResponseEntity<Graphs> getGraphs(HttpServletResponse response,
                                            @RequestParam(value = "system", required = false) Boolean systemincluded,
                                            @RequestParam(value = "from", required = false) Integer start
    )
            throws IOException {

        WebApiList graphs;


        System.out.print("system: ");
        System.out.println(systemincluded);

        if (systemincluded == null || !systemincluded) {
            if (start != null) {
                graphs = graphManager.getNonSystemGraphs(start);
            } else {
                graphs = graphManager.getNonSystemGraphs();
            }
            ;
        } else {
            if (start != null) {
                graphs = graphManager.getAllGraphs(start);
            } else {
                graphs = graphManager.getAllGraphs();
            }
            ;
        }

        Graphs something = new Graphs();
        something.setKind("graphs");
        something.setGraphs(graphs);
        return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
    }

    ;

    // follow virtuoso like syntax
    // http://docs.openlinksw.com/virtuoso/SELECTSTMT.html
    @RequestMapping(value = "/graphsregex", method = RequestMethod.GET)
    public ResponseEntity<Graphs> getGraphRegex(HttpServletResponse response,
                                                @RequestParam(value = "system", required = false) Boolean systemincluded,
                                                @RequestParam(value = "regex", required = false) String regex,
                                                @RequestParam(value = "from", required = false) Integer start,
                                                // all parameter ensures that ALL graphs are examined, even the non-explicitly created ones and system graphs! in this case, start is nog supported (yet?)
                                                @RequestParam(value = "all", required = false) Boolean all
    )
            throws Exception {

        WebApiList graphs;
        if(regex==null){
            regex="";
        }

        if(all !=null && all==true){
            graphs = graphManager.getReallyAllGraphs(regex, (start==null?0:start));
        } else {
            // going to old procedure
            if (systemincluded == null || !systemincluded) {
                if (start == null) {
                    graphs = graphManager.getNonSystemGraphsRegex(regex);
                } else {
                    graphs = graphManager.getNonSystemGraphsRegex(regex, start);
                }
                ;
            } else {
                if (start == null) {
                    graphs = graphManager.getAllGraphsRegex(regex);
                } else {
                    graphs = graphManager.getAllGraphsRegex(regex, start);
                }
                ;
            }
        }
        Graphs something = new Graphs();
        something.setKind("graphsregex");
        something.setGraphs(graphs);
        return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);

    }

    @RequestMapping(value = "/declare_system_graph", method = RequestMethod.POST)
    public ResponseEntity<Graphs> declare_system_graph(HttpServletResponse response,
                                                       @RequestParam("graph") String graph,
                                                       @RequestParam("for_tool") String forTool)
            throws IOException {

        graphManager.insertSystemGraph(graph, forTool);
        WebApiList graphs = graphManager.getAllSystemGraphs();
        Graphs something = new Graphs();
        something.setKind("systemgraphs");
        something.setGraphs(graphs);
        return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
    }

    ;

    @RequestMapping(value = "/sparql_endpoints", method = RequestMethod.GET)
    public ResponseEntity<Graphs> sparql_endpoints(HttpServletResponse response)
            throws Exception {

        WebApiList endpoints = graphManager.getAllEndPoints();
        Graphs something = new Graphs();
        something.setKind("sparqlendpoints");
        something.setGraphs(endpoints);

        return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
    }

    // register a graph in the store
    @RequestMapping(value = "/register_graph", method = RequestMethod.POST)
    public ResponseEntity<Graphs> register_graph(HttpServletResponse response,
                                                 @RequestParam("graph") String graph)
            throws Exception {

        graphManager.registerGraph(graph);
        WebApiList allGraphs = graphManager.getAllGraphs();
        Graphs something = new Graphs();
        something.setKind("graphs");
        something.setGraphs(allGraphs);

        return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/register_sparql_endpoint", method = RequestMethod.POST)
    public ResponseEntity<Graphs> register_sparql_endpoint(HttpServletResponse response,
                                                           @RequestParam("ep") String endpoint)
            throws Exception {

        graphManager.registerSparqlEndpoint(endpoint);
        WebApiList endpoints = graphManager.getAllEndPoints();
        Graphs something = new Graphs();
        something.setKind("sparqlendpoints");
        something.setGraphs(endpoints);

        return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
    }

    // XXX update the return of this request
    @RequestMapping(value = "/register_graph_in_sparql_endpoint", method = RequestMethod.POST)
    public ResponseEntity<Graphs> register_graph_in_sparql_endpoint(HttpServletResponse response,
                                                                    @RequestParam("ep") String endpoint,
                                                                    @RequestParam("graph") String graph
    )
            throws Exception {
        graphManager.registerGraphforSparqlEndpoint(graph, endpoint);
        WebApiList endpoints = graphManager.getAllEndPoints();
        Graphs something = new Graphs();
        something.setKind("sparqlendpoints");
        something.setGraphs(endpoints);
        return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
    }


    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        return headers;
    }


    @RequestMapping(value = "/getError", method = RequestMethod.GET)
    public void getError(HttpServletResponse response, @RequestParam("message") String message) throws Exception {
        throw new RuntimeException(message);
    }


    @RequestMapping(value = "/getMessage", method = RequestMethod.GET)
    public void getMessage(HttpServletResponse response, @RequestParam("message") String message) throws Exception {
        ServletOutputStream outputStream = response.getOutputStream();
        try {
            outputStream.write(message.getBytes(Charset.forName("UTF-8")));
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

    }

    // remove the given graphs from the store
    @RequestMapping(
            value = "/remove_graphs", method = RequestMethod.POST)
    public ResponseEntity<Graphs> remove_graph(HttpServletResponse response,
                                               @RequestParam("graphs") String graphs)
            throws Exception {

        List<String> graphList=this.parseGraphList(graphs);
        for(String graph : graphList){
            graphManager.removeGraph(graph);
        }
        WebApiList allGraphs = graphManager.getAllGraphs();
        Graphs something = new Graphs();
        something.setKind("graphs");
        something.setGraphs(allGraphs);
        return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
    }

    // build a graph group
    @RequestMapping(
            value = "/build_group", method = RequestMethod.POST)
    public ResponseEntity<Graphs> build_group(HttpServletResponse response, @RequestParam("group") String group,
                                              @RequestParam("graphs") String graphs) throws Exception{
        List<String> graphList=this.parseGraphList(graphs);
        graphManager.buildGraphGroup(group,graphList);

        WebApiList allGraphs = graphManager.getAllGraphs();
        Graphs something = new Graphs();
        something.setKind("graphs");
        something.setGraphs(allGraphs);
        return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
    }

    // drops the given graph group
    @RequestMapping(
            value = "/drop_group", method = RequestMethod.POST)
    public ResponseEntity<Graphs> drop_group(HttpServletResponse response, @RequestParam("group") String group) throws Exception{
        graphManager.dropGraphGroup(group);

        WebApiList allGraphs = graphManager.getAllGraphs();
        Graphs something = new Graphs();
        something.setKind("graphs");
        something.setGraphs(allGraphs);
        return new ResponseEntity<Graphs>(something, getHeaders(), HttpStatus.OK);
    }

    /**
     * Parses a list of graphs from a string of the format <graph1>.*<graph2>.*<graph3>.*<graph4>
     * @param graphs ; the string to parse
     * @return a list of parsed strings without wrapping <>
     */
    private List<String> parseGraphList(String graphs){
        List<String> graphList= new ArrayList<String>();
        while(graphs.indexOf("<")>=0){
            int open=graphs.indexOf("<");
            int close= graphs.indexOf(">");

            if(open>=0 && close>=0){
                String graph=graphs.substring(open+1,close);
                graphList.add(graph);
                graphs= graphs.substring(Math.max(graph.length(),close+1));
            }else{
                graphs="";
            }
        }
        return graphList;
    }


    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error by GraphController errorhandling: " + e.getMessage());
    }


}
