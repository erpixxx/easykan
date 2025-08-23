package dev.erpix.easykan.server.domain.user.constraint.validator;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.user.constraint.annotation.Password;
import dev.erpix.easykan.server.validator.StringConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PasswordConstraintValidator<T> implements StringConstraintValidator<Password, T> {

    private final EasyKanConfig config;

    @Override
    public boolean isStringContentValid(String value, ConstraintValidatorContext context) {
        if (value.isBlank()) {
            context.buildConstraintViolationWithTemplate("Password cannot be blank")
                    .addConstraintViolation();
            return false;
        }

        EasyKanConfig.PasswordProperties props = config.password();
        boolean isValid = true;

        if (value.length() < props.minLength()) {
            context.buildConstraintViolationWithTemplate("Password must be at least " + props.minLength() + " characters long")
                    .addConstraintViolation();
            isValid = false;
        }

        if (props.requireLowercase() && value.chars().noneMatch(Character::isLowerCase)) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one lowercase letter")
                    .addConstraintViolation();
            isValid = false;
        }

        if (props.requireUppercase() && value.chars().noneMatch(Character::isUpperCase)) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one uppercase letter")
                    .addConstraintViolation();
            isValid = false;
        }

        if (props.requireDigit() && value.chars().noneMatch(Character::isDigit)) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one digit")
                    .addConstraintViolation();
            isValid = false;
        }

        if (props.requireSpecialCharacter() && value.chars().allMatch(Character::isLetterOrDigit)) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one special character")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }

}
