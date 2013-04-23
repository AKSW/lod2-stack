package eu.lod2.stat;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;

import eu.lod2.LOD2DemoServlet;

public class StatLOD2DemoServlet extends LOD2DemoServlet {

	@Override
	public Class<? extends Application> getApplicationClass() {
		return StatLOD2Demo.class;
	}

	@Override
	public Application getNewApplication(HttpServletRequest request) {
		return new StatLOD2Demo();
	}
	
	

}
