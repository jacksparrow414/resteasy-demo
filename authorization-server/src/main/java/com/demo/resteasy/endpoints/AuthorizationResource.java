package com.demo.resteasy.endpoints;

import com.demo.resteasy.model.AppDataRepository;
import com.demo.resteasy.model.AuthorizationCode;
import com.demo.resteasy.model.Client;
import com.demo.resteasy.model.User;
import net.bytebuddy.utility.RandomString;

import javax.annotation.security.DenyAll;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Path("authorize")
public class AuthorizationResource {

    @Inject
    private AppDataRepository appDataRepository;

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
    @DenyAll
    public Response applyForUserAuthorization(@Context HttpServletRequest request,
                                      @Context HttpServletResponse response,
                                      @Context UriInfo uriInfo) throws ServletException, IOException {
        MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
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
        String clientError = "";
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
            //error = "invalid_grant :" + responseType + ", response_type params should be code or token:";
            //return informUserAboutError(error);
        }
        //Save params in session
        request.getSession().setAttribute("ORIGINAL_PARAMS", params);

        //4.scope: Optional
        String requestedScope = request.getParameter("scope");
        if (requestedScope == null || requestedScope.isEmpty()) {
            requestedScope = client.getScopes();
        }
//        Principal principal = securityContext.getCallerPrincipal();
//        User user = appDataRepository.getUser(principal.getName());
        User user = appDataRepository.getUser("appuser");
        String allowedScopes = checkUserScopes(user.getScopes(), requestedScope);
        request.setAttribute("scopes", allowedScopes);
        request.getRequestDispatcher("/authorize.jsp").forward(request, response);
        return null;
    }

    private String checkUserScopes(String userScopes, String requestedScope) {
        Set<String> allowedScopes = new LinkedHashSet<>();
        Set<String> rScopes = new HashSet(Arrays.asList(requestedScope.split(" ")));
        Set<String> uScopes = new HashSet(Arrays.asList(userScopes.split(" ")));
        for (String scope : uScopes) {
            if (rScopes.contains(scope)) allowedScopes.add(scope);
        }
        return String.join(" ", allowedScopes);
    }

    private Response informUserAboutError(HttpServletRequest request, HttpServletResponse response, String error) throws ServletException, IOException {
        request.setAttribute("error", error);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return null;
    }

    @DenyAll
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response userAuthorization(@Context HttpServletRequest request,
                                      @Context HttpServletResponse response,
                                      MultivaluedMap<String, String> params) throws ServletException, IOException {
        MultivaluedMap<String, String> originalParams = (MultivaluedMap<String, String>) request.getSession().getAttribute("ORIGINAL_PARAMS");
        if (originalParams == null) {
            return informUserAboutError(request, response, "No pending authorization request.");
        }
//        String redirectUri = originalParams.getFirst("resolved_redirect_uri");
        String redirectUri = "http://localhost:9180/callback";
        StringBuilder sb = new StringBuilder(redirectUri);

        String approvalStatus = params.getFirst("approval_status");
        if ("NO".equals(approvalStatus)) {
            URI location = UriBuilder.fromUri(sb.toString())
                    .queryParam("error", "User doesn't approved the request.")
                    .queryParam("error_description", "User doesn't approved the request.")
                    .build();
            return Response.seeOther(location).build();
        }

        //==> YES
        List<String> approvedScopes = params.get("scope");
        if (approvedScopes == null || approvedScopes.isEmpty()) {
            URI location = UriBuilder.fromUri(sb.toString())
                    .queryParam("error", "User doesn't approved the request.")
                    .queryParam("error_description", "User doesn't approved the request.")
                    .build();
            return Response.seeOther(location).build();
        }
        String responseType = originalParams.getFirst("response_type");
        String clientId = originalParams.getFirst("client_id");
        if ("code".equals(responseType)) {
//            String userId = securityContext.getCallerPrincipal().getName();
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
            sb.append("?code=").append(code);
        }
        // 回调第三方应用
        return Response.ok().entity(sb.toString()).build();
    }
}
