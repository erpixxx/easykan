package dev.erpix.easykan.server.validator;

import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import dev.erpix.easykan.server.domain.user.validator.UserValidator;
import dev.erpix.easykan.server.exception.ResourceAlreadyExistsException;
import dev.erpix.easykan.server.exception.ValidationException;
import dev.erpix.easykan.server.testsupport.Category;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Tag(Category.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailValidator emailValidator;

    private final UUID userId = UUID.randomUUID();

    @Test
    void validateLogin_shouldDoNothing_whenLoginIsValid() {
        String validLogin = "validLogin";

        when(userRepository.existsByLoginAndIdNot(validLogin, userId))
                .thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateLogin(validLogin, userId));
    }

    @Test
    void validateLogin_shouldThrowException_whenLoginIsBlank() {
        String blankLogin = " ";

        assertThrows(ValidationException.class, () ->
                userValidator.validateLogin(blankLogin, userId));
    }

    @Test
    void validateLogin_shouldThrowException_whenLoginAlreadyExists() {
        String existingLogin = "admin";

        when(userRepository.existsByLoginAndIdNot(existingLogin, userId))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () ->
                userValidator.validateLogin(existingLogin, userId));
    }

    @Test
    void validateEmail_shouldNotThrowException_whenEmailIsValidAndUnique() {
        String validEmail = "test@example.com";

        when(emailValidator.isValid(validEmail, null))
                .thenReturn(true);
        when(userRepository.existsByEmailAndIdNot(validEmail, userId))
                .thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateEmail(validEmail, userId));
    }

    @Test
    void validateEmail_shouldThrowException_whenEmailFormatIsInvalid() {
        String invalidEmail = "not-an-email";

        when(emailValidator.isValid(invalidEmail, null))
                .thenReturn(false);

        assertThrows(ValidationException.class, () ->
                userValidator.validateEmail(invalidEmail, userId));
    }

}
