package com.demo.resteasy.handler;

import com.demo.resteasy.vo.TokenErrorVO;
import com.demo.resteasy.vo.TokenVO;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;

import jakarta.inject.Named;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Named("refresh_token")
public class RefreshTokenGrantTypeHandler extends AbstractGrantTypeHandler{


    @Override
    public TokenVO createAccessToken(String clientId, MultivaluedMap<String, String> params) throws Exception {

        String refreshToken = params.getFirst("refresh_token");
        if (refreshToken == null || "".equals(refreshToken)) {
            throw new WebApplicationException("invalid_grant");
        }
        //Decode refresh token
        SignedJWT signedRefreshToken = SignedJWT.parse(refreshToken);
        JWSVerifier verifier = generateRsaJwsVerifier();

        if (!signedRefreshToken.verify(verifier)) {
            throw new WebApplicationException("Invalid refresh token.");
        }
        // refresh token expired, need to re-authorize
        if (!(new Date().before(signedRefreshToken.getJWTClaimsSet().getExpirationTime()))) {
            throw new WebApplicationException("Refresh token expired.");
        }
        //At this point, the refresh token is valid and not yet expired
        //So create a new access token from it.
        String subject = signedRefreshToken.getJWTClaimsSet().getSubject();
        String approvedScopes = signedRefreshToken.getJWTClaimsSet().getStringClaim("scope");

        String requestedScopes = params.getFirst("scope");
        if (requestedScopes != null && !requestedScopes.isEmpty()) {
            Set<String> rScopes = new HashSet(Arrays.asList(requestedScopes.split(" ")));
            Set<String> aScopes = new HashSet(Arrays.asList(approvedScopes.split(" ")));
            if (!aScopes.containsAll(rScopes)) {
                TokenErrorVO error = new TokenErrorVO("400", "error");
                Response response = Response.status(Response.Status.BAD_REQUEST).entity(error).build();
                throw new WebApplicationException(response);
            }
        } else {
            requestedScopes = approvedScopes;
        }

        String accessToken = generateAccessToken(clientId, subject, requestedScopes);
        TokenVO result = new TokenVO();
        result.setAccess_token(accessToken);
        result.setExpires_in(expiresInMilliseconds);
        result.setScope(requestedScopes);
        result.setRefresh_token(refreshToken);
        return result;
    }
}
