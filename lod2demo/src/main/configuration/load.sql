CREATE PROCEDURE dump_one_graph 
  ( IN  srcgraph           VARCHAR  , 
    IN  out_file           VARCHAR  , 
    IN  file_length_limit  INTEGER  := 1000000000
  )
  {
    DECLARE  file_name  varchar;
    DECLARE  env, ses      any;
    DECLARE  ses_len, 
             max_ses_len, 
             file_len, 
             file_idx      integer;
    SET ISOLATION = 'uncommitted';
    max_ses_len := 10000000;
    file_len := 0;
    file_idx := 1;
    file_name := sprintf ('%s%06d.ttl', out_file, file_idx);
    string_to_file ( file_name || '.graph', 
                     srcgraph, 
                     -2
                   );
    string_to_file ( file_name, 
                     sprintf ( '# Dump of graph <%s>, as of %s\n', 
                               srcgraph, 
                               CAST (NOW() AS VARCHAR)
                             ), 
                     -2
                   );
    env := vector (dict_new (16000), 0, '', '', '', 0, 0, 0, 0);
    ses := string_output ();
    FOR (SELECT * FROM ( SPARQL DEFINE input:storage "" 
                         SELECT ?s ?p ?o { GRAPH `iri(?:srcgraph)` { ?s ?p ?o } } 
                       ) AS sub OPTION (LOOP)) DO
      {
        http_ttl_triple (env, "s", "p", "o", ses);
        ses_len := length (ses);
        IF (ses_len > max_ses_len)
          {
            file_len := file_len + ses_len;
            IF (file_len > file_length_limit)
              {
                http (' .\n', ses);
                string_to_file (file_name, ses, -1);
                file_len := 0;
                file_idx := file_idx + 1;
                file_name := sprintf ('%s%06d.ttl', out_file, file_idx);
                string_to_file ( file_name, 
                                 sprintf ( '# Dump of graph <%s>, as of %s (part %d)\n', 
                                           srcgraph, 
                                           CAST (NOW() AS VARCHAR), 
                                           file_idx), 
                                 -2
                               );
                 env := vector (dict_new (16000), 0, '', '', '', 0, 0, 0, 0);
              }
            ELSE
              string_to_file (file_name, ses, -1);
            ses := string_output ();
          }
      }
    IF (LENGTH (ses))
      {
        http (' .\n', ses);
        string_to_file (file_name, ses, -1);
      }
  }
;


dump_one_graph('http://localhost/lod2democonfiguration', '/var/lib/lod2demo/configuration.preload.nt');

DB.DBA.TTLP_MT (file_to_string_output ('/var/lib/lod2demo/configuration.nt'), '', 'http://localhost/lod2democonfiguration');
DB.DBA.RDF_LOAD_RDFXML (file_to_string_output ('/var/lib/lod2demo/tools.owl'), '', 'http://lod2.eu/tools.owl');
