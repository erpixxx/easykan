package dev.erpix.easykan.server.domain.user.constraint.annotation;

import dev.erpix.easykan.server.domain.user.constraint.validator.OptionalLoginConstraintValidator;
import dev.erpix.easykan.server.domain.user.constraint.validator.RequiredLoginConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Constraint(validatedBy = { OptionalLoginConstraintValidator.class, RequiredLoginConstraintValidator.class })
public @interface Login {

	String message() default "Invalid login format";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
