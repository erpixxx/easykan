package dev.erpix.easykan.server.service;

import dev.erpix.easykan.server.domain.user.dto.UserInfoUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.domain.user.validator.UserValidator;
import dev.erpix.easykan.server.exception.UserNotFoundException;
import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
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

    @Mock
    private UserValidator userValidator;

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

        when(userRepository.existsById(userId))
                .thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void getAllUsers_shouldReturnPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        User user1 = User.builder().build();
        List<User> userList = List.of(user1);
        Page<User> expectedPage = new PageImpl<>(userList, pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<User> resultPage = userService.getAllUsers(pageable);

        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements())
                .isEqualTo(1);
        assertThat(resultPage.getContent())
                .isEqualTo(userList);
        verify(userRepository).findAll(pageable);
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        User foundUser = userService.getById(userId);

        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void getById_shouldThrowException_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void getByLogin_shouldReturnUser_whenUserExists() {
        String login = "testuser";
        User user = User.builder()
                .login(login)
                .build();

        when(userRepository.findByLogin(login))
                .thenReturn(Optional.of(user));

        User foundUser = userService.getByLogin(login);

        assertThat(foundUser).isEqualTo(user);
        verify(userRepository).findByLogin(login);
    }

    @Test
    void getByLogin_shouldThrowException_whenUserDoesNotExist() {
        String login = "nonexistent";

        when(userRepository.findByLogin(login))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getByLogin(login));
        verify(userRepository).findByLogin(login);
    }

    @Test
    void updateCurrentUserInfo_shouldUpdateUserInfoAndReturnUpdatedUser() {
        UUID userId = UUID.randomUUID();
        String userLogin = "testuser";
        String userDisplayName = "Test User";
        String userEmail = "test.user@easykan.dev";
        User user = User.builder()
                .id(userId)
                .login(userLogin)
                .displayName(userDisplayName)
                .email(userEmail)
                .build();

        String updatedLogin = "updateduser";
        String updatedDisplayName = "Updated User";
        String updatedEmail = "updated.user@easykan.dev";
        User updatedUser = User.builder()
                .id(userId)
                .login(updatedLogin)
                .displayName(updatedDisplayName)
                .email(updatedEmail)
                .build();

        var requestDto = new UserInfoUpdateRequestDto(
                Optional.of(updatedLogin),
                Optional.of(updatedDisplayName),
                Optional.of(updatedEmail));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        User result = userService.updateCurrentUserInfo(userId, requestDto);

        assertThat(result).isEqualTo(updatedUser);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
        verify(userValidator).validateLogin(updatedLogin, userId);
        verify(userValidator).validateDisplayName(updatedDisplayName);
        verify(userValidator).validateEmail(updatedEmail, userId);
    }

    @Test
    void updateCurrentUserInfo_shouldThrowException_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        var requestDto = new UserInfoUpdateRequestDto(
                Optional.of("updateduser"),
                Optional.of("Updated User"),
                Optional.of("updated.user@easykan.dev"));

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.updateCurrentUserInfo(userId, requestDto));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(userValidator, never()).validateLogin(anyString(), any(UUID.class));
        verify(userValidator, never()).validateDisplayName(anyString());
        verify(userValidator, never()).validateEmail(anyString(), any(UUID.class));
    }

}
