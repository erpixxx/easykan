package dev.erpix.easykan.server.domain.user.constraint.annotation;

import dev.erpix.easykan.server.domain.user.constraint.validator.OptionalDisplayNameConstraintValidator;
import dev.erpix.easykan.server.domain.user.constraint.validator.RequiredDisplayNameConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Constraint(validatedBy = {OptionalDisplayNameConstraintValidator.class, RequiredDisplayNameConstraintValidator.class})
public @interface DisplayName {

    String message() default "Invalid display name format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
