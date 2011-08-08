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
 * The online tools tab which collects information about 
 * additional online tools.
 */
//@SuppressWarnings("serial")
public class OnlineToolsTab extends CustomComponent
{

	private String storeInGraph;
	private String sameasid;

	// reference to the global internal state
	private LOD2DemoState state;

	public OnlineToolsTab(String g, LOD2DemoState st) {

		// The internal state and 
		state = st;
		storeInGraph = g;

		//************************************************************************
		// OnlineToolsTab
		VerticalLayout onlineToolsTab = new VerticalLayout();




		TextField sid = new TextField("search sameAs id's for:");
		final Label sidenc = new Label("");
		// configure & add to layout
		sid.setImmediate(true);
		sid.setColumns(30);
		sid.addListener(new TextChangeListener() {
			public void textChange(TextChangeEvent event) {
				sameasid = event.getText();
			}
		});

		Button sidbutton = new Button("convert", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					URI encoded = new URI(sameasid);
					sidenc.setValue("http://sameAs.org/rdf?uri=" + encoded.toASCIIString());
				} catch (Exception e) {
					e.printStackTrace();
				};
				try {
					//	java.net.URL sameAsData = new java.net.URL("http://sameAs.org/rdf?uri=http%3A%2F%2Fdbpedia.org%2Fresource%2FGermany");

					URI sidencoded = new URI(sameasid);
					java.net.URL sameAsData = new java.net.URL("http://sameAs.org/rdf?uri=" + sidencoded.toASCIIString());
					String baseURI = "http://sameAs.org#";

					RepositoryConnection con = state.getRdfStore().getConnection();
					Resource contextURI = con.getValueFactory().createURI(storeInGraph);
					Resource[] contexts = new Resource[] {contextURI};
					con.add(sameAsData, baseURI, RDFFormat.RDFXML, contexts);


				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (RDFParseException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}; 
			};
		});

		onlineToolsTab.addComponent(sid);
		onlineToolsTab.addComponent(sidenc);
		onlineToolsTab.addComponent(sidbutton);


		try { 
			URL url2 = new URL(state.getHostName() + "/lod2.sigma.html");
			Embedded browser2 = new Embedded("", new ExternalResource(url2));
			browser2.setType(Embedded.TYPE_BROWSER);
			browser2.setWidth("100%");
			browser2.setHeight(500);
			onlineToolsTab.addComponent(browser2);	
		} catch (MalformedURLException e) {
			e.printStackTrace();
		};


		/* The online resources link page */
		final Panel panel = new Panel("LOD2 online components");

		VerticalLayout panelContent = new VerticalLayout();

		Link l = new Link("SameAs Inference",
				new ExternalResource("http://sameAs.org/"));
		l.setTargetName("_blank");
		l.setTargetBorder(Link.TARGET_BORDER_NONE);
		panelContent.addComponent(l);

		Link l2 = new Link("Sig.ma",
				new ExternalResource("http://sig.ma/"));
		l2.setTargetName("_blank");
		l2.setTargetBorder(Link.TARGET_BORDER_NONE);
		panelContent.addComponent(l2);

		Link l3 = new Link("LOD cloud",
				new ExternalResource("http://lod.openlinksw.com/"));
		l3.setTargetName("_blank");
		l3.setTargetBorder(Link.TARGET_BORDER_NONE);
		panelContent.addComponent(l3);

		panel.setContent(panelContent);
		onlineToolsTab.addComponent(panel);


		// The composition root MUST be set
		setCompositionRoot(onlineToolsTab);
	}


};

