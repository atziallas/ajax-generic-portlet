package agp.ajax;

import static agp.ajax.AjaxHandler.CONTENT_TYPE_HTML;
import static agp.ajax.AjaxHandler.CONTENT_TYPE_JSON;
import static agp.ajax.AjaxHandler.FILENAME;
import static agp.ajax.AjaxHandler.FILE_PARAMETER;
import static agp.ajax.AjaxHandler.PENDING;
import static agp.ajax.AjaxHandler.PENDING_FILE;

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

import org.apache.commons.io.IOUtils;

public class AjaxPortlet extends GenericPortlet {

	AjaxHandler handler;

	@Override
	public void init() {
		String handlerClass = getPortletConfig().getInitParameter("handler-class");
		Class<?> clazz;
		try {
			clazz = Class.forName(handlerClass);
			handler = (AjaxHandler) clazz.newInstance();
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
		if (page == null) // if this is the initial render
			page = handler.getDefaultPage();

		// create a csrf token and store it to the session
		GenericSession session = new GenericSession(request.getPortletSession());
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

	// Responds only with JSON. Redirect to another page is possible by creating and
	// sending a url to javascript.
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		GenericSession session = new GenericSession(request.getPortletSession());
		if (PENDING.equals(request.getParameter(FILE_PARAMETER))) {
			fileResponse(response, session);
		} else {
			jsonResponse(request, response, session);
		}
	}

	private void jsonResponse(ResourceRequest request, ResourceResponse response, GenericSession session)
			throws IOException, UnsupportedEncodingException {
		String payload = IOUtils.toString(request.getReader());
		HttpServletRequest httpRequest = (HttpServletRequest) request.getAttribute("httpRequest");
		String csrfToken = httpRequest.getHeader("csrf-token");
		String page = request.getParameter("page");
		CsrfUtils.validateCSRFToken(page, csrfToken, session);

		GenericResponse handlerResponse = handler.handleAjaxRequest(payload, page, session);
		checkForRedirectResponse(handlerResponse, response);
		checkForFileResponse(handlerResponse, response, session);

		response.setContentType(CONTENT_TYPE_JSON);
		PrintWriter writer = response.getWriter();
		writer.write(handlerResponse.getResponse());
	}

	// this means previous response created a resource URL and stored a file in the
	// session.
	private void fileResponse(ResourceResponse response, GenericSession session) throws IOException {
		byte[] file = (byte[]) session.getAttribute(PENDING_FILE);
		OutputStream os = response.getPortletOutputStream();
		os.write(file);
		response.setProperty("Content-Disposition", "attachment; filename=" + session.getAttribute(FILENAME));
		os.close();
		session.removeAttribute(FILENAME);
		session.removeAttribute(PENDING_FILE);
	}

	private void checkForFileResponse(GenericResponse handlerResponse, ResourceResponse response,
			GenericSession session) {
		if (ResponseType.FILE.equals(handlerResponse.getResponseType())) {
			// create a resource for this file and store it in the session
			ResourceURL url = response.createResourceURL();
			url.setParameter(FILE_PARAMETER, PENDING);
			session.setAttribute(PENDING_FILE, handlerResponse.getFile());
			session.setAttribute(FILENAME, handlerResponse.getResponse());

			AjaxHandler.convertResponseToRedirect(handlerResponse, url.toString());
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
			AjaxHandler.convertResponseToRedirect(handlerResponse, url.toString());
		}
	}
}
