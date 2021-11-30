package com.demo.resteasy.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenErrorVO {

    private String error;

    private String errorDescription;
}
