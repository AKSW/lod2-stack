package eu.lod2.stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

import eu.lod2.LOD2DemoState;
import eu.lod2.utils.SearchUtils;

public class SearchCubes extends CustomComponent {
	
	private LOD2DemoState state;
	private VerticalLayout mainContainer;
	private VisualizeResults visualizer;
	
	private class MatchingParams {
		public String g, ds;
		public Value title, label, description, comment;
		public int getSortingScore(){
			int score = 0;
			score += (title != null)?3:0;
			score += (label != null)?3:0;
			score += (description != null)?1:0;
			score += (comment != null)?1:0;
			return score;
		}
	}
	private class ParamsComparator implements Comparator<MatchingParams> {
		public int compare(MatchingParams o1, MatchingParams o2) {
			return o2.getSortingScore() - o1.getSortingScore();
		}
	}
	private interface VisualizeResults {
		public void visualize(Collection<MatchingParams> results, String regex);
	}
	private class TableVisualization implements VisualizeResults {
		private Table table;
		public TableVisualization(Table table){
			this.table = table;
		}
		public void visualize(Collection<MatchingParams> results, String regex) {
			int i=0;
			table.removeAllItems();
			for (MatchingParams params: results){
				String ds = params.ds;
				String graph = params.g;
				table.addItem(new Object [] {ds, "", ""}, i++);
				table.addItem(new Object[] {"", "graph", graph}, i++);
				if (params.title != null) {
					table.addItem(new Object[] {"", "dct:title", params.title.stringValue()}, i++);
				}
				if (params.label != null) {
					table.addItem(new Object[] {"", "rdfs:label", params.label.stringValue()}, i++);
				}
				if (params.description != null) {
					table.addItem(new Object[] {"", "dct:description", params.description.stringValue()}, i++);
				}
				if (params.comment != null) {
					table.addItem(new Object[] {"", "rdfs:comment", params.comment.stringValue()}, i++);
				}
			}
		}
	}
	private class LabelVisualization implements VisualizeResults {
		private VerticalLayout layout;
		public LabelVisualization(VerticalLayout l){
			layout = l;
		}
		private String formatText(String text, Pattern pattern){
			StringBuilder builder = new StringBuilder();
			Matcher matcher = pattern.matcher(text);
			int lastPosition = 0;
			while (matcher.find()){
				int beginIndex = matcher.start();
				int endIndex = matcher.end();
				builder.append(text.substring(lastPosition, beginIndex));
				builder.append("<b>").append(text.subSequence(beginIndex, endIndex)).append("</b>");
				lastPosition = endIndex;
			}
			builder.append(text.substring(lastPosition));
			return builder.toString();
		}
		public void visualize(Collection<MatchingParams> results, String regex) {
			layout.removeAllComponents();
			Label emptySpace = new Label("");
			emptySpace.setHeight("1.5em");
			layout.addComponent(emptySpace);
			StringBuilder html = new StringBuilder();
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			for (MatchingParams params: results){
				final String ds = params.ds;
				final String graph = params.g;
				
				html.append("<span style=\"font-size:16px;font-weight:bold;color:#007FFF\">");
				html.append(ds);
				html.append("</span><br>");
				Label lbl = new Label(html.toString(), Label.CONTENT_XHTML);
				layout.addComponent(lbl);
				html = new StringBuilder();
				
				HorizontalLayout gLayout = new HorizontalLayout();
				gLayout.setSpacing(true);
				layout.addComponent(gLayout);
				html.append("<span style=\"font-size:14px;\">");
				html.append("<b>Graph: </b></span>");
				Button btn = new Button(graph);
				btn.setStyleName(Reindeer.BUTTON_LINK);
				btn.addStyleName("graph-btn");
				btn.addListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						state.setCurrentGraph(graph);
						getWindow().showNotification("Set default graph: " + graph);
					}
				});
				gLayout.addComponent(new Label(html.toString(), Label.CONTENT_XHTML));
				gLayout.addComponent(btn);
				html = new StringBuilder();
				
				if (params.title != null) {
					html.append("<span style=\"font-size:12px;\">");
					html.append("<b>dct:title: </b>").append(formatText(params.title.stringValue(), pattern));
					html.append("</span><br>");
				}
				if (params.label != null) {
					html.append("<span style=\"font-size:12px;\">");
					html.append("<b>rdfs:label: </b>").append(formatText(params.label.stringValue(), pattern));
					html.append("</span><br>");
				}
				if (params.description != null) {
					html.append("<span style=\"font-size:12px;\">");
					html.append("<b>dct:description: </b>").append(formatText(params.description.stringValue(), pattern));
					html.append("</span><br>");
				}
				if (params.comment != null) {
					html.append("<span style=\"font-size:12px;\">");
					html.append("<b>rdfs:comment: </b>").append(formatText(params.comment.stringValue(), pattern));
					html.append("</span><br>");
				}
				
				layout.addComponent(new Label(html.toString(), Label.CONTENT_XHTML));
				html = new StringBuilder();
				
				emptySpace = new Label("");
				emptySpace.setHeight("1.5em");
				layout.addComponent(emptySpace);
			}
		}
	}
	
	public SearchCubes(LOD2DemoState state){
		this.state = state;
		mainContainer = new VerticalLayout();
		mainContainer.setSizeFull();
		mainContainer.setSpacing(true);
		mainContainer.setMargin(true, false, true, true);
		setCompositionRoot(mainContainer);
	}
	
	private void refresh(){
		mainContainer.removeAllComponents();
		// TODO: draw the component
		HorizontalLayout searchBar = new HorizontalLayout();
		searchBar.setWidth("100%");
		searchBar.setSpacing(true);
		mainContainer.addComponent(searchBar);
		searchBar.addComponent(new Label("Search:"));
		final TextField searchPhrase = new TextField();
		searchPhrase.setWidth("100%");
		searchBar.addComponent(searchPhrase);
		searchBar.setExpandRatio(searchPhrase, 2.0f);
		Button searchButton = new Button("Search");
		searchBar.addComponent(searchButton);
		searchButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String regex = searchPhrase.getValue().toString(); 
				try {
					visualizer.visualize(getMatchingCubes(regex), regex);
				} catch (Exception e) {
					e.printStackTrace();
					getWindow().showNotification("Error", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
		
//		final Table resultsTable = new Table();
//		mainContainer.addComponent(resultsTable);
//		resultsTable.setWidth("100%");
//		resultsTable.addContainerProperty("DataSet", String.class, null);
//		resultsTable.addContainerProperty("Property", String.class, null);
//		resultsTable.addContainerProperty("Value", String.class, null);
//		mainContainer.setExpandRatio(resultsTable, 2.0f);
//		visualizer = new TableVisualization(resultsTable);
		
		VerticalLayout resultsLayout = new VerticalLayout();
		resultsLayout.setSizeFull();
		mainContainer.addComponent(resultsLayout);
		mainContainer.setExpandRatio(resultsLayout, 2.0f);
		visualizer = new LabelVisualization(resultsLayout);
	}
	
	private Collection<MatchingParams> getMatchingCubes(String regex) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		ArrayList<MatchingParams> cubes = new ArrayList<SearchCubes.MatchingParams>();
		RepositoryConnection conn = state.getRdfStore().getConnection();
		TupleQueryResult resultSet = conn.prepareTupleQuery(QueryLanguage.SPARQL, SearchUtils.getMatchingDataSets(regex)).evaluate();
		String lastG = null, lastDS = null;
		while (resultSet.hasNext()){
			BindingSet set = resultSet.next();
			String graph = set.getValue("g").stringValue();
			String dataset = set.getValue("ds").stringValue();
			if (graph.equals(lastG) && dataset.equals(lastDS)) continue;
			
			MatchingParams params = new MatchingParams();
			params.g = lastG = graph;
			params.ds = lastDS = dataset;
			params.title = set.getValue("title");
			params.label = set.getValue("label");
			params.description = set.getValue("description");
			params.comment = set.getValue("comment");
			cubes.add(params);
		}
		Collections.sort(cubes, new ParamsComparator());
		return cubes;
	}

	@Override
	public void attach() {
		super.attach();
		refresh();
	}

}
