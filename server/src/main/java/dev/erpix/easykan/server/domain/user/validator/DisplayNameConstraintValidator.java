package dev.erpix.easykan.server.domain.user.validator;

import dev.erpix.easykan.server.constant.ValidationConstants;
import dev.erpix.easykan.server.validator.StringConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public abstract class DisplayNameConstraintValidator<T> implements StringConstraintValidator<DisplayName, T> {

    @Override
    public boolean isStringContentValid(String value, ConstraintValidatorContext context) {
        if (value.isBlank()) {
            context.buildConstraintViolationWithTemplate("Display name cannot be blank")
                    .addConstraintViolation();
            return false;
        }

        if (value.length() < ValidationConstants.DISPLAY_NAME_MIN_LENGTH
                || value.length() > ValidationConstants.DISPLAY_NAME_MAX_LENGTH) {
            context.buildConstraintViolationWithTemplate("Display name must be between "
                            + ValidationConstants.DISPLAY_NAME_MIN_LENGTH + " and "
                            + ValidationConstants.DISPLAY_NAME_MAX_LENGTH + " characters long")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
