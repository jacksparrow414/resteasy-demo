package com.demo.resteasy.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionData {

    /**
     * jackson对具体属性实现get方法.
     * 否则会报 disable SerializationFeature.FAIL_ON_EMPTY_BEANS
     *
     * 用不到set方法不写.
     */
    private String errorMessage;

}
