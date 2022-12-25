package org.demo.resteasy.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Person {

    @Size(min = 3, max = 5, message = "{userName.invalid}")
    private String userName;
    
    @Max(value = 10, message = "{userAge.invalid}")
    private Integer userAge;
}
