package agp.ajax;

public interface PageHandler {
	public GenericResponse handleAjaxRequest(String payload, GenericSession session); 
}
