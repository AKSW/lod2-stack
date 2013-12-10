package eu.lod2;

import java.util.List;
import java.util.Map;

public interface IConfiguration {
	
	public class PropertyValue {
		public String prop;
		public String val;
	}
	
	public List<String> getComponents();
	public Map<String, String> getComponentURLs();
	public List<PropertyValue> getComponentProperties(String component);
	public void setProperty(String component, String property, String value);
	public void setServiceURL(String component, String url);
	public String getHostname();
	public void setHostname(String hostname);

}
