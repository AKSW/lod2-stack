
/*
 * Copyright 2011 LOD2 consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package eu.lod2;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Extract RDF via a D2R wrapper which maps a SQL database to a RDF view.
 * Each SPARQL query to D2R SPARQL endpoints will be translated to a corresponding SQL query.
 * The data hence is not materialized as RDF.
 */
//@SuppressWarnings("serial")
public class D2RCordis extends CustomComponent
{

    // reference to the global internal state
    private LOD2DemoState state;

    public D2RCordis(LOD2DemoState st) {

        // The internal state 
        state = st;

	Embedded browser = new Embedded();
	try {

        LOD2DemoInitApp urlD2r = new LOD2DemoInitApp(st, "d2r-cordis");

        System.err.println(urlD2r.service);
	  	URL url = new URL(urlD2r.service);
		browser = new Embedded("", new ExternalResource(url));
		browser.setType(Embedded.TYPE_BROWSER);
		browser.setSizeFull();
		//panel.addComponent(browser);
	} catch (MalformedURLException e) {
                e.printStackTrace();
	};

        // The composition root MUST be set
        setCompositionRoot(browser);
    }

	// propagate the information of one tab to another.
	public void setDefaults() {
	};

};

