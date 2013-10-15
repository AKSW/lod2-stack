package eu.lod2;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ApplicationServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.IOException;


public class LOD2DemoServlet extends ApplicationServlet {
 
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    };

	

    @Override
    protected void writeAjaxPageHtmlHeader(BufferedWriter page, String title, String themeUri, HttpServletRequest request) throws IOException {
        super.writeAjaxPageHtmlHeader(page, title, themeUri, request);
      //  page.append("<script type=\"text/javascript\" src=\"http://sig.ma/js/sigma-widget.js\"></script>");
	// this can possibly be removed => the project deployment seems not to refresh all information in the browser.
//        page.append("<link rel=\"shortcut icon\" href=\"/lod2statworkbench/VAADIN/themes/lod2/favicon.ico\" type=\"image/x-icon\" />");
    }

    @Override
    public Class<? extends Application> getApplicationClass() {
        return LOD2Demo.class;
    }

    @Override
    public Application getNewApplication(HttpServletRequest request) {
	return new LOD2Demo();
    }   

}
