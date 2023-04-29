package agp.ajax;

public abstract class PageHandler {

	private String defaultPage;

	public abstract String handlePageRequest(String page, GenericSession session); 
	public abstract GenericResponse handleAjaxRequest(String payload, GenericSession session); 

	public String getDefaultPage() {
		return defaultPage;
	}
	public void setDefaultPage(String defaultPage) {
		this.defaultPage = defaultPage;
	}
}
