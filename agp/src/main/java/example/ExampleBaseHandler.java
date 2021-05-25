package example;

import java.util.HashMap;
import java.util.Map;

import agp.ajax.AjaxHandler;
import agp.ajax.PageHandler;

public class ExampleBaseHandler extends AjaxHandler {

	//return the name of the JSP file you want to be loaded as default
	public String getDefaultPage() {
		return "example";
	}

	//return a map, that has the JSP name for a key and links it to a PageHandler implementation
	//you could use the same PageHandler for multiple JSP files
	@Override
	public Map<String, PageHandler> getPageHandlersMap() {
		Map<String,PageHandler> map = new HashMap<String,PageHandler>();
		map.put("example", new ExamplePageHandler());
		return map;
	}
}
