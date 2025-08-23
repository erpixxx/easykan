package dev.erpix.easykan.server.validator;

import jakarta.validation.ConstraintValidatorContext;

import java.util.function.Supplier;

public interface RequiredStringConstraintValidator {

    default boolean requireNonBlank(String value, ConstraintValidatorContext context,
                                    String errorMessage, Supplier<Boolean> continuation) {
         if (value == null || value.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
            return false;
        }
        return continuation.get();
    }

}
