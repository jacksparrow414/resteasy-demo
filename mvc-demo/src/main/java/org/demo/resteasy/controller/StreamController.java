package org.demo.resteasy.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.ObjectOutputStream;
import java.util.Properties;

/**
 * @author jacksparrow414
 * @date 2023/7/8
 */
@RequestScoped
@Path("stream")
public class StreamController {
    
    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("getStream")
    public Response getStream() {
        Properties props = new Properties();
        props.put("test", "test");
        StreamingOutput streamingOutput = output -> {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(output);
            objectOutputStream.writeObject(props);
        };
        return Response.ok().entity(streamingOutput).build();
    }
}
