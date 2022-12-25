package org.demo.resteasy.endpoints;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Set;
import lombok.extern.java.Log;
import org.demo.resteasy.vo.Person;

/**
 * @author jacksparrow414
 * @date 2022/12/24
 */
@RequestScoped
@Path("inline")
@Log
public class ValidationResource {
    
//    @Inject
//    Validator validator;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateMessage(@Valid Person person) {
        return Response.ok().build();
    }
    
    @Path("validator")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateMessageByValidator(Person person) {
//        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);
//        constraintViolations.forEach(item -> {
//            log.info(item.getMessage());
//        });
        return Response.ok().build();
    }
}
