package agp.ajax;

import static agp.ajax.AjaxHandler.FILENAME;
import static agp.ajax.AjaxHandler.FILE_PARAMETER;
import static agp.ajax.AjaxHandler.PENDING;
import static agp.ajax.AjaxHandler.PENDING_FILE;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class AjaxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	AjaxHandler handler;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String handlerClass = getServletConfig().getInitParameter("handler-class");
		Class<?> clazz;
		try {
			clazz = Class.forName(handlerClass);
			handler = (AjaxHandler) clazz.newInstance();
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException("Incorrect parameter handler-class in portlet.xml");
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		GenericSession session = new GenericSession(request.getSession());
		if (PENDING.equals(request.getParameter(FILE_PARAMETER))) {
			fileResponse(response, session);
		} else {
			renderResponse(request, response, session);
		}
	}

	private void renderResponse(HttpServletRequest request, HttpServletResponse response, GenericSession session)
			throws ServletException, IOException {
		String page = request.getParameter("page");
		if (page == null) // if this is the initial render
			page = handler.getDefaultPage();
		String csrfToken = CsrfUtils.registerCSRFToken(session, page);

		request.setAttribute("ajaxUrl", request.getRequestURL().toString() + "?page=" + page);
		request.setAttribute("csrfToken", csrfToken);

		RequestDispatcher disp = request.getRequestDispatcher(page + ".jsp");
		disp.forward(request, response);
	}

	private void fileResponse(HttpServletResponse response, GenericSession session) throws IOException {
		byte[] file = (byte[]) session.getAttribute(PENDING_FILE);
		OutputStream os = response.getOutputStream();
		os.write(file);
		response.setHeader("Content-Disposition", "attachment; filename=" + session.getAttribute(FILENAME));
		os.close();
		session.removeAttribute(FILENAME);
		session.removeAttribute(PENDING_FILE);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String payload = IOUtils.toString(request.getReader());
		String csrfToken = request.getHeader("csrf-token");
		GenericSession session = new GenericSession(request.getSession());
		String page = request.getParameter("page");

		CsrfUtils.validateCSRFToken(page, csrfToken, session);

		GenericResponse handlerResponse = handler.handleAjaxRequest(payload, page, session);
		checkForFileResponse(handlerResponse, request, session);
		checkForRedirectResponse(handlerResponse, request);

		PrintWriter writer = response.getWriter();
		writer.write(handlerResponse.getResponse());
	}

	private void checkForFileResponse(GenericResponse handlerResponse, HttpServletRequest request,
			GenericSession session) {
		if (ResponseType.FILE.equals(handlerResponse.getResponseType())) {
			String url = request.getRequestURL().toString() + "?" + FILE_PARAMETER + "=" + PENDING;
			session.setAttribute(PENDING_FILE, handlerResponse.getFile());
			session.setAttribute(FILENAME, handlerResponse.getResponse());
			AjaxHandler.convertResponseToRedirect(handlerResponse, url);
		}
	}

	private void checkForRedirectResponse(GenericResponse handlerResponse, HttpServletRequest request) {
		if (ResponseType.REDIRECT.equals(handlerResponse.getResponseType())) {
			String url = request.getRequestURL().toString() + "?page=" + handlerResponse.getResponse();
			AjaxHandler.convertResponseToRedirect(handlerResponse, url.toString());
		}
	}
}
