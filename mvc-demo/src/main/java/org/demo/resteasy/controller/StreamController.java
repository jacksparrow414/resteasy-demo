package org.demo.resteasy.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;

/**
 * @author jacksparrow414
 * @date 2023/7/8
 */
@RequestScoped
@Path("stream")
public class StreamController {
    
    @Context
    private HttpServletResponse response;
    
    @POST
    @Produces(MediaType.WILDCARD)
    @Path("getStream")
    public Response getStream() {
        Properties props = new Properties();
        props.put("test", "test");
        StreamingOutput streamingOutput = output -> {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(response.getOutputStream());
            objectOutputStream.writeObject(props);
        };
        return Response.ok().entity(streamingOutput).build();
    }
}
