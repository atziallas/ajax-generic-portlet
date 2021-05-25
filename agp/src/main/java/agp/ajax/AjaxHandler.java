package agp.ajax;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;


public abstract class AjaxHandler {
	public static final String REDIRECT = "REDIRECT";
	public static final String REDIRECT_URL = "REDIRECT_URL";
	public static final String FILENAME = "filename";
	public static final String PENDING_FILE = "pendingFile";
	public static final String PENDING = "pending";
	public static final String FILE_PARAMETER = "file";
	public static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";
	public static final String CONTENT_TYPE_JSON = "application/json";
	
	public GenericResponse handleAjaxRequest(String payload,String page, GenericSession session){
		if (!getPageHandlersMap().containsKey(page)) throw new RuntimeException("No PageHandler for page: "+page);
		return getPageHandlersMap().get(page).handleAjaxRequest(payload, session);
	}
	abstract public String getDefaultPage();
	
	abstract public Map<String,PageHandler> getPageHandlersMap();
	
	public static void convertResponseToRedirect(GenericResponse handlerResponse, String url) {
		Map<String, String> jsonResponse = new HashMap<String, String>();
		jsonResponse.put(REDIRECT_URL, url);
		handlerResponse.setResponse(new Gson().toJson(jsonResponse));
	}
	
}
