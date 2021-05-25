# ajax-generic-portlet

You wanted to use your favourite JS framework on your java portlet, but you don't want to be tied with an Ajax/REST
implementation of a specific Java Portal implementation?

Now you can! **Portal agnostic** AJAX implementation that supports the JSR-286 specification (Java Portlet Specification
2).

Utilizes **JSR-286 serveResource** lifecycle method to handle AJAX request and serve JSON to the client.

<a href="https://github.com/atziallas/ajax-generic-portlet/blob/main/LICENSE">
<img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="React Native is released under the MIT license." data-canonical-src="https://img.shields.io/badge/license-MIT-blue.svg" style="max-width:100%;">
</a>

## 📋 Features

- Plain JSP files are used for each portlet page. URL generation is also possible for clients to use
  with `window.location.href`:

    - to redirect to another JSP

    - to serve a file to the user


- Generates a CSRF token on first view of a page and demands it to be sent for every subsequent AJAX request to prevent
  cross site attacks.


- Can also be used as a servlet instead of a portlet through the same API, to facilitate developers that don't wish to
  use a portal for local development.


- Repository contains a Dockerized proof of concept that uses Liferay Portal CE version 7.4.0-ga1

## 🚀 How to run example
Run `docker-compose up` inside base folder (next to docker-compose.yml)

When the installation and deployment finishes visit localhost:8080.

Click on the pencil on the top right to edit the home page and select the widgets column from the sidebar that popped on the right. Drag Ajax Generic Portlet somewhere inside the page and you are ready to go.


Note: if you are running docker on windows, make sure /liferay/start.sh file has linux style endings (LF, not CRLF)

## 📖 Documentation

Core implementation resides inside agp/src/main/java/agp/ajax folder

These are the steps you need to follow to utilize the generic AjaxPortlet class:

Update your web.xml as follows:

- add a filter that uses agp.ajax.PortletEnchancer:

```xml
<filter>
    <filter-name>ajaxgenericportlet</filter-name>
    <filter-class>agp.ajax.PortletEnhancer</filter-class>
</filter>
```

- add the parameter portlet-class that points to AjaxPortlet:

```xml
<servlet>
    <servlet-name>AjaxPortletServlet</servlet-name>
    <servlet-class>com.liferay.portal.kernel.servlet.PortletServlet</servlet-class>
    <init-param>
        <param-name>portlet-class</param-name>
        <param-value>agp.ajax.AjaxPortlet</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
```
<br/>
Update your portlet.xml to declare the appropriate portlet class and define your base handler (AjaxHandler) that will
serve JSP files

```xml
<portlet>
    ...
    <portlet-class>agp.ajax.AjaxPortlet</portlet-class>
    <init-param>
        <name>handler-class</name>
        <value>example.ExampleBaseHandler</value>
    </init-param>
    ...
</portlet>
```

<br/>
The base handler that AjaxHandler and routes to the appropriate PageHandler:

```java
public class ExampleBaseHandler extends AjaxHandler {

    //return the name of the JSP file you want to be loaded as default
    public String getDefaultPage() {
        return "example";
    }

    //Return a map, that links a JSP file name (the map key) to a PageHandler implementation.
    //AjaxHandler will route the request payload to the underlying page handler
    //You could use the same PageHandler for multiple JSP files
    @Override
    public Map<String, PageHandler> getPageHandlersMap() {
        Map<String, PageHandler> map = new HashMap<String, PageHandler>();
        map.put("example", new ExamplePageHandler());
        return map;
    }
}
```

<br/>
A page handler for a specific page:

```java
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
                return new GenericResponse(ResponseType.FILE, file, "test.txt");
            default:
                return new GenericResponse(ResponseType.JSON, "");
        }
    }
}
```

All the code above is taken from repository itself, so you can check it in the whole application context.

