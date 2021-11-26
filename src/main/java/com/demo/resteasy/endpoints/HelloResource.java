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
import java.util.logging.Level;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 * basic use for RestEasy
 */
@Log
@Path("hello")
public class HelloResource {
    
    private final String UPLOADED_FILE_PATH = "D:\\tmp\\";
    
    private static List<UserVO> users = new ArrayList<>();

    /**
     * curl http:localhost:8080/rest/hello/jack
     */
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
     *  curl http:localhost:8080/rest/hello/advanced/jack
     */
    @GET
    @Path("advanced/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloRestEasy(@org.jboss.resteasy.annotations.jaxrs.PathParam String name) {
        return "Hi,there " + name;
    }

    /**
     * curl -X POST -H 'Content-Type: application/json' -d '{"userName":"jack", "age":18"}' http://localhost:8080/rest/hello/users
     */
    @POST
    @Path("users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid UserVO userVO) {
        users.add(userVO);
        return Response.ok().entity(userVO).build();
    }

    /**
     * curl http:localhost:8080/rest/hello/users/0
     */
    @GET
    @Path("users/{index}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryUser(@PathParam("index") Integer index) {
        checkParameter(index);
        UserVO targetUser = users.get(index);
        return Response.ok().entity(targetUser).build();
    }

    /**
     * curl -X DELETE http://localhost:8080/rest/hello/users/0
     */
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

    /**
     * 接收表单类型数据
     * curl -X POST -d 'username=jack&age=18' http://localhost/rest/hello/users
     */
    @POST
    @Path("users")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createFromFrom(@Form UserVO userVO) {
        if (Objects.isNull(userVO)) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        users.add(userVO);
        return Response.ok().build();
    }

    /**
     * 接受一个或多个文件.
     * curl -F 'fileName=@pictureLocation/upload.png' http://localhost:8080/rest/hello/file
     */
    @POST
    @Path("file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImage(MultipartFormDataInput input) {
        //Get API input data
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("fileName");
        if (Objects.isNull(inputParts)) {
            throw new WebApplicationException("no upload file, please upload file", Status.BAD_REQUEST);
        }
        StringBuilder uploadFileName = new StringBuilder();
        for (InputPart inputPart : inputParts) {
            // convert the uploaded file to inputstream
            try(InputStream inputStream = inputPart.getBody(InputStream.class, null)) {
                //Use this header for extra processing if required
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                String fileName = getFileName(header);
                uploadFileName.append(fileName).append(",");
                // 有内存溢出的危险
                byte[] bytes = IOUtils.toByteArray(inputStream);
                // constructs upload file path
                fileName = UPLOADED_FILE_PATH + fileName;
                writeFile(bytes, fileName);
                System.out.println("Success !!!!!");
            } catch (Exception e) {
                log.log(Level.WARNING, "upload file fail", e);
                throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
            }
        }
        return Response.status(200)
            .entity("File uploaded successfully.Uploaded file name : "+ uploadFileName.substring(0, uploadFileName.length()-1)).build();
    }

    /**
     * header sample
     * {
     * 	Content-Type=[image/png],
     * 	Content-Disposition=[form-data; name="file"; filename="filename.extension"]
     * }
     **/
    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                return name[1].trim().replaceAll("\"", "");
            }
        }
        return "unknown";
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

    /**
     * curl -o download.png http://localhost:8080/rest/hello/file/upload.png
     * @param fileName
     * @return
     */
    @GET
    @Path("file/{fileName}")
    @Produces("image/png")
    public Response downLoadImage(@org.jboss.resteasy.annotations.jaxrs.PathParam String fileName) {
        if(fileName == null || fileName.isEmpty()) {
            ResponseBuilder response = Response.status(Status.BAD_REQUEST);
            return response.build();
        }
        //Prepare a file object with file to return
        File file = new File(UPLOADED_FILE_PATH + fileName);
        ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment; filename="+fileName);
        return response.build();
    }
}
