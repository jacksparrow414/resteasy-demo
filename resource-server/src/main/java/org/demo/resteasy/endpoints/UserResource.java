package org.demo.resteasy.endpoints;

import lombok.extern.java.Log;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
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
