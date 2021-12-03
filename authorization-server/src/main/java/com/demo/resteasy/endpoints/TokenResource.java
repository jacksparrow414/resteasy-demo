package com.demo.resteasy.endpoints;

import com.demo.resteasy.handler.AuthorizationGrantTypeHandler;
import com.demo.resteasy.model.AppDataRepository;
import com.demo.resteasy.model.Client;
import com.demo.resteasy.vo.TokenErrorVO;
import com.demo.resteasy.vo.TokenVO;
import lombok.extern.java.Log;

import javax.annotation.security.DenyAll;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.literal.NamedLiteral;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;

@Log
@Path("token")
public class TokenResource {

    private static final List<String> supportedGrantTypes = Arrays.asList("authorization_code", "refresh_token");

    @Inject
    private AppDataRepository appDataRepository;

    @Inject
    private Instance<AuthorizationGrantTypeHandler> authorizationGrantTypeHandlers;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @DenyAll
    public Response token(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
                          MultivaluedMap<String, String> params) {
        //Check grant_type params
        String grantType = params.getFirst("grant_type");
        if (grantType == null || grantType.isEmpty()) {
            return responseError("Invalid_request", "grant_type is required", Response.Status.BAD_REQUEST);
        }
        if (!supportedGrantTypes.contains(grantType)) {
            return responseError("unsupported_grant_type", "grant_type should be one of :" + supportedGrantTypes, Response.Status.BAD_REQUEST);
        }

        //Client Authentication
        String[] clientCredentials = extract(authHeader);
        if (clientCredentials.length != 2) {
            return responseError("Invalid_request", "Bad Credentials client_id/client_secret", Response.Status.BAD_REQUEST);
        }
        String clientId = clientCredentials[0];
        Client client = appDataRepository.getClient(clientId);
        if (client == null) {
            return responseError("Invalid_request", "Invalid client_id", Response.Status.BAD_REQUEST);
        }
        String clientSecret = clientCredentials[1];
        if (!clientSecret.equals(client.getClientSecret())) {
            return responseError("Invalid_request", "Invalid client_secret", Response.Status.UNAUTHORIZED);
        }
        AuthorizationGrantTypeHandler authorizationGrantTypeHandler = authorizationGrantTypeHandlers.select(NamedLiteral.of(grantType)).get();
        TokenVO tokenResponse = null;
        try {
            tokenResponse = authorizationGrantTypeHandler.createAccessToken(clientId, params);
        }catch (Exception ex) {
            log.log(Level.WARNING, "acquire token failed", ex);
        }
        return Response.ok(tokenResponse)
                .header("Cache-Control", "no-store")
                .header("Pragma", "no-cache")
                .build();
    }

    private String[] extract(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            return new String(Base64.getDecoder().decode(authHeader.substring(6))).split(":");
        }
        return new String[]{};
    }

    private Response responseError(String error, String errorDescription, Response.Status status) {
        TokenErrorVO errorResponse = new TokenErrorVO(error, errorDescription);
        return Response.status(status)
                .entity(errorResponse).build();
    }
}
