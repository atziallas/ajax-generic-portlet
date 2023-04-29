package example;

import java.util.HashMap;
import java.util.Map;

import agp.ajax.ApplicationHandler;
import agp.ajax.PageHandler;

public class ExampleAppHandler extends ApplicationHandler {

	// each JSP file has a corresponding PageHandler to handle Ajax Requests
	@Override
	public void init() {
		// first argument is the name of the JSP file e.g. 'example.js'
		// second argument is the PageHandler that's going to be used for that JSP
		addPageHandler("example", new ExamplePageHandler());
		addPageHandler("another", new AnotherPageHandler());
	}

	//return the name of the JSP file you want to be loaded as default when the portlet loads
	@Override
	public String getDefaultPage() {
		return "example";
	}

}
