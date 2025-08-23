package dev.erpix.easykan.server.domain.user.validator;

import dev.erpix.easykan.server.constant.ValidationConstants;
import dev.erpix.easykan.server.validator.StringConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public abstract class LoginConstraintValidator<T> implements StringConstraintValidator<Login, T> {

    @Override
    public boolean isStringContentValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (value.isBlank()) {
            context.buildConstraintViolationWithTemplate("Login cannot be blank")
                    .addConstraintViolation();
            return false;
        }

        if (value.length() < ValidationConstants.LOGIN_MIN_LENGTH
                || value.length() > ValidationConstants.LOGIN_MAX_LENGTH) {
            context.buildConstraintViolationWithTemplate("Login must be between"
                            + ValidationConstants.LOGIN_MIN_LENGTH + " and "
                            + ValidationConstants.LOGIN_MAX_LENGTH + " characters long")
                    .addConstraintViolation();
            return false;
        }

        if (!value.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_')) {
            context.buildConstraintViolationWithTemplate("Login can only contain lowercase letters, digits and underscores")
                    .addConstraintViolation();
            return false;
        }

        if (value.chars().anyMatch(Character::isUpperCase)) {
            context.buildConstraintViolationWithTemplate("Login must not contain uppercase letters")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
