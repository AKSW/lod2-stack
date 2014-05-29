package eu.lod2.stat;

import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;
import eu.lod2.LOD2DemoState;
import eu.lod2.utils.SearchUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

public class SearchCubes extends CustomComponent {
	
	private LOD2DemoState state;
	private VerticalLayout mainContainer;
	private VisualizeResults visualizer;
    private TextField searchPhrase;
	
	private class MatchingParams {
		public String g, ds;
                private List<String> titles, labels, descriptions, comments;
                private int mergedScore;
                
                public MatchingParams (){
                    titles = new ArrayList<String>();
                    labels = new ArrayList<String>();
                    descriptions = new ArrayList<String>();
                    comments = new ArrayList<String>();
                    mergedScore = 0;
                }

                public List<String> getTitles() {
                    return titles;
                }
                
                public String getMainTitle(){
                    return titles.get(0);
                }

                public List<String> getLabels() {
                    return labels;
                }
                
                public String getMainLabel(){
                    return labels.get(0);
                }

                public List<String> getDescriptions() {
                    return descriptions;
                }
                
                public String getMainDescription(){
                    return descriptions.get(0);
                }

                public List<String> getComments() {
                    return comments;
                }
                
                public String getMainComment(){
                    return comments.get(0);
                }
                
                public boolean hasTitle(){
                    return titles.size()>0;
                }
                public boolean hasLabel(){
                    return labels.size()>0;
                }
                public boolean hasDescription(){
                    return descriptions.size()>0;
                }
                public boolean hasComment(){
                    return comments.size()>0;
                }
                
		public int calculateScore(){
			int score = 0;
                        score += (titles.size() > 0)?3:0;
			score += (labels.size() > 0)?3:0;
			score += (descriptions.size() > 0)?1:0;
			score += (comments.size() > 0)?1:0;
			return score;
		}
                public int getSortingScore(){
                    return (mergedScore == 0)?calculateScore():mergedScore;
                }
                public void mergeTuple(Value title, Value label,
                        Value description, Value comment){
                    if (title != null && !titles.contains(title.stringValue()))
                        titles.add(title.stringValue());
                    if (label != null && !labels.contains(label.stringValue()))
                        labels.add(label.stringValue());
                    if (description != null && !descriptions.contains(description.stringValue()))
                        descriptions.add(description.stringValue());
                    if (comment != null && !comments.contains(comment.stringValue()))
                        comments.add(comment.stringValue());
                }
                public boolean merge(MatchingParams mp){
                    if (!g.equals(mp.g) || !ds.equals(mp.ds))
                        return false;
                    
                    if (mergedScore == 0) mergedScore = calculateScore();
                    mergedScore += mp.calculateScore();
                    for (String item: mp.getTitles())
                        if (!titles.contains(item)) titles.add(item);
                    for (String item: mp.getLabels())
                        if (!labels.contains(item)) labels.add(item);
                    for (String item: mp.getDescriptions())
                        if (!descriptions.contains(item)) descriptions.add(item);
                    for (String item: mp.getComments())
                        if (!comments.contains(item)) comments.add(item);
                    return true;
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
                                
				if (params.hasTitle()) {
					table.addItem(new Object[] {"", "dct:title", params.getMainTitle()}, i++);
				}
				if (params.hasLabel()) {
					table.addItem(new Object[] {"", "rdfs:label", params.getMainLabel()}, i++);
				}
				if (params.hasDescription()) {
					table.addItem(new Object[] {"", "dct:description", params.getMainDescription()}, i++);
				}
				if (params.hasComment()) {
					table.addItem(new Object[] {"", "rdfs:comment", params.getMainComment()}, i++);
				}
			}
		}
	}
	private class LabelVisualization implements VisualizeResults {
		private VerticalLayout layout;
		public LabelVisualization(VerticalLayout l){
			layout = l;
		}
                private String formatTextKeywords(String text, String keywords){
                    String res = text;    
                    for (String kw: keywords.split("\\s+"))
                        res = formatText(res, kw.trim());
                    return res;
                }
		private String formatText(String text, String regex){
                        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
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
				
				if (params.hasTitle()) {
					html.append("<span style=\"font-size:12px;\">");
					html.append("<b>dct:title: </b>").append(formatTextKeywords(params.getMainTitle(), regex));
					html.append("</span><br>");
				}
				if (params.hasLabel()) {
					html.append("<span style=\"font-size:12px;\">");
					html.append("<b>rdfs:label: </b>").append(formatTextKeywords(params.getMainLabel(), regex));
					html.append("</span><br>");
				}
				if (params.hasDescription()) {
					html.append("<span style=\"font-size:12px;\">");
					html.append("<b>dct:description: </b>").append(formatTextKeywords(params.getMainDescription(), regex));
					html.append("</span><br>");
				}
				if (params.hasComment()) {
					html.append("<span style=\"font-size:12px;\">");
					html.append("<b>rdfs:comment: </b>").append(formatTextKeywords(params.getMainComment(), regex));
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
		searchPhrase = new TextField();
		searchPhrase.setWidth("100%");
		searchBar.addComponent(searchPhrase);
		searchBar.setExpandRatio(searchPhrase, 2.0f);
		Button searchButton = new Button("Search");
		searchBar.addComponent(searchButton);
		
                searchButton.addListener(new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        searchAction();
                    }
		});
                searchPhrase.addShortcutListener(new ShortcutListener("ENTER", ShortcutAction.KeyCode.ENTER, null) { 
                    @Override
                    public void handleAction(Object sender, Object target) {
                        if (target == searchPhrase)
                            searchAction();
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
        
        private void searchAction(){
            String regex = searchPhrase.getValue().toString().trim(); 
            try {
                visualizer.visualize(getMatchingCubesKeywords(regex), regex);
            } catch (Exception e) {
                e.printStackTrace();
                getWindow().showNotification("Error", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
            }
        }
        
        private Collection<MatchingParams> getMatchingCubesKeywords(String keywords) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
            ArrayList<MatchingParams> cubes = new ArrayList<SearchCubes.MatchingParams>();
            for (String kw: keywords.split("\\s+"))
                for (MatchingParams mp: getMatchingCubes(kw.trim())) {
                    boolean newParamsInd = true;
                    for (MatchingParams cube: cubes)
                        if (cube.merge(mp)) {
                            newParamsInd = false;
                            break;
                        }
                    if (newParamsInd) cubes.add(mp);
                }
            Collections.sort(cubes, new ParamsComparator());
            return cubes;   
        }
	
	private Collection<MatchingParams> getMatchingCubes(String regex) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		ArrayList<MatchingParams> cubes = new ArrayList<SearchCubes.MatchingParams>();
		RepositoryConnection conn = state.getRdfStore().getConnection();
		TupleQueryResult resultSet = conn.prepareTupleQuery(QueryLanguage.SPARQL, SearchUtils.getMatchingDataSets(regex)).evaluate();
		String lastG = null, lastDS = null;
                MatchingParams params = new MatchingParams();
		while (resultSet.hasNext()){
			BindingSet set = resultSet.next();
			String graph = set.getValue("g").stringValue();
			String dataset = set.getValue("ds").stringValue();
			if (!graph.equals(lastG) || !dataset.equals(lastDS)) {
                            if (lastG != null) {
                                cubes.add(params);
                            }
//                            cubes.add(params);
                            params = new MatchingParams();
                            params.g = lastG = graph;
                            params.ds = lastDS = dataset;
                        }
                        params.mergeTuple(set.getValue("title"), set.getValue("label"), 
                                set.getValue("description"), set.getValue("comment"));
		}
                if (params.ds != null && params.g != null)
                    cubes.add(params);
		return cubes;
	}

	@Override
	public void attach() {
		super.attach();
		refresh();
	}

}
