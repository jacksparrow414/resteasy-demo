package org.demo.resteasy.endpoints.fixture;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * @author jacksparrow414
 * @date 2022/12/25
 */
public class Car {
    
    private String name;
    
    private Integer age;
    
    @NotNull
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Max(8)
    public Integer getAge() {
        return age;
    }
    
    public void setAge(final Integer age) {
        this.age = age;
    }
    
    public void testParametersValidator(@NotBlank String message) {
    
    }
    
    @NotEmpty
    public List<String> testReturnValues() {
        return Collections.emptyList();
    }
}
