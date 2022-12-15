package org.demo.resteasy.filter;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.demo.resteasy.util.PEMKeyUtils;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * verify token and parse token from request
 * 通过全局Filter，可以达到根据角色判断是否有权限
 */
@Log
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;


    @Override
    @SneakyThrows
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
         // no need to check permissions
        if (method.isAnnotationPresent(DenyAll.class)) {
            log.info("no need to check permission");
            return;
        }
        // 特殊情况都处理完毕之后,开始正常处理token的解析和权限的校验
        verifyTokenAndPermission(containerRequestContext, method);
    }

    @SneakyThrows
    private void verifyTokenAndPermission(final ContainerRequestContext containerRequestContext, final Method method) {
        MultivaluedMap<String, String> headers = containerRequestContext.getHeaders();
        List<String> authorization = headers.get("Authorization");
        String token = authorization.get(0).substring("Bearer".length()).trim();
        // verify token
        JWSVerifier verifier = generateRsaJwsVerifier();
        SignedJWT jwt = SignedJWT.parse(token);
        if (!jwt.verify(verifier)) {
            containerRequestContext.abortWith(buildResponse(Response.Status.FORBIDDEN));
        }
        Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();

        // 这里第三方平台和认证服务器之间的user通过unionId关联，第三方平台数据库的用户表的关系 user_id , union_id
        // 如果认证服务器只是内部使用的话，也可以是userId
        String unionId = jwt.getJWTClaimsSet().getSubject();
        String scopes = (String) claims.get("scope");
        log.info("scopes is:\n" + scopes);
        List<String> parsedScopes = Arrays.asList(scopes.split("\\s+"));


        // verify permission
        if (method.isAnnotationPresent(RolesAllowed.class)) {
            RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
            String[] roles = rolesAnnotation.value();
            if (!parsedScopes.containsAll(Arrays.asList(roles))) {
                containerRequestContext.abortWith(buildResponse(Response.Status.FORBIDDEN));
            }
            containerRequestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> unionId;
                }

                @Override
                public boolean isUserInRole(String role) {
                    return parsedScopes.contains(role);
                }

                @Override
                public boolean isSecure() {
                    return true;
                }

                @Override
                public String getAuthenticationScheme() {
                    return "CLIENT_CERT";
                }
            });
        }
    }

    private JWSVerifier generateRsaJwsVerifier() throws Exception{
        String pemEncodedRSAPrivateKey = PEMKeyUtils.readKeyAsString("rsa/publish-key.pem");
        RSAKey rsaKey = (RSAKey) JWK.parseFromPEMEncodedObjects(pemEncodedRSAPrivateKey);
        return new RSASSAVerifier(rsaKey);
    }

    private Response buildResponse(Response.Status status) {
        return Response
                .status(status)
                .entity("{\"errmsg\": \"\"}")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
