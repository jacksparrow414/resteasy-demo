package com.demo.resteasy.endpoints;

import com.demo.resteasy.vo.UserVO;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * basic use for RestEasy
 */
@Path("hello")
public class HelloResource {

    private static List<UserVO> users = new ArrayList<>();

    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("name") String name) {
        return "Hello " + name.toUpperCase();
    }

    @POST
    @Path("users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid UserVO userVO) {
        users.add(userVO);
        return Response.ok().entity(userVO).build();
    }

    @GET
    @Path("users/{index}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryUser(@PathParam("index") Integer index) {
        checkParameter(index);
        UserVO targetUser = users.get(index);
        return Response.ok().entity(targetUser).build();
    }

    @DELETE
    @Path("users/{index}")
    public Response deleteUser(@PathParam("index") Integer index) {
        checkParameter(index);
        users.remove(index);
        return Response.ok().build();
    }

    private void checkParameter(Integer index) {
        if (users.size() == 0 || Objects.isNull(index) ||  index < 0 || index >= users.size()) {
            throw new WebApplicationException("user Not Found, please enter valid index", Response.Status.NOT_FOUND);
        }
    }
}
