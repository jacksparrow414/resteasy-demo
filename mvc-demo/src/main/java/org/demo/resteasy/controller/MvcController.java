package org.demo.resteasy.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.MvcContext;
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
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;

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
    
    @Inject
    private MvcContext mvcContext;

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
    
    @GET
    @Path("buildUrl")
    public Response buildUrl() {
        Map<String, Object> map = Map.of("path", "buildUrl");
        String path = mvcContext.uri("MvcController#helloMvc", map).getPath();
//        path is /mvc-demo/mvc/test/helloMvc/buildUrl; 由于请求中带有mvc-demo/mvc，所以需要去掉
        URI uri = UriBuilder.fromPath("../.."+path).buildFromMap(map);
        return Response.seeOther(uri).build();
    }
}
