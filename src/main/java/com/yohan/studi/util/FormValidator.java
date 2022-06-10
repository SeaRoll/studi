package com.yohan.studi.util;

import com.yohan.studi.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Component
@Slf4j
public class FormValidator {

    /**
     * Validates an object and throws error if there are validations
     * that are not met.
     *
     * @param object an object to be validated
     */
    public void validateForm(Object object) {
        log.info("Validating form");
        Validator validator = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true)
                .buildValidatorFactory()
                .getValidator();

        Set<ConstraintViolation<Object>> result = validator.validate(object);

        log.info("Form validation result: {}", result.toString());

        if (!result.isEmpty()) {
            ConstraintViolation<Object> firstResult = (ConstraintViolation<Object>) result.toArray()[0];
            throw new BadRequestException(firstResult.getMessageTemplate());
        }
    }
}
