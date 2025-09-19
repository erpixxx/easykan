package dev.erpix.easykan.server.domain.user.constraint.validator;

import dev.erpix.easykan.server.domain.user.constraint.annotation.OptionalEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptionalEmailConstraintValidator
        implements ConstraintValidator<OptionalEmail, Optional<String>> {

    private final EmailValidator emailValidator = new EmailValidator();

    @Override
    public boolean isValid(Optional<String> value, ConstraintValidatorContext context) {
        return value.map(s -> emailValidator.isValid(s, context)).orElse(true);
    }
}
