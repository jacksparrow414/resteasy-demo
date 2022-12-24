package com.demo.resteasy.handler;

import com.demo.resteasy.vo.TokenVO;

import jakarta.ws.rs.core.MultivaluedMap;

public interface AuthorizationGrantTypeHandler {
    /**
     * 创建access_token
     * @param clientId
     * @param params
     * @return
     * @throws Exception
     */
    TokenVO createAccessToken(String clientId, MultivaluedMap<String, String> params) throws Exception;
}
