package dev.erpix.easykan.server.domain.user.constraint.validator;

import dev.erpix.easykan.server.validator.RequiredStringConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class RequiredLoginConstraintValidator extends LoginConstraintValidator<String>
		implements RequiredStringConstraintValidator {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();

		return requireNonBlank(value, context, "Login is required", () -> isStringContentValid(value, context));
	}

}
