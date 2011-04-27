
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

import java.net.*;
import java.net.URI;
import java.io.*;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Alignment.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.*;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.model.*;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;

/**
 * The extraction tab which collects information about 
 * ways and components to extract information.
 */
//@SuppressWarnings("serial")
public class ExtractionTab extends CustomComponent
{

	// reference to the global internal state
	private LOD2DemoState state;

	public ExtractionTab(LOD2DemoState st) {

		// The internal state and 
		state = st;

		VerticalLayout extractionTab = new VerticalLayout();

		final Panel panel = new Panel("External components interfaces");

		VerticalLayout panelContent = new VerticalLayout();

        Link l = new Link("Virtuoso Web Interface",
                new ExternalResource("http://localhost:8890/conductor/"));
        l.setTargetName("_blank");
        l.setTargetBorder(Link.TARGET_BORDER_NONE);
        panelContent.addComponent(l);


        Link t1l2 = new Link("OpenRDF Workbench",
                new ExternalResource("http://localhost:8080/openrdf-workbench/"));
        t1l2.setTargetName("_blank");
        t1l2.setTargetBorder(Link.TARGET_BORDER_NONE);
        panelContent.addComponent(t1l2);

        Link t1l3 = new Link("Spotlight",
                new ExternalResource("http://dbpedia.org/spotlight"));
        t1l3.setTargetName("_blank");
        t1l3.setTargetBorder(Link.TARGET_BORDER_NONE);
        panelContent.addComponent(t1l3);

		panel.setContent(panelContent);
		extractionTab.addComponent(panel);


		// The composition root MUST be set
		setCompositionRoot(extractionTab);
	}


};

