package org.demo.resteasy.endpoints;

import lombok.extern.java.Log;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.util.logging.Level;

@Path("user/protect")
@RequestScoped
@Log
public class UserResource {

    @GET
    @Path("read")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("resource.read")
    public String readProtectedInfo(@Context SecurityContext securityContext) {
        log.info( "unionId: " + securityContext.getUserPrincipal().getName());
        return "Read Success";
    }

    @POST
    @Path("write")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("resource.write")
    public String writeProtectedInfo(@FormParam("writeInfo") String writeInfo, @Context SecurityContext securityContext) {
        log.log(Level.INFO, "unionId" + securityContext.getUserPrincipal().getName());
        return "Write Success \n" + writeInfo;
    }
}
