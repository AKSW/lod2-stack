package eu.lod2.stat;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

import eu.lod2.ConfigurationTab;
import eu.lod2.LOD2DemoState;
import eu.lod2.utils.ValidationFixUtils;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class Validation extends CustomComponent implements LOD2DemoState.CurrentGraphListener {
	
	private LOD2DemoState state;
	private String testProvenance;
	private String testLinkToDSD;
	private String testLinkToDataSet;
	private ListSelect criteriaList;
	private VerticalLayout validationTab;
	private HorizontalLayout mainContrainer;
	private String testCodesFromCodeLists;
	private String errorMsg;
	private String testNoDuplicateObservations;
	private String testDimensionsRequired;
	private Panel validationPanel;
	private String testMeasuresInDSD;
	private String testDimensionsHaveRange;
	private AbstractLayout target;
	private String testSliceKeysDeclared;
	private String testSliceKeysConsistentWithDSD;
	private String testSliceStructureUnique;
	private String testSliceDimensionsComplete;
	private ThemeResource iconOK;
	private ThemeResource iconError;
	private ThemeResource iconInfo;
	private Tree criteriaTree;
	private IntegrityConstraint icLinkToDSD;
	private IntegrityConstraint icLinkToDataSet;
	private IntegrityConstraint icMeasuresInDSD;
	private IntegrityConstraint icDimensionsHaveRange;
	private IntegrityConstraint icSliceKeysDeclared;
	private IntegrityConstraint icSliceKeysConsistentWithDSD;
	private IntegrityConstraint icSliceStructureUnique;
	private IntegrityConstraint icSliceDimensionsComplete;
	private IntegrityConstraint icCodesFromCodeLists;
	private IntegrityConstraint icDimensionsRequired;
	private IntegrityConstraint icNoDuplicateObservations;
	private String testAttributesOptional;
	private IntegrityConstraint icAttributesOptional;
	private String testRequiredAttributes;
	private IntegrityConstraint icRequiredAttributes;
	private String testAllMeasuresPresent;
	private IntegrityConstraint icAllMeasuresPresent;
	private String testMeasureDimConsistent;
	private IntegrityConstraint icMeasureDimConsistent;
	private String testSingleMeasure;
	private IntegrityConstraint icSingleMeasure;
	private String testAllMeasuresPresentInMeasDimCube;
	private IntegrityConstraint icAllMeasuresPresentInMeasDimCube;
	private String testConsistentDataSetLinks;
	private IntegrityConstraint icConsistentDataSetLinks;
	private VerticalLayout criteriaTab;
	private Panel criteriaPanel;
	private String testDimsHaveCodeLists;
	private IntegrityConstraint icDimsHaveCodeLists;
	
	private String helperGraph;
	
	private interface StatusFunction{
		public Boolean getStatus(Iterator<BindingSet> queryResult);
	}
	
	@SuppressWarnings("unused")
	private class IntegrityConstraint{
//		private String query;
//		private TupleQueryResult res;
		private ICQuery icQuery;
		private List<BindingSet> resList = new LinkedList<BindingSet>();
		private Boolean status = null;
//		private StatusFunction statusFunction;
		
		public IntegrityConstraint(String query){
			this.icQuery = new ICQuerySimple(query);
		}
		public IntegrityConstraint(String query, StatusFunction statusFunction){
			this.icQuery = new ICQuerySimple(query, statusFunction);
		}
		public IntegrityConstraint(ICQuery icQuery){
			this.icQuery = icQuery;
		}
		public void evaluate(){
			resList.clear();
			resList.addAll(icQuery.evaluate());
			status = icQuery.getStatus();
		}
		public Iterator<BindingSet> getResults(){ 
			return resList.iterator();
		}
		public Boolean getStatus(){
			return status;
		}
	}
	
	private abstract class ICQuery {
		protected List<ICQuery> list = new LinkedList<Validation.ICQuery>();
		public abstract List<BindingSet> evaluate();
		public abstract Boolean getStatus();
		public void add(ICQuery q) {}
		public void remove(ICQuery q) {}
	}
	
	private class ICQueryComposite extends ICQuery{
		public void add(ICQuery q) { list.add(q); }
		public void remove(ICQuery q) { list.remove(q); }
		public Boolean getStatus(){
			Boolean res = null;
			for (ICQuery q:list){
				if (q.getStatus() == null) return null;
				if (!q.getStatus().booleanValue()) res = q.getStatus(); 
			}
			if (res != null) return res;
			return true;
		}
		public List<BindingSet> evaluate(){
			List<BindingSet> res = new LinkedList<BindingSet>();
			for (ICQuery q: list) res.addAll(q.evaluate());
			return res;
		}
	}
	
	private class ICQuerySimple extends ICQuery {
		private Boolean status = null;
		private StatusFunction statusFunction;
		private String query;
		private TupleQueryResult res;
		private List<BindingSet> resList = new LinkedList<BindingSet>();
		public ICQuerySimple(String query){
			this.query = query;
			this.statusFunction = new StatusFunction() {
				public Boolean getStatus(Iterator<BindingSet> queryResult) {
					if (queryResult == null) return null;
					if (queryResult.hasNext()) return false;
					return true;
				}
			};
		}
		public ICQuerySimple(String query, StatusFunction statusFunction){
			this.query = query;
			this.statusFunction = statusFunction;
		}
		public List<BindingSet> evaluate(){
			try {
				RepositoryConnection conn = state.getRdfStore().getConnection();
				res = conn.prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
				resList.clear();
				while (res.hasNext()) resList.add(res.next());
				status = statusFunction.getStatus(resList.iterator());
			} catch (RepositoryException e) {
				e.printStackTrace();
			} catch (MalformedQueryException e) {
				e.printStackTrace();
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
			}
			try { if (res!=null) res.close(); } catch (QueryEvaluationException e) {}
			return resList;
		}
		public Boolean getStatus(){
			return status;
		}
	}
	
	private void createTestQueries(){
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("prefix dct: <http://purl.org/dc/terms/> \n");
		strBuilder.append("select ?ds ?label ?comment ?title ?description ?issued ?modified ?subject ?publisher ?licence \n");
		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?ds a qb:DataSet . \n");
		strBuilder.append("  OPTIONAL { ?ds rdfs:label ?label . } \n");
		strBuilder.append("  OPTIONAL { ?ds rdfs:comment ?comment . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:title ?title . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:description ?description . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:issued ?issued . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:modified ?modified . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:subject ?subject . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:publisher ?publisher . } \n");
		strBuilder.append("  OPTIONAL { ?ds dct:licence ?licence . } \n");
		strBuilder.append("}");
		testProvenance = strBuilder.toString();
		
		// IC-2
//		strBuilder = new StringBuilder();
//		strBuilder.append("select ?dataSet (count(?struct) as ?dsdNum) \n");
//		strBuilder.append("from <").append(helperGraph).append("> where { \n");
////		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
//		strBuilder.append("  ?dataSet a <http://purl.org/linked-data/cube#DataSet> . ");
//		strBuilder.append("  OPTIONAL { ");
//		strBuilder.append("    ?dataSet <http://purl.org/linked-data/cube#structure> ?struct . \n");
//		strBuilder.append("    ?struct a <http://purl.org/linked-data/cube#DataStructureDefinition> . \n");
//		strBuilder.append("  } ");
//		strBuilder.append("} group by ?dataSet having (count(?struct) != 1)");
//		testLinkToDSD = strBuilder.toString();
//		icLinkToDSD = new IntegrityConstraint(testLinkToDSD);
		
		// IC-1
//		strBuilder = new StringBuilder();
//		strBuilder.append("select ?obs (count(?dataSet) as ?dsNum) \n");
//		strBuilder.append("from <").append(helperGraph).append("> where { \n");
////		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
//		strBuilder.append("  ?obs a <http://purl.org/linked-data/cube#Observation> . ");
//		strBuilder.append("  OPTIONAL { ");
//		strBuilder.append("    ?obs <http://purl.org/linked-data/cube#dataSet> ?dataSet . \n");
//		strBuilder.append("    ?dataSet a <http://purl.org/linked-data/cube#DataSet> . \n");
//		strBuilder.append("  } ");
//		strBuilder.append("} group by ?obs having (count(?dataSet) != 1)");
//		testLinkToDataSet = strBuilder.toString();
//		icLinkToDataSet = new IntegrityConstraint(testLinkToDataSet);
		
		// TODO sort out the problem with count(distinct)
		// IC-1
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?obs ?dsNum \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  { SELECT DISTINCT ?obs (0 as ?dsNum) WHERE { ?obs a qb:Observation . FILTER NOT EXISTS { ?obs qb:dataSet ?ds . ?ds a qb:DataSet . } } } \n");
		strBuilder.append("  UNION \n");
		strBuilder.append("  { SELECT ?obs (count(distinct ?ds) as ?dsNum) { ?obs a qb:Observation . ?obs qb:dataSet ?ds . ?ds a qb:DataSet . } group by ?obs } \n");
		strBuilder.append("  FILTER (?dsNum != 1) \n");
		strBuilder.append("}");
		testLinkToDataSet = strBuilder.toString();
		icLinkToDataSet = new IntegrityConstraint(testLinkToDataSet);
		// IC-2
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?dataSet ?dsdNum \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  { SELECT DISTINCT ?dataSet (0 as ?dsdNum) WHERE { ?dataSet a qb:DataSet . FILTER NOT EXISTS { ?dataSet qb:structure ?dsd . ?dsd a qb:DataStructureDefinition . } } } \n");
		strBuilder.append("  UNION \n");
		strBuilder.append("  { SELECT ?dataSet (count(distinct ?dsd) as ?dsdNum) { ?dataSet a qb:DataSet . ?dataSet qb:structure ?dsd . ?dsd a qb:DataStructureDefinition . } group by ?dataSet } \n");
		strBuilder.append("  FILTER (?dsdNum != 1) \n");
		strBuilder.append("}");
		testLinkToDSD = strBuilder.toString();
		icLinkToDSD = new IntegrityConstraint(testLinkToDSD);
		
		// IC-3
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?dsd \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dsd a qb:DataStructureDefinition . \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?dsd qb:component ?cs . \n");
		strBuilder.append("    ?cs qb:componentProperty ?prop . \n");
		strBuilder.append("    ?prop a qb:MeasureProperty . \n");
		strBuilder.append("  } \n");
		strBuilder.append("}");
		testMeasuresInDSD = strBuilder.toString();
		icMeasuresInDSD = new IntegrityConstraint(testMeasuresInDSD);
		
		// IC-4
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?dim \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dim a qb:DimensionProperty . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?dim rdfs:range [] . } \n");
		strBuilder.append("}");
		testDimensionsHaveRange = strBuilder.toString();
		icDimensionsHaveRange = new IntegrityConstraint(testDimensionsHaveRange);
		
		// IC-6
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select distinct ?dsd ?componentSpec ?component \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dsd qb:component ?componentSpec . \n");
		strBuilder.append("  ?componentSpec qb:componentRequired \"false\"^^xsd:boolean . \n");
		strBuilder.append("  ?componentSpec qb:componentProperty ?component . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?component a qb:AttributeProperty } \n");
		strBuilder.append("}");
		testAttributesOptional = strBuilder.toString();
		icAttributesOptional = new IntegrityConstraint(testAttributesOptional);
		
		// IC-7
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?sliceKey \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?sliceKey a qb:SliceKey . \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?dsd a qb:DataStructureDefinition . \n");
		strBuilder.append("    ?dsd qb:sliceKey ?sliceKey . \n");
		strBuilder.append("  } \n");
		strBuilder.append("}");
		testSliceKeysDeclared = strBuilder.toString();
		icSliceKeysDeclared = new IntegrityConstraint(testSliceKeysDeclared);
		
		// IC-8
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?sliceKey \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?sliceKey a qb:SliceKey . \n");
		strBuilder.append("  ?sliceKey qb:componentProperty ?prop . \n");
		strBuilder.append("  ?dsd qb:sliceKey ?sliceKey . \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?dsd qb:component ?cs . \n");
		strBuilder.append("    ?cs qb:componentProperty ?prop . \n");
		strBuilder.append("  } \n");
		strBuilder.append("}");
		testSliceKeysConsistentWithDSD = strBuilder.toString();
		icSliceKeysConsistentWithDSD = new IntegrityConstraint(testSliceKeysConsistentWithDSD);
		
		// IC-9
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select distinct ?slice \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  { \n");
		strBuilder.append("    ?slice a qb:Slice . \n");
		strBuilder.append("    FILTER NOT EXISTS { ?slice qb:sliceStructure ?key } \n");
		strBuilder.append("  } UNION { \n");
		strBuilder.append("    ?slice a qb:Slice . \n");
		strBuilder.append("    ?slice qb:sliceStructure ?key1 . \n");
		strBuilder.append("    ?slice qb:sliceStructure ?key2 . \n");
		strBuilder.append("    FILTER (?key1 != ?key2) \n");
		strBuilder.append("  } \n");
		strBuilder.append("}");
		testSliceStructureUnique = strBuilder.toString();
		icSliceStructureUnique = new IntegrityConstraint(testSliceStructureUnique);
		
		// IC-10
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select ?slice ?dim \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?slice qb:sliceStructure ?key . \n");
		strBuilder.append("  ?key qb:componentProperty ?dim . \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?slice ?dim ?val . \n");
		strBuilder.append("  } \n");
		strBuilder.append("} order by ?slice");
		testSliceDimensionsComplete = strBuilder.toString();
		icSliceDimensionsComplete = new IntegrityConstraint(testSliceDimensionsComplete);
		
		// IC-5
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		strBuilder.append("select distinct ?dim \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dim a qb:DimensionProperty . \n");
		strBuilder.append("  ?dim rdfs:range skos:Concept . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?dim qb:codeList [] } \n");
		strBuilder.append("}");
		testDimsHaveCodeLists = strBuilder.toString();
		icDimsHaveCodeLists = new IntegrityConstraint(testDimsHaveCodeLists);
		
		// IC-11
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		strBuilder.append("select distinct ?obs \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs qb:dataSet ?ds . \n");
		strBuilder.append("  ?ds qb:structure ?dsd . \n");
		strBuilder.append("  ?dsd qb:component ?cs . \n");
		strBuilder.append("  ?cs qb:componentProperty ?dim . \n");
		strBuilder.append("  ?dim a qb:DimensionProperty . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?obs ?dim [] } \n");
		strBuilder.append("}");
		testDimensionsRequired = strBuilder.toString();
		icDimensionsRequired = new IntegrityConstraint(testDimensionsRequired);
		
		// IC-12
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		strBuilder.append("select distinct ?obs1 ?obs2 \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs1 qb:dataSet ?dataSet . \n");
		strBuilder.append("  ?obs2 qb:dataSet ?dataSet . \n");
		strBuilder.append("  FILTER (?obs1 != ?obs2) \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?dataSet qb:structure ?dsd . \n");
		strBuilder.append("    ?dsd qb:component ?cs . \n");
		strBuilder.append("    ?cs qb:componentProperty ?dim . \n");
		strBuilder.append("    ?dim a qb:DimensionProperty . \n");
		strBuilder.append("    ?obs1 ?dim ?val1 . \n");
		strBuilder.append("    ?obs2 ?dim ?val2 . \n");
		strBuilder.append("    FILTER (?val1 != ?val2) \n");
		strBuilder.append("  } \n");
		strBuilder.append("} order by ?obs1");
		testNoDuplicateObservations = strBuilder.toString();
		icNoDuplicateObservations = new IntegrityConstraint(testNoDuplicateObservations);
		
		// IC-13
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select distinct ?obs ?attr \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs qb:dataSet ?ds . \n");
		strBuilder.append("  ?ds qb:structure ?dsd . \n");
		strBuilder.append("  ?dsd qb:component ?component . \n");
		strBuilder.append("  ?component qb:componentRequired \"true\"^^xsd:boolean . \n");
		strBuilder.append("  ?component qb:componentProperty ?attr . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?obs ?attr [] } \n");
		strBuilder.append("}");
		testRequiredAttributes = strBuilder.toString();
		icRequiredAttributes = new IntegrityConstraint(testRequiredAttributes);
		
		// IC-14
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select distinct ?obs ?measure \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs qb:dataSet ?ds . \n");
		strBuilder.append("  ?ds qb:structure ?dsd . \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?dsd qb:component ?cs0 . \n");
		strBuilder.append("    ?cs0 qb:componentProperty qb:measureType . \n");
		strBuilder.append("  } \n");
		strBuilder.append("  ?dsd qb:component ?cs . \n");
		strBuilder.append("  ?cs qb:componentProperty ?measure . \n");
		strBuilder.append("  ?measure a qb:MeasureProperty . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?obs ?measure [] } \n");
		strBuilder.append("}");
		testAllMeasuresPresent = strBuilder.toString();
		icAllMeasuresPresent = new IntegrityConstraint(testAllMeasuresPresent);
		
		// IC-15
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select distinct ?obs ?measure \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs qb:dataSet ?ds . \n");
		strBuilder.append("  ?ds qb:structure ?dsd . \n");
		strBuilder.append("  ?obs qb:measureType ?measure . \n");
		strBuilder.append("  ?dsd qb:component ?cs . \n");
		strBuilder.append("  ?component qb:componentProperty qb:measureType . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?obs ?measure [] } \n");
		strBuilder.append("}");
		testMeasureDimConsistent = strBuilder.toString();
		icMeasureDimConsistent = new IntegrityConstraint(testMeasureDimConsistent);
		
		// IC-16
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select distinct ?obs ?measure ?omeasure \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs qb:dataSet ?ds . \n");
		strBuilder.append("  ?ds qb:structure ?dsd . \n");
		strBuilder.append("  ?obs qb:measureType ?measure . \n");
		strBuilder.append("  ?obs ?omeasure [] . \n");
		strBuilder.append("  ?dsd qb:component ?cs1 . \n");
		strBuilder.append("  ?cs1 qb:componentProperty qb:measureType . \n");
		strBuilder.append("  ?dsd qb:component ?cs2 . \n");
		strBuilder.append("  ?cs2 qb:componentProperty ?omeasure . \n");
		strBuilder.append("  ?omeasure a qb:MeasureProperty . \n");
		strBuilder.append("  FILTER (?omeasure != ?measure) \n");
		strBuilder.append("}");
		testSingleMeasure = strBuilder.toString();
		icSingleMeasure = new IntegrityConstraint(testSingleMeasure);
		
		// IC-17
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select distinct ?obs1 ?numMeasures (COUNT(?obs2) AS ?count) \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  { \n");
		strBuilder.append("    SELECT ?dsd (COUNT(?m) AS ?numMeasures) WHERE { \n");
		strBuilder.append("      ?dsd qb:component ?cs0 . \n");
		strBuilder.append("      ?cs0 qb:componentProperty ?m . \n");
		strBuilder.append("      ?m a qb:MeasureProperty . \n");
		strBuilder.append("    } GROUP BY ?dsd \n");
		strBuilder.append("  } \n");
		strBuilder.append("  ?obs1 qb:dataSet ?dataset . \n");
		strBuilder.append("  ?dataset qb:structure ?dsd . \n");
		strBuilder.append("  ?obs1 qb:measureType ?m1 . \n");
		strBuilder.append("  ?obs2 qb:dataSet ?dataset . \n");
		strBuilder.append("  ?obs2 qb:measureType ?m2 . \n");
		strBuilder.append("  FILTER NOT EXISTS { \n");
		strBuilder.append("    ?dsd qb:component ?cs1 . \n");
		strBuilder.append("    ?cs1 qb:componentProperty ?dim . \n");
		strBuilder.append("    FILTER (?dim != qb:measureType) \n");
		strBuilder.append("    ?dim a qb:DimensionProperty . \n");
		strBuilder.append("    ?obs1 ?dim ?v1 . \n");
		strBuilder.append("    ?obs2 ?dim ?v2 . \n");
		strBuilder.append("    FILTER (?v1 != ?v2) \n");
		strBuilder.append("  } \n");
		strBuilder.append("} GROUP BY ?obs1 ?numMeasures \n  HAVING (COUNT(?obs2) != ?numMeasures)");
		testAllMeasuresPresentInMeasDimCube = strBuilder.toString();
		icAllMeasuresPresentInMeasDimCube = new IntegrityConstraint(testAllMeasuresPresentInMeasDimCube);
		
		// IC-18
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("select distinct ?obs ?dataset ?slice \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?dataset qb:slice ?slice . \n");
		strBuilder.append("  ?slice   qb:observation ?obs . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?obs qb:dataSet ?dataset . } \n");
		strBuilder.append("}");
		testConsistentDataSetLinks = strBuilder.toString();
		icConsistentDataSetLinks = new IntegrityConstraint(testConsistentDataSetLinks);
		
		// IC-19
		strBuilder = new StringBuilder();
		strBuilder.append("prefix qb: <http://purl.org/linked-data/cube#> \n");
		strBuilder.append("prefix skos: <http://www.w3.org/2004/02/skos/core#> \n");
		strBuilder.append("select distinct ?dim ?v ?list \n");
		strBuilder.append("from <").append(helperGraph).append("> where { \n");
//		strBuilder.append("from <").append(state.getCurrentGraph()).append("> \n where { \n");
		strBuilder.append("  ?obs qb:dataSet ?ds . \n");
		strBuilder.append("  ?ds qb:structure ?dsd . \n");
		strBuilder.append("  ?dsd qb:component ?cs . \n");
		strBuilder.append("  ?cs qb:componentProperty ?dim . \n");
		strBuilder.append("  ?dim a qb:DimensionProperty . \n");
		strBuilder.append("  ?dim qb:codeList ?list . \n");
		strBuilder.append("  ?list a skos:ConceptScheme . \n");
		strBuilder.append("  ?obs ?dim ?v . \n");
		strBuilder.append("  FILTER NOT EXISTS { ?v a skos:Concept . ?v skos:inScheme ?list . } \n");
		strBuilder.append("}");
		testCodesFromCodeLists = strBuilder.toString();
		icCodesFromCodeLists = new IntegrityConstraint(testCodesFromCodeLists);
	}
	
	public Validation(LOD2DemoState state, AbstractLayout target){
		this.state = state;
		this.target = target;
		this.iconOK = new ThemeResource("icons/thumbs_up_color.png");
		this.iconError = new ThemeResource("icons/thumbs_down_color.png");
		this.iconInfo = new ThemeResource("icons/comments_color.png");
		mainContrainer = new HorizontalLayout();
//		mainContrainer = new HorizontalSplitPanel();
		mainContrainer.setSizeUndefined();
		mainContrainer.setSpacing(true);
		setCompositionRoot(mainContrainer);
	}
	
	private void refresh(){
		mainContrainer.removeAllComponents();
		String currentGraph = state.getCurrentGraph();
	    if (currentGraph == null || currentGraph.isEmpty()){
	    	VerticalLayout l = new VerticalLayout();
	    	l.setSizeFull();
	    	mainContrainer.addComponent(l);
	        Label message=new Label("No graph is currently selected. You can select one below:");
	        l.addComponent(message);
	        l.setExpandRatio(message, 0.0f);
	        ConfigurationTab config=new ConfigurationTab(this.state);
	        l.addComponent(config);
	        l.setExpandRatio(config, 2.0f);
	        l.setComponentAlignment(message,Alignment.TOP_LEFT);
	        l.setComponentAlignment(config,Alignment.TOP_LEFT);
	
	        return;
	    }
	    
	    createHelperGraph();
	    createTestQueries();
	    createGUI();
	    removeHelperGraph();
	}
	
	private void executeHelperInsertQuery(RepositoryConnection conn, String insertString, String whereString) 
			throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		String curGraph = "<" + state.getCurrentGraph() + ">";
		StringBuilder builder = new StringBuilder();
		final String insertIntoPart = builder.append("INSERT INTO GRAPH <").append(helperGraph).append("> { \n").toString();
		builder = new StringBuilder();
		final String wherePart = builder.append("} WHERE { GRAPH ").append(curGraph).append(" { \n").toString();
		final String endingPart = "}}";
		
		builder = new StringBuilder();
		builder.append("PREFIX qb: <http://purl.org/linked-data/cube#> \n");
		builder.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		builder.append(insertIntoPart);
		builder.append(insertString);
		builder.append(wherePart);
		builder.append(whereString);
		builder.append(endingPart);
		
		conn.prepareGraphQuery(QueryLanguage.SPARQL, builder.toString()).evaluate();
	}
	
	private void createHelperGraph(){
		helperGraph = "http://localhost:8080/lod2statworkbench/validationHelper/" + this.hashCode() + "/";
		try {
			RepositoryConnection conn = state.getRdfStore().getConnection();
			conn.prepareGraphQuery(QueryLanguage.SPARQL, "DROP SILENT GRAPH <" + helperGraph + ">").evaluate();
			conn.prepareGraphQuery(QueryLanguage.SPARQL, "CREATE SILENT GRAPH <" + helperGraph + ">").evaluate();
			
			executeHelperInsertQuery(conn, "  ?o rdf:type qb:Observation . \n", 
					"  [] qb:observation ?o . \n");
			executeHelperInsertQuery(conn, "  ?o rdf:type qb:Observation . \n", 
					"  ?o qb:dataSet [] . \n");
			executeHelperInsertQuery(conn, "  ?s rdf:type qb:Slice . \n", 
					"  [] qb:slice ?s. \n");
			executeHelperInsertQuery(conn, "  ?cs qb:componentProperty ?p . \n  ?p  rdf:type qb:DimensionProperty . \n", 
					"  ?cs qb:dimension ?p . \n");
			executeHelperInsertQuery(conn, "  ?cs qb:componentProperty ?p . \n  ?p  rdf:type qb:MeasureProperty . \n", 
					"  ?cs qb:measure ?p . \n");
			executeHelperInsertQuery(conn, "  ?cs qb:componentProperty ?p . \n  ?p  rdf:type qb:AttributeProperty . \n", 
					"  ?cs qb:attribute ?p . \n");
			
			final String insertString = "  ?obs  ?comp ?value . \n";
			// Data set attachments
			StringBuilder whereBuilder = new StringBuilder();
			whereBuilder.append("  ?spec qb:componentProperty ?comp . \n");
			whereBuilder.append("  ?spec qb:componentAttachment qb:DataSet . \n");
			whereBuilder.append("  ?dataset qb:structure ?dsd . \n");
			whereBuilder.append("  ?dsd qb:component ?spec . \n");
			whereBuilder.append("  ?dataset ?comp ?value . \n");
			whereBuilder.append("  ?obs qb:dataSet ?dataset . \n");
			executeHelperInsertQuery(conn, insertString, whereBuilder.toString());
			// Slice attachments
			whereBuilder = new StringBuilder();
			whereBuilder.append("  ?spec qb:componentProperty ?comp . \n");
			whereBuilder.append("  ?spec qb:componentAttachment qb:Slice . \n");
			whereBuilder.append("  ?dataset qb:structure ?dsd . \n");
			whereBuilder.append("  ?dsd qb:component ?spec . \n");
			whereBuilder.append("  ?dataset qb:slice ?slice . \n");
			whereBuilder.append("  ?slice ?comp ?value . \n");
			whereBuilder.append("  ?slice qb:observation ?obs . \n");
			executeHelperInsertQuery(conn, insertString, whereBuilder.toString());
			// Dimension values on slices
			whereBuilder = new StringBuilder();
			whereBuilder.append("  ?spec qb:componentProperty ?comp . \n");
			whereBuilder.append("  ?comp a  qb:DimensionProperty . \n");
			whereBuilder.append("  ?dataset qb:structure ?dsd . \n");
			whereBuilder.append("  ?dsd qb:component ?spec . \n");
			whereBuilder.append("  ?dataset qb:slice ?slice . \n");
			whereBuilder.append("  ?slice ?comp ?value . \n");
			whereBuilder.append("  ?slice qb:observation ?obs . \n");
			executeHelperInsertQuery(conn, insertString, whereBuilder.toString());
			// Copy graph
			whereBuilder = new StringBuilder();
			whereBuilder.append("  ?obs  ?comp ?value . \n");
			executeHelperInsertQuery(conn, insertString, whereBuilder.toString());
		} catch (RepositoryException e) {
			e.printStackTrace();
			getWindow().showNotification(e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
			getWindow().showNotification(e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
			getWindow().showNotification(e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void removeHelperGraph(){
		try {
			RepositoryConnection conn = state.getRdfStore().getConnection();
			conn.prepareGraphQuery(QueryLanguage.SPARQL, "DROP SILENT GRAPH <" + helperGraph + ">").evaluate();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
	}
	
	private void createGUI(){
		criteriaList = new ListSelect("Validation criteria");
		criteriaList.setNullSelectionAllowed(false);
		criteriaList.setHeight("400px");
//		final Object itemSummary = criteriaList.addItem();
//		criteriaList.setItemCaption(itemSummary, "Summary");
//		final Object itemProvenance = criteriaList.addItem();
//		criteriaList.setItemCaption(itemProvenance, "Provenance information");
//		final Object itemObsLinks = criteriaList.addItem();
//		criteriaList.setItemCaption(itemObsLinks, "Observations linked to DataSets");
//		final Object itemDataSetLinks = criteriaList.addItem();
//		criteriaList.setItemCaption(itemDataSetLinks, "DataSets linked to DSDs");
//		final Object itemMeasuresInDSDs = criteriaList.addItem();
//		criteriaList.setItemCaption(itemMeasuresInDSDs, "Measures in DSDs");
//		final Object itemDimensionsHaveRange = criteriaList.addItem();
//		criteriaList.setItemCaption(itemDimensionsHaveRange, "Dimensions have range");
//		final Object itemDimDefined = criteriaList.addItem();
//		criteriaList.setItemCaption(itemDimDefined, "Dimensions - codes from code lists");
//		final Object itemSliceKeysDeclared = criteriaList.addItem();
//		criteriaList.setItemCaption(itemSliceKeysDeclared, "Slice keys declared");
//		final Object itemSliceKeysConsistent = criteriaList.addItem();
//		criteriaList.setItemCaption(itemSliceKeysConsistent, "Slice keys consistent");
//		final Object itemSliceStructureUnique = criteriaList.addItem();
//		criteriaList.setItemCaption(itemSliceStructureUnique, "Slice structure unique");
//		final Object itemSliceDimensionsComplete = criteriaList.addItem();
//		criteriaList.setItemCaption(itemSliceDimensionsComplete, "Slice dimensions complete");
//		final Object itemDimReq = criteriaList.addItem();
//		criteriaList.setItemCaption(itemDimReq, "All dimensions required");
//		final Object itemObsUnique = criteriaList.addItem();
//		criteriaList.setItemCaption(itemObsUnique, "No duplicate observations");
		
		criteriaTree = new Tree("Validation criteria");
		criteriaTree.setNullSelectionAllowed(false);
		criteriaTree.setImmediate(true);
		final Object itemSummary = criteriaTree.addItem();
		criteriaTree.setItemCaption(itemSummary, "Summary");
		criteriaTree.setChildrenAllowed(itemSummary, false);
		criteriaTree.setItemIcon(itemSummary, iconInfo);
		final Object itemProvenance = criteriaTree.addItem();
		criteriaTree.setItemCaption(itemProvenance, "Provenance information");
		criteriaTree.setChildrenAllowed(itemProvenance, false);
		criteriaTree.setItemIcon(itemProvenance, iconInfo);
		final Object itemObsLinks = createTreeItem("IC-1 Unique DataSet", icLinkToDataSet);
		final Object itemDataSetLinks = createTreeItem("IC-2 Unique DSD", icLinkToDSD);
		final Object itemMeasuresInDSDs = createTreeItem("IC-3 DSD includes measure", icMeasuresInDSD);
		final Object itemDimensionsHaveRange = createTreeItem("IC-4 Dimensions have range", icDimensionsHaveRange);
		final Object itemDimDefined = createTreeItem("IC-5 Concept dimensions have code lists", icDimsHaveCodeLists);
		final Object itemAttributesOptional = createTreeItem("IC-6 Only attributes may be optional", icAttributesOptional);
		final Object itemSliceKeysDeclared = createTreeItem("IC-7 Slice Keys must be declared", icSliceKeysDeclared);
		final Object itemSliceKeysConsistent = createTreeItem("IC-8 Slice Keys consistent with DSD", icSliceKeysConsistentWithDSD);
		final Object itemSliceStructureUnique = createTreeItem("IC-9 Unique slice structure", icSliceStructureUnique);
		final Object itemSliceDimensionsComplete = createTreeItem("IC-10 Slice dimensions complete", icSliceDimensionsComplete);
		final Object itemDimReq = createTreeItem("IC-11 All dimensions required", icDimensionsRequired);
		final Object itemObsUnique = createTreeItem("IC-12 No duplicate observations", icNoDuplicateObservations);
		final Object itemRequiredAttributes = createTreeItem("IC-13 Required attributes", icRequiredAttributes);
		final Object itemAllMeasuresPresent = createTreeItem("IC-14 All measures present", icAllMeasuresPresent);
		final Object itemMeasureDimConsistent = createTreeItem("IC-15 Measure dimension consistent", icMeasureDimConsistent);
		final Object itemSingleMeasure = createTreeItem("IC-16 Single measure", icSingleMeasure);
		final Object itemAllMeasuresPresentInMeasDimCube = createTreeItem("IC-17 All measures present in meas. dim. cube ", icAllMeasuresPresentInMeasDimCube);
		final Object itemConsistentDataSetLinks = createTreeItem("IC-18 Consistent data set links", icConsistentDataSetLinks);
		final Object itemCodesFromCodeList = createTreeItem("IC-19 Codes from code list", icCodesFromCodeLists);
		
		criteriaTree.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object selectedItem = event.getProperty().getValue();
				if (selectedItem == itemSummary)
					summary();
				else if (selectedItem == itemProvenance)
					provenance();
				else if (selectedItem == itemObsLinks)
					observationLinks();
				else if (selectedItem == itemDataSetLinks)
					dataSetLinks();
				else if (selectedItem == itemMeasuresInDSDs)
					measuresInDSD();
				else if (selectedItem == itemDimensionsHaveRange)
					dimensionsHaveRange();
				else if (selectedItem == itemSliceKeysDeclared)
					sliceKeysDeclared();
				else if (selectedItem == itemSliceKeysConsistent)
					sliceKeysConsistentWithDSD();
				else if (selectedItem == itemSliceStructureUnique)
					sliceStructureUnique();
				else if (selectedItem == itemSliceDimensionsComplete)
					sliceDimensionsComplete();
				else if (selectedItem == itemDimDefined)
					dimensionDefinitions();
				else if (selectedItem == itemAttributesOptional)
					attributesOptional();
				else if (selectedItem == itemDimReq)
					dimensionsRequired();
				else if (selectedItem == itemObsUnique)
					noDuplicateObs();
				else if (selectedItem == itemRequiredAttributes)
					requiredAttributes();
				else if (selectedItem == itemAllMeasuresPresent)
					allMeasuresPresent();
				else if (selectedItem == itemMeasureDimConsistent)
					measureDimConsistent();
				else if (selectedItem == itemSingleMeasure)
					singleMeasure();
				else if (selectedItem == itemAllMeasuresPresentInMeasDimCube)
					allMeasuresPresentInMeasDimCube();
				else if (selectedItem == itemConsistentDataSetLinks)
					consistentDataSetLinks();
				else if (selectedItem == itemCodesFromCodeList)
					codesFromCodeList();
				else {
					summary();
				}
			}
		});
		
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		splitPanel.setImmediate(true);
		splitPanel.setSizeFull();
		
		validationTab = new VerticalLayout();
		validationTab.setMargin(false, false, false, true);
		validationTab.setSpacing(true);
		validationPanel = new Panel(validationTab);
		validationPanel.setSizeFull();
		validationPanel.setScrollable(true);
		validationPanel.setStyleName(Reindeer.PANEL_LIGHT);
		
		criteriaTab = new VerticalLayout();
		criteriaTab.setMargin(false);
		criteriaTab.setSpacing(true);
		criteriaTab.addComponent(criteriaTree);
		criteriaPanel = new Panel(criteriaTab);
		criteriaPanel.setSizeFull();
		criteriaPanel.setScrollable(true);
		criteriaPanel.setStyleName(Reindeer.PANEL_LIGHT);
		
//		mainContrainer.addComponent(criteriaPanel);
//		mainContrainer.setExpandRatio(criteriaPanel, 0.0f);
//		mainContrainer.addComponent(validationPanel);
//		mainContrainer.setExpandRatio(validationPanel, 2.0f);
		
		splitPanel.setFirstComponent(criteriaPanel);
		splitPanel.setSecondComponent(validationPanel);
		splitPanel.setSplitPosition(320, Sizeable.UNITS_PIXELS);
		mainContrainer.addComponent(splitPanel);
		mainContrainer.setExpandRatio(splitPanel, 2.0f);
		mainContrainer.setSizeFull();
		
//		criteriaList.setImmediate(true);
//		criteriaList.addListener(new Property.ValueChangeListener() {
//			public void valueChange(ValueChangeEvent event) {
//				Object selectedItem = event.getProperty().getValue();
//				if (selectedItem == itemSummary)
//					summary();
//				else if (selectedItem == itemProvenance)
//					provenance();
//				else if (selectedItem == itemObsLinks)
//					observationLinks();
//				else if (selectedItem == itemDataSetLinks)
//					dataSetLinks();
//				else if (selectedItem == itemMeasuresInDSDs)
//					measuresInDSD();
//				else if (selectedItem == itemDimensionsHaveRange)
//					dimensionsHaveRange();
//				else if (selectedItem == itemSliceKeysDeclared)
//					sliceKeysDeclared();
//				else if (selectedItem == itemSliceKeysConsistent)
//					sliceKeysConsistentWithDSD();
//				else if (selectedItem == itemSliceStructureUnique)
//					sliceStructureUnique();
//				else if (selectedItem == itemSliceDimensionsComplete)
//					sliceDimensionsComplete();
//				else if (selectedItem == itemDimDefined)
//					dimensionDefinitions();
//				else if (selectedItem == itemDimReq)
//					dimensionsRequired();
//				else if (selectedItem == itemObsUnique)
//					noDuplicateObs();
//				else {
//					summary();
//				}
//			}
//		});
	}
	
	private Object createTreeItem(String caption, IntegrityConstraint ic){
		final Object itemObject = criteriaTree.addItem();
		criteriaTree.setItemCaption(itemObject, caption);
		criteriaTree.setChildrenAllowed(itemObject, false);
		ic.evaluate();
		if (ic.getStatus() != null && ic.getStatus().booleanValue())
			criteriaTree.setItemIcon(itemObject, iconOK);
		else 
			criteriaTree.setItemIcon(itemObject, iconError);
		return itemObject;
	}
	
	private void summary(){
		validationTab.removeAllComponents();
		Label label = new Label("", Label.CONTENT_XHTML);
		List<String> obsList = getObservations();
		List<String> dsList = getDataSets();
		List<String> dsdList = getDataStructureDefinitions();
		StringBuilder sb = new StringBuilder();
		sb.append("<h2>Summary</h2>");
		sb.append("This page contains summary information about the working graph, i.e. ");
		sb.append("number of observations, data sets, DSDs, dimensions, etc. ");
		sb.append("Therefore, this page only detects if some resources are missing, for more information, e.g. ");
		sb.append(" missing links refer to other validation criteria.");
		sb.append("<p>Summary information: <ul><li>");
		if (obsList.size() == 0) sb.append("ERROR - the graph is missing observations");
		else sb.append("There are ").append(obsList.size()).append(" observations");
		sb.append("</li><li>");
		if (dsList.size() == 0) sb.append("ERROR - the graph is missing data sets");
		else sb.append("There are ").append(dsList.size()).append(" data sets");
		sb.append("</li><li>");
		if (dsdList.size() == 0) sb.append("ERROR - the graph is missing data structure definitions");
		else sb.append("There are ").append(dsdList.size()).append(" data structure definitions");
		sb.append("</li></ul></p>");
		sb.append("<p>TODO: add info about dimensions, maybe include pointers on cubeviz possibilities</p>");
		label.setValue(sb.toString());
		validationTab.addComponent(label);
		showContent();
	}
	
	private void provenance(){
		validationTab.removeAllComponents();
		final String [] metaProps = new String [] {
				"rdfs:label",
				"rdfs:comment",
				"dct:title",
				"dct:description",
				"dct:issued",
				"dct:modified",
				"dct:subject",
				"dct:publisher",
				"dct:licence"
		};
		
		final Label label = new Label("It is recommended to mark datasets with metadata tu support discovery, presentation and processing. Choose a dataset below and check the values for recommended core set of metadata",Label.CONTENT_TEXT);
		validationTab.addComponent(label);
		
		TupleQueryResult res = executeTupleQuery(testProvenance);
		if (res == null) {
			label.setValue("ERROR");
			showContent();
			return;
		}
		final HashMap<String, ArrayList<Value>> map = new HashMap<String, ArrayList<Value>>();
		try {
			while (res.hasNext()){
				BindingSet set = res.next();
				String ds = set.getValue("ds").stringValue();
				ArrayList<Value> values = new ArrayList<Value>(9);
				values.add(set.getValue("label"));
				values.add(set.getValue("comment"));
				values.add(set.getValue("title"));
				values.add(set.getValue("description"));
				values.add(set.getValue("issued"));
				values.add(set.getValue("modified"));
				values.add(set.getValue("subject"));
				values.add(set.getValue("publisher"));
				values.add(set.getValue("licence"));
				map.put(ds, values);
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		final ComboBox combo = new ComboBox("Choose dataset", map.keySet());
		combo.setWidth("100%");
		combo.setNullSelectionAllowed(false);
		combo.setImmediate(true);
		validationTab.addComponent(combo);
		
		final Table table = new Table("Metadata of the chosen dataset");
		table.setWidth("100%");
		table.addContainerProperty("Property", String.class, null);
		table.addContainerProperty("Value", Value.class, null);
		validationTab.addComponent(table);
		
		Button editInOW = new Button("Edit in OntoWiki");
		validationTab.addComponent(editInOW);
		validationTab.setExpandRatio(editInOW, 2.0f);
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)combo.getValue());
			}
		});
		
		combo.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				ArrayList<Value> list = map.get((String)event.getProperty().getValue());
				table.removeAllItems();
				for (int i=0; i<metaProps.length; i++)
					table.addItem(new Object [] { metaProps[i], list.get(i) }, i);
			}
		});
		
		showContent();
	}
	
	// IC-1 layout
	private void observationLinks(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icLinkToDataSet.getResults();
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, String> map = new HashMap<String, String>();
		
		while (res.hasNext()){
			BindingSet set = res.next();
			map.put(set.getValue("obs").stringValue(), set.getValue("dsNum").stringValue());
		}
		
		if (map.size() == 0){
			Label label = new Label();
			label.setValue("All observations have links to data sets");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Below is the list of observations that are not linked to exactly one data set. Click on any of them to get more information and either edit the resource in OntoWiki or choose a quick solution");
		validationTab.addComponent(label);
		
		final ListSelect listObs = new ListSelect("Observations", map.keySet());
		listObs.setNullSelectionAllowed(false);
		validationTab.addComponent(listObs);
		listObs.setImmediate(true);
		listObs.setWidth("100%");
		
		final Table detailsTable = new Table("Details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		final Label lblProblem = new Label("<b>Problem description: </b>", Label.CONTENT_XHTML);
		validationTab.addComponent(lblProblem);
		
		Button editInOW = new Button("Edit in OntoWiki");
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listObs.getValue());
			}
		});
		
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		panelLayout.addComponent(new Label("After the fix the selected observation will belong only to the data set selected below or you can choose to edit the selected observation manually in OntoWiki"));
		final ComboBox comboDataSets = new ComboBox(null, getDataSets());
		comboDataSets.setNullSelectionAllowed(false);
		comboDataSets.setWidth("100%");
		panelLayout.addComponent(comboDataSets);
		final Button fix = new Button("Quick Fix");
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		buttonsLayout.addComponent(fix);
		buttonsLayout.addComponent(editInOW);
		panelLayout.addComponent(buttonsLayout);
		panelLayout.setExpandRatio(buttonsLayout, 2.0f);
		
		listObs.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
				String chosenObs = (String)event.getProperty().getValue();
				lblProblem.setValue("<b>Problem description: </b>The selected observation belongs to " + map.get(chosenObs) +
						" data sets. It should belong to exactly one.");
			}
		});
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String chosenDataSet = (String)comboDataSets.getValue();
				String observation = (String)listObs.getValue();
				
				if (chosenDataSet == null) {
					getWindow().showNotification("DataSet was not selected", Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				if (observation == null) {
					getWindow().showNotification("Observation was not selected", Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				
				String dataSetProp = "http://purl.org/linked-data/cube#dataSet";
				List<String> forRemoval = getObsDataSets(observation);
				if (forRemoval.size()>0){
					ArrayList<Statement> stmts = new ArrayList<Statement>();
					for (String ds: forRemoval)
						stmts.add(getStatementFromUris(observation, dataSetProp, ds));
					removeStatements(stmts);
				}
				ArrayList<Statement> addStmts = new ArrayList<Statement>();
				addStmts.add(getStatementFromUris(observation, dataSetProp, chosenDataSet));
				uploadStatements(addStmts);
				observationLinks();
			}
		});
		
		showContent();
	}
	
	// IC-2 layout
	private void dataSetLinks(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icLinkToDSD.getResults();
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, String> map = new HashMap<String, String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			map.put(set.getValue("dataSet").stringValue(), set.getValue("dsdNum").stringValue());
		}
		
		if (map.size() == 0){
			Label label = new Label();
			label.setValue("All data sets have exactly one link to the DSD");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Below is the list of data sets that are not linked to exactly one DSD. Click on any of them to get more information and either edit the data set in OntoWiki or choose a quick solution");
		validationTab.addComponent(label);
		
		final ListSelect listDataSets= new ListSelect("Data Sets", map.keySet());
		listDataSets.setNullSelectionAllowed(false);
		validationTab.addComponent(listDataSets);
		listDataSets.setImmediate(true);
		listDataSets.setWidth("100%");
		
		final Table detailsTable = new Table("Details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		final Label lblProblem = new Label("<b>Problem description: </b>", Label.CONTENT_XHTML);
		validationTab.addComponent(lblProblem);
		
		Button editInOW = new Button("Edit in OntoWiki");
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listDataSets.getValue());
			}
		});
		
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		panelLayout.addComponent(new Label("After the fix the selected data sets will link only to the DSD selected below or you can choose to edit the selected data set manually in OntoWiki"));
		final ComboBox comboDSDs = new ComboBox(null, getDataStructureDefinitions());
		comboDSDs.setNullSelectionAllowed(false);
		comboDSDs.setWidth("100%");
		panelLayout.addComponent(comboDSDs);
		final Button fix = new Button("Quick Fix");
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		buttonsLayout.addComponent(fix);
		buttonsLayout.addComponent(editInOW);
		panelLayout.addComponent(buttonsLayout);
		panelLayout.setExpandRatio(buttonsLayout, 2.0f);
		
		listDataSets.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
				String chosenDataSet = (String)event.getProperty().getValue();
				lblProblem.setValue("<b>Problem description: </b>The selected data set belongs to " + map.get(chosenDataSet) +
						" DSDs. It should belong to exactly one.");
			}
		});
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String chosenDSD = (String)comboDSDs.getValue();
				String dataSet = (String)listDataSets.getValue();
				
				if (chosenDSD == null) {
					getWindow().showNotification("DSD was not selected", Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				if (dataSet == null) {
					getWindow().showNotification("Data set was not selected", Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				
				String structProp = "http://purl.org/linked-data/cube#structure";
				List<String> forRemoval = getDataSetDSDs(dataSet);
				if (forRemoval.size()>0){
					ArrayList<Statement> stmts = new ArrayList<Statement>();
					for (String dsd: forRemoval)
						stmts.add(getStatementFromUris(dataSet, structProp, dsd));
					removeStatements(stmts);
				}
				ArrayList<Statement> addStmts = new ArrayList<Statement>();
				addStmts.add(getStatementFromUris(dataSet, structProp, chosenDSD));
				uploadStatements(addStmts);
				dataSetLinks();
			}
		});
		
		showContent();
	}
	
	// IC-3 layout
	private void measuresInDSD(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icMeasuresInDSD.getResults();
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final List<String> dsdList = new ArrayList<String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			dsdList.add(set.getValue("dsd").stringValue());
		}
		
		if (dsdList.size() == 0){
			Label label = new Label();
			label.setValue("All DSDs contain at least one measure");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following DSDs do not have at least one measure defined");
		validationTab.addComponent(lbl);
		
		final ListSelect listDSDs = new ListSelect("DSDs", dsdList);
		listDSDs.setNullSelectionAllowed(false);
		validationTab.addComponent(listDSDs);
		
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		
		Label fixLabel = new Label();
		fixLabel.setContentMode(Label.CONTENT_XHTML);
		fixLabel.setValue("After the fix, component selected in the combo box below will be turned to measure, " +
				"or you can choose to edit the above selected DSD manually in OntoWiki");
		panelLayout.addComponent(fixLabel);
		final ComboBox comboComponents = new ComboBox();
		comboComponents.setWidth("100%");
		comboComponents.setNullSelectionAllowed(false);
		panelLayout.addComponent(comboComponents);
		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		Button editOW = new Button("Edit in OntoWiki");
		Button turnToMeasure = new Button("Turn to measure");
		btnLayout.addComponent(turnToMeasure);
		btnLayout.addComponent(editOW);
		panelLayout.addComponent(btnLayout);
		panelLayout.setExpandRatio(btnLayout, 2.0f);
		
		listDSDs.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String dsd = event.getProperty().getValue().toString();
				if (dsd == null || dsd.equalsIgnoreCase("")) return;
				
				comboComponents.removeAllItems();
				TupleQueryResult qRes = executeTupleQuery(ValidationFixUtils.ic03_getRequiredAttributes(state.getCurrentGraph(), dsd));
				try {
					while (qRes.hasNext()){
						comboComponents.addItem(qRes.next().getValue("attr").toString());
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		turnToMeasure.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Object selVal = comboComponents.getValue();
				if (selVal == null) {
					getWindow().showNotification("No component was selected in the combo box");
					return;
				}
				
				executeGraphQuery(ValidationFixUtils.ic03_turnToMeasure(state.getCurrentGraph(), selVal.toString()));
				executeGraphQuery(ValidationFixUtils.ic03_turnToMeasure2(state.getCurrentGraph(), selVal.toString()));
				getWindow().showNotification("Fix executed");
				icMeasuresInDSD.evaluate();
				measuresInDSD();
			}
		});
		editOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listDSDs.getValue());
			}
		});
		
		showContent();
	}
	
	// IC-4 layout
	private void dimensionsHaveRange(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icDimensionsHaveRange.getResults();
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final List<String> dimList = new ArrayList<String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			dimList.add(set.getValue("dim").stringValue());
		}
		
		if (dimList.size() == 0){
			Label label = new Label();
			label.setValue("All dimensions have a defined range");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following dimensions do not have a defined range");
		validationTab.addComponent(lbl);
		
		final ListSelect listDimensions = new ListSelect("Dimensions", dimList);
		listDimensions.setNullSelectionAllowed(false);
		validationTab.addComponent(listDimensions);
		
		Button fix = new Button("Edit in OntoWiki");
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listDimensions.getValue());
			}
		});
		
		showContent();
	}
	
	// IC-6 layout
	private void attributesOptional(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icAttributesOptional.getResults();
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, String> compMap = new HashMap<String, String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			compMap.put(set.getValue("component").stringValue(), set.getValue("dsd").stringValue());
		}
		
		if (compMap.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - if there are any optional components, they are attributes");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following components are marked as optional, but they are not attributes");
		validationTab.addComponent(lbl);
		
		final ListSelect listComponents = new ListSelect("Component Properties", compMap.keySet());
		listComponents.setNullSelectionAllowed(false);
		validationTab.addComponent(listComponents);
		
		final Table detailsTable = new Table("Details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		listComponents.addListener(new DetailsListener(detailsTable));
		
//		final Label lblProblem = new Label("<b>Problem description: </b>", Label.CONTENT_XHTML);
//		validationTab.addComponent(lblProblem);
		
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		
		Label fixLabel = new Label();
		fixLabel.setContentMode(Label.CONTENT_XHTML);
		fixLabel.setValue(""); // TODO
		panelLayout.addComponent(fixLabel);
		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		Button editOW = new Button("Edit in OntoWiki");
		Button removeCompReq = new Button("Remove qb:componentRequired");
		Button turnToAttr = new Button("Turn to attribute");
		btnLayout.addComponent(removeCompReq);
		btnLayout.addComponent(turnToAttr);
		btnLayout.addComponent(editOW);
		panelLayout.addComponent(btnLayout);
		panelLayout.setExpandRatio(btnLayout, 2.0f);
		
		removeCompReq.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String chosenComponent = (String)listComponents.getValue();
				if (chosenComponent == null){
					getWindow().showNotification("Cannot execute the action", 
							"A component needs to be chosen first", 
							Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				String chosenDSD = compMap.get(chosenComponent);
				String query = ValidationFixUtils.ic06_removeComponentRequired(state.getCurrentGraph(), chosenDSD, chosenComponent);
				executeGraphQuery(query);
				getWindow().showNotification("Fix executed");
				refresh();
				attributesOptional();
			}
		});
		turnToAttr.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String chosenComponent = (String)listComponents.getValue();
				if (chosenComponent == null){
					getWindow().showNotification("Cannot execute the action", 
							"A component needs to be chosen first", 
							Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				String query = ValidationFixUtils.ic06_changeToAttribute(state.getCurrentGraph(), chosenComponent);
				String query2 = ValidationFixUtils.ic06_changeToAttribute2(state.getCurrentGraph(), chosenComponent);
				executeGraphQuery(query);
				executeGraphQuery(query2);
				getWindow().showNotification("Fix executed");
				refresh();
				attributesOptional();
			}
		});
		editOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listComponents.getValue());
			}
		});
		
		showContent();
	}
	
	// IC-7 layout
	private void sliceKeysDeclared(){
		validationTab.removeAllComponents();
		
		final Iterator<BindingSet> res = icSliceKeysDeclared.getResults();
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - " + errorMsg);
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final ArrayList<String> listSliceKeys = new ArrayList<String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			listSliceKeys.add(set.getValue("sliceKey").stringValue());
		}
		if (listSliceKeys.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - either there are no slice keys or every slice key is associated with a DSD");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Following slice keys should be associated with a DSD");
		validationTab.addComponent(label);
		final ListSelect lsSliceKeys = new ListSelect("Slice keys", listSliceKeys);
		lsSliceKeys.setImmediate(true);
		lsSliceKeys.setNullSelectionAllowed(false);
		validationTab.addComponent(lsSliceKeys);
		
		final Table detailsTable = new Table("Slice key details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		
		Label fixLabel = new Label();
		fixLabel.setContentMode(Label.CONTENT_XHTML);
		fixLabel.setValue("After the fix, slice key chosen above will be associated with the DSD chosen below, or you can edit the slice key manually.");
		panelLayout.addComponent(fixLabel);
		final ComboBox comboDSDs = new ComboBox();
		comboDSDs.setNullSelectionAllowed(false);
		comboDSDs.setWidth("100%");
		panelLayout.addComponent(comboDSDs);
		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		Button editOW = new Button("Edit in OntoWiki");
		Button fix = new Button("Quick fix");
		btnLayout.addComponent(fix);
		btnLayout.addComponent(editOW);
		panelLayout.addComponent(btnLayout);
		panelLayout.setExpandRatio(btnLayout, 2.0f);
		
		editOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)lsSliceKeys.getValue());
			}
		});
		lsSliceKeys.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		lsSliceKeys.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String sk = event.getProperty().getValue().toString();
				if (sk == null || sk.equalsIgnoreCase("")) return;
				
				TupleQueryResult resSliceKeys = executeTupleQuery(ValidationFixUtils.ic07_getMatchingDSDs(state.getCurrentGraph(), sk));
				comboDSDs.removeAllItems();
				try {
					while (resSliceKeys.hasNext()){
						comboDSDs.addItem(resSliceKeys.next().getValue("dsd").stringValue());
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Object selVal = comboDSDs.getValue();
				if (selVal == null){
					getWindow().showNotification("No DSD was selected");
					return;
				}
				executeGraphQuery(ValidationFixUtils.ic07_insertConnection(state.getCurrentGraph(), 
						selVal.toString(), lsSliceKeys.getValue().toString()));
				getWindow().showNotification("Fix executed");
				
				icSliceKeysDeclared.evaluate();
				sliceKeysDeclared();
			}
		});
		
		showContent();
	}
	
	// IC-8 layout
	private void sliceKeysConsistentWithDSD(){
		validationTab.removeAllComponents();
		
		final Iterator<BindingSet> res = icSliceKeysConsistentWithDSD.getResults();
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - " + errorMsg);
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final ArrayList<String> listSliceKeys = new ArrayList<String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			listSliceKeys.add(set.getValue("sliceKey").stringValue());
		}
		if (listSliceKeys.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - either there are no slice keys or all slice keys are consistent with associated DSD, i.e. for every slice key holds: " +
					"every component property of the slice key is also declared as a component of the associated DSD");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("All slice keys should be consistent with thier associated DSDs, i.e. for every slice key following should hold: " +
				"every component property of the slice key is also declared as a component of the associated DSD.");
		validationTab.addComponent(label);
		Label label2 = new Label();
		label2.setValue("Following slice keys should be modified in order to be consistent with the associated DSD");
		validationTab.addComponent(label2);
		final ListSelect lsSliceKeys = new ListSelect("Slice keys", listSliceKeys);
		lsSliceKeys.setImmediate(true);
		lsSliceKeys.setNullSelectionAllowed(false);
		validationTab.addComponent(lsSliceKeys);
		
		final Table detailsTable = new Table("Slice key details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		Button editInOW = new Button("Edit in OntoWiki");
		validationTab.addComponent(editInOW);
		
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)lsSliceKeys.getValue());
			}
		});
		lsSliceKeys.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		
		showContent();
	}
	
	
	// IC-9 layout
	private void sliceStructureUnique(){
		validationTab.removeAllComponents();
		
		final Iterator<BindingSet> res = icSliceStructureUnique.getResults();
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - " + errorMsg);
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final ArrayList<String> listSlices = new ArrayList<String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			listSlices.add(set.getValue("slice").stringValue());
		}
		if (listSlices.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - either there are no slices or every slice has a unique structure, i.e. exactly one associated slice key (via property qb:sliceStructure)");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Following slices have 0 or more than 1 associated slice keys (via property qb:sliceStructure)");
		validationTab.addComponent(label);
		final ListSelect lsSlices = new ListSelect("Slices", listSlices);
		lsSlices.setImmediate(true);
		lsSlices.setNullSelectionAllowed(false);
		validationTab.addComponent(lsSlices);
		
		final Table detailsTable = new Table("Slice details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		
		Label fixLabel = new Label();
		fixLabel.setContentMode(Label.CONTENT_XHTML);
		fixLabel.setValue("After the fix, slice chosen above will be associated with the slice key chosen in the below combo box, " +
				"or the problematic slice can be edited manuallz in OntoWiki");
		panelLayout.addComponent(fixLabel);
		final ComboBox comboKeys = new ComboBox();
		comboKeys.setWidth("100%");
		comboKeys.setNullSelectionAllowed(false);
		panelLayout.addComponent(comboKeys);
		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		Button editOW = new Button("Edit in OntoWiki");
		Button fix = new Button("Quick fix");
		btnLayout.addComponent(fix);
		btnLayout.addComponent(editOW);
		panelLayout.addComponent(btnLayout);
		panelLayout.setExpandRatio(btnLayout, 2.0f);
		
		editOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)lsSlices.getValue());
			}
		});
		lsSlices.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		lsSlices.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String slice = event.getProperty().toString();
				comboKeys.removeAllItems();
				TupleQueryResult resKeys = executeTupleQuery(ValidationFixUtils.ic09_getMatchingKeys(state.getCurrentGraph(), slice));
				try {
					while (resKeys.hasNext()){
						comboKeys.addItem(resKeys.next().getValue("key"));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Object selKey = comboKeys.getValue();
				Object selSlice = lsSlices.getValue();
				if (selKey == null || selSlice == null) {
					getWindow().showNotification("No slice key or slice was selected");
					return;
				}
				
				executeGraphQuery(ValidationFixUtils.ic09_removeSliceKeys(state.getCurrentGraph(), selSlice.toString()));
				executeGraphQuery(ValidationFixUtils.ic09_insertSliceKey(state.getCurrentGraph(), selSlice.toString(), selKey.toString()));
				getWindow().showNotification("Fix executed");
				icSliceStructureUnique.evaluate();
				sliceStructureUnique();
			}
		});
		
		showContent();
	}
	
	// IC-10 layout
	private void sliceDimensionsComplete(){
		validationTab.removeAllComponents();
		
		final Iterator<BindingSet> res = icSliceDimensionsComplete.getResults();
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - " + errorMsg);
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		String lastSlice = null;
		ArrayList<String> lastDimensions = new ArrayList<String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			String s = set.getValue("slice").stringValue();
			if (lastSlice == null) lastSlice = s;
			String d = set.getValue("dim").stringValue();
			if (!s.equals(lastSlice)) {
				map.put(lastSlice, lastDimensions);
				lastSlice = s;
				lastDimensions = new ArrayList<String>();
			}
			lastDimensions.add(d);
		}
		if (lastSlice != null) map.put(lastSlice, lastDimensions);
		if (map.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - either there are no slices or every slice has a value for every dimension declared in its associated slice key (via property qb:sliceStructure)");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Following slices do not have a value for every dimension declared in its associated slice key (via property qb:sliceStructure)");
		validationTab.addComponent(label);
		final ListSelect lsSlices = new ListSelect("Slices", map.keySet());
		lsSlices.setImmediate(true);
		lsSlices.setNullSelectionAllowed(false);
		validationTab.addComponent(lsSlices);
		
		final Table detailsTable = new Table("Slice details");
		detailsTable.setHeight("200px");
		detailsTable.setWidth("100%");
		detailsTable.addContainerProperty("Property", String.class, null);
		detailsTable.addContainerProperty("Object", String.class, null);
		validationTab.addComponent(detailsTable);
		
		final Label lblProblem = new Label("<b>Problem description: </b>", Label.CONTENT_XHTML);
		validationTab.addComponent(lblProblem);
		
		Button editInOW = new Button("Edit in OntoWiki");
		validationTab.addComponent(editInOW);
		
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)lsSlices.getValue());
			}
		});
		lsSlices.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String slice = (String)event.getProperty().getValue();
				TupleQueryResult res = getResourceProperties(slice);
				int i=1;
				detailsTable.removeAllItems();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						detailsTable.addItem(new Object [] { set.getValue("p").stringValue(),
								set.getValue("o").stringValue() }, new Integer(i++));
					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
				StringBuilder sb = new StringBuilder();
				sb.append("<b>Problem description: </b>Selected slice is missing a value for the following dimensions:");
				for (String dim: map.get(slice))
					sb.append(" ").append(dim).append(",");
				sb.deleteCharAt(sb.length()-1);
				lblProblem.setValue(sb.toString());
			}
		});
		
		showContent();
	}
	
	// IC-5 layout
	private void dimensionDefinitions(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icDimsHaveCodeLists.getResults();
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final List<String> dimList = new ArrayList<String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			dimList.add(set.getValue("dim").stringValue());
		}
		
		if (dimList.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - every dimension with range skos:Concept has a code list");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following dimensions with range skos:Concept do not have a code list");
		validationTab.addComponent(lbl);
		
		final ListSelect listDimensions = new ListSelect("Dimensions", dimList);
		listDimensions.setNullSelectionAllowed(false);
		validationTab.addComponent(listDimensions);
		
		Button fix = new Button("Edit in OntoWiki");
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listDimensions.getValue());
			}
		});
		
		showContent();
	}
	
	// IC-11 layout
	private void dimensionsRequired(){
		validationTab.removeAllComponents();
		validationTab.addComponent(new Label("Following observation don't have a value for each dimension: "));
		Iterator<BindingSet> res = icDimensionsRequired.getResults();
		ArrayList<String> listObs = new ArrayList<String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			listObs.add(set.getValue("obs").stringValue());
		}
		ListSelect ls = new ListSelect("Observations", listObs);
		ls.setNullSelectionAllowed(false);
		ls.setWidth("100%");
		validationTab.addComponent(ls);
		Button fix = new Button("Quick Fix");
		fix.setEnabled(false);
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		showContent();
	}
	
	// IC-12 layout
	private void noDuplicateObs(){
		validationTab.removeAllComponents();
		validationTab.addComponent(new Label("Following observations belong to the same data set and have the same value for all dimensions."));
		
		final ListSelect ls1 = new ListSelect("Observations");
		ls1.setNullSelectionAllowed(false);
		ls1.setImmediate(true);
		ls1.setWidth("100%");
		validationTab.addComponent(ls1);
		
		Iterator<BindingSet> res = icNoDuplicateObservations.getResults();
		final HashMap<String, List<String>> mapDuplicates = new HashMap<String, List<String>>();
		String lastObs = "";
		List<String> lastDuplicates = null;
		while (res.hasNext()){
			BindingSet set = res.next();
			String obs1 = set.getValue("obs1").stringValue();
			if (!obs1.equals(lastObs)){
				lastObs = obs1;
				lastDuplicates = new ArrayList<String>();
				mapDuplicates.put(lastObs, lastDuplicates);
				ls1.addItem(lastObs);
			}
			lastDuplicates.add(set.getValue("obs2").stringValue());
		}
		
		final ListSelect ls2 = new ListSelect("Duplicates");
		ls2.setNullSelectionAllowed(false);
		ls2.setImmediate(true);
		ls2.setWidth("100%");
		validationTab.addComponent(ls2);
		
		ls1.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				ls2.removeAllItems();
				for (String duplicate: mapDuplicates.get(event.getProperty().getValue()))
					ls2.addItem(duplicate);
			}
		});
		
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		panelLayout.addComponent(new Label("After the fix duplicates of the selected observation will be removed from the graph"));
		
		Button fix = new Button("Quick Fix");
		fix.setEnabled(false);
		panelLayout.addComponent(fix);
		panelLayout.setExpandRatio(fix, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String obsString = (String)ls1.getValue();
				if (obsString == null || obsString.isEmpty())
					getWindow().showNotification("Select observation first", Notification.TYPE_ERROR_MESSAGE);
				ValueFactory factory = state.getRdfStore().getValueFactory();
				URI obsURI = factory.createURI(obsString);
				TupleQueryResult res = getResourceProperties(obsString);
				ArrayList<Statement> stmts = new ArrayList<Statement>();
				try {
					while (res.hasNext()){
						BindingSet set = res.next();
						URI propURI = factory.createURI(set.getValue("p").stringValue());
						Value objValue = set.getValue("o");
						stmts.add(factory.createStatement(obsURI, propURI, objValue));
					}
					res = getResourceLinks(obsString);
					while (res.hasNext()){
						BindingSet set = res.next();
						URI propURI = factory.createURI(set.getValue("p").stringValue());
						URI subURI = factory.createURI(set.getValue("s").stringValue());
						stmts.add(factory.createStatement(subURI, propURI, obsURI));
					}
					removeStatements(stmts);
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		});
		
		showContent();
	}
	
	// IC-13 layout
	private void requiredAttributes(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icRequiredAttributes.getResults();
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, String> obsMap = new HashMap<String, String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			obsMap.put(set.getValue("obs").stringValue(), set.getValue("attr").stringValue());
		}
		
		if (obsMap.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - Every qb:Observation has a value for each declared attribute that is marked as required");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following observations do not have a value for required attribute(s)");
		validationTab.addComponent(lbl);
		
		final ListSelect listObservations = new ListSelect("Observations", obsMap.keySet());
		listObservations.setNullSelectionAllowed(false);
		validationTab.addComponent(listObservations);
		
		// TODO: add label that tells which attribute is missing
		Form panelQuickFix = new Form();
		panelQuickFix.setCaption("Quick Fix");
		panelQuickFix.setSizeFull();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setSizeFull();
		panelQuickFix.setLayout(panelLayout);
		validationTab.addComponent(panelQuickFix);
		validationTab.setExpandRatio(panelQuickFix, 2.0f);
		
		Label fixLabel = new Label();
		fixLabel.setContentMode(Label.CONTENT_XHTML);
		fixLabel.setValue(""); // TODO
		panelLayout.addComponent(fixLabel);
		
		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		Button removeRequired = new Button("Remove qb:componentRequired");
		Button editOW = new Button("Edit in OntoWiki");
		btnLayout.addComponent(removeRequired);
		btnLayout.addComponent(editOW);
		panelLayout.addComponent(btnLayout);
		panelLayout.setExpandRatio(btnLayout, 2.0f);
		
		removeRequired.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				String chosenObs = (String)listObservations.getValue();
				if (chosenObs == null){
					getWindow().showNotification("Cannot execute the action", 
							"Observation needs to be chosen first", 
							Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				String query = ValidationFixUtils.ic13_removeComponentRequiredTrue(state.getCurrentGraph(), 
						chosenObs, 
						obsMap.get(chosenObs));
				executeGraphQuery(query);
				getWindow().showNotification("Fix executed");
				refresh();
				requiredAttributes();
			}
		});
		editOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listObservations.getValue());
			}
		});
		
		showContent();
	}
	
	// IC-14 layout
	private void allMeasuresPresent(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icAllMeasuresPresent.getResults();
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, String> obsMap = new HashMap<String, String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			obsMap.put(set.getValue("obs").stringValue(), set.getValue("measure").stringValue());
		}
		
		if (obsMap.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - In Data Sets that do not use a Measure dimension (if there are any) each Observation has a value for every declared measure");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following observations are missing a value for declared measure(s)");
		validationTab.addComponent(lbl);
		
		final ListSelect listObservations = new ListSelect("Observations", obsMap.keySet());
		listObservations.setNullSelectionAllowed(false);
		validationTab.addComponent(listObservations);
		
		// TODO: add label that tells which measure is missing
		
		Button fix = new Button("Edit in OntoWiki");
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listObservations.getValue());
			}
		});
		
		showContent();
	}
	
	// IC-15 layout
	private void measureDimConsistent(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icMeasureDimConsistent.getResults();
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, String> obsMap = new HashMap<String, String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			obsMap.put(set.getValue("obs").stringValue(), set.getValue("measure").stringValue());
		}
		
		if (obsMap.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - In Data Sets that a Measure dimension (if there are any) each Observation has a value for the measure corresponding to its given qb:measureType");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following observations are missing a value for the measure corresponding to its given qb:measureType");
		validationTab.addComponent(lbl);
		
		final ListSelect listObservations = new ListSelect("Observations", obsMap.keySet());
		listObservations.setNullSelectionAllowed(false);
		validationTab.addComponent(listObservations);
		
		// TODO: add label that tells which measure is missing
		
		Button fix = new Button("Edit in OntoWiki");
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listObservations.getValue());
			}
		});
		
		showContent();
	}
	
	// IC-16 layout
	private void singleMeasure(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icSingleMeasure.getResults();
		
		@SuppressWarnings("unused")
		final class MeasureOmeasurePair{
			String measure;
			String omeasure;
		}
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, MeasureOmeasurePair> obsMap = new HashMap<String, MeasureOmeasurePair>();
		while (res.hasNext()){
			BindingSet set = res.next();
			MeasureOmeasurePair pair = new MeasureOmeasurePair();
			pair.measure = set.getValue("measure").stringValue();
			pair.omeasure = set.getValue("omeasure").stringValue();
			obsMap.put(set.getValue("obs").stringValue(), pair);
		}
		
		if (obsMap.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - In Data Sets that use a Measure dimension (if there are any) each Observation only has a value for one measure");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following observations belong to data sets that use a Measure dimension and have a value for more than one measure");
		validationTab.addComponent(lbl);
		
		final ListSelect listObservations = new ListSelect("Observations", obsMap.keySet());
		listObservations.setNullSelectionAllowed(false);
		validationTab.addComponent(listObservations);
		
		// TODO: add label that tells what is the measure dimension and mention the omeasure, perhaps details table
		
		Button fix = new Button("Edit in OntoWiki");
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listObservations.getValue());
			}
		});
		
		showContent();
	}
		
	// IC-17 layout
	private void allMeasuresPresentInMeasDimCube(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icAllMeasuresPresentInMeasDimCube.getResults();
		
		@SuppressWarnings("unused")
		final class NumMeasuresCountPair{
			String numMeasures;
			String count;
		}
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, NumMeasuresCountPair> obsMap = new HashMap<String, NumMeasuresCountPair>();
		while (res.hasNext()){
			BindingSet set = res.next();
			NumMeasuresCountPair pair = new NumMeasuresCountPair();
			pair.numMeasures = set.getValue("numMeasures").stringValue();
			pair.count = set.getValue("count").stringValue();
			obsMap.put(set.getValue("obs1").stringValue(), pair);
		}
		
		if (obsMap.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - In a data set which uses a measure dimension then " +
					"if there is an Observation for some combination of non-measure dimensions then " +
					"there must be other Observations with the same non-measure dimension values for each of the declared measures");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following observations belong to data sets that use a Measure dimension and break a rule that " +
				"if there is an Observation for some combination of non-measure dimensions then " +
				"there must be other Observations with the same non-measure dimension values for each of the declared measures");
		validationTab.addComponent(lbl);
		
		final ListSelect listObservations = new ListSelect("Observations", obsMap.keySet());
		listObservations.setNullSelectionAllowed(false);
		validationTab.addComponent(listObservations);
		
		// TODO: add label that tells what is the difference in counts, maybe even more, perhaps details table
		
		Button fix = new Button("Edit in OntoWiki");
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listObservations.getValue());
			}
		});
		
		showContent();
	}
	
	// IC-18 layout
	private void consistentDataSetLinks(){
		validationTab.removeAllComponents();
		Iterator<BindingSet> res = icConsistentDataSetLinks.getResults();
		
		@SuppressWarnings("unused")
		final class DataSetSlicePair{
			String dataset;
			String slice;
		}
		
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, DataSetSlicePair> obsMap = new HashMap<String, DataSetSlicePair>();
		while (res.hasNext()){
			BindingSet set = res.next();
			DataSetSlicePair pair = new DataSetSlicePair();
			pair.dataset = set.getValue("dataset").stringValue();
			pair.slice = set.getValue("slice").stringValue();
			obsMap.put(set.getValue("obs").stringValue(), pair);
		}
		
		if (obsMap.size() == 0){
			Label label = new Label();
			label.setValue("No problems were detected - If a qb:DataSet D has a qb:slice S, and S has an qb:observation O, then the qb:dataSet corresponding to O must be D");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label lbl = new Label();
		lbl.setValue("Following observations are missing a link to the appropriate data set");
		validationTab.addComponent(lbl);
		
		final ListSelect listObservations = new ListSelect("Observations", obsMap.keySet());
		listObservations.setNullSelectionAllowed(false);
		validationTab.addComponent(listObservations);
		
		// TODO: add label that tells which dataset and slice are in question, perhaps details table
		
		Button fix = new Button("Edit in OntoWiki");
		validationTab.addComponent(fix);
		validationTab.setExpandRatio(fix, 2.0f);
		
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listObservations.getValue());
			}
		});
		
		showContent();
	}
	
	// IC-19 layout
	private void codesFromCodeList(){
		validationTab.removeAllComponents();
		final Iterator<BindingSet> res = icCodesFromCodeLists.getResults();
		if (res == null) {
			Label label = new Label();
			label.setValue("ERROR - " + errorMsg);
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		final HashMap<String, String> map = new HashMap<String, String>();
		while (res.hasNext()){
			BindingSet set = res.next();
			map.put(set.getValue("v").stringValue(), set.getValue("list").stringValue());
		}
		
		if (map.size() == 0){
			Label label = new Label();
			label.setValue("All values of coded dimensions are linked to the code lists");
			validationTab.addComponent(label);
			showContent();
			return;
		}
		
		Label label = new Label();
		label.setValue("Following resources should be of type skos:Concept and linked to the appropriate code list");
		validationTab.addComponent(label);
		
		final ListSelect listValues= new ListSelect("Resources", map.keySet());
		listValues.setNullSelectionAllowed(false);
		validationTab.addComponent(listValues);
		
		Button editInOW = new Button("Edit in OntoWiki");
		editInOW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInOntowiki((String)listValues.getValue());
			}
		});
		
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		
		Button fix = new Button("Quick Fix");
		validationTab.addComponent(buttonsLayout);
		validationTab.setExpandRatio(buttonsLayout, 2.0f);
		buttonsLayout.addComponent(fix);
		buttonsLayout.addComponent(editInOW);
		fix.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String resource = (String)listValues.getValue();
				String codeList = map.get(resource);
				getWindow().addWindow(new QuickFixCodesFromCodeLists(resource, codeList));
			}
		});
		
		showContent();
	}
	
	private void showContent(){
//		mainContrainer.setExpandRatio(criteriaPanel, 0.0f);
//		mainContrainer.setExpandRatio(validationPanel, 2.0f);
//		mainContrainer.setSizeFull();
	}
	
	private class DetailsListener implements Property.ValueChangeListener{
		private Table tbl;
		public DetailsListener(Table tbl) {
			this.tbl = tbl;
		}
		public void valueChange(ValueChangeEvent event) {
			TupleQueryResult res = getResourceProperties((String)event.getProperty().getValue());
			int i=1;
			tbl.removeAllItems();
			try {
				while (res.hasNext()){
					BindingSet set = res.next();
					tbl.addItem(new Object [] { set.getValue("p").stringValue(),
							set.getValue("o").stringValue() }, new Integer(i++));
				}
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void showInOntowiki(String resource){
		if (resource == null || resource.isEmpty())
			return;
		target.removeAllComponents();
		String path = "/resource/properties?m="+state.getCurrentGraph()+"&r="+resource;
		OntoWikiPathExtended component = new OntoWikiPathExtended(state, path, false);
		component.setSizeFull();
		target.addComponent(component);
	}
	
	private GraphQueryResult executeGraphQuery(String query){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			GraphQuery graphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, query);
			GraphQueryResult result = graphQuery.evaluate();
			return result;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private TupleQueryResult executeTupleQuery(String query){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
			TupleQueryResult tupleResult = tupleQuery.evaluate(); 
			return tupleResult;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private TupleQueryResult getResourceProperties(String resource){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			StringBuilder q = new StringBuilder();
			q.append("select ?p ?o from <").append(state.getCurrentGraph()).append("> where { <");
			q.append(resource).append("> ?p ?o . }");
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			return tupleQuery.evaluate();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private TupleQueryResult getResourceLinks(String resource){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			StringBuilder q = new StringBuilder();
			q.append("select ?s ?p from <").append(state.getCurrentGraph()).append("> where { ?s ?p <");
			q.append(resource).append("> . }");
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			return tupleQuery.evaluate();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<String> getObservations(){
		StringBuilder q = new StringBuilder();
		q.append("select ?o from <").append(state.getCurrentGraph());
		q.append("> where { ?o a <http://purl.org/linked-data/cube#Observation> . }");
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			TupleQueryResult result = tupleQuery.evaluate();
			ArrayList<String> list = new ArrayList<String>();
			while (result.hasNext())
				list.add(result.next().getValue("o").stringValue());
			return list;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<String> getDataSets(){
		StringBuilder q = new StringBuilder();
		q.append("select ?ds from <").append(state.getCurrentGraph());
		q.append("> where { ?ds a <http://purl.org/linked-data/cube#DataSet> . }");
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			TupleQueryResult result = tupleQuery.evaluate();
			ArrayList<String> list = new ArrayList<String>();
			while (result.hasNext())
				list.add(result.next().getValue("ds").stringValue());
			return list;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<String> getDataStructureDefinitions(){
		StringBuilder q = new StringBuilder();
		q.append("select ?dsd from <").append(state.getCurrentGraph());
		q.append("> where { ?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> . }");
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			TupleQueryResult result = tupleQuery.evaluate();
			ArrayList<String> list = new ArrayList<String>();
			while (result.hasNext())
				list.add(result.next().getValue("dsd").stringValue());
			return list;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<String> getObsDataSets(String obs){
		StringBuilder q = new StringBuilder();
		q.append("select ?ds from <").append(state.getCurrentGraph());
		q.append("> where { <").append(obs).append("> <http://purl.org/linked-data/cube#dataSet> ?ds . ");
		q.append("?ds a <http://purl.org/linked-data/cube#DataSet> . }");
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			TupleQueryResult result = tupleQuery.evaluate();
			ArrayList<String> list = new ArrayList<String>();
			while (result.hasNext())
				list.add(result.next().getValue("ds").stringValue());
			return list;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<String> getDataSetDSDs(String dataSet){
		StringBuilder q = new StringBuilder();
		q.append("select ?dsd from <").append(state.getCurrentGraph());
		q.append("> where { <").append(dataSet).append("> <http://purl.org/linked-data/cube#structure> ?dsd . ");
		q.append("?dsd a <http://purl.org/linked-data/cube#DataStructureDefinition> . }");
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, q.toString());
			TupleQueryResult result = tupleQuery.evaluate();
			ArrayList<String> list = new ArrayList<String>();
			while (result.hasNext())
				list.add(result.next().getValue("dsd").stringValue());
			return list;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void uploadStatements(Iterable<? extends Statement> statements){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			URI graph = state.getRdfStore().getValueFactory().createURI(state.getCurrentGraph());
			con.add(statements, graph);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private void removeStatements(Iterable<? extends Statement> statements){
		try {
			RepositoryConnection con = state.getRdfStore().getConnection();
			URI graph = state.getRdfStore().getValueFactory().createURI(state.getCurrentGraph());
			con.remove(statements, graph);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private Statement getStatementFromUris(String s, String p, String o){
		ValueFactory factory = state.getRdfStore().getValueFactory();
		URI sub = factory.createURI(s);
		URI pre = factory.createURI(p);
		URI obj = factory.createURI(o);
		return factory.createStatement(sub, pre, obj);
	}
	
	private class QuickFixCodesFromCodeLists extends Window {
		public QuickFixCodesFromCodeLists(final String resource, final String codeList){
			this.setCaption("Quick Fix");
			this.setWidth(400, UNITS_PIXELS);
			this.setHeight(300, UNITS_PIXELS);
			VerticalLayout content = new VerticalLayout();
			content.setSizeFull();
			if (resource == null || resource.isEmpty())
				content.addComponent(new Label("You need to select a resource first"));
			else 
				content.addComponent(new Label("If you choose to apply the fix, the selected resource (" +
						resource + ") will be of type skos:Conept and linked to the code list " + codeList +
						" via skos:inScheme property"));
			HorizontalLayout layoutButtons = new HorizontalLayout();
			content.addComponent(layoutButtons);
			content.setComponentAlignment(layoutButtons, Alignment.MIDDLE_CENTER);
			Button btnOK = new Button("OK");
			layoutButtons.addComponent(btnOK);
			Button btnCancel = new Button("Cancel");
			layoutButtons.addComponent(btnCancel);
			
			btnCancel.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					Validation.this.getWindow().removeWindow(QuickFixCodesFromCodeLists.this);
				}
			});
			
			btnOK.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (resource == null || resource.isEmpty())
						return;
					
					ArrayList<Statement> statements = new ArrayList<Statement>();
					String concept = "http://www.w3.org/2004/02/skos/core#Concept";
					String inScheme = "http://www.w3.org/2004/02/skos/core#inScheme";
					String type = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
					statements.add(getStatementFromUris(resource, type, concept));
					statements.add(getStatementFromUris(resource, inScheme, codeList));
					uploadStatements(statements);
					Validation.this.getWindow().removeWindow(QuickFixCodesFromCodeLists.this);
					dimensionDefinitions();
				}
			});
			setContent(content);
			QuickFixCodesFromCodeLists.this.center();
		}
	}
	
	public void notifyCurrentGraphChange(String graph) {
        refresh();
    }

	@Override
	public void attach() {
		state.addCurrentGraphListener(this);
		refresh();
	}

	@Override
	public void detach() {
		state.removeCurrentGraphListener(this);
	}

}
