package com.demo.resteasy.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * handle all web exception. use WebApplicationException
 *
 * 负责处理具体Endpoint/Resource所抛出的WebApplicationException
 */
@Provider
public class GlobalWebExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException e) {
        return Response.status(e.getResponse().getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(new ExceptionData(e.getMessage()))
                .build();
    }
}
