package example;

import agp.ajax.GenericResponse;
import agp.ajax.GenericSession;
import agp.ajax.PageHandler;
import agp.ajax.ResponseType;

public class AnotherPageHandler extends PageHandler {

    @Override
    public String handlePageRequest(String page, GenericSession session) {
        return page;

    }

    @Override
    public GenericResponse handleAjaxRequest(String payload, GenericSession session) {
        // TODO Auto-generated method stub
        return new GenericResponse(ResponseType.JSON, "");
    }
}
