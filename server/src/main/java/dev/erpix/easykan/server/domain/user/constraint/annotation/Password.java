package dev.erpix.easykan.server.domain.user.constraint.annotation;

import dev.erpix.easykan.server.domain.user.constraint.validator.OptionalPasswordConstraintValidator;
import dev.erpix.easykan.server.domain.user.constraint.validator.RequiredPasswordConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Constraint(validatedBy = { OptionalPasswordConstraintValidator.class, RequiredPasswordConstraintValidator.class })
public @interface Password {

	String message() default "Invalid password format";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
