package com.demo.resteasy.handler;

import com.demo.resteasy.vo.TokenVO;

import javax.inject.Named;
import javax.ws.rs.core.MultivaluedMap;

@Named("refresh_token")
public class RefreshTokenGrantTypeHandler extends AbstractGrantTypeHandler{

    @Override
    public TokenVO createAccessToken(String clientId, MultivaluedMap<String, String> params) throws Exception {
        return null;
    }
}
