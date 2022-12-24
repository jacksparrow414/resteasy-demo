package com.demo.resteasy.endpoints;

import com.demo.resteasy.model.AppDataRepository;
import com.demo.resteasy.model.AuthorizationCode;
import com.demo.resteasy.model.Client;
import com.demo.resteasy.model.User;
import net.bytebuddy.utility.RandomString;

import jakarta.annotation.security.DenyAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 这里把所有权限相关的注解全部注释掉的原因是，
 * 1、 认证服务器和资源服务器用于公司内部平台使用，不对外提供服务.
 * 2、 认证服务器和资源服务器对外提供服务，授权的时候还需要加一步用户登录。那么这一步可能的思路是：采用OAuth2-password模式登录，登录成功之后再拉起授权页面
 */
@Path("authorize")
@RequestScoped
public class AuthorizationResource {

    @Inject
    private AppDataRepository appDataRepository;

    @Context
    private SecurityContext securityContext;

    private static Map<Integer, String> stateMap = new HashMap<>();

    /**
     * 申请用户授权.
     *
     * @param request
     * @param response
     * @param uriInfo
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @GET
//    @DenyAll
    public Response applyForUserAuthorization(@Context HttpServletRequest request,
                                      @Context HttpServletResponse response,
                                      @Context UriInfo uriInfo) throws ServletException, IOException {
        MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
        String state = params.getFirst("state");
        // TODO: 实际业务中使用其他方式存储
        if (state.length() > 0) {
            stateMap.put(1, state);
        }
        //1. client_id
        String clientId = params.getFirst("client_id");
        if (clientId == null || clientId.isEmpty()) {
            return informUserAboutError(request, response, "Invalid client_id :" + clientId);
        }
        // 判断clientId是否在认证服务器中
        Client client = appDataRepository.getClient(clientId);
        if (client == null) {
            return informUserAboutError(request, response, "Invalid client_id :" + clientId);
        }
        //2. Client Authorized Grant Type
        if (client.getAuthorizedGrantTypes() != null && !client.getAuthorizedGrantTypes().contains("authorization_code")) {
            return informUserAboutError(request, response, "Authorization Grant type, authorization_code, is not allowed for this client :" + clientId);
        }
        //3. redirectUri
        String redirectUri = params.getFirst("redirect_uri");
        if (client.getRedirectUrl() != null && !client.getRedirectUrl().isEmpty()) {
            if (redirectUri != null && redirectUri.isEmpty() && !client.getRedirectUrl().equals(redirectUri)) {
                //sould be in the client.redirectUri
                return informUserAboutError(request, response, "redirect_uri is pre-registred and should match");
            }
            redirectUri = client.getRedirectUrl();
//            params.putSingle("resolved_redirect_uri", redirectUri);
        } else {
            if (redirectUri == null || redirectUri.isEmpty()) {
                return informUserAboutError(request, response, "redirect_uri is not pre-registred and should be provided");
            }
            params.putSingle("resolved_redirect_uri", redirectUri);
        }
        request.setAttribute("client", client);

        //4. response_type
        String responseType = params.getFirst("response_type");
        if (!"code".equals(responseType) && !"token".equals(responseType)) {
            return informUserAboutError(request, response, "invalid_grant :" + responseType + ", response_type params should be code or token:");
        }
        //Save params in session
        request.getSession().setAttribute("ORIGINAL_PARAMS", params);

        //4.scope: Optional
        String requestedScope = request.getParameter("scope");
        if (requestedScope == null || requestedScope.isEmpty()) {
            requestedScope = client.getScopes();
        }
        //5. user principal, common userId,这里不再使用SecurityFilter
//        Principal principal = securityContext.getUserPrincipal();
//        String userId = principal.getName();
        User user = appDataRepository.getUser("appuser");
        String allowedScopes = checkUserScopes(user.getScopes(), requestedScope);
        request.setAttribute("scopes", allowedScopes);
        // 转发至授权页面
        // TODO 实际情况可能要更复杂一些，这里其实是假设用户已登录的情况下。所以还差一个用户未登录时需要用户先登录再拉起授权页面. 如最上面的注释
        request.getRequestDispatcher("/authorize.jsp").forward(request, response);
        return null;
    }

    private String checkUserScopes(String userScopes, String requestedScope) {
        Set<String> allowedScopes = new LinkedHashSet<>();
        Set<String> rScopes = new HashSet(Arrays.asList(requestedScope.split(" ")));
        Set<String> uScopes = new HashSet(Arrays.asList(userScopes.split(" ")));
        for (String scope : uScopes) {
            if (rScopes.contains(scope)) {
                allowedScopes.add(scope);
            }
        }
        return String.join(" ", allowedScopes);
    }

    private Response informUserAboutError(HttpServletRequest request, HttpServletResponse response, String error) throws ServletException, IOException {
        request.setAttribute("error", error);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return null;
    }

//    @DenyAll
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void userAuthorization(@Context HttpServletRequest request,
                                      @Context HttpServletResponse response,
                                      MultivaluedMap<String, String> params) throws ServletException, IOException {
        MultivaluedMap<String, String> originalParams = (MultivaluedMap<String, String>) request.getSession().getAttribute("ORIGINAL_PARAMS");
        if (originalParams == null) {
             informUserAboutError(request, response, "No pending authorization request.");
        }
//        String redirectUri = originalParams.getFirst("resolved_redirect_uri");
        String redirectUri = "http://localhost:8080/thirdparty-server/third/apply/callback";
        StringBuilder sb = new StringBuilder(redirectUri);
        sb.append("?state=").append(stateMap.get(1));
        String approvalStatus = params.getFirst("approval_status");
        if ("NO".equals(approvalStatus)) {
            URI location = UriBuilder.fromUri(sb.toString())
                    .queryParam("error", "User doesn't approved the request.")
                    .queryParam("error_description", "User doesn't approved the request.")
                    .build();
             Response.seeOther(location).build();
        }

        //==> YES
        List<String> approvedScopes = params.get("scope");
        if (approvedScopes == null || approvedScopes.isEmpty()) {
            URI location = UriBuilder.fromUri(sb.toString())
                    .queryParam("error", "User doesn't approved the request.")
                    .queryParam("error_description", "User doesn't approved the request.")
                    .build();
             Response.seeOther(location).build();
        }
        String responseType = originalParams.getFirst("response_type");
        String clientId = originalParams.getFirst("client_id");
        if ("code".equals(responseType)) {
//            String userId = securityContext.getUserPrincipal().getName();
            String userId = "appuser";
            AuthorizationCode authorizationCode = new AuthorizationCode();
            authorizationCode.setCode(RandomString.make(15));
            authorizationCode.setClientId(clientId);
            authorizationCode.setUserId(userId);
            authorizationCode.setApprovedScopes(String.join(" ", approvedScopes));
            authorizationCode.setExpirationDate(LocalDateTime.now().plusMinutes(10));
            authorizationCode.setRedirectUrl(redirectUri);
            appDataRepository.save(authorizationCode);
            String code = authorizationCode.getCode();
            sb.append("&code=").append(code);
        }
        // 回调第三方应用
        response.sendRedirect(sb.toString());
    }
}
