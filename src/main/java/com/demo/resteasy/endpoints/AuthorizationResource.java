package com.demo.resteasy.endpoints;

import com.demo.resteasy.model.Client;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.IOException;

@Path("authorize")
public class AuthorizationResource {

    @GET
    public void userAuthorization(@Context HttpServletRequest request,
                                      @Context HttpServletResponse response) throws ServletException, IOException {

        Client client = new Client();
        client.setClientId("id");
        request.setAttribute("client", client);
        request.setAttribute("scopes", "scopeA, scopeB");
        request.getRequestDispatcher("/authorize.jsp").forward(request, response);
    }
}
