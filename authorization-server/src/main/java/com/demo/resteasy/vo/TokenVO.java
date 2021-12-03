package com.demo.resteasy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TokenVO {

    private String token_type = "Bearer";

    private String access_token;

    private Integer expires_in;

    private String scope;

    private String refresh_token;
}
