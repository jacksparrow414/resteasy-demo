package org.demo.resteasy.endpoints;

import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.demo.resteasy.vo.Person;

/**
 * @author jacksparrow414
 * @date 2022/12/24
 */
@RequestScoped
@Path("inline")
public class ValidationResource {
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateMessage(@Valid Person person) {
        return Response.ok().build();
    }
}
