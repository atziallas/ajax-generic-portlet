package example;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;

import agp.ajax.GenericResponse;
import agp.ajax.GenericSession;
import agp.ajax.PageHandler;
import agp.ajax.ResponseType;


public class ExamplePageHandler extends PageHandler {
	@Override
	// payload has the XHR request payload as String made by the client
	public GenericResponse handleAjaxRequest(String payload, GenericSession session) {
	    //the request payload and current portlet session are routed by the base AjaxHandler
		Map<String, Object> request = new Gson().fromJson(payload, Map.class);
		String argument = request.get("arg").toString();
		switch (argument) {
		case "test":
			Map<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("payload", "you sent:" + payload);
			//respond with a JSON
			return new GenericResponse(ResponseType.JSON, new Gson().toJson(responseMap));
		case "redirect":
			String redirectPage = "another";
			//respond with a Redirect URL to another JSP file
			return new GenericResponse(ResponseType.REDIRECT, redirectPage);
		case "file":
			//respond with a file
			byte[] file = Base64.decodeBase64("RVhBTVBMRSBGSUxFIFNFTlQ=");
			GenericResponse response = new GenericResponse(ResponseType.FILE, file,"test.txt");
			response.setContentType("text/plain");
			return response;
		default:
			return new GenericResponse(ResponseType.JSON, "");
		}
	}

	// this method checks if the user has the right to "view" this page
	// you can check the current session and return the page you want to be rendered
	// you could redirect to an error page here instead of rendering the page that was requested
	@Override
	public String handlePageRequest(String page, GenericSession session) {
		return page;
	}
}
