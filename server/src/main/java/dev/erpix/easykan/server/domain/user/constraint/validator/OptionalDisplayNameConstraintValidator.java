package dev.erpix.easykan.server.domain.user.constraint.validator;

import dev.erpix.easykan.server.validator.OptionalConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;

public class OptionalDisplayNameConstraintValidator extends DisplayNameConstraintValidator<Optional<String>>
		implements OptionalConstraintValidator<String> {

	@Override
	public boolean isValid(Optional<String> value, ConstraintValidatorContext context) {
		return validateIfPresent(value, v -> isStringContentValid(v, context));
	}

}
