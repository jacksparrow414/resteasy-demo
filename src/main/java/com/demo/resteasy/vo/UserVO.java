package com.demo.resteasy.vo;

;
import javax.ws.rs.FormParam;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserVO {

    @NotBlank(message = "pleas enter userName")
    @FormParam("username")
    private String userName;

    @NotNull(message = "please enter user's age")
    @Min(value = 0, message = "min value is 0")
    @FormParam("age")
    private Integer age;
}
