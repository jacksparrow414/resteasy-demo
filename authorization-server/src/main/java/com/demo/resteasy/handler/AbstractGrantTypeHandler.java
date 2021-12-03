package com.demo.resteasy.handler;

import com.demo.resteasy.util.PEMKeyUtils;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * https://jwt.io/ 各种语言JWT实现
 */
@Log
public abstract class AbstractGrantTypeHandler implements AuthorizationGrantTypeHandler{

    protected static final int expiresInMilliseconds = 30 * 60 * 1000;

    private static final JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();

    private JWSSigner generateRsaJwsSigner() throws Exception{
        String pemEncodedRSAPrivateKey = PEMKeyUtils.readKeyAsString("rsa/private-key.pem");
        RSAKey rsaKey = (RSAKey) JWK.parseFromPEMEncodedObjects(pemEncodedRSAPrivateKey);
        return new RSASSASigner(rsaKey.toRSAPrivateKey());
    }

    protected JWSVerifier generateRsaJwsVerifier() throws Exception{
        String pemEncodedRSAPrivateKey = PEMKeyUtils.readKeyAsString("rsa/publish-key.pem");
        RSAKey rsaKey = (RSAKey) JWK.parseFromPEMEncodedObjects(pemEncodedRSAPrivateKey);
        return new RSASSAVerifier(rsaKey);
    }

    protected String generateAccessToken(String clientId, String subject, String approvedScope) throws Exception{
        Calendar calendar = Calendar.getInstance();
        Date signTimeTime = calendar.getTime();
        calendar.add(Calendar.MILLISECOND, expiresInMilliseconds);
        Date expireTime = calendar.getTime();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("http://localhost:18080")
                .subject(subject)
                .audience(Arrays.asList("https://app-one.com", "https://app-two.com"))
                .expirationTime(expireTime)
                .notBeforeTime(signTimeTime)
                .issueTime(signTimeTime)
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", approvedScope)
                .claim("client_id", clientId)
                .build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
        signedJWT.sign(generateRsaJwsSigner());
        String result = signedJWT.serialize();
        log.info("access_token is :\n" + result);
        return result;
    }

    protected String generateRefreshToken(String clientId, String subject, String approvedScope) throws Exception{
        JWSSigner jwsSigner = generateRsaJwsSigner();
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, 1);
        Date expireTime = now.getTime();
        //6.Build refresh token
        JWTClaimsSet refreshTokenClaims = new JWTClaimsSet.Builder()
                .subject(subject)
                .claim("client_id", clientId)
                .claim("scope", approvedScope)
                //refresh token for 1 day.
                .expirationTime(expireTime)
                .build();
        SignedJWT signedRefreshToken = new SignedJWT(jwsHeader, refreshTokenClaims);
        signedRefreshToken.sign(jwsSigner);
        String result = signedRefreshToken.serialize();
        log.info("refresh_token is :\n" + result);
        return result;
    }
}
