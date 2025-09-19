package dev.erpix.easykan.server.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public interface StringConstraintValidator<A extends Annotation, T>
        extends ConstraintValidator<A, T> {

    boolean isStringContentValid(String value, ConstraintValidatorContext context);
}
