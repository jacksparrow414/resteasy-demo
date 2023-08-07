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
        
        // 当返回的是PrintWriter时，可以这样，要设置内容编码
        //StreamingOutput streamingOutput = output -> {
        //    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8)));
       //     writer.wtrite("<h1>test</h1>")
       // };

        // 传统Servlet做法，同样要设置内容编码
        //response.setContentType("text/plain; charset=UTF-8")
        //PrintWriter writer = response.getWriter();

        
        return Response.ok().entity(streamingOutput).build();
    }
}
