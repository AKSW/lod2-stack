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
 * The configuration tab which collects information about 
 * the default settings van the LOD2 demonstrator.
 */
//@SuppressWarnings("serial")
public class ConfigurationTab extends CustomComponent
    implements TextChangeListener 
{

	// reference to the global internal state
	private LOD2DemoState state;

    // fields
    private String defaultgraphvalue;

	public ConfigurationTab(LOD2DemoState st) {

		// The internal state and 
		state = st;

		VerticalLayout configurationTab = new VerticalLayout();

	    // Configuration form start
        // Set all properties at once for the moment.
        Form t2f = new Form();
        t2f.setCaption("Configuration");

        TextField defaultgraph = new TextField("Default graph:", state.getCurrentGraph());
        defaultgraph.setImmediate(false);
        defaultgraph.addListener(this);
        defaultgraph.setColumns(50);
        t2f.getLayout().addComponent(defaultgraph);

        // initialize the footer area of the form
        HorizontalLayout t2ffooterlayout = new HorizontalLayout();
        t2f.setFooter(t2ffooterlayout);

        Button commitButton = new Button("Set configuration", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                storeConfiguration(event);
            }
        });
        commitButton.setDescription("Commit the new configuration settings.");
        t2f.getFooter().addComponent(commitButton);

        configurationTab.addComponent(t2f);

        // Configuration form end


		// The composition root MUST be set
		setCompositionRoot(configurationTab);
	}

    private void storeConfiguration(ClickEvent event) {
       state.setCurrentGraph(defaultgraphvalue);

    };

    public void textChange(TextChangeEvent event) {

        defaultgraphvalue = event.getText();
    };
};

