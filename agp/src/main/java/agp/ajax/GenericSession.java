package agp.ajax;

import java.util.Enumeration;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

public class GenericSession {

	private enum SessionType {
		SERVLET, PORTLET
	}

	private PortletSession portletSession;
	private HttpSession servletSession;
	private SessionType sessionType;

	public GenericSession(PortletSession portletSession) {
		this.sessionType = SessionType.PORTLET;
		this.portletSession = portletSession;
	}

	public GenericSession(HttpSession servletSession) {
		this.sessionType = SessionType.SERVLET;
		this.servletSession = servletSession;
	}
	
	public Object getAttribute(String name) {
		switch(sessionType) {
		  case SERVLET:
			 return servletSession.getAttribute(name);
		  case PORTLET:
			 return portletSession.getAttribute(name);
		}
		return null;
	}

	public Enumeration<?> getAttributeNames(){
		switch(sessionType) {
		  case SERVLET:
			 return servletSession.getAttributeNames();
		  case PORTLET:
			 return portletSession.getAttributeNames();
		}
		return null;
	}

    public void setAttribute(String name, Object value) {
		switch(sessionType) {
		  case SERVLET:
			 servletSession.setAttribute(name,value);
			 break;
		  case PORTLET:
			 portletSession.setAttribute(name,value);
			 break;
		}
    }

    public void removeAttribute(String name) {
		switch(sessionType) {
		  case SERVLET:
			 servletSession.removeAttribute(name);
			 break;
		  case PORTLET:
			 portletSession.removeAttribute(name);
			 break;
		}
    }
	
}
