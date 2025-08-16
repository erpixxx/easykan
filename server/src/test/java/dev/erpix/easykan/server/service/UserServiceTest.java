package dev.erpix.easykan.server.service;

import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.UserNotFoundException;
import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void create_shouldEncodePasswordAndSaveChanges() {
        String hashedPassword = "hashedPassword";
        var dto = new UserCreateRequestDto("test", "Test User", "test@test.com", "password123");

        when(passwordEncoder.encode("password123"))
                .thenReturn(hashedPassword);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.create(dto);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getPasswordHash()).isEqualTo(hashedPassword);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void create_shouldNotEncodePassword_whenPasswordIsNull() {
        var dto = new UserCreateRequestDto("oauthUser", "OAuth User", "oauth@test.com", null);
        User user = dto.toUser();

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User createdUser = userService.create(dto);

        assertThat(createdUser).isNotNull();
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void delete_shouldDeleteUser_whenUserExists() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.getById(userId);

        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void getById_shouldThrowException_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
    }

}
