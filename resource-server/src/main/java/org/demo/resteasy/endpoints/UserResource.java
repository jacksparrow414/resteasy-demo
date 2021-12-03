package org.demo.resteasy.endpoints;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("user/protect")
public class UserResource {

    @GET
    @Path("read")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("resource.read")
    public String readProtectedInfo() {
        return "Read Success";
    }

    @POST
    @Path("write")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("resource.write")
    public String writeProtectedInfo(@FormParam("writeInfo") String writeInfo) {
        return "Write Success \n" + writeInfo;
    }
}
