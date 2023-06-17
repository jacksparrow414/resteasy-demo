package org.demo.resteasy.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.mvc.binding.BindingResult;
import jakarta.mvc.binding.MvcBinding;
import jakarta.mvc.security.CsrfProtected;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import net.bytebuddy.implementation.bind.MethodDelegationBinder.BindingResolver;

/**
 * @author jacksparrow414
 * @date 2023/6/17
 */
@RequestScoped
@Controller
@Path("test")
public class MvcController {

    @Inject
    private Models models;
    
    /**
     * 获得校验结果
     */
    @Inject
    private BindingResult bindingResult;

    @GET
    @Path("helloMvc/{path}")
    public Response helloMvc(@PathParam("path") String path) {
        models.put("message", "Hello MVC, " + path);
        return Response.ok("helloMvc.jsp").build();
    }
    
    @DELETE
    @Path("deleteMvc")
    public String deleteMvc(@FormParam("message") @MvcBinding @NotBlank String message) {
        if (bindingResult.isFailed()) {
            models.put("errors", bindingResult.getAllMessages());
            return "deleteMvcResult.jsp";
        }
        models.put("message", message);
        return "deleteMvcResult.jsp";
    }
    
    @POST
    @Path("csrf")
    @CsrfProtected
    @View("csrf.jsp")
    public void csrf() {
        models.put("message", "csrf");
    }
}
