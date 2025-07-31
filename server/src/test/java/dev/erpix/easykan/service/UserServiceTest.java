package dev.erpix.easykan.service;

import dev.erpix.easykan.exception.UserNotFoundException;
import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.model.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.repository.UserRepository;
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
        var dto = new UserCreateRequestDto("test", "Test User", "test@test.com", "password123", false);

        when(passwordEncoder.encode("password123"))
                .thenReturn(hashedPassword);
        when(userRepository.save(any(EKUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EKUser createdUser = userService.create(dto);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getPasswordHash()).isEqualTo(hashedPassword);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(EKUser.class));
    }

    @Test
    void create_shouldNotEncodePassword_whenPasswordIsNull() {
        var dto = new UserCreateRequestDto("oauthUser", "OAuth User", "oauth@test.com", null, false);
        EKUser user = dto.toUser();

        when(userRepository.save(any(EKUser.class)))
                .thenReturn(user);

        EKUser createdUser = userService.create(dto);

        assertThat(createdUser).isNotNull();
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(any(EKUser.class));
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
        EKUser user = EKUser.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        EKUser foundUser = userService.getById(userId);

        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void getById_shouldThrowException_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
    }

}
