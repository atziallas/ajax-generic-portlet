package example;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;

import agp.ajax.GenericResponse;
import agp.ajax.GenericSession;
import agp.ajax.PageHandler;
import agp.ajax.ResponseType;


public class ExamplePageHandler implements PageHandler {
	@Override
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
			//respond with a Redirect URL that downloads a file
			byte[] file = Base64.decodeBase64("RVhBTVBMRSBGSUxFIFNFTlQ=");
			return new GenericResponse(ResponseType.FILE, file,"test.txt");
		default:
			return new GenericResponse(ResponseType.JSON, "");
		}
	}
}
