package dev.erpix.easykan.server.domain.user.constraint.validator;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.validator.RequiredStringConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class RequiredPasswordConstraintValidator extends PasswordConstraintValidator<String>
        implements RequiredStringConstraintValidator {

    public RequiredPasswordConstraintValidator(EasyKanConfig config) {
        super(config);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        return requireNonBlank(value, context, "Password is required",
                () -> isStringContentValid(value, context));
    }

}
