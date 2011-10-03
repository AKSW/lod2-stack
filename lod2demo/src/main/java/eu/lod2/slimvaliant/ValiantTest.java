package eu.lod2.slimvaliant;

import com.googlecode.sardine.DavResource;

import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Johan.De-Smedt
 * Date: Aug 15, 2011
 * Time: 2:25:09 PM
 * .
 */
public class ValiantTest {
    private Reader davReader;
    private String host = "http://wp7.lod2.eu:8890";
    private String webdavUrl = "/DAV/home/wkd/rdf_sink/";
    private String regex = "[a-z][^\\.]*\\.xml";
    private String user = "dav";
    private String psw = "wp7admin";
    private DavResource res;
    private String xslPath = "C:/Project/LOD2/WP07-WKDE/xslt/wkd.xsl";
    private String rdfFolder = "C:/Project/LOD2/WP07-WKDE/result/test/";
    private WkdTransformer transformer;

    ValiantTest() throws Exception {
        try {
            this.davReader = new Reader(this.host, this.webdavUrl, this.regex, -1, this.user, this.psw);
            this.transformer = new WkdTransformer(xslPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void executeWorklow() throws Exception {
        for (; true; ) {
            res = (DavResource) this.davReader.nextMatch();
            if (res == null) return;
            String name = res.getName().replaceAll(".xml",".rdf");
            System.out.println("processing " + name);
            File output = new File(this.rdfFolder + name);
            StreamResult result = new StreamResult(output);
            InputStream rdf = null;
            try {
                this.transformer.transform(this.davReader.getStream(res), result);
                rdf = new FileInputStream(output);
                this.davReader.putStream(name, rdf);
            } catch (Exception e) {
                System.out.println(name);
                e.printStackTrace();
                // throw new Exception("Failed to transform " + name);
            } finally {
                if (rdf != null) rdf.close();
            }
        }
    }


}
