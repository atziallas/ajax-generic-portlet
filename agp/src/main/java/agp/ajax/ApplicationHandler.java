package agp.ajax;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public abstract class ApplicationHandler {
	private Map<String, PageHandler> pageHandlers;

	public static final String REDIRECT = "REDIRECT";
	public static final String REDIRECT_URL = "REDIRECT_URL";
	public static final String FILENAME = "filename";
	public static final String PENDING_FILE = "pendingFile";
	public static final String PENDING = "pending";
	public static final String FILE_PARAMETER = "file";
	public static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";
	public static final String CONTENT_TYPE_JSON = "application/json";

	public ApplicationHandler (){
		pageHandlers = new HashMap<String, PageHandler>();
		init();
	}

	public PageHandler getPageHandler(String page) {
		return pageHandlers.get(page);
	}

	public GenericResponse handleAjaxRequest(String payload, String page, GenericSession session) {
		if (!pageHandlers.containsKey(page))
			throw new RuntimeException("No PageHandler for page: " + page);
		return pageHandlers.get(page).handleAjaxRequest(payload, session);
	}

	protected void addPageHandler(String page, PageHandler handler) {
		handler.setDefaultPage(getDefaultPage());
		pageHandlers.put(page, handler);
	}

	abstract public String getDefaultPage();

	abstract public void init();

	public static void convertResponseToRedirect(GenericResponse handlerResponse, String url) {
		Map<String, String> jsonResponse = new HashMap<String, String>();
		jsonResponse.put(REDIRECT_URL, url);
		handlerResponse.setResponse(new Gson().toJson(jsonResponse));
	}

}
