package agp.ajax;

import java.util.Map;

public class GenericResponse {
	private ResponseType responseType;
	private String response;
	private byte[] file;
	private Map<String,String> headers;
	private String contentType;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public GenericResponse(ResponseType responseType, String response) {
		this.responseType = responseType;
		this.response = response;
	}

	public GenericResponse(ResponseType responseType, byte[] file, String fileName) {
		this.responseType = responseType;
		this.response = fileName;
		this.file = file;
	}
	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

}
