package agp.ajax;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CsrfUtils {
	public static void validateCSRFToken(String page, String csrfToken, GenericSession session) {
		// check if the session contains this token for this page page
		List<String> pageTokens = (List<String>) session.getAttribute(page + "_token");
		if (!pageTokens.contains(csrfToken)) {
			throw new RuntimeException("Invalid CSRF Token");
		}
	}

	public static String registerCSRFToken(GenericSession session, String page) {
		List<String> pageTokens = (List<String>) session.getAttribute(page + "_token");
		if (pageTokens == null)
			pageTokens = new ArrayList<String>();
		if (pageTokens.size() > 20)
			pageTokens.remove(0);
		String generatedToken = UUID.randomUUID().toString();
		pageTokens.add(generatedToken);
		session.setAttribute(page + "_token", pageTokens);
		return generatedToken;
	}

}
