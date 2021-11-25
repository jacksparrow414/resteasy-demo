package com.demo.resteasy.endpoints;

import com.demo.resteasy.vo.UserVO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 * basic use for RestEasy
 */
@Path("hello")
public class HelloResource {
    
    private final String UPLOADED_FILE_PATH = "c:/temp/";
    
    private static List<UserVO> users = new ArrayList<>();

    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("name") String name) {
        return "Hello " + name.toUpperCase();
    }
    
    /**
     * RestEasy提供了高级的@PathParam注解，可以不不用声明path的值，只要变量名字和路径变量一致即可
     * <a href="https://docs.jboss.org/resteasy/docs/3.8.1.Final/userguide/html/_NewParam.html">使用文档</a>
     *  搭配Maven compiler插件使用
     */
    @GET
    @Path("advanced/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloRestEasy(@org.jboss.resteasy.annotations.jaxrs.PathParam String name) {
        return "Hi,there " + name;
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
    
    @POST
    @Path("users")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createFromFrom(@Form UserVO userVO) {
        if (Objects.isNull(userVO)) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        return Response.ok().build();
    }
    
    @POST
    @Path("file")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImage(MultipartFormDataInput input) throws IOException {
        //Get API input data
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
    
        //Get file name
        String fileName = uploadForm.get("fileName").get(0).getBodyAsString();
    
        //Get file data to save
        List<InputPart> inputParts = uploadForm.get("attachment");
    
        for (InputPart inputPart : inputParts)
        {
            try
            {
                //Use this header for extra processing if required
                @SuppressWarnings("unused") MultivaluedMap<String, String> header = inputPart.getHeaders();
            
                // convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
            
                byte[] bytes = IOUtils.toByteArray(inputStream);
                // constructs upload file path
                fileName = UPLOADED_FILE_PATH + fileName;
                writeFile(bytes, fileName);
                System.out.println("Success !!!!!");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return Response.status(200)
            .entity("Uploaded file name : "+ fileName).build();
    }
    
    private void writeFile(byte[] content, String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
    }
    
    @GET
    @Path("file/{fileName}")
    @Produces("image/jpeg")
    public Response downLoadImage(@org.jboss.resteasy.annotations.jaxrs.PathParam String fileName) {
        if(fileName == null || fileName.isEmpty())
        {
            ResponseBuilder response = Response.status(Status.BAD_REQUEST);
            return response.build();
        }
    
        //Prepare a file object with file to return
        File file = new File("c:/demoJpegFile.jpeg");
    
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=howtodoinjava.jpeg");
        return response.build();
    }
}
