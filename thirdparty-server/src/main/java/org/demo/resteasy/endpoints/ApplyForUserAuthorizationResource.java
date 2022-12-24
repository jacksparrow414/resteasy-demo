package org.demo.resteasy.endpoints;

import java.util.Base64;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.demo.resteasy.vo.TokenVO;

/**
 * 主要负责申请用户授权
 * @author jacksparrow414
 * @date 2021/12/4
 */
@Path("apply")
@Log
public class ApplyForUserAuthorizationResource {

    private static String redirectUri = "http://localhost:8080/thirdparty-server/third/apply/callback";
    @POST
    @SneakyThrows
    public void login(@Context HttpServletRequest request,
                        @Context HttpServletResponse response) {
        // 跳转到申请授权页面
        // 获取code
        // 获取access_token
        request.getSession().removeAttribute("tokenResponse");
        String state = UUID.randomUUID().toString();
        request.getSession().setAttribute("CLIENT_LOCAL_STATE", state);
    
        String authorizationUri = "http://localhost:8080/authorization-server/auth/authorize";
        String clientId = "webappclient";

//        String scope = config.getValue("client.scope", String.class);
    
        String authorizationLocation = authorizationUri + "?response_type=code"
            + "&client_id=" + clientId
            + "&redirect_uri=" + redirectUri
            + "&state=" + state
                +"&userId=" + "appuser";
        response.sendRedirect(authorizationLocation);
    }
    
    @GET
    @Path("callback")
    @Produces(MediaType.APPLICATION_JSON)
    @SneakyThrows
    public Response callback(@Context HttpServletRequest request,
                             @Context HttpServletResponse response) {
    
        String clientId = "webappclient";
        String clientSecret = "webappclientsecret";
    
        //Error:
        String error = request.getParameter("error");
        if (error != null) {
            request.setAttribute("error", error);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("获取access_token失败").build();
        }
        String localState = (String) request.getSession().getAttribute("CLIENT_LOCAL_STATE");
        if (!localState.equals(request.getParameter("state"))) {
            request.setAttribute("error", "The state attribute doesn't match !!");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("校验state失败").build();
        }
    
        String code = request.getParameter("code");
        // 内部直接调用authorization-server获取token
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/authorization-server/auth/token");
    
        Form form = new Form();
        form.param("grant_type", "authorization_code");
        form.param("code", code);
        form.param("redirect_uri", redirectUri);
        TokenVO tokenResponse = target.request(MediaType.APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(clientId, clientSecret))
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), TokenVO.class);

        request.setAttribute("token", tokenResponse);
        // TODO: 浏览器URL不会改变
        // https://stackoverflow.com/questions/29645344/null-pointer-exception-in-response-sendredirect

        // 将获取到的token显示在页面上
        request.getRequestDispatcher("/success.jsp").forward(request, response);
        return null;
    
    }
    
    private String getAuthorizationHeaderValue(String clientId, String clientSecret) {
        String token = clientId + ":" + clientSecret;
        String encodedString = Base64.getEncoder().encodeToString(token.getBytes());
        return "Basic " + encodedString;
    }
}
