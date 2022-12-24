package org.demo.resteasy.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Person {

    @NotBlank
    private String userName;
    
    @Max(value = 10, message = "${validatedValue} max value is {value}")
    private Integer userAge;
}
