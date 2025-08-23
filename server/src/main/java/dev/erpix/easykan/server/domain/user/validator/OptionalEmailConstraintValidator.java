package dev.erpix.easykan.server.domain.user.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OptionalEmailConstraintValidator implements ConstraintValidator<OptionalEmail, Optional<String>> {

    private final EmailValidator emailValidator = new EmailValidator();

    @Override
    public boolean isValid(Optional<String> value, ConstraintValidatorContext context) {
        return value.map(s -> emailValidator.isValid(s, context))
                .orElse(true);
    }

}
