package com.demo.resteasy.endpoints;

import com.demo.resteasy.vo.TokenVO;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("token")
public class TokenResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response token() {
        TokenVO tokenResponse = null;
        return Response.ok(tokenResponse)
                .header("Cache-Control", "no-store")
                .header("Pragma", "no-cache")
                .build();
    }
}
