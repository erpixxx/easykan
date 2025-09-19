package dev.erpix.easykan.server.domain.user.constraint.validator;

import dev.erpix.easykan.server.domain.user.constraint.annotation.UserPermissionMask;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.exception.user.PermissionMaskValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UserPermissionConstraintValidator
        implements ConstraintValidator<UserPermissionMask, Long> {

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            UserPermission.validate(value);
            return true;
        } catch (PermissionMaskValidationException e) {
            context.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation();
            return false;
        }
    }
}
