package eu.lod2;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import eu.lod2.IConfiguration.PropertyValue;

public class ConfigurationGUI extends VerticalLayout {
	
	private IConfiguration configService;
	private Tree categoryTree;
	private HorizontalSplitPanel splitPanel;
	private VerticalLayout contentPanel;
	
	public ConfigurationGUI(IConfiguration configService){
		this.configService = configService;
	}
	
	public void render(){
		// tree on the left Components->indiv.comp. and Global->domain
		// comp: table with properties and values
		// domain: table with domains and how many components (and which) use them
	}
	
	private void createGUI(){
		removeAllComponents();
		setSpacing(true);
		setSizeFull();
		
		splitPanel = new HorizontalSplitPanel();
//		splitPanel.setHeight("100%");
//		splitPanel.setWidth("100%");
		splitPanel.setImmediate(true);
		splitPanel.setSizeFull();
		splitPanel.setSplitPosition(250, Sizeable.UNITS_PIXELS);
		addComponent(splitPanel);
		
		categoryTree = new Tree();
		categoryTree.setNullSelectionAllowed(false);
		categoryTree.setImmediate(true);
		splitPanel.addComponent(categoryTree);
		
		contentPanel = new VerticalLayout();
		contentPanel.setSpacing(true);
		contentPanel.setSizeFull();
		contentPanel.setMargin(false, false, false, true);
		splitPanel.addComponent(contentPanel);
		
		final Object globalItem = categoryTree.addItem();
		categoryTree.setItemCaption(globalItem, "Global");
		final Object domainItem = categoryTree.addItem();
		categoryTree.setParent(domainItem, globalItem);
		categoryTree.setItemCaption(domainItem, "Domain");
		categoryTree.setChildrenAllowed(domainItem, false);
		final Object compItem = categoryTree.addItem();
		categoryTree.setItemCaption(compItem, "Component");
		
		final List<String> compList = configService.getComponents();
		for (String compString: compList){
			categoryTree.addItem(compString);
			categoryTree.setParent(compString, compItem);
			categoryTree.setChildrenAllowed(compString, false);
		}
		
		categoryTree.expandItemsRecursively(globalItem);
		categoryTree.expandItemsRecursively(compItem);
		
		categoryTree.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object selectedItem = event.getProperty().getValue();
				if (compList.contains(selectedItem))
					componentSelected((String)selectedItem);
				else if (selectedItem == domainItem)
					domainSelected();
				else 
					contentPanel.removeAllComponents();
			}
		});
	}

	private void componentSelected(final String component) {
		contentPanel.removeAllComponents();
		final Table table = new Table("Component configuration");
		table.setWidth("100%");
		table.addContainerProperty("Property", String.class, null);
		table.addContainerProperty("Value", String.class, null);
		table.setTableFieldFactory(new MyFieldFactory());
		table.setEditable(true);
		table.setColumnWidth("Property", 300);
		
		final List<PropertyValue> props = new ArrayList<IConfiguration.PropertyValue>();
		props.addAll(configService.getComponentProperties(component));
		updateComponentTable(table, props);
		
		contentPanel.addComponent(table);
		contentPanel.setExpandRatio(table, 0.0f);
		Button btn = new Button("Submit");
		contentPanel.addComponent(btn);
		contentPanel.setExpandRatio(btn, 2.0f);
		contentPanel.setComponentAlignment(btn, Alignment.TOP_LEFT);
		btn.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				boolean changed = false;
				for (int i=0; i<props.size(); i++){
					PropertyValue pv = props.get(i);
					String newVal = table.getItem(i).getItemProperty("Value").getValue().toString();
					if (!pv.val.equals(newVal)){
						configService.setProperty(component, pv.prop, newVal);
						changed = true;
					}
				}
				
				if (changed){
					props.clear();
					props.addAll(configService.getComponentProperties(component));
					updateComponentTable(table, props);
					getWindow().showNotification("Settings saved");
				}
			}
		});
	}
	
	private void updateComponentTable(Table table, List<PropertyValue> props){
		table.removeAllItems();
		for (int i=0; i<props.size(); i++){
			PropertyValue pair = props.get(i);
			table.addItem(new Object [] {pair.prop, pair.val}, i);
		}
		table.setPageLength(props.size());
	}
	
	private void domainSelected(){
		final Map<String, String> compURLs = configService.getComponentURLs();
		final List<String> domainList = new ArrayList<String>();
		final List<String> urlList = new ArrayList<String>();
		final List<List<String>> compLists = new ArrayList<List<String>>();
		final List<String> errorList = new ArrayList<String>();
		for (Entry<String, String> c:compURLs.entrySet()){
			URI uri = null;
			boolean synthaxProblem = false;
			try {
				uri = new URI(c.getValue());
			} catch (URISyntaxException e) {
				synthaxProblem = true;
			}
			boolean domainProblem = uri.getHost() == null;
			boolean schemaProblem = false;
			try {
				new URL(c.getValue());
			} catch (MalformedURLException e) {
				schemaProblem = true;
			}
			if (synthaxProblem || domainProblem || schemaProblem) {
				errorList.add(c.getKey());
				continue;
			}
			
			String domain = uri.getHost();
			int index = domainList.indexOf(domain);
			if (index > -1)
				compLists.get(index).add(c.getKey());
			else {
				// add new domain and its list of components
				domainList.add(domain);
				List<String> l = new LinkedList<String>();
				l.add(c.getKey());
				compLists.add(l);
			}
				
		}
		
		contentPanel.removeAllComponents();
		final Table table = new Table("Domains");
		table.setWidth("100%");
		table.setHeight("400px");
		table.addContainerProperty("Domain", String.class, null);
		table.addContainerProperty("Number of components", Integer.class, null);
		table.addGeneratedColumn("Components", new ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Item item = table.getItem(itemId);
				String domain = item.getItemProperty("Domain").getValue().toString();
				String res = "";
				for (String c: compLists.get(domainList.indexOf(domain)))
					res += c + "<br>";
				return new Label(res, Label.CONTENT_XHTML);
			}
		});
		table.setTableFieldFactory(new MyFieldFactory());
		table.setEditable(true);
		
		// add elements
		for (int i=0; i<domainList.size(); i++)
			table.addItem(new Object [] {domainList.get(i), compLists.get(i).size()}, i);
		table.setPageLength(domainList.size());
//		table.setColumnWidth("Domain", 300);
		table.setColumnWidth("Number of components", 200);
		contentPanel.addComponent(table);
		contentPanel.setExpandRatio(table, 0.0f);
		
		Button btn = new Button("Submit");
		contentPanel.addComponent(btn);
		contentPanel.setExpandRatio(btn, 2.0f);
		
		btn.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				boolean changed = false;
				for (int i=0; i<domainList.size(); i++){
					String oldDomain = domainList.get(i);
					String newDomain = table.getItem(i).getItemProperty("Domain").getValue().toString();
					if (!oldDomain.equals(newDomain)){
						for (String component: compLists.get(i)){
							String regex = "(.*://)" + oldDomain;
							String replacement = "$1" + newDomain;
							String newURL = compURLs.get(component).replaceFirst(regex, replacement);
							configService.setServiceURL(component, newURL);
						}	
						changed = true;
						String hostname = configService.getHostname();
						if (hostname.startsWith(oldDomain))
							configService.setHostname(hostname.replaceFirst(oldDomain, newDomain));
						getWindow().showNotification("Settings changed");
					}
				}
				
				if (changed) {
					domainSelected();
					// TODO: optimize
				}
			}
		});
	}

	@Override
	public void attach() {
		super.attach();
		createGUI();
	}
	
	private class MyFieldFactory extends DefaultFieldFactory {

		@Override
		public Field createField(Container container, Object itemId,
				Object propertyId, Component uiContext) {
			if (propertyId.toString().equalsIgnoreCase("Property") || 
					propertyId.toString().equalsIgnoreCase("Components") ||
					propertyId.toString().equalsIgnoreCase("Number of components")) return null;
			Field res = super.createField(container, itemId, propertyId, uiContext);
			res.setWidth("100%");
			res.addStyleName("config-table-input");
			return res;
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
