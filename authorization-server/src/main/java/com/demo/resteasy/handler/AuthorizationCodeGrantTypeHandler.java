package com.demo.resteasy.handler;

import com.demo.resteasy.model.AppDataRepository;
import com.demo.resteasy.model.AuthorizationCode;
import com.demo.resteasy.model.User;
import com.demo.resteasy.util.JPAUtil;
import com.demo.resteasy.vo.TokenVO;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import java.time.LocalDateTime;
import java.util.Objects;

@Named("authorization_code")
public class AuthorizationCodeGrantTypeHandler extends AbstractGrantTypeHandler{

    private EntityManager entityManager = JPAUtil.acquireEntityManager();

    @Inject
    private AppDataRepository appDataRepository;

    @Override
    public TokenVO createAccessToken(String clientId, MultivaluedMap<String, String> params) throws Exception {
        //1. code is required
        String code = params.getFirst("code");
        if (code == null || "".equals(code)) {
            throw new WebApplicationException("invalid_grant");
        }
        AuthorizationCode authorizationCode = entityManager.find(AuthorizationCode.class, code);
        if (!authorizationCode.getExpirationDate().isAfter(LocalDateTime.now())) {
            throw new WebApplicationException("code Expired !");
        }
        String redirectUri = params.getFirst("redirect_uri");
        //redirecturi match
        if (authorizationCode.getRedirectUrl() != null && !authorizationCode.getRedirectUrl().equals(redirectUri)) {
            //redirectUri params should be the same as the requested redirectUri.
            throw new WebApplicationException("invalid_grant");
        }
        //client match
        if (!clientId.equals(authorizationCode.getClientId())) {
            throw new WebApplicationException("invalid_grant");
        }
        String accessToken = generateAccessToken(clientId, authorizationCode.getUserId(), authorizationCode.getApprovedScopes());
        String refreshToken = generateRefreshToken(clientId, authorizationCode.getUserId(), authorizationCode.getApprovedScopes());
        TokenVO result = new TokenVO();
        result.setAccess_token(accessToken);
        result.setExpires_in(expiresInMilliseconds);
        result.setScope(authorizationCode.getApprovedScopes());
        result.setRefresh_token(refreshToken);
        return result;
    }
}
