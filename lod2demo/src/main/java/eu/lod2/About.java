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
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Alignment.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.*;
import com.vaadin.data.util.BeanContainer;

import org.openrdf.model.*;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.model.impl.*;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.lod2.LOD2DemoState;

/**
 * An about page on the LOD2 stack
 */
//@SuppressWarnings("serial")
public class About extends CustomComponent
{

    // reference to the global internal state
    private LOD2DemoState state;

    public About(LOD2DemoState st) {

        // The internal state 
        state = st;

	VerticalLayout panel = new VerticalLayout(); 

	Label intro = new Label(
		"The following lists gives an overview of all the components that have been contributed to the LOD2 stack.",
		Label.CONTENT_XHTML);

	panel.addComponent(intro);


	BeanContainer<String, AboutTable> abouts = createAboutTable();

	Table table = new Table("", abouts);
	table.setWidth("100%");
	table.setSelectable(false);
        table.setMultiSelect(false);
        table.setImmediate(false);
 	table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
	table.setItemIconPropertyId("license");
	table.setVisibleColumns(new String[] {"component", "partner", "contact", "homepage", "license", "description" });
	table.setColumnWidth("partner",70);
	table.setColumnWidth("contact",55);
	table.setColumnWidth("homepage",55);
	table.setColumnWidth("license",55);
	table.setColumnExpandRatio("component", 0.1f);
	table.setColumnExpandRatio("description", 0.9f);

//	table.setColumnHeaders(new String[] {"Component", "LOD2 Partner", "Contact", "Homepage", "License", "Description" });
	table.setColumnAlignments(new String[] { Table.ALIGN_LEFT,
                Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_LEFT});



	panel.addComponent(table);


        // The composition root MUST be set
        setCompositionRoot(panel);
    }

	// propagate the information of one tab to another.
	public void setDefaults() {
	};

	// the table data structure as a bean.
	public class AboutTable implements Serializable {
    		String component;
    		String partner; 
		Link contact;
		Link homepage;
		Link license;
		Label description;

		final ThemeResource opensource = new ThemeResource("app_images/opensource.png");
		final ThemeResource closedsource = new ThemeResource("app_images/commercial.png");
		final ThemeResource bothsource = new ThemeResource("app_images/com-open.png");

		public AboutTable(String c, String p, String con, String h, String l, String lm, String d) {
			component = c;
			partner = p;
			if (!con.equals("")) {
				contact = new Link();
        			contact.setResource(new ExternalResource("mailto:"+con));
//				contact.setCaption(con);
				contact.setIcon(new ThemeResource("icons/E-Mail.png"));
			};
			homepage = new Link();
			if (!h.equals("")) {
                 		homepage.setResource(new ExternalResource(h));
//                 		homepage.setCaption(h);
				homepage.setIcon(new ThemeResource("icons/Internet.png"));
			};
			license = new Link();
			if (!l.equals("")) {
                 		license.setResource(new ExternalResource(l));
				if (lm.equals("http://lod2.eu/tools.owl#OpenSource")) {
					license.setIcon(opensource);
				} else if (lm.equals("http://lod2.eu/tools.owl#Commercial")) {
					license.setIcon(closedsource);
				} else {
					license.setIcon(bothsource);
				};
			};
			description = new Label(d, Label.CONTENT_XHTML);
//			description.setValue(d);
//			description.setWidth("100%");
//			description.setReadOnly(true);
		};

		public String getComponent() {
        		return component;
    		};
    
    		public void setComponent(String component) {
        		this.component = component;
    		};

		public String getPartner() {
        		return partner;
    		};
    
    		public void setPartner(String partner) {
        		this.partner = partner;
    		};

		public Link getContact() {
        		return contact;
    		};
    
    		public void setContact(String contact) {
        		this.contact.setResource(new ExternalResource("mailto:"+contact));
			this.contact.setCaption(contact);
    		};

		public Link getHomepage() {
        		return homepage;
    		};
    
    		public void setHomepage(String l) {
			homepage = new Link();
                 	homepage.setResource(new ExternalResource(l));
                 	homepage.setCaption(l);
    		};

		public Link getLicense() {
        		return license;
    		};
    
    		public void setLicense(String l) {
			this.license = new Link();
			if (!l.equals("")) {
                 		license.setResource(new ExternalResource(l));
				license.setIcon(opensource);
			};
    		};

		public Label getDescription() {
        		return description;
    		};
    
    		public void setDescription(String description) {
        		this.description.setValue(description);
    		};
	};

	public BeanContainer<String, AboutTable> createAboutTable() {

	BeanContainer<String, AboutTable> abouts = new BeanContainer<String, AboutTable>(AboutTable.class);
    	abouts.setBeanIdProperty("component");

	try {
		RepositoryConnection con = state.getRdfStore().getConnection();

		// initialize the hostname and portnumber
		String query = "PREFIX lod2: <http://lod2.eu/tools.owl#>" +
			"SELECT  DISTINCT ?complabel ?partner ?responsible ?homepage ?license ?licenseModel ?desc from <http://lod2.eu/tools.owl> where {" +
			"?comp rdf:type lod2:Tool. ?comp rdfs:label ?complabel. " + 
			"OPTIONAL {?comp lod2:contributed_by_partner ?partnerId. ?partnerId rdfs:label ?partner.}" +
			"OPTIONAL {?comp lod2:responsible ?r. ?r lod2:person_email ?responsible.}" +
			"OPTIONAL {?comp lod2:tool_homepage ?homepage.}" +
			"OPTIONAL {?comp lod2:license ?licenseId. ?licenseId lod2:license_url ?license. ?licenseId rdf:type ?licenseModel.}" +
			"OPTIONAL {?comp lod2:tool_description ?desc.}}";
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult result = tupleQuery.evaluate();


		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			Value valueOf1 = bindingSet.getValue("complabel");
			Value valueOf2 = bindingSet.getValue("partner");
			Value valueOf3 = bindingSet.getValue("responsible");
			Value valueOf4 = bindingSet.getValue("homepage");
			Value valueOf5 = bindingSet.getValue("license");
			Value valueOf6 = bindingSet.getValue("desc");
			Value valueOf7 = bindingSet.getValue("licenseModel");
		

			abouts.addBean(new AboutTable(getStringValue(valueOf1), getStringValue(valueOf2), getStringValue(valueOf3), 
				getStringValue(valueOf4), getStringValue(valueOf5), getStringValue(valueOf7), getStringValue(valueOf6)));
		};

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	return abouts;
	};
	
	public String getStringValue(Value v) {
		
		if (v == null) {
			return "";
		} else {
			return v.stringValue();
		}
	};

};

