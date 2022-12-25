package org.demo.resteasy.endpoints;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import lombok.extern.java.Log;
import org.demo.resteasy.endpoints.fixture.Car;
import org.demo.resteasy.vo.Person;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author jacksparrow414
 * @date 2022/12/25
 */
@Log
class ValidationResourceTest {
    
    /**
     * validator负责具体JavaBean和其属性的校验
     */
    private Validator validator;
    
    /**
     * executableValidator负责普通方法和构造方法的入参和返回值的校验
     */
    private ExecutableValidator executableValidator;
    
    @BeforeEach
    public void setUp() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory();
        validator = validatorFactory.getValidator();
        executableValidator = validatorFactory.getValidator().forExecutables();
    }
    
    @Test
    public void validateObject() {
        Person person = new Person();
        person.setUserName("dd");
        person.setUserAge(2);
        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);
        Assertions.assertEquals(1, constraintViolations.size());
        log.info(constraintViolations.iterator().next().getMessage());
    }
    
    /**
     * 注解还可以放到get方法上
     */
    @Test
    public void validateProperty() {
        Car car = new Car();
        car.setName(null);
        car.setAge(10);
        Set<ConstraintViolation<Car>> constraintViolations = validator.validateProperty(car, "age");
        Assertions.assertEquals(1, constraintViolations.size());
        constraintViolations.forEach(constraintViolation -> {
            log.info(constraintViolation.getMessage());
        });
    }
    
    /**
     * 校验方法入参
     * @throws Exception
     */
    @Test
    public void validateMethodParameters() throws Exception {
        Car car = new Car();
        Method method = Car.class.getMethod("testParametersValidator", String.class);
        Object[] parameterValues = {""};
        Set<ConstraintViolation<Car>> constraintViolationSet = executableValidator.validateParameters(car, method, parameterValues);
        Assertions.assertEquals(1, constraintViolationSet.size());
    }
    
    /**
     * 校验方法返回值
     * @throws Exception
     */
    @Test
    public void validateMethodReturn() throws Exception {
        Car car = new Car();
        Method method = Car.class.getMethod("testReturnValues");
        Set<ConstraintViolation<Car>> constraintViolations = executableValidator.validateReturnValue(car, method, Collections.emptyList());
        Assertions.assertEquals(1, constraintViolations.size());
        log.info(constraintViolations.iterator().next().getMessage());
    }
    
}