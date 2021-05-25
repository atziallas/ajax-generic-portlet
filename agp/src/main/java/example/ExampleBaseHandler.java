package example;

import java.util.HashMap;
import java.util.Map;

import agp.ajax.AjaxHandler;
import agp.ajax.PageHandler;

public class ExampleBaseHandler extends AjaxHandler {

	public String getDefaultPage() {
		return "example";
	}

	@Override
	public Map<String, PageHandler> getPageHandlersMap() {
		Map<String,PageHandler> map = new HashMap<String,PageHandler>();
		map.put("example", new ExamplePageHandler());
		return map;
	}
}
