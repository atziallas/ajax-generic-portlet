package agp.ajax;

import static agp.ajax.ApplicationHandler.CONTENT_TYPE_HTML;
import static agp.ajax.ApplicationHandler.CONTENT_TYPE_JSON;
import static agp.ajax.ApplicationHandler.FILENAME;
import static agp.ajax.ApplicationHandler.FILE_PARAMETER;
import static agp.ajax.ApplicationHandler.PENDING;
import static agp.ajax.ApplicationHandler.PENDING_FILE;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;

import org.apache.commons.io.IOUtils;

public class AjaxPortlet extends GenericPortlet {

	ApplicationHandler appHandler;

	@Override
	public void init() {
		String handlerClass = getPortletConfig().getInitParameter("handler-class");
		Class<?> clazz;
		try {
			clazz = Class.forName(handlerClass);
			appHandler = (ApplicationHandler) clazz.newInstance();
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException("Incorrect parameter handler-class in portlet.xml");
		}
	}

	// Renders a page. Passes a resourceUrl to the jsp,
	// which the frontend framework will call to get data
	// Also generates a CSRF token per page, draws it on the page
	// and stores it to the session
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		String page = request.getParameter("page");

		// create a csrf token and store it to the session
		GenericSession session = new GenericSession(request.getPortletSession());

		if (page == null) // if this is the initial render
			page = appHandler.getDefaultPage();
		else // go through handlePageRequest to see if you have rights to show this page
			page = appHandler.getPageHandler(page).handlePageRequest(page, session);

		String csrfToken = CsrfUtils.registerCSRFToken(session, page);

		// create the resource url for AJAX calls
		ResourceURL url = response.createResourceURL();
		url.setParameter("page", page);

		// pass these parameters to the jsp file
		request.setAttribute("ajaxUrl", url);
		request.setAttribute("csrfToken", csrfToken);

		// get the jsp to render
		PortletRequestDispatcher rd = this.getPortletContext().getRequestDispatcher("/" + page + ".jsp");
		response.setContentType(CONTENT_TYPE_HTML);
		rd.include(request, response);
	}

	// all ajax communication is routed from this method
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException,UnsupportedEncodingException {
		GenericSession session = new GenericSession(request.getPortletSession());
		String payload = IOUtils.toString(request.getReader());
		HttpServletRequest httpRequest = (HttpServletRequest) request.getAttribute("httpRequest");
		String csrfToken = httpRequest.getHeader("csrf-token");
		String page = request.getParameter("page");

		GenericResponse handlerResponse;
		if (!CsrfUtils.validateCSRFToken(page, csrfToken, session)) {
			handlerResponse = new GenericResponse(ResponseType.JSON, "Invalid CSRF Token");
		} else {
			handlerResponse = appHandler.handleAjaxRequest(payload, page, session);
			if (handlerResponse.getContentType() == null) {
				response.setContentType(CONTENT_TYPE_JSON);
			}
			else {
				response.setContentType(handlerResponse.getContentType());
			}
			checkForRedirectResponse(handlerResponse, response);
			checkForFileResponse(handlerResponse, response, session);
		}

		PrintWriter writer = response.getWriter();
		writer.write(handlerResponse.getResponse());
	}

	// this means PageHandler replied with a file
	private void checkForFileResponse(GenericResponse handlerResponse, ResourceResponse response,
			GenericSession session) throws IOException {
		if (ResponseType.FILE.equals(handlerResponse.getResponseType())) {
			byte[] file = (byte[]) handlerResponse.getFile();
			OutputStream os = response.getPortletOutputStream();
			os.write(file);
			response.setProperty("Content-Disposition", "attachment; filename=" + handlerResponse.getResponse());
			os.close();
		}
	}

	private void checkForRedirectResponse(GenericResponse handlerResponse, ResourceResponse response) {
		// If the response demands a redirect to another page create a render url and
		// append it to the JSON
		// The redirect will be performed in javascript with window.location
		if (ResponseType.REDIRECT.equals(handlerResponse.getResponseType())) {
			PortletURL url = response.createRenderURL();
			// the page parameter defines which jsp will be loaded on doView method
			url.setParameter("page", handlerResponse.getResponse());
			ApplicationHandler.convertResponseToRedirect(handlerResponse, url.toString());
		}
	}
}
