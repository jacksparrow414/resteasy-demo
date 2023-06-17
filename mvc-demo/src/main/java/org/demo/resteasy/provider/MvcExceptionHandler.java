package org.demo.resteasy.provider;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.mvc.security.CsrfValidationException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.SneakyThrows;

/**
 * @author jacksparrow414
 * @date 2023/6/17
 */
@Provider
@Priority(Priorities.USER)
public class MvcExceptionHandler implements ExceptionMapper<CsrfValidationException> {
    
    @Context
    private HttpServletResponse response;
    
    @Context
    public HttpServletRequest request;
    
    @Context
    private ServletContext servletContext;
    
    @SneakyThrows
    @Override
    public Response toResponse(final CsrfValidationException e) {
        request.setAttribute("errors", e.getMessage());
//        两种写法都可以
//        servletContext.getRequestDispatcher("/WEB-INF/views/csrf.jsp").forward(request, response);
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/csrf.jsp").forward(request, response);
        return null;
    }
}
