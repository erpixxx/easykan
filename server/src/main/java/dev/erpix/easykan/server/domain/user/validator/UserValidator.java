package dev.erpix.easykan.server.domain.user.validator;

import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import dev.erpix.easykan.server.exception.resource.ResourceAlreadyExistsException;
import dev.erpix.easykan.server.exception.common.ValidationException;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserValidator {

    public static final int LOGIN_MAX_LENGTH = 64;
    public static final int LOGIN_MIN_LENGTH = 1;
    public static final int DISPLAY_NAME_MAX_LENGTH = 64;
    public static final int DISPLAY_NAME_MIN_LENGTH = 1;
    public static final int PASSWORD_MIN_LENGTH = 8;

    private final UserRepository userRepository;
    private final EmailValidator emailValidator;

    public void validateLogin(String login, UUID userId) {
        if (login.isBlank() || login.length() > LOGIN_MAX_LENGTH) {
            throw new ValidationException("Login must be between " + LOGIN_MIN_LENGTH +
                    " and " + LOGIN_MAX_LENGTH + " characters");
        }
        if (userRepository.existsByLoginAndIdNot(login, userId)) {
            throw new ResourceAlreadyExistsException("Login already exists: " + login);
        }
    }

    public void validateDisplayName(String displayName) {
        if (displayName.isBlank() || displayName.length() > DISPLAY_NAME_MAX_LENGTH) {
            throw new ValidationException("Display name must be between " + DISPLAY_NAME_MIN_LENGTH +
                    " and " + DISPLAY_NAME_MAX_LENGTH + " characters");
        }
    }

    public void validateEmail(String email, UUID userId) {
        if (!emailValidator.isValid(email, null)) {
            throw new ValidationException("Email must be a valid email address");
        }
        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new ResourceAlreadyExistsException("Email already exists: " + email);
        }
    }

    public void validatePassword(String password) {
        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new ValidationException("Password must be at least " + PASSWORD_MIN_LENGTH + " characters long");
        }
    }

}
