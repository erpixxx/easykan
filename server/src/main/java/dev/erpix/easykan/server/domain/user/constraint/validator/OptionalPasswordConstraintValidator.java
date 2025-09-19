package dev.erpix.easykan.server.domain.user.constraint.validator;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.validator.OptionalConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class OptionalPasswordConstraintValidator
        extends PasswordConstraintValidator<Optional<String>>
        implements OptionalConstraintValidator<String> {

    public OptionalPasswordConstraintValidator(EasyKanConfig config) {
        super(config);
    }

    @Override
    public boolean isValid(Optional<String> value, ConstraintValidatorContext context) {
        return validateIfPresent(value, v -> isStringContentValid(v, context));
    }
}
